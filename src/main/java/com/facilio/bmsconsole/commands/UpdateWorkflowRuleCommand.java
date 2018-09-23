package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.view.SLARuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class UpdateWorkflowRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		rule.setRuleType(null); //Type is not allowed to be changed
		if (rule instanceof ReadingRuleContext) {
			rule = WorkflowRuleAPI.updateReadingRuleWithChildren((ReadingRuleContext) rule);
		}
		if (rule instanceof SLARuleContext) {
			rule = WorkflowRuleAPI.updateSLARuleWithChildren((SLARuleContext) rule);
		}
		else {
			rule = WorkflowRuleAPI.updateWorkflowRuleWithChildren(rule);
		}
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}

}
