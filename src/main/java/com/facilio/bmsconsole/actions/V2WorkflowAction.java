package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ScheduledRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;

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
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, approvalRule.getActions());
		Chain addRule = TransactionChainFactory.getAddWorkflowRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", approvalRule);
		
		return SUCCESS;
	}
	
	public String updateApprovalRule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		approvalRule.setRuleType(RuleType.APPROVAL_RULE);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, approvalRule);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, approvalRule.getActions());
		Chain addRule = TransactionChainFactory.updateWorkflowRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", approvalRule);
		
		return SUCCESS;
	}
	
	private ScheduledRuleContext scheduledRule;
	public ScheduledRuleContext getScheduledRule() {
		return scheduledRule;
	}
	public void setScheduledRule(ScheduledRuleContext scheduledRule) {
		this.scheduledRule = scheduledRule;
	}
	
	public String addScheduledRule() throws Exception {
		
		FacilioContext facilioContext = new FacilioContext();
		scheduledRule.setRuleType(RuleType.SCHEDULED_RULE);
		if (scheduledRule.getEvent() != null) {
			scheduledRule.getEvent().setActivityType(ActivityType.SCHEDULED);
		}
		
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, scheduledRule);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, scheduledRule.getActions());
		Chain addRule = TransactionChainFactory.getAddWorkflowRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", scheduledRule);
		
		return SUCCESS;
	}
	
	public String updateScheduledRule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		scheduledRule.setRuleType(RuleType.SCHEDULED_RULE);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, scheduledRule);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, scheduledRule.getActions());
		Chain addRule = TransactionChainFactory.updateWorkflowRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", scheduledRule);
		
		return SUCCESS;
	}
	
}
