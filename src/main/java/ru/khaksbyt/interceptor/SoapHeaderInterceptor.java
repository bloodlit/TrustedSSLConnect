package ru.khaksbyt.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import ru.gosuslugi.dom.schema.integration.base.ObjectFactory;
import ru.gosuslugi.dom.schema.integration.base.RequestHeader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.UUID;

@Slf4j
public class SoapHeaderInterceptor implements ClientInterceptor {

    private final String orgGUID;

    public SoapHeaderInterceptor(String orgGUID) {
        this.orgGUID = orgGUID;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        log.debug("Server Request Message: " + messageContext.getRequest().toString());

        WebServiceMessage message = messageContext.getRequest();
        if (message instanceof SoapMessage) {
            SoapHeader soapHeader = ((SoapMessage) message).getSoapHeader();
            XMLGregorianCalendar xgcal = null;

            try {
                GregorianCalendar gcal = new GregorianCalendar();
                xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }

            ObjectFactory factory = new ObjectFactory();
            RequestHeader requestHeader = factory.createRequestHeader();
            requestHeader.setDate(xgcal);
            requestHeader.setMessageGUID(UUID.randomUUID().toString());
            requestHeader.setOrgPPAGUID(orgGUID);

            try {
                // Создаем маршелинг
                JAXBContext context = JAXBContext.newInstance(RequestHeader.class);
                Marshaller marshaller = context.createMarshaller();

                // преобразуем заголовок
                marshaller.marshal(requestHeader, soapHeader.getResult());
            } catch (JAXBException e) {
                log.error("JAXBException error during marshalling of the SOAP headers", e);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception e) throws WebServiceClientException {

    }
}
