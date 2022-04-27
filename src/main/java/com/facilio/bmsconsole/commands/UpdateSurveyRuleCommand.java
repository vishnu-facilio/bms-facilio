package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

public class UpdateSurveyRuleCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		WorkflowRuleContext workflowRule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);

		if(workflowRule != null && workflowRule.getId() > 0){
			WorkflowRuleAPI.updateWorkflowRuleWithChildren(workflowRule,WorkflowRuleAPI.getWorkflowRule(workflowRule.getId()));
		}

		return false;
	}
}
