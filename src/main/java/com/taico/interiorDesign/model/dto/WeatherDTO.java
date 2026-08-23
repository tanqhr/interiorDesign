package com.taico.interiorDesign.model.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherDTO {

    private String city;
    private double temperature;
    private String description;
    private double windSpeed;
}