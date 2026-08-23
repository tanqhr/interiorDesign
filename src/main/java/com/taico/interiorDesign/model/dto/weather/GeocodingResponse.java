package com.taico.interiorDesign.model.dto.weather;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class GeocodingResponse {

    private String name;

    @JsonProperty("local_names")
    private Map<String, String> localNames;


}