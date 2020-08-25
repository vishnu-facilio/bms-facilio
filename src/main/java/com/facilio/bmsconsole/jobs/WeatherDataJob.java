package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class WeatherDataJob extends FacilioJob {
	private static final Logger logger = LogManager.getLogger(WeatherDataJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		try {
			//logger.log(Level.INFO,"The weather data feature enabled for orgid: "+AccountUtil.getCurrentOrg().getOrgId());
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WEATHER_INTEGRATION))
			{
				return;
			}
			List<SiteContext> sites = WeatherUtil.getAllSites(true);
			List<ReadingContext> hourlyReadings= new ArrayList<ReadingContext>();
			List<ReadingContext> dailyReadings=new ArrayList<ReadingContext>();
			List<ReadingContext> psychrometricReadings= new ArrayList<ReadingContext>();
			Map<Long,List<ReadingContext>> siteCurrentReadings = new HashMap<Long,List<ReadingContext>>();
			Map<Long,List<ReadingContext>> siteDailyReadings = new HashMap<Long,List<ReadingContext>>();

			for(SiteContext site:sites) {

				Map<String,Object> weatherData=WeatherUtil.getWeatherData(site,null);
				logger.log(Level.INFO,"The weather data: "+weatherData);
				if(weatherData==null || weatherData.isEmpty()) {
					continue;
				}
				long siteId=site.getId();
				Map<String,Object> currentWeather= (JSONObject)weatherData.get("currently");
				ReadingContext reading=WeatherUtil.getHourlyReadingOld(siteId,FacilioConstants.ContextNames.WEATHER_READING,currentWeather);
				if(reading!=null) {
					
					WeatherUtil.populateMap(siteId, reading,siteCurrentReadings);
					
					ReadingContext psychrometricReading = WeatherUtil.getPsychrometricReading(siteId, currentWeather);
					if(psychrometricReading != null) {
						psychrometricReadings.add(psychrometricReading);
					}
					logger.log(Level.INFO,"The psychometric data: "+psychrometricReading);
				}
				//forecast..
				List<ReadingContext> hourlyForecast= WeatherUtil.getHourlyForecastReadings(siteId,FacilioConstants.ContextNames.WEATHER_HOURLY_FORECAST_READING,weatherData,true);
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
			logger.log(Level.ERROR, e.getMessage(), e);
			CommonCommandUtil.emailException("WeatherDataJob", "Exception in Weather Data job ", e);
		}
	}
}
