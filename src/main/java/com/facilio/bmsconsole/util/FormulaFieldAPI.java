package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.mysql.jdbc.ResultSetImpl;

public class FormulaFieldAPI {
	private static final Logger logger = LogManager.getLogger(ResultSetImpl.class.getName());
	public static long addEnPI (FormulaFieldContext enpi) throws Exception {
		enpi.setFormulaFieldType(FormulaFieldType.ENPI);
		return addFormulaField(enpi);
	}
	
	public static long addFormulaField (FormulaFieldContext formula) throws Exception {
		updateChildIds(formula);
		validateFormula(formula);
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
	
	private static void validateFormula (FormulaFieldContext field) throws Exception {
		if (field.getTriggerTypeEnum() == null) {
			throw new IllegalArgumentException("Trigger type cannot be null for FormulaFIeld");
		}
		if (field.getFormulaFieldTypeEnum() == null) {
			throw new IllegalArgumentException("Formula Field type cannot be null for FormulaFIeld");
		}
		if (field.getWorkflowId() == -1) {
			throw new IllegalArgumentException("Workflow ID cannot be null for FormulaField");
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
		
		if (field.getAssetCategoryId() == -1 && field.getResourceId() == -1) {
			throw new IllegalArgumentException("Both Cateogry and Resource cannot be null for Formula Field");
		}
		
		switch (field.getTriggerTypeEnum()) {
			case LIVE_READING:
				if (field.getInterval() == -1) {
					throw new IllegalArgumentException("Interval cannot be empty for 'READING' trigger type");
				}
				break;
			case SCHEDULE:
				if (field.getFrequencyEnum() == null) {
					throw new IllegalArgumentException("Frequency type cannot be empty for 'FREQUENCY' trigger type"); 
				}
				break;
		}
	}
	
	private static void updateChildIds(FormulaFieldContext enpi) throws Exception {
		long workflowId = WorkflowUtil.addWorkflow(enpi.getWorkflow());
		enpi.setWorkflowId(workflowId);
		enpi.setOrgId(AccountUtil.getCurrentOrg().getId());
		enpi.setReadingFieldId(enpi.getReadingField().getId());
	}
	
	public static FormulaFieldContext getENPI(long enpiId) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getFormulaFieldFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(enpiId, module))
														;
		
		List<FormulaFieldContext> enpiList = getFormulaFieldsFromProps(selectBuilder.get());
		if (enpiList != null && !enpiList.isEmpty()) {
			return enpiList.get(0);
		}
		return null;
	}
	
	public static List<ReadingContext> calculateFormulaReadings(long resourceId, String fieldName, Map<Long, Long> intervalMap, WorkflowContext workflow) throws Exception {
		if (intervalMap != null && !intervalMap.isEmpty()) {
			List<ReadingContext> readings = new ArrayList<>();
			for(Map.Entry<Long, Long> map:intervalMap.entrySet()) {
				long iStartTime = map.getKey();
				long iEndTime = map.getValue();
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
						reading.setTtime(iEndTime);
						readings.add(reading);
					}
				}
				catch (Exception e) {
					logger.log(Level.ERROR, e.getMessage(), e);
					if (e.getMessage() == null || !(e.getMessage().contains("Division by zero") || e.getMessage().contains("Division undefined")  || e.getMessage().contains("/ by zero"))) {
						CommonCommandUtil.emailException("EnPI calculation failed for : "+fieldName+" between "+iStartTime+" and "+iEndTime, e);
					}
				}
			}
			return readings;
		}
		return null;
	}
	
	public static List<FormulaFieldContext> getAllENPIs() throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		FacilioField formulaType = FieldFactory.getAsMap(fields).get("formulaFieldType");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(formulaType, String.valueOf(FormulaFieldType.ENPI.getValue()), NumberOperators.EQUALS))
														;
		
		return getFormulaFieldsFromProps(selectBuilder.get());
		
	}
	
	public static List<FormulaFieldContext> getScheduledFormulasOfFrequencyType(List<Integer> types) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField frequencyField = fieldMap.get("frequency");
		FacilioField triggerType = fieldMap.get("triggerType");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(triggerType, String.valueOf(TriggerType.SCHEDULE.getValue()), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(frequencyField, StringUtils.join(types, ","), NumberOperators.EQUALS))
														;
		
		return getFormulaFieldsFromProps(selectBuilder.get());
		
	}
	
	public static void recalculateHistoricalData(FormulaFieldContext enpi, FacilioField enpiField) throws Exception {
		ModuleCRUDBean crudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
		crudBean.deleteAllData(enpiField.getModule().getName());
		
		FacilioTimer.deleteJob(enpi.getId(), "HistoricalFormulaFieldCalculator");
		FacilioTimer.scheduleOneTimeJob(enpi.getId(), "HistoricalFormulaFieldCalculator", 30, "priority");
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

}
