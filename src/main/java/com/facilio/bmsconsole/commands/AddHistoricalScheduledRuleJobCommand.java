package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.time.DateRange;

public class AddHistoricalScheduledRuleJobCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long id = (long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		if (id == -1 || range == null || range.getStartTime() == -1 || range.getEndTime() == -1) {
			throw new IllegalArgumentException("In sufficient params for running Scheduled Rules for historical data");
		}
		
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(id);
		if (rule == null || rule.getActivityTypeEnum() !=  EventType.SCHEDULED_READING_RULE) {
			throw new IllegalArgumentException("Invalid Scheduled rule id for running through historical data");
		}
		
		BmsJobUtil.deleteJobWithProps(rule.getId(), "HistoricalScheduledRule");
		BmsJobUtil.scheduleOneTimeJobWithProps(rule.getId(), "HistoricalScheduledRule", 30, "historicalRule", FieldUtil.getAsJSON(range));
		return false;
	}

}
