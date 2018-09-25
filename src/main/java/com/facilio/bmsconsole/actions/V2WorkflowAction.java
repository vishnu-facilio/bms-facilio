package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;

public class V2WorkflowAction extends FacilioAction {
	private ApprovalRuleContext approvalRule;
	public ApprovalRuleContext getApprovalRule() {
		return approvalRule;
	}
	public void setApprovalRule(ApprovalRuleContext approvalRule) {
		this.approvalRule = approvalRule;
	}
	
	public String addApprovalRule() throws Exception {
		
		FacilioContext facilioContext = new FacilioContext();
		approvalRule.setRuleType(RuleType.APPROVAL_RULE);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, approvalRule);
		Chain addRule = TransactionChainFactory.addApprovalRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", approvalRule);
		
		return SUCCESS;
	}
	
	public String updateApprovalRule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		approvalRule.setRuleType(RuleType.APPROVAL_RULE);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, approvalRule);
		Chain addRule = TransactionChainFactory.updateApprovalRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", approvalRule);
		
		return SUCCESS;
	}
}
