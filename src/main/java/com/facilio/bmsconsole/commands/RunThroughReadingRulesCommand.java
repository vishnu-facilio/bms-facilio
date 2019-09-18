package com.facilio.bmsconsole.commands;
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
import com.facilio.time.DateRange;

import com.facilio.time.DateTimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		long loggerGroupId = -1l;
		boolean isFirst = true;
		Map<Long,WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerMap = new HashMap<Long,WorkflowRuleHistoricalLoggerContext>();
		
		for(Long finalResourceId:finalResourceIds)
		{
			if(isFirst) {
				WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = getworkflowRuleHistoricalLoggerContext(rule.getId(), range, finalResourceId, -1);	
				WorkflowRuleHistoricalLoggerUtil.addWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);
				
				loggerGroupId = workflowRuleHistoricalLoggerContext.getId();
				workflowRuleHistoricalLoggerContext.setLoggerGroupId(loggerGroupId);
				WorkflowRuleHistoricalLoggerUtil.updateWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);
				workflowRuleHistoricalLoggerMap.put(workflowRuleHistoricalLoggerContext.getId(), workflowRuleHistoricalLoggerContext);
				isFirst = false;
			}
			else {
				WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger = getworkflowRuleHistoricalLoggerContext(rule.getId(), range, finalResourceId, loggerGroupId);	
				WorkflowRuleHistoricalLoggerUtil.addWorkflowRuleHistoricalLogger(workflowRuleHistoricalLogger);
				workflowRuleHistoricalLoggerMap.put(workflowRuleHistoricalLogger.getId(), workflowRuleHistoricalLogger);
			}
		}	
		
		if (MapUtils.isNotEmpty(workflowRuleHistoricalLoggerMap)) {
			
			for(Long loggerId:workflowRuleHistoricalLoggerMap.keySet())
			{
				Long resourceId = workflowRuleHistoricalLoggerMap.get(loggerId).getResourceId();
				if(resourceId != null) {
					
					JSONObject jobprop = new JSONObject();
					jobprop.put("startTime", range.getStartTime());
					jobprop.put("endTime", range.getEndTime());
					jobprop.put("ruleId", rule.getId());
					jobprop.put("resourceId", resourceId);

					BmsJobUtil.deleteJobWithProps(loggerId, "HistoricalRunForReadingRule");
					BmsJobUtil.scheduleOneTimeJobWithProps(loggerId, "HistoricalRunForReadingRule", 30, "priority", jobprop);
					
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
	
	
	private static WorkflowRuleHistoricalLoggerContext getworkflowRuleHistoricalLoggerContext(long ruleId, DateRange range,Long resourceId, long loggerGroupId)
	{
		WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = new WorkflowRuleHistoricalLoggerContext();
		workflowRuleHistoricalLoggerContext.setRuleId(ruleId);
		workflowRuleHistoricalLoggerContext.setType(WorkflowRuleHistoricalLoggerContext.Type.READING_RULE.getIntVal());
		workflowRuleHistoricalLoggerContext.setResourceId(resourceId);
		workflowRuleHistoricalLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.IN_PROGRESS.getIntVal());
		workflowRuleHistoricalLoggerContext.setLoggerGroupId(loggerGroupId);
		workflowRuleHistoricalLoggerContext.setStartTime(range.getStartTime());
		workflowRuleHistoricalLoggerContext.setEndTime(range.getEndTime());
		workflowRuleHistoricalLoggerContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
		workflowRuleHistoricalLoggerContext.setCreatedTime(DateTimeUtil.getCurrenTime());
		workflowRuleHistoricalLoggerContext.setCalculationStartTime(DateTimeUtil.getCurrenTime());	
		return workflowRuleHistoricalLoggerContext;	
	}
}
