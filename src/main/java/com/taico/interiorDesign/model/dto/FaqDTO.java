package com.taico.interiorDesign.model.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaqDTO {

    private Long id;
    @NotBlank(message = "Въпросът е задължителен")
    private String question;

    @NotBlank(message = "Отговорът е задължителен")
    private String answer;
    private String category;
    private boolean isActive;

}