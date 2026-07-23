package com.taico.interiorDesign.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServiceController {

    @GetMapping("/services")
    public String services() {
        return "services";
    }
}