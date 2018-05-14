package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.PsychrometricUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class WeatherDataJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(WeatherDataJob.class.getName());
	
	

	@Override
	public void execute(JobContext jc) {
		try {
			logger.log(Level.INFO,"The weather data feature enabled for orgid: "+AccountUtil.getCurrentOrg().getOrgId());
		    System.out.println("The weather data feature enabled for orgid: "+AccountUtil.getCurrentOrg().getOrgId());
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_WEATHER_INTEGRATION))
			{
				return;
			}
			List<SiteContext> sites=SpaceAPI.getAllSites(1);
			List<ReadingContext> readings= new ArrayList<ReadingContext>();
			
			List<ReadingContext> psychrometricReadings= new ArrayList<ReadingContext>();
			for(SiteContext site:sites) {
				
				Map<String,Object> weatherData=WeatherUtil.getWeatherData(site);
			    logger.log(Level.INFO,"The weather data for orgid: "+AccountUtil.getCurrentOrg().getOrgId()+" : "+weatherData);
			    System.out.println("The weather data for orgid: "+AccountUtil.getCurrentOrg().getOrgId()+" : "+weatherData);
				if(weatherData==null || weatherData.isEmpty()) {
					continue;
				}
				long siteId=site.getId();
				 ReadingContext reading=getReading(siteId,weatherData);
				 if(reading==null) {
					 continue;
				 }
				readings.add(reading);
				
				ReadingContext psychrometricReading = getPsychrometricReading(siteId, weatherData);
				if(psychrometricReading == null) {
					continue;
				}
			    logger.log(Level.INFO,"The psychometric data for orgid: "+AccountUtil.getCurrentOrg().getOrgId()+" : "+psychrometricReading);
			    System.out.println("The psychometric data for orgid: "+AccountUtil.getCurrentOrg().getOrgId()+" : "+psychrometricReading);
				psychrometricReadings.add(psychrometricReading);
			}
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WEATHER_READING);
			context.put(FacilioConstants.ContextNames.READINGS, readings);
			Chain addReading = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
			addReading.execute(context);
			
			try {
				context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PSYCHROMETRIC_READING);
				context.put(FacilioConstants.ContextNames.READINGS, psychrometricReadings);
				Chain addPsychrometricReading = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
				addPsychrometricReading.execute(context);
			}
			catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private ReadingContext getPsychrometricReading(long siteId, Map<String,Object> weatherData) {
		
		ReadingContext reading= new ReadingContext();
		reading.setParentId(siteId);
		try {
			Long time=(Long)weatherData.get("time");
			long ttime=System.currentTimeMillis();
			if(time!=null) {
				ttime=time*1000;
			}
			reading.setTtime(ttime);
			Double wetBulbTemperature = PsychrometricUtil.getWetBulbTemperatureFromRelativeHumidity(weatherData);
			Double dewPointTemperature = PsychrometricUtil.getDewPointTemperatureFromRelativeHumudity(weatherData);
			
			reading.addReading("wetBulbTemperature", wetBulbTemperature);
			reading.addReading("dewPointTemperature", dewPointTemperature);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return reading;
	}
	
	private ReadingContext getReading(long siteId,Map<String,Object> currentWeather) {
		
		
		Object temperature=currentWeather.get("temperature");
		Long time=(Long)currentWeather.get("time");
		
		if(temperature==null) {
			return null;
		}
		
		long ttime=System.currentTimeMillis();
		if(time!=null) {
			ttime=time*1000;
		}
		ReadingContext reading= new ReadingContext();
		reading.setParentId(siteId);
		reading.setTtime(ttime);
		reading.addReading("temperature", temperature);
		reading.addReading("icon", currentWeather.get("icon"));
		reading.addReading("summary", currentWeather.get("summary"));
		reading.addReading("humidity",currentWeather.get("humidity"));
		reading.addReading("dewPoint", currentWeather.get("dewPoint"));
		reading.addReading("pressure", currentWeather.get("pressure"));
		reading.addReading("apparentTemperature", currentWeather.get("apparentTemperature"));
		
		return reading;
	}

}
