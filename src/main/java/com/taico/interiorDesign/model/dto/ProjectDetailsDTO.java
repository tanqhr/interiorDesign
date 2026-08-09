package com.taico.interiorDesign.model.dto;


import com.taico.interiorDesign.enums.ProjectStatus;
import com.taico.interiorDesign.enums.RoomType;
import com.taico.interiorDesign.enums.ServiceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ProjectDetailsDTO {


    private Long id;


    // ======================
    // PROJECT INFO
    // ======================

    private String title;

    private String description;


    private RoomType roomType;

    private ServiceType serviceType;


    private BigDecimal budget;


    // ======================
    // CLIENT INFO
    // ======================

    private String clientName;

    private String clientEmail;


    // ======================
    // ADMIN INFO
    // ======================

    private String adminNote;

    private String clientFeedback;

    private BigDecimal price;

    private ProjectStatus status;

    private BigDecimal area;

    private List<ImageDTO> images = new ArrayList<>();

    private List<DesignFileDTO> designs = new ArrayList<>();

    private String author;

    // ======================
    // IMAGES
    // ======================

    //private List<String> images;


    // ======================
    // DATES
    // ======================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
