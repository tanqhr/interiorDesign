package com.taico.interiorDesign.service;



public interface GeocodingService {

    String getBulgarianCityName(
            String originalCity,
            double lat,
            double lon
    );
}
