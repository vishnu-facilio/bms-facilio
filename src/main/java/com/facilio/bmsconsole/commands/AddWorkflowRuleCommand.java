package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddWorkflowRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		rule.setStatus(true);
		rule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		long ruleId = WorkflowRuleAPI.addWorkflowRule(rule);
		
		return false;
	}
}
