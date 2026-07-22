package com.taico.interiorDesign.services;

import com.taico.interiorDesign.model.entity.ImageEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import org.springframework.stereotype.Service;
import com.taico.interiorDesign.repositories.ImageRepository;

@Service
public class ImageService {

    private final ImageRepository imageRepository;


    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void saveImage(ProjectEntity project,
                          String fileName,
                          String filePath,
                          String contentType,
                          Long fileSize) {

        ImageEntity image = new ImageEntity();

        image.setProject(project);
        image.setFileName(fileName);
        image.setFilePath(filePath);
        image.setContentType(contentType);
        image.setFileSize(fileSize);

        imageRepository.save(image);
    }

}