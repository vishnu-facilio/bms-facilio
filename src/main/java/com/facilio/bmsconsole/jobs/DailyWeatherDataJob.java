package com.facilio.bmsconsole.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.weather.util.WeatherAPI;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class DailyWeatherDataJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(DailyWeatherDataJob.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		try {

			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_SITE_SUMMARY) || !WeatherAPI.allow())
			{
				return;
			}
			Map<Long,List<ReadingContext>> siteDailyReadings = new HashMap<Long,List<ReadingContext>>();

			List<SiteContext> sites=WeatherUtil.getAllSites(true);
			Map<Long, Map<String,Object>> stationVsData = new HashMap<>();
			long time = DateTimeUtil.getDayStartTime(-1)/1000;

			for(SiteContext site:sites) {
				
				Map<String,Object> weatherData = null;
				long weatherStationId = site.getWeatherStation();
				if (weatherStationId > 0) {
					weatherData = stationVsData.get(weatherStationId);
					if (weatherData == null) {
						weatherData=WeatherUtil.getWeatherData(site,time);
						stationVsData.put(weatherStationId, weatherData);
					}
				}
				else {
					weatherData=WeatherUtil.getWeatherData(site,time);
				}
				
				logger.log(Level.INFO,"Daily weather data : "+weatherData);
				if(weatherData==null || weatherData.isEmpty()) {
					continue;
				}
				long siteId=site.getId();
				List<ReadingContext> readings= WeatherUtil.getDailyForecastReadings(siteId,FacilioConstants.ContextNames.WEATHER_DAILY_READING,weatherData,false);
				if(readings!=null && !readings.isEmpty()) {
					WeatherUtil.populateMap(siteId, readings,siteDailyReadings);
				}
			}

			siteDailyReadings   = WeatherUtil.getWeatherReading(FacilioConstants.ContextNames.WEATHER_DAILY_READING, siteDailyReadings );
			WeatherUtil.addReading(FacilioConstants.ContextNames.WEATHER_DAILY_READING,WeatherUtil.getReadingList(siteDailyReadings));	
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			CommonCommandUtil.emailException("DailyWeatherDataJob", "Exception in DailyWeatherDataJob ", e);
		}
	}

}
