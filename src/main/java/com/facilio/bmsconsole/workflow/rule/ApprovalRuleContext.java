package com.facilio.bmsconsole.workflow.rule;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApprovalRuleContext extends WorkflowRuleContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	private FacilioForm approvalForm;
	
	public FacilioForm getApprovalForm() {
		return approvalForm;
	}
	public void setApprovalForm(FacilioForm approvalForm) {
		this.approvalForm = approvalForm;
	}
	private FacilioForm rejectionForm;
	public FacilioForm getRejectionForm() {
		return rejectionForm;
	}
	public void setRejectionForm(FacilioForm rejectionForm) {
		this.rejectionForm = rejectionForm;
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
	
	
	@Override
	public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders)
			throws Exception {
		// TODO Auto-generated method stub
		updateRecordApprovalState(record, (User) context.get(FacilioConstants.ApprovalRule.APPROVAL_REQUESTER));
		deletePreviousApprovalSteps(((ModuleBaseWithCustomFields) record).getId());
		super.executeTrueActions(record, context, placeHolders);
	}
	
	private void updateRecordApprovalState(Object record, User approvalRequester) throws Exception {
		if (getEvent() == null) {
			setEvent(WorkflowRuleAPI.getWorkflowEvent(getEventId()));
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		WorkflowEventContext event = getEvent();
		Map<String, Object> prop = new HashMap<>();
		prop.put(FacilioConstants.ApprovalRule.APPROVAL_STATE_FIELD_NAME, ApprovalState.REQUESTED.getValue());
		prop.put(FacilioConstants.ApprovalRule.APPROVAL_RULE_ID_FIELD_NAME, getId());
		
		if (getRuleTypeEnum() == RuleType.APPROVAL_RULE) {
			if (approvalRequester == null) {
				prop.put(FacilioConstants.ApprovalRule.APPROVAL_REQUESTED_BY_FIELD_NAME, FieldUtil.getAsProperties(AccountUtil.getCurrentUser()));
			}
			else {
				prop.put(FacilioConstants.ApprovalRule.APPROVAL_REQUESTED_BY_FIELD_NAME, FieldUtil.getAsProperties(approvalRequester));
			}
		}
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(modBean.getField(FacilioConstants.ApprovalRule.APPROVAL_STATE_FIELD_NAME, event.getModule().getName()));
		fields.add(modBean.getField(FacilioConstants.ApprovalRule.APPROVAL_RULE_ID_FIELD_NAME, event.getModule().getName()));
		fields.add(modBean.getField(FacilioConstants.ApprovalRule.APPROVAL_REQUESTED_BY_FIELD_NAME, event.getModule().getName()));
		
		UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
																			.fields(fields)
																			.module(event.getModule())
																			.andCondition(CriteriaAPI.getIdCondition(((ModuleBaseWithCustomFields) record).getId(), event.getModule()))
																			;
		updateBuilder.updateViaMap(prop);
	}
	
	private int deletePreviousApprovalSteps(long id) throws Exception {
		FacilioModule module = ModuleFactory.getApprovalStepsModule();
		List<FacilioField> fields = FieldFactory.getApprovalStepsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField ruleIdField = fieldMap.get("ruleId");
		FacilioField recordIdField = fieldMap.get("recordId");
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(getId()), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(recordIdField, String.valueOf(id), PickListOperators.IS))
														;
		return deleteBuilder.delete();
	}

	public static enum ApprovalOrder {
		SEQUENTIAL,
		PARALLEL
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static ApprovalOrder valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	public boolean verified(WorkOrderContext wo, ApprovalState action) throws Exception {
		boolean result = true;
		List<SingleSharingContext> matchingApprovers = approvers == null ? null : approvers.getMatching(wo);
		if (action == ApprovalState.APPROVED && isAllApprovalRequired() && approvalOrder == ApprovalOrder.PARALLEL) {
			if (approvers != null && approvers.size() > 1) {
				List<Long> previousApprovers = fetchPreviousApprovers(wo.getId());
				Map<Long, SingleSharingContext> approverMap = approvers.stream().collect(Collectors.toMap(SingleSharingContext::getId, Function.identity()));
				if (previousApprovers != null && !previousApprovers.isEmpty()) {
					for (Long id : previousApprovers) {
						approverMap.remove(id);
					}
				}
				for (SingleSharingContext approver : matchingApprovers) {
					approverMap.remove(approver.getId());
				}
				result = approverMap.isEmpty();
			}
		}
		addApprovalStep(wo.getId(), action, matchingApprovers);
		return result;
	}
	
	private List<Long> fetchPreviousApprovers(long recordId) throws Exception {
		FacilioModule module = ModuleFactory.getApprovalStepsModule();
		List<FacilioField> fields = FieldFactory.getApprovalStepsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField recordIdField = fieldMap.get("recordId");
		FacilioField ruleIdField = fieldMap.get("ruleId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(getId()), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(recordIdField, String.valueOf(recordId), PickListOperators.IS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props.stream().map(p -> (Long) p.get("id")).collect(Collectors.toList());
		}
		return null;
	}
	
	private void addApprovalStep (long recordId, ApprovalState action, List<SingleSharingContext> matchingApprovers) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getApprovalStepsModule().getTableName())
													.fields(FieldFactory.getApprovalStepsFields())
													;
		
		if (matchingApprovers == null || matchingApprovers.isEmpty()) {
			insertBuilder.addRecord(constructStep(recordId, null, action));
		}
		else {
			for (SingleSharingContext approver : matchingApprovers) {
				insertBuilder.addRecord(constructStep(recordId, approver, action));
			}
		}
		insertBuilder.save();
	}
	
	private Map<String, Object> constructStep(long recordId, SingleSharingContext approver, ApprovalState action) {
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", AccountUtil.getCurrentOrg().getId());
		prop.put("siteId", getSiteId());
		prop.put("ruleId", getId());
		prop.put("recordId", recordId);
		prop.put("actionBy", AccountUtil.getCurrentUser().getId());
		prop.put("action", action.getValue());
		prop.put("actionTime", System.currentTimeMillis());
		if (approver != null) {
			prop.put("approverGroup", approver.getId());
		}
		
		return prop;
	}
}
