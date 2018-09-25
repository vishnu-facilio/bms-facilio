package com.facilio.bmsconsole.workflow.rule;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.util.Element;

public class ApprovalRuleContext extends WorkflowRuleContext {
	
	private long approvalRuleId = -1;
	public long getApprovalRuleId() {
		return approvalRuleId;
	}
	public void setApprovalRuleId(long approvalRuleId) {
		this.approvalRuleId = approvalRuleId;
	}
	
	private WorkflowRuleContext approvalRule;
	public WorkflowRuleContext getApprovalRule() {
		return approvalRule;
	}
	public void setApprovalRule(WorkflowRuleContext approvalRule) {
		this.approvalRule = approvalRule;
	}
	
	private long rejectionRuleId = -1;
	public long getRejectionRuleId() {
		return rejectionRuleId;
	}
	public void setRejectionRuleId(long rejectionRuleId) {
		this.rejectionRuleId = rejectionRuleId;
	}
	
	private WorkflowRuleContext rejectionRule;
	public WorkflowRuleContext getRejectionRule() {
		return rejectionRule;
	}
	public void setRejectionRule(WorkflowRuleContext rejectionRule) {
		this.rejectionRule = rejectionRule;
	}
	
	private SharingContext approvers;
	public List<SingleSharingContext> getApprovers() {
		return approvers;
	}
	public void setApprovers(List<SingleSharingContext> approvers) {
		this.approvers = new SharingContext(approvers);
	}
	public void addApprover (SingleSharingContext approver) {
		if (this.approvers == null) {
			this.approvers = new SharingContext(approvers);
		}
		approvers.add(approver);
	}
	
	public boolean hasApprovalPermission() throws Exception {
		if (approvers == null) {
			return true;
		}
		else {
			return approvers.isAllowed();
		}
	}
	
	private List<ActionContext> approvalActions;
	public List<ActionContext> getApprovalActions() {
		return approvalActions;
	}
	public void setApprovalActions(List<ActionContext> approvalActions) {
		this.approvalActions = approvalActions;
	}
	
	private long approvalFormId = -1;
	public long getApprovalFormId() {
		return approvalFormId;
	}
	public void setApprovalFormId(long approvalFormId) {
		this.approvalFormId = approvalFormId;
	}

	private List<ActionContext> rejectionActions;
	public List<ActionContext> getRejectionActions() {
		return rejectionActions;
	}
	public void setRejectionActions(List<ActionContext> rejectionActions) {
		this.rejectionActions = rejectionActions;
	}
	
	private long rejectionFormId = -1;
	public long getRejectionFormId() {
		return rejectionFormId;
	}
	public void setRejectionFormId(long rejectionFormId) {
		this.rejectionFormId = rejectionFormId;
	}
	
	@Override
	public void executeWorkflowActions(Object record, Context context, Map<String, Object> placeHolders)
			throws Exception {
		// TODO Auto-generated method stub
		super.executeWorkflowActions(record, context, placeHolders);
	}
}
