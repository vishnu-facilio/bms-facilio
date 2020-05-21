package com.facilio.bmsconsole.commands;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
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
	private static final Logger LOGGER = LogManager.getLogger(RunThroughHistoricalRuleCommand.class.getName());

	@SuppressWarnings("null")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		final long maximumDailyEventRuleJobsPerOrg = 10000l; 

		long ruleId = (long) context.get(FacilioConstants.ContextNames.RULE_ID);
		Integer ruleJobType = (Integer) context.get(FacilioConstants.ContextNames.RULE_JOB_TYPE);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);

		List<Long> resourceIds = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
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
		
		if(ruleJobTypeEnum == RuleJobType.READING_ALARM) {
			AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(ruleId),null);
			ReadingRuleContext triggerRule = alarmRule.getAlarmTriggerRule();
			if(triggerRule.isConsecutive() || triggerRule.getOverPeriod() != -1 || triggerRule.getOccurences() > 1) {
				ruleJobType = RuleJobType.PRE_ALARM.getIndex();
			}	
		}
		
		HashMap<Long, List<Long>> primaryVsSecondaryIds = ruleJobTypeEnum.getHistoryRuleExecutionType().getRuleAndResourceIds(ruleId, isInclude, resourceIds);
		if(primaryVsSecondaryIds == null || MapUtils.isEmpty(primaryVsSecondaryIds) || primaryVsSecondaryIds.size() != 1) {
			throw new IllegalArgumentException("Undefined params for running historical rule.");
		}
		
		long primaryId = -1;
		List<Long> secondaryIds = new ArrayList<Long>();
		for(Long ruleOrResourceId: primaryVsSecondaryIds.keySet()) {
			primaryId = ruleOrResourceId;
			secondaryIds = primaryVsSecondaryIds.get(ruleOrResourceId);
		}
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
			LOGGER.log(Level.ERROR, "Already present active historical event jobs are more than expected limit being "+(-1*noOfJobsCanbeCreatedAtPresent));
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
		
		for(Long finalResourceId :secondaryIds)
		{	
			String messageKey = NewEventAPI.getMessageKey(ruleJobTypeEnum, primaryId, finalResourceId);
			WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.setWorkflowRuleResourceLoggerContext(parentRuleLoggerId, finalResourceId, null, ruleJobType, messageKey);
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
			}
		}

		return false;
	}
}
