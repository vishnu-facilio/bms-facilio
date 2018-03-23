package com.facilio.bmsconsole.jobs;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;

public class WeatherDataJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	

	@Override
	public void execute(JobContext jc) {
		try {
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_WEATHER_INTEGRATION))
			{
				return;
			}
			List<SiteContext> sites=SpaceAPI.getAllSites(1);
			List<ReadingContext> readings= new ArrayList<ReadingContext>();
			for(SiteContext site:sites) {
				
				Map<String,Object> weatherData=getWeatherData(site);
				if(weatherData==null || weatherData.isEmpty()) {
					continue;
				}
				long siteId=site.getId();
				 ReadingContext reading=getReading(siteId,weatherData);
				 if(reading==null) {
					 continue;
				 }
				readings.add(reading);
			}
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WEATHER_READING);
			context.put(FacilioConstants.ContextNames.READINGS, readings);
			Chain addReading = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
			addReading.execute(context);
			
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<String,Object>getWeatherData(SiteContext site) throws Exception {
		
		LocationContext location= site.getLocation();
		if(location==null) {
			return null;
		}
		Double lat=location.getLat();
		Double lng=location.getLng();
		if(lat==-1 || lng==-1) {
			return null;
		}
		String weatherURL=WeatherUtil.getForecastURL(lat, lng);
		HttpURLConnection connection= WeatherUtil.getHttpURLConnection(weatherURL);
		String response=WeatherUtil.getResponse(connection);
		if(response==null){
			System.err.println("The response is null from the weather server");
			return null;
		}
		JSONParser parser = new JSONParser();
		JSONObject weatherData= (JSONObject) parser.parse(response);
		return (JSONObject)weatherData.get("currently");
		
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
		
		return reading;
	}

}
