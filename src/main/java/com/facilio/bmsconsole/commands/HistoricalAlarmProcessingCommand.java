package com.facilio.bmsconsole.commands;	

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.bmsconsole.commands.RuleRollupCommand.RollupType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class HistoricalAlarmProcessingCommand extends FacilioCommand implements PostTransactionCommand{
	
	private static final Logger LOGGER = Logger.getLogger(HistoricalAlarmProcessingCommand.class.getName());
	private WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext = null;
	private Long parentRuleResourceLoggerId = null;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	private int retryCount = 0;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			parentRuleResourceLoggerId = (long) context.get(FacilioConstants.ContextNames.HISTORICAL_ALARM_PROCESSING_JOB_ID);
			retryCount = (int) context.get(FacilioConstants.ContextNames.HISTORICAL_ALARM_PROCESSING_JOB_RETRY_COUNT);
			parentRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLoggerById(parentRuleResourceLoggerId);
			
			Long lesserStartTime = parentRuleResourceLoggerContext.getModifiedStartTime();
			Long greaterEndTime = parentRuleResourceLoggerContext.getModifiedEndTime();
			JSONObject loggerInfo = parentRuleResourceLoggerContext.getLoggerInfo();
			RuleJobType ruleJobType = parentRuleResourceLoggerContext.getRuleJobTypeEnum();
			Type type = Type.valueOf(ruleJobType.getValue());
			Long totalAlarmOccurrenceCount = 0l;
			
			ExecuteHistoricalRule historyExecutionType = ruleJobType.getHistoryRuleExecutionType();
			Criteria eventsFetchCriteria = historyExecutionType.getEventsProcessingCriteria(loggerInfo, type);
			Long primaryRuleId = (Long)loggerInfo.get(historyExecutionType.fetchPrimaryLoggerKey());
			
			if(type == Type.PRE_ALARM) {
				Criteria readingOccurenceDeletionCriteria = historyExecutionType.getOccurrenceDeletionCriteria(loggerInfo, Type.READING_ALARM);
				Criteria readingEventsDeletionCriteria = historyExecutionType.getEventsProcessingCriteria(loggerInfo, Type.READING_ALARM);
				DateRange modifiedDateRange = WorkflowRuleHistoricalAlarmsAPI.deleteAllAlarmOccurrencesBasedonCriteria(readingOccurenceDeletionCriteria, readingEventsDeletionCriteria, lesserStartTime, greaterEndTime, Type.READING_ALARM);
				totalAlarmOccurrenceCount = fetchAndProcessAllEventsBasedOnAlarmDeletionRange(primaryRuleId, eventsFetchCriteria, modifiedDateRange.getStartTime(), modifiedDateRange.getEndTime(), Type.PRE_ALARM, totalAlarmOccurrenceCount);
			}
			else {	
				totalAlarmOccurrenceCount = fetchAndProcessAllEventsBasedOnAlarmDeletionRange(primaryRuleId, eventsFetchCriteria, lesserStartTime, greaterEndTime, type, totalAlarmOccurrenceCount);				
			}			
		
			if(parentRuleResourceLoggerContext.getStatus() == WorkflowRuleResourceLoggerContext.Status.PARTIALLY_PROCESSED_STATE.getIntVal()) {
				parentRuleResourceLoggerContext.setAlarmCount(totalAlarmOccurrenceCount);
				WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.PARTIALLY_COMPLETED_STATE.getIntVal());
			}
			else {
				parentRuleResourceLoggerContext.setAlarmCount(totalAlarmOccurrenceCount);
				WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.RESOLVED.getIntVal());	
			}
		}
		
		catch (Exception historicalAlarmProcessingException) {	
			exceptionMessage = historicalAlarmProcessingException.getMessage();
			stack = historicalAlarmProcessingException.getStackTrace();
			LOGGER.severe("HISTORICAL RULE ALARM PROCESSING JOB COMMAND FAILED, JOB ID -- : "+parentRuleResourceLoggerId+ " parentRuleResourceLoggerContext --: " +parentRuleResourceLoggerContext+ " Exception -- " + exceptionMessage + " StackTrace -- " + String.valueOf(stack));
			throw historicalAlarmProcessingException;		
		}
		return false;
	}
	
	private Long fetchAndProcessAllEventsBasedOnAlarmDeletionRange(long primaryRuleId, Criteria fetchEventsCriteria, long lesserStartTime, long greaterEndTime, Type type, Long totalAlarmOccurrenceCount) throws Exception
	{
		final int EVENTS_FETCH_LIMIT_COUNT = 5000; 
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(type);
		FacilioModule eventModule = modBean.getModule(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(eventModule.getName()));

		SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(eventModule)
				.beanClass(NewEventAPI.getEventClass(type))
				.andCriteria(fetchEventsCriteria)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), lesserStartTime+","+greaterEndTime, DateOperators.BETWEEN))	
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("eventType"), String.valueOf(type.getIndex()), NumberOperators.EQUALS));
		
		HashMap<String, AlarmOccurrenceContext> lastOccurrenceOfPreviousBatchMap = new HashMap<String, AlarmOccurrenceContext>();
		List<BaseEventContext> baseEvents = new ArrayList<BaseEventContext>();
		SelectRecordsBuilder.BatchResult<BaseEventContext> batchSelect = selectEventbuilder.getInBatches("CREATED_TIME", EVENTS_FETCH_LIMIT_COUNT);
		
		while(batchSelect.hasNext()) 
		{
			if (baseEvents != null && !baseEvents.isEmpty())
			{
				FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(true);
				addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
				addEvent.getContext().put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, true);
				addEvent.getContext().put(EventConstants.EventContextNames.CONSTRUCT_HISTORICAL_AUTO_CLEAR_EVENT, false);
				addEvent.getContext().put(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH, lastOccurrenceOfPreviousBatchMap);
				addEvent.execute();
				
				LOGGER.info("Events added in alarm processing job: "+parentRuleResourceLoggerId+" Primary Rule : "+primaryRuleId+" for fetchEventsCriteria : "+fetchEventsCriteria+" Size  -- "+baseEvents.size()+ " events -- "+baseEvents);				

				Integer alarmOccurrenceCount = (Integer) addEvent.getContext().get(FacilioConstants.ContextNames.ALARM_COUNT);
				lastOccurrenceOfPreviousBatchMap = (HashMap<String,AlarmOccurrenceContext>) addEvent.getContext().get(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH);
				if(alarmOccurrenceCount != null) {
					totalAlarmOccurrenceCount += alarmOccurrenceCount;
				}
			}	
			baseEvents = batchSelect.get();
			setHistoricalSeverityPropsForBaseEvents(baseEvents, primaryRuleId, type); //ruleId for PreEvents processing to ReadingEvents		
		}
		
		//final batch of historical events to proceed with system autoclear
		if (baseEvents != null && !baseEvents.isEmpty())
		{
			FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(true);
			addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
			addEvent.getContext().put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, true);
			addEvent.getContext().put(EventConstants.EventContextNames.CONSTRUCT_HISTORICAL_AUTO_CLEAR_EVENT, true);
			addEvent.getContext().put(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH, lastOccurrenceOfPreviousBatchMap);
			addEvent.execute();
			
			LOGGER.info("Events added in final alarm processing job: "+parentRuleResourceLoggerId+" Primary Rule : "+primaryRuleId+" for fetchEventsCriteria : "+fetchEventsCriteria+" Size  -- "+baseEvents.size()+ " events -- "+baseEvents);				
		
			Integer alarmOccurrenceCount = (Integer) addEvent.getContext().get(FacilioConstants.ContextNames.ALARM_COUNT);
			if(alarmOccurrenceCount != null) {
				totalAlarmOccurrenceCount += alarmOccurrenceCount;
			}
		}
		
		return totalAlarmOccurrenceCount;
	}

	@Override
	public boolean postExecute() throws Exception {	
		try {
			
			long parentRuleLoggerId = parentRuleResourceLoggerContext.getParentRuleLoggerId();
			WorkflowRuleLoggerContext parentRuleLoggerContext = WorkflowRuleLoggerAPI.getWorkflowRuleLoggerById(parentRuleLoggerId);
			List<Map<String, Object>> props = WorkflowRuleResourceLoggerAPI.getResolvedWorkflowRuleResourceLogsAndAlarmCountByParentRuleLoggerId(parentRuleLoggerId); //checking all resource childs completion count
			
			if(props != null && !props.isEmpty() && (long)props.get(0).get("parentRuleLoggerId") == parentRuleLoggerId) {		
				if(props.get(0).get("count") != null) {
					parentRuleLoggerContext.setResolvedResourcesCount((long) props.get(0).get("count"));	
				}
				if(props.get(0).get("sum") != null)	{
					parentRuleLoggerContext.setTotalAlarmCount(Long.valueOf(String.valueOf(props.get(0).get("sum"))));
				}
			}
			propagateStatusToRuleLog(parentRuleLoggerContext);
			int rowsUpdated = WorkflowRuleLoggerAPI.updateWorkflowRuleLogger(parentRuleLoggerContext);
			if(rowsUpdated == 1 && (parentRuleLoggerContext.getStatus() == WorkflowRuleLoggerContext.Status.RESOLVED.getIntVal() || parentRuleLoggerContext.getStatus() == WorkflowRuleLoggerContext.Status.PARTIALLY_COMPLETED.getIntVal())) {
				checkforRollUpAlarms(parentRuleLoggerContext);
			}
		}
		catch (Exception e) {		
			LOGGER.severe("HISTORICAL RULE ALARM PROCESSING JOB Post Execute Failed -- "+parentRuleResourceLoggerId+" Exception --  "+e);
		}
		return false;
	}
	
	public void onError() throws Exception {
		constructErrorMessage();
	}
	
	public void constructErrorMessage() throws Exception 
	{
		try {	
			if(parentRuleResourceLoggerContext != null)	{
				parentRuleResourceLoggerContext.setAlarmCount(-1);
				if(retryCount == 0) {
					NewTransactionService.newTransaction(() -> 
						WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.RESCHEDULED.getIntVal()));	
				}
				else if(retryCount == 1) {
					NewTransactionService.newTransaction(() -> 
					WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.FAILED.getIntVal()));	
				}	
				long parentRuleLoggerId = parentRuleResourceLoggerContext.getParentRuleLoggerId();
				WorkflowRuleLoggerContext parentRuleLoggerContext = WorkflowRuleLoggerAPI.getWorkflowRuleLoggerById(parentRuleLoggerId);
				propagateStatusToRuleLog(parentRuleLoggerContext);
				int rowsUpdated = WorkflowRuleLoggerAPI.updateWorkflowRuleLogger(parentRuleLoggerContext);
 				if(rowsUpdated == 1 && (parentRuleLoggerContext.getStatus() == WorkflowRuleLoggerContext.Status.RESOLVED.getIntVal() || parentRuleLoggerContext.getStatus() == WorkflowRuleLoggerContext.Status.PARTIALLY_COMPLETED.getIntVal())) {
 					checkforRollUpAlarms(parentRuleLoggerContext);
 				}
			}
			else  {
				LOGGER.severe("HISTORICAL RULE ALARM PROCESSING JOB IS NULL IN ONERROR FOR JOB -- " + parentRuleResourceLoggerId);
			}		
		}
		catch (Exception e)  {
			LOGGER.severe("HISTORICAL RULE ALARM PROCESSING JOB Failed In On Error -- "+parentRuleResourceLoggerId+" Exception --  "+e);
		}
	}
	
	private void propagateStatusToRuleLog(WorkflowRuleLoggerContext parentRuleLoggerContext) throws Exception
	{
		List<Map<String, Object>> propList = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLogsStatusCountByParentRuleLoggerId(parentRuleLoggerContext.getId()); 
		if(propList != null && !propList.isEmpty() && (long)propList.get(0).get("parentRuleLoggerId") == parentRuleLoggerContext.getId())
		{
			long resolvedCount = 0, partiallyCompletedCount = 0, rescheduledCount = 0, failedCount = 0, currentLogsCount = 0;
			for(Map<String, Object> prop:propList) {
				if(prop.get("status") != null && prop.get("count") != null) {
					int status = (int)prop.get("status");
					if(status == WorkflowRuleResourceLoggerContext.Status.RESOLVED.getIntVal()) {
						resolvedCount = (long)prop.get("count");
					}
					else if(status == WorkflowRuleResourceLoggerContext.Status.PARTIALLY_COMPLETED_STATE.getIntVal()) {
						partiallyCompletedCount = (long)prop.get("count");
					}
					else if(status == WorkflowRuleResourceLoggerContext.Status.RESCHEDULED.getIntVal()) {
						rescheduledCount = (long)prop.get("count");
					}
					else if(status == WorkflowRuleResourceLoggerContext.Status.FAILED.getIntVal()) {
						failedCount = (long)prop.get("count");
					}	
				}
			}
				
			currentLogsCount = resolvedCount + partiallyCompletedCount + rescheduledCount + failedCount;
			
			if(rescheduledCount > 0)
			{
				parentRuleLoggerContext.setStatus(WorkflowRuleLoggerContext.Status.RESCHEDULED.getIntVal());
				parentRuleLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());	 
			}
			else if(currentLogsCount == parentRuleLoggerContext.getNoOfResources())
			{	
				if(partiallyCompletedCount > 0){
					parentRuleLoggerContext.setStatus(WorkflowRuleLoggerContext.Status.PARTIALLY_COMPLETED.getIntVal());
				}
				else if(partiallyCompletedCount == 0 && parentRuleLoggerContext.getResolvedResourcesCount() == parentRuleLoggerContext.getNoOfResources()) {
					parentRuleLoggerContext.setStatus(WorkflowRuleLoggerContext.Status.RESOLVED.getIntVal());
				}
				else if(partiallyCompletedCount == 0 && failedCount == parentRuleLoggerContext.getNoOfResources()) {
					parentRuleLoggerContext.setStatus(WorkflowRuleLoggerContext.Status.FAILED.getIntVal());
				}
				else {
					parentRuleLoggerContext.setStatus(WorkflowRuleLoggerContext.Status.PARTIALLY_COMPLETED.getIntVal());
				}
				parentRuleLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
			}
		}
	}
	
	private void setHistoricalSeverityPropsForBaseEvents(List<BaseEventContext> baseEvents, long ruleId, Type type) throws Exception
	{
		if(baseEvents != null && !baseEvents.isEmpty())
		{
			List<Long> baseEventSeverityIds = new ArrayList<Long>();
			for(BaseEventContext baseEvent :baseEvents) {
				baseEventSeverityIds.add(baseEvent.getSeverity().getId());
			}
			Map<Long, AlarmSeverityContext> alarmSeverityMap = AlarmAPI.getAlarmSeverityMap(baseEventSeverityIds);
			
			AlarmRuleContext alarmRule = null;
			if(type == Type.READING_ALARM || type == Type.PRE_ALARM) {
				alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(ruleId),null);
			}
			
			for(BaseEventContext baseEvent :baseEvents)
			{
//				if (baseEvent instanceof ReadingEventContext && alarmRule != null) {
//					ReadingEventContext readingEventContext = (ReadingEventContext) baseEvent;
//					readingEventContext.setRule(alarmRule.getPreRequsite());
//					readingEventContext.setSubRule(alarmRule.getAlarmTriggerRule());
//				}
				if (baseEvent instanceof PreEventContext && alarmRule != null) {
					PreEventContext preEvent = (PreEventContext) baseEvent;
					preEvent.setRule(alarmRule.getPreRequsite());
					preEvent.setSubRule(alarmRule.getAlarmTriggerRule());
				}
				baseEvent.getSeverity().setSeverity(alarmSeverityMap.get(baseEvent.getSeverity().getId()).getSeverity());
				baseEvent.setSeverityString(alarmSeverityMap.get(baseEvent.getSeverity().getId()).getSeverity());
			}				
		}
	}
	
	private void checkforRollUpAlarms(WorkflowRuleLoggerContext parentRuleLoggerContext) {
		try {
			if(parentRuleLoggerContext.getRuleJobTypeEnum() == RuleJobType.READING_ALARM || parentRuleLoggerContext.getRuleJobTypeEnum() == RuleJobType.PRE_ALARM) {
				DateRange dateRange = new DateRange(parentRuleLoggerContext.getStartTime(),parentRuleLoggerContext.getEndTime());
				long ruleId = parentRuleLoggerContext.getRuleId();
				List<WorkflowRuleResourceLoggerContext> ruleResourceLoggerList = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLogsByParentRuleLoggerId(parentRuleLoggerContext.getId());
				if(ruleResourceLoggerList != null && !ruleResourceLoggerList.isEmpty()) {
					List<Long> resourceIds = new ArrayList<Long>();
					for(WorkflowRuleResourceLoggerContext ruleResourceLogger: ruleResourceLoggerList) {
						resourceIds.add(ruleResourceLogger.getResourceId());
					}
					//triggerRuleAssetRollUpJobs(resourceIds, RuleJobType.ASSET_ROLLUP_ALARM, dateRange);				
				}
				//triggerRuleAssetRollUpJobs(Collections.singletonList(ruleId), RuleJobType.RULE_ROLLUP_ALARM, dateRange);
			}				
		}
		catch (Exception error) {		
			LOGGER.severe("HISTORICAL RULE/ASSET ROLLUP PROPAGATION ALARM PROCESSING JOB Post Execute Failed -- "+parentRuleResourceLoggerId+" Exception --  "+error);
		}
	}
	
	private void triggerRuleAssetRollUpJobs(List<Long> ids, RuleJobType ruleJobType, DateRange dateRange) throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);
		context.put(FacilioConstants.ContextNames.RULE_JOB_TYPE, ruleJobType.getIndex());
		
		if(ids != null && !ids.isEmpty()) {
			for(long id:ids)
			{
				JSONObject loggerInfo = new JSONObject();
				loggerInfo.put(ruleJobType.getHistoryRuleExecutionType().fetchPrimaryLoggerKey(), id);
				context.put(FacilioConstants.ContextNames.HISTORICAL_RULE_LOGGER_PROPS, loggerInfo);
				FacilioChain runThroughRuleChain = TransactionChainFactory.runThroughHistoricalRuleChain();
				runThroughRuleChain.execute(context);
			}	
			LOGGER.info("Triggered RuleAssetRollUpJobs in HistoricalAlarmProcessing jobId: " +parentRuleResourceLoggerId+ " for rule/resourceIds: " +ids+ " with RuleJobType: " +ruleJobType.getValue());				
		}
	}
}

