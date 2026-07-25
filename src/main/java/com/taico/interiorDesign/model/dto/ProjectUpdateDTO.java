package com.taico.interiorDesign.model.dto;


import com.taico.interiorDesign.enums.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class ProjectUpdateDTO {


    /*
     * Цена на услугата,
     * която администраторът определя
     */
    private BigDecimal price;


    /*
     * Статус на проекта
     */
    private ProjectStatus status;


    /*
     * Администраторска бележка
     */
    private String adminNote;

}
