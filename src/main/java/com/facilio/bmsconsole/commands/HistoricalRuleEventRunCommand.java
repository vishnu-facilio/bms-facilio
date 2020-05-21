package com.facilio.bmsconsole.commands;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.jobs.SingleResourceHistoricalFormulaCalculatorJob;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.LoggerAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLogsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleLoggerAPI;
import com.facilio.bmsconsole.util.WorkflowRuleResourceLoggerAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;

public class HistoricalRuleEventRunCommand extends FacilioCommand implements PostTransactionCommand{

private static final Logger LOGGER = Logger.getLogger(HistoricalRuleEventRunCommand.class.getName());
	
	private WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext = null;
	private Long jobId;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	private boolean isFailed = false;
	Boolean isManualFailed = false;
	
	@Override
	public boolean executeCommand(Context jobContext) throws Exception {
		
	try {
		long jobStartTime = System.currentTimeMillis();
		jobId = (long) jobContext.get(FacilioConstants.ContextNames.HISTORICAL_EVENT_RULE_JOB_ID);
		workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.getWorkflowRuleHistoricalLogsContextById(jobId);
		
		if(workflowRuleHistoricalLogsContext != null && workflowRuleHistoricalLogsContext.getStatusAsEnum() != null)
		{
			workflowRuleHistoricalLogsContext.setCalculationStartTime(jobStartTime);
			Long startTime = workflowRuleHistoricalLogsContext.getSplitStartTime();
			Long endTime = workflowRuleHistoricalLogsContext.getSplitEndTime();
			Integer logState = workflowRuleHistoricalLogsContext.getLogState();
			Long parentRuleResourceId = workflowRuleHistoricalLogsContext.getParentRuleResourceId();
			RuleJobType ruleJobType = workflowRuleHistoricalLogsContext.getRuleJobTypeEnum();
			Type type = Type.valueOf(ruleJobType.getValue());

			WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLoggerById(parentRuleResourceId);
			String messageKey = workflowRuleResourceLoggerContext.getMessageKey();
			Long resourceId = workflowRuleResourceLoggerContext.getResourceId();
			
			DateRange dateRange = new DateRange(startTime, endTime);
			WorkflowRuleLoggerContext workflowRuleLoggerContext = WorkflowRuleLoggerAPI.getWorkflowRuleLoggerById(workflowRuleResourceLoggerContext.getParentRuleLoggerId());
			Long ruleId = workflowRuleLoggerContext.getRuleId();

			LOGGER.info("HistoricalRuleEventRunCommand started for jobId: "+ jobId +" Reading Rule : "+ruleId+" for resource : "+resourceId+ " between "+ dateRange.getStartTime() +" and "+dateRange.getEndTime()+" with the jobtriggeredtime at: "+ jobStartTime);	
			Boolean isFirstIntervalJob = false,isLastIntervalJob = false;
			
			if(logState != null) {
				isFirstIntervalJob = (logState == WorkflowRuleHistoricalLogsContext.LogState.IS_FIRST_JOB.getIntVal() || logState == WorkflowRuleHistoricalLogsContext.LogState.FIRST_AS_WELL_AS_LAST.getIntVal()) ? Boolean.TRUE : Boolean.FALSE;
				isLastIntervalJob = (logState == WorkflowRuleHistoricalLogsContext.LogState.IS_LAST_JOB.getIntVal() || logState == WorkflowRuleHistoricalLogsContext.LogState.FIRST_AS_WELL_AS_LAST.getIntVal()) ? Boolean.TRUE : Boolean.FALSE;
			}
			
			HashMap<String, Boolean> jobStatesMap = constructJobStates(isFirstIntervalJob, isLastIntervalJob, isManualFailed);
			List<BaseEventContext> baseEvents = ruleJobType.getHistoryRuleExecutionType().executeRuleAndGenerateEvents(messageKey, dateRange, jobStatesMap, jobId);
			
			long eventInsertStartTime = System.currentTimeMillis();
			if(baseEvents != null && !baseEvents.isEmpty()) {
				insertEventsWithoutAlarmOccurrenceProcessed(baseEvents, type);			
				LOGGER.info("HistoricalRuleEventRunCommand Events added in daily job: "+jobId+" Rule : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" Events Size  -- "+baseEvents.size()+ " Events -- "+baseEvents);						
			}
			long eventInsertEndTime = System.currentTimeMillis();

			LOGGER.info("HistoricalRuleEventRunCommand Time taken for Historical Run for jobId: "+jobId+" Reading Rule : "+ruleId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is -- " +(System.currentTimeMillis() - jobStartTime)+ 
					" and Event insertion time is -- " +(eventInsertEndTime - eventInsertStartTime));
			WorkflowRuleHistoricalLogsAPI.updateWorkflowRuleHistoricalLogsContextState(workflowRuleHistoricalLogsContext, WorkflowRuleHistoricalLogsContext.Status.RESOLVED.getIntVal());
		}		
	}
	
	catch (Exception historicalRuleException) {
		exceptionMessage = historicalRuleException.getMessage();
		stack = historicalRuleException.getStackTrace();
		isFailed = true;

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
			LOGGER.severe("HISTORICAL RULE RESOURCE EVENT JOB COMMAND FAILED, JOB ID -- : "+ jobId +" ExceptionMessage -- " + exceptionMessage + " StackTrace -- " + String.valueOf(stack));
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
			if(isFailed && !isManualFailed) {
				parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.PARTIALLY_PROCESSED_STATE.getIntVal());
			}
			else {
				parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.ALARM_PROCESSING_STATE.getIntVal());
			}			
			int rowsUpdated = WorkflowRuleResourceLoggerAPI.updateEventGeneratingParentWorkflowRuleResourceLoggerContext(parentRuleResourceLoggerContext);
			if(rowsUpdated == 1)
			{
				FacilioTimer.scheduleOneTimeJobWithDelay(parentRuleResourceLoggerContext.getId(), "HistoricalAlarmProcessingJob", 30, "history");
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
				
				if(isManualFailed) { //Failed by us
					NewTransactionService.newTransaction(() -> WorkflowRuleHistoricalLogsAPI.updateWorkflowRuleHistoricalLogsContextState(workflowRuleHistoricalLogsContext, WorkflowRuleHistoricalLogsContext.Status.SKIPPED.getIntVal()));
				}
				else {
					NewTransactionService.newTransaction(() -> WorkflowRuleHistoricalLogsAPI.updateWorkflowRuleHistoricalLogsContextState(workflowRuleHistoricalLogsContext, WorkflowRuleHistoricalLogsContext.Status.FAILED.getIntVal()));
					CommonCommandUtil.emailException(HistoricalRuleEventRunCommand.class.getName(), "Historical Run Failed for Reading_Rule_Resource_Event_Logger : "+jobId, mailExp);
					LOGGER.log(Level.SEVERE, exceptionMessage);	
				}
			
				long parentRuleResourceLoggerId = workflowRuleHistoricalLogsContext.getParentRuleResourceId();
				long activeRuleResourceGroupedLoggerIds = WorkflowRuleHistoricalLogsAPI.getActiveWorkflowRuleHistoricalLogsCountByParentRuleResourceId(parentRuleResourceLoggerId); //checking all childs completion
				if(activeRuleResourceGroupedLoggerIds == 0)
				{
					WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLoggerById(parentRuleResourceLoggerId);
					if(isFailed && !isManualFailed) {
						parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.PARTIALLY_PROCESSED_STATE.getIntVal());
					}
					else {
						parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.ALARM_PROCESSING_STATE.getIntVal());
					}
					int rowsUpdated = WorkflowRuleResourceLoggerAPI.updateEventGeneratingParentWorkflowRuleResourceLoggerContext(parentRuleResourceLoggerContext);
					if(rowsUpdated == 1)
					{
						FacilioTimer.scheduleOneTimeJobWithDelay(parentRuleResourceLoggerContext.getId(), "HistoricalAlarmProcessingJob", 30, "history");
					}
				}			
			}
			else  {
				LOGGER.severe("HISTORICAL RULERESOURCEEVENT LOGGER IS NULL IN ONERROR FOR JOB -- " + jobId);
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
	
	private void insertEventsWithoutAlarmOccurrenceProcessed(List<BaseEventContext> events, Type type) throws Exception
	{	
		List<AlarmSeverityContext> alarmSeverityList = AlarmAPI.getAlarmSeverityList();
		HashMap<String,AlarmSeverityContext> alarmSeverityStringMap = new HashMap<String,AlarmSeverityContext>();
		for(AlarmSeverityContext alarmSeverity:alarmSeverityList) {
			alarmSeverityStringMap.put(alarmSeverity.getSeverity(),alarmSeverity);	
		}
		
		for(BaseEventContext event:events)
		{	
			event.setSeverity(alarmSeverityStringMap.get(event.getSeverityString()));
			event.setMessageKey(event.constructMessageKey());
			event.setAlarmOccurrence(null);
			event.setBaseAlarm(null);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = NewEventAPI.getEventModuleName(type);	
		InsertRecordBuilder<BaseEventContext> builder = new InsertRecordBuilder<BaseEventContext>()
				.moduleName(moduleName)
				.fields(modBean.getAllFields(moduleName));

		builder.addRecords(events);
		builder.save();			
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
	
	private HashMap<String, Boolean> constructJobStates(Boolean isFirstIntervalJob, Boolean isLastIntervalJob, Boolean isManualFailed) throws Exception
	{
		HashMap<String, Boolean> jobStatesMap = new HashMap<String, Boolean>();
		jobStatesMap.put("isFirstIntervalJob", isFirstIntervalJob);
		jobStatesMap.put("isLastIntervalJob", isLastIntervalJob);
		jobStatesMap.put("isManualFailed", isManualFailed);
		return jobStatesMap;
	}
}

