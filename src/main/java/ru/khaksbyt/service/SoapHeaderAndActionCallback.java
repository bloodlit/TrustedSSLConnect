package ru.khaksbyt.service;

import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import ru.gosuslugi.dom.schema.integration.base.ISRequestHeader;
import ru.gosuslugi.dom.schema.integration.base.RequestHeader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.UUID;

public class SoapHeaderAndActionCallback implements WebServiceMessageCallback {
    private final String action;
    private final String org;

    public SoapHeaderAndActionCallback(String action, String org) {
        this.org = org;
        this.action = action;
    }

    public SoapHeaderAndActionCallback(String action) {
        this.org = "";
        this.action = action;
    }

    @Override
    public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
        Assert.isInstanceOf(SoapMessage.class, webServiceMessage);
        SoapMessage soapMessage = (SoapMessage) webServiceMessage;
        SoapHeader soapHeader = soapMessage.getSoapHeader();

        soapMessage.setSoapAction(this.action);

        try {
            Object request;
            JAXBContext context;
            String guid = UUID.randomUUID().toString();
            GregorianCalendar gcal = new GregorianCalendar();
            XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

            if (!"".equals(org)) {
                request = new RequestHeader();
                ((RequestHeader) request).setDate(xgcal);
                ((RequestHeader) request).setMessageGUID(guid);
                ((RequestHeader) request).setOrgPPAGUID(org);

                context = JAXBContext.newInstance(RequestHeader.class);
            } else {
                request = new ISRequestHeader();
                ((ISRequestHeader) request).setMessageGUID(guid);
                ((ISRequestHeader) request).setDate(xgcal);

                context = JAXBContext.newInstance(ISRequestHeader.class);
            }

            // преобразуем заголовок
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(request, soapHeader.getResult());
        } catch (JAXBException | DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }
}
