package com.facilio.bundle.context;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;

public class WorkflowRuleBundleComponent extends WorkflowRuleBaseBundleComponent {

	@Override
	public RuleType getWorkflowRuleType() {
		// TODO Auto-generated method stub
		return RuleType.MODULE_RULE;
	}

	@Override
	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
