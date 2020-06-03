package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalAlarmsAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;

public class ExecuteHistoryForReadingRule extends ExecuteHistoricalRule {

	private static final Logger LOGGER = Logger.getLogger(ExecuteHistoryForReadingRule.class.getName());

	@Override
	public List<BaseEventContext> executeRuleAndGenerateEvents(JSONObject loggerInfo, DateRange dateRange, HashMap<String, Boolean> jobStatesMap, long jobId) throws Exception{
		
		long processStartTime = System.currentTimeMillis();
		long startTime = dateRange.getStartTime();
		long endTime = dateRange.getEndTime();
		
		Long ruleId = (Long) loggerInfo.get("rule");
    	Long resourceId = (Long) loggerInfo.get("resource");
    	Integer ruleJobType = (Integer) loggerInfo.get("ruleJobType");
    	RuleJobType ruleJobTypeEnum = RuleJobType.valueOf(ruleJobType);

		ReadingRuleContext readingRule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId, false, true);
		ResourceContext currentResourceContext = ResourceAPI.getResource(resourceId);
		if(readingRule == null || currentResourceContext == null || jobStatesMap == null || MapUtils.isEmpty(jobStatesMap) || jobId == -1 || dateRange == null || ruleJobTypeEnum == null) {
			throw new Exception("Invalid params to execute daily reading event job: "+jobId);				
		}
		readingRule.setMatchedResources(Collections.singletonMap(currentResourceContext.getId(), currentResourceContext));

		LOGGER.info("Historical Reading Rule Run started for jobId: "+jobId+" with RuleType " +ruleJobTypeEnum.getValue()+ " and RuleId : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+". Triggered at  -- "+processStartTime);				
		boolean isFirstIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isFirstIntervalJob"));
		boolean isLastIntervalJob = Boolean.TRUE.equals((Boolean) jobStatesMap.get("isLastIntervalJob"));
		Boolean isManualFailed = (Boolean) jobStatesMap.get("isManualFailed");

		Map<String, List<ReadingDataMeta>> supportFieldsRDM = null;
		List<WorkflowFieldContext> fields = null;
		AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(Collections.singletonList(readingRule.getId()), false, true),null);
		if(alarmRule != null) {		
			fields = new ArrayList<>();
			if(alarmRule.getPreRequsite().getWorkflow() != null) {
				List<WorkflowFieldContext> workflowFields = WorkflowUtil.getWorkflowFields(alarmRule.getPreRequsite().getWorkflow().getId());
				if(workflowFields != null) {
					fields.addAll(workflowFields);
				}
			}
			if(alarmRule.getAlarmTriggerRule().getWorkflow() != null) {
				List<WorkflowFieldContext> workflowFields = WorkflowUtil.getWorkflowFields(alarmRule.getAlarmTriggerRule().getWorkflow().getId());
				if(workflowFields != null) {
					fields.addAll(workflowFields);
				}
			}
		}				
		
		if (fields != null && !fields.isEmpty()) {
			supportFieldsRDM = getSupportingData(fields, startTime, endTime, -1, isManualFailed);
		}	
		Map<String, List<ReadingDataMeta>> currentRDMList = null;
		if (fields != null) {
			currentRDMList = getSupportingData(fields, startTime, endTime, resourceId, isManualFailed);
		}
		
		
		Map<String, List<ReadingDataMeta>> currentFields = supportFieldsRDM;
		if (currentRDMList != null && !currentRDMList.isEmpty()) {
			if (supportFieldsRDM == null) {
				currentFields = currentRDMList;
			}
			else {
				currentFields = new HashMap<>(supportFieldsRDM);
				currentFields.putAll(currentRDMList);
			}
		}
		
		long readingsFetchStartTime = System.currentTimeMillis();
		List<ReadingContext> readings = null;
		List<BaseEventContext> events = new ArrayList<>();
		BaseEventContext previousEventMeta = new BaseEventContext();

