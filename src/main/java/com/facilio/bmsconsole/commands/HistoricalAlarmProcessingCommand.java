package com.facilio.bmsconsole.commands;	

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Level;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
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
			
			WorkflowRuleLoggerContext parentRuleLoggerContext = WorkflowRuleLoggerAPI.getWorkflowRuleLoggerById(parentRuleResourceLoggerContext.getParentRuleLoggerId());
			Long ruleId = parentRuleLoggerContext.getRuleId();
			Long resourceId = parentRuleResourceLoggerContext.getResourceId();
			Long lesserStartTime = parentRuleResourceLoggerContext.getModifiedStartTime();
			Long greaterEndTime = parentRuleResourceLoggerContext.getModifiedEndTime();
			Long totalAlarmOccurrenceCount = 0l;

			AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(ruleId),null);
			ReadingRuleContext triggerRule = alarmRule.getAlarmTriggerRule();
			
			if(triggerRule.isConsecutive() || triggerRule.getOverPeriod() > 0 || triggerRule.getOccurences() > 1) {
				WorkflowRuleHistoricalAlarmsDeletionAPI.deleteEntireAlarmOccurrences(ruleId, resourceId, lesserStartTime, greaterEndTime, AlarmOccurrenceContext.Type.READING);
				totalAlarmOccurrenceCount = fetchAndProcessAllEventsBasedOnAlarmDeletionRange(ruleId, alarmRule ,resourceId, lesserStartTime, greaterEndTime, Type.PRE_ALARM, totalAlarmOccurrenceCount);
			}
			else {	
				totalAlarmOccurrenceCount = fetchAndProcessAllEventsBasedOnAlarmDeletionRange(ruleId, alarmRule,resourceId, lesserStartTime, greaterEndTime, Type.READING_ALARM, totalAlarmOccurrenceCount);				
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
	
	private Long fetchAndProcessAllEventsBasedOnAlarmDeletionRange(long ruleId, AlarmRuleContext alarmRule,long resourceId,long lesserStartTime,long greaterEndTime, Type type, Long totalAlarmOccurrenceCount) throws Exception
	{
		final int EVENTS_FETCH_LIMIT_COUNT = 5000; 
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(type);
		FacilioModule eventModule = modBean.getModule(moduleName);
		SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(eventModule)
				.beanClass(NewEventAPI.getEventClass(type))
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", ""+resourceId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lesserStartTime+","+greaterEndTime, DateOperators.BETWEEN));	
		
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
				
				LOGGER.info("Events added in alarm processing job: "+parentRuleResourceLoggerId+" Reading Rule : "+ruleId+" for resource : "+resourceId+" Size  -- "+baseEvents.size()+ " events -- "+baseEvents);				

				Integer alarmOccurrenceCount = (Integer) addEvent.getContext().get(FacilioConstants.ContextNames.ALARM_COUNT);
				lastOccurrenceOfPreviousBatchMap = (HashMap<String,AlarmOccurrenceContext>) addEvent.getContext().get(EventConstants.EventContextNames.LAST_OCCURRENCE_OF_PREVIOUS_BATCH);
				if(alarmOccurrenceCount != null) {
					totalAlarmOccurrenceCount += alarmOccurrenceCount;
				}
			}	
			baseEvents = batchSelect.get();
			setHistoricalPropsForBaseEvents(baseEvents, alarmRule);		
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
			
			LOGGER.info("Events added in final alarm processing job: "+parentRuleResourceLoggerId+" Reading Rule : "+ruleId+" for resource : "+resourceId+" Size  -- "+baseEvents.size()+ " events -- "+baseEvents);				
		
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
			WorkflowRuleLoggerAPI.updateWorkflowRuleLogger(parentRuleLoggerContext);
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
					//deleteAllEvents() //between lesser startime and greater end time (with ao and alarm id as null)
				}	
				long parentRuleLoggerId = parentRuleResourceLoggerContext.getParentRuleLoggerId();
				WorkflowRuleLoggerContext parentRuleLoggerContext = WorkflowRuleLoggerAPI.getWorkflowRuleLoggerById(parentRuleLoggerId);
				propagateStatusToRuleLog(parentRuleLoggerContext);
				WorkflowRuleLoggerAPI.updateWorkflowRuleLogger(parentRuleLoggerContext);
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
	
	private void setHistoricalPropsForBaseEvents(List<BaseEventContext> baseEvents, AlarmRuleContext alarmRule) throws Exception
	{
		if(baseEvents != null && !baseEvents.isEmpty())
		{
			List<Long> baseEventSeverityIds = new ArrayList<Long>();
			for(BaseEventContext baseEvent :baseEvents) {
				baseEventSeverityIds.add(baseEvent.getSeverity().getId());
			}
			Map<Long, AlarmSeverityContext> alarmSeverityMap = AlarmAPI.getAlarmSeverityMap(baseEventSeverityIds);
			
			for(BaseEventContext baseEvent :baseEvents)
			{
				if (baseEvent instanceof ReadingEventContext) {
					ReadingEventContext readingEventContext = (ReadingEventContext) baseEvent;
					readingEventContext.setRule(alarmRule.getPreRequsite());
					readingEventContext.setSubRule(alarmRule.getAlarmTriggerRule());
				}
				else if (baseEvent instanceof PreEventContext) {
					PreEventContext preEvent = (PreEventContext) baseEvent;
					preEvent.setRule(alarmRule.getPreRequsite());
					preEvent.setSubRule(alarmRule.getAlarmTriggerRule());
				}
				baseEvent.getSeverity().setSeverity(alarmSeverityMap.get(baseEvent.getSeverity().getId()).getSeverity());
				baseEvent.setSeverityString(alarmSeverityMap.get(baseEvent.getSeverity().getId()).getSeverity());
			}				
		}
	}
}

