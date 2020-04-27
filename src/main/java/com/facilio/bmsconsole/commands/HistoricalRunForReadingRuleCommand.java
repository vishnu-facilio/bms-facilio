package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.LoggerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.jobs.SingleResourceHistoricalFormulaCalculatorJob;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.LoggerAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
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
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;

public class HistoricalRunForReadingRuleCommand extends FacilioCommand implements PostTransactionCommand{

private static final Logger LOGGER = Logger.getLogger(HistoricalRunForReadingRuleCommand.class.getName());
	
	private WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = null;
	private Long jobId;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	
	@Override
	public boolean executeCommand(Context jobContext) throws Exception {
		
		try {
			long jobStartTime = System.currentTimeMillis();
			
			jobId = (long) jobContext.get(FacilioConstants.ContextNames.HISTORICAL_RULE_JOB_ID);
			
			workflowRuleHistoricalLoggerContext = WorkflowRuleHistoricalLoggerUtil.getWorkflowRuleHistoricalLoggerById(jobId);
			
			if(workflowRuleHistoricalLoggerContext != null && workflowRuleHistoricalLoggerContext.getStatusAsEnum() != null)
			{
				switch(workflowRuleHistoricalLoggerContext.getStatusAsEnum()) {
				case FAILED:
					return true;
				default:
					break;
				}
				
				workflowRuleHistoricalLoggerContext.setCalculationStartTime(DateTimeUtil.getCurrenTime());
	
				Long startTime = workflowRuleHistoricalLoggerContext.getStartTime();
				Long endTime = workflowRuleHistoricalLoggerContext.getEndTime();
				Long resourceId = workflowRuleHistoricalLoggerContext.getResourceId();
				Long ruleId = workflowRuleHistoricalLoggerContext.getRuleId();

				ReadingRuleContext readingRule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId, false, true);
				if (readingRule == null || resourceId == null) {
					return false;
				}
				
				LOGGER.info("JobStartTime: "+ jobStartTime+ "Job id:"+ jobId +" Reading Rule : "+ruleId+" for resource : "+resourceId+" -- ");	
//				LOGGER.info("Historical execution of rule : "+readingRule.getId()+" for resources : "+readingRule.getMatchedResources().keySet());
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
//					if(alarmRule.getAlarmRCARules() != null) {
//						for(ReadingRuleContext rcaRules :alarmRule.getAlarmRCARules()) {
//							if(rcaRules.getWorkflow() != null) {
//								List<WorkflowFieldContext> workflowFields = WorkflowUtil.getWorkflowFields(rcaRules.getWorkflow().getId());
//								if(workflowFields != null) {
//									fields.addAll(workflowFields);
//								}
//							}
//						}
//					}
				}
				
				if (fields != null && !fields.isEmpty()) {
					supportFieldsRDM = getSupportingData(fields, startTime, endTime, -1);
				}
				
				Map<String, List<ReadingDataMeta>> currentFields = supportFieldsRDM;
				
				for(String field:currentFields.keySet())
				{
					LOGGER.info("CurrentFields RDM Values size : " +currentFields.get(field).size() + " field "+field );
				}
				
				Map<String, List<ReadingDataMeta>> currentRDMList = null;
				if (fields != null) {
					currentRDMList = getSupportingData(fields, startTime, endTime, resourceId);
				}
				for(String rdmfield:currentRDMList.keySet())
				{
					LOGGER.info(" Current RDM List RDM Values size : " + currentRDMList.get(rdmfield).size() + " field "+rdmfield );
				}
				
				if (currentRDMList != null && !currentRDMList.isEmpty()) {
					if (supportFieldsRDM == null) {
						currentFields = currentRDMList;
					}
					else {
						currentFields = new HashMap<>(supportFieldsRDM);
						currentFields.putAll(currentRDMList);
					}
				}
				
				if (AccountUtil.getCurrentOrg().getId() == 231) {
					LOGGER.info(" ProcessStartTime: "+ jobId +" Reading Rule : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - jobStartTime));		
				}
				long processStartTime = System.currentTimeMillis();
				List<ReadingContext> readings = null;
				
				if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS))
				{
					readings = fetchReadings(readingRule, resourceId, startTime, endTime);
					
					if(readings != null && !readings.isEmpty())
					{
						startTime = readings.get(0).getTtime();
						endTime = readings.get(readings.size() - 1).getTtime();
					}
					
					WorkflowRuleHistoricalLoggerUtil.deleteAlarmOccurrencesWithEdgeCases(readingRule.getId(), startTime, endTime, resourceId);				
				}
				else
				{
					long currentStartTime = startTime - (ReadingsAPI.getDataInterval(resourceId, readingRule.getReadingField()) * 60 * 1000);
					readings = fetchReadings(readingRule, resourceId, currentStartTime, endTime);
					WorkflowRuleHistoricalLoggerUtil.deleteReadingAlarm(readingRule.getId(), startTime, endTime, resourceId);
				}		
				
