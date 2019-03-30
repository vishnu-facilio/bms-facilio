package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

public class V2WorkflowAction extends FacilioAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	private long ruleId = -1;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	
	public String fetchApprovalRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, ruleId);
		
		Chain fetchWorkflowChain = ReadOnlyChainFactory.fetchApprovalRuleWithActionsChain();
		fetchWorkflowChain.execute(context);
		
		approvalRule = (ApprovalRuleContext) context.get(FacilioConstants.ContextNames.APPROVAL_RULE);
		setResult("rule", approvalRule);
		return SUCCESS;
	}
}
