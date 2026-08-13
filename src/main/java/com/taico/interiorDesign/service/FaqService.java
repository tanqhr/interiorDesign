package com.taico.interiorDesign.service;

import com.taico.interiorDesign.model.dto.FaqDTO;

import java.util.List;

public interface FaqService {

    List<FaqDTO> getActiveFaqs();
}
