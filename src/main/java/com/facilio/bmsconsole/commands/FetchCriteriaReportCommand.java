package com.facilio.bmsconsole.commands;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.AbstractMap.SimpleEntry;

//import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFactory.ReportFacilioField;
import com.facilio.report.util.FilterUtil;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.UnitsUtil;

public class FetchCriteriaReportCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(FetchCriteriaReportCommand.class.getName());
	private Map<Long, Long> combinedMap = new HashMap<>();
	private HashMap<Long, Long> tempMap = new HashMap<>();
	private List<Map<Long, Long>> combinedList = new ArrayList();
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		boolean needCriteriaReport =  (boolean) context.getOrDefault(FacilioConstants.ContextNames.NEED_CRITERIAREPORT, false);
		if(needCriteriaReport) {
			List<ReportDataPointContext> dataPoints = new ArrayList<>();
			
			ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
			JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
			Map<String, Object> reportAggrData = (Map<String, Object>) reportData.get(FacilioConstants.ContextNames.AGGR_KEY);
			Map<String, Object> filters = new HashMap<>();
			
			List<ReportDataPointContext> reportDataPoints = report.getDataPoints();
			
			JSONObject dataFilter = (JSONObject) context.get(FacilioConstants.ContextNames.DATA_FILTER);
			if(dataFilter != null && !dataFilter.isEmpty()) {
				JSONObject conditions = (JSONObject) dataFilter.get("conditions");
				if(conditions != null && !conditions.isEmpty()) {
					for(Object key : conditions.keySet()) {
						JSONObject condition = (JSONObject)conditions.get((String)key);
						List<Map<String, Object>> timeLine = getDFTimeLine(condition, report.getDateRange());
						key = "Cri_"+key+".timeline";
						filters.put((String) key, timeLine);
					}
				}
				
				dataPoints.addAll(FilterUtil.getDFDataPoints(dataFilter));
			}
			
			
			JSONObject timeFilter = (JSONObject) context.get(FacilioConstants.ContextNames.TIME_FILTER);
			if(timeFilter != null && !timeFilter.isEmpty()) {
				filters.put("TimeFilter.timeline", getTFTimeLine(report.getDateRange(), timeFilter));
				
				dataPoints.addAll(FilterUtil.getTFDataPoints(reportDataPoints.get(0).getxAxis().getModuleName(), timeFilter));
			}
			
			createCombinedMap();
			if(MapUtils.isNotEmpty(combinedMap)) {
				filters.put("Combine.timeline", getCombinedTimeLine(report.getDateRange()));
				
				dataPoints.add(FilterUtil.getDataPoint(reportDataPoints.get(0).getxAxis().getModuleName(), "Combine"));
			}
			dataPoints.addAll(reportDataPoints);
			report.setDataPoints(dataPoints);
			reportAggrData.putAll(filters);
//			System.out.println(combinedMap);
		}
		return false;
	}
	
	public List<Map<String, Object>> getDFTimeLine(JSONObject conditionObj, DateRange range) throws Exception{
		List<Map<String, Object>> timeline = new ArrayList<>();
		List<Map<Long, Long>> timeList = new ArrayList<>();
		Map<Long, Long> localMap = new HashMap<>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String moduleName = (String) conditionObj.get("moduleName");
		String field = (String) conditionObj.get("fieldName");
		Long parentId = (Long) conditionObj.get("parentId");
		
		FacilioModule module = modBean.getModule(moduleName);
		FacilioField timeField = modBean.getField("ttime", moduleName);
		FacilioField parentIdField = modBean.getField("parentId", moduleName);
		FacilioField conditionField = modBean.getField(field, moduleName);
		String tableName = conditionField.getModule().getTableName();
		
		Object value = conditionObj.get("value"); 
		if (conditionField instanceof NumberField) {
			NumberField numberField =  (NumberField)conditionField;
			if(numberField.getUnitEnum() != null) {
				value = UnitsUtil.convertToSiUnit(value, numberField.getUnitEnum());
			}
		}
		int operatorId = ((Number) conditionObj.get("operatorId")).intValue();
		Operator operator = Operator.getOperator(operatorId);
		
		FacilioField orgIdField = AccountConstants.getOrgIdField(module);
		FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
		
		List<FacilioField> selectFields =  new ArrayList<>();
		
		ReportFacilioField yField = FilterUtil.getCriteriaField("appliedVsUnapplied", "appliedVsUnapplied", module, "CASE WHEN "+conditionField.getCompleteColumnName()+" "+operator.getOperator()+" "+value+" THEN '1' ELSE '0' END", FieldType.ENUM);
		selectFields.add(timeField);
		selectFields.add(yField);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(tableName)
				.select(selectFields)
				.andCondition(CriteriaAPI.getCondition(orgIdField, String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(parentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(timeField, range.toString(), DateOperators.BETWEEN))
				.orderBy(timeField.getCompleteColumnName());
		List<Map<String, Object>> props = builder.get();
		
		if (props != null && !props.isEmpty()) {
			int preVal = 0;
			Long start = null;
			for (Map<String, Object> prop : props) {
				Map<String, Object> obj = new HashMap();
				int val = Integer.parseInt((String)prop.get("appliedVsUnapplied"));
				Long time = (Long) prop.get("ttime");
				if(val == 1 && start == null) {
					start = time;
				}
				if(val == 0 && start != null) {
					localMap.put(start, time);
					start = null;
				}
				if(preVal != val) {
					preVal = val;
					obj.put("key", prop.get("ttime"));
					obj.put("value", val);
					timeline.add(obj);
				}
			}
			if(start != null) {
				localMap.put(start, range.getEndTime());
			}
			timeList.add(localMap);
			combinedList.add(localMap);
			Map<String, Object> obj = new HashMap();
			obj.put("key", range.getEndTime());
			obj.put("value", preVal == 0 ? 1 : 0);
			timeline.add(obj);
		}
		
		return timeline;
	}
	
	public List<Map<String, Object>> getTFTimeLine(DateRange dateRange, JSONObject criteriaObj) throws Exception {
		List<Map<String, Object>> timeline = new ArrayList<>();
		List<Map<Long, Long>> timeList = new ArrayList<>();
		Map<Long, Long> localMap = new HashMap<>();
		
		if(criteriaObj != null && !criteriaObj.isEmpty()) {
			JSONObject conditions = (JSONObject) criteriaObj.get("conditions");
			JSONObject calendarObj = new JSONObject();
			JSONObject timeObj = new JSONObject();
			int index = 0;
			for(Object key : conditions.keySet()) {
				JSONObject condition = (JSONObject)conditions.get((String)key);
				if(condition.get("operatorId").equals(new Long(85))) {
					calendarObj.put("days", condition.get("value"));
				}
				else if(condition.get("operatorId").equals(new Long(86))) {
					timeObj.put(index, condition.get("value"));
					calendarObj.put("time", timeObj);
				}
			}
			if(calendarObj != null && !calendarObj.isEmpty()) {
				JSONArray days = (JSONArray)calendarObj.get("days");
				JSONObject intervals = (JSONObject)calendarObj.get("time");
				if((days != null && !days.isEmpty()) || (intervals != null && !intervals.isEmpty())) {
					ZonedDateTime start = DateTimeUtil.getDateTime(dateRange.getStartTime(), false),  end = DateTimeUtil.getDateTime(dateRange.getEndTime(), false);
					do {
						if ((days != null && !days.isEmpty()) && days.contains(new Long(start.getDayOfWeek().getValue()))) {
				    		for (Object values : intervals.values()) {
				    			Map<String, Object> obj = new HashMap();
				    			Map<String, Object> obj1 = new HashMap();
			
				    			JSONArray interval = (JSONArray) values;
						    	if(interval != null && !interval.isEmpty()) {
						    		Long startTime = (long) (LocalTime.parse((CharSequence) interval.get(0)).toSecondOfDay()*1000);
							    	Long endTime = (long) (LocalTime.parse((CharSequence) interval.get(1)).toSecondOfDay()*1000);
							    	obj.put("key", start.toInstant().toEpochMilli()+startTime);
							    	obj.put("value", 1);
							    	obj1.put("key", start.toInstant().toEpochMilli()+endTime);
							    	obj1.put("value", 0);
							    	localMap.put(start.toInstant().toEpochMilli()+startTime, start.toInstant().toEpochMilli()+endTime);
						    	}else {
						    		obj.put("key", DateTimeUtil.getDayStartTimeOf(start.toInstant().toEpochMilli(), false));
							    	obj.put("value", 1);
							    	obj1.put("key", DateTimeUtil.getDayEndTimeOf(start.toInstant().toEpochMilli(), false));
							    	obj1.put("value", 0);
						    	}
						    	timeline.add(obj);
						    	timeline.add(obj1);
						    };
						}
						else if (days == null && intervals != null && !intervals.isEmpty()){
							for (Object values : intervals.values()) {
				    			Map<String, Object> obj = new HashMap();
				    			Map<String, Object> obj1 = new HashMap();
			
				    			JSONArray interval = (JSONArray) values;
						    	if(interval != null && !interval.isEmpty()) {
						    		Long startTime = (long) (LocalTime.parse((CharSequence) interval.get(0)).toSecondOfDay()*1000);
							    	Long endTime = (long) (LocalTime.parse((CharSequence) interval.get(1)).toSecondOfDay()*1000);
							    	obj.put("key", start.toInstant().toEpochMilli()+startTime);
							    	obj.put("value", 1);
							    	obj1.put("key", start.toInstant().toEpochMilli()+endTime);
							    	obj1.put("value", 0);
							    	localMap.put(start.toInstant().toEpochMilli()+startTime, start.toInstant().toEpochMilli()+endTime);
						    	}else {
						    		obj.put("key", DateTimeUtil.getDayStartTimeOf(start.toInstant().toEpochMilli(), false));
							    	obj.put("value", 1);
							    	obj1.put("key", DateTimeUtil.getDayEndTimeOf(start.toInstant().toEpochMilli(), false));
							    	obj1.put("value", 0);
						    	}
						    	timeline.add(obj);
						    	timeline.add(obj1);
						    };
						}
					    start = start.plusDays(1);
					}  while (start.toEpochSecond() <= end.toEpochSecond());
					timeList.add(localMap);
					combinedList.add(localMap);
				}
			}
		}
		return timeline;
	}
	
	public void createCombinedMap() {
		for(int i = 0; i < combinedList.size(); i++) {
			Map<Long, Long> map = combinedList.get(i);
			combinedMap = (Map<Long, Long>) tempMap.clone();
			if(MapUtils.isNotEmpty(combinedMap)) {
				for(Map.Entry<Long, Long> entry: combinedMap.entrySet()) {
					if(i+1 < combinedList.size()) {
						aggregate(entry.getKey(), entry.getValue(), combinedList.get(i+1));
					}
				}
			}else {
				for(Map.Entry<Long, Long> entry: map.entrySet()) {
					if(i+1 < combinedList.size()) {
						aggregate(entry.getKey(), entry.getValue(), combinedList.get(i+1));
					}
				}
			}
		}
	}
	
	public void aggregate(Long key, Long value, Map<Long,Long> map) {
		Long start = null,end = null;
		boolean setStart = false, setEnd = false;
		Map<Long, Long> localMap = new HashMap();
		for(Map.Entry<Long, Long> entry: map.entrySet()) {
			Long from = entry.getKey();
			Long to = entry.getValue();
			if(from.equals(key) && !setStart) {
				start = key;
				setStart = true;
			}
			else if(from > key && !setStart){
				start = from;
				setStart = true;
			}
			else if(from < key && !setStart){
				start = key;
				setStart = true;
			}
			if((to.equals(value) || value < to) && start != null) {
				end = value;
			}
			if(start != null && end != null) {
				localMap.put(start, end);
				start = end = null;
				setStart = false;
			}
		}
		tempMap.putAll(localMap);
	}
	
	public List<Map<String, Object>> getCombinedTimeLine(DateRange dateRange) throws Exception {
		List<Map<String, Object>> timeLine = new ArrayList<>();
			
		 TreeMap<Long, Long> sorted = new TreeMap<>(); 
        sorted.putAll(combinedMap);
	  
        for (Map.Entry<Long, Long> entry : sorted.entrySet()) {
        	Map<String, Object> obj = new HashMap();
        	Map<String, Object> obj1 = new HashMap();
        	obj.put("key", entry.getKey());
	    	obj.put("value", 1);
	    	obj1.put("key", entry.getValue());
	    	obj1.put("value", 0);
	    	timeLine.add(obj);
	    	timeLine.add(obj1);
        }
		return timeLine;
	}
	
}