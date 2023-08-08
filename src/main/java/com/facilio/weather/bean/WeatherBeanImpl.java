package com.facilio.weather.bean;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class WeatherBeanImpl implements WeatherBean {

    @Override
    public void addOrUpdateWeatherStationData(JSONObject content) {
        long startTime = System.currentTimeMillis();
        long parentId = 0;
        try {
            Map<String,Object> weatherData = (Map<String, Object>) content.get("weatherData");
            LOGGER.debug("The weather data: " + weatherData);
            List<ReadingContext> psychrometricReadings = new ArrayList<>();
            Map<Long, List<ReadingContext>> stationCurrentReadings = new HashMap<>();
            parentId = Long.parseLong((String) content.get("stationId"));
            Map<String, Object> currentWeather = (Map<String, Object>) weatherData.get("currently");
            ReadingContext reading = WeatherUtil.getWeatherReading(parentId, FacilioConstants.ContextNames.NEW_WEATHER_READING, currentWeather);
            if (reading != null) {
                WeatherUtil.populateMap(parentId, reading, stationCurrentReadings);
                ReadingContext psychrometricReading = WeatherUtil.getPsychrometricReading(parentId, currentWeather);
                if (psychrometricReading != null) {
                    psychrometricReadings.add(psychrometricReading);
                }
                LOGGER.debug("The psychometric data: " + psychrometricReading);
            }
            //forecast..
            List<ReadingContext> forecastReadings = WeatherUtil.getForecastReadings(parentId,
                    FacilioConstants.ContextNames.WEATHER_HOURLY_FORECAST_READING, weatherData, false);
            if (!forecastReadings.isEmpty()) {
                WeatherUtil.populateMap(parentId, forecastReadings, stationCurrentReadings);

            }
            WeatherUtil.addDewPoint(parentId, stationCurrentReadings);
            WeatherUtil.addReading(FacilioConstants.ContextNames.NEW_PSYCHROMETRIC_READING, psychrometricReadings);
            stationCurrentReadings = WeatherUtil.getWeatherReading(FacilioConstants.ContextNames.NEW_WEATHER_READING, stationCurrentReadings);
            List<ReadingContext> newWeatherReadings = WeatherUtil.getReadingList(stationCurrentReadings);
            WeatherUtil.addReading(FacilioConstants.ContextNames.NEW_WEATHER_READING, newWeatherReadings);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            CommonCommandUtil.emailException("FacilioWeatherDataJob", "Exception in Facilio Weather Data job ", e);
        } finally {
            LOGGER.info("Time taken to process stationId "+parentId+
                    " in FacilioWeatherDataJob :: "+(System.currentTimeMillis() - startTime));
        }
    }
}
