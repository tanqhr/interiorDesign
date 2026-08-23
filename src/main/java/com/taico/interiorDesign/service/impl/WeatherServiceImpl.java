package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.config.WeatherProperties;
import com.taico.interiorDesign.model.dto.WeatherDTO;
import com.taico.interiorDesign.model.dto.weather.GeocodingResponse;
import com.taico.interiorDesign.model.dto.weather.WeatherApiResponse;
import com.taico.interiorDesign.service.GeocodingService;
import com.taico.interiorDesign.service.WeatherService;
import jakarta.persistence.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final WeatherProperties weatherProperties;
    private final RestClient weatherRestClient;
    private final RestClient geocodingRestClient;
    private final GeocodingService geocodingService;

    public WeatherServiceImpl(WeatherProperties weatherProperties, GeocodingService geocodingService) {

        this.weatherProperties = weatherProperties;

        this.weatherRestClient = RestClient.builder()
                .baseUrl(weatherProperties.getApiUrl())
                .build();
        this.geocodingService = geocodingService;

        this.geocodingRestClient = RestClient.builder()
                .baseUrl("https://api.openweathermap.org")
                .build();
    }

    @Override
    public WeatherDTO getCurrentWeather(String city) {

        WeatherApiResponse response = weatherRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", city)
                        .queryParam("appid", weatherProperties.getApiKey())
                        .queryParam("units", "metric")
                        .queryParam("lang", "bg")
                        .build())
                .retrieve()
                .body(WeatherApiResponse.class);

        WeatherDTO dto = new WeatherDTO();

        dto.setCity(response.getName());
        dto.setTemperature(response.getMain().getTemp());
        dto.setDescription(
                response.getWeather().get(0).getDescription()
        );
        dto.setWindSpeed(response.getWind().getSpeed());

        return dto;
    }

    @Override
    public WeatherDTO getWeatherByCoordinates(double lat, double lon) {

        WeatherApiResponse response = weatherRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", weatherProperties.getApiKey())
                        .queryParam("units", "metric")
                        .queryParam("lang", "bg")
                        .build())
                .retrieve()
                .body(WeatherApiResponse.class);

        String cityName = geocodingService.getBulgarianCityName(
                response.getName(),
                lat,
                lon
        );

        WeatherDTO dto = new WeatherDTO();

        dto.setCity(cityName);
        dto.setTemperature(response.getMain().getTemp());
        dto.setDescription(
                response.getWeather().get(0).getDescription()
        );
        dto.setWindSpeed(response.getWind().getSpeed());

        return dto;
    }

    private String getBulgarianCityName(
            String originalCity,
            double lat,
            double lon) {

        try {

            GeocodingResponse[] response = geocodingRestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geo/1.0/reverse")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("limit", 1)
                            .queryParam("appid", weatherProperties.getApiKey())
                            .build())
                    .retrieve()
                    .body(GeocodingResponse[].class);

            if (response != null
                    && response.length > 0
                    && response[0].getLocalNames() != null) {

                String bulgarianName =
                        response[0].getLocalNames().get("bg");

                if (bulgarianName != null
                        && !bulgarianName.isBlank()) {

                    return bulgarianName;
                }
            }

        } catch (Exception e) {

            // Ако Geocoding API не върне превод,
            // използваме оригиналното име.
        }

        return originalCity;
    }
}