		if(isFirstIntervalJob)
		{
			List<BaseEventContext> beforeFetchFirstEvent = new ArrayList<BaseEventContext>();
			ReadingContext previousFetchStartReading = fetchSingleReading(readingRule, resourceId, startTime, NumberOperators.LESS_THAN, "TTIME DESC");
			
			if(previousFetchStartReading != null)
			{
				Map<String, List<ReadingDataMeta>> beforeCurrentFields = prepareCurrentFieldsRDM(readingRule, previousFetchStartReading.getTtime(), startTime, resourceId, fields, isManualFailed);
				executeWorkflows(readingRule, Collections.singletonList(previousFetchStartReading), beforeCurrentFields, fields, beforeFetchFirstEvent, new BaseEventContext());
				if(beforeFetchFirstEvent != null && !beforeFetchFirstEvent.isEmpty() && !beforeFetchFirstEvent.get(0).getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY))
				{
					previousEventMeta = beforeFetchFirstEvent.get(0);
				}
			}		
		}
		
		readings = fetchReadings(readingRule, resourceId, startTime, endTime);
		long eventProcessingStartTime = System.currentTimeMillis();
		long eventSpecialCaseStartTime = 0, eventInsertStartTime = 0, eventInsertEndTime = 0;
		boolean isReadingsEmpty = (readings == null || readings.isEmpty())? true: false;	

		if(readings != null && !readings.isEmpty())
		{	
			WorkflowRuleHistoricalAlarmsAPI.handleDuplicateTriggerMetricReadingErrors(isManualFailed, readings, readingRule.getReadingField(), currentResourceContext);
			startTime = readings.get(0).getTtime();
			endTime = readings.get(readings.size() - 1).getTtime();
			executeWorkflows(readingRule, readings, currentFields, fields, events, previousEventMeta);
			
			eventSpecialCaseStartTime = System.currentTimeMillis();
			if(events != null && !events.isEmpty())
			{
				BaseEventContext finalEventOfCurrentJobInterval = events.get(events.size() - 1);

				if(!isLastIntervalJob && finalEventOfCurrentJobInterval.getCreatedTime() == endTime && !finalEventOfCurrentJobInterval.getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY))
				{		
					previousEventMeta = finalEventOfCurrentJobInterval;
					List<BaseEventContext> nextJobFirstEvent = new ArrayList<BaseEventContext>();
					
					ReadingContext nextSingleReading = fetchSingleReading(readingRule, resourceId, endTime, NumberOperators.GREATER_THAN, "TTIME");
					if(nextSingleReading != null)
					{
						Map<String, List<ReadingDataMeta>> extendedCurrentFields = prepareCurrentFieldsRDM(readingRule, endTime, nextSingleReading.getTtime(), resourceId, fields, isManualFailed);
						
						executeWorkflows(readingRule, Collections.singletonList(nextSingleReading), extendedCurrentFields, fields, nextJobFirstEvent, previousEventMeta);
						if(nextJobFirstEvent != null && !nextJobFirstEvent.isEmpty() && nextJobFirstEvent.get(0).getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY))
						{
							events.add(nextJobFirstEvent.get(0));
						}
					}	
				}
			}
		}
		//LOGGER.info("Process Time taken for Historical Run for jobId: "+jobId+" Rule : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is  -- "+(System.currentTimeMillis() - processStartTime));				
		LOGGER.info("Time taken for Reading Rule Historical Run for jobId: "+jobId+" Reading Rule : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is -- " +(System.currentTimeMillis() - processStartTime) + " and isReadingsEmpty -- " +isReadingsEmpty+
				" Fetch prequisite readings time taken will be : --" + (readingsFetchStartTime - processStartTime) + " Fetch readings time taken -- " + (eventProcessingStartTime - readingsFetchStartTime) +
				" Event processing time will be -- " +(eventSpecialCaseStartTime - eventProcessingStartTime)+ " Event special handling time -- " +(eventInsertStartTime- eventSpecialCaseStartTime) +
				" Event insertion time will be -- " +(eventInsertEndTime - eventInsertStartTime));
	
		return events;
	}
	
	private int executeWorkflows(ReadingRuleContext readingRule, List<ReadingContext> readings, Map<String, List<ReadingDataMeta>> supportFieldsRDM, List<WorkflowFieldContext> fields, List<BaseEventContext> baseEvents,BaseEventContext previousEventMeta) throws Exception
	{
		int alarmCount = 0;
		if (readings != null && !readings.isEmpty()) 
		{
			Map<String, Object> placeHolders = new HashMap<>();
			CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
			CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
			RuleType[] ruleTypes = {RuleType.READING_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE,RuleType.ALARM_RCA_RULES, RuleType.IMPACT_RULE};

			FacilioContext context = new FacilioContext();
			Map<Long, ReadingRuleAlarmMeta> alarmMetaMap = new HashMap<>();
			context.put(FacilioConstants.ContextNames.READING_RULE_ALARM_META, alarmMetaMap);
			context.put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, true);
			ReadingDataMeta prevRDM = null;			
			int itr = 0;
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> allFields = modBean.getAllFields(readingRule.getReadingField().getModule().getName());
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
			Map<String, Integer> lastItr = new HashMap<>(); //To store itr of currently matched rdm itr
			Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap = new HashMap<String, List<WorkflowRuleContext>>();
			
			for (int i = itr; i < readings.size(); i++) {
				ReadingContext reading = readings.get(i);
				try 
				{
					ReadingDataMeta currentRDM = getRDM(reading, readingRule.getReadingField());
					if (currentRDM != null) {
						context.put(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META, Collections.singletonMap(ReadingsAPI.getRDMKey(reading.getParentId(), readingRule.getReadingField()), prevRDM));
						
						Map<String, ReadingDataMeta> rdmCache = getCurrentRDMs(reading, fieldMap);
						getOtherRDMs(reading.getParentId(), reading.getTtime(), supportFieldsRDM, rdmCache, lastItr, fields);
						context.put(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META, rdmCache);
						
						Map<String, Object> recordPlaceHolders = new HashMap<>(placeHolders);
						CommonCommandUtil.appendModuleNameInKey(readingRule.getReadingField().getModule().getName(), readingRule.getReadingField().getModule().getName(), FieldUtil.getAsProperties(reading), recordPlaceHolders);
						
						context.put(EventConstants.EventContextNames.PREVIOUS_EVENT_META, previousEventMeta);
						WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(Collections.singletonList(readingRule), readingRule.getReadingField().getModule(), reading, null, recordPlaceHolders, context, false, workflowRuleCacheMap, false, Collections.singletonList(readingRule.getActivityTypeEnum()), ruleTypes);

						prevRDM = currentRDM;
						
						Boolean isAlarmCreated = (Boolean) context.get(FacilioConstants.ContextNames.IS_ALARM_CREATED);
						if(isAlarmCreated != null && isAlarmCreated) {
							alarmCount++;
							context.put(FacilioConstants.ContextNames.IS_ALARM_CREATED, Boolean.FALSE);
						}
						List<BaseEventContext> currentEvent = (List<BaseEventContext>) context.remove(EventConstants.EventContextNames.EVENT_LIST);
						if (CollectionUtils.isNotEmpty(currentEvent)) {
							previousEventMeta = currentEvent.get(0);
							baseEvents.addAll(currentEvent);
						}
					}
				}
				catch (Exception e) {
					StringBuilder builder = new StringBuilder("Error during execution of rule : ");
					builder.append(readingRule.getId());
					builder.append(" for Record : ").append(reading.getId())
							.append(" of module : ").append(readingRule.getReadingField().getModule().getName());
					LOGGER.log(Level.SEVERE, builder.toString(), e);
					throw e;
				}
			}
			if (readingRule.clearAlarm() && !AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
				clearLatestAlarms(alarmMetaMap, readingRule);		
			}
		}
		return alarmCount;
	}
	
	private ReadingDataMeta getRDM(ReadingContext value, FacilioField valField) {
		Object val = value.getReading(valField.getName());
		if (val != null) {
			ReadingDataMeta rdm = new ReadingDataMeta();
			rdm.setFieldId(valField.getFieldId());
			rdm.setField(valField);
			rdm.setTtime(value.getTtime());
			rdm.setValue(val);
			rdm.setReadingDataId(value.getId());
			rdm.setResourceId(value.getParentId());
			return rdm;
		}
		return null;
	}
	
	private Map<String, ReadingDataMeta> getCurrentRDMs(ReadingContext reading, Map<String, FacilioField> fieldMap) {
		Map<String, ReadingDataMeta> rdmCache = new HashMap<>();
		Map<String, Object> data = reading.getReadings();
		if (data != null && !data.isEmpty()) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				FacilioField field = fieldMap.get(entry.getKey());
				if (field != null) {
					ReadingDataMeta rdm = getRDM(reading, field);
					rdmCache.put(ReadingsAPI.getRDMKey(reading.getParentId(), field), rdm);
				}
			}
		}
		return rdmCache;
	}
	
	private void getOtherRDMs(long resourceId, long ttime, Map<String, List<ReadingDataMeta>> rdmMap, Map<String, ReadingDataMeta> rdmCache, Map<String, Integer> lastItr, List<WorkflowFieldContext> fields) {
		if (rdmMap != null && !rdmMap.isEmpty() && fields != null && !fields.isEmpty()) {
			for (WorkflowFieldContext field : fields) {
				if (field.getAggregationEnum() == BmsAggregateOperators.SpecialAggregateOperator.LAST_VALUE) {
					long parentId = field.getResourceId() == -1 ? resourceId : field.getResourceId();
					String rdmKey = ReadingsAPI.getRDMKey(parentId, field.getField());
					List<ReadingDataMeta> rdmList = rdmMap.get(ReadingsAPI.getRDMKey(parentId, field.getField()));
					ReadingDataMeta prevRDM = null;
					Integer itr = lastItr.get(rdmKey);
					if (itr == null) {
						itr = 0;
					}
					
					if (rdmList != null && !rdmList.isEmpty()) {
						for (; itr < rdmList.size(); itr++) {
							ReadingDataMeta rdm = rdmList.get(itr);
							if (rdm.getTtime() > ttime) {
								break;
							}
							prevRDM = rdm;
						}
					}
					if (prevRDM != null) {
						rdmCache.put(rdmKey, prevRDM);
						lastItr.put(rdmKey, itr - 1);
					}
				}
			}
		}
	}
	
	private Map<String, List<ReadingDataMeta>> getSupportingData(List<WorkflowFieldContext> fields, long startTime, long endTime, long resourceId, boolean isManualFailed) throws Exception {
		return getSupportingData(fields, startTime, endTime, resourceId, isManualFailed, true);
	}
	
	private Map<String, List<ReadingDataMeta>> getSupportingData(List<WorkflowFieldContext> fields, long startTime, long endTime, long resourceId, Boolean isManualFailed, boolean canConstructErrorMessage) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, List<ReadingDataMeta>> supportingValues = new HashMap<>();
		for (WorkflowFieldContext field : fields) {
			if (field.getAggregationEnum() == BmsAggregateOperators.SpecialAggregateOperator.LAST_VALUE) {
				if (resourceId == -1 && field.getResourceId() == -1) {
					continue;
				}
				if (resourceId != -1 && field.getResourceId() != -1) {
					continue;
				}
				long parentId = resourceId == -1? field.getResourceId() : resourceId;
				
				FacilioField valField = modBean.getField(field.getFieldId());
				field.setField(valField);
				String rdmKey = ReadingsAPI.getRDMKey(parentId, valField);
				
				if(supportingValues.containsKey(rdmKey)) {
					continue;
				}
				
				List<FacilioField> allFields = modBean.getAllFields(valField.getModule().getName());
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
				FacilioField parentField = fieldMap.get("parentId");
				FacilioField ttimeField = fieldMap.get("ttime");
				
				List<FacilioField> selectFields = new ArrayList<>();
				selectFields.add(valField);
				selectFields.add(parentField);
				selectFields.add(ttimeField);
				
				SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																		.select(selectFields)
																		.module(valField.getModule())
																		.beanClass(ReadingContext.class)
																		.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(parentId), PickListOperators.IS))
																		.andCondition(CriteriaAPI.getCondition(ttimeField, startTime+","+endTime, DateOperators.BETWEEN))
																		.andCondition(CriteriaAPI.getCondition(valField, CommonOperators.IS_NOT_EMPTY))
																		.orderBy("TTIME")
																		;
	
				List<ReadingContext> values = selectBuilder.get();
				if (values != null && !values.isEmpty()) {
					List<ReadingDataMeta> rdms = new ArrayList<>();
					for (ReadingContext value : values) {
						ReadingDataMeta rdm = getRDM(value, valField);
						rdms.add(rdm);
					}
					supportingValues.put(rdmKey, rdms);
				}
				else {	
					if(canConstructErrorMessage){
						isManualFailed = true;
						supportingValues.put(rdmKey, null);
						ResourceContext currentResource = ResourceAPI.getResource(resourceId);
						throw new Exception("Selected asset (" +currentResource.getName()+ ") seems to have no data for the configured field (" + field.getField().getDisplayName() + ") in this timeline.");				
					}
//							selectBuilder = new SelectRecordsBuilder<ReadingContext>()
//												.select(selectFields)
//												.module(valField.getModule())
//												.beanClass(ReadingContext.class)
//												.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(parentId), PickListOperators.IS))
//												.andCondition(CriteriaAPI.getCondition(ttimeField, String.valueOf(startTime), DateOperators.IS_BEFORE))
//												.andCondition(CriteriaAPI.getCondition(valField, CommonOperators.IS_NOT_EMPTY))
//												.orderBy("TTIME DESC")
//												.limit(1)
//												;
//							values = selectBuilder.get();
//							ReadingDataMeta rdm = null;
//							if (values != null && !values.isEmpty()) {
//								rdm = getRDM(values.get(0), valField);
//							}
//							else {
//								rdm = ReadingsAPI.getReadingDataMeta(parentId, valField);
//							}
//							if (rdm != null) {
//								supportingValues.put(rdmKey, Collections.singletonList(rdm));
//							}
				}
			}
		}
		return supportingValues;
	}
	
	private List<ReadingContext> fetchReadings(ReadingRuleContext readingRule, long resourceId, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(readingRule.getReadingField().getModule().getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField parentField = fieldMap.get("parentId");
		FacilioField ttimeField = fieldMap.get("ttime");
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																.select(fields)
																.module(readingRule.getReadingField().getModule())
																.beanClass(ReadingContext.class)
																.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(resourceId), PickListOperators.IS))
																.andCondition(CriteriaAPI.getCondition(ttimeField, startTime+","+endTime, DateOperators.BETWEEN))
																.andCondition(CriteriaAPI.getCondition(readingRule.getReadingField(), CommonOperators.IS_NOT_EMPTY))
																.orderBy("TTIME")
																;
		
		return selectBuilder.get();
	}
	
	private ReadingContext fetchSingleReading(ReadingRuleContext readingRule, long resourceId, long ttime, Operator<String> NumberOperator, String orderBy) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(readingRule.getReadingField().getModule().getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField parentField = fieldMap.get("parentId");
		FacilioField ttimeField = fieldMap.get("ttime");
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																.select(fields)
																.module(readingRule.getReadingField().getModule())
																.beanClass(ReadingContext.class)
																.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(resourceId), PickListOperators.IS))
																.andCondition(CriteriaAPI.getCondition(ttimeField, ""+ttime, NumberOperator))
																.andCondition(CriteriaAPI.getCondition(readingRule.getReadingField(), CommonOperators.IS_NOT_EMPTY))
																.orderBy(orderBy).limit(1)
																;
		
		return selectBuilder.fetchFirst();
	}
	
	private Map<String, List<ReadingDataMeta>> prepareCurrentFieldsRDM(ReadingRuleContext rule, long startTime, long endTime, long resourceId, List<WorkflowFieldContext> fields, Boolean isManualFailed) throws Exception
	{
		Map<String, List<ReadingDataMeta>> extendedSupportFieldsRDM = null;
		if (fields != null && !fields.isEmpty()) {
			extendedSupportFieldsRDM = getSupportingData(fields, startTime, endTime, -1, isManualFailed, false);
		}	
		Map<String, List<ReadingDataMeta>> extendedCurrentRDMList = null;
		if (fields != null) {
			extendedCurrentRDMList = getSupportingData(fields, startTime, endTime, resourceId, isManualFailed, false);
		}
		
		Map<String, List<ReadingDataMeta>> extendedCurrentFields = extendedSupportFieldsRDM;
		if (extendedCurrentRDMList != null && !extendedCurrentRDMList.isEmpty()) {
			if (extendedSupportFieldsRDM == null) {
				extendedCurrentFields = extendedCurrentRDMList;
			}
			else {
				extendedCurrentFields = new HashMap<>(extendedSupportFieldsRDM);
				extendedCurrentFields.putAll(extendedCurrentRDMList);
			}
		}
		return extendedCurrentFields;
	}
	
	
	@Override
	public List<Long> getMatchedSecondaryParamIds(JSONObject loggerInfo, Boolean isInclude) throws Exception 
	{
		String ruleKeyName = fetchPrimaryLoggerKey();
		Long ruleId = (Long)loggerInfo.get(ruleKeyName);
		List<Long> selectedResourceIds = (List<Long>) loggerInfo.get("resource");
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(ruleId);
		if (rule == null) {
			throw new IllegalArgumentException("Invalid reading rule id to run through historical data.");
		}
	
		List<Long> matchedResourceIds = new ArrayList<>();
		ReadingRuleContext readingRuleContext = (ReadingRuleContext)rule;
		if(readingRuleContext.getMatchedResources() != null) {
			matchedResourceIds = new ArrayList<>(readingRuleContext.getMatchedResources().keySet());
		}

		List<Long> finalResourceIds = WorkflowRuleHistoricalAlarmsAPI.getMatchedFinalSecondaryIds(selectedResourceIds, matchedResourceIds, isInclude);
		return finalResourceIds;
	}
	

	private void clearLatestAlarms(Map<Long, ReadingRuleAlarmMeta> alarmMetaMap, ReadingRuleContext rule) throws Exception { //Clearing the alarm that is not cleared even with the last reading. It's assumed that it'll be cleared in the next interval
		for (ReadingRuleAlarmMeta meta : alarmMetaMap.values()) {
			if (!meta.isClear()) {
				AlarmContext alarm = AlarmAPI.getAlarm(meta.getAlarmId());
				int interval = ReadingsAPI.getDataInterval(alarm.getResource().getId(), rule.getReadingField());
				JSONObject json = AlarmAPI.constructClearEvent(alarm, "System auto cleared Historical Alarm because associated rule executed false for the associated resource", alarm.getModifiedTime() + (interval * 60 * 1000));
	
				FacilioContext addEventContext = new FacilioContext();
				addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, json);
				FacilioChain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
				getAddEventChain.execute(addEventContext);
			}
		}
	}
}
