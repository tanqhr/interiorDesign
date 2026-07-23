package com.taico.interiorDesign.web;

import com.taico.interiorDesign.enums.RoomType;
import com.taico.interiorDesign.enums.ServiceType;
import com.taico.interiorDesign.model.dto.ProjectCreateDTO;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.taico.interiorDesign.repositories.ProjectRepository;
import com.taico.interiorDesign.service.FileUploadService;
import com.taico.interiorDesign.service.ImageService;


@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final FileUploadService fileUploadService;
    private final ProjectRepository projectRepository;
    private final ImageService imageService;

    public ProjectController(FileUploadService fileUploadService,
                             ProjectRepository projectRepository,
                             ImageService imageService) {
        this.fileUploadService = fileUploadService;
        this.projectRepository = projectRepository;
        this.imageService = imageService;
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
                ServiceType.values()
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
}