package com.taico.interiorDesign.service;

import com.taico.interiorDesign.model.dto.FaqDTO;

import java.util.List;

public interface FaqService {

    List<FaqDTO> getActiveFaqs();

    List<FaqDTO> getAllFaqs();

    FaqDTO createFaq(FaqDTO dto);

    FaqDTO updateFaq(Long id, FaqDTO dto);

    FaqDTO toggleActive(Long id);

    void deleteFaq(Long id);
}
