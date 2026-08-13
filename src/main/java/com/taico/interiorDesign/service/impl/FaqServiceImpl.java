package com.taico.interiorDesign.service.impl;


import com.taico.interiorDesign.model.dto.FaqDTO;
import com.taico.interiorDesign.repositories.FaqRepository;
import com.taico.interiorDesign.service.FaqService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqServiceImpl implements FaqService {

        private final FaqRepository faqRepository;

    public FaqServiceImpl(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<FaqDTO> getActiveFaqs() {

            return faqRepository.findByActiveTrueOrderByIdAsc()
                    .stream()
                    .map(faq -> {
                        FaqDTO dto = new FaqDTO();

                        dto.setId(faq.getId());
                        dto.setQuestion(faq.getQuestion());
                        dto.setAnswer(faq.getAnswer());
                        dto.setCategory(faq.getCategory());

                        return dto;
                    })
                    .toList();
        }
    }

