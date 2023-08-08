package com.facilio.weather.service;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class WeatherServiceType {

    private static final Map<String, String> WEATHER_CONF_MAP = Collections.unmodifiableMap(initWeatherConf());

    private static final String WEATHER_SERVICE_CONF_PATH = "conf/weather/weatherserviceconf.yml";

    public static void init() {}

    private static Map<String, String> initWeatherConf() {
        try {
            Map<String, Object> weatherConf = FacilioUtil.loadYaml(WEATHER_SERVICE_CONF_PATH);
            List<Map<String, Object>> weatherServiceList = (List<Map<String, Object>>) weatherConf.get("weatherservices");
            Map<String, String> weatherServiceMap = new HashMap<>();
            for (Map<String, Object> conf : weatherServiceList) {
                weatherServiceMap.put(conf.get("name").toString(), conf.get("class").toString());
            }
            return weatherServiceMap;
        } catch (Exception e) {
            String msg = "Error occurred while parsing weatherservice conf : " + WEATHER_SERVICE_CONF_PATH;
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    private static final WeatherService defaultService = initDefaultService();

    private static WeatherService initDefaultService() {
        String serviceName = FacilioProperties.getConfig("weather.service");
        if (WEATHER_CONF_MAP.get(serviceName) == null) {
            throw new RuntimeException("Given weather.service is not available in weather conf :: " + WEATHER_SERVICE_CONF_PATH);
        }
        String className = WEATHER_CONF_MAP.get(serviceName);
        if(serviceName.equals("facilioweather")) {
            return create(className);
        }
        return create(className, FacilioProperties.getConfig("weather.key"));
    }

    public static WeatherService getWeatherService(String name, String apiKey) {

        boolean isDefault = FacilioProperties.getConfig("weather.service").equals(name);
        if(isDefault) {
            return defaultService;
        }

        String className = WEATHER_CONF_MAP.get(name);
        if(StringUtils.isEmpty(className)) {
            LOGGER.error("watherservice - " + name + " is not supported or Given weather service is not available " +
                    "in weather conf :: " + WEATHER_SERVICE_CONF_PATH);
            return null;
        }
        if (StringUtils.isEmpty(apiKey)) {
            LOGGER.error("watherservice - " + name + "'s apiKey is empty. can't instantiate the given service");
            return null;
        }
        return create(className, apiKey);
    }

    private static WeatherService create(String className, String apiKey) {
        try {
            Class<? extends WeatherService> serviceObj = (Class<? extends WeatherService>) Class.forName(className);
            Class[] types = { String.class };
            Object[] params = { apiKey };
            Constructor cons = serviceObj.getConstructor(types);
            return (WeatherService) cons.newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    private static WeatherService create(String className) {
        try {
            Class<? extends WeatherService> serviceObj = (Class<? extends WeatherService>) Class.forName(className);
            return serviceObj.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}