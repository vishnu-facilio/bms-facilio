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

@Log4j
public class WeatherApiService implements WeatherService {

    private double lat;
    private double lng;

    private String apiKey;
    public WeatherApiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getReverseGeoURL(double lat, double lng) {
        String reverseGeoURL = "http://api.weatherapi.com/v1/search.json?key={0}&q={1},{2}";
        return MessageFormat.format(reverseGeoURL, apiKey, lat, lng);
    }

    public String getWeatherURL() {
        String currentWeatherURL = "http://api.weatherapi.com/v1/current.json?key={0}&q={1},{2}&aqi=yes";
        return MessageFormat.format(currentWeatherURL, apiKey, lat, lng);
    }

    public String getForecastURL() {
        String forecastUrl = "http://api.weatherapi.com/v1/forecast.json?key={0}&q={1},{2}&days=10&aqi=yes&alerts=no";
        return MessageFormat.format(forecastUrl, apiKey, lat, lng);
    }

    public String getDailyForecastURL() {
        //https://www.weatherapi.com/api-explorer.aspx#future
        //dt - Date between 14 days and 300 days from today in the future in yyyy-MM-dd format
        String url = "http://api.weatherapi.com/v1/future.json?key={0}&q={1},{2}&dt=2023-08-31";
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
            stationData.put("identifier", weatherData.get("name").toString() +" - "+weatherData.get("region").toString());
            stationData.put("serviceName", "weatherapi");
            stationData.put("country", weatherData.getOrDefault("country", "Default"));
            stationData.put("stationCode", weatherData.get("url"));
            LOGGER.info("ReverseGeo api hit on WEATHERAPI done for : "+lat+" & "+lng);
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
        String url = doForecast ? getForecastURL() : getWeatherURL();
        String response = hitApi(url, "The weather data fetching response is null from the weather server");
        try {
            JSONParser parser = new JSONParser();
            JSONObject weatherResponse = (JSONObject) parser.parse(response);
            List<Map<String, Object>> weatherDataList = new ArrayList<>();
            Map<String, Object> currentWeather = parse((JSONObject) weatherResponse.get("current"));
            Long currentTime = System.currentTimeMillis();
            Object predictedTime = WeatherAPI.roundOfTime(currentTime);
            currentWeather.put("ttime", predictedTime);
            weatherDataList.add(currentWeather);

            if(weatherResponse.containsKey("forecast")) {
                JSONArray forecastDayArr = (JSONArray) ((JSONObject)weatherResponse.get("forecast")).get("forecastday");
                for(Object forecastDay : forecastDayArr) {
                    JSONArray forecastHourArr = (JSONArray) ((JSONObject) forecastDay).get("hour");
                    for(Object forecastHour : forecastHourArr) {
                        Long hrTime = WeatherAPI.getTimeinMillis(((JSONObject) forecastHour).get("time_epoch"));
                        if(hrTime <= currentTime) {
                            continue;
                        }
                        weatherDataList.add(parse((JSONObject) forecastHour));
                    }
                }
            }

            for(Map<String, Object> wd : weatherDataList) {
                wd.put("stationCode", stationCode);
                wd.put("predictedTime", predictedTime);
            }

            LOGGER.info("WeatherData api hit on WEATHERAPI done for stationCode="+stationCode+ ", ttime="+ predictedTime);
            return WeatherAPI.formattedResponse(weatherDataList);
        } catch (Exception e) {
            LOGGER.error("WEATHER_RESPONSE :: "+response);
            LOGGER.error("WEATHER_URL :: "+url);
            throw new Exception("Weather data parsing failed with response", e);
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

        weatherData.put("temperature", data.get("temp_c"));
        weatherData.put("humidity", data.get("humidity"));
        if(data.containsKey("dewpoint_c")) {
            weatherData.put("dewPoint", data.get("dewpoint_c"));
        }
        weatherData.put("pressure", data.get("pressure_mb"));
        weatherData.put("apparentTemperature", data.get("feelslike_c"));

        Double windSpeed = Double.parseDouble(data.get("wind_kph").toString());
        windSpeed = windSpeed * 0.277778; //kilometer per hour -> meter per sec
        weatherData.put("windSpeed", windSpeed);
        weatherData.put("windDegree", data.get("wind_degree"));
        Double windGust = Double.parseDouble(data.get("gust_kph").toString());
        windGust = windGust * 0.277778; //kilometer per hour -> meter per sec
        weatherData.put("windGust", windGust);
        weatherData.put("windBearing", data.get("wind_degree"));

        weatherData.put("cloudCover", data.get("cloud"));
        weatherData.put("uvIndex", data.get("uv"));
        Double visibility = Double.parseDouble(data.get("vis_km").toString());
        visibility = visibility * 1000; //kilometer -> meter
        weatherData.put("visibility", visibility);

        if(data.containsKey("will_it_rain")) {
            int willItRain = Integer.parseInt(data.get("will_it_rain").toString());
            if (willItRain == 1) {
                weatherData.put("precipType", "rain");
                weatherData.put("precipProbability", data.get("chance_of_rain"));

            }
            int willItSnow = Integer.parseInt(data.get("will_it_snow").toString());
            if (willItSnow == 1) {
                weatherData.put("precipType", "snow");
                weatherData.put("precipProbability", data.get("chance_of_snow"));
            }
        }

        weatherData.put("precipIntensity", data.get("precip_mm"));

        if(data.containsKey("air_quality")) {
            JSONObject airQualityData = (JSONObject) data.get("air_quality");
            weatherData.put("co", airQualityData.get("co"));
            weatherData.put("pm2_5", airQualityData.get("pm2_5"));
            weatherData.put("pm10", airQualityData.get("pm10"));
        }

        Object ttime = WeatherAPI.roundOfTime(System.currentTimeMillis());
        if(data.containsKey("time_epoch")) {
            ttime = WeatherAPI.getTimeinMillis(data.get("time_epoch"));
        }
        weatherData.put("actualTtime", ttime);
        weatherData.put("ttime", ttime);

        JSONObject conditionObj = (JSONObject) data.get("condition");
        weatherData.put("summary", conditionObj.get("text"));  //need to fix this
        weatherData.put("icon", getIcon(conditionObj));
        weatherData.put("iconMeta", conditionObj);
        return weatherData;
    }

    private Object getIcon(JSONObject weatherObj) {
        if(WEATHER_CONDITIONS.isEmpty()) {
            return null;
        }
        return WEATHER_CONDITIONS.get(weatherObj.get("code").toString());
    }

    /*
    Reference link - https://www.weatherapi.com/docs/weather_conditions.json
     */
    private static final String WEATHER_CONDITION_PATH = "conf/weather/weatherapi.yml";
    public static Map<String, String> WEATHER_CONDITIONS = Collections.unmodifiableMap(initWeatherConditions());

    public static Map<String, String> initWeatherConditions() {
        Yaml yaml = new Yaml();
        Map<String, String> weatherCondnMap = new HashMap<>();
        try (InputStream inputStream = WeatherApiService.class.getClassLoader().getResourceAsStream(WEATHER_CONDITION_PATH)) {
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


}
