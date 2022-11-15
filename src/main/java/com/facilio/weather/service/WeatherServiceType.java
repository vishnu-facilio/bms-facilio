package com.facilio.weather.service;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class WeatherServiceType {

    private static final Map<String, WeatherConf> WEATHER_CONF_MAP = Collections.unmodifiableMap(initWeatherConf());

    private static final String WEATHER_SERVICE_CONF_PATH = "conf/weather/weatherserviceconf.yml";

    public static void init() { }

    private static Map<String, WeatherConf> initWeatherConf() {
        try {
            Map<String, Object> weatherConf = FacilioUtil.loadYaml(WEATHER_SERVICE_CONF_PATH);
            List<Map<String, Object>> weatherServiceList = (List<Map<String, Object>>) weatherConf.get("weatherservices");
            Map<String, WeatherConf> weatherServiceMap = new HashMap<>();
            for (Map<String, Object> conf : weatherServiceList) {
                WeatherServiceType.WeatherConf wConf = new WeatherConf((String)conf.get("name"), (String)conf.get("class"));
                weatherServiceMap.put(wConf.getName(), wConf);
            }
            return weatherServiceMap;
        } catch (Exception e) {
            String msg = "Error occurred while parsing weatherservice conf : "+ WEATHER_SERVICE_CONF_PATH;
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    private static final WeatherService currentService = initCurrentService();

    private static WeatherService initCurrentService() {
        String serviceName = FacilioProperties.getConfig("weather.service");
        if(WEATHER_CONF_MAP.get(serviceName) == null) {
            throw new RuntimeException("Given weather.service is not available in weather conf :: "+WEATHER_SERVICE_CONF_PATH);
        }
        return getWeatherService(serviceName);
    }

    public static WeatherService getWeatherService(String name) {
        return WEATHER_CONF_MAP.get(name).getWeatherService();
    }

    public static WeatherService getCurrentService() {
        return currentService;
    }

    @Getter
    public static class WeatherConf {
        
        private String name;
        private WeatherService weatherService;

        private WeatherConf(String name, String className) {
            this.name = name;
            try {
                Class<? extends WeatherService> serviceObj = (Class<? extends WeatherService>) Class.forName(className);
                this.weatherService = serviceObj.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
