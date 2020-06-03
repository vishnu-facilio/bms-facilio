package com.facilio.bmsconsole.commands;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ExecuteHistoricalRule;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLogsContext;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLogsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleLoggerAPI;
import com.facilio.bmsconsole.util.WorkflowRuleResourceLoggerAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class RunThroughHistoricalRuleCommand extends FacilioCommand {
	private static final Logger LOGGER = Logger.getLogger(RunThroughHistoricalRuleCommand.class.getName());

	@SuppressWarnings("null")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		final long maximumDailyEventRuleJobsPerOrg = 10000l; 

		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		Integer ruleJobType = (Integer) context.get(FacilioConstants.ContextNames.RULE_JOB_TYPE);
		JSONObject loggerInfo = (JSONObject) context.get(FacilioConstants.ContextNames.HISTORICAL_RULE_LOGGER_PROPS);

		Boolean isInclude = (Boolean) context.get(FacilioConstants.ContextNames.IS_INCLUDE);
		isInclude = (isInclude == null) ? true : isInclude;
		
		ruleJobType = (ruleJobType == null) ? RuleJobType.READING_ALARM.getIndex() : ruleJobType;
		RuleJobType ruleJobTypeEnum = RuleJobType.valueOf(ruleJobType);

		if(range == null || range.getStartTime() == -1 || range.getEndTime() == -1 || ruleJobType == null || ruleJobTypeEnum == null) {
			throw new IllegalArgumentException("Invalid daterange or ruletype to run historical rule.");
		}
		if(range.getStartTime() >= range.getEndTime()) {
			throw new IllegalArgumentException("Start time should be less than the Endtime");
		}
		
		ExecuteHistoricalRule historyRuleExecutionType = ruleJobTypeEnum.getHistoryRuleExecutionType();
		String primaryPropKeyName = historyRuleExecutionType.fetchPrimaryLoggerKey();
		if(loggerInfo == null || loggerInfo.get(primaryPropKeyName) == null) {
			throw new IllegalArgumentException("Insufficient loggerInfo to run through historical rule.");
		}
		
		Long primaryId = (Long)loggerInfo.get(primaryPropKeyName);
		if(ruleJobTypeEnum == RuleJobType.READING_ALARM) {
			AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(primaryId),null);
			ReadingRuleContext triggerRule = alarmRule.getAlarmTriggerRule();
			if(triggerRule.isConsecutive() || triggerRule.getOverPeriod() > 0 || triggerRule.getOccurences() > 1) {
				ruleJobType = RuleJobType.PRE_ALARM.getIndex();
			}	
		}
		
		List<Long> secondaryIds = historyRuleExecutionType.getMatchedSecondaryParamIds(loggerInfo, isInclude);
		if(secondaryIds == null || secondaryIds.isEmpty() || primaryId == -1) {
			throw new IllegalArgumentException("Insufficient params for running historical rule.");
		}
		
		int minutesInterval = 24*60; 								//As of now, splitting up the rule_resource job each day
		List<DateRange> intervals = DateTimeUtil.getTimeIntervals(range.getStartTime(), range.getEndTime(), minutesInterval);
		DateRange firstInterval = intervals.get(0);
		DateRange lastInterval = intervals.get(intervals.size()-1);
		
		long activeCurrentRuleResourceLogs = WorkflowRuleResourceLoggerAPI.getActiveWorkflowRuleResourceLogsByRuleAndResourceId(primaryId, secondaryIds, ruleJobType);
		if(activeCurrentRuleResourceLogs > 0)
		{
			throw new Exception("Rule evaluation is already on progress for the selected asset(s).");
		}
		
		long requestedDailyJobsCount = intervals.size() * secondaryIds.size();
		long activeDailyEventRuleJobsAtPresent = WorkflowRuleHistoricalLogsAPI.getActiveDailyWorkflowRuleHistoricalLogsCount();	
		
		long noOfJobsCanbeCreatedAtPresent = maximumDailyEventRuleJobsPerOrg - activeDailyEventRuleJobsAtPresent;
		if(noOfJobsCanbeCreatedAtPresent < 0) {
			LOGGER.severe("Already present active historical event jobs are more than expected limit being "+(-1*noOfJobsCanbeCreatedAtPresent));
			noOfJobsCanbeCreatedAtPresent = maximumDailyEventRuleJobsPerOrg;
		}
		
		if(requestedDailyJobsCount > noOfJobsCanbeCreatedAtPresent) {
			long assetCountLimitAtPresent = (noOfJobsCanbeCreatedAtPresent/intervals.size()) - 1;
			long intervalLimitAtPresent = (noOfJobsCanbeCreatedAtPresent/secondaryIds.size()) - 2;
			if(assetCountLimitAtPresent <= 0) {
				throw new Exception("Please reduce the specified time range to stay within a period of " +intervalLimitAtPresent+ " days.");
			}
			throw new Exception("Please reduce the number of selected assets to " +assetCountLimitAtPresent+ " or the time range within " +intervalLimitAtPresent+ " days.");
		}
				
		WorkflowRuleLoggerContext workflowRuleLoggerContext = WorkflowRuleLoggerAPI.setWorkflowRuleLoggerContext(primaryId, secondaryIds.size(), range, ruleJobType);
		WorkflowRuleLoggerAPI.addWorkflowRuleLogger(workflowRuleLoggerContext);
		long parentRuleLoggerId = workflowRuleLoggerContext.getId();
		List<Long> workflowRuleResourceParentLoggerIds = new ArrayList<Long>();
		
		String secondaryPropKeyName = historyRuleExecutionType.fetchSecondaryLoggerKey();
		for(Long secondaryId :secondaryIds)
		{	
			if(secondaryPropKeyName != null) {
				loggerInfo.put(secondaryPropKeyName, secondaryId);
			}
			WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.setWorkflowRuleResourceLoggerContext(parentRuleLoggerId, secondaryId, null, ruleJobType, loggerInfo);
			WorkflowRuleResourceLoggerAPI.addWorkflowRuleResourceLogger(workflowRuleResourceLoggerContext);
			long parentRuleResourceId = workflowRuleResourceLoggerContext.getId();
			workflowRuleResourceParentLoggerIds.add(parentRuleResourceId);
			
			if(intervals.size() == 1 && (firstInterval.getStartTime() == lastInterval.getStartTime() && firstInterval.getEndTime() == lastInterval.getEndTime())){
				WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.setWorkflowRuleHistoricalLogsContext(parentRuleResourceId, firstInterval, WorkflowRuleHistoricalLogsContext.LogState.FIRST_AS_WELL_AS_LAST.getIntVal(), ruleJobType);
				WorkflowRuleHistoricalLogsAPI.addWorkflowRuleHistoricalLogsContext(workflowRuleHistoricalLogsContext);	
			}
			else 
			{
				for(DateRange interval:intervals)
				{			
					WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext = new WorkflowRuleHistoricalLogsContext();
					
					if(interval.getStartTime() == firstInterval.getStartTime() && interval.getEndTime() == firstInterval.getEndTime()) {
						workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.setWorkflowRuleHistoricalLogsContext(parentRuleResourceId, interval, WorkflowRuleHistoricalLogsContext.LogState.IS_FIRST_JOB.getIntVal(), ruleJobType);	
					}
					else if(interval.getStartTime() == lastInterval.getStartTime() && interval.getEndTime() == lastInterval.getEndTime()) {
						workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.setWorkflowRuleHistoricalLogsContext(parentRuleResourceId, interval, WorkflowRuleHistoricalLogsContext.LogState.IS_LAST_JOB.getIntVal(), ruleJobType);	
					}
					else {
						workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.setWorkflowRuleHistoricalLogsContext(parentRuleResourceId, interval, null, ruleJobType);	
					}	
					WorkflowRuleHistoricalLogsAPI.addWorkflowRuleHistoricalLogsContext(workflowRuleHistoricalLogsContext);	
				}		
			}			
		}	
		
		if(!workflowRuleResourceParentLoggerIds.isEmpty()) {
			for(Long parentRuleResourceLoggerId :workflowRuleResourceParentLoggerIds)
			{		
				FacilioTimer.scheduleOneTimeJobWithDelay(parentRuleResourceLoggerId, "HistoricalAlarmOccurrenceDeletionJob", 30, "history");
				if(ruleJobTypeEnum == RuleJobType.RULE_ROLLUP_ALARM || ruleJobTypeEnum == RuleJobType.ASSET_ROLLUP_ALARM) {
					LOGGER.info("Added triggered RuleAssetRollUpJobs with jobId: " +parentRuleResourceLoggerId+ " for primary alarmRollUpId: " +primaryId+ " and secondary alarmRollUpIds: " +secondaryIds+ " with RuleJobType: " +ruleJobTypeEnum.getValue() + " at: "+System.currentTimeMillis());				
				}
			}
		}

		return false;
	}
}
