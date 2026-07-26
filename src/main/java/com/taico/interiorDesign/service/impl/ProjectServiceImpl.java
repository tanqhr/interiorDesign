package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.enums.ProjectStatus;
import com.taico.interiorDesign.model.dto.*;
import com.taico.interiorDesign.model.entity.ImageEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.ProjectRepository;
import com.taico.interiorDesign.repositories.UserRepository;
import com.taico.interiorDesign.security.CurrentUser;
import com.taico.interiorDesign.service.FileUploadService;
import com.taico.interiorDesign.service.ImageService;
import com.taico.interiorDesign.service.ProjectService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public

class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final ImageService imageService;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, FileUploadService fileUploadService, ImageService imageService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;

        this.fileUploadService = fileUploadService;
        this.imageService = imageService;
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

        dto.setClientName(
                project.getAuthor().getFirstName()
                        + " "
                        + project.getAuthor().getLastName()
        );

        dto.setClientEmail(
                project.getAuthor().getEmail()
        );

        dto.setImages(
                project.getImages()
                        .stream()
                        .map(image -> {

                            ImageDTO imageDTO = new ImageDTO();

                            imageDTO.setId(image.getId());
                            imageDTO.setFileName(image.getFileName());
                            imageDTO.setFilePath(image.getFilePath());

                            return imageDTO;

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
}
