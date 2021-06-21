package ru.khaksbyt.service;

import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import ru.gosuslugi.dom.schema.integration.base.AckRequest;
import ru.gosuslugi.dom.schema.integration.base.GetStateRequest;
import ru.gosuslugi.dom.schema.integration.house_management.ExportHouseRequest;
import ru.gosuslugi.dom.schema.integration.house_management.GetStateResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Component
public class HouseManagementClient {
    private static final String DEFAULT_URL = "https://api.dom.gosuslugi.ru/ext-bus-home-management-service/services/HomeManagementAsync";

    private final WebServiceTemplate webServiceTemplate;

    public HouseManagementClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public String getState(String uid) {
        ru.gosuslugi.dom.schema.integration.base.ObjectFactory factory = new ru.gosuslugi.dom.schema.integration.base.ObjectFactory();

        GetStateRequest request = factory.createGetStateRequest();
        request.setMessageGUID(uid);

        GetStateResult result = (GetStateResult) webServiceTemplate.marshalSendAndReceive(
                DEFAULT_URL,
                request,
                new SoapActionCallback("urn:getState"));

        String res = "";
        if (result != null) {
            if (result.getErrorMessage() != null)
                res = result.getErrorMessage().getDescription();
            if (result.getImportResult() != null && result.getImportResult().size() > 0) {
                GetStateResult.ImportResult.CommonResult commonResult = result.getImportResult().get(0).getCommonResult().get(0);

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                String dateStr = df.format(commonResult.getUpdateDate().toGregorianCalendar().getTime());

                res += "UniqueNumber: " + commonResult.getUniqueNumber();
                res += " UpdateDate: " + dateStr;
                res += " GUID: " + commonResult.getGUID();
            }
        } else
            res = "not result";

        return res;
    }

    public String exportHouseRequest(String guid) {
        ru.gosuslugi.dom.schema.integration.house_management.ObjectFactory factory = new ru.gosuslugi.dom.schema.integration.house_management.ObjectFactory();

        ExportHouseRequest request = factory.createExportHouseRequest();
        request.setVersion("11.1.0.1");
        request.setId("signed-data-container");

        request.setFIASHouseGuid(guid);
        AckRequest result = (AckRequest) webServiceTemplate.marshalSendAndReceive(
                DEFAULT_URL,
                request,
                new SoapActionCallback("urn:exportHouseData"));


        String res = "";
        if (result != null) {
            if (result.getAck() != null)
                res = result.getAck().getMessageGUID();
        } else
            res = "not result";

        return res;
    }
}
