package com.facilio.report.util;

import java.time.ZonedDateTime;

import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class FilterUtil {
	private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(FilterUtil.class.getName());
	
	public static Criteria getTimeFilterCriteria(DateRange dateRange, String moduleName, JSONObject calendarObj) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField timeField = modBean.getField("ttime", moduleName);
		Criteria timeFilterCriteria = new Criteria();
		
		if(calendarObj != null && !calendarObj.isEmpty()) {
			JSONArray days = (JSONArray)calendarObj.get("days");
			JSONObject intervals = (JSONObject)calendarObj.get("time");
			if((days != null && !days.isEmpty()) || (intervals != null && !intervals.isEmpty())) {
				ZonedDateTime start = DateTimeUtil.getDateTime(dateRange.getStartTime(), false),  end = DateTimeUtil.getDateTime(dateRange.getEndTime(), false);
				do {
				    if (days.contains(new Long(start.getDayOfWeek().getValue())) || days.isEmpty()) {
				    	if(intervals != null && !intervals.isEmpty()) {
				    		for (Object key : intervals.keySet()) {
					            String keyStr = (String)key;
			
						    	JSONArray interval = (JSONArray)intervals.get(keyStr);
						    	if(interval != null && !interval.isEmpty()) {
						    		Long startTime = (Long)interval.get(0);
							    	Long endTime = (Long)interval.get(1);
							    	DateRange intervalRange = new DateRange();
							    	intervalRange.setStartTime(start.toInstant().toEpochMilli()+startTime);
							    	intervalRange.setEndTime(start.toInstant().toEpochMilli()+endTime);
							    	timeFilterCriteria.addOrCondition(CriteriaAPI.getCondition(timeField, intervalRange.toString(), DateOperators.BETWEEN));
						    	}else {
						    		DateRange intervalRange = new DateRange();
							    	intervalRange.setStartTime(DateTimeUtil.getDayStartTimeOf(start.toInstant().toEpochMilli(), false));
							    	intervalRange.setEndTime(DateTimeUtil.getDayEndTimeOf(start.toInstant().toEpochMilli(), false));
							    	timeFilterCriteria.addOrCondition(CriteriaAPI.getCondition(timeField, intervalRange.toString(), DateOperators.BETWEEN));
						    	}
						    };
				    	}
				    }
				    start = start.plusDays(1);
				}  while (start.toEpochSecond() <= end.toEpochSecond());
			}
			return timeFilterCriteria;
		}
		return null;
	}
	
	public static Criteria getDataFilterCriteria(DateRange datRange, String moduleName, JSONObject calendarObj) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField timeField = modBean.getField("ttime", moduleName);
		Criteria timeFilterCriteria = new Criteria();
		return null;
	}
	private static boolean isValidObj(JSONObject calendarObj) {
		if(calendarObj != null && !calendarObj.isEmpty()) {
			return true;
		}
		return false;
	}
}