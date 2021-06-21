package ru.khaksbyt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.khaksbyt.service.HouseManagementClient;

@Controller
public class InformationController {

    private final HouseManagementClient client;

    public InformationController(HouseManagementClient client) {
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
        model.addAttribute("result", res);

        return "information";
    }

    @GetMapping("/base/getstate/{guid}")
    public String getstat(@PathVariable String guid, Model model) {

        String res = client.getState(guid);
        model.addAttribute("result", res);

        return "getstate";
    }
}
