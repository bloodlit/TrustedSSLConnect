package ru.khaksbyt.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.gosuslugi.dom.schema.integration.base.HeaderType;
import ru.khaksbyt.model.memory.History;
import ru.khaksbyt.repository.memory.HistoryRepository;
import ru.khaksbyt.util.UtilsDate;
import xades4j.XAdES4jException;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.algorithms.ExclusiveCanonicalXMLWithoutComments;
import xades4j.production.DataObjectReference;
import xades4j.production.SignatureAppendingStrategies;
import xades4j.production.SignedDataObjects;
import xades4j.production.XadesSigner;
import xades4j.properties.DataObjectDesc;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.xpath.*;
import java.util.Iterator;

@Slf4j
@Component
public class SignInterceptor implements ClientInterceptor {

    private final XadesSigner signer;
    private final HistoryRepository historyRepository;

    public SignInterceptor(XadesSigner signer, HistoryRepository repository) {
        this.signer = signer;
        this.historyRepository = repository;
    }

    public void signDocument(Document doc) throws XPathExpressionException, XAdES4jException {
        String signingId = "signed-data-container";

        final XPathFactory factory = XPathFactory.newInstance();
        final XPath xpath = factory.newXPath();

        // Подписываемый узел (предположительно, FinalPayment с неким Id).
        final XPathExpression expr = xpath.compile(String.format("//*[@Id='%s']", signingId));
        final NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        //Signature если в щаблоне есть упоминания то необходимо добавить подпись
        if (nodes.getLength() != 0) {
            Node nodeToSign = nodes.item(0);
            String referenceURI = "#" + signingId;

            final DataObjectDesc dataObj = new DataObjectReference(referenceURI);
            dataObj.withTransform(new EnvelopedSignatureTransform());
            dataObj.withTransform(new ExclusiveCanonicalXMLWithoutComments());

            final SignedDataObjects dataObjects = new SignedDataObjects(dataObj);

            //Обекзательно ds:Signature первым элементом, для этого устанавливаем SignatureAppendingStrategies.AsFirstChild
            this.signer.sign(dataObjects, nodeToSign, SignatureAppendingStrategies.AsFirstChild);
            log.debug("XAdES-T signature completed for document.");
        } else
            log.debug("The document does not need to be signed.");
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        log.debug("Server Request Message: " + messageContext.getRequest().toString());
        WebServiceMessage message = messageContext.getRequest();
        if (message instanceof SoapMessage) {
            try {
                SoapMessage soapMessage = (SoapMessage) message;
                Document envelopeAsDocument = soapMessage.getDocument();

                //Пытаемся подписать документ перед отправкой, для подписания в запросе
                //обезательно должен быть установлен Id "signed-data-container"
                signDocument(envelopeAsDocument);

                soapMessage.setDocument(envelopeAsDocument);
            } catch (XPathExpressionException | XAdES4jException e) {
                e.printStackTrace();
            }

            SoapHeader header = ((SoapMessage) message).getSoapHeader();
            Iterator<SoapHeaderElement> it = header.examineAllHeaderElements();
            while (it.hasNext()) {
                try {
                    JAXBContext context = JAXBContext.newInstance(HeaderType.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    HeaderType headerType = (HeaderType) unmarshaller.unmarshal(it.next().getSource());

                    historyRepository.save(
                            new History(
                                    headerType.getMessageGUID(),
                                    UtilsDate.convert(headerType.getDate()),
                                    ((SoapMessage) message).getSoapAction())
                    );

                } catch (JAXBException e) {
                    e.printStackTrace();
                }

            }
        }

        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        log.debug("Server Response Message: " + messageContext.getResponse().toString());
        WebServiceMessage message = messageContext.getResponse();

        if (message instanceof SoapMessage) {
            SoapMessage soapMessage = (SoapMessage) message;

            log.debug(soapMessage.toString());
        }

        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        log.debug("Message Context on soap Fault  " + messageContext);

        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception e) throws WebServiceClientException {

    }
}
