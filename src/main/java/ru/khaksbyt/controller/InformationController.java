package ru.khaksbyt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.khaksbyt.service.HouseManagementClient;
import ru.khaksbyt.service.NsiCommonImpl;

@Controller
public class InformationController {
    private final NsiCommonImpl nsiCommon;
    private final HouseManagementClient client;

    public InformationController(NsiCommonImpl nsiCommon, HouseManagementClient client) {
        this.nsiCommon = nsiCommon;
        this.client = client;
    }

    @GetMapping("/house/exportHouseRequest")
    public String exportHouseRequest() {
        return "exportHouseRequest";
    }

    @RequestMapping(value = "/house/exportHouseRequest", method = RequestMethod.POST)
    public String information(@RequestParam(value = "guid", required = true) String guid, Model model) {

        String res = client.exportHouseRequest(guid);

        model.addAttribute("request", guid);
        model.addAttribute("type", "HouseManagement");
        model.addAttribute("result", res);

        return "information";
    }

    @GetMapping("/base/getstate/{type}/{guid}")
    public String getstat(@PathVariable String type, @PathVariable String guid, Model model) {
        String res = "";

        if ("HouseManagement".equals(type))
            res = client.getState(guid);
        else if ("NsiCommon".equals(type))
            res = nsiCommon.getState(guid);

        model.addAttribute("result", res);

        return "getstate";
    }

    @GetMapping("/nsi/listrequest")
    public String listrequest(Model model) {

        String res = nsiCommon.exportNsiListRequest();

        model.addAttribute("request", "exportNsiListRequest");
        model.addAttribute("type", "NsiCommon");
        model.addAttribute("result", res);

        return "information";
    }
}
