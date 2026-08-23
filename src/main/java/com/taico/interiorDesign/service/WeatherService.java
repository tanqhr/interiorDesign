package com.taico.interiorDesign.service;

import com.taico.interiorDesign.model.dto.WeatherDTO;

public interface WeatherService {

    WeatherDTO getCurrentWeather(String city);

    WeatherDTO getWeatherByCoordinates(double lat, double lon);
}
