package com.facilio.bmsconsole.commands;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.time.DateRange;

import com.facilio.time.DateTimeUtil;

import java.util.List;

public class RunThroughReadingRulesCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(RunThroughReadingRulesCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		if (id == -1 || range == null || range.getStartTime() == -1 || range.getEndTime() == -1) {
			throw new IllegalArgumentException("In sufficient params for running Alarm Rules for historical data");
		}
		
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(id);
		if (rule == null || !(rule instanceof ReadingRuleContext)) {
			throw new IllegalArgumentException("Invalid Alarm rule id for running through historical data");
		}
		
		BmsJobUtil.deleteJobWithProps(rule.getId(), "HistoricalRunForReadingRule");
		BmsJobUtil.scheduleOneTimeJobWithProps(rule.getId(), "HistoricalRunForReadingRule", 30, "priority", FieldUtil.getAsJSON(range));
		
		AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(id),null);
		
		ReadingRuleContext readingRuleContext =  alarmRule.getPreRequsite();
		
		List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(readingRuleContext.getAssetCategoryId());
		WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = getworkflowRuleHistoricalLoggerContext(
				rule.getId(), range, assets);	
		WorkflowRuleHistoricalLoggerUtil.addWorkflowRuleHistoricalLogger(workflowRuleHistoricalLoggerContext);
		
		return false;
	}
	
	
	private static WorkflowRuleHistoricalLoggerContext getworkflowRuleHistoricalLoggerContext(
			long ruleId, DateRange range,List<AssetContext> assets)
	{		
		WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = new WorkflowRuleHistoricalLoggerContext();
		workflowRuleHistoricalLoggerContext.setRuleId(ruleId);
		workflowRuleHistoricalLoggerContext.setType(WorkflowRuleHistoricalLoggerContext.Type.READING_RULE.getIntVal());
		for(AssetContext asset:assets)
		{
			workflowRuleHistoricalLoggerContext.setResourceId(asset.getId());
		}
		workflowRuleHistoricalLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.IN_PROGRESS.getIntVal());
		workflowRuleHistoricalLoggerContext.setStartTime(range.getStartTime());
		workflowRuleHistoricalLoggerContext.setEndTime(range.getEndTime());
		workflowRuleHistoricalLoggerContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
		workflowRuleHistoricalLoggerContext.setCreatedTime(DateTimeUtil.getCurrenTime());
		workflowRuleHistoricalLoggerContext.setCalculationStartTime(DateTimeUtil.getCurrenTime());
		return workflowRuleHistoricalLoggerContext;
		
	}
}
