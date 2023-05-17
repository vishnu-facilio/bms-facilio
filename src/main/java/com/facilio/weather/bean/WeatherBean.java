package com.facilio.weather.bean;

import org.json.simple.JSONObject;

public interface WeatherBean {

    void addOrUpdateWeatherStationData(JSONObject content);

}
