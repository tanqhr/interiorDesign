package com.taico.interiorDesign.model.dto;

import com.taico.interiorDesign.enums.ProjectStatus;
import com.taico.interiorDesign.enums.RoomType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectViewDTO {

    private Long id;

    private String title;

    private String author;

    private RoomType roomType;

    private ProjectStatus status;
}
