package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.enums.ProjectStatus;
import com.taico.interiorDesign.exception.ProjectAlreadyPaidException;
import com.taico.interiorDesign.model.dto.ProjectUpdateDTO;
import com.taico.interiorDesign.model.entity.DesignFileEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.DesignFileRepository;
import com.taico.interiorDesign.repositories.ProjectRepository;
import com.taico.interiorDesign.repositories.UserRepository;
import com.taico.interiorDesign.security.CurrentUser;
import com.taico.interiorDesign.service.FileUploadService;
import com.taico.interiorDesign.service.ImageService;
import com.taico.interiorDesign.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.AfterEach;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ImageService imageService;

    @Mock
    private DesignFileRepository designFileRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private ProjectEntity project;
    private UserEntity user;
    private Path createdTestFile;

    @BeforeEach
    void setUp() {

        user = new UserEntity();

        project = new ProjectEntity();

        project.setId(1L);
        project.setTitle("Стилен апартамент");
        project.setStatus(ProjectStatus.NEW);
        project.setAuthor(user);
    }


    @Test
    void updateProject_WhenPriceIsSet_ShouldChangeStatusToPendingPayment() {

        ProjectUpdateDTO dto = new ProjectUpdateDTO();

        dto.setPrice(new BigDecimal("850.00"));

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));


        projectService.updateProject(1L, dto);


        assertEquals(
                ProjectStatus.PENDING_PAYMENT,
                project.getStatus()
        );

        assertEquals(
                new BigDecimal("850.00"),
                project.getPrice()
        );
    }


    @Test
    void updateProject_WhenPriceIsSet_ShouldCreateNotification() {

        ProjectUpdateDTO dto = new ProjectUpdateDTO();

        dto.setPrice(new BigDecimal("850.00"));

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));


        projectService.updateProject(1L, dto);


        verify(notificationService, times(1))
                .createNotification(
                        eq(user),
                        eq(project),
                        contains("очаква плащане")
                );
    }


    @Test
    void payProject_WhenProjectIsPendingPayment_ShouldChangeStatusToPaid() {

        project.setStatus(ProjectStatus.PENDING_PAYMENT);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));


        projectService.payProject(1L, "CARD");


        assertEquals(
                ProjectStatus.PAID,
                project.getStatus()
        );

        verify(projectRepository, times(1))
                .save(project);
    }


    @Test
    void payProject_WhenProjectIsAlreadyPaid_ShouldThrowException() {

        project.setStatus(ProjectStatus.PAID);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));


        assertThrows(
                ProjectAlreadyPaidException.class,
                () -> projectService.payProject(1L, "CARD")
        );


        verify(projectRepository, never())
                .save(any(ProjectEntity.class));
    }


    @Test
    void uploadDesignFile_WhenValidFile_ShouldUploadAndNotifyClient()
            throws Exception {

        MultipartFile file = mock(MultipartFile.class);
        Authentication authentication = mock(Authentication.class);
        CurrentUser currentUser = mock(CurrentUser.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("design.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(5000L);

        when(file.getInputStream())
                .thenReturn(
                        new ByteArrayInputStream(
                                "test design".getBytes()
                        )
                );

        when(authentication.getPrincipal())
                .thenReturn(currentUser);

        when(currentUser.getId())
                .thenReturn(2L);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(designFileRepository.findByProjectId(1L))
                .thenReturn(Optional.empty());

        projectService.uploadDesignFile(
                1L,
                file,
                authentication
        );

        Path designDirectory = Paths.get("uploads", "designs");

        try (var files = Files.list(designDirectory)) {
            createdTestFile = files
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElse(null);
        }

        assertEquals(
                ProjectStatus.WAITING_FOR_CLIENT,
                project.getStatus()
        );

        verify(designFileRepository, times(1))
                .save(any(DesignFileEntity.class));

        verify(notificationService, times(1))
                .createNotification(
                        eq(user),
                        eq(project),
                        contains("готов за преглед")
                );

        verify(projectRepository, times(1))
                .save(project);
    }

    @AfterEach
    void cleanupTestFile() throws Exception {
        if (createdTestFile != null) {
            Files.deleteIfExists(createdTestFile);
        }
    }


    @Test
    void uploadDesignFile_WhenFileIsEmpty_ShouldThrowException() {

        MultipartFile file = mock(MultipartFile.class);
        Authentication authentication = mock(Authentication.class);

        when(file.isEmpty()).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.uploadDesignFile(
                        1L,
                        file,
                        authentication
                )
        );

        verify(designFileRepository, never())
                .save(any(DesignFileEntity.class));
    }


    @Test
    void uploadDesignFile_WhenDesignAlreadyExists_ShouldUpdateExistingDesign()
            throws Exception {

        MultipartFile file = mock(MultipartFile.class);
        Authentication authentication = mock(Authentication.class);
        CurrentUser currentUser = mock(CurrentUser.class);

        DesignFileEntity existingDesign = new DesignFileEntity();

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("new-design.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(6000L);
        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream("new design".getBytes()));

        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(currentUser.getId()).thenReturn(2L);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(designFileRepository.findByProjectId(1L))
                .thenReturn(Optional.of(existingDesign));

        projectService.uploadDesignFile(
                1L,
                file,
                authentication
        );

        verify(designFileRepository, times(1))
                .save(existingDesign);
    }

    @Test
    void deleteUnpaidProject_WhenProjectIsNew_ShouldDeleteProject() {

        project.setStatus(ProjectStatus.NEW);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        projectService.deleteUnpaidProject(1L);

        verify(projectRepository, times(1))
                .delete(project);
    }


    @Test
    void deleteUnpaidProject_WhenProjectIsPaid_ShouldNotDeleteProject() {

        project.setStatus(ProjectStatus.PAID);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        assertThrows(
                RuntimeException.class,
                () -> projectService.deleteUnpaidProject(1L)
        );

        verify(projectRepository, never())
                .delete(any(ProjectEntity.class));
    }


    @Test
    void deleteUnpaidProject_WhenProjectDoesNotExist_ShouldThrowException() {

        when(projectRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> projectService.deleteUnpaidProject(1L)
        );

        verify(projectRepository, never())
                .delete(any(ProjectEntity.class));
    }


    @Test
    void deleteUnpaidProject_WhenProjectIsPendingPayment_ShouldDeleteProject() {

        project.setStatus(ProjectStatus.PENDING_PAYMENT);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        projectService.deleteUnpaidProject(1L);

        verify(projectRepository, times(1))
                .delete(project);
    }

    @Test
    void sendFeedback_ShouldSaveFeedbackAndChangeStatus() {

        Authentication authentication = mock(Authentication.class);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        projectService.sendFeedback(
                1L,
                "Искам промяна на цветовете.",
                authentication
        );

        assertEquals(
                "Искам промяна на цветовете.",
                project.getClientFeedback()
        );

        assertEquals(
                ProjectStatus.IN_PROGRESS,
                project.getStatus()
        );

        verify(projectRepository, times(1))
                .save(project);
    }

    @Test
    void approveProject_ShouldChangeStatusToCompleted() {

        Authentication authentication = mock(Authentication.class);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        projectService.approveProject(
                1L,
                authentication
        );

        assertEquals(
                ProjectStatus.COMPLETED,
                project.getStatus()
        );

        verify(projectRepository, times(1))
                .save(project);
    }




}
