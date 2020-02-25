package com.facilio.bmsconsole.commands;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

//import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
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
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class FetchCriteriaReportCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(FetchCriteriaReportCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		boolean needCriteriaReport =  (boolean) context.getOrDefault(FacilioConstants.ContextNames.NEED_CRITERIAREPORT, false);
		if(needCriteriaReport) {
			
			ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
			JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
			Map<String, Object> reportAggrData = (Map<String, Object>) reportData.get(FacilioConstants.ContextNames.AGGR_KEY);
			Map<String, Object> filters = new HashMap<>();
			
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
				List<ReportDataPointContext> reportDataPoints = report.getDataPoints();
				
				List<ReportDataPointContext> dataPoints = FilterUtil.getDFDataPoints(dataFilter);
				reportDataPoints.addAll(dataPoints);
				report.setDataPoints(reportDataPoints);
			}
			
			JSONObject timeFilter = (JSONObject) context.get(FacilioConstants.ContextNames.TIME_FILTER);
			if(timeFilter != null && !timeFilter.isEmpty()) {
				filters.put("TimeFilter.timeline", getTFTimeLine(report.getDateRange(), timeFilter));
				List<ReportDataPointContext> reportDataPoints = report.getDataPoints();
				
				List<ReportDataPointContext> dataPoints = FilterUtil.getTFDataPoints(reportDataPoints.get(0).getxAxis().getModuleName(), timeFilter);
				reportDataPoints.addAll(dataPoints);
				report.setDataPoints(reportDataPoints);
			}
//			reportAggrData.put("filters", filters);
			reportAggrData.putAll(filters);
		}
		return false;
	}
	
	public List<Map<String, Object>> getDFTimeLine(JSONObject conditionObj, DateRange range) throws Exception{
		List<Map<String, Object>> timeline = new ArrayList<>();
		
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
			for (Map<String, Object> prop : props) {
				Map<String, Object> obj = new HashMap();
				int val = Integer.parseInt((String)prop.get("appliedVsUnapplied"));
				if(preVal != val) {
					preVal = val;
//					obj.put(String.valueOf(prop.get("ttime")), val);
					obj.put("key", prop.get("ttime"));
					obj.put("value", val);
					timeline.add(obj);
				}
			}
		}
		
		return timeline;
	}
	
	public List<Map<String, Object>> getTFTimeLine(DateRange dateRange, JSONObject criteriaObj) throws Exception {
		List<Map<String, Object>> timeline = new ArrayList<>();
		
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
//					    if (days.contains(new Long(start.getDayOfWeek().getValue())) || (intervals != null && !intervals.isEmpty())) {
//					    	if(intervals != null && !intervals.isEmpty()) {
					    		for (Object values : intervals.values()) {
					    			Map<String, Object> obj = new HashMap();
					    			Map<String, Object> obj1 = new HashMap();
				
					    			JSONArray interval = (JSONArray) values;
							    	if(interval != null && !interval.isEmpty()) {
							    		Long startTime = (long) (LocalTime.parse((CharSequence) interval.get(0)).toSecondOfDay()*1000);
								    	Long endTime = (long) (LocalTime.parse((CharSequence) interval.get(1)).toSecondOfDay()*1000);
//								    	obj.put(String.valueOf(start.toInstant().toEpochMilli()+startTime), 1);
//								    	obj1.put(String.valueOf(start.toInstant().toEpochMilli()+endTime), 0);
								    	obj.put("key", start.toInstant().toEpochMilli()+startTime);
								    	obj.put("value", 1);
								    	obj1.put("key", start.toInstant().toEpochMilli()+endTime);
								    	obj1.put("value", 0);
							    	}else {
//							    		obj.put(String.valueOf(DateTimeUtil.getDayStartTimeOf(start.toInstant().toEpochMilli(), false)), 1);
//								    	obj1.put(String.valueOf(DateTimeUtil.getDayEndTimeOf(start.toInstant().toEpochMilli(), false)), 0);
							    		obj.put("key", DateTimeUtil.getDayStartTimeOf(start.toInstant().toEpochMilli(), false));
								    	obj.put("value", 1);
								    	obj1.put("key", DateTimeUtil.getDayEndTimeOf(start.toInstant().toEpochMilli(), false));
								    	obj1.put("value", 0);
							    	}
							    	timeline.add(obj);
							    	timeline.add(obj1);
							    };
//					    	}
//					    }
					    start = start.plusDays(1);
					}  while (start.toEpochSecond() <= end.toEpochSecond());
				}
			}
		}
		return timeline;
	}
	
}