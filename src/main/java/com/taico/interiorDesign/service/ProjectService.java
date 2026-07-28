package com.taico.interiorDesign.service;

import com.taico.interiorDesign.model.dto.ProjectCreateDTO;
import com.taico.interiorDesign.model.dto.ProjectDetailsDTO;
import com.taico.interiorDesign.model.dto.ProjectUpdateDTO;
import com.taico.interiorDesign.model.dto.ProjectViewDTO;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProjectService {

    List<ProjectViewDTO> getAllProjects();


    ProjectDetailsDTO getProjectById(Long id);

    void createProject(
            ProjectCreateDTO dto,
            List<MultipartFile> images,
            Authentication authentication
    ) throws IOException;

    List<ProjectEntity> findAll();

    void updateProject(Long id, ProjectUpdateDTO dto);

    ProjectDetailsDTO getProjectDetails(Long id);


    List<ProjectEntity> findByAuthor(Long userId);


    ProjectEntity findById(Long id);

    void payProject(Long id, String paymentMethod);

    void uploadDesignFile(Long projectId, MultipartFile file, Authentication authentication);
}
