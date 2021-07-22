package com.facilio.bundle.context;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;

public class NotificationRuleBundleComponent extends WorkflowRuleBaseBundleComponent {

	@Override
	public RuleType getWorkflowRuleType() {
		// TODO Auto-generated method stub
		return RuleType.MODULE_RULE_NOTIFICATION;
	}

}