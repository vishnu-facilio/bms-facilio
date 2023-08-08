package com.facilio.weather.util;

import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;

@Getter
public enum ServiceType implements FacilioStringEnum {
    facilioweather ("Facilio Weather"),
    openweather ("Openweather Service"),
    weatherapi ("Weatherapi Service"),
    weatherbit ("Weatherbit Service");

    private final String description;
    ServiceType(String description) {
        this.description = description;
    }

    @Override
    public String getValue() {
        return this.description;
    }
}