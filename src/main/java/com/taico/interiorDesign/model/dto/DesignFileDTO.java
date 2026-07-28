package com.taico.interiorDesign.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DesignFileDTO {

    private Long id;

    private String fileName;

    private String filePath;

    private String contentType;

    private Long fileSize;

    private LocalDateTime uploadedAt;
}
