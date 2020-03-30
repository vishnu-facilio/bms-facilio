package com.facilio.bmsconsole.commands;	

import java.util.ArrayList;
import java.util.Collection;
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
			
			List<BaseEventContext> baseEvents = new ArrayList<BaseEventContext>();
			AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(ruleId),null);
			ReadingRuleContext triggerRule = alarmRule.getAlarmTriggerRule();
			
			if(triggerRule.isConsecutive() || triggerRule.getOverPeriod() != -1 || triggerRule.getOccurences() > 1) {
				List<BaseEventContext> preEvents = fetchAllEventsBasedOnAlarmDeletionRange(ruleId, alarmRule ,resourceId, lesserStartTime, greaterEndTime, Type.PRE_ALARM);
				if(preEvents != null) {
					baseEvents.addAll(preEvents);
				}
				WorkflowRuleHistoricalAlarmsDeletionAPI.deleteEntireAlarmOccurrences(ruleId, resourceId, lesserStartTime, greaterEndTime, AlarmOccurrenceContext.Type.READING);
			}
			else {	
				baseEvents.addAll(fetchAllEventsBasedOnAlarmDeletionRange(ruleId, alarmRule,resourceId, lesserStartTime, greaterEndTime, Type.READING_ALARM));				
			}
			
			if (baseEvents != null && !baseEvents.isEmpty())
			{
				FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(true);
				addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
				addEvent.execute();
				
				//LOGGER.info("Added Events: "+ baseEvents +" for alarm processing job Id: "+parentRuleLoggerContext.getId());
				Integer alarmOccurrenceCount = (Integer) addEvent.getContext().get(FacilioConstants.ContextNames.ALARM_COUNT);
				if(alarmOccurrenceCount != null)
				{
					parentRuleResourceLoggerContext.setAlarmCount(alarmOccurrenceCount);
				}				
			}
			
			if(parentRuleResourceLoggerContext.getStatus() == WorkflowRuleResourceLoggerContext.Status.PARTIALLY_PROCESSED_STATE.getIntVal()) {
				WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.PARTIALLY_COMPLETED_STATE.getIntVal());				
			}
			else {
				WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.RESOLVED.getIntVal());				
			}
		}
		
		catch (Exception historicalAlarmProcessingException) {	
			exceptionMessage = historicalAlarmProcessingException.getMessage();
			stack = historicalAlarmProcessingException.getStackTrace();
			LOGGER.severe("HISTORICAL RULE ALARM PROCESSING JOB COMMAND FAILED, JOB ID -- : "+parentRuleResourceLoggerId+ " parentRuleResourceLoggerContext --: " +parentRuleResourceLoggerContext+ " Exception -- " + exceptionMessage + " Trace -- " + stack);
			throw historicalAlarmProcessingException;		
		}
		return false;
	}
	
	private List<BaseEventContext> fetchAllEventsBasedOnAlarmDeletionRange(long ruleId, AlarmRuleContext alarmRule ,long resourceId,long lesserStartTime,long greaterEndTime, Type type) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(type);
		FacilioModule eventModule = modBean.getModule(moduleName);
		SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(eventModule)
				.beanClass(NewEventAPI.getEventClass(type))
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", ""+resourceId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lesserStartTime+","+greaterEndTime, DateOperators.BETWEEN))
				.orderBy("CREATED_TIME");
		
		List<BaseEventContext> completeEvents = selectEventbuilder.get();
		
		if(completeEvents != null && !completeEvents.isEmpty())
		{
			List<Long> baseEventSeverityIds = new ArrayList<Long>();

			for(BaseEventContext baseEvent :completeEvents) {
				baseEventSeverityIds.add(baseEvent.getSeverity().getId());
			}
			
			Map<Long, AlarmSeverityContext> alarmSeverityMap = AlarmAPI.getAlarmSeverityMap(baseEventSeverityIds);
			
			for(BaseEventContext baseEvent :completeEvents)
			{
				if (baseEvent instanceof  ReadingEventContext) {
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
		return completeEvents;
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
}

