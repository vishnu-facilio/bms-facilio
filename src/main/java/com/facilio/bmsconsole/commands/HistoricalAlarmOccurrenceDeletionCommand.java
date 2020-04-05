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
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class HistoricalAlarmOccurrenceDeletionCommand extends FacilioCommand implements PostTransactionCommand{
	
	private static final Logger LOGGER = Logger.getLogger(HistoricalAlarmOccurrenceDeletionCommand.class.getName());
	private WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext = null;
	private Long parentRuleResourceLoggerId = null;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	private int retryCount = 0;

	public boolean executeCommand(Context context) throws Exception {
		
		try {
			parentRuleResourceLoggerId = (long) context.get(FacilioConstants.ContextNames.HISTORICAL_ALARM_OCCURRENCE_DELETION_JOB_ID);
			retryCount = (int) context.get(FacilioConstants.ContextNames.HISTORICAL_ALARM_OCCURRENCE_DELETION_JOB_RETRY_COUNT);
			parentRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLoggerById(parentRuleResourceLoggerId);

			WorkflowRuleLoggerContext parentRuleLoggerContext = WorkflowRuleLoggerAPI.getWorkflowRuleLoggerById(parentRuleResourceLoggerContext.getParentRuleLoggerId());
			Long ruleId = parentRuleLoggerContext.getRuleId(); 
			Long actualStartTime = parentRuleLoggerContext.getStartTime();
			Long actualEndTime = parentRuleLoggerContext.getEndTime();
			Long resourceId = parentRuleResourceLoggerContext.getResourceId();
			if (ruleId == null || resourceId == null) {
				return false;
			}
			
			DateRange modifiedDateRange = new DateRange();	

			AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(ruleId),null);
			ReadingRuleContext triggerRule = alarmRule.getAlarmTriggerRule();
			if(triggerRule.isConsecutive() || triggerRule.getOverPeriod() != -1 || triggerRule.getOccurences() > 1) {
				modifiedDateRange = WorkflowRuleHistoricalAlarmsDeletionAPI.deleteEntireAlarmOccurrences(ruleId, resourceId, actualStartTime, actualEndTime, AlarmOccurrenceContext.Type.PRE_OCCURRENCE);
			}
			else{
				modifiedDateRange = WorkflowRuleHistoricalAlarmsDeletionAPI.deleteEntireAlarmOccurrences(ruleId, resourceId, actualStartTime, actualEndTime, AlarmOccurrenceContext.Type.READING);
			}
						 		
			updateParentRuleResourceLoggerToModifiedRangeAndEventGeneratingState(parentRuleResourceLoggerContext, modifiedDateRange);
			
			triggerGroupedTimeSplitRuleResourceLoggers(parentRuleResourceLoggerId);		
		}
		
		catch (Exception historicalRuleDeletionException) {
			exceptionMessage = historicalRuleDeletionException.getMessage();
			stack = historicalRuleDeletionException.getStackTrace();
			LOGGER.severe("HISTORICAL RULE ALARM OCCURRENCE DELETION COMMAND FAILED, JOB ID -- : "+parentRuleResourceLoggerId+ " parentRuleResourceLoggerContext --: " +  parentRuleResourceLoggerContext + " Exception -- " + exceptionMessage + " StackTrace -- " + String.valueOf(stack));			
			throw historicalRuleDeletionException;		
		}
		return false;
	}
	

	
	public static void updateParentRuleResourceLoggerToModifiedRangeAndEventGeneratingState(WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext, DateRange modifiedRange) throws Exception {
		parentRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.EVENT_GENERATING_STATE.getIntVal());
		parentRuleResourceLoggerContext.setModifiedStartTime(modifiedRange.getStartTime()); //lesserStartTime
		parentRuleResourceLoggerContext.setModifiedEndTime(modifiedRange.getEndTime()); 	//greaterEndTime
		WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceLoggerContext(parentRuleResourceLoggerContext);
	}	
	
	public static void triggerGroupedTimeSplitRuleResourceLoggers(long parentRuleResourceLoggerId) throws Exception {
		
		List<WorkflowRuleHistoricalLogsContext> ruleResourceGroupedLoggers = WorkflowRuleHistoricalLogsAPI.getWorkflowRuleHistoricalLogsByParentRuleResourceId(parentRuleResourceLoggerId);
		
		List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdates = new ArrayList<>();

		for(WorkflowRuleHistoricalLogsContext ruleResourceLoggerContext:ruleResourceGroupedLoggers) {
			ruleResourceLoggerContext.setStatus(WorkflowRuleHistoricalLogsContext.Status.IN_PROGRESS.getIntVal());

            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchValue.setWhereId(ruleResourceLoggerContext.getId());
            batchValue.addUpdateValue("status", ruleResourceLoggerContext.getStatus());
            batchUpdates.add(batchValue);
		}

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleHistoricalLogsModule().getTableName())
                .fields(Collections.singletonList(FieldFactory.getField("status", "STATUS", ModuleFactory.getWorkflowRuleHistoricalLogsModule(), FieldType.NUMBER)));
        updateBuilder.batchUpdateById(batchUpdates);				
	}
	
	@Override
	public boolean postExecute() throws Exception {
		
		List<WorkflowRuleHistoricalLogsContext> ruleResourceGroupedLoggers = WorkflowRuleHistoricalLogsAPI.getWorkflowRuleHistoricalLogsByParentRuleResourceId(parentRuleResourceLoggerId);
		
		for(WorkflowRuleHistoricalLogsContext ruleResourceLoggerContext:ruleResourceGroupedLoggers)
		{
			FacilioTimer.scheduleOneTimeJobWithDelay(ruleResourceLoggerContext.getId(), "HistoricalEventRunForReadingRuleJob", 30, "history"); //For events, splitted start and end time would be fetched from the loggers
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
				if(retryCount == 0) {
					NewTransactionService.newTransaction(() -> 
						WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.RESCHEDULED.getIntVal()));	
				}
				else if(retryCount == 1) {
					NewTransactionService.newTransaction(() -> 
					WorkflowRuleResourceLoggerAPI.updateWorkflowRuleResourceContextState(parentRuleResourceLoggerContext, WorkflowRuleResourceLoggerContext.Status.FAILED.getIntVal()));	
				}			
			}
			else  {
				LOGGER.severe("HISTORICAL RULE ALARM OCCURRENCE DELETION IS NULL IN ONERROR FOR JOB -- " + parentRuleResourceLoggerId);
			}		
		}
		catch (Exception e) {
			LOGGER.severe("HISTORICAL RULE ALARM OCCURRENCE DELETION JOB Failed In On Error -- "+parentRuleResourceLoggerId+" Exception --  "+e);
		}
	}
	
}
