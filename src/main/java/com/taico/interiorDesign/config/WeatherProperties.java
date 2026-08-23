package com.taico.interiorDesign.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "weather.service")
public class WeatherProperties {

    private boolean mocked = false;

    private String apiKey;

    private String apiUrl;
}