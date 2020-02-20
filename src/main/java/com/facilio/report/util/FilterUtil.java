package com.facilio.report.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
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
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.report.context.ReportFactory.ReportFacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class FilterUtil {
	private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(FilterUtil.class.getName());
	
	public static Criteria getTimeFilterCriteria(String moduleName, JSONObject criteriaObj) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Criteria criteria = new Criteria();
		
		JSONObject conditions = (JSONObject) criteriaObj.get("conditions");
		if(conditions != null && !conditions.isEmpty()) {
			for(Object key : conditions.keySet()) {
				JSONObject condition = (JSONObject)conditions.get((String)key);
				
				FacilioField timeField = modBean.getField(condition.get("field").toString(), moduleName);
				criteria.addAndCondition(getTimeFieldCondition(timeField, condition));
			}
		}
		return criteria;
	}
	public static Condition getTimeFieldCondition(FacilioField timeField, JSONObject conditionObj){
		String value = "";
		Object valueObj = conditionObj.get("value");
		if(valueObj!=null && valueObj instanceof JSONArray) {
			value = StringUtils.join((JSONArray)valueObj, ",");
		}
		else {
			value = (String)valueObj;
		}
		Operator operator = Operator.getOperator((int)(long)conditionObj.get("operatorId"));
		return CriteriaAPI.getCondition(timeField, value, operator);
	}
	
	public static void setDataFilterCriteria(String parrentModuleName, JSONObject criteriaObj, DateRange range, SelectRecordsBuilder<ModuleBaseWithCustomFields> parrentBuilder) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField timeField = modBean.getField("ttime", parrentModuleName);
		
		JSONObject conditions = (JSONObject) criteriaObj.get("conditions");
		if(conditions != null && !conditions.isEmpty()) {
			for(Object key : conditions.keySet()) {
				JSONObject condition = (JSONObject)conditions.get((String)key);
				
				GenericSelectRecordBuilder builder = getDFConditionSelectBuilder(condition, range);
						
				parrentBuilder.andCustomWhere(timeField.getCompleteColumnName() + " in (" + builder.constructSelectStatement() + ")");
			}
		}
	}
	
	public static GenericSelectRecordBuilder getDFConditionSelectBuilder(JSONObject conditionObj, DateRange range) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String moduleName = (String) conditionObj.get("moduleName");
		String field = (String) conditionObj.get("fieldName");
		Long parentId = (Long) conditionObj.get("parentId");
		
		FacilioField selectField = modBean.getField("ttime", moduleName);
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
		
		FacilioField orgIdField = AccountConstants.getOrgIdField(selectField.getModule());
		FacilioField moduleIdField = FieldFactory.getModuleIdField(selectField.getModule());
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(tableName)
				.select(Collections.singletonList(selectField))
				.andCondition(CriteriaAPI.getCondition(orgIdField, String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(moduleIdField, String.valueOf(selectField.getModuleId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(parentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(selectField, range.toString(), DateOperators.BETWEEN))
				.andCondition(CriteriaAPI.getCondition(conditionField, String.valueOf(value), operator));
		return builder;
		
	}
	public static List<ReportDataPointContext> getDFDataPoints(JSONObject criteriaObj) throws Exception {
		List<ReportDataPointContext> dataPoints = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		JSONObject conditions = (JSONObject) criteriaObj.get("conditions");
		if(conditions != null && !conditions.isEmpty()) {
			for(Object key : conditions.keySet()) {
				JSONObject condition = (JSONObject)conditions.get((String)key);
				key = "Cri_"+key;
				
				ReportDataPointContext dataPoint = new ReportDataPointContext();
				
				String moduleName = (String) condition.get("moduleName");
				String field = (String) condition.get("fieldName");
//				Long parentId = (Long) condition.get("parentId");
				
				FacilioField timeField = modBean.getField("ttime", moduleName);
//				FacilioField parentIdField = modBean.getField("parentId", moduleName);
				FacilioField conditionField = modBean.getField(field, moduleName);
				String value = "";
				if (conditionField instanceof NumberField) {
					NumberField numberField =  (NumberField)conditionField;
					Unit siUnit = Unit.valueOf(Metric.valueOf(numberField.getMetric()).getSiUnitId());
					value = String.valueOf(UnitsUtil.convertToSiUnit(condition.get("value"), siUnit));
				}else {
					value = (String) condition.get("value");
				}
				
				FacilioModule module = conditionField.getModule();
//				String tableName = module.getTableName();
				
//				String value = (String) condition.get("value");
//				int operatorId = ((Number) condition.get("operatorId")).intValue();
//				Operator operator = Operator.getOperator(operatorId);
				
//				Criteria dpCriteria = new Criteria();
//				dpCriteria.addAndCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(parentId), NumberOperators.EQUALS));
//				dpCriteria.addAndCondition(CriteriaAPI.getCondition(conditionField, value, operator));
//				dataPoint.setDpCriteria(dpCriteria);
				
				ReportFacilioField yField = getCriteriaField("appliedVsUnapplied", "appliedVsUnapplied", module, "CASE WHEN "+conditionField.getCompleteColumnName()+"="+value+" THEN 'true' ELSE 'false' END", FieldType.BOOLEAN);
				ReportYAxisContext yAxis = new ReportYAxisContext();
				yAxis.setField(module, yField);
				Map<Integer, Object> enumMap = new HashMap<>();
				enumMap.put(0, "False");
				enumMap.put(1, "True");
				yAxis.setEnumMap(enumMap);
				dataPoint.setyAxis(yAxis);
				
				dataPoint.setModuleName(moduleName);
				
				ReportFieldContext xAxis = new ReportFieldContext();
				xAxis.setField(module, timeField);
				dataPoint.setxAxis(xAxis);
				
				ReportFieldContext dateField = new ReportFieldContext();
				dateField.setField(module, timeField);
				dataPoint.setDateField(dateField);
				
				dataPoint.setName((String)key);
				
				Map<String, String> aliases = new HashMap<>();
				aliases.put("actual", (String)key);
				dataPoint.setAliases(aliases);
				
				List<String> orderBy = new ArrayList<>();
				orderBy.add(timeField.getCompleteColumnName());
//				dataPoint.setType(DataPointType.CRITERIA);
//				dataPoint.setOrderBy(orderBy);
				dataPoints.add(dataPoint);
			}
		}
		return dataPoints;
		
	}
	
	public static List<ReportDataPointContext> getTFDataPoints(JSONObject criteriaObj) throws Exception {
		List<ReportDataPointContext> dataPoints = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		JSONObject conditions = (JSONObject) criteriaObj.get("conditions");
		if(conditions != null && !conditions.isEmpty()) {
			for(Object key : conditions.keySet()) {
				JSONObject condition = (JSONObject)conditions.get((String)key);
				key = "Time Filter";
				
				ReportDataPointContext dataPoint = new ReportDataPointContext();
				
				String moduleName = (String) condition.get("moduleName");
				String field = (String) condition.get("fieldName");
//				Long parentId = (Long) condition.get("parentId");
				
				FacilioField timeField = modBean.getField("ttime", moduleName);
//				FacilioField parentIdField = modBean.getField("parentId", moduleName);
				FacilioField conditionField = modBean.getField(field, moduleName);
				String value = "";
				if (conditionField instanceof NumberField) {
					NumberField numberField =  (NumberField)conditionField;
					Unit siUnit = Unit.valueOf(Metric.valueOf(numberField.getMetric()).getSiUnitId());
					value = String.valueOf(UnitsUtil.convertToSiUnit(condition.get("value"), siUnit));
				}else {
					value = (String) condition.get("value");
				}
				
				FacilioModule module = conditionField.getModule();
//				String tableName = module.getTableName();
				
//				String value = (String) condition.get("value");
//				int operatorId = ((Number) condition.get("operatorId")).intValue();
//				Operator operator = Operator.getOperator(operatorId);
				
//				Criteria dpCriteria = new Criteria();
//				dpCriteria.addAndCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(parentId), NumberOperators.EQUALS));
//				dpCriteria.addAndCondition(CriteriaAPI.getCondition(conditionField, value, operator));
//				dataPoint.setDpCriteria(dpCriteria);
				
				ReportFacilioField yField = getCriteriaField("appliedVsUnapplied", "appliedVsUnapplied", module, "CASE WHEN "+conditionField.getCompleteColumnName()+"="+value+" THEN 'true' ELSE 'false' END", FieldType.BOOLEAN);
				ReportYAxisContext yAxis = new ReportYAxisContext();
				yAxis.setField(module, yField);
				Map<Integer, Object> enumMap = new HashMap<>();
				enumMap.put(0, "False");
				enumMap.put(1, "True");
				yAxis.setEnumMap(enumMap);
				dataPoint.setyAxis(yAxis);
				
				dataPoint.setModuleName(moduleName);
				
				ReportFieldContext xAxis = new ReportFieldContext();
				xAxis.setField(module, timeField);
				dataPoint.setxAxis(xAxis);
				
				ReportFieldContext dateField = new ReportFieldContext();
				dateField.setField(module, timeField);
				dataPoint.setDateField(dateField);
				
				dataPoint.setName((String)key);
				
				Map<String, String> aliases = new HashMap<>();
				aliases.put("actual", (String)key);
				dataPoint.setAliases(aliases);
				
				List<String> orderBy = new ArrayList<>();
				orderBy.add(timeField.getCompleteColumnName());
//				dataPoint.setType(DataPointType.CRITERIA);
//				dataPoint.setOrderBy(orderBy);
				dataPoints.add(dataPoint);
			}
		}
		return dataPoints;
		
	}
	
	public static ReportFacilioField getCriteriaField(String name, String displayName, FacilioModule module, String columnName, FieldType fieldType){
		ReportFacilioField field = new ReportFacilioField(-1);
		field.setName(name);
		field.setDisplayName(displayName);
		field.setModule(module);
		field.setGenericColumnName(columnName);
		field.setDataType(fieldType);
		return field;
	}
	
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