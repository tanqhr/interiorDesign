package com.taico.interiorDesign.web.rest;

import com.taico.interiorDesign.model.dto.FaqDTO;
import com.taico.interiorDesign.service.FaqService;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/all")
    public List<FaqDTO> getAllFaqs() {
        return faqService.getAllFaqs();
    }

    @PostMapping
    public FaqDTO createFaq(@RequestBody FaqDTO dto) {
        return faqService.createFaq(dto);
    }

    @PutMapping("/{id}")
    public FaqDTO updateFaq(
            @PathVariable Long id,
            @RequestBody FaqDTO dto) {

        return faqService.updateFaq(id, dto);
    }

    @PatchMapping("/{id}/active")
    public FaqDTO toggleActive(@PathVariable Long id) {
        return faqService.toggleActive(id);
    }

    @DeleteMapping("/{id}")
    public void deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
    }
}