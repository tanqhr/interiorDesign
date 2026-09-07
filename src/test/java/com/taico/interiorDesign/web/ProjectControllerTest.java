package com.taico.interiorDesign.web;


import com.taico.interiorDesign.enums.RoomType;
import com.taico.interiorDesign.enums.ServiceType;
import com.taico.interiorDesign.model.dto.ProjectCreateDTO;
import com.taico.interiorDesign.model.entity.DesignFileEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.repositories.ProjectRepository;
import com.taico.interiorDesign.service.FileUploadService;
import com.taico.interiorDesign.service.ImageService;
import com.taico.interiorDesign.service.ProjectService;
import com.taico.interiorDesign.service.ServiceSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private ProjectService projectService;

    @Mock
    private ServiceSettingService serviceSettingService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ProjectController projectController;


    // =========================================================
    // create()
    // =========================================================

    @Test
    void create_shouldAddProjectDtoRoomTypesAndServiceTypes() {

        when(model.containsAttribute("projectDTO"))
                .thenReturn(false);

        List<ServiceType> activeServices =
                List.of(ServiceType.INTERIOR_DESIGN);

        when(serviceSettingService.getActiveServices())
                .thenReturn(activeServices);

        String result = projectController.create(model);

        assertEquals("project-create", result);

        verify(model).addAttribute(
                eq("projectDTO"),
                any(ProjectCreateDTO.class)
        );

        verify(model).addAttribute(
                "roomTypes",
                RoomType.values()
        );

        verify(model).addAttribute(
                "serviceTypes",
                activeServices
        );
    }


    @Test
    void create_shouldNotReplaceExistingProjectDto() {

        when(model.containsAttribute("projectDTO"))
                .thenReturn(true);

        List<ServiceType> activeServices =
                List.of(ServiceType.INTERIOR_DESIGN);

        when(serviceSettingService.getActiveServices())
                .thenReturn(activeServices);

        String result = projectController.create(model);

        assertEquals("project-create", result);

        verify(model, never()).addAttribute(
                eq("projectDTO"),
                any(ProjectCreateDTO.class)
        );

        verify(model).addAttribute(
                "roomTypes",
                RoomType.values()
        );

        verify(model).addAttribute(
                "serviceTypes",
                activeServices
        );
    }


    // =========================================================
    // uploadImage()
    // =========================================================

    @Test
    void uploadImage_shouldUploadAndSaveImage() throws Exception {

        Long projectId = 1L;

        ProjectEntity project = mock(ProjectEntity.class);

        MultipartFile file = mock(MultipartFile.class);

        when(projectRepository.findById(projectId))
                .thenReturn(java.util.Optional.of(project));

        when(fileUploadService.uploadFile(file, projectId))
                .thenReturn("uploads/test.jpg");

        when(file.getOriginalFilename())
                .thenReturn("test.jpg");

        when(file.getContentType())
                .thenReturn("image/jpeg");

        when(file.getSize())
                .thenReturn(12345L);

        String result =
                projectController.uploadImage(projectId, file);

        assertEquals(
                "redirect:/projects/1",
                result
        );

        verify(projectRepository).findById(projectId);

        verify(fileUploadService)
                .uploadFile(file, projectId);

        verify(imageService).saveImage(
                project,
                "test.jpg",
                "uploads/test.jpg",
                "image/jpeg",
                12345L
        );
    }


    @Test
    void uploadImage_shouldThrowException_whenProjectDoesNotExist() {

        Long projectId = 999L;

        MultipartFile file = mock(MultipartFile.class);

        when(projectRepository.findById(projectId))
                .thenReturn(java.util.Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> projectController.uploadImage(
                                projectId,
                                file
                        )
                );

        assertEquals(
                "Project not found",
                exception.getMessage()
        );

        verifyNoInteractions(fileUploadService);
        verifyNoInteractions(imageService);
    }


    // =========================================================
    // createProject()
    // =========================================================

    @Test
    void createProject_shouldCreateProjectAndRedirect() throws Exception {

        ProjectCreateDTO dto = mock(ProjectCreateDTO.class);

        List<MultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "test.jpg",
                                "image/jpeg",
                                "test".getBytes()
                        )
                );

        when(bindingResult.hasErrors())
                .thenReturn(false);

        String result =
                projectController.createProject(
                        dto,
                        bindingResult,
                        images,
                        authentication,
                        model
                );

        assertEquals(
                "redirect:/home",
                result
        );

        verify(projectService).createProject(
                dto,
                images,
                authentication
        );

        verifyNoInteractions(serviceSettingService);
    }


    @Test
    void createProject_shouldReturnCreatePage_whenValidationFails()
            throws Exception {

        ProjectCreateDTO dto = mock(ProjectCreateDTO.class);

        List<MultipartFile> images = List.of();

        List<ServiceType> activeServices =
                List.of(ServiceType.INTERIOR_DESIGN);

        when(bindingResult.hasErrors())
                .thenReturn(true);

        when(serviceSettingService.getActiveServices())
                .thenReturn(activeServices);

        String result =
                projectController.createProject(
                        dto,
                        bindingResult,
                        images,
                        authentication,
                        model
                );

        assertEquals(
                "project-create",
                result
        );

        verify(model).addAttribute(
                "roomTypes",
                RoomType.values()
        );

        verify(model).addAttribute(
                "serviceTypes",
                activeServices
        );

        verifyNoInteractions(projectService);
    }


    // =========================================================
    // projectDetails()
    // =========================================================

    @Test
    void projectDetails_shouldReturnProjectDetailsPage() {

        Long projectId = 1L;

        ProjectEntity project =
                mock(ProjectEntity.class);

        when(projectService.findByIdForUser(
                projectId,
                authentication
        )).thenReturn(project);

        String result =
                projectController.projectDetails(
                        projectId,
                        authentication,
                        model
                );

        assertEquals(
                "project-details",
                result
        );

        verify(projectService)
                .findByIdForUser(
                        projectId,
                        authentication
                );

        verify(model)
                .addAttribute(
                        "project",
                        project
                );
    }


    // =========================================================
    // sendFeedback()
    // =========================================================

    @Test
    void sendFeedback_shouldSendFeedbackAndRedirect() {

        Long projectId = 1L;
        String feedback = "Много ми харесва.";

        String result =
                projectController.sendFeedback(
                        projectId,
                        feedback,
                        authentication
                );

        assertEquals(
                "redirect:/projects/1",
                result
        );

        verify(projectService)
                .sendFeedback(
                        projectId,
                        feedback,
                        authentication
                );
    }


    // =========================================================
    // approveProject()
    // =========================================================

    @Test
    void approveProject_shouldApproveAndRedirect() {

        Long projectId = 1L;

        String result =
                projectController.approveProject(
                        projectId,
                        authentication
                );

        assertEquals(
                "redirect:/projects/1",
                result
        );

        verify(projectService)
                .approveProject(
                        projectId,
                        authentication
                );
    }


    // =========================================================
    // openDesign()
    // =========================================================

    @Test
    void openDesign_shouldReturnResourceWithInlineDisposition()
            throws Exception {

        Long projectId = 1L;
        Long fileId = 10L;

        Path tempFile =
                Files.createTempFile(
                        "design-test",
                        ".pdf"
                );

        Files.write(
                tempFile,
                "test design".getBytes()
        );

        DesignFileEntity designFile =
                mock(DesignFileEntity.class);

        when(designFile.getId())
                .thenReturn(fileId);

        when(designFile.getFilePath())
                .thenReturn(tempFile.toString());

        when(designFile.getContentType())
                .thenReturn("application/pdf");

        when(designFile.getFileName())
                .thenReturn("design.pdf");

        ProjectEntity project =
                mock(ProjectEntity.class);

        when(project.getDesigns())
                .thenReturn(List.of(designFile));

        when(projectService.findByIdForUser(
                projectId,
                authentication
        )).thenReturn(project);

        var response =
                projectController.openDesign(
                        projectId,
                        fileId,
                        authentication
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "application/pdf",
                response.getHeaders()
                        .getContentType()
                        .toString()
        );

        assertEquals(
                "inline; filename=\"design.pdf\"",
                response.getHeaders()
                        .getFirst("Content-Disposition")
        );

        Files.deleteIfExists(tempFile);
    }


    @Test
    void openDesign_shouldThrowException_whenFileDoesNotBelongToProject() {

        Long projectId = 1L;
        Long fileId = 999L;

        DesignFileEntity designFile =
                mock(DesignFileEntity.class);

        when(designFile.getId())
                .thenReturn(10L);

        ProjectEntity project =
                mock(ProjectEntity.class);

        when(project.getDesigns())
                .thenReturn(List.of(designFile));

        when(projectService.findByIdForUser(
                projectId,
                authentication
        )).thenReturn(project);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> projectController.openDesign(
                                projectId,
                                fileId,
                                authentication
                        )
                );

        assertEquals(
                "Файлът не е намерен.",
                exception.getMessage()
        );
    }


    // =========================================================
    // downloadDesign()
    // =========================================================

    @Test
    void downloadDesign_shouldReturnResourceWithAttachmentDisposition()
            throws Exception {

        Long projectId = 1L;
        Long fileId = 10L;

        Path tempFile =
                Files.createTempFile(
                        "design-download",
                        ".pdf"
                );

        Files.write(
                tempFile,
                "test design".getBytes()
        );

        DesignFileEntity designFile =
                mock(DesignFileEntity.class);

        when(designFile.getId())
                .thenReturn(fileId);

        when(designFile.getFilePath())
                .thenReturn(tempFile.toString());

        when(designFile.getContentType())
                .thenReturn("application/pdf");

        when(designFile.getFileName())
                .thenReturn("final-design.pdf");

        ProjectEntity project =
                mock(ProjectEntity.class);

        when(project.getDesigns())
                .thenReturn(List.of(designFile));

        when(projectService.findByIdForUser(
                projectId,
                authentication
        )).thenReturn(project);

        var response =
                projectController.downloadDesign(
                        projectId,
                        fileId,
                        authentication
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "application/pdf",
                response.getHeaders()
                        .getContentType()
                        .toString()
        );

        assertEquals(
                "attachment; filename=\"final-design.pdf\"",
                response.getHeaders()
                        .getFirst("Content-Disposition")
        );

        Files.deleteIfExists(tempFile);
    }


    @Test
    void downloadDesign_shouldThrowException_whenFileDoesNotBelongToProject() {

        Long projectId = 1L;
        Long fileId = 999L;

        DesignFileEntity designFile =
                mock(DesignFileEntity.class);

        when(designFile.getId())
                .thenReturn(10L);

        ProjectEntity project =
                mock(ProjectEntity.class);

        when(project.getDesigns())
                .thenReturn(List.of(designFile));

        when(projectService.findByIdForUser(
                projectId,
                authentication
        )).thenReturn(project);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> projectController.downloadDesign(
                                projectId,
                                fileId,
                                authentication
                        )
                );

        assertEquals(
                "Файлът не е намерен.",
                exception.getMessage()
        );
    }


    @Test
    void downloadDesign_shouldUseOctetStream_whenContentTypeIsInvalid()
            throws Exception {

        Long projectId = 1L;
        Long fileId = 10L;

        Path tempFile =
                Files.createTempFile(
                        "design",
                        ".unknown"
                );

        Files.write(
                tempFile,
                "test".getBytes()
        );

        DesignFileEntity designFile =
                mock(DesignFileEntity.class);

        when(designFile.getId())
                .thenReturn(fileId);

        when(designFile.getFilePath())
                .thenReturn(tempFile.toString());

        when(designFile.getContentType())
                .thenReturn("invalid-content-type");

        when(designFile.getFileName())
                .thenReturn("design.bin");

        ProjectEntity project =
                mock(ProjectEntity.class);

        when(project.getDesigns())
                .thenReturn(List.of(designFile));

        when(projectService.findByIdForUser(
                projectId,
                authentication
        )).thenReturn(project);

        var response =
                projectController.downloadDesign(
                        projectId,
                        fileId,
                        authentication
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                "application/octet-stream",
                response.getHeaders()
                        .getContentType()
                        .toString()
        );

        assertEquals(
                "attachment; filename=\"design.bin\"",
                response.getHeaders()
                        .getFirst("Content-Disposition")
        );

        Files.deleteIfExists(tempFile);
    }
}
