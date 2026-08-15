package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.model.dto.FaqDTO;
import com.taico.interiorDesign.model.entity.FaqEntity;
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

    // Публичната страница - само активни FAQ
    @Override
    public List<FaqDTO> getActiveFaqs() {

        return faqRepository.findByActiveTrueOrderByIdAsc()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Администраторът вижда всички FAQ
    @Override
    public List<FaqDTO> getAllFaqs() {

        return faqRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Добавяне на нов FAQ
    @Override
    public FaqDTO createFaq(FaqDTO dto) {

        FaqEntity faq = new FaqEntity();

        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faq.setCategory(dto.getCategory());

        // Ако DTO не задава active, новият FAQ ще бъде активен
        faq.setActive(dto.isActive());

        FaqEntity savedFaq = faqRepository.save(faq);

        return toDTO(savedFaq);
    }

    // Редактиране на съществуващ FAQ
    @Override
    public FaqDTO updateFaq(Long id, FaqDTO dto) {

        FaqEntity faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found with id: " + id));

        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faq.setCategory(dto.getCategory());
        faq.setActive(dto.isActive());

        FaqEntity updatedFaq = faqRepository.save(faq);

        return toDTO(updatedFaq);
    }

    // Активиране / деактивиране
    @Override
    public FaqDTO toggleActive(Long id) {

        FaqEntity faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found with id: " + id));

        faq.setActive(!faq.isActive());

        FaqEntity updatedFaq = faqRepository.save(faq);

        return toDTO(updatedFaq);
    }

    // Изтриване
    @Override
    public void deleteFaq(Long id) {

        if (!faqRepository.existsById(id)) {
            throw new RuntimeException("FAQ not found with id: " + id);
        }

        faqRepository.deleteById(id);
    }

    // Entity -> DTO
    private FaqDTO toDTO(FaqEntity faq) {

        FaqDTO dto = new FaqDTO();

        dto.setId(faq.getId());
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        dto.setCategory(faq.getCategory());
        dto.setActive(faq.isActive());

        return dto;
    }
}

