package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.chain.Chain;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.FieldChangeFieldContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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
				rule.setApprovalRuleId(addWorkflowRule(rule.getApprovalRule(), rule.getApprovalActions()));
			}
		}
		if (rule.getApprovalFormId() == -1) {
			if (rule.getApprovalForm() == null) {
				throw new IllegalArgumentException("Approval Form cannot be empty for approval rule");
			}
			else {
				rule.setApprovalFormId(FormsAPI.createForm(rule.getApprovalForm(), module));
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
				rule.setRejectionRuleId(addWorkflowRule(rule.getRejectionRule(), rule.getRejectionActions()));
			}
		}
		if (rule.getRejectionFormId() == -1) {
			if (rule.getRejectionForm() == null) {
				throw new IllegalArgumentException("Rejection Form cannot be empty for approval rule");
			}
			else {
				rule.setRejectionFormId(FormsAPI.createForm(rule.getRejectionForm(), module));
			}
		}
	}
	
	protected static void addApprovers(ApprovalRuleContext rule) throws Exception {
		if (rule.getApprovers() != null && !rule.getApprovers().isEmpty()) {
			SharingAPI.addSharing((SharingContext) rule.getApprovers(), rule.getId(), ModuleFactory.getApproversModule());
		}
	}
	
	private static long addWorkflowRule(WorkflowRuleContext rule, List<ActionContext> actions) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, actions);
		
		Chain addRule = TransactionChainFactory.getAddWorkflowRuleChain();
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
		WorkflowEventContext event = new WorkflowEventContext();
		event.setModuleId(approvalRule.getEvent().getModuleId());
		event.setActivityType(ActivityType.FIELD_CHANGE);
		rule.setEvent(event);
		
		FacilioModule module = modBean.getModule(event.getModuleId());
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
	
	public static ApprovalRuleContext updateApprovalRuleWithChldren(ApprovalRuleContext rule) throws Exception {
		ApprovalRuleContext oldRule = (ApprovalRuleContext) getWorkflowRule(rule.getId());
		updateWorkflowRuleChildIds(rule);
		updateChildRuleIds(rule);
		updateExtendedRule(rule, ModuleFactory.getApprovalRulesModule(), FieldFactory.getApprovalRuleFields());
		deleteChildRuleIds(oldRule);
		deleteChildIdsForWorkflow(oldRule, rule);
		
		if (rule.getName() == null) {
			rule.setName(oldRule.getName());
		}
		
		return rule;
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
		FormsAPI.deleteForms(Arrays.asList(rule.getApprovalFormId(), rule.getRejectionFormId()));
	}
	
	protected static ApprovalRuleContext constructApprovalRuleFromProps(Map<String, Object> prop, ModuleBean modBean) throws Exception {
		ApprovalRuleContext approvalRule = FieldUtil.getAsBeanFromMap(prop, ApprovalRuleContext.class);
		approvalRule.setApprovers(SharingAPI.getSharing(approvalRule.getId(), ModuleFactory.getApproversModule()));
		return approvalRule;
	}
}
