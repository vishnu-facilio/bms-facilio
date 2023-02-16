package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.weather.util.WeatherAPI;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.PsychrometricUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class PsychrometricsCalculatorJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(PsychrometricsCalculatorJob.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		try {

			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_SITE_SUMMARY) || !WeatherAPI.allow())
			{
				return;
			}

			Map<Long,List<Map<String,Object>>> siteVsReadings=WeatherUtil.getWeatherReadings();
			Set<Long> siteIds= siteVsReadings.keySet();
			if (siteIds==null || siteIds.isEmpty()) {
				return;
			}
			Map<String, List<ReadingContext>> moduleVsReading = getModuleReadings(siteVsReadings, siteIds);
			persistReading(moduleVsReading);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void persistReading(Map<String, List<ReadingContext>> moduleVsReading) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.READINGS_MAP,moduleVsReading);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
		FacilioChain addDataChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
		addDataChain.execute(context);
	}


	private Map<String, List<ReadingContext>> getModuleReadings( Map<Long, List<Map<String, Object>>> siteVsReadings,
			Set<Long> siteIds) throws Exception {
		
		List<SiteContext> sites= SpaceAPI.getSiteSpace(StringUtils.join(siteIds, ","));
		Map<String,List<ReadingContext>> moduleVsReading = new HashMap<String,List<ReadingContext>> ();

		for(SiteContext site: sites) {

			Long siteId=site.getId();
			List<Map<String,Object>> weatherReadings=siteVsReadings.get(siteId);
			Double wetBulbTemperature = PsychrometricUtil.getWetBulbTemperatureFromRelativeHumidity(weatherReadings.get(0));
			ReadingContext cReading= getReading(siteId, "wetBulbTemperature", wetBulbTemperature);
			addReading(moduleVsReading,FacilioConstants.ContextNames.WET_BULB_TEMPERATURE, cReading);
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
