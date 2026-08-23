package com.taico.interiorDesign.service.impl;


import com.taico.interiorDesign.config.WeatherProperties;
import com.taico.interiorDesign.model.dto.weather.GeocodingResponse;
import com.taico.interiorDesign.service.GeocodingService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeocodingServiceImpl implements GeocodingService {

    private final WeatherProperties weatherProperties;
    private final RestClient geocodingRestClient;

    public GeocodingServiceImpl(
            WeatherProperties weatherProperties) {

        this.weatherProperties = weatherProperties;

        this.geocodingRestClient = RestClient.builder()
                .baseUrl("https://api.openweathermap.org")
                .build();
    }

    @Override
    @Cacheable(value = "cityNames",
                    key = "T(java.lang.Math).round(#lat * 100) + ',' + T(java.lang.Math).round(#lon * 100)")
    public String getBulgarianCityName(
            String originalCity,
            double lat,
            double lon) {
        try {

            GeocodingResponse[] response =
                    geocodingRestClient
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/geo/1.0/reverse")
                                    .queryParam("lat", lat)
                                    .queryParam("lon", lon)
                                    .queryParam("limit", 1)
                                    .queryParam(
                                            "appid",
                                            weatherProperties.getApiKey()
                                    )
                                    .build())
                            .retrieve()
                            .body(GeocodingResponse[].class);

            if (response != null
                    && response.length > 0
                    && response[0].getLocalNames() != null) {

                String bulgarianName =
                        response[0]
                                .getLocalNames()
                                .get("bg");

                if (bulgarianName != null
                        && !bulgarianName.isBlank()) {

                    return bulgarianName;
                }
            }

        } catch (Exception e) {
            // fallback към оригиналното име
        }

        return originalCity;
    }
}