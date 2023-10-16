package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ApprovalRulesAPI extends WorkflowRuleAPI {
	protected static void updateChildRuleIds(ApprovalRuleContext rule) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("workOrder");
		if (rule.getApprovalRuleId() == -1) {
			if (rule.getApprovalRule() == null) {
				if (rule.getApprovalActions() != null && !rule.getApprovalActions().isEmpty()) {
					rule.setApprovalRule(getRequestApprovalRejectRule(rule, true, modBean));
					rule.setApprovalRuleId(addWorkflowRule(rule.getApprovalRule(), rule.getApprovalActions()));
				}
			}
			else {
				updateEventAndCriteria(rule.getApprovalRule(), rule, true, modBean);
				rule.setApprovalRuleId(addWorkflowRule(rule.getApprovalRule(), rule.getApprovalRule().getActions()));
			}
		}
		if (rule.getApprovalFormId() == -1) {
			if (rule.getApprovalForm() != null) {
				if (rule.getApprovalForm().getId() == -1) {
					rule.getApprovalForm().setHideInList(true);
					rule.setApprovalFormId(FormsAPI.createForm(rule.getApprovalForm(), module));
				}
				else {
					rule.setApprovalFormId(rule.getApprovalForm().getId());
				}
			}
		}
			
		if (rule.getRejectionRuleId() == -1) {
			if (rule.getRejectionRule() == null) {
				if (rule.getRejectionActions() != null && !rule.getRejectionActions().isEmpty()) {
					rule.setRejectionRule(getRequestApprovalRejectRule(rule, false, modBean));
					rule.setRejectionRuleId(addWorkflowRule(rule.getRejectionRule(), rule.getRejectionActions()));
				}
			}
			else {
				updateEventAndCriteria(rule.getRejectionRule(), rule, false, modBean);
				rule.setRejectionRuleId(addWorkflowRule(rule.getRejectionRule(), rule.getRejectionRule().getActions()));
			}
		}
		if (rule.getRejectionFormId() == -1) {
			if (rule.getRejectionForm() != null) {
				if (rule.getRejectionForm().getId() == -1) {
					rule.getRejectionForm().setHideInList(true);
					rule.setRejectionFormId(FormsAPI.createForm(rule.getRejectionForm(), module));
				}
				else {
					rule.setRejectionFormId(rule.getRejectionForm().getId());
				}
			}
		}
	}

	public static void addApproverRuleChildren(ApproverWorkflowRuleContext rule) throws Exception {
		addApprover(rule.getId(), rule.getApprovers());
		addValidations(rule.getId(), rule.getValidations());

		ConfirmationDialogAPI.addConfirmationDialogs(rule.getConfirmationDialogs(), rule.getId());
	}
	
	protected static void addApprover(long parentId, List<ApproverContext> sharing) throws Exception {
		if (CollectionUtils.isNotEmpty(sharing) && parentId > 0) {
			SharingAPI.addSharing((SharingContext<? extends SingleSharingContext>) sharing, parentId, ModuleFactory.getApproversModule());
		}
	}
	
	public static void addApproverActionsRel (List<ApproverContext> approvers) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getApproverActionsRelModule().getTableName())
														.fields(FieldFactory.getApproverActionsRelFields())
														;
		
		for (ApproverContext approver : approvers) {
			if (approver.getActions() != null && !approver.getActions().isEmpty()) {
				for (ActionContext action : approver.getActions()) {
					Map<String, Object> prop = new HashMap<>();
					prop.put("orgId", AccountUtil.getCurrentOrg().getId());
					prop.put("approverId", approver.getId());
					prop.put("actionId", action.getId());
					
					insertBuilder.addRecord(prop);
				}
			}
		}
		insertBuilder.save();
	}
	
	public static Map<Long, List<Long>> getApproverActionMap(Collection<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getApproverActionsRelModule();
		List<FacilioField> fields = FieldFactory.getApproverActionsRelFields();
		FacilioField approverIdField = FieldFactory.getAsMap(fields).get("approverId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(approverIdField, ids, PickListOperators.IS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<Long, List<Long>> approverActionMap = new HashMap<>();
			for (Map<String, Object> prop : props) {
				Long approverId = (Long) prop.get("approverId");
				Long actionId = (Long) prop.get("actionId");
				
				List<Long> actions = approverActionMap.get(approverId);
				if (actions == null) {
					actions = new ArrayList<>();
					approverActionMap.put(approverId, actions);
				}
				actions.add(actionId);
			}
			return approverActionMap;
		}
		return null;
	}
	
	private static long addWorkflowRule(WorkflowRuleContext rule, List<ActionContext> actions) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, actions);
		
		FacilioChain addRule = TransactionChainFactory.addWorkflowRuleChain();
		addRule.execute(context);
		
		return rule.getId();
	}
	
	private static WorkflowRuleContext getRequestApprovalRejectRule(ApprovalRuleContext approvalRule, boolean isApproval, ModuleBean modBean) throws Exception {
		WorkflowRuleContext rule = new WorkflowRuleContext();
		
		StringBuilder name = new StringBuilder()
									.append(approvalRule.getName())
									.append("_")
									.append(isApproval ? "Approval" : "Rejection")
									.append("_")
									;
		rule.setName(name.toString());
		rule.setRuleType(isApproval ? RuleType.REQUEST_APPROVAL_RULE : RuleType.REQUEST_REJECT_RULE);
		updateEventAndCriteria(rule, approvalRule, isApproval, modBean);
		return rule;
	}
	
	private static void updateEventAndCriteria(WorkflowRuleContext rule, ApprovalRuleContext approvalRule, boolean isApproval, ModuleBean modBean) throws Exception {
		rule.setModuleId(approvalRule.getModuleId());
		rule.setActivityType(EventType.FIELD_CHANGE);

		FacilioModule module = modBean.getModule(rule.getModuleId());
		FieldChangeFieldContext field = new FieldChangeFieldContext(); //Field Change event from REQUESTED to APPROVED/ REJECTED
		field.setField(modBean.getField(FacilioConstants.ApprovalRule.APPROVAL_STATE_FIELD_NAME, module.getName()));
		field.setOldValue(String.valueOf(ApprovalState.REQUESTED.getValue()));
		int newValue = isApproval ? ApprovalState.APPROVED.getValue() : ApprovalState.REJECTED.getValue();
		field.setNewValue(String.valueOf(newValue));
		rule.setFields(Collections.singletonList(field));
		
		Criteria criteria = new Criteria(); //Criteria is ruleId
		criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FacilioConstants.ApprovalRule.APPROVAL_RULE_ID_FIELD_NAME, module.getName()), String.valueOf(approvalRule.getId()), PickListOperators.IS));
		rule.setCriteria(criteria);
	}

	public static WorkflowRuleContext updateApproverRule(ApproverWorkflowRuleContext oldRule, ApproverWorkflowRuleContext rule) throws Exception {
		deleteApprovers(oldRule.getApprovers());
		addApprover(rule.getId(), rule.getApprovers());
		deleteValidations(oldRule.getValidations());
		addValidations(rule.getId(), rule.getValidations());

		ConfirmationDialogAPI.deleteConfirmationDialogs(rule.getId());
		ConfirmationDialogAPI.addConfirmationDialogs(rule.getConfirmationDialogs(), rule.getId());

		return rule;
	}

	public static WorkflowRuleContext updateCustomButtonRuleWithChildren(CustomButtonRuleContext rule, CustomButtonRuleContext oldRule) throws Exception {
		updateWorkflowRuleWithChildren(rule, oldRule);

		updateApproverRule(oldRule, rule);

		if (StringUtils.isEmpty(rule.getFormModuleName())) {
			StateFlowRulesAPI.addOrUpdateFormDetails(rule);
		}
		updateExtendedRule(rule, ModuleFactory.getCustomButtonRuleModule(), FieldFactory.getCustomButtonRuleFields());

		addCustomButtonAppRelation(rule);

		return rule;
	}

	public static WorkflowRuleContext updateSystemButtonRuleWithChildren(SystemButtonRuleContext rule, SystemButtonRuleContext oldRule)throws Exception{
		updateWorkflowRuleWithChildren(rule,oldRule);

		updateApproverRule(oldRule,rule);
		updateExtendedRule(rule,ModuleFactory.getSystemButtonRuleModule(),FieldFactory.getSystemButtonRuleFields());

		addSystemButtonAppRelation(rule);

		return rule;
	}

	public static WorkflowRuleContext updateStateflowTransitionRuleWithChildren(StateflowTransitionContext rule, StateflowTransitionContext oldRule) throws Exception {
		updateWorkflowRuleWithChildren(rule, oldRule);

		StateFlowRulesAPI.addTimeFieldBasedTransition(rule);
		updateApproverRule(oldRule, rule);

		if (rule.getDialogTypeEnum() != null && rule.getDialogTypeEnum() == StateflowTransitionContext.DialogType.MODULE) {
			StateFlowRulesAPI.addOrUpdateFormDetails(rule);
		}
		updateExtendedRule(rule, ModuleFactory.getStateRuleTransitionModule(), FieldFactory.getStateRuleTransitionFields());

		return rule;
	}

	public static ApprovalRuleContext updateApprovalRuleWithChldren(ApprovalRuleContext rule, ApprovalRuleContext oldRule) throws Exception {	
		ApprovalRulesAPI.validateApprovalRule(rule);
		deleteApprovers(oldRule.getApprovers());
		updateWorkflowRuleChildIds(rule);
		updateChildRuleIds(rule);
		updateExtendedRule(rule, ModuleFactory.getApprovalRulesModule(), FieldFactory.getApprovalRuleFields());
		addApprover(rule.getId(), rule.getApprovers());
		deleteChildIdsForWorkflow(oldRule, rule);
		deleteChildRuleIds(oldRule);
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		
		return rule;
	}
	
	private static int deleteApprovers (List<ApproverContext> approvers) throws Exception {
		if (CollectionUtils.isNotEmpty(approvers)) {
			return SharingAPI.deleteSharing(approvers.stream().map(ApproverContext::getId).collect(Collectors.toList()), ModuleFactory.getApproversModule());
		}
		return 0;
	}
	
	protected static void deleteApprovalRuleChildIds (ApprovalRuleContext rule) throws Exception {
		deleteChildRuleIds(rule);
	}
	
	private static void deleteChildRuleIds (ApprovalRuleContext rule) throws Exception {
		List<Long> workflowRuleIds = new ArrayList<>();
		if (rule.getApprovalRuleId() != -1) {
			workflowRuleIds.add(rule.getApprovalRuleId());
		}
		
		if (rule.getRejectionRuleId() != -1) {
			workflowRuleIds.add(rule.getRejectionRuleId());
		}
		
		if (!workflowRuleIds.isEmpty()) {
			deleteWorkFlowRules(workflowRuleIds);
		}
		
		List<Long> formIds = new ArrayList<>();
		if (rule.getApprovalFormId() != -1) {
			formIds.add(rule.getApprovalFormId());
		}
		if(rule.getRejectionFormId() != -1) {
			formIds.add(rule.getRejectionFormId());
		}
		
		if (!formIds.isEmpty()) {
			FormsAPI.deleteForms(formIds);
		}
	}
	
	protected static ApprovalRuleContext constructApprovalRuleFromProps(Map<String, Object> prop, ModuleBean modBean) throws Exception {
		ApprovalRuleContext approvalRule = FieldUtil.getAsBeanFromMap(prop, ApprovalRuleContext.class);
		approvalRule.setApprovers(SharingAPI.getSharing(approvalRule.getId(), ModuleFactory.getApproversModule(), ApproverContext.class));
		return approvalRule;
	}

	public static void validateApprovalRule(ApprovalRuleContext rule) {
		// TODO Auto-generated method stub
		if (rule.getApprovers() != null 
				&& rule.getApprovers().size() > 1 
				&& rule.isAllApprovalRequired() 
				&& rule.getApprovalOrderEnum() == null) {
			throw new IllegalArgumentException("Approval Order is mandatory when everyone's approval is required.");
		}
	}

	public static List<Map<String, Object>> getPreviousSteps(Long ruleId, Long recordId) throws Exception {
		FacilioModule module = ModuleFactory.getApprovalStepsModule();
		List<FacilioField> fields = FieldFactory.getApprovalStepsFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("APPROVER_GROUP", "approverGroup", String.valueOf(""), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(ruleId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", String.valueOf(recordId), NumberOperators.EQUALS));
		return selectBuilder.get();
	}
	
	public static Map<Long, List<Long>> fetchPreviousSteps(List<Pair<Long, Long>> recordAndRuleIdPairs) throws Exception {
		FacilioModule module = ModuleFactory.getApprovalStepsModule();
		List<FacilioField> fields = FieldFactory.getApprovalStepsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField recordIdField = fieldMap.get("recordId");
		FacilioField ruleIdField = fieldMap.get("ruleId");
		
		Criteria criteria = new Criteria();
		for (Pair<Long, Long> pair : recordAndRuleIdPairs) {
			Criteria stepCriteria = new Criteria();
			stepCriteria.addAndCondition(CriteriaAPI.getCondition(recordIdField, String.valueOf(pair.getLeft()), PickListOperators.IS));
			stepCriteria.addAndCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(pair.getRight()), PickListOperators.IS));
			
			criteria.orCriteria(stepCriteria);
		}
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCriteria(criteria)
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<Long, List<Long>> previousSteps = new HashMap<>();
			for (Map<String, Object> prop : props) {
				Long woId = (Long) prop.get("recordId");
				Long approverId = (Long) prop.get("approverGroup");
				
				List<Long> approversId = previousSteps.get(woId);
				if (approversId == null) {
					approversId = new ArrayList<>();
					previousSteps.put(woId, approversId);
				}
				approversId.add(approverId);
			}
			
			return previousSteps;
		}
		return null;
	}

	private static void addValidations(long parentId, List<ValidationContext> validations) throws Exception {
		if (CollectionUtils.isNotEmpty(validations)) {
			FacilioModule validationModule = ModuleFactory.getValidationModule();
			List<FacilioField> fields = FieldFactory.getValidationFields(validationModule);

			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(validationModule.getTableName())
					.fields(fields);
			
			for (ValidationContext validationContext : validations) {
				validationContext.setRuleId(parentId);
				if (validationContext.getErrorMessagePlaceHolderScript() != null) {
					Long workflowId = WorkflowUtil.addWorkflow(validationContext.getErrorMessagePlaceHolderScript());
					validationContext.setErrorMessagePlaceHolderScriptId(workflowId);
				}
				if (!validationContext.isValid()) {
					throw new IllegalArgumentException("Invalid validation");
				}
				builder.addRecord(FieldUtil.getAsProperties(validationContext));
			}
			builder.save();
			
			for (int i = 0; i < builder.getRecords().size(); i++) {
				ValidationContext validationContext = validations.get(i);
				validationContext.setId((long) builder.getRecords().get(i).get("id"));
			}
		}
	}
	
	private static void deleteValidations(List<ValidationContext> validations) throws Exception {
		if (CollectionUtils.isNotEmpty(validations)) {
			List<Long> validationIds = validations.stream().map(ValidationContext::getId).collect(Collectors.toList());
			
			FacilioModule validationModule = ModuleFactory.getValidationModule();
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(validationModule.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(validationIds, validationModule));
			builder.delete();

			List<Long> workflowIds = new ArrayList<>();
			for (ValidationContext validationContext : validations) {
				if (validationContext.getErrorMessagePlaceHolderScriptId() > 0) {
					workflowIds.add(validationContext.getErrorMessagePlaceHolderScriptId());
				}
			}
			if (CollectionUtils.isNotEmpty(workflowIds)) {
				WorkflowUtil.deleteWorkflows(workflowIds);
			}
		}
	}

	public static List<ValidationContext> getValidations(long id) throws Exception {
		FacilioModule module = ModuleFactory.getValidationModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getValidationFields(module))
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(id), NumberOperators.EQUALS));
		List<Map<String, Object>> list = builder.get();
		List<ValidationContext> validations = FieldUtil.getAsBeanListFromMapList(list, ValidationContext.class);
		
		List<Long> namedCriteriaIds = new ArrayList<>();
		for (ValidationContext validation: validations) {
			if (validation.getNamedCriteriaId() > 0) {
				namedCriteriaIds.add(validation.getNamedCriteriaId());
			}
		}
		if (CollectionUtils.isNotEmpty(namedCriteriaIds)) {
			Map<Long, NamedCriteria> criteriaMap = NamedCriteriaAPI.getCriteriaAsMap(namedCriteriaIds);

			for (ValidationContext validation: validations) {
				if (validation.getNamedCriteriaId() > 0) {
					NamedCriteria namedCriteria = criteriaMap.get(validation.getNamedCriteriaId());
					validation.setNamedCriteria(namedCriteria);
				}
				if (validation.getErrorMessagePlaceHolderScriptId() > 0) {
					validation.setErrorMessagePlaceHolderScript(WorkflowUtil.getWorkflowContext(validation.getErrorMessagePlaceHolderScriptId()));
				}
			}	
		}
		return validations;
	}

	public static void deleteApproverRuleChildren(ApproverWorkflowRuleContext rule) throws Exception {
		deleteApprovers(rule.getApprovers());
		deleteValidations(rule.getValidations());

		ConfirmationDialogAPI.deleteConfirmationDialogs(rule.getId());
	}
}
