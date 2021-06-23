package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ExecuteScheduledReadingRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long ruleId = (Long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(ruleId,true,true);
		if (rule.isActive()) {
			long executionTime = (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
			WorkflowRuleAPI.executeScheduledRule(rule, executionTime, (FacilioContext) context);
		}
		return false;
	}

}
