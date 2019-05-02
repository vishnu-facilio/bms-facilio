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
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class DailyWeatherDataJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(DailyWeatherDataJob.class.getName());
	
	

	@Override
	public void execute(JobContext jc) {
		try {
		
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WEATHER_INTEGRATION))
			{
				return;
			}
			Map<Long,List<ReadingContext>> siteDailyReadings = new HashMap<Long,List<ReadingContext>>();

			List<SiteContext> sites=SpaceAPI.getAllSites(1);
			for(SiteContext site:sites) {
				
				Map<String,Object> weatherData=WeatherUtil.getWeatherData(site,DateTimeUtil.getDayStartTime(-1)/1000);
				logger.log(Level.INFO,"Daily weather data for orgid: "+AccountUtil.getCurrentOrg().getOrgId()+" : "+weatherData);
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
