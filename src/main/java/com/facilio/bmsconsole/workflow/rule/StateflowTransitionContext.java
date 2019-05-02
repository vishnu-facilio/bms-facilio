package com.facilio.bmsconsole.workflow.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext.ApprovalOrder;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class StateflowTransitionContext extends WorkflowRuleContext {
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
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
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
	
	private Boolean allApprovalRequired = false;
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
	
	private TransitionType type;
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public TransitionType getTypeEnum() {
		return type;
	}
	public void setType(TransitionType type) {
		this.type = type;
	}
	public void setType(int type) {
		this.type = TransitionType.valueOf(type);
	}

	private int scheduleTime = -1;
	public int getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(int scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders,
			FacilioContext context) throws Exception {
		boolean result = true;
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		
		Boolean shouldCheckOnlyConditioned = (Boolean) context.get(FacilioConstants.ContextNames.STATE_TRANSITION_ONLY_CONDITIONED_CHECK);
		if (shouldCheckOnlyConditioned == null) {
			shouldCheckOnlyConditioned = false;
		}
		
		if (shouldCheckOnlyConditioned && type != TransitionType.CONDITIONED) {
			return false;
		}
		
		// this is old records
		if (moduleRecord.getModuleState() == null || moduleRecord.getStateFlowId() <= 0) {
			return false;
		}

		if (moduleRecord.getModuleState() != null && moduleRecord.getStateFlowId() > 0 && moduleRecord.getStateFlowId() == getStateFlowId() && 
				getFromStateId() != moduleRecord.getModuleState().getId()) {
			return false;
		}
		
		if (CollectionUtils.isNotEmpty(approvers)) {
			List<SingleSharingContext> matching = approvers.getMatching(record);
		
			List<SingleSharingContext> checkAnyPendingApprovers = checkAnyPendingApprovers(moduleRecord, matching);
			List<SingleSharingContext> matchingAgain = new SharingContext<>(checkAnyPendingApprovers).getMatching(record);
			result = CollectionUtils.isNotEmpty(matchingAgain);
		}
		return result;
	}
	
	private List<SingleSharingContext> checkAnyPendingApprovers(ModuleBaseWithCustomFields moduleRecord, List<SingleSharingContext> matching) throws Exception {
		if (isAllApprovalRequired()) {
			List<Long> previousApprovers = fetchPreviousApprovers(moduleRecord.getId(), getId());
			Map<Long, SingleSharingContext> approverMap = approvers.stream().collect(Collectors.toMap(SingleSharingContext::getId, Function.identity()));
			if (previousApprovers != null && !previousApprovers.isEmpty()) {
				for (Long id : previousApprovers) {
					approverMap.remove(id);
				}
			}
			return new ArrayList<>(approverMap.values());
		}
		else {
			return matching;	// there is not pending approvers
		}
	}
	
	@Override
	public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		boolean shouldExecuteTrueActions = true;
		if (CollectionUtils.isNotEmpty(approvers)) {
			List<SingleSharingContext> matching = approvers.getMatching(record);
			ApprovalRuleContext.addApprovalStep(moduleRecord.getId(), null, matching, this);
			
			List<SingleSharingContext> checkAnyPendingApprovers = checkAnyPendingApprovers(moduleRecord, matching);
			if (isAllApprovalRequired()) {
				shouldExecuteTrueActions = CollectionUtils.isEmpty(checkAnyPendingApprovers);
			}
		}
		
		if (shouldExecuteTrueActions) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(getModuleId());
			StateFlowRulesAPI.updateState(moduleRecord, module, StateFlowRulesAPI.getStateContext(getToStateId()), false, context);
			
//			StateFlowRulesAPI.addScheduledJobIfAny(getToStateId(), module.getName(), moduleRecord, (FacilioContext) context);
			
			super.executeTrueActions(record, context, placeHolders);
		}
	}
	
	private static List<Long> fetchPreviousApprovers(long recordId, long ruleId) throws Exception {
		FacilioModule module = ModuleFactory.getApprovalStepsModule();
		List<FacilioField> fields = FieldFactory.getApprovalStepsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField recordIdField = fieldMap.get("recordId");
		FacilioField ruleIdField = fieldMap.get("ruleId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(ruleId), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(recordIdField, String.valueOf(recordId), PickListOperators.IS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props.stream().map(p -> (Long) p.get("approverGroup")).collect(Collectors.toList());
		}
		return null;
	}
	
	public enum TransitionType {
		NORMAL,
		SCHEDULED,
		CONDITIONED,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static TransitionType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
