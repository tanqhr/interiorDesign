package com.taico.interiorDesign.web;

import com.taico.interiorDesign.enums.RoomType;
import com.taico.interiorDesign.enums.ServiceType;
import com.taico.interiorDesign.model.dto.ProjectCreateDTO;
import com.taico.interiorDesign.model.entity.DesignFileEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.service.ProjectService;
import com.taico.interiorDesign.service.ServiceSettingService;
import jakarta.validation.Valid;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.taico.interiorDesign.repositories.ProjectRepository;
import com.taico.interiorDesign.service.FileUploadService;
import com.taico.interiorDesign.service.ImageService;

import java.io.IOException;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final FileUploadService fileUploadService;
    private final ProjectRepository projectRepository;
    private final ImageService imageService;
    private final ProjectService projectService;
    private final ServiceSettingService serviceSettingService;

    public ProjectController(FileUploadService fileUploadService,
                             ProjectRepository projectRepository,
                             ImageService imageService, ProjectService projectService, ServiceSettingService serviceSettingService) {
        this.fileUploadService = fileUploadService;
        this.projectRepository = projectRepository;
        this.imageService = imageService;
        this.projectService = projectService;
        this.serviceSettingService = serviceSettingService;
    }




    @GetMapping("/create")
    public String create(Model model) {

        if (!model.containsAttribute("projectDTO")) {
            model.addAttribute(
                    "projectDTO",
                    new ProjectCreateDTO()
            );


        }

        model.addAttribute(
                "roomTypes",
                RoomType.values()
        );

        model.addAttribute(
                "serviceTypes",
                serviceSettingService.getActiveServices()
        );


        return "project-create";
    }

    @PostMapping("/{id}/images")
    public String uploadImage(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file) throws Exception {

        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // 1. upload file to disk
        String path = fileUploadService.uploadFile(file, id);

        // 2. save metadata in DB
        imageService.saveImage(
                project,
                file.getOriginalFilename(),
                path,
                file.getContentType(),
                file.getSize()
        );

        return "redirect:/projects/" + id;
    }

    @PostMapping("/create")
    public String createProject(
            @Valid @ModelAttribute("projectDTO") ProjectCreateDTO dto,
            BindingResult bindingResult,
            @RequestParam("images") List<MultipartFile> images,
            Authentication authentication,
            Model model) throws IOException {

        if (bindingResult.hasErrors()) {

            model.addAttribute("roomTypes", RoomType.values());
            model.addAttribute("serviceTypes", serviceSettingService.getActiveServices()
            );

            return "project-create";
        }

        projectService.createProject(dto, images, authentication);

        return "redirect:/home";
    }

    @GetMapping("/{id}")
    public String projectDetails(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        ProjectEntity project =
                projectService.findByIdForUser(
                        id,
                        authentication
                );

        model.addAttribute(
                "project",
                project
        );

        return "project-details";
    }



    @GetMapping("/{projectId}/designs/{fileId}")
    public ResponseEntity<Resource> openDesign(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            Authentication authentication) {

        ProjectEntity project =
                projectService.findByIdForUser(
                        projectId,
                        authentication
                );

        DesignFileEntity designFile =
                project.getDesigns()
                        .stream()
                        .filter(file ->
                                file.getId().equals(fileId)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Файлът не е намерен."
                                )
                        );

        try {

            Path path =
                    Paths.get(designFile.getFilePath());

            Resource resource =
                    new UrlResource(
                            path.toUri()
                    );

            if (!resource.exists()
                    || !resource.isReadable()) {

                throw new RuntimeException(
                        "Файлът не може да бъде прочетен."
                );
            }

            MediaType mediaType;

            try {

                mediaType =
                        MediaType.parseMediaType(
                                designFile.getContentType()
                        );

            } catch (Exception e) {

                mediaType =
                        MediaType.APPLICATION_OCTET_STREAM;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    designFile.getFileName() +
                                    "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Грешка при отваряне на файла.",
                    e
            );
        }
    }

    @GetMapping("/{projectId}/designs/{fileId}/download")
    public ResponseEntity<Resource> downloadDesign(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            Authentication authentication) {

        // Проверяваме дали проектът принадлежи на текущия клиент
        ProjectEntity project =
                projectService.findByIdForUser(
                        projectId,
                        authentication
                );

        // Търсим файла само сред файловете на този проект
        DesignFileEntity designFile =
                project.getDesigns()
                        .stream()
                        .filter(file ->
                                file.getId().equals(fileId)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Файлът не е намерен."
                                )
                        );

        try {


            Path path =
                    Paths.get(designFile.getFilePath());

            Resource resource =
                    new UrlResource(path.toUri());

            if (!resource.exists()
                    || !resource.isReadable()) {

                throw new RuntimeException(
                        "Файлът не може да бъде прочетен."
                );
            }

            MediaType mediaType;

            try {

                mediaType =
                        MediaType.parseMediaType(
                                designFile.getContentType()
                        );

            } catch (Exception e) {

                mediaType =
                        MediaType.APPLICATION_OCTET_STREAM;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" +
                                    designFile.getFileName() +
                                    "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Грешка при сваляне на файла.",
                    e
            );
        }
    }

}