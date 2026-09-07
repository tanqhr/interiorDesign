package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.exception.ResourceNotFoundException;
import com.taico.interiorDesign.model.dto.FaqDTO;
import com.taico.interiorDesign.model.entity.FaqEntity;
import com.taico.interiorDesign.repositories.FaqRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaqServiceImplTest {

    @Mock
    private FaqRepository faqRepository;

    @InjectMocks
    private FaqServiceImpl faqService;


    // =========================================================
    // getActiveFaqs()
    // =========================================================

    @Test
    void getActiveFaqs_shouldReturnActiveFaqsAsDTOs() {

        // Arrange
        FaqEntity faq1 = createFaq(
                1L,
                "Question 1",
                "Answer 1",
                "General",
                true
        );

        FaqEntity faq2 = createFaq(
                2L,
                "Question 2",
                "Answer 2",
                "Services",
                true
        );

        when(faqRepository.findByActiveTrueOrderByIdAsc())
                .thenReturn(List.of(faq1, faq2));

        // Act
        List<FaqDTO> result = faqService.getActiveFaqs();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Question 1", result.get(0).getQuestion());
        assertEquals("Answer 1", result.get(0).getAnswer());
        assertEquals("General", result.get(0).getCategory());
        assertTrue(result.get(0).isActive());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Question 2", result.get(1).getQuestion());
        assertEquals("Answer 2", result.get(1).getAnswer());
        assertEquals("Services", result.get(1).getCategory());
        assertTrue(result.get(1).isActive());

        verify(faqRepository)
                .findByActiveTrueOrderByIdAsc();
    }


    @Test
    void getActiveFaqs_shouldReturnEmptyList_whenNoActiveFaqsExist() {

        // Arrange
        when(faqRepository.findByActiveTrueOrderByIdAsc())
                .thenReturn(List.of());

        // Act
        List<FaqDTO> result = faqService.getActiveFaqs();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(faqRepository)
                .findByActiveTrueOrderByIdAsc();
    }


    // =========================================================
    // getAllFaqs()
    // =========================================================

    @Test
    void getAllFaqs_shouldReturnAllFaqsAsDTOs() {

        // Arrange
        FaqEntity activeFaq = createFaq(
                1L,
                "Active question",
                "Active answer",
                "General",
                true
        );

        FaqEntity inactiveFaq = createFaq(
                2L,
                "Inactive question",
                "Inactive answer",
                "Services",
                false
        );

        when(faqRepository.findAll())
                .thenReturn(List.of(activeFaq, inactiveFaq));

        // Act
        List<FaqDTO> result = faqService.getAllFaqs();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.get(0).isActive());
        assertFalse(result.get(1).isActive());

        assertEquals(
                "Active question",
                result.get(0).getQuestion()
        );

        assertEquals(
                "Inactive question",
                result.get(1).getQuestion()
        );

        verify(faqRepository).findAll();
    }


    @Test
    void getAllFaqs_shouldReturnEmptyList_whenNoFaqsExist() {

        // Arrange
        when(faqRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<FaqDTO> result = faqService.getAllFaqs();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(faqRepository).findAll();
    }


    // =========================================================
    // createFaq()
    // =========================================================

    @Test
    void createFaq_shouldCreateAndReturnFaq() {

        // Arrange
        FaqDTO dto = new FaqDTO();

        dto.setQuestion("What is interior design?");
        dto.setAnswer("Interior design is...");
        dto.setCategory("General");
        dto.setActive(true);

        FaqEntity savedFaq = createFaq(
                1L,
                "What is interior design?",
                "Interior design is...",
                "General",
                true
        );

        when(faqRepository.save(any(FaqEntity.class)))
                .thenReturn(savedFaq);

        // Act
        FaqDTO result = faqService.createFaq(dto);

        // Assert
        assertNotNull(result);

        assertEquals(1L, result.getId());
        assertEquals(
                "What is interior design?",
                result.getQuestion()
        );
        assertEquals(
                "Interior design is...",
                result.getAnswer()
        );
        assertEquals("General", result.getCategory());
        assertTrue(result.isActive());

        verify(faqRepository).save(any(FaqEntity.class));
    }


    @Test
    void createFaq_shouldSaveCorrectEntity() {

        // Arrange
        FaqDTO dto = new FaqDTO();

        dto.setQuestion("Question");
        dto.setAnswer("Answer");
        dto.setCategory("Category");
        dto.setActive(false);

        FaqEntity savedFaq = createFaq(
                1L,
                "Question",
                "Answer",
                "Category",
                false
        );

        when(faqRepository.save(any(FaqEntity.class)))
                .thenReturn(savedFaq);

        // Act
        faqService.createFaq(dto);

        // Assert
        ArgumentCaptor<FaqEntity> captor =
                ArgumentCaptor.forClass(FaqEntity.class);

        verify(faqRepository).save(captor.capture());

        FaqEntity savedEntity = captor.getValue();

        assertEquals("Question", savedEntity.getQuestion());
        assertEquals("Answer", savedEntity.getAnswer());
        assertEquals("Category", savedEntity.getCategory());
        assertFalse(savedEntity.isActive());
    }


    // =========================================================
    // updateFaq()
    // =========================================================

    @Test
    void updateFaq_shouldUpdateFaqSuccessfully() {

        // Arrange
        Long id = 1L;

        FaqEntity faq = createFaq(
                id,
                "Old question",
                "Old answer",
                "Old category",
                true
        );

        FaqDTO dto = new FaqDTO();

        dto.setQuestion("New question");
        dto.setAnswer("New answer");
        dto.setCategory("New category");
        dto.setActive(false);

        when(faqRepository.findById(id))
                .thenReturn(Optional.of(faq));

        when(faqRepository.save(faq))
                .thenReturn(faq);

        // Act
        FaqDTO result = faqService.updateFaq(id, dto);

        // Assert
        assertNotNull(result);

        assertEquals(id, result.getId());
        assertEquals("New question", result.getQuestion());
        assertEquals("New answer", result.getAnswer());
        assertEquals("New category", result.getCategory());
        assertFalse(result.isActive());

        verify(faqRepository).findById(id);
        verify(faqRepository).save(faq);
    }


    @Test
    void updateFaq_shouldThrowException_whenFaqDoesNotExist() {

        // Arrange
        Long id = 999L;

        FaqDTO dto = new FaqDTO();

        dto.setQuestion("Question");
        dto.setAnswer("Answer");
        dto.setCategory("Category");
        dto.setActive(true);

        when(faqRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> faqService.updateFaq(id, dto)
                );

        // Assert
        assertEquals(
                "FAQ not found with id: 999",
                exception.getMessage()
        );

        verify(faqRepository).findById(id);

        verify(faqRepository, never())
                .save(any(FaqEntity.class));
    }


    // =========================================================
    // toggleActive()
    // =========================================================

    @Test
    void toggleActive_shouldDeactivateActiveFaq() {

        // Arrange
        Long id = 1L;

        FaqEntity faq = createFaq(
                id,
                "Question",
                "Answer",
                "General",
                true
        );

        when(faqRepository.findById(id))
                .thenReturn(Optional.of(faq));

        when(faqRepository.save(faq))
                .thenReturn(faq);

        // Act
        FaqDTO result = faqService.toggleActive(id);

        // Assert
        assertFalse(faq.isActive());
        assertFalse(result.isActive());

        verify(faqRepository).findById(id);
        verify(faqRepository).save(faq);
    }


    @Test
    void toggleActive_shouldActivateInactiveFaq() {

        // Arrange
        Long id = 1L;

        FaqEntity faq = createFaq(
                id,
                "Question",
                "Answer",
                "General",
                false
        );

        when(faqRepository.findById(id))
                .thenReturn(Optional.of(faq));

        when(faqRepository.save(faq))
                .thenReturn(faq);

        // Act
        FaqDTO result = faqService.toggleActive(id);

        // Assert
        assertTrue(faq.isActive());
        assertTrue(result.isActive());

        verify(faqRepository).findById(id);
        verify(faqRepository).save(faq);
    }


    @Test
    void toggleActive_shouldThrowException_whenFaqDoesNotExist() {

        // Arrange
        Long id = 999L;

        when(faqRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> faqService.toggleActive(id)
                );

        // Assert
        assertEquals(
                "FAQ not found with id: 999",
                exception.getMessage()
        );

        verify(faqRepository).findById(id);

        verify(faqRepository, never())
                .save(any(FaqEntity.class));
    }


    // =========================================================
    // deleteFaq()
    // =========================================================

    @Test
    void deleteFaq_shouldDeleteFaqSuccessfully() {

        // Arrange
        Long id = 1L;

        when(faqRepository.existsById(id))
                .thenReturn(true);

        // Act
        faqService.deleteFaq(id);

        // Assert
        verify(faqRepository).existsById(id);
        verify(faqRepository).deleteById(id);
    }


    @Test
    void deleteFaq_shouldThrowException_whenFaqDoesNotExist() {

        // Arrange
        Long id = 999L;

        when(faqRepository.existsById(id))
                .thenReturn(false);

        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> faqService.deleteFaq(id)
                );

        // Assert
        assertEquals(
                "FAQ not found with id: 999",
                exception.getMessage()
        );

        verify(faqRepository).existsById(id);

        verify(faqRepository, never())
                .deleteById(anyLong());
    }


    // =========================================================
    // HELPER
    // =========================================================

    private FaqEntity createFaq(
            Long id,
            String question,
            String answer,
            String category,
            boolean active
    ) {

        FaqEntity faq = new FaqEntity();

        faq.setId(id);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setCategory(category);
        faq.setActive(active);

        return faq;
    }
}
