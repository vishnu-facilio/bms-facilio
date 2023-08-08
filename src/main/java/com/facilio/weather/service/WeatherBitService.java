package com.facilio.weather.service;

import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.bmsconsoleV3.context.weather.V3WeatherStationContext;
import com.facilio.weather.util.WeatherAPI;
import com.facilio.weather.util.WeatherServiceUtils;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class WeatherBitService implements WeatherService {

    private double lat;
    private double lng;
    private String apiKey;

    private static final String WEATHER_CONDITION_PATH = "conf/weather/weatherbit.yml";
    public static Map<String, String> WEATHER_CONDITIONS = Collections.unmodifiableMap(initWeatherConditions());

    public static Map<String, String> initWeatherConditions() {
        Yaml yaml = new Yaml();
        Map<String, String> weatherCondnMap = new HashMap<>();
        try (InputStream inputStream = WeatherBitService.class.getClassLoader().getResourceAsStream(WEATHER_CONDITION_PATH)) {
            Map<String, Object> json = yaml.load(inputStream);
            List<Map<String, Object>> weatherConditions = (List<Map<String, Object>>) json.get("weatherConditionInfo");
            for(Map<String, Object> row : weatherConditions) {
                weatherCondnMap.put(String.valueOf(row.get("code")), (String)row.get("facilio-icon"));
            }
        } catch (Exception e) {
            LOGGER.error("Error occured while loading weatherConditions conf file. "+WEATHER_CONDITION_PATH, e);
        }
        return weatherCondnMap;
    }

    public WeatherBitService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getReverseGeoURL() {
        return getWeatherURL();
    }

    public String getWeatherURL() {
        String currentWeatherURL = "https://api.weatherbit.io/v2.0/current?lat={0}&lon={1}&key={2}";
        return MessageFormat.format(currentWeatherURL, lat, lng, apiKey);
    }

    public String getForecastURL() {
        String forecastUrl = "https://api.weatherbit.io/v2.0/forecast/hourly?lat={0}&lon={1}&key={2}&hours=240";
        return MessageFormat.format(forecastUrl, lat, lng, apiKey);
    }

    public String getDailyForecastURL() {
        String url = "https://api.weatherbit.io/v2.0/forecast/daily?lat={0}&lon={1}&key={2}&hours=240";
        return MessageFormat.format(url, lat, lng, apiKey);
    }

    public String getAirQualityURL() {
        String url = "https://api.weatherbit.io/v2.0/current/airquality?lat={0}&lon={1}&key={2}";
        return MessageFormat.format(url, lat, lng, apiKey);
    }

    public String getAirQualityForecastURL() {
        String url = "https://api.weatherbit.io/v2.0/forecast/airquality?lat={0}&lon={1}&key={2}";
        return MessageFormat.format(url, lat, lng, apiKey);
    }

    @Override
    public JSONObject getStationCode(Double lat, Double lng) throws Exception {
        this.lat = lat;
        this.lng = lng;
        String reverseGeoURL = getReverseGeoURL();
        String response = hitApi(reverseGeoURL, "The station code fetching response is null from the weather server");
        try {
            JSONParser parser = new JSONParser();
            JSONArray responseArr = (JSONArray) ((JSONObject) parser.parse(response)).get("data");

            if(responseArr.size()==0) {
                throw new Exception("Invalid lat, lng given");
            }

            JSONObject weatherData = (JSONObject) responseArr.get(0);

            JSONObject stationData = new JSONObject();
            stationData.put("lat", WeatherServiceUtils.convert(Double.parseDouble(weatherData.get("lat").toString())));
            stationData.put("lng", WeatherServiceUtils.convert(Double.parseDouble(weatherData.get("lon").toString())));
            stationData.put("identifier", weatherData.get("city_name"));
            stationData.put("serviceName", "weatherbit");
            stationData.put("country", weatherData.getOrDefault("country_code", "DF"));
            stationData.put("stationCode", MessageFormat.format("{0}_{1}", stationData.get("country"), stationData.get("identifier")));
            LOGGER.info("ReverseGeo api hit on WEATHERBIT done for : "+lat+" & "+lng);
            return stationData;
        } catch (Exception e) {
            LOGGER.error("WEATHER_RESPONSE :: "+response);
            LOGGER.error("WEATHER_URL :: "+reverseGeoURL);
            throw new Exception("Error while parsing weather station data", e);
        }
    }

    @Override
    public JSONObject getWeatherData(V3WeatherStationContext weatherStation, Long ttime, boolean doForecast, boolean dailyForecast) throws Exception {
        this.lat = weatherStation.getLat();
        this.lng = weatherStation.getLng();
        return getWeatherData(doForecast, dailyForecast, weatherStation.getStationCode());
    }

    @Override
    public JSONObject getWeatherData(double lat, double lng, Long time) throws Exception {
        this.lat = lat;
        this.lng = lng;
        return getWeatherData(true, false, "-1");
    }

    private JSONObject getWeatherData(boolean doForecast, boolean dailyForecast, String stationCode) throws Exception {
        String url = getWeatherURL();
        String response = hitApi(url, "The weather data fetching response is null from the weather server");
        try {
            JSONParser parser = new JSONParser();
            JSONObject weatherResponse = (JSONObject) parser.parse(response);
            List<Map<String, Object>> weatherDataList = new ArrayList<>();
            JSONObject currentData = (JSONObject) ((JSONArray) weatherResponse.get("data")).get(0);
            Map<String, Object> currentWeather = parse(currentData);
            Object currentTimeInMillis = System.currentTimeMillis();
            Object predictedTime = WeatherAPI.roundOfTime(currentTimeInMillis);
            currentWeather.put("actualTtime", currentTimeInMillis);
            currentWeather.put("ttime", predictedTime);
            appendCurrentAirQualityData(currentWeather);
            weatherDataList.add(currentWeather);

            try {
                if (doForecast) {
                    String forecastUrl = getForecastURL();
                    String forecastResponse = hitApi(forecastUrl, "The forecast weather data fetching response is null from the weather server");
                    JSONArray hourlyData = (JSONArray) ((JSONObject) parser.parse(forecastResponse)).get("data");
                    for (Object data : hourlyData) {
                        weatherDataList.add(parse((JSONObject) data));
                    }

                    weatherDataList = appendForcastAirQualityData(weatherDataList);
                }

                if (doForecast && dailyForecast) {
                    String dailyForecastResponse = hitApi(getDailyForecastURL(), "The daily forecast weather data fetching response is null from the weather server");

                    Map<Long, Map<String, Object>> weatherDataMap = new LinkedHashMap<>();
                    weatherDataList.stream().forEach(row -> weatherDataMap.put((Long) row.get("ttime"), row));
                    Long maxTtime = (Long) weatherDataList.get(weatherDataList.size() - 1).get("ttime");

                    JSONArray dailyData = (JSONArray) ((JSONObject) parser.parse(dailyForecastResponse)).get("data");
                    for (Object data : dailyData) {
                        JSONObject dailyJson = (JSONObject) data;
                        Long dt = WeatherAPI.getTimeinMillis(dailyJson.get("ts"));
                        if (dt <= maxTtime || weatherDataMap.containsKey(dt)) {
                            continue;
                        }
                        weatherDataList.add(parse(dailyJson));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("WEATHER_RESPONSE :: "+response);
                LOGGER.error("WEATHER_URL :: "+url);
                LOGGER.error("Weather data parsing failed for forecast with response", e);
            }

            for(Map<String, Object> wd : weatherDataList) {
                wd.put("stationCode", stationCode);
                wd.put("predictedTime", predictedTime);
            }

            LOGGER.info("WeatherData api hit on WEATHERBIT done for stationCode="+stationCode+ ", ttime="+ predictedTime);
            return WeatherAPI.formattedResponse(weatherDataList);
        } catch (Exception e) {
            LOGGER.error("WEATHER_RESPONSE :: "+response);
            LOGGER.error("WEATHER_URL :: "+url);
            throw new Exception("Weather data parsing failed with response", e);
        }
    }

    private void appendCurrentAirQualityData(Map<String, Object> currentWeather) {

        String airQualityUrl = getAirQualityURL();
        String response = null;
        try {
            response = hitApi(airQualityUrl, "The air quality weather data fetching response is null from the weather server");
            JSONParser parser = new JSONParser();
            JSONObject weatherResponse = (JSONObject) ((JSONArray) ((JSONObject) parser.parse(response)).get("data")).get(0);
            appendRawAirQualityData(currentWeather, weatherResponse);

        } catch (Exception e) {
            LOGGER.error("WEATHER_RESPONSE :: "+response);
            LOGGER.error("WEATHER_URL :: "+airQualityUrl);
            LOGGER.error("Weather air data parsing failed with response", e);
        }

    }

    private void appendRawAirQualityData(Map<String, Object> weatherData, JSONObject airData) {
        weatherData.put("co", airData.get("co"));
        weatherData.put("pm2_5", airData.get("pm25"));
        weatherData.put("pm10", airData.get("pm10"));
    }

    private List<Map<String, Object>> appendForcastAirQualityData(List<Map<String, Object>> weatherDataList) {
        Map<Object, Map<String, Object>> weatherDataMap = new LinkedHashMap<>();
        weatherDataList.stream().forEach(row -> weatherDataMap.put(row.get("ttime"), row));
        String airQualityUrl = getAirQualityForecastURL();
        String response = null;
        try {
            response = hitApi(airQualityUrl, "The air quality forecast weather data fetching response is null from the weather server");
            JSONParser parser = new JSONParser();
            JSONArray hourlyData = (JSONArray) ((JSONObject) parser.parse(response)).get("data");
            for (Object data : hourlyData) {
                JSONObject airData  = (JSONObject) data;
                Object ttime = WeatherAPI.getTimeinMillis(airData.get("ts"));
                Map<String, Object> weatherData = weatherDataMap.get(ttime);
                if(weatherData!=null) {
                    appendRawAirQualityData(weatherData, airData);
                }
            }
            return weatherDataMap.values().stream().collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("WEATHER_RESPONSE :: "+response);
            LOGGER.error("WEATHER_URL :: "+airQualityUrl);
            LOGGER.error("Weather air forecast data parsing failed with response", e);
            return weatherDataList;
        }
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

    public Map<String, Object> parse(JSONObject data) {

        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("temperature", data.get("temp"));
        weatherData.put("humidity", data.get("rh"));
        weatherData.put("dewPoint", data.get("dewpt"));
        weatherData.put("pressure", data.get("pres")); //mb
        if(data.containsKey("app_temp")) {
            weatherData.put("apparentTemperature", data.get("app_temp"));
        }

        weatherData.put("windSpeed", data.get("wind_spd"));
        weatherData.put("windDegree", data.get("wind_dir"));
        if(data.get("gust") != null) {
            weatherData.put("windGust", data.get("gust"));
        }else if(data.get("wind_gust_spd") != null) {
            weatherData.put("windGust", data.get("wind_gust_spd"));
        }

        weatherData.put("windBearing", data.get("wind_dir"));

        weatherData.put("cloudCover", data.get("clouds"));
        weatherData.put("uvIndex", data.get("uv"));
        // object to double


        Double visibility = Double.parseDouble(data.get("vis").toString());
        visibility = visibility * 1000; //kilometer -> meter
        weatherData.put("visibility", visibility);

        if(data.containsKey("pop")) {
            weatherData.put("precipProbability", data.get("pop"));
        }

        weatherData.put("precipIntensity", data.get("precip"));

        if(data.containsKey("clouds_mid")) { // forecast api only will have this key
            Object ttime = WeatherAPI.getTimeinMillis(data.get("ts"));
            weatherData.put("actualTtime", ttime);
            weatherData.put("ttime", ttime);
        }

        JSONObject weatherObj = (JSONObject) data.get("weather");

        weatherData.put("description", weatherObj.get("description"));
        weatherData.put("icon", getIcon(weatherObj));
        weatherData.put("iconMeta", weatherObj);


        return weatherData;
    }

    private Object getIcon(JSONObject weatherObj) {
        if(WEATHER_CONDITIONS.isEmpty()) {
            return null;
        }
        return WEATHER_CONDITIONS.get(weatherObj.get("code").toString());
    }



}
