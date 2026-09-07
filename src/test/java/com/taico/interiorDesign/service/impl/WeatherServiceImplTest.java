package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.config.WeatherProperties;
import com.taico.interiorDesign.model.dto.WeatherDTO;
import com.taico.interiorDesign.model.dto.weather.WeatherApiResponse;
import com.taico.interiorDesign.service.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private WeatherProperties weatherProperties;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private RestClient weatherRestClient;

    @Mock
    private RestClient geocodingRestClient;

    @Mock
    private RestClient.RequestHeadersUriSpec request;

    @Mock
    private RestClient.RequestHeadersSpec headers;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() {

        when(weatherProperties.getApiUrl())
                .thenReturn("https://api.openweathermap.org/data/2.5/weather");

        weatherService = new WeatherServiceImpl(
                weatherProperties,
                geocodingService
        );

        ReflectionTestUtils.setField(
                weatherService,
                "weatherRestClient",
                weatherRestClient
        );

        ReflectionTestUtils.setField(
                weatherService,
                "geocodingRestClient",
                geocodingRestClient
        );
    }

    @Test
    void getCurrentWeather_shouldReturnWeatherDto() {

        // Arrange
        when(weatherRestClient.get())
                .thenReturn(request);

        when(request.uri(any(java.util.function.Function.class)))
                .thenReturn(headers);

        when(headers.retrieve())
                .thenReturn(responseSpec);

        WeatherApiResponse response =
                createWeatherResponse(
                        "Burgas",
                        25.5,
                        "ясно небе",
                        4.2
                );

        when(responseSpec.body(WeatherApiResponse.class))
                .thenReturn(response);

        // Act
        WeatherDTO result =
                weatherService.getCurrentWeather("Burgas");

        // Assert
        assertNotNull(result);
        assertEquals("Burgas", result.getCity());
        assertEquals(25.5, result.getTemperature());
        assertEquals("ясно небе", result.getDescription());
        assertEquals(4.2, result.getWindSpeed());

        verify(weatherRestClient).get();
        verify(responseSpec).body(WeatherApiResponse.class);
    }

    @Test
    void getWeatherByCoordinates_shouldReturnWeatherDtoWithBulgarianCityName() {

        // Arrange
        double lat = 42.5048;
        double lon = 27.4626;

        when(weatherRestClient.get())
                .thenReturn(request);

        when(request.uri(any(java.util.function.Function.class)))
                .thenReturn(headers);

        when(headers.retrieve())
                .thenReturn(responseSpec);

        WeatherApiResponse response =
                createWeatherResponse(
                        "Burgas",
                        26.0,
                        "слънчево",
                        5.5
                );

        when(responseSpec.body(WeatherApiResponse.class))
                .thenReturn(response);

        when(geocodingService.getBulgarianCityName(
                "Burgas",
                lat,
                lon
        )).thenReturn("Бургас");

        // Act
        WeatherDTO result =
                weatherService.getWeatherByCoordinates(
                        lat,
                        lon
                );

        // Assert
        assertNotNull(result);
        assertEquals("Бургас", result.getCity());
        assertEquals(26.0, result.getTemperature());
        assertEquals("слънчево", result.getDescription());
        assertEquals(5.5, result.getWindSpeed());

        verify(weatherRestClient).get();

        verify(geocodingService)
                .getBulgarianCityName(
                        "Burgas",
                        lat,
                        lon
                );

        verify(responseSpec)
                .body(WeatherApiResponse.class);
    }

    @Test
    void getWeatherByCoordinates_shouldUseOriginalCityName_whenGeocodingReturnsOriginalName() {

        // Arrange
        double lat = 42.5048;
        double lon = 27.4626;

        when(weatherRestClient.get())
                .thenReturn(request);

        when(request.uri(any(java.util.function.Function.class)))
                .thenReturn(headers);

        when(headers.retrieve())
                .thenReturn(responseSpec);

        WeatherApiResponse response =
                createWeatherResponse(
                        "Burgas",
                        24.0,
                        "облачно",
                        3.0
                );

        when(responseSpec.body(WeatherApiResponse.class))
                .thenReturn(response);

        when(geocodingService.getBulgarianCityName(
                "Burgas",
                lat,
                lon
        )).thenReturn("Burgas");

        // Act
        WeatherDTO result =
                weatherService.getWeatherByCoordinates(
                        lat,
                        lon
                );

        // Assert
        assertNotNull(result);
        assertEquals("Burgas", result.getCity());
        assertEquals(24.0, result.getTemperature());
        assertEquals("облачно", result.getDescription());
        assertEquals(3.0, result.getWindSpeed());

        verify(weatherRestClient).get();

        verify(geocodingService)
                .getBulgarianCityName(
                        "Burgas",
                        lat,
                        lon
                );

        verify(responseSpec)
                .body(WeatherApiResponse.class);
    }

    @Test
    void getCurrentWeather_shouldPropagateException_whenWeatherApiFails() {

        // Arrange
        when(weatherRestClient.get())
                .thenReturn(request);

        when(request.uri(any(java.util.function.Function.class)))
                .thenThrow(new RuntimeException("API error"));

        // Act & Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> weatherService.getCurrentWeather("Burgas")
                );

        assertEquals(
                "API error",
                exception.getMessage()
        );

        verify(weatherRestClient).get();

        verify(request)
                .uri(any(java.util.function.Function.class));

        verifyNoInteractions(responseSpec);
    }

    private WeatherApiResponse createWeatherResponse(
            String city,
            double temperature,
            String description,
            double windSpeed) {

        WeatherApiResponse response =
                mock(WeatherApiResponse.class);

        WeatherApiResponse.Main main =
                mock(WeatherApiResponse.Main.class);

        WeatherApiResponse.Weather weather =
                mock(WeatherApiResponse.Weather.class);

        WeatherApiResponse.Wind wind =
                mock(WeatherApiResponse.Wind.class);

        when(response.getName())
                .thenReturn(city);

        when(response.getMain())
                .thenReturn(main);

        when(main.getTemp())
                .thenReturn(temperature);

        when(response.getWeather())
                .thenReturn(List.of(weather));

        when(weather.getDescription())
                .thenReturn(description);

        when(response.getWind())
                .thenReturn(wind);

        when(wind.getSpeed())
                .thenReturn(windSpeed);

        return response;
    }
}