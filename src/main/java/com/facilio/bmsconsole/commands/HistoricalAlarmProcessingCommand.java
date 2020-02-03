package com.facilio.bmsconsole.commands;	

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLogsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleLoggerAPI;
import com.facilio.bmsconsole.util.WorkflowRuleResourceLoggerAPI;
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
			
			List<ReadingEventContext> readingEvents = fetchAllEventsBasedOnAlarmDeletionRange(ruleId, resourceId, lesserStartTime, greaterEndTime);
			
			if (readingEvents != null && !readingEvents.isEmpty())
			{
				FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain();
				addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, readingEvents);
				addEvent.execute();
				
				LOGGER.debug("Added Events: "+ readingEvents +" for alarm processing job Id: "+parentRuleLoggerContext.getId());
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
			LOGGER.error("HISTORICAL RULE ALARM PROCESSING JOB COMMAND FAILED, JOB ID -- : "+parentRuleResourceLoggerId+ " Exception -- " + exceptionMessage + " Trace -- " + stack);
			
			if(parentRuleResourceLoggerContext != null) {		
				WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.FAILED.getIntVal());				
			}
			throw historicalAlarmProcessingException;		
		}
		return false;
	}
	
	private List<ReadingEventContext> fetchAllEventsBasedOnAlarmDeletionRange(long ruleId,long resourceId,long lesserStartTime,long greaterEndTime) throws Exception  
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(Type.READING_ALARM);
		FacilioModule eventModule = modBean.getModule(moduleName);
		
		SelectRecordsBuilder<ReadingEventContext> selectEventbuilder = new SelectRecordsBuilder<ReadingEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(eventModule)
				.beanClass(ReadingEventContext.class)
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", ""+resourceId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lesserStartTime+","+greaterEndTime, DateOperators.BETWEEN))
				.orderBy("CREATED_TIME");
		
		List<ReadingEventContext> completeEvents = selectEventbuilder.get();
		
		if(completeEvents != null && !completeEvents.isEmpty())
		{
			List<Long> readingEventIds = new ArrayList<Long>();
			for(ReadingEventContext readingEvent :completeEvents)
			{
				readingEventIds.add(readingEvent.getSeverity().getId());
			}
			
			Map<Long, AlarmSeverityContext> alarmSeverityMap = AlarmAPI.getAlarmSeverityMap(readingEventIds);
			
			for(ReadingEventContext readingEvent :completeEvents)
			{
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

