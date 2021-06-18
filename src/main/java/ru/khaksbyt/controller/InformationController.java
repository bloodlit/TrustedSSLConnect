package ru.khaksbyt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.khaksbyt.service.HouseManagementClient;
import ru.khaksbyt.service.InformationService;

@Controller
public class InformationController {

    private final InformationService service;
    private final HouseManagementClient client;

    public InformationController(InformationService service, HouseManagementClient client) {
        this.service = service;
        this.client = client;
    }

    @GetMapping("/information")
    public String information(Model model) {

        String res = client.getState("C4C98E02-2FB7-2772-6E05-30100007F149");

        model.addAttribute("certificate", service.certificateName());
        model.addAttribute("result", res);

        return "information";
    }
}
