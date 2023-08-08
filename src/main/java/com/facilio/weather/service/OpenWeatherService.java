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
public class OpenWeatherService implements WeatherService {

    /*
    Reference link - https://openweathermap.org/weather-conditions
     */
    private static final String WEATHER_CONDITION_PATH = "conf/weather/openweather.yml";

    public static Map<String, String> WEATHER_CONDITIONS = Collections.unmodifiableMap(initWeatherConditions());

    private double lat;
    private double lng;

    private String apiKey;
    public OpenWeatherService(String apiKey) {
        this.apiKey = apiKey;
    }

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

    public String getReverseGeoURL(double lat, double lng) {
        String reverseGeoURL = "http://api.openweathermap.org/geo/1.0/reverse?lat={0}&lon={1}&appid={2}";
        return MessageFormat.format(reverseGeoURL, lat, lng, apiKey);
    }

    public String getWeatherURL() {
        String currentWeatherURL = "https://api.openweathermap.org/data/2.5/weather?lat={0}&lon={1}&appid={2}&units=metric";
        return MessageFormat.format(currentWeatherURL, lat, lng, apiKey);
    }

    public String getForecastURL() {
        String forecastUrl = "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat={0}&lon={1}&appid={2}&units=metric";
        return MessageFormat.format(forecastUrl, lat, lng, apiKey);
    }

    public String getDailyForecastURL() {
        String url = "https://api.openweathermap.org/data/2.5/forecast/daily?lat={0}&lon={1}&cnt=16&appid={2}&units=metric";
        return MessageFormat.format(url, lat, lng, apiKey);
    }

    public String getAirQualityURL() {
        String url = "https://api.openweathermap.org/data/2.5/air_pollution?lat={0}&lon={1}&appid={2}&units=metric";
        return MessageFormat.format(url, lat, lng, apiKey);
    }

    public String getAirQualityForecastURL() {
        String url = "https://api.openweathermap.org/data/2.5/air_pollution/forecast?lat={0}&lon={1}&appid={2}";
        return MessageFormat.format(url, lat, lng, apiKey);
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
            stationData.put("stationCode", MessageFormat.format("{0}_{1}", stationData.get("country"), stationData.get("identifier")));
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
        return getWeatherData(lat, lng, true, WeatherAPI.isStartOfTheDay(), "-1");
    }

    @Override
    public JSONObject getWeatherData(V3WeatherStationContext weatherStation, Long ttime, boolean doForecast, boolean dailyForecast) throws Exception {
        Double lat = weatherStation.getLat();
        Double lng = weatherStation.getLng();
        return getWeatherData(lat, lng, doForecast, dailyForecast, weatherStation.getStationCode());
    }

