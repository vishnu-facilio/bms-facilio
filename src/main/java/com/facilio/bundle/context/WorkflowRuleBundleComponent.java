package com.facilio.bundle.context;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;

public class WorkflowRuleBundleComponent extends WorkflowRuleBaseBundleComponent {

	@Override
	public RuleType getWorkflowRuleType() {
		// TODO Auto-generated method stub
		return RuleType.MODULE_RULE;
	}

	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fillBundleXML(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getInstallMode(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getBundleXMLComponentFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
