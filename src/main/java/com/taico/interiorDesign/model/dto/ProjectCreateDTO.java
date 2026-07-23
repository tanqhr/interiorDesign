package com.taico.interiorDesign.model.dto;

import com.taico.interiorDesign.enums.RoomType;
import com.taico.interiorDesign.enums.ServiceType;
import jakarta.validation.constraints.DecimalMin;
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

    @NotNull(message = "Въведете площ.")
    @DecimalMin(value = "1.0", message = "Площта трябва да е поне 1 кв.м.")
    private BigDecimal area;


    private BigDecimal budget;

    @NotNull(message = "Изберете услуга.")
    private ServiceType serviceType;

    private List<MultipartFile> images;

}
