package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.mysql.jdbc.ResultSetImpl;

public class FormulaFieldAPI {
	private static final Logger LOGGER = LogManager.getLogger(ResultSetImpl.class.getName());
	public static long addFormulaField (FormulaFieldContext formula) throws Exception {
		updateChildIds(formula);
		validateFormula(formula, true);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormulaFieldModule().getTableName())
				.fields(FieldFactory.getFormulaFieldFields())
				;
		long id = insertBuilder.insert(FieldUtil.getAsProperties(formula));
		if (id == -1) {
			throw new RuntimeException("Unable to add Formula Field");
		}
		else {
			formula.setId(id);
		}
		addInclusions(formula);
		return formula.getId();
	}
	
	private static void addInclusions(FormulaFieldContext field) throws SQLException, RuntimeException {
		if (field.getResourceTypeEnum() != ResourceType.ONE_RESOURCE && field.getIncludedResources() != null && !field.getIncludedResources().isEmpty()) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getFormulaFieldInclusionsModule().getTableName())
														.fields(FieldFactory.getFormulaFieldInclusionsFields())
														.addRecords(getInclusionList(field.getId(), field.getIncludedResources()));
			insertBuilder.save();
		}
	}
	
	private static List<Map<String, Object>> getInclusionList(long formulaId, List<Long> resources) {
		long orgId = AccountUtil.getCurrentOrg().getId();
		List<Map<String, Object>> props = new ArrayList<>();
		for (Long resourceId : resources) {
			Map<String, Object> prop = new HashMap<>();
			prop.put("orgId", orgId);
			prop.put("formulaId", formulaId);
			prop.put("resourceId", resourceId);
			
			props.add(prop);
		}
		return props;
	}
	
	public static void validateFormula (FormulaFieldContext field, boolean checkChildIds) throws Exception {
		if (field.getTriggerTypeEnum() == null) {
			throw new IllegalArgumentException("Trigger type cannot be null for FormulaField");
		}
		if (field.getFormulaFieldTypeEnum() == null) {
			throw new IllegalArgumentException("Formula Field type cannot be null for FormulaField");
		}
		if (field.getResourceTypeEnum() == null) {
			throw new IllegalArgumentException("Resource Type cannot be null for FormulaField");
		}
		
		switch(field.getResourceTypeEnum()) {
			case ONE_RESOURCE:
				if (field.getResourceId() == -1) {
					throw new IllegalArgumentException("Resource ID cannot be null for FormulaField of type 'ONE_RESOURCE'");
				}
				break;
			case ASSET_CATEGORY:
				if (field.getAssetCategoryId() == -1) {
					throw new IllegalArgumentException("Asset Category ID cannot be null for FormulaField of type 'ASSET_CATEGORY'");
				}
				break;
			case SPACE_CATEGORY:
				if (field.getSpaceCategoryId() == -1) {
					throw new IllegalArgumentException("Space Category ID cannot be null for FormulaField of type 'SPACE_CATEGORY'");
				}
				break;
			default:
				break;
		}
		
		switch (field.getTriggerTypeEnum()) {
			case LIVE_READING:
				if (field.getInterval() == -1) {
					throw new IllegalArgumentException("Interval cannot be empty for 'LIVE_READING' trigger type");
				}
				if (field.getInterval() > (24 * 60)) {
					throw new IllegalArgumentException("Interval cannot be more than 1440 minutes (1 day) for 'LIVE_READING' trigger type");
				}
				break;
			case SCHEDULE:
				if (field.getFrequencyEnum() == null) {
					throw new IllegalArgumentException("Frequency type cannot be empty for 'FREQUENCY' trigger type"); 
				}
				else if (field.getFrequencyEnum() == FacilioFrequency.DO_NOT_REPEAT || field.getFrequencyEnum() == FacilioFrequency.CUSTOM) {
					throw new IllegalArgumentException("Invalid Frequency for 'SCHEDULE' trigger type");
				}
				break;
		}
		
		if (checkChildIds) {
			if (field.getWorkflowId() == -1) {
				throw new IllegalArgumentException("Workflow ID cannot be null for FormulaField");
			}
		}
	}
	
	private static void updateChildIds(FormulaFieldContext formula) throws Exception {
		long workflowId = WorkflowUtil.addWorkflow(formula.getWorkflow());
		formula.setWorkflowId(workflowId);
		formula.setOrgId(AccountUtil.getCurrentOrg().getId());
		formula.setReadingFieldId(formula.getReadingField().getId());
		formula.setActive(true);
	}
	
	public static FormulaFieldContext getFormulaField(long id) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getFormulaFieldFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		
		List<FormulaFieldContext> enpiList = getFormulaFieldsFromProps(selectBuilder.get());
		if (enpiList != null && !enpiList.isEmpty()) {
			return enpiList.get(0);
		}
		return null;
	}
	
	public static FormulaFieldContext getFormulaField (FacilioField readingField) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		FacilioField readingFieldId = FieldFactory.getAsMap(fields).get("readingFieldId");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(readingFieldId, String.valueOf(readingField.getFieldId()), PickListOperators.IS))
														;
		
		List<FormulaFieldContext> enpiList = getFormulaFieldsFromProps(selectBuilder.get());
		if (enpiList != null && !enpiList.isEmpty()) {
			return enpiList.get(0);
		}
		return null;
	}
	
	public static List<ReadingContext> calculateFormulaReadings(long resourceId, String fieldName, List<Pair<Long, Long>> intervals, WorkflowContext workflow) throws Exception {
		if (intervals != null && !intervals.isEmpty()) {
			List<ReadingContext> readings = new ArrayList<>();
			for(Pair<Long, Long> interval : intervals) {
				long iStartTime = interval.getLeft();
				long iEndTime = interval.getRight();
				try {
					Map<String, Object> params = new HashMap<>();
					params.put("startTime", iStartTime);
					params.put("endTime", iEndTime);
					params.put("resourceId", resourceId);
					
					if (workflow.getWorkflowString() == null) {
						workflow.setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(workflow));
					}
					Double resultVal = (Double) WorkflowUtil.getWorkflowExpressionResult(workflow.getWorkflowString(), params, false);
					
					if (resultVal != null) {
						ReadingContext reading = new ReadingContext();
						reading.setParentId(resourceId);
						reading.addReading(fieldName, resultVal);
						reading.addReading("startTime", iStartTime);
						reading.setTtime(iEndTime);
						readings.add(reading);
					}
				}
				catch (Exception e) {
					LOGGER.log(Level.ERROR, e.getMessage(), e);
					if (e.getMessage() == null || !(e.getMessage().contains("Division by zero") || e.getMessage().contains("Division undefined")  || e.getMessage().contains("/ by zero"))) {
						CommonCommandUtil.emailException("FormulaFieldAPI", "Formula calculation failed for : "+fieldName+" between "+iStartTime+" and "+iEndTime, e);
					}
				}
			}
			return readings;
		}
		return null;
	}
	
	public static List<FormulaFieldContext> getAllFormulaFieldsOfType(FormulaFieldType type) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		FacilioField formulaType = FieldFactory.getAsMap(fields).get("formulaFieldType");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(formulaType, String.valueOf(type.getValue()), NumberOperators.EQUALS))
														;
		
		return getFormulaFieldsFromProps(selectBuilder.get());
	}
	
	public static List<FormulaFieldContext> getActiveScheduledFormulasOfFrequencyType(List<Integer> types) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField frequencyField = fieldMap.get("frequency");
		FacilioField triggerType = fieldMap.get("triggerType");
		FacilioField active = fieldMap.get("active");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(triggerType, String.valueOf(TriggerType.SCHEDULE.getValue()), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(frequencyField, StringUtils.join(types, ","), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(active, String.valueOf(true), BooleanOperators.IS))
														;
		
		return getFormulaFieldsFromProps(selectBuilder.get());
		
	}
	
	public static List<FormulaFieldContext> getActiveFormulasDependingOnFields (TriggerType triggerType, Collection<Long> fieldIds) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField triggerTypeField = fieldMap.get("triggerType");
		FacilioField active = fieldMap.get("active");
				
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getCondition(triggerTypeField, String.valueOf(triggerType.getValue()), NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition(active, String.valueOf(true), BooleanOperators.IS))
													.andCustomWhere("WORKFLOW_ID IN (SELECT WORKFLOW_ID FROM Workflow_Field WHERE ORGID = ? AND FIELD_ID IN ("+StringUtils.join(fieldIds, ",")+"))", AccountUtil.getCurrentOrg().getId())
													;

		return getFormulaFieldsFromProps(selectBuilder.get());
	}
	
	public static void recalculateHistoricalData(FormulaFieldContext enpi, FacilioField enpiField, DateRange range) throws Exception {
		BmsJobUtil.deleteJobWithProps(enpi.getId(), "HistoricalFormulaFieldCalculator");
		BmsJobUtil.scheduleOneTimeJobWithProps(enpi.getId(), "HistoricalFormulaFieldCalculator", 30, "priority", FieldUtil.getAsJSON(range));
	}
	
	private static List<FormulaFieldContext> getFormulaFieldsFromProps (List<Map<String, Object>> props) throws Exception {
		if( props != null && !props.isEmpty()) {
			List<FormulaFieldContext> enpiList = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Long> workflowIds = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				FormulaFieldContext formula = FieldUtil.getAsBeanFromMap(prop, FormulaFieldContext.class);
				formula.setReadingField(modBean.getField(formula.getReadingFieldId()));
				enpiList.add(formula);
				workflowIds.add(formula.getWorkflowId());
				fetchInclusions(formula);
				fetchMatchedResources(formula);
			}
			
			Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workflowIds, true);
			Map<Long, List<Long>> dependentFieldMap = WorkflowUtil.getDependentFieldsIdsAsMap(workflowIds);
			for (FormulaFieldContext enpi : enpiList) {
				WorkflowContext workflow = workflowMap.get(enpi.getWorkflowId());
				if (dependentFieldMap != null && !dependentFieldMap.isEmpty()) {
					workflow.setDependentFieldIds(dependentFieldMap.get(workflow.getId()));
				}
				enpi.setWorkflow(workflow);
			}
			
			return enpiList;
		}
		return null;
	}
	
	private static void fetchMatchedResources (FormulaFieldContext field) throws Exception {
		if (field.getResourceTypeEnum() == ResourceType.ONE_RESOURCE) {
			field.setMatchedResources(Collections.singletonList(field.getResourceId()));
			return;
		}
		else {
			List<? extends ResourceContext> matchedResources = null;
			if (field.getIncludedResources() != null && !field.getIncludedResources().isEmpty()) {
				matchedResources = ResourceAPI.getResources(field.getIncludedResources(), false);
			}
			else {
				switch (field.getResourceTypeEnum()) {
					case ALL_SITES:
						matchedResources = SpaceAPI.getAllSites();
						break;
					case ALL_BUILDINGS:
						matchedResources = SpaceAPI.getAllBuildings();
						break;
					case ALL_FLOORS:
						matchedResources = SpaceAPI.getAllFloors();
						break;
					case SPACE_CATEGORY:
						matchedResources = SpaceAPI.getSpaceListOfCategory(field.getSpaceCategoryId());
						break;
					case ASSET_CATEGORY:
						matchedResources = AssetsAPI.getAssetListOfCategory(field.getAssetCategoryId());
						break;	
					default:
						break;
				}
			}
			if (matchedResources != null && !matchedResources.isEmpty()) {
				List<Long> resourceIds = matchedResources.stream().map(ResourceContext::getId).collect(Collectors.toList());
				field.setMatchedResources(resourceIds);
			}
		}
	}
	
	private static void fetchInclusions (FormulaFieldContext formula) throws Exception {
		
		if (formula.getResourceTypeEnum() != ResourceType.ONE_RESOURCE) {
			FacilioModule module = ModuleFactory.getFormulaFieldInclusionsModule();
			List<FacilioField> fields = FieldFactory.getFormulaFieldInclusionsFields();
			FacilioField formulaId = FieldFactory.getAsMap(fields).get("formulaId");
			
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																	.table(module.getTableName())
																	.select(fields)
																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCondition(CriteriaAPI.getCondition(formulaId, String.valueOf(formula.getId()), PickListOperators.IS));
			
			List<Map<String, Object>> props = selectRecordBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<Long> includedResources = new ArrayList<>();
				for (Map<String, Object> prop : props) {
					includedResources.add((Long) prop.get("resourceId"));
				}
				formula.setIncludedResources(includedResources);
			}
		}
	}
	
	public static ScheduleInfo getSchedule (FacilioFrequency frequency) {
		ScheduleInfo schedule = null;
		List<Integer> values = null;
		switch (frequency) {
			case HOURLY:
					schedule = new ScheduleInfo();
					for (int i = 0; i < 24; i++) {
						LocalTime time = LocalTime.of(i, 00);
						schedule.addTime(time);
					}
					schedule.setFrequencyType(FrequencyType.DAILY);
					return schedule;
		    case DAILY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.DAILY);
					return schedule;
			case WEEKLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.WEEKLY);
					values = new ArrayList<>();
					values.add(DateTimeUtil.getWeekFields().getFirstDayOfWeek().getValue());
					schedule.setValues(values);
					return schedule;
			case MONTHLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.MONTHLY_DAY);
					values = new ArrayList<>();
					values.add(1);
					schedule.setValues(values);
					return schedule;
			case QUARTERTLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.YEARLY);
					schedule.setYearlyDayValue(1);
					values = new ArrayList<>();
					values.add(1);
					values.add(4);
					values.add(7);
					values.add(10);
					schedule.setValues(values);
					return schedule;
			case HALF_YEARLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.YEARLY);
					schedule.setYearlyDayValue(1);
					values = new ArrayList<>();
					values.add(1);
					values.add(7);
					schedule.setValues(values);
					return schedule;
			case ANNUALLY:
					schedule = new ScheduleInfo();
					schedule.addTime("00:00");
					schedule.setFrequencyType(FrequencyType.YEARLY);
					schedule.setYearlyDayValue(1);
					values = new ArrayList<>();
					values.add(1);
					schedule.setValues(values);
					return schedule;
			default:
					return null;
		}
	}
	
	public static long getStartTimeForHistoricalCalculation(FormulaFieldContext enpi) {
		switch (enpi.getFrequencyEnum()) {
			case HOURLY:
				return DateTimeUtil.getMonthStartTime();
			case DAILY:
				return DateTimeUtil.getDayStartTime(-90);
			case WEEKLY:
			case MONTHLY:
				return DateTimeUtil.getMonthStartTime(-12);
			case HALF_YEARLY:
			case QUARTERTLY:
				return DateTimeUtil.getYearStartTime(-1);
			default:
				return -1;
		}
	}
	
	public static ModuleType getModuleTypeFromTrigger(TriggerType type) {
		switch (type) {
			case SCHEDULE:
				return ModuleType.SCHEDULED_FORMULA;
			case LIVE_READING:
				return ModuleType.LIVE_FORMULA;
			default:
				return null;
		}
	}

}
