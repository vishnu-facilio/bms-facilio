package com.facilio.bmsconsole.commands;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BooleanField;
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
	private TreeMap<Long, Long> combinedMap = new TreeMap<>();
	private TreeMap<Long, Long> tempMap = new TreeMap<>();
	private List<TreeMap<Long, Long>> combinedList = new ArrayList();
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ReportDataPointContext> dFDataPoints = new ArrayList<>();
		ReportDataPointContext tfDataPoint = null, cfDataPoint = null, ohDataPoint = null;
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		Long parentId = null;
		if(report.getReportTemplate() != null) {
			parentId = report.getReportTemplate().getParentId();
		}
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		Map<String, Object> reportAggrData = (Map<String, Object>) reportData.get(FacilioConstants.ContextNames.AGGR_KEY);
		Map<String, Object> filters = new HashMap<>();
		
		List<ReportDataPointContext> reportDataPoints = report.getDataPoints();
		
		if(!((ArrayList<Object>)reportData.get("data")).isEmpty()){
			JSONObject dataFilter = report.getDataFilterJSON();
			if(dataFilter != null && !dataFilter.isEmpty()) {
				JSONObject conditions = (JSONObject) dataFilter.get("conditions");
				if(conditions != null && !conditions.isEmpty()) {
					for(Object key : conditions.keySet()) {
						JSONObject condition = (JSONObject)conditions.get((String)key);
						List<Map<String, Object>> timeLine = getDFTimeLine(condition, report.getDateRange(), parentId);
						key = "Cri_"+key+".timeline";
						filters.put((String) key, timeLine);
					}
				}
				
				dFDataPoints = FilterUtil.getDFDataPoints(dataFilter);
			}
			
			
			JSONObject timeFilter = report.getTimeFilterJSON();
			if(timeFilter != null && !timeFilter.isEmpty()) {
				List<Map<String, Object>> TFTimeLine = getTFTimeLine(report.getDateRange(), timeFilter);
				if(CollectionUtils.isNotEmpty(TFTimeLine)) {
					filters.put("TimeFilter.timeline", TFTimeLine);
					
					tfDataPoint = FilterUtil.getTFDataPoints(reportDataPoints.get(0).getxAxis().getModuleName(), getTFDataPointName(timeFilter));	
				}
			}
			
			if(CollectionUtils.isNotEmpty(combinedList)) {
				createCombinedMap();
				if(MapUtils.isNotEmpty(combinedMap)) {
					filters.put("Filter.timeline", getCombinedTimeLine());
					
					cfDataPoint = FilterUtil.getDataPoint(reportDataPoints.get(0).getxAxis().getModuleName(), "Filter", "Filter", null);
				}
			}
			if(cfDataPoint != null) {
				reportDataPoints.add(cfDataPoint);
			}
			if(tfDataPoint != null) {
				reportDataPoints.add(tfDataPoint);
			}
			if(!dFDataPoints.isEmpty()){
				reportDataPoints.addAll(dFDataPoints);
			}
			report.setDataPoints(reportDataPoints);
			reportAggrData.putAll(filters);
			
		}
		Map<String, Object> operatingTimeLine = getOperatingHoursTimeLine((FacilioContext) context, report.getDateRange());
		if(operatingTimeLine != null && !operatingTimeLine.isEmpty()) {
			Map<Integer, Object> enumMap = new HashMap<>();
			enumMap.put(0, "No");
			enumMap.put(1, "Yes");
			ohDataPoint = FilterUtil.getDataPoint(reportDataPoints.get(0).getxAxis().getModuleName(), "Operating Hours", "operatingHours", enumMap);
			if(ohDataPoint != null) {
				reportDataPoints.add(ohDataPoint);
			}
			reportAggrData.putAll(operatingTimeLine);
		}
		long orgId = AccountUtil.getCurrentOrg().getId();
		if(orgId == 6l) {
			Logger.getLogger("FetchCriteriaReportCommand is" + context);
		}
		return false;
	}
	
	public List<Map<String, Object>> getDFTimeLine(JSONObject conditionObj, DateRange range, Long templateAssetId) throws Exception{
		List<Map<String, Object>> timeline = new ArrayList<>();
		List<Map<Long, Long>> timeList = new ArrayList<>();
		TreeMap<Long, Long> localMap = new TreeMap<>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Long field = (Long) conditionObj.get("fieldId");
		Long parentId = (Long) conditionObj.get("parentId");
		Boolean currentAsset = (Boolean) conditionObj.get("currentAsset");
		if(templateAssetId != null && (currentAsset != null && currentAsset)) {
			parentId = templateAssetId;
		}
		
		FacilioField conditionField = modBean.getField(field);
		
		FacilioModule module = conditionField.getModule();
		String moduleName = module.getName();
		
		FacilioField timeField = modBean.getField("ttime", moduleName);
		FacilioField parentIdField = modBean.getField("parentId", moduleName);
		String tableName = conditionField.getModule().getTableName();
		
		Object value = conditionObj.get("value"); 
		if (conditionField instanceof NumberField) {
			NumberField numberField =  (NumberField)conditionField;
			if(numberField.getUnitEnum() != null) {
				value = UnitsUtil.convertToSiUnit(value, numberField.getUnitEnum());
			}
		}
		else if(conditionField instanceof BooleanField) {
			value = (Long)value == 1 ? Boolean.TRUE : Boolean.FALSE;
		}
		
		int operatorId = ((Number) conditionObj.get("operatorId")).intValue();
		Operator operator = Operator.getOperator(operatorId);
		
		FacilioField orgIdField = AccountConstants.getOrgIdField(module);
		FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
		
		List<FacilioField> selectFields =  new ArrayList<>();
		String conditionString = null;
		if (operator.getOperator().equals("is empty")) {
			conditionString = conditionField.getCompleteColumnName() +" IS NULL";
		}
		else if (operator.getOperator().equals("is not empty")) {
			conditionString = conditionField.getCompleteColumnName() +" IS NOT NULL";
		} else {
			conditionString = conditionField.getCompleteColumnName()+" "+operator.getOperator()+" "+value;
		}
		
		ReportFacilioField yField = FilterUtil.getCriteriaField("appliedVsUnapplied", "appliedVsUnapplied", module, "CASE WHEN "+ conditionString +" THEN '1' ELSE '0' END", FieldType.ENUM);
		selectFields.add(timeField);
		selectFields.add(yField);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(tableName)
				.select(selectFields)
				.andCondition(CriteriaAPI.getCondition(orgIdField, String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(parentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(timeField, range.toString(), DateOperators.BETWEEN))
				.andCondition(CriteriaAPI.getCondition(conditionField, CommonOperators.IS_NOT_EMPTY))
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
		TreeMap<Long, Long> localMap = new TreeMap<>();
		
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
					index++;
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
					    	if(intervals != null && !intervals.isEmpty()) {
					    		for (Object values : intervals.values()) {
					    			Map<String, Object> obj = new HashMap();
					    			Map<String, Object> obj1 = new HashMap();
					    			JSONArray interval = (JSONArray) values;
						    		Long startTime = (long) (LocalTime.parse((CharSequence) interval.get(0)).toSecondOfDay()*1000);
							    	Long endTime = (long) (LocalTime.parse((CharSequence) interval.get(1)).toSecondOfDay()*1000);
							    	obj.put("key", start.toInstant().toEpochMilli()+startTime);
							    	obj.put("value", 1);
							    	obj1.put("key", start.toInstant().toEpochMilli()+endTime);
							    	obj1.put("value", 0);
							    	localMap.put(start.toInstant().toEpochMilli()+startTime, start.toInstant().toEpochMilli()+endTime);
							    	timeline.add(obj);
							    	timeline.add(obj1);
					    		}
					    	}else {
					    		Map<String, Object> obj = new HashMap();
				    			Map<String, Object> obj1 = new HashMap();
					    		obj.put("key", DateTimeUtil.getDayStartTimeOf(start.toInstant().toEpochMilli(), false));
						    	obj.put("value", 1);
						    	obj1.put("key", DateTimeUtil.getDayEndTimeOf(start.toInstant().toEpochMilli(), false));
						    	obj1.put("value", 0);
						    	localMap.put(DateTimeUtil.getDayStartTimeOf(start.toInstant().toEpochMilli(), false), DateTimeUtil.getDayEndTimeOf(start.toInstant().toEpochMilli(), false));
						    	timeline.add(obj);
						    	timeline.add(obj1);
					    	}
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
							    	timeline.add(obj);
							    	timeline.add(obj1);
						    	}
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
			if(i+1 < combinedList.size()) {
				TreeMap<Long, Long> nextMap = (TreeMap<Long, Long>) combinedList.get(i+1).clone();
				if(MapUtils.isNotEmpty(combinedMap)) {
					for(Map.Entry<Long, Long> entry: combinedMap.entrySet()) {
							aggregate(entry.getKey(), entry.getValue(), nextMap);
					}
					if(MapUtils.isNotEmpty(tempMap)) {
						combinedMap = (TreeMap<Long, Long>) tempMap.clone();
						tempMap.clear();
					}
				}else {
					for(Map.Entry<Long, Long> entry: map.entrySet()) {
							aggregate(entry.getKey(), entry.getValue(), nextMap);
					}
					if(MapUtils.isNotEmpty(tempMap)) {
						combinedMap = (TreeMap<Long, Long>) tempMap.clone();
						tempMap.clear();
					}
				}
			}
		}
	}
	
	public void aggregate(Long key, Long value, TreeMap<Long,Long> map) {
		Long start = null,end = null;
		boolean setStart = false, setEnd = false;
		Map<Long, Long> localMap = new TreeMap();
		for(Map.Entry<Long, Long> entry: map.entrySet()) {
			Long from = entry.getKey();
			Long to = entry.getValue();
			if(key.equals(from) && !setStart) {
				start = key;
				setStart = true;
			}
			else if(key > from && !setStart){
				start = key;
				setStart = true;
			}
			else if(key < from && !setStart){
				start = from;
				setStart = true;
			}
			if(start != null && value.equals(to)) {
				end = value;
			}
			else if(start != null && (value > to && start < to)){
				end = to;
			}
			else if(start != null && (value < to && start < value)){
				end = value;
			}
			if(start != null && end != null) {
				localMap.put(start, end);
				start = end = null;
				setStart = false;
			}
			start = end = null;
			setStart = false;
		}
		tempMap.putAll(localMap);
	}
	public List<Map<String, Object>> getCombinedTimeLine() throws Exception {
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
	
	public String getTFDataPointName(JSONObject criteriaObj) {
		JSONArray weekDays = new JSONArray();
		weekDays.add(new Long(1));
		weekDays.add(new Long(2));
		weekDays.add(new Long(3));
		weekDays.add(new Long(4));
		weekDays.add(new Long(5));
		JSONArray weekEnd = new JSONArray();
		weekEnd.add(new Long(6));
		weekEnd.add(new Long(7));
		ArrayList<String> daysName =  new ArrayList<String>();
		String name = "";
		String interval = "";
		if(criteriaObj != null && !criteriaObj.isEmpty()) {
			JSONObject conditions = (JSONObject) criteriaObj.get("conditions");
			for(Object key : conditions.keySet()) {
				JSONObject condition = (JSONObject)conditions.get((String)key);
				if(condition.get("operatorId").equals(new Long(85))) {
					JSONArray values = (JSONArray) condition.get("value");
					if(values.equals(weekDays)) {
						name = "Weekdays";
					}
					else if(values.equals(weekEnd)) {
						name = "Weekend";
					}
					else {
						for(int i = 0; i < values.size(); i++) {
							int day = Integer.parseInt(values.get(i).toString());
							String dayName = DayOfWeek.of(day).toString();
							if(!daysName.contains(dayName)) {
								daysName.add(dayName);
							}
						}
						name = StringUtils.join(daysName, ", ");
					}
				}
				else if(condition.get("operatorId").equals(new Long(86))) {
					JSONArray values = (JSONArray) condition.get("value");
					JSONArray formatValue = new JSONArray();
					for(int i = 0; i < values.size(); i++) {
						ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(), LocalTime.parse((CharSequence) values.get(i)) , ZoneOffset.UTC );
						formatValue.add(DateTimeFormatter.ofPattern("hh:mm a").format(zdt).toString());
					}
					
					if(StringUtils.isNotEmpty(interval)){
						interval = interval + " or " + StringUtils.join(formatValue, " - ");
					}
					else {
						interval = StringUtils.join(formatValue, " - ");
					}
				}
			}
			if(StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(interval)) {
				name = name + " ("+interval+")";
			}else if(StringUtils.isEmpty(name)){
				name = "All Days"+" ("+interval+")";
			}
		}
		return name.toString();
	}
	
	private static Map<String, Object> getOperatingHoursTimeLine(FacilioContext context, DateRange dateRange) throws Exception{
		Map<String, Object> operatingHoursObj = new HashMap<>();
		List<Map<String, Object>> timeline = new ArrayList<>();
		Object alarmType =  context.get(FacilioConstants.ContextNames.ALARM_TYPE);
		if(alarmType != null) {
			Boolean isOperationAlarm = (int)alarmType==BaseAlarmContext.Type.OPERATION_ALARM.getIndex();
			ResourceContext alarmResource =  (ResourceContext) context.get(FacilioConstants.ContextNames.ALARM_RESOURCE);
			if(isOperationAlarm != null && isOperationAlarm && alarmResource != null){
				Long operatingHourList = alarmResource.getOperatingHour();
				ZonedDateTime start = DateTimeUtil.getDateTime(dateRange.getStartTime(), false),  end = DateTimeUtil.getDateTime(dateRange.getEndTime(), false);
				if(operatingHourList != null) {
					BusinessHoursList operatingHourObj = BusinessHoursAPI.getBusinessHours(operatingHourList);
					if(CollectionUtils.isNotEmpty(operatingHourObj)) {
						Long startMillis = start.toInstant().toEpochMilli();
			    		Map<String, Object> startObj = new HashMap();
			    		startObj.put("key", startMillis);
			    		startObj.put("value", 0);
				    	timeline.add(startObj);
						do {
							for (BusinessHourContext operatingHour : operatingHourObj) {
								if (operatingHour.getDayOfWeek() == start.getDayOfWeek().getValue()) {
									if(operatingHour.getStartTime() != null) {
										Long startTime = (long) (LocalTime.parse(operatingHour.getStartTime()).toSecondOfDay()*1000);
								    	Long endTime = (long) (LocalTime.parse(operatingHour.getEndTime()).toSecondOfDay()*1000);
						    			Map<String, Object> obj1 = new HashMap();
						    			Map<String, Object> obj = new HashMap();
								    	obj.put("key", start.toInstant().toEpochMilli()+startTime);
								    	obj.put("value", 1);
								    	obj1.put("key", start.toInstant().toEpochMilli()+endTime);
								    	obj1.put("value", 0);
								    	timeline.add(obj);
								    	timeline.add(obj1);
									}else {
										timeline.addAll(prepareDateRangeTimeLine(start, DateTimeUtil.getDayEndTimeOf(start), 1));
									}
								}
							}
							start = start.plusDays(1);
						}  while (start.toEpochSecond() <= end.toEpochSecond());
						Long endMillis = end.toInstant().toEpochMilli();
						Map<String, Object> endObj = new HashMap();
			    		endObj.put("key", endMillis);
			    		endObj.put("value", 0);
			    		timeline.add(endObj);
					}
					else{
						timeline.addAll(prepareDateRangeTimeLine(start, end, 1));
					}
				}
				operatingHoursObj.put("operatingHours.timeline", timeline);
			}
		}
		return operatingHoursObj;
	}
	
	private static List<Map<String, Object>> prepareDateRangeTimeLine(ZonedDateTime start, ZonedDateTime end, int status){
		List<Map<String, Object>> timeLine = new ArrayList();
		Map<String, Object> obj = new HashMap();
		Map<String, Object> obj1 = new HashMap();
    	obj.put("key", start.toInstant().toEpochMilli());
    	obj.put("value", status);
    	obj1.put("key", end.toInstant().toEpochMilli());
    	obj1.put("value", 0);
    	timeLine.add(obj);
    	timeLine.add(obj1);
		return timeLine;
	}
}
