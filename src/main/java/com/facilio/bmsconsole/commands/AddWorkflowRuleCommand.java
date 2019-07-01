package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class AddWorkflowRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		rule.setStatus(true);
		rule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		long ruleId = WorkflowRuleAPI.addWorkflowRule(rule);
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID, ruleId);
		
		return false;
	}
}
