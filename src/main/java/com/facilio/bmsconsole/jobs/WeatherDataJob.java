package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.weather.util.WeatherAPI;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class WeatherDataJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		long startTime = System.currentTimeMillis();
		try {
			//logger.log(Level.INFO,"The weather data feature enabled for orgid: "+AccountUtil.getCurrentOrg().getOrgId());
			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_SITE_SUMMARY) || !WeatherAPI.allow()) {
				return;
			}
			List<SiteContext> sites = WeatherUtil.getAllSites(true);
			Map<Long, Map<String,Object>> stationVsData = new HashMap<>();
			
			List<ReadingContext> hourlyReadings= new ArrayList<ReadingContext>();
			List<ReadingContext> dailyReadings=new ArrayList<ReadingContext>();
			List<ReadingContext> psychrometricReadings= new ArrayList<ReadingContext>();
			Map<Long,List<ReadingContext>> siteCurrentReadings = new HashMap<Long,List<ReadingContext>>();
			Map<Long,List<ReadingContext>> siteDailyReadings = new HashMap<Long,List<ReadingContext>>();

			for(SiteContext site:sites) {
				
				Map<String,Object> weatherData = null;
				long weatherStationId = site.getWeatherStation();
				if (weatherStationId > 0) {
					weatherData = stationVsData.get(weatherStationId);
					if (weatherData == null) {
						weatherData=WeatherUtil.getWeatherData(site,null);
						stationVsData.put(weatherStationId, weatherData);
					}
				}
				else {
					weatherData=WeatherUtil.getWeatherData(site,null);
				}

				LOGGER.debug("The weather data: "+weatherData);
				if(weatherData==null || weatherData.isEmpty()) {
					continue;
				}
				long siteId=site.getId();
				Map<String,Object> currentWeather= (JSONObject)weatherData.get("currently");
				ReadingContext reading=WeatherUtil.getWeatherReading(siteId,FacilioConstants.ContextNames.WEATHER_READING,currentWeather);
				if(reading!=null) {
					
					WeatherUtil.populateMap(siteId, reading,siteCurrentReadings);
					
					ReadingContext psychrometricReading = WeatherUtil.getPsychrometricReading(siteId, currentWeather);
					if(psychrometricReading != null) {
						psychrometricReadings.add(psychrometricReading);
					}
					LOGGER.debug("The psychometric data: "+psychrometricReading);
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
			WeatherUtil.addReading(FacilioConstants.ContextNames.PSYCHROMETRIC_READING ,psychrometricReadings);
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_HOURLY_FORECAST_READING,hourlyReadings);	
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_DAILY_FORECAST_READING ,dailyReadings);
			
			siteCurrentReadings = WeatherUtil.getWeatherReading(FacilioConstants.ContextNames.WEATHER_READING, siteCurrentReadings );
			siteDailyReadings   = WeatherUtil.getWeatherReading(FacilioConstants.ContextNames.WEATHER_DAILY_READING, siteDailyReadings );
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_READING,WeatherUtil.getReadingList(siteCurrentReadings));	
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_DAILY_READING,WeatherUtil.getReadingList(siteDailyReadings));	
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			CommonCommandUtil.emailException("WeatherDataJob", "Exception in Weather Data job ", e);
		}
		finally {
			LOGGER.info("Time taken for Weather Data job. " + (System.currentTimeMillis() - startTime));
		}
	}
}
