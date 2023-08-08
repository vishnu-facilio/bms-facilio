package com.facilio.weather.service;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.weather.util.WeatherAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.MessageFormat;

@Log4j
public class FacilioWeatherService implements WeatherService {

    private static String facilioWeatherURL = weatherServiceUrl();

    private static String weatherServiceUrl() {
        if(FacilioProperties.getEnvironment().startsWith("stage")) {
            return "https://stage-weather.facilio.in/api/v1/weather";
        }
        if(FacilioProperties.isDevelopment()){
            return "http://localhost:8081/api/v1/weather";
        }
        return "https://weather.facilio.com/api/v1/weather";
    }

    public static String getStationURL(double lat, double lng) {
        String url ="{0}/stationcode?lat={1}&lng={2}";
        url = MessageFormat.format(url, facilioWeatherURL, lat, lng);
        LOGGER.log(Level.INFO, "Weather station url is : " + url);
        return  url;
    }

    private String getWeatherURL(String stationCode, Long time, boolean doForecast, boolean dailyForecast) {
        String url ="{0}/data?stationCode={1}&hourlyForecast={2}&dailyForecast={3}";
        url = MessageFormat.format(url, facilioWeatherURL, stationCode, doForecast, dailyForecast);
        if(time != null) {
            url = url + "&ttime=" + time;
        }
        LOGGER.log(Level.INFO, "Weather url is : " + url);
        return url;
    }

    @Override
    public JSONObject getStationCode(Double lat, Double lng) throws Exception {
        String weatherURL = getStationURL(lat, lng);
		return hitExternalWeatherService(weatherURL);
    }

    @Override
    public JSONObject getWeatherData(V3WeatherStationContext weatherStation, Long time, boolean doForecast, boolean dailyForecast) throws Exception {
        String weatherURL = getWeatherURL(weatherStation.getStationCode(), time, doForecast, dailyForecast);
		return hitExternalWeatherService(weatherURL);
    }

    @Override
    public JSONObject getWeatherData(double lat, double lng, Long time) throws Exception {
        String weatherURL = getWeatherURL(lat, lng, time, true, WeatherAPI.isStartOfTheDay());
        return hitExternalWeatherService(weatherURL);
    }

    private static JSONObject hitExternalWeatherService(String url) throws Exception {
        String response = WeatherAPI.doGet(url);
        if (StringUtils.isEmpty(response)) {
            LOGGER.log(Level.INFO, "The response is null from the weather server");
            return null;
        }
        JSONObject weatherData;
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonResponse = (JSONObject) parser.parse(response);
            weatherData = (JSONObject) jsonResponse.get("data");
        } catch (Exception e) {
            throw new Exception(response, e);
        }
        return weatherData;
    }

    private static String getWeatherURL(double lat, double lng, Long time, boolean doForecast, boolean dailyForecast) {
        String url ="{0}/data?lat={1}&lng={2}&hourlyForecas={3}&dailyForecast={4}";
        url = MessageFormat.format(url, facilioWeatherURL, lat, lng, doForecast, dailyForecast);
        if(time != null) {
            url = url + "&ttime=" + time;
        }

        LOGGER.log(Level.INFO, "Weather url is : " + url);
        return url;
    }

}
