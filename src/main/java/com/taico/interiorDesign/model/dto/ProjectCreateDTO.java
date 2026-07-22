package com.taico.interiorDesign.model.dto;

import com.taico.interiorDesign.enums.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProjectCreateDTO {

    @NotBlank(message = "Името на проекта е задължително")
    private String title;


    @NotBlank(message = "Описание е задължително")
    private String description;


    @NotNull(message = "Избери тип помещение")
    private RoomType roomType;


    private BigDecimal budget;


    private List<MultipartFile> images;

}
