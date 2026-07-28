package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.enums.ProjectStatus;
import com.taico.interiorDesign.model.dto.*;
import com.taico.interiorDesign.model.entity.DesignFileEntity;
import com.taico.interiorDesign.model.entity.ImageEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.DesignFileRepository;
import com.taico.interiorDesign.repositories.ProjectRepository;
import com.taico.interiorDesign.repositories.UserRepository;
import com.taico.interiorDesign.security.CurrentUser;
import com.taico.interiorDesign.service.FileUploadService;
import com.taico.interiorDesign.service.ImageService;
import com.taico.interiorDesign.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import java.io.IOException;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public

class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final ImageService imageService;
    private final DesignFileRepository designFileRepository;


    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, FileUploadService fileUploadService, ImageService imageService, DesignFileRepository designFileRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
        this.imageService = imageService;
        this.designFileRepository = designFileRepository;
    }

    @Override
    public List<ProjectViewDTO> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::mapToViewDto)
                .toList();
    }



    private ProjectViewDTO mapToViewDto(ProjectEntity project) {

        ProjectViewDTO dto = new ProjectViewDTO();



        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setRoomType(project.getRoomType());
        dto.setStatus(project.getStatus());
        dto.setAuthor(project.getAuthor().getFirstName()
                + " "
                + project.getAuthor().getLastName());

        return dto;
    }



    @Override
    @Transactional
    public ProjectDetailsDTO getProjectById(Long id) {

        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Проектът не е намерен с id: " + id
                        )
                );

        ProjectDetailsDTO dto = new ProjectDetailsDTO();

        dto.setId(project.getId());

        dto.setTitle(project.getTitle());

        dto.setDescription(
                project.getDescription()
        );

        dto.setRoomType(
                project.getRoomType()
        );

        dto.setArea(
                project.getArea()
        );

        dto.setBudget(
                project.getBudget()
        );

        dto.setPrice(
                project.getPrice()
        );

        dto.setServiceType(
                project.getServiceType()
        );

        dto.setStatus(
                project.getStatus()
        );

        dto.setAuthor(
                project.getAuthor().getFirstName()
                        + " "
                        + project.getAuthor().getLastName()
        );

        dto.setCreatedAt(
                project.getCreatedAt()
        );

        dto.setUpdatedAt(
                project.getUpdatedAt()
        );


        List<ImageDTO> images = project.getImages()
                .stream()
                .map(image -> {

                    ImageDTO imageDTO = new ImageDTO();

                    imageDTO.setId(
                            image.getId()
                    );

                    imageDTO.setFileName(
                            image.getFileName()
                    );

                    imageDTO.setFilePath(
                            image.getFilePath()
                    );

                    return imageDTO;

                })
                .toList();

        dto.setDesigns(
                project.getDesigns()
                        .stream()
                        .map(design -> {

                            DesignFileDTO designDTO =
                                    new DesignFileDTO();

                            designDTO.setId(design.getId());
                            designDTO.setFileName(design.getFileName());
                            designDTO.setFilePath(design.getFilePath());
                            designDTO.setContentType(design.getContentType());
                            designDTO.setFileSize(design.getFileSize());
                            designDTO.setUploadedAt(design.getUploadedAt());

                            return designDTO;

                        })
                        .toList()
        );

        dto.setImages(images);


        return dto;
    }

    @Override
    public void createProject (
            ProjectCreateDTO dto,
            List<MultipartFile> images,
            Authentication authentication){

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        UserEntity author = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProjectEntity project = new ProjectEntity();

        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setRoomType(dto.getRoomType());
        project.setServiceType(dto.getServiceType());
        project.setBudget(dto.getBudget());
        project.setAuthor(author);
        project.setArea(dto.getArea());

        project = projectRepository.save(project);

        for (MultipartFile image : images) {

            if (image.isEmpty()) {
                continue;
            }

            String path = fileUploadService.uploadFile(image, project.getId());

            imageService.saveImage(
                    project,
                    image.getOriginalFilename(),
                    path,
                    image.getContentType(),
                    image.getSize()
            );
        }

    }

    @Override
    @Transactional
    public void updateProject(Long id, ProjectUpdateDTO dto) {

        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Проектът не е намерен с id: " + id)
                );

        project.setPrice(dto.getPrice());

        project.setStatus(dto.getStatus());

        project.setAdminNote(dto.getAdminNote());

        projectRepository.save(project);
    }
    @Override
    public List<ProjectEntity> findAll() {

        return projectRepository.findAll();
    }

    @Override
    public ProjectDetailsDTO getProjectDetails(Long id) {

        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        ProjectDetailsDTO dto = new ProjectDetailsDTO();

        // =========================
        // BASIC INFO
        // =========================

        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());

        dto.setRoomType(project.getRoomType());
        dto.setServiceType(project.getServiceType());

        dto.setBudget(project.getBudget());
        dto.setPrice(project.getPrice());

        dto.setStatus(project.getStatus());
        dto.setAdminNote(project.getAdminNote());

        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());


        // =========================
        // CLIENT
        // =========================

        dto.setClientName(
                project.getAuthor().getFirstName()
                        + " "
                        + project.getAuthor().getLastName()
        );

        dto.setClientEmail(
                project.getAuthor().getEmail()
        );


        // =========================
        // IMAGES
        // =========================

        dto.setImages(
                project.getImages()
                        .stream()
                        .map(image -> {

                            ImageDTO imageDTO = new ImageDTO();

                            imageDTO.setId(
                                    image.getId()
                            );

                            imageDTO.setFileName(
                                    image.getFileName()
                            );

                            imageDTO.setFilePath(
                                    image.getFilePath()
                            );

                            return imageDTO;

                        })
                        .toList()
        );


        // =========================
        // DESIGN FILES
        // =========================

        dto.setDesigns(
                project.getDesigns()
                        .stream()
                        .map(design -> {

                            DesignFileDTO designDTO =
                                    new DesignFileDTO();

                            designDTO.setId(
                                    design.getId()
                            );

                            designDTO.setFileName(
                                    design.getFileName()
                            );

                            designDTO.setFilePath(
                                    design.getFilePath()
                            );

                            designDTO.setContentType(
                                    design.getContentType()
                            );

                            designDTO.setFileSize(
                                    design.getFileSize()
                            );

                            designDTO.setUploadedAt(
                                    design.getUploadedAt()
                            );

                            return designDTO;

                        })
                        .toList()
        );


        return dto;
    }


