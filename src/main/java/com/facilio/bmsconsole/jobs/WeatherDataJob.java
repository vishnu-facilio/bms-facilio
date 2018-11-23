package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
			List<ReadingContext> readings= new ArrayList<ReadingContext>();
			
			List<ReadingContext> psychrometricReadings= new ArrayList<ReadingContext>();
			for(SiteContext site:sites) {

				Map<String,Object> weatherData=WeatherUtil.getWeatherData(site);
				logger.log(Level.INFO,"The weather data for orgid: "+AccountUtil.getCurrentOrg().getOrgId()+" : "+weatherData);
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
				psychrometricReadings.add(psychrometricReading);
			}
				addReading(FacilioConstants.ContextNames.WEATHER_READING,readings);	
				addReading(FacilioConstants.ContextNames.PSYCHROMETRIC_READING ,psychrometricReadings);
			
		}
		catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage(), e);
			CommonCommandUtil.emailException("WeatherDataJob", "Exception in Weather Data job ", e);
		}
	}
	
	
	private void addReading(String moduleName,List<ReadingContext> readings) throws Exception {
		
		if(readings==null || readings.isEmpty()) {
			return;
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.READINGS, readings);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
		Chain addReading = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
		addReading.execute(context);
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
	
	private ReadingContext getReading(long siteId,Map<String,Object> currentWeather) {
		
		
		Object temperature=currentWeather.get("temperature");
		
		if(temperature==null) {
			return null;
		}
		
		ReadingContext reading= new ReadingContext();
		reading.setParentId(siteId);
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
