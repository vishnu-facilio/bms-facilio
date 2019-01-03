package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.PsychrometricUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class WeatherDataJob extends FacilioJob {
	private static final Logger logger = LogManager.getLogger(WeatherDataJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		try {
			logger.log(Level.INFO,"The weather data feature enabled for orgid: "+AccountUtil.getCurrentOrg().getOrgId());
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_WEATHER_INTEGRATION))
			{
				return;
			}
			List<SiteContext> sites=SpaceAPI.getAllSites(1);
			List<ReadingContext> currentReadings= new ArrayList<ReadingContext>();
			List<ReadingContext> hourlyReadings= new ArrayList<ReadingContext>();
			List<ReadingContext> dailyReadings=new ArrayList<ReadingContext>();
			List<ReadingContext> psychrometricReadings= new ArrayList<ReadingContext>();
			
			for(SiteContext site:sites) {

				Map<String,Object> weatherData=WeatherUtil.getWeatherData(site,null);
				logger.log(Level.INFO,"The weather data for orgid: "+AccountUtil.getCurrentOrg().getOrgId()+" : "+weatherData);
				if(weatherData==null || weatherData.isEmpty()) {
					continue;
				}
				long siteId=site.getId();
				Map<String,Object> currentWeather= (JSONObject)weatherData.get("currently");
				ReadingContext reading=WeatherUtil.getHourlyReading(siteId,currentWeather);
				if(reading!=null) {
					currentReadings.add(reading);
					ReadingContext psychrometricReading = getPsychrometricReading(siteId, currentWeather);
					if(psychrometricReading != null) {
						psychrometricReadings.add(psychrometricReading);
					}
					logger.log(Level.INFO,"The psychometric data for orgid: "+AccountUtil.getCurrentOrg().getOrgId()+" : "+psychrometricReading);
				}
				//forecast..
				List<ReadingContext> hourlyForecast= WeatherUtil.getHourlyForecastReadings(siteId,weatherData,true);
				List<ReadingContext> dailyForecast= WeatherUtil.getDailyForecastReadings(siteId,weatherData,true);
				if(!hourlyForecast.isEmpty()) {
					hourlyReadings.addAll(hourlyForecast);
				}
				if(!dailyForecast.isEmpty()) {
                  dailyReadings.addAll(dailyForecast);
				}
			}
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_READING,currentReadings);	
			WeatherUtil.addReading(FacilioConstants.ContextNames.PSYCHROMETRIC_READING ,psychrometricReadings);
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_HOURLY_FORECAST_READING,hourlyReadings);	
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_DAILY_FORECAST_READING ,dailyReadings);	
		}
		catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage(), e);
			CommonCommandUtil.emailException("WeatherDataJob", "Exception in Weather Data job ", e);
		}
	}
	
	
	


	
	
	private ReadingContext getPsychrometricReading(long siteId, Map<String,Object> weatherData) {
		
		ReadingContext reading= new ReadingContext();
		reading.setParentId(siteId);
		try {
			Double wetBulbTemperature = PsychrometricUtil.getWetBulbTemperatureFromRelativeHumidity(weatherData);
			Double dewPointTemperature = PsychrometricUtil.getDewPointTemperatureFromRelativeHumudity(weatherData);
			Double enthalpy = PsychrometricUtil.getEnthalpy(weatherData);
			
			
			reading.addReading("wetBulbTemperature", wetBulbTemperature);
			reading.addReading("dewPointTemperature", dewPointTemperature);
			reading.addReading("enthalpy", enthalpy);
		}
		catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage(), e);
		}
		return reading;
	}
	
	
	
}
