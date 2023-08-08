package com.facilio.weather.service;

import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import org.json.simple.JSONObject;

public interface WeatherService {

    JSONObject getStationCode(Double lat, Double lng) throws Exception;

    JSONObject getWeatherData(V3WeatherStationContext weatherStation, Long time, boolean doForecast, boolean dailyForecast) throws Exception;

    @Deprecated
    JSONObject getWeatherData(double lat, double lng, Long time) throws Exception; // temp func, to support old Weather_Reading

}