    private JSONObject getWeatherData(Double lat, Double lng, boolean doForecast, boolean dailyForecast, String stationCode) throws Exception {
        this.lat = lat;
        this.lng = lng;
        String url = getWeatherURL();
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
            appendCurrentAirQualityData(currentWeather);
            result.put("currently", currentWeather);

            if(doForecast) {
                String forecastUrl = getForecastURL();
                String forecastResponse = hitApi(forecastUrl, "The forecast weather data fetching response is null from the weather server");
                JSONArray hourlyData = (JSONArray) ((JSONObject) parser.parse(forecastResponse)).get("list");
                List<Map<String, Object>> forecastList = new ArrayList<>();
                for (Object data : hourlyData) {
                    Map hourlyMap = parse((JSONObject) data);
                    time =  (Long) hourlyMap.get("ttime");
                    hourlyMap.put("predictedTime", predictedTime);
                    hourlyMap.put("stationCode", stationCode);
                    hourlyMap.put("time", time/1000);
                    forecastList.add(hourlyMap);
                }
                forecastList = appendForcastAirQualityData(forecastList);

                if(dailyForecast) {
                    String dailyForecastResponse = hitApi(getDailyForecastURL(), "The daily forecast weather data fetching response is null from the weather server");

                    Map<Object, Map<String, Object>> weatherDataMap = new LinkedHashMap<>();
                    forecastList.stream().forEach(row -> weatherDataMap.put(row.get("ttime"), row));

                    JSONArray dailyData = (JSONArray) ((JSONObject) parser.parse(dailyForecastResponse)).get("list");
                    for (Object data : dailyData) {
                        JSONObject dailyJson = (JSONObject) data;
                        Object dt = WeatherAPI.getTimeinMillis(dailyJson.get("dt"));
                        if(weatherDataMap.containsKey(dt)) {
                            continue;
                        }
                        forecastList.add(parseDailyWeather(dailyJson));
                    }
                }

                JSONObject hourlyJson = new JSONObject();
                hourlyJson.put("data", forecastList);
                result.put("forecast", hourlyJson);
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

    private List<Map<String, Object>> appendForcastAirQualityData(List<Map<String, Object>> weatherDataList) throws Exception {
        Map<Object, Map<String, Object>> weatherDataMap = new LinkedHashMap<>();
        weatherDataList.stream().forEach(row -> weatherDataMap.put(row.get("ttime"), row));
        String airQualityUrl = getAirQualityForecastURL();
        String response = hitApi(airQualityUrl, "The air quality forecast weather data fetching response is null from the weather server");
        try {
            JSONParser parser = new JSONParser();
            JSONArray hourlyData = (JSONArray) ((JSONObject) parser.parse(response)).get("list");
            for (Object data : hourlyData) {
                JSONObject airData  = (JSONObject) data;
                Object ttime = WeatherAPI.getTimeinMillis(airData.get("dt"));
                Map<String, Object> weatherData = weatherDataMap.get(ttime);
                if(weatherData!=null) {
                    appendRawAirQualityData(weatherData, airData);
                }
            }
            return weatherDataMap.values().stream().collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("WEATHER_RESPONSE :: "+response);
            LOGGER.error("WEATHER_URL :: "+airQualityUrl);
            throw new Exception("Weather air forecast data parsing failed with response", e);
        }

    }

    private void appendCurrentAirQualityData(JSONObject currentWeather) throws Exception {
        String airQualityUrl = getAirQualityURL();
        String response = hitApi(airQualityUrl, "The air quality weather data fetching response is null from the weather server");
        try {
            JSONParser parser = new JSONParser();
            JSONObject weatherResponse = (JSONObject) ((JSONArray) ((JSONObject) parser.parse(response)).get("list")).get(0);
            appendRawAirQualityData(currentWeather, weatherResponse);
        } catch (Exception e) {
            LOGGER.error("WEATHER_RESPONSE :: "+response);
            LOGGER.error("WEATHER_URL :: "+airQualityUrl);
            throw new Exception("Weather air data parsing failed with response", e);
        }

    }

    private void appendRawAirQualityData(Map<String, Object> weatherData, JSONObject airData) {
        JSONObject components = (JSONObject) airData.get("components");
        weatherData.put("co", components.get("co"));
        weatherData.put("no", components.get("no"));
        weatherData.put("nh3", components.get("nh3"));
        weatherData.put("pm2_5", components.get("pm2_5"));
        weatherData.put("pm10", components.get("pm10"));
    }

    private Map<String, Object> parseDailyWeather(JSONObject data) {
        Map<String, Object> weatherData = new HashMap<>();

        JSONArray weatherArr = (JSONArray) data.get("weather");
        JSONObject weatherObj = (JSONObject) weatherArr.get(0);

        weatherData.put("summary", weatherObj.get("description"));
        weatherData.put("icon", getIcon(weatherObj));
        weatherData.put("iconMeta", weatherObj);

        weatherData.put("temperature", ((JSONObject) data.get("temp")).get("day"));
        weatherData.put("humidity", data.get("humidity"));;
        weatherData.put("pressure", data.get("pressure"));

        weatherData.put("apparentTemperature", ((JSONObject) data.get("feels_like")).get("day"));

        weatherData.put("windSpeed", data.get("speed"));
        weatherData.put("windDegree", data.get("deg"));
        weatherData.put("windGust", data.get("gust"));
        weatherData.put("windBearing", data.get("deg"));

        weatherData.put("cloudCover", data.get("clouds"));

        if(data.containsKey("rain")) {
            weatherData.put("precipType", "rain");
            weatherData.put("precipIntensity", data.get("rain"));
        }
        if(data.containsKey("snow")) {
            weatherData.put("precipType", "snow");
            weatherData.put("precipIntensity",  data.get("rain"));
        }
        Object ttime = WeatherAPI.getTimeinMillis(data.get("dt"));
        weatherData.put("actualTtime", ttime);
        weatherData.put("ttime", ttime);
        return weatherData;
    }


    public JSONObject parse(JSONObject data) {
        JSONObject weatherData = new JSONObject();

        JSONObject mainData = (JSONObject) data.get("main");
        weatherData.put("temperature", mainData.get("temp"));
        weatherData.put("humidity", mainData.get("humidity"));
        weatherData.put("pressure", mainData.get("pressure"));
        weatherData.put("apparentTemperature", mainData.get("feels_like"));

        JSONObject windData = (JSONObject) data.get("wind");
        weatherData.put("windSpeed", windData.get("speed"));
        weatherData.put("windDegree", windData.get("deg"));
        weatherData.put("windGust", windData.get("gust"));
        weatherData.put("windBearing", windData.get("deg"));

        weatherData.put("cloudCover", ((JSONObject)data.get("clouds")).get("all"));
        weatherData.put("visibility", data.get("visibility"));
        if(data.containsKey("rain")) {
            weatherData.put("precipType", "rain");
            weatherData.put("precipIntensity", ((JSONObject) data.get("rain")).get("1h"));
        }
        if(data.containsKey("snow")) {
            weatherData.put("precipType", "snow");
            weatherData.put("precipIntensity", ((JSONObject) data.get("snow")).get("1h"));
        }

        JSONArray weatherArr = (JSONArray) data.get("weather");
        JSONObject weatherObj = (JSONObject) weatherArr.get(0);

        weatherData.put("summary", weatherObj.get("description"));
        weatherData.put("icon", getIcon(weatherObj));
        weatherData.put("iconMeta", weatherObj);

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
