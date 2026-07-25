package com.taico.interiorDesign.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "uploads/";

    public String uploadFile(MultipartFile file, Long projectId) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // 1. Create project folder
        String projectFolder = UPLOAD_DIR + "projects/" + projectId + "/";
        File dir = new File(projectFolder);

        if (!dir.exists()) {
            dir.mkdirs();
        }


        // 2. Generate unique file name
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        String fileName = UUID.randomUUID() + "." + extension;

        // 3. Full path
        Path filePath = Paths.get(projectFolder, fileName);

//        // 4. Save file
//        Files.write(filePath, file.getBytes());


        try {
            Files.write(filePath, file.getBytes());
            return "uploads/projects/" + projectId + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
//        // 5. Return relative path for DB
//        return "uploads/projects/" + projectId + "/" + fileName;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
