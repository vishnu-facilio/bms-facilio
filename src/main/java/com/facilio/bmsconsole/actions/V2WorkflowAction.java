package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ScheduledRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
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
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, scheduledRule.getActions());
		Chain addRule = TransactionChainFactory.addWorkflowRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", scheduledRule);
		
		return SUCCESS;
	}
	
	public String updateScheduledRule() throws Exception {
		FacilioContext facilioContext = new FacilioContext();
		scheduledRule.setRuleType(RuleType.SCHEDULED_RULE);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, scheduledRule);
		facilioContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, scheduledRule.getActions());
		Chain addRule = TransactionChainFactory.updateWorkflowRuleChain();
		addRule.execute(facilioContext);
		setResult("rule", scheduledRule);
		
		return SUCCESS;
	}
	
}
