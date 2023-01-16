package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLogsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleResourceLoggerAPI;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.taskengine.common.JobConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HistoricalRuleEventRunCommand extends FacilioCommand implements PostTransactionCommand {

	private static final Logger LOGGER = LogManager.getLogger(HistoricalRuleEventRunCommand.class.getName());
	
	private WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext = null;
	private Long jobId;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	private boolean isFailed = false;
	HashMap<String, Boolean> jobStatesMap = new HashMap<String, Boolean>();
	private Context context;
	@Override
	public boolean executeCommand(Context jobContext) throws Exception {
		this.context = jobContext;
	try {
		long jobStartTime = System.currentTimeMillis();
		jobId = (long) jobContext.get(FacilioConstants.ContextNames.HISTORICAL_EVENT_RULE_JOB_ID);
		workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.getWorkflowRuleHistoricalLogsContextById(jobId);
		
		if(workflowRuleHistoricalLogsContext != null && workflowRuleHistoricalLogsContext.getStatusAsEnum() != null)
		{
			workflowRuleHistoricalLogsContext.setCalculationStartTime(jobStartTime);
			Long parentSecondaryId = workflowRuleHistoricalLogsContext.getParentRuleResourceId();
			Long startTime = workflowRuleHistoricalLogsContext.getSplitStartTime();
			Long endTime = workflowRuleHistoricalLogsContext.getSplitEndTime();
			Integer logState = workflowRuleHistoricalLogsContext.getLogState();
			RuleJobType ruleJobType = workflowRuleHistoricalLogsContext.getRuleJobTypeEnum();
			ExecuteHistoricalRule historyExecutionType = ruleJobType.getHistoryRuleExecutionType();

			WorkflowRuleResourceLoggerContext secondaryRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLoggerById(parentSecondaryId);
			Long secondaryId = secondaryRuleResourceLoggerContext.getResourceId();
			JSONObject loggerInfo = secondaryRuleResourceLoggerContext.getLoggerInfo();
			Long primaryId = (Long)loggerInfo.get(historyExecutionType.fetchPrimaryLoggerKey());
			loggerInfo.put("ruleJobType",  ruleJobType.getIndex());
			
			DateRange dateRange = new DateRange(startTime, endTime);
			Type type = Type.valueOf(ruleJobType.getValue());
	
			LOGGER.info("HistoricalRuleEventRunCommand started for jobId: "+ jobId +" Primary Rule : "+ primaryId +" for secondaryId : "+secondaryId+ " between "+ dateRange.getStartTime() +" and "+dateRange.getEndTime()+" with the jobtriggeredtime at: "+ jobStartTime + " and Type: "+type);	
			Boolean isFirstIntervalJob = false,isLastIntervalJob = false;
			
			if(logState != null) {
				isFirstIntervalJob = (logState == WorkflowRuleHistoricalLogsContext.LogState.IS_FIRST_JOB.getIndex() || logState == WorkflowRuleHistoricalLogsContext.LogState.FIRST_AS_WELL_AS_LAST.getIndex()) ? Boolean.TRUE : Boolean.FALSE;
				isLastIntervalJob = (logState == WorkflowRuleHistoricalLogsContext.LogState.IS_LAST_JOB.getIndex() || logState == WorkflowRuleHistoricalLogsContext.LogState.FIRST_AS_WELL_AS_LAST.getIndex()) ? Boolean.TRUE : Boolean.FALSE;
			}
			
			jobStatesMap = constructJobStates(isFirstIntervalJob, isLastIntervalJob, false);
			loggerInfo.put("ruleResourceLogger", secondaryRuleResourceLoggerContext);
			List<BaseEventContext> baseEvents = historyExecutionType.executeRuleAndGenerateEvents(loggerInfo, dateRange, jobStatesMap, jobId);
			
			long eventInsertStartTime = System.currentTimeMillis();
			if(baseEvents != null && !baseEvents.isEmpty()) {
				insertEventsWithoutAlarmOccurrenceProcessed(baseEvents);			
				LOGGER.info("HistoricalRuleEventRunCommand Events added in daily job: "+jobId+" PrimaryRule : "+primaryId+" for secondaryId : "+secondaryId+" between "+startTime+" and "+endTime+" Events Size  -- "+baseEvents.size()+ " Events -- "+baseEvents);						
			}

			LOGGER.info("HistoricalRuleEventRunCommand Time taken for Historical Run for jobId: "+jobId+" Primary Rule : "+primaryId+" for secondaryId : "+secondaryId+" between "+startTime+" and "+endTime+" is -- " +(System.currentTimeMillis() - jobStartTime)+ 
					" and Event insertion time is -- " +(System.currentTimeMillis() - eventInsertStartTime));
			WorkflowRuleHistoricalLogsAPI.updateWorkflowRuleHistoricalLogsContextState(workflowRuleHistoricalLogsContext, WorkflowRuleHistoricalLogsContext.Status.RESOLVED.getIndex());
		}		
	}
	
	catch (Exception historicalRuleException) {
		exceptionMessage = historicalRuleException.getMessage();
		stack = historicalRuleException.getStackTrace();
		isFailed = true;
		Boolean isManualFailed = jobStatesMap.get("isManualFailed");

		if(exceptionMessage != null && isManualFailed) {
			workflowRuleHistoricalLogsContext.setErrorMessage(exceptionMessage);
		}
		else if(historicalRuleException instanceof SQLException && !isManualFailed) {
			workflowRuleHistoricalLogsContext.setErrorMessage("Sorry there seems to be a connectivity issue with our system right now. Please try re-running the rule for the current timeline.");
		}	
		else if(!isManualFailed) {
			workflowRuleHistoricalLogsContext.setErrorMessage("Sorry there seems to be a technical problem. Check your configurations and try re-running the rule for the current timeline.");
		}
				
		if(!isManualFailed) {
			LOGGER.debug("HISTORICAL RULE RESOURCE EVENT JOB COMMAND FAILED, JOB ID -- : "+ jobId +" ExceptionMessage -- " + exceptionMessage + " StackTrace -- " + ExceptionUtils.getStackTrace(historicalRuleException));
		}
		
		throw historicalRuleException;
	}	
	return false;
	}
	
	@Override
	public boolean postExecute() throws Exception {
		
		long parentRuleResourceLoggerId = workflowRuleHistoricalLogsContext.getParentRuleResourceId();
		long activeRuleResourceGroupedLoggerIds = WorkflowRuleHistoricalLogsAPI.getActiveWorkflowRuleHistoricalLogsCountByParentRuleResourceId(parentRuleResourceLoggerId); //checking all childs completion
		if(activeRuleResourceGroupedLoggerIds == 0)
		{
			WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLoggerById(parentRuleResourceLoggerId);
			Boolean isManualFailed = jobStatesMap.get("isManualFailed");
			if(isFailed && !isManualFailed) {
				parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.PARTIALLY_PROCESSED_STATE.getIndex());
			}
			else {
				parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.ALARM_PROCESSING_STATE.getIndex());
			}			
			int rowsUpdated = WorkflowRuleResourceLoggerAPI.updateEventGeneratingParentWorkflowRuleResourceLoggerContext(parentRuleResourceLoggerContext);
			if(rowsUpdated == 1)
			{
				FacilioTimer.scheduleOneTimeJobWithDelay(parentRuleResourceLoggerContext.getId(), "HistoricalAlarmProcessingJob", 30, "history", (int) context.getOrDefault(JobConstants.LOGGER_LEVEL, -1));
			}
		}	
		return false;
	}
	
	public void onError() throws Exception {
		constructErrorMessage();
	}
	
	public void constructErrorMessage() throws Exception 
	{
		try {
			Exception mailExp = new Exception(exceptionMessage);
			if (stack != null) {
				mailExp.setStackTrace(stack);
			}

			if(workflowRuleHistoricalLogsContext != null)	{
				Boolean isManualFailed = (Boolean)jobStatesMap.get("isManualFailed");
				if(isManualFailed) { //Failed by us
					NewTransactionService.newTransaction(() -> WorkflowRuleHistoricalLogsAPI.updateWorkflowRuleHistoricalLogsContextState(workflowRuleHistoricalLogsContext, WorkflowRuleHistoricalLogsContext.Status.SKIPPED.getIndex()));
				}
				else {
					NewTransactionService.newTransaction(() -> WorkflowRuleHistoricalLogsAPI.updateWorkflowRuleHistoricalLogsContextState(workflowRuleHistoricalLogsContext, WorkflowRuleHistoricalLogsContext.Status.FAILED.getIndex()));
					CommonCommandUtil.emailException(HistoricalRuleEventRunCommand.class.getName(), "Historical Run Failed for Reading_Rule_Resource_Event_Logger : "+jobId, mailExp);
					LOGGER.error(exceptionMessage);
				}
			
				long parentRuleResourceLoggerId = workflowRuleHistoricalLogsContext.getParentRuleResourceId();
				long activeRuleResourceGroupedLoggerIds = WorkflowRuleHistoricalLogsAPI.getActiveWorkflowRuleHistoricalLogsCountByParentRuleResourceId(parentRuleResourceLoggerId); //checking all childs completion
				if(activeRuleResourceGroupedLoggerIds == 0)
				{
					WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLoggerById(parentRuleResourceLoggerId);
					if(isFailed && !isManualFailed) {
						parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.PARTIALLY_PROCESSED_STATE.getIndex());
					}
					else {
						parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.ALARM_PROCESSING_STATE.getIndex());
					}
					int rowsUpdated = WorkflowRuleResourceLoggerAPI.updateEventGeneratingParentWorkflowRuleResourceLoggerContext(parentRuleResourceLoggerContext);
					if(rowsUpdated == 1)
					{
						FacilioTimer.scheduleOneTimeJobWithDelay(parentRuleResourceLoggerContext.getId(), "HistoricalAlarmProcessingJob", 30, "history");
					}
				}			
			}
			else  {
				LOGGER.debug("HISTORICAL RULERESOURCEEVENT LOGGER IS NULL IN ONERROR FOR JOB -- " + jobId);
			}	
			
		}
		catch (Exception e) 
		{
			CommonCommandUtil.emailException("Historical Rule Exception Handling failed",
					"Historical Rule Exception Handling failed - orgid -- " + AccountUtil.getCurrentOrg().getId()+ ", JOB ID -- " +jobId, e);
			LOGGER.debug("Historical Rule Exception Handling failed  --"+jobId);
			LOGGER.debug(e.getMessage(), e);
		}
	}
	
	private void insertEventsWithoutAlarmOccurrenceProcessed(List<BaseEventContext> events) throws Exception
	{	
		List<AlarmSeverityContext> alarmSeverityList = AlarmAPI.getAlarmSeverityList();
		HashMap<String,AlarmSeverityContext> alarmSeverityStringMap = new HashMap<String,AlarmSeverityContext>();
		for(AlarmSeverityContext alarmSeverity:alarmSeverityList) {
			alarmSeverityStringMap.put(alarmSeverity.getSeverity(),alarmSeverity);	
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<Type, List<BaseEventContext>> eventsMap = new HashMap<>();
		for(BaseEventContext event:events)
		{	
			event.setSeverity(alarmSeverityStringMap.get(event.getSeverityString()));
			event.setMessageKey(event.constructMessageKey());
			event.setAlarmOccurrence(null);
			event.setBaseAlarm(null);
			event.setIsLiveEvent(false);
			
			List<BaseEventContext> list = eventsMap.get(event.getEventTypeEnum());
			if (list == null) {
				list = new ArrayList<>();
				eventsMap.put(event.getEventTypeEnum(), list);
			}
			list.add(event);
		}
		
		if (MapUtils.isNotEmpty(eventsMap)) {
			for (Type eventType : eventsMap.keySet()) {
				String moduleName = NewEventAPI.getEventModuleName(eventType);
				InsertRecordBuilder<BaseEventContext> insertBuilder = new InsertRecordBuilder<BaseEventContext>()
						.moduleName(moduleName).fields(modBean.getAllFields(moduleName));
				insertBuilder.addRecords(eventsMap.get(eventType));
				insertBuilder.save();
			}
		}			
	}
	
	private HashMap<String, Boolean> constructJobStates(Boolean isFirstIntervalJob, Boolean isLastIntervalJob, Boolean isManualFailed) throws Exception
	{
		HashMap<String, Boolean> jobStatesMap = new HashMap<String, Boolean>();
		jobStatesMap.put("isFirstIntervalJob", isFirstIntervalJob);
		jobStatesMap.put("isLastIntervalJob", isLastIntervalJob);
		jobStatesMap.put("isManualFailed", isManualFailed);
		return jobStatesMap;
	}
}

