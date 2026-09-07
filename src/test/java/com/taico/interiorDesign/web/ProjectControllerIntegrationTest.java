package com.taico.interiorDesign.web;


import com.taico.interiorDesign.enums.RoomType;
import com.taico.interiorDesign.enums.ServiceType;
import com.taico.interiorDesign.model.entity.DesignFileEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.repositories.ProjectRepository;
import com.taico.interiorDesign.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithUserDetails;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import com.taico.interiorDesign.enums.ProjectStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.taico.interiorDesign.model.entity.DesignFileEntity;
import com.taico.interiorDesign.repositories.DesignFileRepository;

import org.springframework.http.HttpHeaders;

@SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DesignFileRepository designFileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails("kosta@abv.bg")
    void createPage_shouldReturnProjectCreateView() throws Exception {

        mockMvc.perform(get("/projects/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("project-create"))
                .andExpect(model().attributeExists("projectDTO"))
                .andExpect(model().attributeExists("roomTypes"))
                .andExpect(model().attributeExists("serviceTypes"));
    }


    @Test
    @WithUserDetails("kosta@abv.bg")
    void createProject_shouldRedirectToHome() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        mockMvc.perform(multipart("/projects/create")
                        .file(image)
                        .with(csrf())
                        .param("title", "Моята кухня")
                        .param("description", "Модерен дизайн на кухня")
                        .param("roomType", RoomType.KITCHEN.name())
                        .param("area", "25")
                        .param("budget", "5000")
                        .param("serviceType", ServiceType.INTERIOR_DESIGN.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithUserDetails("kosta@abv.bg")
    void createProject_withInvalidData_shouldReturnCreateView() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "kitchen.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        mockMvc.perform(
                        multipart("/projects/create")
                                .file(image)
                                .with(csrf())
                                .param("title", "")
                                .param("description", "")
                                .param("roomType", RoomType.KITCHEN.name())
                                .param("area", "0")
                                .param("serviceType", ServiceType.INTERIOR_DESIGN.name())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("project-create"))
                .andExpect(model().attributeExists("projectDTO"))
                .andExpect(model().attributeExists("roomTypes"))
                .andExpect(model().attributeExists("serviceTypes"));
    }

    @Test
    @WithUserDetails("kosta@abv.bg")
    void projectDetails_shouldReturnProjectDetailsView() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        mockMvc.perform(
                multipart("/projects/create")
                        .file(image)
                        .with(csrf())
                        .param("title", "Проект за детайли")
                        .param("description", "Описание")
                        .param("roomType", RoomType.KITCHEN.name())
                        .param("area", "20")
                        .param("budget", "4000")
                        .param("serviceType", ServiceType.INTERIOR_DESIGN.name())
        );

        ProjectEntity project = projectRepository.findAll()
                .stream()
                .filter(p -> p.getTitle().equals("Проект за детайли"))
                .findFirst()
                .orElseThrow();

        MvcResult result = mockMvc.perform(
                        get("/projects/" + project.getId())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("project-details"))
                .andExpect(model().attributeExists("project"))
                .andReturn();

        ProjectEntity actualProject =
                (ProjectEntity) result.getModelAndView()
                        .getModel()
                        .get("project");

        assertEquals(project.getId(), actualProject.getId());
        assertEquals("Проект за детайли", actualProject.getTitle());
        assertEquals("Описание", actualProject.getDescription());
        assertEquals(RoomType.KITCHEN, actualProject.getRoomType());
    }

    @Test
    @WithUserDetails("kosta@abv.bg")
    void sendFeedback_shouldUpdateProjectAndRedirect() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        mockMvc.perform(
                multipart("/projects/create")
                        .file(image)
                        .with(csrf())
                        .param("title", "Проект feedback")
                        .param("description", "Описание")
                        .param("roomType", RoomType.BEDROOM.name())
                        .param("area", "18")
                        .param("budget", "3000")
                        .param("serviceType", ServiceType.INTERIOR_DESIGN.name())
        );

        ProjectEntity project = projectRepository.findAll()
                .stream()
                .filter(p -> p.getTitle().equals("Проект feedback"))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(
                        post("/projects/" + project.getId() + "/feedback")
                                .with(csrf())
                                .param("feedback", "Моля, променете цвета на стените.")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/" + project.getId()));

        ProjectEntity updated = projectRepository.findById(project.getId())
                .orElseThrow();

        assertEquals(
                "Моля, променете цвета на стените.",
                updated.getClientFeedback()
        );

        assertEquals(
                ProjectStatus.IN_PROGRESS,
                updated.getStatus()
        );
    }

    @Test
    @WithUserDetails("kosta@abv.bg")
    void approveProject_shouldSetCompletedAndRedirect() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        mockMvc.perform(
                multipart("/projects/create")
                        .file(image)
                        .with(csrf())
                        .param("title", "Проект approve")
                        .param("description", "Описание")
                        .param("roomType", RoomType.LIVING_ROOM.name())
                        .param("area", "30")
                        .param("budget", "6000")
                        .param("serviceType", ServiceType.INTERIOR_DESIGN.name())
        );

        ProjectEntity project = projectRepository.findAll()
                .stream()
                .filter(p -> p.getTitle().equals("Проект approve"))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(
                        post("/projects/" + project.getId() + "/approve")
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/" + project.getId()));

        ProjectEntity updated = projectRepository.findById(project.getId())
                .orElseThrow();

        assertEquals(ProjectStatus.COMPLETED, updated.getStatus());
    }

    @Test
    @WithUserDetails("kosta@abv.bg")
    void uploadImage_shouldRedirectToProjectDetails() throws Exception {

        MockMultipartFile initialImage = new MockMultipartFile(
                "images",
                "initial.jpg",
                "image/jpeg",
                "initial image".getBytes()
        );

        mockMvc.perform(
                multipart("/projects/create")
                        .file(initialImage)
                        .with(csrf())
                        .param("title", "Проект upload image")
                        .param("description", "Описание")
                        .param("roomType", RoomType.KITCHEN.name())
                        .param("area", "20")
                        .param("budget", "4000")
                        .param("serviceType", ServiceType.INTERIOR_DESIGN.name())
        );

        ProjectEntity project = projectRepository.findAll()
                .stream()
                .filter(p -> p.getTitle().equals("Проект upload image"))
                .findFirst()
                .orElseThrow();

        MockMultipartFile newImage = new MockMultipartFile(
                "file",
                "new-image.jpg",
                "image/jpeg",
                "new image content".getBytes()
        );

        mockMvc.perform(
                        multipart("/projects/" + project.getId() + "/images")
                                .file(newImage)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/" + project.getId()));
    }

    @Test
    @WithUserDetails("kosta@abv.bg")
    void uploadImage_withNonExistingProject_shouldReturnError() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        mockMvc.perform(
                        multipart("/projects/999999/images")
                                .file(image)
                                .with(csrf())
                )
                .andExpect(status().is5xxServerError());
    }


    @Test
    @WithUserDetails("kosta@abv.bg")
    void openDesign_shouldReturnFile() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        mockMvc.perform(
                multipart("/projects/create")
                        .file(image)
                        .with(csrf())
                        .param("title", "Проект open design test")
                        .param("description", "Описание")
                        .param("roomType", RoomType.KITCHEN.name())
                        .param("area", "20")
                        .param("budget", "4000")
                        .param("serviceType", ServiceType.INTERIOR_DESIGN.name())
        );

        ProjectEntity project = projectRepository.findAll()
                .stream()
                .filter(p -> p.getTitle().equals("Проект open design test"))
                .findFirst()
                .orElseThrow();

        Path filePath = Files.createTempFile("design-", ".pdf");
        Files.write(filePath, "test design".getBytes());

        DesignFileEntity designFile = new DesignFileEntity();
        designFile.setFileName("design.pdf");
        designFile.setFilePath(filePath.toString());
        designFile.setContentType("application/pdf");
        designFile.setFileSize(Files.size(filePath));
        designFile.setProject(project);
        designFile.setUploadedBy(project.getAuthor());

        designFileRepository.save(designFile);

        mockMvc.perform(
                        get("/projects/" + project.getId()
                                + "/designs/" + designFile.getId())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"design.pdf\""
                ));
    }

    @Test
    @WithUserDetails("kosta@abv.bg")
    void downloadDesign_shouldReturnFileAsAttachment() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        mockMvc.perform(
                multipart("/projects/create")
                        .file(image)
                        .with(csrf())
                        .param("title", "Проект download design")
                        .param("description", "Описание")
                        .param("roomType", RoomType.BEDROOM.name())
                        .param("area", "18")
                        .param("budget", "3000")
                        .param("serviceType", ServiceType.INTERIOR_DESIGN.name())
        );

        ProjectEntity project = projectRepository.findAll()
                .stream()
                .filter(p -> p.getTitle().equals("Проект download design"))
                .findFirst()
                .orElseThrow();

        Path filePath = Files.createTempFile("design-", ".pdf");
        Files.write(filePath, "test design".getBytes());

        DesignFileEntity designFile = new DesignFileEntity();
        designFile.setFileName("design.pdf");
        designFile.setFilePath(filePath.toString());
        designFile.setContentType("application/pdf");
        designFile.setFileSize(Files.size(filePath));
        designFile.setProject(project);
        designFile.setUploadedBy(project.getAuthor());

        designFileRepository.save(designFile);

        mockMvc.perform(
                        get("/projects/" + project.getId()
                                + "/designs/" + designFile.getId() + "/download")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"design.pdf\""
                ));
    }



    @Test
    @WithUserDetails("kosta@abv.bg")
    void openDesign_withInvalidContentType_shouldReturnOctetStream() throws Exception {

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );

        mockMvc.perform(
                multipart("/projects/create")
                        .file(image)
                        .with(csrf())
                        .param("title", "Проект invalid content type")
                        .param("description", "Описание")
                        .param("roomType", RoomType.KITCHEN.name())
                        .param("area", "20")
                        .param("budget", "4000")
                        .param("serviceType", ServiceType.INTERIOR_DESIGN.name())
        );

        ProjectEntity project = projectRepository.findAll()
                .stream()
                .filter(p -> p.getTitle().equals("Проект invalid content type"))
                .findFirst()
                .orElseThrow();

        Path filePath = Files.createTempFile("design-", ".bin");
        Files.write(filePath, "test".getBytes());

        DesignFileEntity designFile = new DesignFileEntity();
        designFile.setFileName("design.bin");
        designFile.setFilePath(filePath.toString());
        designFile.setContentType("invalid-content-type");
        designFile.setFileSize(Files.size(filePath));
        designFile.setProject(project);
        designFile.setUploadedBy(project.getAuthor());

        designFileRepository.save(designFile);

        mockMvc.perform(
                        get("/projects/" + project.getId()
                                + "/designs/" + designFile.getId())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                ));
    }
    @Test
    @WithUserDetails("kosta@abv.bg")
    void openDesign_withMissingFile_shouldReturnServerError() throws Exception {

        ProjectEntity project = new ProjectEntity();
        project.setTitle("Проект missing file");
        project.setDescription("Описание");
        project.setRoomType(RoomType.BEDROOM);
        project.setArea(new BigDecimal("18"));
        project.setBudget(new BigDecimal("3000"));
        project.setServiceType(ServiceType.INTERIOR_DESIGN);
        project.setAuthor(
                userRepository.findByEmail("kosta@abv.bg").orElseThrow()
        );

        projectRepository.save(project);

        DesignFileEntity designFile = new DesignFileEntity();
        designFile.setFileName("missing.pdf");
        designFile.setFilePath("uploads/designs/missing-file.pdf");
        designFile.setContentType("application/pdf");
        designFile.setFileSize(100L);
        designFile.setProject(project);
        designFile.setUploadedBy(project.getAuthor());

        designFileRepository.save(designFile);

        mockMvc.perform(
                        get("/projects/" + project.getId()
                                + "/designs/" + designFile.getId())
                )
                .andExpect(status().is5xxServerError());
    }
}
