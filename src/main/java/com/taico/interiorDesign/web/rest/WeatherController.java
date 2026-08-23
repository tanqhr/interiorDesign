package com.taico.interiorDesign.web.rest;

import com.taico.interiorDesign.model.dto.WeatherDTO;
import com.taico.interiorDesign.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public WeatherDTO getWeather(
            @RequestParam String city) {

        return weatherService.getCurrentWeather(city);
    }


    @GetMapping("/current")
    public WeatherDTO getCurrentWeather(
            @RequestParam double lat,
            @RequestParam double lon) {

        return weatherService.getWeatherByCoordinates(lat, lon);
    }
}
