package com.taico.interiorDesign.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PriceController {

    @GetMapping("/prices")
    public String prices() {
        return "prices";
    }
}
