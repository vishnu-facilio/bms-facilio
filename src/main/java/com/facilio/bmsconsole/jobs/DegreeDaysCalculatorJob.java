package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;

public class DegreeDaysCalculatorJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	

	@Override
	public void execute(JobContext jc) {
		try {
		
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_WEATHER_INTEGRATION))
			{
				return;
			}
			Map<String, List<ReadingContext>> moduleVsReading =new HashMap<String,List<ReadingContext>> ();
		
			//cdd n hdd calculations..
			Map<Long,List<Map<String,Object>>> siteVsReadings=WeatherUtil.getWeatherReadings();
			Set<Long> siteIds= siteVsReadings.keySet();
			if (siteIds!=null && !siteIds.isEmpty()) {
				 moduleVsReading = getWeatherReadings(siteVsReadings, siteIds);
			}
			
			//wet  bulb calculation..
			siteVsReadings=WeatherUtil.getWetBulbReadings();
			siteIds= siteVsReadings.keySet();
			if (siteIds!=null && !siteIds.isEmpty()) {
				moduleVsReading.putAll(getWetBulbReadings(siteVsReadings, siteIds));
			}
			persistReading(moduleVsReading);
			
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void persistReading(Map<String, List<ReadingContext>> moduleVsReading) throws Exception {
		
		if(moduleVsReading.isEmpty()) {
			return;
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.READINGS_MAP,moduleVsReading);
		Chain addDataChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
		addDataChain.execute(context);
	}


	private Map<String, List<ReadingContext>> getWeatherReadings( Map<Long, List<Map<String, Object>>> siteVsReadings,
			Set<Long> siteIds) throws Exception {
		
		List<SiteContext> sites= SpaceAPI.getSiteSpace(StringUtils.join(siteIds, ","));
		Map<String,List<ReadingContext>> moduleVsReading = new HashMap<String,List<ReadingContext>> ();

		for(SiteContext site: sites) {

			Long siteId=site.getId();
			Double cddBaseTemp=site.getCddBaseTemperature();
			Double hddBaseTemp=site.getHddBaseTemperature();
			List<Map<String,Object>> weatherReadings=siteVsReadings.get(siteId);

			if(cddBaseTemp>0) {
				Double cdd=WeatherUtil.getCDD(cddBaseTemp, weatherReadings);
				ReadingContext cReading= getReading(siteId,"cdd",cdd);
				addReading(moduleVsReading,FacilioConstants.ContextNames.CDD_READING, cReading);
			}

			if(hddBaseTemp>0) {
				Double hdd=WeatherUtil.getHDD(hddBaseTemp, weatherReadings);
				ReadingContext hReading= getReading(siteId,"hdd",hdd);
				addReading(moduleVsReading,FacilioConstants.ContextNames.HDD_READING, hReading);
			}
		}
		return moduleVsReading;
	}
	
	
	private Map<String, List<ReadingContext>> getWetBulbReadings( Map<Long, List<Map<String, Object>>> siteVsReadings,
			Set<Long> siteIds) throws Exception {
		
		List<SiteContext> sites= SpaceAPI.getSiteSpace(StringUtils.join(siteIds, ","));
		Map<String,List<ReadingContext>> moduleVsReading = new HashMap<String,List<ReadingContext>> ();

		for(SiteContext site: sites) {

			Long siteId=site.getId();
			Double wddBaseTemp=site.getWddBaseTemperature();
			List<Map<String,Object>> weatherReadings=siteVsReadings.get(siteId);
			
			if(wddBaseTemp>0) {
				Double wdd=WeatherUtil.getWDD(wddBaseTemp, weatherReadings);
				ReadingContext wReading= getReading(siteId,"wdd",wdd);
				addReading(moduleVsReading,FacilioConstants.ContextNames.WDD_READING, wReading);
			}
		}
		return moduleVsReading;
	}

	private void addReading(Map<String, List<ReadingContext>> moduleVsReading, String moduleName, ReadingContext reading) {
		List<ReadingContext> readingList=moduleVsReading.get(moduleName);
		if(readingList==null) {
			readingList= new ArrayList<ReadingContext>();
			moduleVsReading.put(moduleName, readingList);
		}
		readingList.add(reading);
	}
	
	private ReadingContext getReading(Long siteId,String key, Double dd) {
		
		ReadingContext reading= new ReadingContext();
		reading.setParentId(siteId);
		reading.setTtime(DateTimeUtil.getDayStartTime(-1));
		reading.addReading(key, dd);
		return reading;
	}

}
