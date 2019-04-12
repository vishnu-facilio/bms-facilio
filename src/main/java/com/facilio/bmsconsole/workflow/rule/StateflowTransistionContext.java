package com.facilio.bmsconsole.workflow.rule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext.ApprovalOrder;
import com.facilio.chain.FacilioContext;

public class StateflowTransistionContext extends WorkflowRuleContext {
	private static final long serialVersionUID = 1L;
	
	private long fromStateId = -1;
	public long getFromStateId() {
		return fromStateId;
	}
	public void setFromStateId(long fromStateId) {
		this.fromStateId = fromStateId;
	}
	
	private long toStateId = -1;
	public long getToStateId() {
		return toStateId;
	}
	public void setToStateId(long toStateId) {
		this.toStateId = toStateId;
	}
	
	private long stateFlowId = -1;
	public long getStateFlowId() {
		return stateFlowId;
	}
	public void setStateFlowId(long stateFlowId) {
		this.stateFlowId = stateFlowId;
	}
	
	private long formId = -1; // check whether it is good to have
	public long getFormId() {
		return formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
	
	private SharingContext<ApproverContext> approvers;
	public List<ApproverContext> getApprovers() {
		return approvers;
	}
	public void setApprovers(List<ApproverContext> approvers) {
		if (approvers != null) {
			this.approvers = new SharingContext<>(approvers);
		}
	}
	public void addApprover (ApproverContext approver) {
		if (this.approvers == null) {
			this.approvers = new SharingContext<>(approvers);
		}
		approvers.add(approver);
	}
	
	public boolean hasApprovalPermission(Object object) throws Exception {
		if (approvers == null) {
			return true;
		}
		else {
			return approvers.isAllowed(object);
		}
	}
	
	private Boolean allApprovalRequired;
	public Boolean getAllApprovalRequired() {
		return allApprovalRequired;
	}
	public void setAllApprovalRequired(Boolean allApprovalRequired) {
		this.allApprovalRequired = allApprovalRequired;
	}
	public void setAllApprovalRequired(boolean allApprovalRequired) {
		this.allApprovalRequired = allApprovalRequired;
	}
	public boolean isAllApprovalRequired() {
		if (allApprovalRequired != null) {
			return allApprovalRequired.booleanValue();
		}
		return false;
	}
	
	private ApprovalOrder approvalOrder;
	public ApprovalOrder getApprovalOrderEnum() {
		return approvalOrder;
	}
	public void setApprovalOrder(ApprovalOrder approvalOrder) {
		this.approvalOrder = approvalOrder;
	}
	public int getApprovalOrder() {
		if (approvalOrder != null) {
			return approvalOrder.getValue();
		}
		return -1;
	}
	public void setApprovalOrder(int approvalOrder) {
		this.approvalOrder = ApprovalOrder.valueOf(approvalOrder);
	}
	
	private int buttonType;
	public int getButtonType() {
		return buttonType;
	}
	public void setButtonType(int buttonType) {
		this.buttonType = buttonType;
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders,
			FacilioContext context) throws Exception {
		boolean result = true;
		if (CollectionUtils.isNotEmpty(approvers)) {
			List<SingleSharingContext> matching = approvers.getMatching(record);
			result = CollectionUtils.isNotEmpty(matching);
		}
		return result;
	}
	
	@Override
	public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		if (CollectionUtils.isNotEmpty(approvers)) {
			ApprovalRuleContext.addApprovalStep(moduleRecord.getId(), null, approvers.getMatching(record), this);
		}
		super.executeTrueActions(record, context, placeHolders);
	}
}
