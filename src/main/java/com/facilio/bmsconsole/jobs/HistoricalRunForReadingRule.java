package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Chain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class HistoricalRunForReadingRule extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(HistoricalRunForReadingRule.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long jobStartTime = System.currentTimeMillis();
			ReadingRuleContext readingRule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(jc.getJobId(), true);
			if (readingRule.getMatchedResources() == null || readingRule.getMatchedResources().isEmpty()) {
				return;
			}
			
			JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			long startTime = (long) props.get("startTime");
			long endTime = (long) props.get("endTime");
			
//			LOGGER.info("Historical execution of rule : "+readingRule.getId()+" for resources : "+readingRule.getMatchedResources().keySet());
			Map<String, List<ReadingDataMeta>> supportFieldsRDM = null;
			List<WorkflowFieldContext> fields = null;
			if (readingRule.getWorkflow() != null) {
				fields = WorkflowUtil.getWorkflowFields(readingRule.getWorkflow().getId());
//				LOGGER.info("Dependent fields : "+fields);
			
				if (fields != null && !fields.isEmpty()) {
					supportFieldsRDM = getSupportingData(fields, startTime, endTime, -1);
//					LOGGER.info("Support Fields RDM Values size : "+supportFieldsRDM.size());
				}
			}

			List<ReadingEventContext> events = new ArrayList<>();
			for (long resourceId : readingRule.getMatchedResources().keySet()) {
//				LOGGER.info("Gonna fetch data and execute rule for resource : "+resourceId);
				Map<String, List<ReadingDataMeta>> currentFields = supportFieldsRDM;
				Map<String, List<ReadingDataMeta>> currentRDMList = null;
				if (readingRule.getWorkflow() != null) {
					currentRDMList = getSupportingData(fields, startTime, endTime, resourceId);
				}
				if (currentRDMList != null && !currentRDMList.isEmpty()) {
//					LOGGER.info("Current resource Support Fields RDM Values size : "+currentRDMList.size());
					if (supportFieldsRDM == null) {
						currentFields = currentRDMList;
					}
					else {
						currentFields = new HashMap<>(supportFieldsRDM);
						currentFields.putAll(currentRDMList);
					}
				}
				
				long processStartTime = System.currentTimeMillis();
				long currentStartTime = startTime - (ReadingsAPI.getDataInterval(resourceId, readingRule.getReadingField()) * 60 * 1000);
				List<ReadingContext> readings = fetchReadings(readingRule, resourceId, currentStartTime, endTime);
				executeWorkflows(readingRule, readings, currentFields, fields, events);
				LOGGER.info("Time taken for Historical Run for Reading Rule : "+jc.getJobId()+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - processStartTime));
			}

			if (!events.isEmpty()) {
				FacilioContext context = new FacilioContext();
				context.put(EventConstants.EventContextNames.EVENT_LIST, events);
				Chain addEvent = TransactionChainFactory.getV2AddEventChain();
				addEvent.execute(context);
			}

			long timeTaken = (System.currentTimeMillis() - jobStartTime);
			LOGGER.info("Total Time taken for Historical Run for Reading Rule : "+jc.getJobId()+" between "+startTime+" and "+endTime+" is "+timeTaken);
			
//			if (AccountUtil.getCurrentOrg().getId() == 135 || AccountUtil.getCurrentOrg().getId() == 134) {
				JSONObject json = new JSONObject();
				json.put("to", "praveen@facilio.com, manthosh@facilio.com, shivaraj@facilio.com, vasanth@facilio.com");
				json.put("sender", "noreply@facilio.com");
				json.put("subject", "Historical Run completed for Rule : "+jc.getJobId());
				json.put("message", "Total Time taken for Historical Run for Reading Rule : "+jc.getJobId()+" between "+startTime+" and "+endTime+" is "+timeTaken);
				
				AwsUtil.sendEmail(json);
//			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			CommonCommandUtil.emailException("HistoricalRunForReadingRule", "Historical Run failed for reading rule : "+jc.getJobId(), e);
		}
	}
	
	private void executeWorkflows(ReadingRuleContext readingRule, List<ReadingContext> readings, Map<String, List<ReadingDataMeta>> supportFieldsRDM, List<WorkflowFieldContext> fields, List<ReadingEventContext> readingEvents) throws Exception {
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
						
						RuleType[] ruleTypes = {RuleType.READING_RULE,RuleType.ALARM_TRIGGER_RULE,RuleType.ALARM_CLEAR_RULE,RuleType.ALARM_RCA_RULES};
						
						WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(Collections.singletonList(readingRule), readingRule.getReadingField().getModule(), reading, null, null, recordPlaceHolders, context, false, Collections.singletonList(readingRule.getEvent().getActivityTypeEnum()), ruleTypes);
						
						prevRDM = currentRDM;

						List<ReadingEventContext> currentEvent = (List<ReadingEventContext>) context.remove(EventConstants.EventContextNames.EVENT_LIST);
						if (CollectionUtils.isNotEmpty(currentEvent)) {
							latestEvent = currentEvent.get(0);
							LOGGER.info("Event from history : "+FieldUtil.getAsJSON(latestEvent).toJSONString());
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
					LOGGER.log(Level.ERROR, builder.toString(), e);
					throw e;
				}
			}
			if (readingRule.clearAlarm()) {
				if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
					newClearLatestAlarm(latestEvent, readingRule, readingEvents);
				}
				else {
					clearLatestAlarms(alarmMetaMap, readingRule);
				}
			}
		}
	}

	private void newClearLatestAlarm(ReadingEventContext event, ReadingRuleContext rule, List<ReadingEventContext> events) throws Exception {
		if (event != null && !event.getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
			int interval = ReadingsAPI.getDataInterval(event.getResource().getId(), rule.getReadingField());
			ReadingEventContext clearEvent = rule.constructClearEvent(event.getResource(), event.getCreatedTime() + (interval * 60 * 1000));
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
				Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
				getAddEventChain.execute(addEventContext);
			}
		}
	}
	
	private void getOtherRDMs(long resourceId, long ttime, Map<String, List<ReadingDataMeta>> rdmMap, Map<String, ReadingDataMeta> rdmCache, Map<String, Integer> lastItr, List<WorkflowFieldContext> fields) {
		if (rdmMap != null && !rdmMap.isEmpty() && fields != null && !fields.isEmpty()) {
			for (WorkflowFieldContext field : fields) {
				if (field.getAggregationEnum() == AggregateOperator.SpecialAggregateOperator.LAST_VALUE) {
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
			if (field.getAggregationEnum() == AggregateOperator.SpecialAggregateOperator.LAST_VALUE) {
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
