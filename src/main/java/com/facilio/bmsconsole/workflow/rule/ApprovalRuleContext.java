package com.facilio.bmsconsole.workflow.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.fw.BeanFactory;

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
		if (approvers != null) {
			this.approvers = new SharingContext(approvers);
		}
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
	
	private String approvalButton;
	public String getApprovalButton() {
		return approvalButton;
	}
	public void setApprovalButton(String approvalButton) {
		this.approvalButton = approvalButton;
	}
	
	private String rejectionButton;
	public String getRejectionButton() {
		return rejectionButton;
	}
	public void setRejectionButton(String rejectionButton) {
		this.rejectionButton = rejectionButton;
	}
	
	private Boolean allApprovalRequired;
	public Boolean getAllApprovalRequired() {
		return allApprovalRequired;
	}
	public void setAllApprovalRequired(Boolean allApprovalRequired) {
		this.allApprovalRequired = allApprovalRequired;
	}
	public boolean isAllApprovalRequired() {
		if (allApprovalRequired != null) {
			return allApprovalRequired.booleanValue();
		}
		return false;
	}
	
	private ApprovalOrder executeOrder;
	public ApprovalOrder getExecuteOrder() {
		return executeOrder;
	}
	public void setExecuteOrder(ApprovalOrder executeOrder) {
		this.executeOrder = executeOrder;
	}
	public int getExecuteOrderENUM() {
		if(executeOrder != null) {
			return executeOrder.getValue();
		}
		return -1;
	}
	public void setInternalState(int executeOrder) {
		this.executeOrder = APPROVAL_ORDER[executeOrder-1];
	}
	
	@Override
	public void executeWorkflowActions(Object record, Context context, Map<String, Object> placeHolders)
			throws Exception {
		// TODO Auto-generated method stub
		updateRecordApprovalState(record);
		super.executeWorkflowActions(record, context, placeHolders);
	}
	
	private void updateRecordApprovalState(Object record) throws Exception {
		if (getEvent() == null) {
			setEvent(WorkflowRuleAPI.getWorkflowEvent(getEventId()));
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		WorkflowEventContext event = getEvent();
		Map<String, Object> prop = new HashMap<>();
		prop.put(FacilioConstants.ApprovalRule.APPROVAL_STATE_FIELD_NAME, ApprovalState.REQUESTED.getValue());
		prop.put(FacilioConstants.ApprovalRule.APPROVAL_RULE_ID_FIELD_NAME, getId());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(modBean.getField(FacilioConstants.ApprovalRule.APPROVAL_STATE_FIELD_NAME, event.getModule().getName()));
		fields.add(modBean.getField(FacilioConstants.ApprovalRule.APPROVAL_RULE_ID_FIELD_NAME, event.getModule().getName()));
		
		UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
																			.fields(fields)
																			.module(event.getModule())
																			.andCondition(CriteriaAPI.getIdCondition(((ModuleBaseWithCustomFields) record).getId(), event.getModule()))
																			;
		updateBuilder.update(prop);
	}
	private static final ApprovalOrder[] APPROVAL_ORDER = ApprovalOrder.values();

	public static enum ApprovalOrder {
		SEQUENTIAL,
		PARALLEL
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public ApprovalOrder valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
