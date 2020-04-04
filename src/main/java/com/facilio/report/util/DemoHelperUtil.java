package com.facilio.report.util;

import org.apache.log4j.LogManager;

import com.facilio.modules.FacilioModule;
import com.facilio.time.DateRange;

public class DemoHelperUtil {
	private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(FilterUtil.class.getName());
	
	public static Long getEndTime(FacilioModule module, DateRange dateRange) {
		Long endTime = System.currentTimeMillis();
		if (endTime < dateRange.getEndTime()) {
			endTime = dateRange.getEndTime();
		}
		return endTime;
	}
}