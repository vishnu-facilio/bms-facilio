package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.common.JobConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class HistoricalAlarmOccurrenceDeletionCommand extends FacilioCommand implements PostTransactionCommand{
	
	private static final Logger LOGGER = Logger.getLogger(HistoricalAlarmOccurrenceDeletionCommand.class.getName());
	private WorkflowRuleResourceLoggerContext parentRuleResourceLoggerContext = null;
	private Long parentRuleResourceLoggerId = null;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	private int retryCount = 0;
	private Context context;

	public boolean executeCommand(Context context) throws Exception {
		this.context = context;
		try {
			parentRuleResourceLoggerId = (long) context.get(FacilioConstants.ContextNames.HISTORICAL_ALARM_OCCURRENCE_DELETION_JOB_ID);
			retryCount = (int) context.get(FacilioConstants.ContextNames.HISTORICAL_ALARM_OCCURRENCE_DELETION_JOB_RETRY_COUNT);
			parentRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.getWorkflowRuleResourceLoggerById(parentRuleResourceLoggerId);

			WorkflowRuleLoggerContext parentRuleLoggerContext = WorkflowRuleLoggerAPI.getWorkflowRuleLoggerById(parentRuleResourceLoggerContext.getParentRuleLoggerId());
			Long primaryRuleId = parentRuleLoggerContext.getRuleId(); 
			Long actualStartTime = parentRuleLoggerContext.getStartTime();
			Long actualEndTime = parentRuleLoggerContext.getEndTime();
			JSONObject loggerInfo = parentRuleResourceLoggerContext.getLoggerInfo();
			RuleJobType ruleJobType = parentRuleResourceLoggerContext.getRuleJobTypeEnum();
			Type type = Type.valueOf(ruleJobType.getValue());
			if (primaryRuleId == null || loggerInfo == null || ruleJobType == null || type == null) {
				return false;
			}
			ExecuteHistoricalRule historyExecutionType = ruleJobType.getHistoryRuleExecutionType();
			Criteria deletionCriteria = historyExecutionType.getOccurrenceDeletionCriteria(loggerInfo, type);
			Criteria eventsFetchCriteria = historyExecutionType.getEventsProcessingCriteria(loggerInfo, type);

			DateRange modifiedDateRange = WorkflowRuleHistoricalAlarmsAPI.deleteAllAlarmOccurrencesBasedonCriteria(deletionCriteria, eventsFetchCriteria, actualStartTime, actualEndTime, type, loggerInfo);
			if(ruleJobType == RuleJobType.SENSOR_ROLLUP_ALARM && ruleJobType.getRollUpAlarmType() != null) {
				WorkflowRuleHistoricalAlarmsAPI.deleteAllAlarmOccurrencesBasedonCriteria(deletionCriteria, eventsFetchCriteria, modifiedDateRange.getStartTime(), modifiedDateRange.getEndTime(), type, loggerInfo);
				WorkflowRuleHistoricalAlarmsAPI.deleteMultipleAlarmOccurrencesBasedonCriteria(historyExecutionType.getOccurrenceDeletionCriteria(loggerInfo, Type.valueOf(ruleJobType.getRollUpAlarmType().getIndex())), historyExecutionType.getEventsProcessingCriteria(loggerInfo, Type.valueOf(ruleJobType.getRollUpAlarmType().getIndex())), modifiedDateRange.getStartTime(), modifiedDateRange.getEndTime(), Type.valueOf(ruleJobType.getRollUpAlarmType().getIndex()));		
			}
			 		
			updateParentRuleResourceLoggerToModifiedRangeAndEventGeneratingState(parentRuleResourceLoggerContext, modifiedDateRange);
			
			triggerGroupedTimeSplitRuleResourceLoggers(parentRuleResourceLoggerId);		
			
			//rule data point deletion starts
			
			WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(primaryRuleId,false,false);
			
			if(rule instanceof ReadingRuleContext) {
				ReadingRuleContext readingRule = (ReadingRuleContext) rule;
				if(readingRule.getDataModuleId() > 0) {
					
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					
					Criteria criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", parentRuleResourceLoggerContext.getResourceId()+"", NumberOperators.EQUALS));
					criteria.addAndCondition(CriteriaAPI.getCondition("TTIME", "ttime", actualStartTime+","+actualEndTime, DateOperators.BETWEEN));
					
					DeleteRecordBuilder<ReadingContext> deleteBuilder = new DeleteRecordBuilder<ReadingContext>()
							.module(modBean.getModule(readingRule.getDataModuleId()))
							.andCriteria(criteria);
					
					deleteBuilder.delete();
				}
			}
			
			//rule data point deletion ends
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
		
		for(WorkflowRuleHistoricalLogsContext ruleResourceLoggerContext:ruleResourceGroupedLoggers) {
			FacilioTimer.scheduleOneTimeJobWithDelay(ruleResourceLoggerContext.getId(), "HistoricalRuleEventRunJob", 30, "history", (int)context.getOrDefault(JobConstants.LOGGER_LEVEL, -1)); //For events, splitted start and end time would be fetched from the loggers
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
