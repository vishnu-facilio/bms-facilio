package com.facilio.report.util;

import org.apache.log4j.LogManager;

import com.facilio.modules.FacilioModule;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
;

public class DemoHelperUtil {

	private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(FilterUtil.class.getName());

	private static final long DAY_IN_MILLISECONDS = (long)(24*60*60*1000);
	
	public static Long getEndTime(FacilioModule module, long endTime) {
		long currentTime = System.currentTimeMillis();
		
//		if(endTime != null) {

			String moduleName = module.getName();
			
			if("energypredictionmlreadings".equals(moduleName))  {
				return endTime;
			}
			
			if("loadpredictionmlreadings".equals(moduleName)) {
				long mlReadingEndTime = DateTimeUtil.getDayEndTimeOf(currentTime) + DAY_IN_MILLISECONDS;
				
				if(mlReadingEndTime >= endTime) {
					return endTime;
				}
				return mlReadingEndTime;
			}
			
			// to handle past date as endTime
			if (currentTime >= endTime) {
				return endTime;
			}
//		}
		return currentTime;
	}
}