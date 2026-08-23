package com.taico.interiorDesign.model.dto.weather;



import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeatherApiResponse {

    private String name;
    private Main main;
    private List<Weather> weather;
    private Wind wind;

    @Getter
    @Setter
    public static class Main {
        private double temp;
    }

    @Getter
    @Setter
    public static class Weather {
        private String description;
    }

    @Getter
    @Setter
    public static class Wind {
        private double speed;
    }
}