@Override
public List<ProjectEntity> findByAuthor(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow();

        return projectRepository.findByAuthor(user);
    }

@Override
public ProjectEntity findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Проектът не е намерен"));
    }
    @Transactional
    public void payProject(
            Long projectId,
            String paymentMethod
    ) {

        ProjectEntity project =
                projectRepository.findById(projectId)
                        .orElseThrow();

        project.setStatus(
                ProjectStatus.PAID
        );

        projectRepository.save(project);
    }


    @Override
    @Transactional
    public void uploadDesignFile(
            Long projectId,
            MultipartFile file,
            Authentication authentication
    ) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Моля, изберете файл."
            );
        }

        CurrentUser currentUser =
                (CurrentUser) authentication.getPrincipal();

        UserEntity admin =
                userRepository.findById(currentUser.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Администраторът не е намерен."
                                )
                        );

        ProjectEntity project =
                projectRepository.findById(projectId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Проектът не е намерен."
                                )
                        );

        try {

            String uploadDir =
                    "uploads/designs/";

            Path directory =
                    Paths.get(uploadDir);

            Files.createDirectories(directory);

            String originalFileName =
                    file.getOriginalFilename();

            String extension = "";

            if (originalFileName != null
                    && originalFileName.contains(".")) {

                extension = originalFileName
                        .substring(
                                originalFileName.lastIndexOf(".")
                        );
            }

            String storedFileName =
                    UUID.randomUUID() + extension;

            Path filePath =
                    directory.resolve(storedFileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );


            DesignFileEntity designFile =
                    new DesignFileEntity();

            designFile.setFileName(
                    originalFileName
            );

            designFile.setFilePath(
                    filePath.toString()
            );

            designFile.setContentType(
                    file.getContentType()
            );

            designFile.setFileSize(
                    file.getSize()
            );

            designFile.setProject(
                    project
            );

            designFile.setUploadedBy(
                    admin
            );


            designFileRepository.save(
                    designFile
            );


            project.setStatus(
                    ProjectStatus.DESIGN_READY
            );

            projectRepository.save(project);


        } catch (IOException e) {

            throw new RuntimeException(
                    "Грешка при качването на файла.", e);
        }
    }

}
