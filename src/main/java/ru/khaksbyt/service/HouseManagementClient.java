package ru.khaksbyt.service;

import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import ru.gosuslugi.dom.schema.integration.base.ObjectFactory;
import ru.gosuslugi.dom.schema.integration.base.RequestHeader;
import ru.gosuslugi.dom.schema.integration.house_management.GetStateResult;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

@Component
public class HouseManagementClient {
    private static final String DEFAULT_URL = "https://api.dom.gosuslugi.ru/ext-bus-home-management-service/services/HomeManagementAsync";

    private final WebServiceTemplate webServiceTemplate;

    public HouseManagementClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public String getState(String uid) {
        ObjectFactory factory = new ObjectFactory();
        XMLGregorianCalendar xgcal = null;
        try {
            GregorianCalendar gcal = new GregorianCalendar();
            xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        RequestHeader request = factory.createRequestHeader();
        request.setDate(xgcal);
        request.setOrgPPAGUID("3ac589cd-3ff4-476a-81a7-c158944868bd");
        request.setMessageGUID(uid);

        GetStateResult result = (GetStateResult) webServiceTemplate.marshalSendAndReceive(
                DEFAULT_URL,
                request,
                new SoapActionCallback("urn:getState"));

        return result != null ? result.toString() : "not result";
    }
}
