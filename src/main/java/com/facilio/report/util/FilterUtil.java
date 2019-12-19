package com.facilio.report.util;

import java.time.LocalTime;
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
	
	public static Criteria getTimeFilterCriteria(DateRange datRange, String moduleName, JSONObject calendarObj) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField timeField = modBean.getField("ttime", moduleName);
		Criteria timeFilterCriteria = new Criteria();
		
		JSONArray days = (JSONArray)calendarObj.get("days");
		JSONObject intervals = (JSONObject)calendarObj.get("time");
//		intervals.keySet().forEach(keyStr ->
//	    {
//	    	intervalArray.addAll((JSONArray)intervals.get(keyStr));
//	    });
		ZonedDateTime start = DateTimeUtil.getDateTime(datRange.getStartTime(), false),  end = DateTimeUtil.getDateTime(datRange.getEndTime(), false);
		do {
		    if (days.contains(new Long(start.getDayOfWeek().getValue()))) {
		    	
		    	for (Object key : intervals.keySet()) {
		            String keyStr = (String)key;

			    	JSONArray interval = (JSONArray)intervals.get(keyStr);
			    	Long startTime = (Long)interval.get(0);
			    	Long endTime = (Long)interval.get(1);
			    	DateRange intervalRange = new DateRange();
			    	intervalRange.setStartTime(start.toInstant().toEpochMilli()+startTime);
			    	intervalRange.setEndTime(start.toInstant().toEpochMilli()+endTime);
			    	timeFilterCriteria.addOrCondition(CriteriaAPI.getCondition(timeField, intervalRange.toString(), DateOperators.BETWEEN));
			    };
//		    	Condition condition = CriteriaAPI.getCondition(timeField, intervalRange.toString(), DateOperators.BETWEEN);
//		    	timeFilterCriteria.addOrCondition(condition);
//		    	printIt(start.plusHours(0).plusMinutes(0).toInstant().toEpochMilli());
//		    	System.out.println(start.plusHours(05).plusMinutes(30).toInstant().toEpochMilli());
//		    	printIt(start.toInstant().toEpochMilli());
//		    	printIt(start.toLocalDate().atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli());
//		    	System.out.println(start.toInstant().toEpochMilli() - start.minusDays(1).toLocalDate().atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli());
//		    	printIt(start.toLocalDate().atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli()-start.toInstant().toEpochMilli());
		    }
		    start = start.plusDays(1);
		}  while (start.toEpochSecond() <= end.toEpochSecond());
		return timeFilterCriteria;
	}
	
}