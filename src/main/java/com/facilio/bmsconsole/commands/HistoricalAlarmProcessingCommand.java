package com.facilio.bmsconsole.commands;	

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.time.DateTimeUtil;

public class HistoricalAlarmProcessingCommand extends FacilioCommand implements PostTransactionCommand{
	
	private static final Logger LOGGER = LogManager.getLogger(HistoricalAlarmProcessingCommand.class.getName());
	private WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext = null;
	private Long parentRuleResourceLoggerId = null;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			parentRuleResourceLoggerId = (long) context.get(FacilioConstants.ContextNames.HISTORICAL_ALARM_PROCESSING_JOB_ID);
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
				List<BaseEventContext> preAlarmEvents = fetchAllEventsBasedOnAlarmDeletionRange(ruleId, alarmRule ,resourceId, lesserStartTime, greaterEndTime, Type.PRE_ALARM);
				if(preAlarmEvents != null) {
					baseEvents.addAll(preAlarmEvents);
				}
			}	
			List<BaseEventContext> readingEvents = fetchAllEventsBasedOnAlarmDeletionRange(ruleId, alarmRule,resourceId, lesserStartTime, greaterEndTime,  Type.READING_ALARM);
			if(readingEvents != null) {
				baseEvents.addAll(readingEvents);
			}
			
			if (baseEvents != null && !baseEvents.isEmpty())
			{
				FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(true);
				addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
				addEvent.execute();
				
				LOGGER.debug("Added Events: "+ baseEvents +" for alarm processing job Id: "+parentRuleLoggerContext.getId());
				Integer alarmOccurrenceCount = (Integer) addEvent.getContext().get(FacilioConstants.ContextNames.ALARM_COUNT);
				if(alarmOccurrenceCount != null)
				{
					parentRuleResourceLoggerContext.setAlarmCount(alarmOccurrenceCount);
				}				
			}	
			WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.RESOLVED.getIntVal());				
		}
		
		catch (Exception historicalAlarmProcessingException) {	
			exceptionMessage = historicalAlarmProcessingException.getMessage();
			stack = historicalAlarmProcessingException.getStackTrace();
			LOGGER.error("HISTORICAL RULE ALARM PROCESSING JOB COMMAND FAILED, JOB ID -- : "+parentRuleResourceLoggerId+ " parentRuleResourceLoggerContext --: " +parentRuleResourceLoggerContext+ " Exception -- " + exceptionMessage + " Trace -- " + stack);
			
			if(parentRuleResourceLoggerContext != null) {		
				WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.FAILED.getIntVal());				
			}
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
			List<Long> readingEventIds = new ArrayList<Long>();

			for(BaseEventContext readingEvent :completeEvents)
			{
				readingEventIds.add(readingEvent.getSeverity().getId());
			}
			
			Map<Long, AlarmSeverityContext> alarmSeverityMap = AlarmAPI.getAlarmSeverityMap(readingEventIds);
			
			for(BaseEventContext readingEvent :completeEvents)
			{
				if (readingEvent instanceof  ReadingEventContext) {
					ReadingEventContext readingEventContext = (ReadingEventContext) readingEvent;
					readingEventContext.setRule(alarmRule.getPreRequsite());
					readingEventContext.setSubRule(alarmRule.getAlarmTriggerRule());
				}
				else if (readingEvent instanceof PreEventContext) {
					PreEventContext preEvent = (PreEventContext) readingEvent;
					preEvent.setRule(alarmRule.getPreRequsite());
					preEvent.setSubRule(alarmRule.getAlarmTriggerRule());
				}
				readingEvent.getSeverity().setSeverity(alarmSeverityMap.get(readingEvent.getSeverity().getId()).getSeverity());
				readingEvent.setSeverityString(alarmSeverityMap.get(readingEvent.getSeverity().getId()).getSeverity());
			}
		}	
		return completeEvents;
	}

	@Override
	public boolean postExecute() throws Exception {	
		
		long parentRuleLoggerId = parentRuleResourceLoggerContext.getParentRuleLoggerId();
		WorkflowRuleLoggerContext parentRuleLoggerContext = WorkflowRuleLoggerAPI.getWorkflowRuleLoggerById(parentRuleLoggerId);
		List<Map<String, Object>> props = WorkflowRuleResourceLoggerAPI.getResolvedWorkflowRuleResourceLogsAndAlarmCountByParentRuleLoggerId(parentRuleLoggerId); //checking all childs completion
		
		if(props != null && !props.isEmpty() && (long)props.get(0).get("parentRuleLoggerId") == parentRuleLoggerId)
		{		
			if(props.get(0).get("count") != null) 
			{
				parentRuleLoggerContext.setResolvedResourcesCount((long) props.get(0).get("count"));	
				if(parentRuleLoggerContext.getResolvedResourcesCount() == parentRuleLoggerContext.getNoOfResources())
				{
					parentRuleLoggerContext.setStatus(WorkflowRuleLoggerContext.Status.RESOLVED.getIntVal());
					parentRuleLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
				}
			}
			if(props.get(0).get("sum") != null)	{
				parentRuleLoggerContext.setTotalAlarmCount(Long.valueOf(String.valueOf(props.get(0).get("sum"))));
			}	
			WorkflowRuleLoggerAPI.updateWorkflowRuleLogger(parentRuleLoggerContext);	
		}	
		return false;
	}
}

