package com.taico.interiorDesign.web.rest;

import com.taico.interiorDesign.model.dto.FaqDTO;
import com.taico.interiorDesign.service.FaqService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
public class FaqRestController {

    private final FaqService faqService;

    public FaqRestController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public List<FaqDTO> getFaqs() {
        return faqService.getActiveFaqs();
    }
}