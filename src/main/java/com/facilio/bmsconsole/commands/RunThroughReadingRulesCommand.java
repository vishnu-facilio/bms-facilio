package com.facilio.bmsconsole.commands;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
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
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		List<Long> resourceIds = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
		Boolean isInclude = (Boolean) context.get(FacilioConstants.ContextNames.IS_INCLUDE);
		
		if(isInclude == null)
		{
			isInclude = true;
		}
		
		if (id == -1 || range == null || range.getStartTime() == -1 || range.getEndTime() == -1) {
			throw new IllegalArgumentException("In sufficient params for running Alarm Rules for historical data");
		}
		
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(id);
		if (rule == null || !(rule instanceof ReadingRuleContext)) {
			throw new IllegalArgumentException("Invalid Alarm rule id for running through historical data");
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
		else
		{
			throw new Exception("Not a valid Inclusion/Exclusion of Resources");
		}
		
		List<WorkflowRuleHistoricalLoggerContext> currentRuleLoggerList = WorkflowRuleHistoricalLoggerUtil.getActiveRuleHistoricalLogger(rule.getId(), finalResourceIds);
		if(currentRuleLoggerList != null && !currentRuleLoggerList.isEmpty())
		{
			throw new Exception("Historical already In-Progress for the Current Rule Logger with ruleId "+ rule.getId());
		}
		
		int minutesInterval = 24*60; 								//As of now, splitting up the rule_resource job each day
		List<DateRange> intervals = DateTimeUtil.getTimeIntervals(range.getStartTime(), range.getEndTime(), minutesInterval);
		
		long loggerGroupId = -1l;
		boolean isFirst = true;
		Map<Long,WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerMap = new HashMap<Long,WorkflowRuleHistoricalLoggerContext>();
		
		for(Long finalResourceId:finalResourceIds)
		{
			if(isFirst) 
			{
				WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = setWorkflowRuleHistoricalLoggerContext(rule.getId(), range, finalResourceId, -99, -1);
				WorkflowRuleHistoricalLoggerUtil.addWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);	
				
				loggerGroupId = workflowRuleHistoricalLoggerContext.getId();
				workflowRuleHistoricalLoggerContext.setLoggerGroupId(loggerGroupId);	
				WorkflowRuleHistoricalLoggerUtil.updateWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);
				workflowRuleHistoricalLoggerMap.put(workflowRuleHistoricalLoggerContext.getId(), workflowRuleHistoricalLoggerContext);
				
				for(DateRange interval:intervals)
				{		
					workflowRuleHistoricalLoggerContext.setRuleResourceLoggerId(workflowRuleHistoricalLoggerContext.getId());
					WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger = setWorkflowRuleHistoricalLoggerContext(rule.getId(), interval, finalResourceId, loggerGroupId, loggerGroupId);	
					WorkflowRuleHistoricalLoggerUtil.addWorkflowRuleHistoricalLogger(workflowRuleHistoricalLogger);								
				}
				isFirst = false;
			}
			else 
			{
				WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = setWorkflowRuleHistoricalLoggerContext(rule.getId(), range, finalResourceId, -99, loggerGroupId);	
				WorkflowRuleHistoricalLoggerUtil.addWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);
				workflowRuleHistoricalLoggerMap.put(workflowRuleHistoricalLoggerContext.getId(), workflowRuleHistoricalLoggerContext);
				
				long ruleResourceLoggerId = workflowRuleHistoricalLoggerContext.getId();
				for(DateRange interval:intervals)
				{
					workflowRuleHistoricalLoggerContext.setRuleResourceLoggerId(workflowRuleHistoricalLoggerContext.getId());
					WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger = setWorkflowRuleHistoricalLoggerContext(rule.getId(), interval, finalResourceId, ruleResourceLoggerId, loggerGroupId);	
					WorkflowRuleHistoricalLoggerUtil.addWorkflowRuleHistoricalLogger(workflowRuleHistoricalLogger);					
				}
			}
		}	
		
		if (MapUtils.isNotEmpty(workflowRuleHistoricalLoggerMap)) {
			
			for(Long parentRuleResourceLoggerId :workflowRuleHistoricalLoggerMap.keySet())
			{		
				FacilioTimer.scheduleOneTimeJobWithDelay(parentRuleResourceLoggerId, "HistoricalAlarmOccurrenceDeletion", 30, "history");		
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
	
	
	private static WorkflowRuleHistoricalLoggerContext setWorkflowRuleHistoricalLoggerContext(long ruleId, DateRange range,Long resourceId, long ruleResourceId, long loggerGroupId)
	{
		WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = new WorkflowRuleHistoricalLoggerContext();
		workflowRuleHistoricalLoggerContext.setRuleId(ruleId);
		workflowRuleHistoricalLoggerContext.setType(WorkflowRuleHistoricalLoggerContext.Type.READING_RULE.getIntVal());
		workflowRuleHistoricalLoggerContext.setResourceId(resourceId);
		workflowRuleHistoricalLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.IN_PROGRESS.getIntVal());
		workflowRuleHistoricalLoggerContext.setRuleResourceLoggerId(ruleResourceId);
		workflowRuleHistoricalLoggerContext.setLoggerGroupId(loggerGroupId);
		workflowRuleHistoricalLoggerContext.setStartTime(range.getStartTime());
		workflowRuleHistoricalLoggerContext.setEndTime(range.getEndTime());
		workflowRuleHistoricalLoggerContext.setCalculationStartTime(DateTimeUtil.getCurrenTime());
		workflowRuleHistoricalLoggerContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
		workflowRuleHistoricalLoggerContext.setCreatedTime(DateTimeUtil.getCurrenTime());
		return workflowRuleHistoricalLoggerContext;	
	}
}