				if (AccountUtil.getCurrentOrg().getId() == 231) {
					LOGGER.info("Readings fetch Time for Historical Run for RuleLogger: "+ jobId +" Reading Rule : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - jobStartTime));
				}
				List<ReadingEventContext> events = new ArrayList<>();	
				int alarmCount = executeWorkflows(readingRule, readings, currentFields, fields, events);	
				
				if (AccountUtil.getCurrentOrg().getId() == 231) {
					LOGGER.info("After Execute all workflows for Historical Run for RuleLogger: "+ jobId +" Reading Rule : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - jobStartTime));
				}
				if (!events.isEmpty())
				{
					FacilioContext context = new FacilioContext();
					context.put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, true);
					context.put(EventConstants.EventContextNames.EVENT_LIST, events);
					FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(true);
					addEvent.execute(context);
					
					if (AccountUtil.getCurrentOrg().getId() == 231) {
						LOGGER.info("After V2 chain for RuleLogger: "+ jobId +" Reading Rule : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - jobStartTime));
					}
						
					Integer alarmOccurrenceCount = (Integer) context.get(FacilioConstants.ContextNames.ALARM_COUNT);
					if(alarmOccurrenceCount != null)
					{
						workflowRuleHistoricalLoggerContext.setAlarmCount(alarmOccurrenceCount);
					}
				}
			
				if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) 
				{
					workflowRuleHistoricalLoggerContext.setAlarmCount(alarmCount);
				}
				
				workflowRuleHistoricalLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.RESOLVED.getIntVal());
				workflowRuleHistoricalLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
				WorkflowRuleHistoricalLoggerUtil.updateWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);
				
				long timeTaken = (System.currentTimeMillis() - jobStartTime);
				LOGGER.info("Process Time taken for Historical Run for RuleLogger: "+jobId+" Reading Rule : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - processStartTime));
				LOGGER.info("Total Time taken for Historical Run for RuleLogger: "+jobId+" Reading Rule : "+ruleId+" between "+startTime+" and "+endTime+" is "+timeTaken);
				
			}
			
		}
		catch (Exception historicalRuleException) {
			exceptionMessage = historicalRuleException.getMessage();
			stack = historicalRuleException.getStackTrace();
			throw historicalRuleException;
		}
		
		return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	public void onError() throws Exception {
		constructErrorMessage();
	}
	
	public void constructErrorMessage() throws Exception 
	{
		try {
			
			Exception mailExp = new Exception(exceptionMessage);
			if (stack != null) 
			{
				mailExp.setStackTrace(stack);
			}
			CommonCommandUtil.emailException(HistoricalRunForReadingRuleCommand.class.getName(), "Historical Run failed for reading_rule_logger : "+jobId, mailExp);

			LOGGER.severe("HISTORICAL RULE JOB COMMAND FAILED, JOB ID -- : "+jobId);
			LOGGER.log(Level.SEVERE, exceptionMessage);

			if(workflowRuleHistoricalLoggerContext != null )
			{
				workflowRuleHistoricalLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.FAILED.getIntVal());
				workflowRuleHistoricalLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
				WorkflowRuleHistoricalLoggerUtil.updateWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);
			}
			else
			{
				LOGGER.severe(" HISTORICALRULE LOGGER IS NULL IN ONERROR FOR JOB -- " + jobId);
			}	
			
		}
		catch (Exception e) 
		{
			CommonCommandUtil.emailException("Historical Rule Exception Handling failed",
					"Historical Rule Exception Handling failed - orgid -- " + AccountUtil.getCurrentOrg().getId()+ ", JOB ID -- " +jobId, e);
			LOGGER.severe("Historical Rule Exception Handling failed  --"+jobId);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
private int executeWorkflows(ReadingRuleContext readingRule, List<ReadingContext> readings, Map<String, List<ReadingDataMeta>> supportFieldsRDM, List<WorkflowFieldContext> fields, List<ReadingEventContext> readingEvents) throws Exception {
		
		int alarmCount = 0;
		if (readings != null && !readings.isEmpty()) {
			Map<String, Object> placeHolders = new HashMap<>();
			CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
			CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
			
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
			ReadingEventContext latestEvent = null;
			Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap = new HashMap<String, List<WorkflowRuleContext>>();

			for (int i = itr; i < readings.size(); i++) {
				ReadingContext reading = readings.get(i);
//				LOGGER.info("Executing rule for reading : "+reading);
				try {
					ReadingDataMeta currentRDM = getRDM(reading, readingRule.getReadingField());
//					LOGGER.info("Current RDM : "+currentRDM);
					if (currentRDM != null) {
						context.put(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META, Collections.singletonMap(ReadingsAPI.getRDMKey(reading.getParentId(), readingRule.getReadingField()), prevRDM));
						
						Map<String, ReadingDataMeta> rdmCache = getCurrentRDMs(reading, fieldMap);
//						LOGGER.info("Current RDMs : "+rdmCache);
						getOtherRDMs(reading.getParentId(), reading.getTtime(), supportFieldsRDM, rdmCache, lastItr, fields);
//						LOGGER.info("After other RDM : "+rdmCache);
						
						context.put(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META, rdmCache);
						
						Map<String, Object> recordPlaceHolders = new HashMap<>(placeHolders);
						CommonCommandUtil.appendModuleNameInKey(readingRule.getReadingField().getModule().getName(), readingRule.getReadingField().getModule().getName(), FieldUtil.getAsProperties(reading), recordPlaceHolders);
//						WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(readingRule, readingRule.getReadingField().getModule().getName(), reading, null, recordPlaceHolders, context);
						
						RuleType[] ruleTypes = {RuleType.READING_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE,RuleType.ALARM_RCA_RULES, RuleType.IMPACT_RULE};
						WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(Collections.singletonList(readingRule), readingRule.getReadingField().getModule(), reading, null, recordPlaceHolders, context, false, workflowRuleCacheMap, false, Collections.singletonList(readingRule.getActivityTypeEnum()), ruleTypes);						
						prevRDM = currentRDM;
						
						Boolean isAlarmCreated = (Boolean) context.get(FacilioConstants.ContextNames.IS_ALARM_CREATED);
						if(isAlarmCreated != null && isAlarmCreated) {
							alarmCount++;
							context.put(FacilioConstants.ContextNames.IS_ALARM_CREATED, Boolean.FALSE);
						}

						List<ReadingEventContext> currentEvent = (List<ReadingEventContext>) context.remove(EventConstants.EventContextNames.EVENT_LIST);
						if (CollectionUtils.isNotEmpty(currentEvent)) {
							latestEvent = currentEvent.get(0);
//							LOGGER.info("Event from history : "+FieldUtil.getAsJSON(latestEvent).toJSONString());
							readingEvents.addAll(currentEvent);
						}
					}
				}
				catch (Exception e) {
					StringBuilder builder = new StringBuilder("Error during execution of rule : ");
					builder.append(readingRule.getId());
					builder.append(" for Record : ")
							.append(reading.getId())
							.append(" of module : ")
							.append(readingRule.getReadingField().getModule().getName());
					LOGGER.log(Level.SEVERE, builder.toString(), e);
					throw e;
				}
			}
			if (readingRule.clearAlarm()) {
				if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
//					newClearLatestAlarm(latestEvent, readingRule, readingEvents);
				}
				else {
					clearLatestAlarms(alarmMetaMap, readingRule);
				}
			}
		}
		return alarmCount;
	}


	private void newClearLatestAlarm(ReadingEventContext event, ReadingRuleContext rule, List<ReadingEventContext> events) throws Exception {
		if (event != null && !event.getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
			int interval = ReadingsAPI.getDataInterval(event.getResource().getId(), rule.getReadingField());
			ReadingEventContext clearEvent = rule.constructClearEvent(event.getResource(), event.getCreatedTime() + (interval * 60 * 1000),event.getEventMessage());
			clearEvent.setComment("System auto cleared Historical Alarm because associated rule executed false for the associated resource");
			if (clearEvent != null) {
				events.add(clearEvent);
			}
		}
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
	
	private Map<String, List<ReadingDataMeta>> getSupportingData(List<WorkflowFieldContext> fields, long startTime, long endTime, long resourceId) throws Exception {
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
					selectBuilder = new SelectRecordsBuilder<ReadingContext>()
										.select(selectFields)
										.module(valField.getModule())
										.beanClass(ReadingContext.class)
										.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(parentId), PickListOperators.IS))
										.andCondition(CriteriaAPI.getCondition(ttimeField, String.valueOf(startTime), DateOperators.IS_BEFORE))
										.andCondition(CriteriaAPI.getCondition(valField, CommonOperators.IS_NOT_EMPTY))
										.orderBy("TTIME DESC")
										.limit(1)
										;
					values = selectBuilder.get();
					ReadingDataMeta rdm = null;
					if (values != null && !values.isEmpty()) {
						rdm = getRDM(values.get(0), valField);
					}
					else {
						rdm = ReadingsAPI.getReadingDataMeta(parentId, valField);
					}
					if (rdm != null) {
						supportingValues.put(rdmKey, Collections.singletonList(rdm));
					}
				}
			}
		}
		return supportingValues;
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
}
