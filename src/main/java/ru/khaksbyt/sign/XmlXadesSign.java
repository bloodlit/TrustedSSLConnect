package ru.khaksbyt.sign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.CryptoPro.JCPxml.XmlInit;
import ru.khaksbyt.xades.CustomizableAlgorithmProvider;
import ru.khaksbyt.xades.CustomizableDigestEngineProvider;
import ru.khaksbyt.xades.production.CustomizableXadesBesSigningProfileFactory;
import xades.util.XMLUtility;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.algorithms.ExclusiveCanonicalXMLWithoutComments;
import xades4j.production.*;
import xades4j.properties.DataObjectDesc;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.impl.DirectKeyingDataProvider;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class XmlXadesSign {

    final Logger logger = LoggerFactory.getLogger(XmlXadesSign.class);
    final XadesSigner signer;

    static {
        if (!XmlInit.isInitialized()) {
            XmlInit.init();
        }
    }

    public XmlXadesSign(X509Certificate certificate, PrivateKey privateKey) throws Exception {
        logger.info("Initialize XmlXadesSign!");

        // создаем провайдер для доступа к закрытому ключу
        KeyingDataProvider kp = new DirectKeyingDataProvider(certificate, privateKey);

        // создаем провайдер, описывающий используемые алгоритмы
        CustomizableAlgorithmProvider algorithmsProvider = new CustomizableAlgorithmProvider();
        // создаем провайдер, ответственный за расчет хешей
        CustomizableDigestEngineProvider digestEngineProvider = new CustomizableDigestEngineProvider();

        // настраиваем профиль подписания
        final XadesSigningProfile sigProf = new CustomizableXadesBesSigningProfileFactory()
                .withKeyingProvider(kp)
                .withAlgorithmsProvider(algorithmsProvider)
                .withMessageDigestEngineProvider(digestEngineProvider)
                .create();

        signer = sigProf.newSigner();
    }

    /**
     * @param sourceXmlBin Исходный подписываемый документ.
     * @param signingId    Подписываемый узел.
     * @return документ с подписью.
     * @throws Exception
     */
    public void sign(byte[] sourceXmlBin, String signingId, OutputStream outputStream) throws Exception {
        if (signer == null)
            throw new NullPointerException();

        // загрузка содержимого подписываемого документа на основе установленных флагами правил
        final Document doc = XMLUtility.parseFile(sourceXmlBin);

        final XPathFactory factory = XPathFactory.newInstance();
        final XPath xpath = factory.newXPath();

        // Подписываемый узел (предположительно, FinalPayment с неким Id).
        final XPathExpression expr = xpath.compile(String.format("//*[@Id='%s']", signingId));
        final NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        if (nodes.getLength() == 0) {
            throw new Exception("Can't find node with id: " + signingId);
        }
        final Node nodeToSign = nodes.item(0);
        final String referenceURI = "#" + signingId;

        final DataObjectDesc dataObj = new DataObjectReference(referenceURI);
        dataObj.withTransform(new EnvelopedSignatureTransform());
        dataObj.withTransform(new ExclusiveCanonicalXMLWithoutComments());

        final SignedDataObjects dataObjects = new SignedDataObjects(dataObj);

        //Обекзательно ds:Signature первым элементом, для этого устанавливаем SignatureAppendingStrategies.AsFirstChild
        signer.sign(dataObjects, nodeToSign, SignatureAppendingStrategies.AsFirstChild);
        System.out.println("XAdES-T signature completed.");

        XMLUtility.saveXml2Stream(doc, outputStream);
    }
}
