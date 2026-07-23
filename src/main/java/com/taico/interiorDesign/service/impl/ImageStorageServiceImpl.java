package com.taico.interiorDesign.service.impl;



import com.taico.interiorDesign.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private final Path uploadPath;

    public ImageStorageServiceImpl(
            @Value("${app.upload-dir}") String uploadDir) {

        this.uploadPath = Paths.get(uploadDir);

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory.", e);
        }
    }

    @Override
    public String store(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файлът е празен.");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Файлът трябва да бъде изображение.");
        }

        String originalFilename =
                StringUtils.cleanPath(file.getOriginalFilename());

        String storedFileName =
                UUID.randomUUID() + "_" + originalFilename;

        Path destination =
                uploadPath.resolve(storedFileName);

        try {

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Грешка при записването на изображението.",
                    e
            );
        }

        return "uploads/" + storedFileName;
    }
}