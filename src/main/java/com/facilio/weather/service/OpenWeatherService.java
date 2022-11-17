package com.facilio.weather.service;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.weather.util.WeatherServiceUtils;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class OpenWeatherService implements WeatherService {

    /*
    Reference link - https://openweathermap.org/weather-conditions
     */
    private static final String WEATHER_CONDITION_PATH = "conf/weather/openweather.yml";

    public static Map<String, String> WEATHER_CONDITIONS = Collections.unmodifiableMap(initWeatherConditions());

    public static Map<String, String> initWeatherConditions() {
        Yaml yaml = new Yaml();
        Map<String, String> weatherCondnMap = new HashMap<>();
        try (InputStream inputStream = OpenWeatherService.class.getClassLoader().getResourceAsStream(WEATHER_CONDITION_PATH)) {
            Map<String, Object> json = yaml.load(inputStream);
            List<Map<String, Object>> weatherConditions = (List<Map<String, Object>>) json.get("weatherConditionInfo");
            for(Map<String, Object> row : weatherConditions) {
                String id = String.valueOf(row.get("id"));
                for(String icon : ((String)row.get("icon")).split(",")) {
                    weatherCondnMap.put(id+"_"+icon, (String)row.get("facilio-icon"));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error occured while loading weatherConditions conf file. "+WEATHER_CONDITION_PATH, e);
        }
        return weatherCondnMap;
    }

    private Object getAPIKey() {
        return FacilioProperties.getConfig("weather.key");
    }

    public String getReverseGeoURL(double lat, double lng) {
        String reverseGeoURL = "http://api.openweathermap.org/geo/1.0/reverse?lat={0}&lon={1}&appid={2}";
        return MessageFormat.format(reverseGeoURL, lat, lng, getAPIKey());
    }

    public String getWeatherURL(double lat, double lng) {
        String currentWeatherURL = "https://api.openweathermap.org/data/2.5/weather?lat={0}&lon={1}&appid={2}&units=metric";
        return MessageFormat.format(currentWeatherURL, lat, lng, getAPIKey());
    }

    public String getForecastURL(double lat, double lng) {
        String forecastUrl = "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat={0}&lon={1}&appid={2}&units=metric";
        return MessageFormat.format(forecastUrl, lat, lng, getAPIKey());
    }


    @Override
    public JSONObject getStationCode(Double lat, Double lng) throws Exception {
        String reverseGeoURL = getReverseGeoURL(lat, lng);
        String response = hitApi(reverseGeoURL, "The station code fetching response is null for openweather service");
        try {
            JSONParser parser = new JSONParser();
            JSONArray responseArr = (JSONArray) parser.parse(response);
            if(responseArr.size()==0) {
                throw new Exception("Invalid lat, lng given");
            }
            JSONObject weatherData = (JSONObject) responseArr.get(0);

            JSONObject stationData = new JSONObject();
            stationData.put("lat", WeatherServiceUtils.convert((Double) weatherData.get("lat")));
            stationData.put("lng", WeatherServiceUtils.convert((Double) weatherData.get("lon")));
            stationData.put("identifier", weatherData.get("name"));
            stationData.put("serviceName", "openweather");
            stationData.put("country", weatherData.getOrDefault("country", "DF"));
            stationData.put("stationCode", MessageFormat.format("{0}_{1}_{2}", stationData.get("country"),
                            stationData.get("lat"), stationData.get("lng")));
            LOGGER.info("ReverseGeo api hit on OPENWEATHER done for : "+lat+" & "+lng);
            return stationData;
        } catch (Exception e) {
            LOGGER.error("WEATHER_RESPONSE :: "+response);
            LOGGER.error("WEATHER_URL :: "+reverseGeoURL);
            throw new Exception("Error while parsing weather station data", e);
        }
    }

    @Override
    public JSONObject getWeatherData(double lat, double lng, Long time) throws Exception {
        return getWeatherData(lat, lng, true, "-1");
    }

    @Override
    public JSONObject getWeatherData(V3WeatherStationContext weatherStation, Long ttime, boolean doForecast) throws Exception {
        Double lat = weatherStation.getLat();
        Double lng = weatherStation.getLng();
        return getWeatherData(lat, lng, doForecast, weatherStation.getStationCode());
    }

    private JSONObject getWeatherData(Double lat, Double lng, boolean doForecast, String stationCode) throws Exception {
        String url = getWeatherURL(lat, lng);
        String response = hitApi(url, "The weather data fetching response is null in openweather service");
        try {
            JSONObject result = new JSONObject();

            JSONParser parser = new JSONParser();
            JSONObject weatherResponse = (JSONObject) parser.parse(response);
            JSONObject currentWeather = parse(weatherResponse);
            Long time = (Long) currentWeather.get("ttime");
            Long predictedTime = WeatherServiceUtils.roundOfTime(time);
            currentWeather.put("predictedTime", predictedTime);
            currentWeather.put("stationCode", stationCode);
            currentWeather.put("time", predictedTime/1000);

            result.put("currently", currentWeather);

            if(doForecast) {
                String forecastUrl = getForecastURL(lat, lng);
                String forecastResponse = hitApi(forecastUrl, "The forecast weather data fetching response is null from the weather server");
                JSONArray hourlyData = (JSONArray) ((JSONObject) parser.parse(forecastResponse)).get("list");
                JSONArray hourlyList = new JSONArray();
                for (Object data : hourlyData) {
                    Map hourlyMap = parse((JSONObject) data);
                    time =  (Long) hourlyMap.get("ttime");
                    hourlyMap.put("predictedTime", predictedTime);
                    hourlyMap.put("stationCode", stationCode);
                    hourlyMap.put("time", time/1000);
                    hourlyList.add(hourlyMap);
                }
                JSONObject hourlyJson = new JSONObject();
                hourlyJson.put("data", hourlyList);
                result.put("hourly", hourlyJson);
            }

            LOGGER.info("WeatherData api hit on OPENWEATHER done for stationCode="+stationCode+ ", ttime="+ time);
            result.put("serviceName", "openweather");
            return result;
        } catch (Exception e) {
            LOGGER.error("WEATHER_RESPONSE :: "+response);
            LOGGER.error("WEATHER_URL :: "+url);
            throw new Exception("Weather data parsing failed with response", e);
        }
    }

    public JSONObject parse(JSONObject data) {
        JSONObject weatherData = new JSONObject();

        JSONArray weatherArr = (JSONArray) data.get("weather");
        JSONObject weatherObj = (JSONObject) weatherArr.get(0);

        weatherData.put("summary", weatherObj.get("description"));
        weatherData.put("icon", getIcon(weatherObj));
        weatherData.put("iconMeta", weatherObj);

        JSONObject mainData = (JSONObject) data.get("main");
        weatherData.put("temperature", mainData.get("temp"));
        weatherData.put("humidity", mainData.get("humidity"));
//        weatherData.put("dewPoint", data.get("dew_point"));
        weatherData.put("pressure", mainData.get("pressure"));
        weatherData.put("apparentTemperature", mainData.get("feels_like"));

        JSONObject windData = (JSONObject) data.get("wind");
        weatherData.put("windSpeed", windData.get("speed"));
        weatherData.put("windDegree", windData.get("deg"));
        weatherData.put("windGust", windData.get("gust"));
        weatherData.put("windBearing", windData.get("deg"));

        weatherData.put("cloudCover", ((JSONObject)data.get("clouds")).get("all"));
//        weatherData.put("uvIndex", data.get("uvi"));
        weatherData.put("visibility", data.get("visibility"));
        if(data.containsKey("rain")) {
            weatherData.put("precipType", "rain");
            weatherData.put("precipIntensity", ((JSONObject) data.get("rain")).get("1h"));
        }
        if(data.containsKey("snow")) {
            weatherData.put("precipType", "snow");
            weatherData.put("precipIntensity", ((JSONObject) data.get("snow")).get("1h"));
        }
        Object ttime = WeatherServiceUtils.getTimeinMillis(data.get("dt"));
        weatherData.put("actualTtime", ttime);
        weatherData.put("ttime", ttime);
        return weatherData;
    }

    private String hitApi(String url, String errMsg) throws Exception {
        HttpURLConnection connection = WeatherUtil.getHttpURLConnection(url);
        String response = WeatherUtil.getResponse(connection);
        if (response == null || response.equals("[]")) {
            throw new Exception(errMsg+ " :: "+url);
        }
        if(response.contains("\"cod\":429")) {  //limit reached issue
            LOGGER.error("API limit exhausted.");
        }
        return response;
    }

    private Object getIcon(JSONObject weatherObj) {
        if(WEATHER_CONDITIONS.isEmpty()) {
            return null;
        }
        String key = String.valueOf(weatherObj.get("id"))+"_"+(String)weatherObj.get("icon");
        return WEATHER_CONDITIONS.get(key);
    }
}
