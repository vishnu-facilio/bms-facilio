package com.facilio.report.util;

import org.apache.log4j.LogManager;

import com.facilio.modules.FacilioModule;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
;

public class DemoHelperUtil {

	private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(FilterUtil.class.getName());

	public static Long getEndTime(FacilioModule module, DateRange dateRange) {
		Long currentTime = System.currentTimeMillis();
		
		if(dateRange != null) {
			Long endTime = 0L;
			String moduleName = module.getName();
			if("energypredictionmlreadings".equals(moduleName))  {
				return DateTimeUtil.getMonthEndTimeOf(currentTime);
			}
			
			if("loadpredictionmlreadings".equals(moduleName)) {
				endTime = DateTimeUtil.getDayEndTimeOf(currentTime);
				endTime += (24*60*60*1000);
				return endTime;
			}
			// to handle past date as endTime
			if (endTime >= dateRange.getEndTime()) {
				return dateRange.getEndTime();
			}
		}
		return currentTime;
	}
}