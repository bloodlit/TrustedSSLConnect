package ru.khaksbyt.service;

import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import ru.gosuslugi.dom.schema.integration.base.AckRequest;
import ru.gosuslugi.dom.schema.integration.base.GetStateRequest;
import ru.gosuslugi.dom.schema.integration.nsi_common.ExportNsiListRequest;
import ru.gosuslugi.dom.schema.integration.nsi_common.GetStateResult;
import ru.khaksbyt.model.primary.NsiItemInfoDTO;
import ru.khaksbyt.repository.primary.NsiItemInfoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NsiCommonImpl {

    private final NsiItemInfoRepository nsiItemInfoRepository;

    private final WebServiceTemplate webServiceTemplate;

    private static final String DEFAULT_URL = "https://api.dom.gosuslugi.ru/ext-bus-nsi-common-service/services/NsiCommonAsync";

    public NsiCommonImpl(NsiItemInfoRepository nsiItemInfoRepository, WebServiceTemplate webServiceTemplate) {
        this.nsiItemInfoRepository = nsiItemInfoRepository;
        this.webServiceTemplate = webServiceTemplate;
    }

    public String exportNsiListRequest() {
        ExportNsiListRequest request = new ExportNsiListRequest();
        request.setVersion("10.0.1.2");
        request.setId("signed-data-container");
        AckRequest result = (AckRequest) webServiceTemplate.marshalSendAndReceive(
                DEFAULT_URL,
                request,
                new SoapHeaderAndActionCallback("urn:exportNsiList"));

        if (result != null && result.getAck() != null) {
            return result.getAck().getMessageGUID();
        }

        return "";
    }

    public String getState(String uid) {
        GetStateRequest request = new GetStateRequest();
        request.setMessageGUID(uid);

        GetStateResult result = (GetStateResult) webServiceTemplate.marshalSendAndReceive(
                DEFAULT_URL,
                request,
                new SoapHeaderAndActionCallback("urn:getState"));

        String res = "";
        if (result != null) {
            if (result.getErrorMessage() != null)
                res = result.getErrorMessage().getDescription();
            else if (result.getNsiList().getNsiItemInfo() != null) {
                List<NsiItemInfoDTO> collect = result.getNsiList().getNsiItemInfo().stream()
                        .map(ns -> new NsiItemInfoDTO(ns.getRegistryNumber(), ns.getName(), ns.getModified()))
                        .collect(Collectors.toUnmodifiableList());

                int count = nsiItemInfoRepository.saveAll(collect).size();

                res = String.format("Количество NsiItemInfoType: %d save - %d", result.getNsiList().getNsiItemInfo().size(), count);
            }

        } else
            res = "not result";

        return res;
    }
}
