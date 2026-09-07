package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.config.WeatherProperties;
import com.taico.interiorDesign.model.dto.weather.GeocodingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceImplTest {

    @Mock
    private WeatherProperties weatherProperties;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec request;

    @Mock
    private RestClient.RequestHeadersSpec headers;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private GeocodingServiceImpl geocodingService;

    @BeforeEach
    void setUp() {

        geocodingService =
                new GeocodingServiceImpl(weatherProperties);

        // Заменяме реалния RestClient с mock
        ReflectionTestUtils.setField(
                geocodingService,
                "geocodingRestClient",
                restClient
        );

        // Настройваме RestClient chain-а
        when(restClient.get())
                .thenReturn(request);

        when(request.uri(any(java.util.function.Function.class)))
                .thenReturn(headers);

        when(headers.retrieve())
                .thenReturn(responseSpec);
    }

    @Test
    void getBulgarianCityName_shouldReturnBulgarianName_whenApiReturnsBgName() {

        // Arrange
        GeocodingResponse response =
                mock(GeocodingResponse.class);

        Map<String, String> localNames =
                new HashMap<>();

        localNames.put("bg", "Бургас");

        when(response.getLocalNames())
                .thenReturn(localNames);

        when(responseSpec.body(GeocodingResponse[].class))
                .thenReturn(new GeocodingResponse[]{response});

        // Act
        String result =
                geocodingService.getBulgarianCityName(
                        "Burgas",
                        42.5048,
                        27.4626
                );

        // Assert
        assertEquals("Бургас", result);

        verify(restClient).get();
        verify(responseSpec).body(GeocodingResponse[].class);
    }

    @Test
    void getBulgarianCityName_shouldReturnOriginalCity_whenResponseIsNull() {

        // Arrange
        when(responseSpec.body(GeocodingResponse[].class))
                .thenReturn(null);

        // Act
        String result =
                geocodingService.getBulgarianCityName(
                        "Burgas",
                        42.5048,
                        27.4626
                );

        // Assert
        assertEquals("Burgas", result);
    }

    @Test
    void getBulgarianCityName_shouldReturnOriginalCity_whenResponseIsEmpty() {

        // Arrange
        when(responseSpec.body(GeocodingResponse[].class))
                .thenReturn(new GeocodingResponse[0]);

        // Act
        String result =
                geocodingService.getBulgarianCityName(
                        "Burgas",
                        42.5048,
                        27.4626
                );

        // Assert
        assertEquals("Burgas", result);
    }

    @Test
    void getBulgarianCityName_shouldReturnOriginalCity_whenLocalNamesAreNull() {

        // Arrange
        GeocodingResponse response =
                mock(GeocodingResponse.class);

        when(response.getLocalNames())
                .thenReturn(null);

        when(responseSpec.body(GeocodingResponse[].class))
                .thenReturn(new GeocodingResponse[]{response});

        // Act
        String result =
                geocodingService.getBulgarianCityName(
                        "Burgas",
                        42.5048,
                        27.4626
                );

        // Assert
        assertEquals("Burgas", result);
    }

    @Test
    void getBulgarianCityName_shouldReturnOriginalCity_whenBgNameDoesNotExist() {

        // Arrange
        GeocodingResponse response =
                mock(GeocodingResponse.class);

        Map<String, String> localNames =
                new HashMap<>();

        localNames.put("en", "Burgas");

        when(response.getLocalNames())
                .thenReturn(localNames);

        when(responseSpec.body(GeocodingResponse[].class))
                .thenReturn(new GeocodingResponse[]{response});

        // Act
        String result =
                geocodingService.getBulgarianCityName(
                        "Burgas",
                        42.5048,
                        27.4626
                );

        // Assert
        assertEquals("Burgas", result);
    }

    @Test
    void getBulgarianCityName_shouldReturnOriginalCity_whenApiThrowsException() {

        // Arrange
        when(headers.retrieve())
                .thenThrow(new RuntimeException("API error"));

        // Act
        String result =
                geocodingService.getBulgarianCityName(
                        "Burgas",
                        42.5048,
                        27.4626
                );

        // Assert
        assertEquals("Burgas", result);
    }
}