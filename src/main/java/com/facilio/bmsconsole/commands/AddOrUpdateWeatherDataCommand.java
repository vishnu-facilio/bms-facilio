package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateWeatherDataCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(AddOrUpdateWeatherDataCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			Map<Long,List<Long>>  siteVsWeatherStationMap = (Map<Long, List<Long>>) context.get("WeatherStationIdVsSiteId");
			Map<Long, Map<String, Object>> dataMap = (Map<Long, Map<String, Object>>) context.get("dataMap");
			List<ReadingContext> hourlyReadings= new ArrayList<ReadingContext>();
			List<ReadingContext> dailyReadings=new ArrayList<ReadingContext>();
			List<ReadingContext> psychrometricReadings= new ArrayList<ReadingContext>();

				
			Map<Long,List<ReadingContext>> siteCurrentReadings = new HashMap<Long,List<ReadingContext>>();
			Map<Long,List<ReadingContext>> siteDailyReadings = new HashMap<Long,List<ReadingContext>>();

			for(Entry<Long, List<Long>> entry:siteVsWeatherStationMap.entrySet()) {

				Map<String,Object> weatherData=dataMap.get(entry.getKey());
				LOGGER.log(Level.INFO,"The weather data: "+weatherData);
				if(weatherData==null || weatherData.isEmpty()) {
					continue;
				}
				List<Long> sites = entry.getValue();
				for(long siteId:sites) {
					Map<String,Object> currentWeather= (JSONObject)weatherData.get("currently");
					ReadingContext reading=WeatherUtil.getWeatherReading(siteId,FacilioConstants.ContextNames.WEATHER_READING,currentWeather);
					if(reading!=null) {

						WeatherUtil.populateMap(siteId, reading,siteCurrentReadings);

						ReadingContext psychrometricReading = WeatherUtil.getPsychrometricReading(siteId, currentWeather);
						if(psychrometricReading != null) {
							psychrometricReadings.add(psychrometricReading);
						}
						LOGGER.log(Level.INFO,"The psychometric data: "+psychrometricReading);
					}
					//forecast..
					List<ReadingContext> hourlyForecast= WeatherUtil.getForecastReadings(siteId,FacilioConstants.ContextNames.WEATHER_HOURLY_FORECAST_READING,weatherData,true);
					List<ReadingContext> dailyForecast= WeatherUtil.getDailyForecastReadings(siteId,FacilioConstants.ContextNames.WEATHER_DAILY_FORECAST_READING,weatherData,true);
					if(!hourlyForecast.isEmpty()) {
						hourlyReadings.addAll(hourlyForecast);
						WeatherUtil.populateMap(siteId, hourlyForecast,siteCurrentReadings);

					}
					if(!dailyForecast.isEmpty()) {
						dailyReadings.addAll(dailyForecast);
						WeatherUtil.populateMap(siteId, dailyForecast,siteDailyReadings);
					}
				}
			}
			WeatherUtil.addReading(FacilioConstants.ContextNames.PSYCHROMETRIC_READING ,psychrometricReadings);
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_HOURLY_FORECAST_READING,hourlyReadings);	
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_DAILY_FORECAST_READING ,dailyReadings);

			siteCurrentReadings = WeatherUtil.getWeatherReading(FacilioConstants.ContextNames.WEATHER_READING, siteCurrentReadings );
			siteDailyReadings   = WeatherUtil.getWeatherReading(FacilioConstants.ContextNames.WEATHER_DAILY_READING, siteDailyReadings );
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_READING,WeatherUtil.getReadingList(siteCurrentReadings));	
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_DAILY_READING,WeatherUtil.getReadingList(siteDailyReadings));
		}catch(Exception e) {
			throw e;
		}
		
		return false;
	}

}
