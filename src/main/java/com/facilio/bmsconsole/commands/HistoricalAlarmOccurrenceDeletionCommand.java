package com.facilio.bmsconsole.commands;	

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLogsContext;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLogsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleLoggerAPI;
import com.facilio.bmsconsole.util.WorkflowRuleResourceLoggerAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class HistoricalAlarmOccurrenceDeletionCommand extends FacilioCommand implements PostTransactionCommand{
	
	private static final Logger LOGGER = LogManager.getLogger(HistoricalAlarmOccurrenceDeletionCommand.class.getName());
	private WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext = null;
	private Long parentRuleResourceLoggerId = null;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;

	public boolean executeCommand(Context context) throws Exception {
		
		try {
			parentRuleResourceLoggerId = (long) context.get(FacilioConstants.ContextNames.HISTORICAL_ALARM_OCCURRENCE_DELETION_JOB_ID);
			parentRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLoggerById(parentRuleResourceLoggerId);
			
			WorkflowRuleLoggerContext parentRuleLoggerContext = WorkflowRuleLoggerAPI.getWorkflowRuleLoggerById(parentRuleResourceLoggerContext.getParentRuleLoggerId());
			Long ruleId = parentRuleLoggerContext.getRuleId(); 
			Long actualStartTime = parentRuleLoggerContext.getStartTime();
			Long actualEndTime = parentRuleLoggerContext.getEndTime();
			Long resourceId = parentRuleResourceLoggerContext.getResourceId();
			if (ruleId == null || resourceId == null) {
				return false;
			}
			
			DateRange modifiedDateRange = deleteEntireAlarmOccurrences(ruleId, resourceId, actualStartTime, actualEndTime);	
						 		
			updateParentRuleResourceLoggerToModifiedRangeAndEventGeneratingState(parentRuleResourceLoggerContext, modifiedDateRange);
			
			triggerGroupedTimeSplitRuleResourceLoggers(parentRuleResourceLoggerId);		
		}
		
		catch (Exception historicalRuleDeletionException) {
			exceptionMessage = historicalRuleDeletionException.getMessage();
			stack = historicalRuleDeletionException.getStackTrace();
			LOGGER.error("HISTORICAL RULE DELETION JOB COMMAND FAILED, JOB ID -- : "+parentRuleResourceLoggerId+ " Exception -- " + exceptionMessage + " Trace -- " + stack);
			
			if(parentRuleResourceLoggerContext != null) {
				WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.FAILED.getIntVal());				
			}
			
			throw historicalRuleDeletionException;		
		}
		return false;
	}
	
	public static DateRange deleteEntireAlarmOccurrences(long ruleId, long resourceId, long startTime, long endTime) throws Exception 
	{		
		List<AlarmOccurrenceContext> alarmOccurrenceList = NewAlarmAPI.getAllAlarmOccurrences(ruleId, startTime, endTime, resourceId);
	
		if (alarmOccurrenceList != null && !alarmOccurrenceList.isEmpty())
		{
			AlarmOccurrenceContext lastAlarmOccurrence = alarmOccurrenceList.get(alarmOccurrenceList.size() - 1);
			BaseAlarmContext baseAlarm =  lastAlarmOccurrence.getAlarm();
			BaseAlarmContext baseAlarmContext = NewAlarmAPI.getBaseAlarmById(baseAlarm.getId());
			
			if (alarmOccurrenceList.get(0).getCreatedTime() < startTime) 
			{
				AlarmOccurrenceContext initialEdgeAlarmOccurrence = alarmOccurrenceList.get(0);				
				clearAlarmOccurrenceIdForEdgeEvents(initialEdgeAlarmOccurrence, initialEdgeAlarmOccurrence.getCreatedTime(), startTime); 
				startTime = initialEdgeAlarmOccurrence.getCreatedTime();
			}
			if (lastAlarmOccurrence.getClearedTime() == -1 || lastAlarmOccurrence.getClearedTime() > endTime) 
			{
				BaseEventContext finalEvent = getFinalEventForAlarmOccurrence(lastAlarmOccurrence);
				if(finalEvent != null && finalEvent.getCreatedTime() > endTime) {
					clearAlarmOccurrenceIdForEdgeEvents(lastAlarmOccurrence, endTime, finalEvent.getCreatedTime()); //avoid event deletion
					endTime = finalEvent.getCreatedTime();
				}
			}	
			
			deleteAllAlarmOccurences(baseAlarmContext, startTime, endTime);
			NewAlarmAPI.updateBaseAlarmWithNoOfOccurences(baseAlarm);	
			NewAlarmAPI.changeLatestAlarmOccurrence(baseAlarmContext);		
		}
		
		return new DateRange(startTime, endTime);	
	}
	
	public static void clearAlarmOccurrenceIdForEdgeEvents(AlarmOccurrenceContext edgeAlarmOccurrence, long startTime, long endTime) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		List<FacilioField> allEventFields = modBean.getAllFields(eventModule.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allEventFields);
		
		List<FacilioField> eventFields = new ArrayList<FacilioField>();
		eventFields.add(fieldMap.get("baseAlarm"));	
		eventFields.add(fieldMap.get("alarmOccurrence"));
		
		UpdateRecordBuilder<BaseEventContext> updateBuilder = new UpdateRecordBuilder<BaseEventContext>()
				.module(eventModule)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +edgeAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				.fields(eventFields);
		
		BaseAlarmContext baseAlarmNull = new BaseAlarmContext();
		baseAlarmNull.setId(-99);
		
		AlarmOccurrenceContext alarmOccurrenceContextNull = new AlarmOccurrenceContext();
		alarmOccurrenceContextNull.setId(-99);
		
		Map<String, Object> map = new HashMap<>();
		map.put("baseAlarm", FieldUtil.getAsProperties(baseAlarmNull));
		map.put("alarmOccurrence", FieldUtil.getAsProperties(alarmOccurrenceContextNull));
		updateBuilder.updateViaMap(map);
	}
	
	public static BaseEventContext getFinalEventForAlarmOccurrence(AlarmOccurrenceContext finalEdgeCaseAlarmOccurrence) throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		
		SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT))
				.beanClass(BaseEventContext.class)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +finalEdgeCaseAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC").limit(1);
		
		return selectEventbuilder.fetchFirst();
	}
	
	public static void deleteAllAlarmOccurences(BaseAlarmContext baseAlarmContext, long startTime, long endTime) throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
	
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" +baseAlarmContext.getId(), NumberOperators.EQUALS));
	
		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
		criteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", startTime+","+endTime, DateOperators.BETWEEN));	
		
		builder.andCriteria(criteria);
		builder.delete();			
	}
	
	public static void updateParentRuleResourceLoggerToModifiedRangeAndEventGeneratingState(WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext, DateRange modifiedRange) throws Exception {
		parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.EVENT_GENERATING_STATE.getIntVal());
		parentRuleResourceLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
		parentRuleResourceLoggerContext.setModifiedStartTime(modifiedRange.getStartTime()); //lesserStartTime
		parentRuleResourceLoggerContext.setModifiedEndTime(modifiedRange.getEndTime()); 	//greaterEndTime
		WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceLoggerContext(parentRuleResourceLoggerContext);
	}	
	
	public static void triggerGroupedTimeSplitRuleResourceLoggers(long parentRuleResourceLoggerId) throws Exception {
		List<WorkflowRuleHistoricalLogsContext> ruleResourceGroupedLoggerIds = WorkflowRuleHistoricalLogsAPI.getWorkflowRuleHistoricalLogsByParentRuleResourceId(parentRuleResourceLoggerId);
		for(WorkflowRuleHistoricalLogsContext ruleResourceLoggerContext:ruleResourceGroupedLoggerIds)
		{
			FacilioTimer.scheduleOneTimeJobWithDelay(ruleResourceLoggerContext.getId(), "HistoricalRunForReadingRule", 30, "history"); //For events, splitted start and end time would be fetched from the loggers
		}				
	}
	
	@Override
	public boolean postExecute() throws Exception {
		return false;
	}
	
}
