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
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLogsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleLoggerAPI;
import com.facilio.bmsconsole.util.WorkflowRuleResourceLoggerAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class RunThroughReadingRulesCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(RunThroughReadingRulesCommand.class.getName());

	@SuppressWarnings("null")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		final long maximumDailyEventRuleJobsPerOrg = 10000l; 

		long id = (long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		List<Long> resourceIds = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
		Boolean isInclude = (Boolean) context.get(FacilioConstants.ContextNames.IS_INCLUDE);
		Boolean isScaledFlow = (Boolean) context.get(FacilioConstants.ContextNames.IS_SCALED_FLOW);

		if(isInclude == null)
		{
			isInclude = true;
		}
		
		if (id == -1 || range == null || range.getStartTime() == -1 || range.getEndTime() == -1) {
			throw new IllegalArgumentException("In sufficient params for running historical reading rule.");
		}
		
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(id);
		if (rule == null || !(rule instanceof ReadingRuleContext)) {
			throw new IllegalArgumentException("Invalid alarm rule id to run through historical data.");
		}
		
		List<Long> finalResourceIds = new ArrayList<Long>();
		if(resourceIds == null || resourceIds.isEmpty())
		{
			finalResourceIds = getMatchedResourcesIds(rule);
		}
		else if (resourceIds!=null && !resourceIds.isEmpty() && isInclude)
		{
			List<Long> matchedResources = getMatchedResourcesIds(rule);
			for(Long resourceId: resourceIds)
			{
				if(matchedResources.contains(resourceId)) {
					finalResourceIds.add(resourceId);
				}
			}
		}
		else if (resourceIds!=null && !resourceIds.isEmpty() && !isInclude)
		{
			List<Long> matchedResources = getMatchedResourcesIds(rule);
			for(Long matchedResourceId: matchedResources)
			{
				if(!resourceIds.contains(matchedResourceId)) {
					finalResourceIds.add(matchedResourceId);
				}
			}
		}
		if(finalResourceIds == null || finalResourceIds.isEmpty())
		{
			throw new IllegalArgumentException("Not a valid Inclusion/Exclusion of Resources for the given rule.");
		}
		
		if(isScaledFlow)
		{
			int minutesInterval = 24*60; 								//As of now, splitting up the rule_resource job each day
			List<DateRange> intervals = DateTimeUtil.getTimeIntervals(range.getStartTime(), range.getEndTime(), minutesInterval);
			DateRange firstInterval = intervals.get(0);
			DateRange lastInterval = intervals.get(intervals.size()-1);
			
			long activeCurrentRuleResourceLogs = WorkflowRuleResourceLoggerAPI.getActiveWorkflowRuleResourceLogsByRuleAndResourceId(rule.getId(), finalResourceIds);
			if(activeCurrentRuleResourceLogs > 0)
			{
				throw new Exception("Rule evaluation is already on progress for the selected asset(s).");
			}
			
			long requestedDailyJobsCount = intervals.size() * finalResourceIds.size();
			long activeDailyEventRuleJobsAtPresent = WorkflowRuleHistoricalLogsAPI.getActiveDailyWorkflowRuleHistoricalLogsCount();	
			
			long noOfJobsCanbeCreatedAtPresent = maximumDailyEventRuleJobsPerOrg - activeDailyEventRuleJobsAtPresent;
			if(noOfJobsCanbeCreatedAtPresent < 0) {
				LOGGER.log(Level.ERROR, "Already present active historical event jobs are more than expected limit being "+(-1*noOfJobsCanbeCreatedAtPresent));
				noOfJobsCanbeCreatedAtPresent = maximumDailyEventRuleJobsPerOrg;
			}
			
			if(requestedDailyJobsCount > noOfJobsCanbeCreatedAtPresent) {
				long assetCountLimitAtPresent = (noOfJobsCanbeCreatedAtPresent/intervals.size()) - 1;
				long intervalLimitAtPresent = (noOfJobsCanbeCreatedAtPresent/finalResourceIds.size()) - 2;
				if(assetCountLimitAtPresent <= 0) {
					throw new Exception("Please reduce the specified time range to stay within a period of " +intervalLimitAtPresent+ " days.");
				}
				throw new Exception("Please reduce the number of selected assets to " +assetCountLimitAtPresent+ " or the time range within " +intervalLimitAtPresent+ " days.");
			}
					
			WorkflowRuleLoggerContext workflowRuleLoggerContext = WorkflowRuleLoggerAPI.setWorkflowRuleLoggerContext(rule.getId(), finalResourceIds.size(), range);
			WorkflowRuleLoggerAPI.addWorkflowRuleLogger(workflowRuleLoggerContext);
			long parentRuleLoggerId = workflowRuleLoggerContext.getId();
			List<Long> workflowRuleResourceParentLoggerIds = new ArrayList<Long>();
			
			for(Long finalResourceId:finalResourceIds)
			{	
				WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext = WorkflowRuleResourceLoggerAPI.setWorkflowRuleResourceLoggerContext(parentRuleLoggerId, finalResourceId, null);
				WorkflowRuleResourceLoggerAPI.addWorkflowRuleResourceLogger(workflowRuleResourceLoggerContext);
				long parentRuleResourceId = workflowRuleResourceLoggerContext.getId();
				workflowRuleResourceParentLoggerIds.add(parentRuleResourceId);
				
				if(intervals.size() == 1 && (firstInterval.getStartTime() == lastInterval.getStartTime() && firstInterval.getEndTime() == lastInterval.getEndTime())){
					WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.setWorkflowRuleHistoricalLogsContext(parentRuleResourceId, firstInterval, WorkflowRuleHistoricalLogsContext.LogState.FIRST_AS_WELL_AS_LAST.getIntVal());
					WorkflowRuleHistoricalLogsAPI.addWorkflowRuleHistoricalLogsContext(workflowRuleHistoricalLogsContext);	
				}
				else 
				{
					for(DateRange interval:intervals)
					{			
						WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext = new WorkflowRuleHistoricalLogsContext();
						
						if(interval.getStartTime() == firstInterval.getStartTime() && interval.getEndTime() == firstInterval.getEndTime()) {
							workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.setWorkflowRuleHistoricalLogsContext(parentRuleResourceId, interval, WorkflowRuleHistoricalLogsContext.LogState.IS_FIRST_JOB.getIntVal());	
						}
						else if(interval.getStartTime() == lastInterval.getStartTime() && interval.getEndTime() == lastInterval.getEndTime()) {
							workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.setWorkflowRuleHistoricalLogsContext(parentRuleResourceId, interval, WorkflowRuleHistoricalLogsContext.LogState.IS_LAST_JOB.getIntVal());	
						}
						else {
							workflowRuleHistoricalLogsContext = WorkflowRuleHistoricalLogsAPI.setWorkflowRuleHistoricalLogsContext(parentRuleResourceId, interval, null);	
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
		}
		else
		{
			List<WorkflowRuleHistoricalLoggerContext> currentRuleLoggerList = WorkflowRuleHistoricalLoggerUtil.getActiveRuleHistoricalLogger(rule.getId(), finalResourceIds);
			if(currentRuleLoggerList != null && !currentRuleLoggerList.isEmpty())
			{
				throw new Exception("Historical already In-Progress for the Current Rule Logger with ruleId "+ rule.getId());
			}
			
			long loggerGroupId = -1l;
			boolean isFirst = true;
			Map<Long,WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerMap = new HashMap<Long,WorkflowRuleHistoricalLoggerContext>();
			
			for(Long finalResourceId:finalResourceIds)
			{
				if(isFirst) {
					WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = WorkflowRuleHistoricalLoggerUtil.setWorkflowRuleHistoricalLoggerContext(rule.getId(), range, finalResourceId, -1);	
					WorkflowRuleHistoricalLoggerUtil.addWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);
					
					loggerGroupId = workflowRuleHistoricalLoggerContext.getId();
					workflowRuleHistoricalLoggerContext.setLoggerGroupId(loggerGroupId);
					WorkflowRuleHistoricalLoggerUtil.updateWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);
					workflowRuleHistoricalLoggerMap.put(workflowRuleHistoricalLoggerContext.getId(), workflowRuleHistoricalLoggerContext);
					isFirst = false;
				}
				else {
					WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger = WorkflowRuleHistoricalLoggerUtil.setWorkflowRuleHistoricalLoggerContext(rule.getId(), range, finalResourceId, loggerGroupId);	
					WorkflowRuleHistoricalLoggerUtil.addWorkflowRuleHistoricalLogger(workflowRuleHistoricalLogger);
					workflowRuleHistoricalLoggerMap.put(workflowRuleHistoricalLogger.getId(), workflowRuleHistoricalLogger);
				}
			}	
			
			if (MapUtils.isNotEmpty(workflowRuleHistoricalLoggerMap)) {
				
				for(Long loggerId:workflowRuleHistoricalLoggerMap.keySet())
				{
					FacilioTimer.scheduleOneTimeJobWithDelay(loggerId, "HistoricalRunForReadingRule", 30, "history");				
				}
			}
		}
		
		return false;
	}
	
	private static List<Long> getMatchedResourcesIds(WorkflowRuleContext rule) {

		List<Long> matchedResourceIds = new ArrayList<>();
		ReadingRuleContext readingRuleContext = (ReadingRuleContext)rule;
		if(readingRuleContext.getMatchedResources() != null) {
			matchedResourceIds = new ArrayList<>(readingRuleContext.getMatchedResources().keySet());
		}
		return matchedResourceIds;
	}
}
