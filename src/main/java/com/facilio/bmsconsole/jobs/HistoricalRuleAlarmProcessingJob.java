package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class HistoricalRuleAlarmProcessingJob extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(HistoricalRuleAlarmProcessingJob.class.getName());

	
	@Override
	public void execute(JobContext jc) throws Exception {
		
		Long parentRuleResourceLoggerId = null;
		WorkflowRuleHistoricalLoggerContext parentRuleResourceLoggerContext = null;
		
		try {
			parentRuleResourceLoggerId = jc.getJobId();
			JSONObject props = BmsJobUtil.getJobProps(parentRuleResourceLoggerId, jc.getJobName());
			Long ruleId = (Long) props.get("ruleId");
			Long resourceId = (Long) props.get("resourceId");
			Long lesserStartTime = (Long) props.get("startTime");
			Long greaterEndTime = (Long) props.get("endTime");	
			
			 parentRuleResourceLoggerContext = WorkflowRuleHistoricalLoggerUtil.getWorkflowRuleHistoricalLoggerById(parentRuleResourceLoggerId);
			
			List<ReadingEventContext> readingEvents = fetchAllEventsBasedOnAlarmDeletionRange(ruleId, resourceId, lesserStartTime, greaterEndTime);
			
			if (!readingEvents.isEmpty())
			{
				FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain();
				addEvent.getContext().put(EventConstants.EventContextNames.EVENT_LIST, readingEvents);
				addEvent.execute();
				
				System.out.println("Added Events: "+ readingEvents);
				Integer alarmOccurrenceCount = (Integer) addEvent.getContext().get(FacilioConstants.ContextNames.ALARM_COUNT);
				if(alarmOccurrenceCount != null)
				{
					parentRuleResourceLoggerContext.setAlarmCount(alarmOccurrenceCount);
				}				
				WorkflowRuleHistoricalLoggerUtil.updateRuleLoggerContextToResolvedState(parentRuleResourceLoggerContext);				
			}		
		}
		
		catch (Exception historicalAlarmProcessingException) {
			LOGGER.error("HISTORICAL RULE ALARM PROCESSING JOB COMMAND FAILED, JOB ID -- : "+parentRuleResourceLoggerId+ " Exception -- " +historicalAlarmProcessingException.getMessage());
			if(parentRuleResourceLoggerContext != null)
			{
				historicalAlarmProcessingException.printStackTrace();
				parentRuleResourceLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.FAILED.getIntVal());
				parentRuleResourceLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
				WorkflowRuleHistoricalLoggerUtil.updateWorkflowRuleHistoricalLogger(parentRuleResourceLoggerContext);
			}
		}
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
		
		return completeEvents;
	}
}