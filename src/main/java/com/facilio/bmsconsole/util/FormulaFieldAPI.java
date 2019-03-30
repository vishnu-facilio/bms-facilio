package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.*;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.workflows.context.*;
import com.facilio.workflows.util.ExpressionAggregateOperator;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class FormulaFieldAPI {
	private static final Logger LOGGER = LogManager.getLogger(FormulaFieldAPI.class.getName());
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
			case PRE_LIVE_READING:
				if (field.getModuleName() == null || field.getModuleName().isEmpty()) {
					throw new IllegalArgumentException("Module Name cannot be empty for 'PRE_LIVE_READING'");
				}
				//Check for Workflow Fields. It should be empty
				break;
			case POST_LIVE_READING:
				if (field.getInterval() == -1) {
					throw new IllegalArgumentException("Interval cannot be empty for 'POST_LIVE_READING' trigger type");
				}
				if (field.getInterval() > (24 * 60)) {
					throw new IllegalArgumentException("Interval cannot be more than 1440 minutes (1 day) for 'POST_LIVE_READING' trigger type");
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
			if (field.getModuleId() == -1) {
				throw new IllegalArgumentException("Module ID cannot be null for FormulaField");
			}
			if (field.getReadingFieldId() == -1) {
				throw new IllegalArgumentException("Reading Field ID cannot be null for FormulaField");
			}
			if (field.getWorkflowId() == -1) {
				throw new IllegalArgumentException("Workflow ID cannot be null for FormulaField");
			}
		}
	}
	
	private static void updateChildIds(FormulaFieldContext formula) throws Exception {
		long workflowId = WorkflowUtil.addWorkflow(formula.getWorkflow());
		formula.setWorkflowId(workflowId);
		formula.setWorkflow(null);
		formula.setOrgId(AccountUtil.getCurrentOrg().getId());
		formula.setModuleId(formula.getReadingField().getModule().getModuleId());
		formula.setReadingFieldId(formula.getReadingField().getId());
		formula.setReadingField(null);
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
		
		List<FormulaFieldContext> enpiList = getFormulaFieldsFromProps(selectBuilder.get(), false);
		if (enpiList != null && !enpiList.isEmpty()) {
			return enpiList.get(0);
		}
		return null;
	}
	
	public static FormulaFieldContext getFormulaField (FacilioField readingField) throws Exception {
		return getFormulaFieldFromReadingField(readingField.getFieldId());
	}
	
	public static FormulaFieldContext getFormulaFieldFromReadingField (Long readingFieldId) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		FacilioField readingField = FieldFactory.getAsMap(fields).get("readingFieldId");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(readingField, String.valueOf(readingFieldId), PickListOperators.IS))
														;
		
		List<FormulaFieldContext> enpiList = getFormulaFieldsFromProps(selectBuilder.get(), false);
		if (enpiList != null && !enpiList.isEmpty()) {
			return enpiList.get(0);
		}
		return null;
	}
	
	public static List<ReadingContext> calculateFormulaReadings(long resourceId, String moduleName, String fieldName, List<DateRange> intervals, WorkflowContext workflow, boolean ignoreNullValues, boolean addValue) throws Exception {
		if (intervals != null && !intervals.isEmpty()) {
			intervals.get(0).getStartTime();
			intervals.get(intervals.size() - 1).getEndTime();
			
			List<ReadingContext> readings = new ArrayList<>();
			for(DateRange interval : intervals) {
				long iStartTime = interval.getStartTime();
				long iEndTime = interval.getEndTime();
				try {
					long startTime = System.currentTimeMillis();
					Map<String, Object> params = new HashMap<>();
					params.put("startTime", iStartTime);
					params.put("endTime", iEndTime);
					params.put("resourceId", resourceId);
					params.put("currentModule", moduleName);
					params.put("currentField", fieldName);
					
					if (workflow.getWorkflowString() == null) {
						workflow.setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(workflow));
					}
					Object workflowResult = WorkflowUtil.getWorkflowExpressionResult(workflow.getWorkflowString(), params, null, ignoreNullValues, false);
					if(workflowResult != null) {
						Double resultVal = Double.parseDouble(workflowResult.toString());
//						if (AccountUtil.getCurrentOrg().getId() == 135) {
							LOGGER.debug("Result of Formula : "+fieldName+" for resource : "+resourceId+" : "+resultVal);
//						}
						if (resultVal != null) {
							ReadingContext reading = new ReadingContext();
							reading.setParentId(resourceId);
							reading.addReading(fieldName, resultVal);
							reading.addReading("startTime", iStartTime);
							reading.setTtime(iEndTime);
							readings.add(reading);
							
							if (addValue) {
								FacilioContext context = new FacilioContext();
								context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
								context.put(FacilioConstants.ContextNames.READING, reading);
		//						context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
								context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
								
								Chain addReadingChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
								addReadingChain.execute(context);
							}
						}
					}
					long timeTaken = System.currentTimeMillis() - startTime;
//					if (AccountUtil.getCurrentOrg().getId() == 135) {
						LOGGER.debug("Time taken for Formula calculation of : "+fieldName+" between "+iStartTime+" and "+iEndTime+" : "+timeTaken);
//					}
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
	
	public static List<FormulaFieldContext> getAllFormulaFieldsOfType(FormulaFieldType type, boolean fetchResources) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		FacilioField formulaType = FieldFactory.getAsMap(fields).get("formulaFieldType");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(formulaType, String.valueOf(type.getValue()), NumberOperators.EQUALS))
														;
		
		return getFormulaFieldsFromProps(selectBuilder.get(), fetchResources);
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
		
		return getFormulaFieldsFromProps(selectBuilder.get(), false);
		
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

		return getFormulaFieldsFromProps(selectBuilder.get(), false);
	}
	
	public static Map<String, List<FormulaFieldContext>> getActivePreFormulasOfModule(Collection<String> moduleNames) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> moduleIds = new ArrayList<>();
		for (String moduleName : moduleNames) {
			FacilioModule mod = modBean.getModule(moduleName);
			moduleIds.add(mod.getModuleId());
		}
		
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField triggerTypeField = fieldMap.get("triggerType");
		FacilioField moduleIdField = fieldMap.get("moduleId");
		FacilioField active = fieldMap.get("active");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(triggerTypeField, String.valueOf(TriggerType.PRE_LIVE_READING.getValue()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(active, String.valueOf(true), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition(moduleIdField, moduleIds, PickListOperators.IS))
				.orderBy("ID")
				;

		List<FormulaFieldContext> formulas = getFormulaFieldsFromProps(selectBuilder.get(), false);
		if (formulas != null && !formulas.isEmpty()) {
			Map<String, List<FormulaFieldContext>> formulaMap = new HashMap<>();
			for (FormulaFieldContext formula : formulas) {
				List<FormulaFieldContext> formulaList = formulaMap.get(formula.getModuleName());
				if (formulaList == null) {
					formulaList = new ArrayList<>();
					formulaMap.put(formula.getModuleName(), formulaList);
				}
				formulaList.add(formula);
			}
			return formulaMap;
		}
		return null;
	}
	
	public static void recalculateHistoricalData(long formulaId, DateRange range) throws Exception {
		BmsJobUtil.deleteJobWithProps(formulaId, "HistoricalFormulaFieldCalculator");
		BmsJobUtil.scheduleOneTimeJobWithProps(formulaId, "HistoricalFormulaFieldCalculator", 30, "priority", FieldUtil.getAsJSON(range));
	}
	
	public static void calculateHistoricalDataForSingleResource(long formulaId, long resourceId, DateRange range, boolean isSystem, boolean historicalAlarm) throws Exception {
		calculateHistoricalDataForSingleResource(formulaId, resourceId, range, isSystem, historicalAlarm,false);
	}
	
	public static void calculateHistoricalDataForSingleResource(long formulaId, long resourceId, DateRange range, boolean isSystem, boolean historicalAlarm,boolean skipOptimisedWorkflow) throws Exception {
		Map<String, Object> prop = getFormulaFieldResourceJob(formulaId, resourceId, isSystem);
		long id = -1;
		if (prop == null) {
			prop = new HashMap<>();
			prop.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
			prop.put("formulaId", formulaId);
			prop.put("resourceId", resourceId);
			prop.put("startTime", range.getStartTime());
			prop.put("endTime", range.getEndTime());
			prop.put("isSystem", isSystem);
			prop.put("historicalAlarm", historicalAlarm);
			prop.put("skipOptimisedWorkflow", skipOptimisedWorkflow);
			id = addFormulaFieldResourceJob(prop);
		}
		else {
			id = (long) prop.get("id");
			updateFormulaFieldResourceJob(id, range.getStartTime(), range.getEndTime(), historicalAlarm);
			FacilioTimer.deleteJob(id, "SingleResourceHistoricalFormulaFieldCalculator");
		}
		FacilioTimer.scheduleOneTimeJob(id, "SingleResourceHistoricalFormulaFieldCalculator", 30, "priority");
	}
	
	private static long addFormulaFieldResourceJob(Map<String, Object> prop) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getFormulaFieldResourceJobModule().getTableName())
														.fields(FieldFactory.getFormulaFieldResourceJobFields())
														;
		return insertBuilder.insert(prop);
	}
	
	private static void updateFormulaFieldResourceJob (long id, long startTime, long endTime, boolean historicalAlarm) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldResourceJobModule();
		Map<String, Object> prop = new HashMap<>();
		prop.put("startTime", startTime);
		prop.put("endTime", endTime);
		prop.put("historicalAlarm", historicalAlarm);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(FieldFactory.getFormulaFieldResourceJobFields())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		updateBuilder.update(prop);
	}
	
	private static Map<String, Object> getFormulaFieldResourceJob(long formulaId, long resourceId, boolean isSystem) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldResourceJobModule();
		List<FacilioField> fields = FieldFactory.getFormulaFieldResourceJobFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField formulaIdField = fieldMap.get("formulaId");
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField isSystemField = fieldMap.get("isSystem");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(formulaIdField, String.valueOf(formulaId), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(resourceId), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(isSystemField, String.valueOf(isSystem), BooleanOperators.IS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}
	
	public static Map<String, Object> getFormulaFieldResourceJob(long id) throws Exception {
		FacilioModule module = ModuleFactory.getFormulaFieldResourceJobModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(FieldFactory.getFormulaFieldResourceJobFields())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}
	
	private static List<FormulaFieldContext> getFormulaFieldsFromProps (List<Map<String, Object>> props, boolean fetchResources) throws Exception {
		if( props != null && !props.isEmpty()) {
			List<FormulaFieldContext> formulaList = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Long> workflowIds = new ArrayList<>();
			List<Long> resourceIds = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				FormulaFieldContext formula = FieldUtil.getAsBeanFromMap(prop, FormulaFieldContext.class);
				formula.setReadingField(modBean.getField(formula.getReadingFieldId()));
				formulaList.add(formula);
				workflowIds.add(formula.getWorkflowId());
				fetchInclusions(formula);
				fetchMatchedResources(formula, fetchResources);
				FacilioModule module = modBean.getModule(formula.getModuleId());
				formula.setModuleName(module.getName());
				if (fetchResources && formula.getResourceId() != -1) {
					resourceIds.add(formula.getResourceId());
				}
			}
			
			Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workflowIds, true);
			Map<Long, List<Long>> dependentFieldMap = WorkflowUtil.getDependentFieldsIdsAsMap(workflowIds);
			
			Map<Long, ResourceContext> resourceMap = null;
			if (fetchResources && !resourceIds.isEmpty()) {
				resourceMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
			}
			
			for (FormulaFieldContext formula : formulaList) {
				WorkflowContext workflow = workflowMap.get(formula.getWorkflowId());
				if (dependentFieldMap != null && !dependentFieldMap.isEmpty()) {
					workflow.setDependentFieldIds(dependentFieldMap.get(workflow.getId()));
				}
				formula.setWorkflow(workflow);
				if (fetchResources && formula.getResourceId() != -1) {
					formula.setMatchedResources(Collections.singletonList(resourceMap.get(formula.getResourceId())));
				}
			}
			
			return formulaList;
		}
		return null;
	}
	
	private static void fetchMatchedResources (FormulaFieldContext field, boolean fetchResources) throws Exception {
		if (field.getResourceTypeEnum() == ResourceType.ONE_RESOURCE) {
			field.setMatchedResourcesIds(Collections.singletonList(field.getResourceId()));
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
				field.setMatchedResourcesIds(resourceIds);
				if (fetchResources) {
					field.setMatchedResources(matchedResources);
				}
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
				//return DateTimeUtil.getDayStartTime(-90);
				return DateTimeUtil.getYearStartTime(); //Temp for Al Seef
			case WEEKLY:
			case MONTHLY:
				return DateTimeUtil.getMonthStartTime(-6); //Temp for Al Seef
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
			case PRE_LIVE_READING:
			case POST_LIVE_READING:
				return ModuleType.LIVE_FORMULA;
			default:
				return null;
		}
	}
	
	private static boolean dependsOnSameModule(FormulaFieldContext formula) throws Exception {
		List<ParameterContext> parameters = formula.getWorkflow().getParameters();
		if (parameters != null && !parameters.isEmpty()) {
			for (ParameterContext parameter : parameters) {
				if ("currentField".equals(parameter.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void historicalCalculation(FormulaFieldContext formula, DateRange range, boolean historicalAlarm) throws Exception {
		historicalCalculation(formula, range, -1, true, historicalAlarm);
	}
	
	private static boolean isAllWorkflowFieldsAggregationIsLastVal(WorkflowContext workflow) throws Exception {
		List<WorkflowFieldContext> fields = WorkflowUtil.getWorkflowFields(workflow.getId());
		
		if (fields != null) {
			for (WorkflowFieldContext field : fields) {
				if (field.getAggregationEnum() != ExpressionAggregateOperator.LAST_VALUE) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void optimisedHistoricalCalculation(FormulaFieldContext formula, DateRange range, long singleResourceId, boolean isSystem, boolean historicalAlarm) throws Exception {
		
		if (formula.getTriggerTypeEnum() != TriggerType.POST_LIVE_READING) {
			throw new IllegalArgumentException("Currently only Live reading is supported for optimised historical calculation");
		}
		
		if (dependsOnSameModule(formula)) {
			LOGGER.debug("Calculating the usual way instead of optimised, since formula is depending on itself");
			historicalCalculation(formula, range, singleResourceId, isSystem, historicalAlarm);
		}
		
		if (!isAllWorkflowFieldsAggregationIsLastVal(formula.getWorkflow())) {
			LOGGER.debug("Calculating the usual way instead of optimised, since formula depends on fields who's aggregation is not 'lastValue'");
			historicalCalculation(formula, range, singleResourceId, isSystem, historicalAlarm);
		}
		
		List<DateRange> intervals = getIntervals(formula, range);
		LOGGER.debug(intervals);
		if (intervals != null && !intervals.isEmpty()) {
			List<ReadingContext> readings = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (singleResourceId != -1) {
				LOGGER.debug("Gonna perform optmised historical calculation for formula : "+formula.getId()+" for resource : "+singleResourceId);
				if (formula.getMatchedResourcesIds().contains(singleResourceId)) {
					LOGGER.debug("Matched");
					long workflowStarttime = System.currentTimeMillis();
					OptimisedFormulaCalculationWorkflow optimisedWorkflow = constructOptimisedWorkflowForHistoricalCalculation(formula.getWorkflow());
					if(AccountUtil.getCurrentOrg().getId() == 191l) {
						LOGGER.error("optimisedWorkflow to String -- "+ WorkflowUtil.getXmlStringFromWorkflow(optimisedWorkflow));
					}
					LOGGER.debug("Time taken to generate optimised workflow : "+(System.currentTimeMillis() - workflowStarttime));
					int deletedData = deleteOlderData(range.getStartTime(), range.getEndTime(), Collections.singletonList(singleResourceId), formula.getReadingField().getModule().getName());
					LOGGER.debug("Deleted rows for formula : "+formula.getName()+" between "+range+" is : "+deletedData);
					Set<Object> xValues = new TreeSet<>(); 
					long independentDataStarttime = System.currentTimeMillis();
					Map<String,Object> wfParams = fetchIndependentParams(optimisedWorkflow.getMetas(), range, modBean, xValues);
					LOGGER.debug("Time taken to fetch independent data : "+(System.currentTimeMillis() - independentDataStarttime));
					List<ReadingContext> currentReadings = computeOptimisedWorkflow(formula.getReadingField().getName(), optimisedWorkflow, range, wfParams, xValues, singleResourceId, modBean);
					if (currentReadings != null && !currentReadings.isEmpty()) {
						readings.addAll(currentReadings);
					}
				}
			}
			else {
				OptimisedFormulaCalculationWorkflow optimisedWorkflow = constructOptimisedWorkflowForHistoricalCalculation(formula.getWorkflow());
				int deletedData = deleteOlderData(range.getStartTime(), range.getEndTime(), formula.getMatchedResourcesIds(), formula.getReadingField().getModule().getName());
				LOGGER.debug("Deleted rows for formula : "+formula.getName()+" between "+range+" is : "+deletedData);
				Set<Object> xValues = new TreeSet<>(); 
				Map<String,Object> wfParams = fetchIndependentParams(optimisedWorkflow.getMetas(), range, modBean, xValues);
				for (Long resourceId : formula.getMatchedResourcesIds()) {
					List<ReadingContext> currentReadings = computeOptimisedWorkflow(formula.getReadingField().getName(), optimisedWorkflow, range, wfParams, xValues, resourceId, modBean);
					if (currentReadings != null && !currentReadings.isEmpty()) {
						readings.addAll(currentReadings);
					}
				}
			}
			
			LOGGER.debug("Historical Data to be added for formula "+readings.size());
			if (!readings.isEmpty()) {
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.MODULE_NAME, formula.getReadingField().getModule().getName());
				context.put(FacilioConstants.ContextNames.READINGS, readings);
				context.put(FacilioConstants.ContextNames.HISTORY_READINGS, !historicalAlarm);
				context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
				
				Chain addReadingChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
				addReadingChain.execute(context);
			}
		}
	}
	
	private static List<ReadingContext> computeOptimisedWorkflow(String fieldName, OptimisedFormulaCalculationWorkflow workflow, DateRange range, Map<String,Object> wfParams, Set<Object> xValues, long resourceId, ModuleBean modBean) throws Exception {
		Map<String, Object> params = new HashMap<>(wfParams);
		Set<Object> currentxValues = new TreeSet<>(xValues);
		long resourceParamsStarttime = System.currentTimeMillis();
		params.putAll(fetchResourceParams(resourceId, workflow.getMetas(), range, modBean, currentxValues));
		LOGGER.debug("Time taken to fetch resource params : "+(System.currentTimeMillis() - resourceParamsStarttime));
		params.put("xValues", currentxValues);
		String wfXmlString = WorkflowUtil.getXmlStringFromWorkflow(workflow);
		LOGGER.debug("Optimised wfXmlString -- "+wfXmlString);
		LOGGER.debug("Meta -- "+workflow.getMetas());
		LOGGER.debug("wfParams :: "+params);
		long workflowExecutionStartTime = System.currentTimeMillis();
		Map<Object, Object> result = (Map<Object,Object>) WorkflowUtil.getWorkflowExpressionResult(wfXmlString, params, null, false, false);
		LOGGER.debug("Time taken for optimised workflow execution : "+(System.currentTimeMillis() - workflowExecutionStartTime));
		
		long readingsStartTime = System.currentTimeMillis();
		if (result != null && !result.isEmpty()) {
			List<ReadingContext> readings = new ArrayList<>();
			for (Map.Entry<Object, Object> entry : result.entrySet()) {
				ReadingContext reading = new ReadingContext();
				reading.setParentId(resourceId);
				reading.setTtime((long) entry.getKey());
				reading.addReading(fieldName, entry.getValue());
				readings.add(reading);
			}
			return readings;
		}
		LOGGER.debug("Time taken to generate readings from Workflow result : "+(System.currentTimeMillis() - readingsStartTime));
		return null;
	}
	
	private static Map<String,Object> fetchResourceParams(long resourceId, List<OptimisedFormulaCalculationMeta> metas, DateRange range, ModuleBean modBean, Set<Object> xValues) throws Exception {
		Map<String,Object> wfParams = new HashMap<>();
		for (OptimisedFormulaCalculationMeta meta : metas) {
			if (meta.getResourceId() == -1) {
				wfParams.put(meta.getParamName(), fetchHistoricalData(meta, resourceId, range, modBean, xValues));
			}
		}
		return wfParams;
	}
	
	private static Map<String,Object> fetchIndependentParams(List<OptimisedFormulaCalculationMeta> metas, DateRange range, ModuleBean modBean, Set<Object> xValues) throws Exception {
		Map<String,Object> wfParams = new HashMap<>();
		for (OptimisedFormulaCalculationMeta meta : metas) {
			if (meta.getResourceId() != -1) {
				wfParams.put(meta.getParamName(), fetchHistoricalData(meta, -1, range, modBean, xValues));
			}
		}
		return wfParams;
	}
	
	private static Object fetchHistoricalData(OptimisedFormulaCalculationMeta meta, long resourceId, DateRange range, ModuleBean modBean, Set<Object> xValues) throws Exception {
		if (meta.getResourceId() == -1 && resourceId == -1) {
			throw new IllegalArgumentException("Both the resource ids cannot be empty");
		}
		
		
		long parentId = resourceId == -1 ? meta.getResourceId() : resourceId;
		FacilioField valField = modBean.getField(meta.getFieldName(), meta.getModuleName());
		
		if (meta.isOnlyParentId()) {
			ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(parentId, valField);
			return rdm.getValue().toString();
		}
		
		FacilioField parentIdField = modBean.getField("parentId", meta.getModuleName());
		FacilioField ttime = modBean.getField("ttime", meta.getModuleName());
		List<FacilioField> fields = new ArrayList<>();
		fields.add(valField);
		fields.add(ttime);
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																	.moduleName(meta.getModuleName())
																	.select(fields)
																	.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(parentId), PickListOperators.IS))
																	.andCondition(CriteriaAPI.getCondition(ttime, range.toString(), DateOperators.BETWEEN))
																	.andCondition(CriteriaAPI.getCondition(valField, CommonOperators.IS_NOT_EMPTY))
																	;
		
		Map<Object, Object> values = new HashMap<>();
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				Object timeVal = prop.get("ttime");
				xValues.add(timeVal);
				Object val = prop.get(meta.getFieldName());
				values.put(timeVal, val);
			}
		}
		
		return values;
	}
	
	private static WorkflowFunctionContext getWorkflowFunction (String nameSpace, String name, String params) {
		WorkflowFunctionContext function = new WorkflowFunctionContext();
		function.setFunctionName(name);
		function.setNameSpace(nameSpace);
		
		if (params != null) {
			function.setParams(params);
		}
		return function;
	}
	
	private static ParameterContext getWorkflowParameter (String name, String type) {
		ParameterContext param = new ParameterContext();
		param.setName(name);
		param.setTypeString(type);
		return param;
	}
	
	public static OptimisedFormulaCalculationWorkflow constructOptimisedWorkflowForHistoricalCalculation (WorkflowContext workflow) {
		OptimisedFormulaCalculationWorkflow optimisedWorkflow = new OptimisedFormulaCalculationWorkflow();
		
		optimisedWorkflow.addParamater(getWorkflowParameter("xValues", "list"));
		
		ExpressionContext result = new ExpressionContext();
		result.setName("result");
		result.setDefaultFunctionContext(getWorkflowFunction("map", "create", null));
		
		optimisedWorkflow.addWorkflowExpression(result);
		optimisedWorkflow.setResultEvaluator("result");
		
		IteratorContext iterator = new IteratorContext();
		iterator.setIteratableVariable("xValues");
		iterator.setLoopVariableIndexName("index");
		iterator.setLoopVariableValueName("value");
		
		for (WorkflowExpression expression : workflow.getExpressions()) {
			ExpressionContext expr = (ExpressionContext) expression;
			if (expr.getConstant() == null && expr.getDefaultFunctionContext() == null && expr.getExpr() == null) {
				
				if (expr.getCriteria() == null || expr.getCriteria().isEmpty()) {
					optimisedWorkflow.addWorkflowExpression(expr);
					continue;
				}
				String exprName = "param"+expr.getName();
				OptimisedFormulaCalculationMeta meta = new OptimisedFormulaCalculationMeta();
				meta.setParamName(exprName);
				meta.setModuleName(expr.getModuleName());
				meta.setFieldName(expr.getFieldName());
				
				boolean onlyParentId = true;
				for (Condition condition : expr.getCriteria().getConditions().values()) {
					if (condition.getFieldName().equals("parentId")) {
						if (!condition.getValue().equals("${resourceId}")) {
							meta.setResourceId(Long.parseLong(condition.getValue()));
						}
					}
					else if (condition.getFieldName().equals("ttime")) {
						onlyParentId = false;
					}
					else {
						optimisedWorkflow.addWorkflowExpression(expr);
						continue;
					}
				}
				
				ExpressionContext param = null;
				if (onlyParentId) {
					meta.setOnlyParentId(true);
					optimisedWorkflow.addParamater(getWorkflowParameter(exprName, "string"));
					param = new ExpressionContext();
					param.setName(expr.getName());
					param.setConstant("${"+exprName+"}");
				}
				else {
					optimisedWorkflow.addParamater(getWorkflowParameter(exprName, "map"));
					param = new ExpressionContext();
					param.setName(expr.getName());
					param.setDefaultFunctionContext(getWorkflowFunction("map", "get", exprName+", value"));
				}
				iterator.addExpression(param);
				optimisedWorkflow.addMeta(meta);
			}
			else {
				iterator.addExpression(expr);
			}
		}
		ExpressionContext itrResult = new ExpressionContext();
		itrResult.setName("itrResult");
		itrResult.setExpr(workflow.getResultEvaluator());
		iterator.addExpression(itrResult);
		
		ExpressionContext itrResultPut = new ExpressionContext();
		itrResultPut.setName("put");
		itrResultPut.setDefaultFunctionContext(getWorkflowFunction("map", "put", "result, value, itrResult"));
		iterator.addExpression(itrResultPut);
		
		optimisedWorkflow.addWorkflowExpression(iterator);
		
		return optimisedWorkflow;
	}
	
	public static void historicalCalculation(FormulaFieldContext formula, DateRange range, long singleResourceId, boolean isSystem, boolean historicalAlarm) throws Exception {
		List<DateRange> intervals = getIntervals(formula, range);
		LOGGER.debug(intervals);
		if (intervals != null && !intervals.isEmpty()) {
			List<ReadingContext> readings = new ArrayList<>();
			boolean isSelfDependent = dependsOnSameModule(formula);
			if (singleResourceId != -1) {
				LOGGER.debug("Gonna calculate historical formula of : "+formula.getId()+" for resource : "+singleResourceId);
				if (formula.getMatchedResourcesIds().contains(singleResourceId)) {
					LOGGER.debug("Matched");
					int deletedData = deleteOlderData(range.getStartTime(), range.getEndTime(), Collections.singletonList(singleResourceId), formula.getReadingField().getModule().getName());
					LOGGER.debug("Deleted rows for formula : "+formula.getName()+" between "+range+" is : "+deletedData);
					
					List<ReadingContext> currentReadings = FormulaFieldAPI.calculateFormulaReadings(singleResourceId, formula.getReadingField().getModule().getName(), formula.getReadingField().getName(), intervals, formula.getWorkflow(), formula.getTriggerTypeEnum() == TriggerType.SCHEDULE, isSelfDependent);
					if (currentReadings != null && !currentReadings.isEmpty()) {
						readings.addAll(currentReadings);
					}
				}
			}
			else {
				int deletedData = deleteOlderData(range.getStartTime(), range.getEndTime(), formula.getMatchedResourcesIds(), formula.getReadingField().getModule().getName());
				LOGGER.debug("Deleted rows for formula : "+formula.getName()+" between "+range+" is : "+deletedData);
				
				for (Long resourceId : formula.getMatchedResourcesIds()) {
					List<ReadingContext> currentReadings = FormulaFieldAPI.calculateFormulaReadings(resourceId, formula.getReadingField().getModule().getName(), formula.getReadingField().getName(), intervals, formula.getWorkflow(), formula.getTriggerTypeEnum() == TriggerType.SCHEDULE, isSelfDependent);
					if (currentReadings != null && !currentReadings.isEmpty()) {
						readings.addAll(currentReadings);
					}
				}
			}
			
			LOGGER.debug("Historical Data to be added for formula "+readings.size());
			if (!isSelfDependent && !readings.isEmpty()) {
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.MODULE_NAME, formula.getReadingField().getModule().getName());
				context.put(FacilioConstants.ContextNames.READINGS, readings);
				context.put(FacilioConstants.ContextNames.HISTORY_READINGS, !historicalAlarm);
				context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
				
				Chain addReadingChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
				addReadingChain.execute(context);
			}
			
			if (formula.getTriggerTypeEnum() == TriggerType.SCHEDULE) {
				List<FormulaFieldContext> dependentFormulas = FormulaFieldAPI.getActiveFormulasDependingOnFields(TriggerType.SCHEDULE, Collections.singletonList(formula.getReadingField().getId()));
				if (dependentFormulas != null && !dependentFormulas.isEmpty()) {
					for (FormulaFieldContext currentFormula : dependentFormulas) {
						if (singleResourceId != -1 ) {
							if (currentFormula.getMatchedResourcesIds().contains(singleResourceId)) {
								List<Long> dependentFieldIds = currentFormula.getWorkflow().getDependentFieldIds();
								if (dependentFieldIds.contains(formula.getReadingField().getFieldId())) {
									calculateHistoricalDataForSingleResource(currentFormula.getId(), singleResourceId, range, isSystem, historicalAlarm);
								}
							}
						}
						else{
							List<Long> dependentFieldIds = currentFormula.getWorkflow().getDependentFieldIds();
							if (dependentFieldIds.contains(formula.getReadingField().getFieldId())) {
								recalculateHistoricalData(currentFormula.getId(), range);
							}
						}
					}
				}
			}
		}
	}
	
	private static int deleteOlderData(long startTime, long endTime, List<Long> parentIds, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
		FacilioField parentId = fieldMap.get("parentId");
		FacilioField ttime = fieldMap.get("ttime");
		DeleteRecordBuilder<ReadingContext> deleteBuilder = new DeleteRecordBuilder<ReadingContext>()
																.module(module)
																.andCondition(CriteriaAPI.getCondition(parentId, parentIds, PickListOperators.IS))
																.andCondition(CriteriaAPI.getCondition(ttime, startTime+","+endTime, DateOperators.BETWEEN))
																;
		return deleteBuilder.delete();
	}
	
	private static List<DateRange> getIntervals(FormulaFieldContext formula, DateRange range) {
		switch (formula.getTriggerTypeEnum()) {
			case PRE_LIVE_READING:
				return null;
			case POST_LIVE_READING:
				return DateTimeUtil.getTimeIntervals(range.getStartTime(), range.getEndTime(), formula.getInterval());
			case SCHEDULE:
				ScheduleInfo schedule = FormulaFieldAPI.getSchedule(formula.getFrequencyEnum());
				return DateTimeUtil.getTimeIntervals(range.getStartTime(), range.getEndTime(), schedule);
		}
		return null;
	}
	
	private static class OptimisedFormulaCalculationWorkflow extends WorkflowContext {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private List<OptimisedFormulaCalculationMeta> metas;
		public List<OptimisedFormulaCalculationMeta> getMetas() {
			return metas;
		}
		public void setMetas(List<OptimisedFormulaCalculationMeta> metas) {
			this.metas = metas;
		}
		public void addMeta(OptimisedFormulaCalculationMeta meta) {
			if (metas == null) {
				metas = new ArrayList<>();
			}
			metas.add(meta);
		}
	}
	
	private static class OptimisedFormulaCalculationMeta {
		private String paramName;
		public String getParamName() {
			return paramName;
		}
		public void setParamName(String paramName) {
			this.paramName = paramName;
		}
		
		private String moduleName;
		public String getModuleName() {
			return moduleName;
		}
		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}
		
		private String fieldName;
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		
		private long resourceId = -1;
		public long getResourceId() {
			return resourceId;
		}
		public void setResourceId(long resourceId) {
			this.resourceId = resourceId;
		}
		
		private boolean onlyParentId = false;
		public boolean isOnlyParentId() {
			return onlyParentId;
		}
		public void setOnlyParentId(boolean onlyParentId) {
			this.onlyParentId = onlyParentId;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return paramName+"::"+moduleName+"::"+fieldName+"::"+resourceId;
		}
	}
	
	public static int getDataInterval(FormulaFieldContext formula) {
		switch (formula.getTriggerTypeEnum()) {
			case SCHEDULE:
				switch (formula.getFrequencyEnum()) {
					case HOURLY:
						return 60;
					case DAILY:
					case WEEKLY:
					case MONTHLY:
					case QUARTERTLY:
					case HALF_YEARLY:
					case ANNUALLY:
						return 24 * 60;
					default:
						return -1;
				}
			case POST_LIVE_READING:
				if (formula.getInterval() > (24 * 60)) {
					return 24 * 60;
				}
				return formula.getInterval();
			case PRE_LIVE_READING:
				break;
		}
		return -1;
	}

}
