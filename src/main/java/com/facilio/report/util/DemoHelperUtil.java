package com.facilio.report.util;

import org.apache.log4j.LogManager;

import com.facilio.modules.FacilioModule;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
;

public class DemoHelperUtil {

	private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(FilterUtil.class.getName());

	private static final long DAY_IN_MILLISECONDS = (long)(24*60*60*1000);
	
	public static Long getEndTime(FacilioModule module, DateRange dateRange) {
		long currentTime = System.currentTimeMillis();
		
		if(dateRange != null) {

			String moduleName = module.getName();
			
			if("energypredictionmlreadings".equals(moduleName))  {
				return dateRange.getEndTime();
			}
			
			if("loadpredictionmlreadings".equals(moduleName)) {
				long mlReadingEndTime = DateTimeUtil.getDayEndTimeOf(currentTime) + DAY_IN_MILLISECONDS;
				
				if(mlReadingEndTime >= dateRange.getEndTime()) {
					return dateRange.getEndTime();
				}
				return mlReadingEndTime;
			}
			
			// to handle past date as endTime
			if (currentTime >= dateRange.getEndTime()) {
				return dateRange.getEndTime();
			}
		}
		return currentTime;
	}
}