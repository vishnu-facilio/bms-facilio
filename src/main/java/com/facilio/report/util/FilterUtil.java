package com.facilio.report.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
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
			if((days != null && !days.isEmpty()) && (intervals != null && !intervals.isEmpty())) {
				ZonedDateTime start = DateTimeUtil.getDateTime(dateRange.getStartTime(), false),  end = DateTimeUtil.getDateTime(dateRange.getEndTime(), false);
				do {
				    if (days.contains(new Long(start.getDayOfWeek().getValue()))) {
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
	
	public static Criteria getDataFilterCriteria(String moduleName, JSONObject criteriaObj) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField timeField = modBean.getField("ttime", moduleName);
		Criteria dataFilterCriteria = new Criteria();
		
		if(criteriaObj != null && !criteriaObj.isEmpty()) {
			JSONArray conditions = (JSONArray)criteriaObj.get("conditions");
			for (int i = 0; i < conditions.size(); i++) {
				Map<String, Object> dataFilter = (Map<String, Object>) conditions.get(i);
				Long fieldId = (Long) dataFilter.get("fieldId");
				Long parentId = (Long) dataFilter.get("parentId");
				
				FacilioField field = modBean.getField(fieldId);
				FacilioModule module = field.getModule();
				
				FacilioField parentIdField = modBean.getField("parentId", module.getName());
				FacilioField ttimeField = modBean.getField("ttime", module.getName());
				
				String value = (String) dataFilter.get("value");
				int operatorId = ((Number) dataFilter.get("operatorId")).intValue();
				Operator operator = Operator.getOperator(operatorId);
				
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
						.table(module.getTableName())
						.select(Collections.singletonList(ttimeField))
						.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(parentId), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(field, value, operator));
						;
						
				List<Map<String, Object>> list = builder.get();
				Criteria criteria = new Criteria();
				for (Map<String, Object> map : list) {
					criteria.addOrCondition(CriteriaAPI.getCondition(timeField, String.valueOf(map.get("ttime")), NumberOperators.EQUALS));
				}
				dataFilterCriteria.orCriteria(criteria);
			}
		}
		return dataFilterCriteria;
	}
	private static boolean isValidObj(JSONObject calendarObj) {
		if(calendarObj != null && !calendarObj.isEmpty()) {
			return true;
		}
		return false;
	}
}