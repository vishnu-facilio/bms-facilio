package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.ApprovalRulesAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.ApproverContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;

public class FetchApprovalRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkOrderContext> workOrders = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST);
		if (workOrders == null) {
			WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
			if (wo != null) {
				workOrders = Collections.singletonList(wo);
			}
		}
		
		if (workOrders != null && !workOrders.isEmpty()) {
			
//			if (workOrders.get(0).getModuleState() != null && workOrders.get(0).getModuleState().getId() != -1) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
			if (modBean.getField("moduleState", ContextNames.WORK_ORDER) != null && ((viewName != null && viewName.equals("approval_requested")) || workOrders.size() == 1)) {
				setAvailableStates(context, workOrders);
			}
//				return false;
//			}
			
			List<Long> ruleIds = new ArrayList<>();
			List<Pair<Long, Long>> recordAndRuleIdPairs = new ArrayList<>();
			for (WorkOrderContext wo : workOrders) {
				// moved to state flow
				if (wo.getModuleState() != null) {
					continue;
				}
				
				if (wo.getApprovalStateEnum() == ApprovalState.REQUESTED) {
					ruleIds.add(wo.getApprovalRuleId());
					recordAndRuleIdPairs.add(Pair.of(wo.getId(), wo.getApprovalRuleId()));
				}
			}
			
			if (!ruleIds.isEmpty()) {
				List<WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRules(ruleIds);
				Map<Long, ApprovalRuleContext> ruleMap = new HashMap<>();
				List<Long> formIds = new ArrayList<>();
				for (WorkflowRuleContext rule : rules) {
					ApprovalRuleContext approvalRule = (ApprovalRuleContext) rule;
					ruleMap.put(rule.getId(), approvalRule);
					if (approvalRule.getApprovalFormId() != -1) {
						formIds.add(approvalRule.getApprovalFormId());
					}
					if (approvalRule.getRejectionFormId() != -1) {
						formIds.add(approvalRule.getRejectionFormId());
					}
				}
				
				if (!formIds.isEmpty()) {
					Map<Long, FacilioForm> forms = FormsAPI.getFormsAsMap(formIds);
					for (ApprovalRuleContext approvalRule : ruleMap.values()) {
						if (approvalRule.getApprovalFormId() != -1) {
							approvalRule.setApprovalForm(forms.get(approvalRule.getApprovalFormId()));
							if (approvalRule.getApprovalForm() == null) {
								throw new IllegalArgumentException("Invalid approval rule with corrupted approval form id");
							}
						}
						
						if (approvalRule.getRejectionFormId() != -1) {
							approvalRule.setRejectionForm(forms.get(approvalRule.getRejectionFormId()));
							if (approvalRule.getRejectionForm() == null) {
								throw new IllegalArgumentException("Invalid approval rule with corrupted rejection form id");
							}
						}
					}
				}
				
				Map<Long, List<Long>> previousSteps = ApprovalRulesAPI.fetchPreviousSteps(recordAndRuleIdPairs);
				List<Long> groupIds = null;
				for (WorkOrderContext wo : workOrders) {
					// moved to state flow
					if (wo.getModuleState() != null) {
						continue;
					}
					
					if (wo.getApprovalStateEnum() == ApprovalState.REQUESTED) {
						ApprovalRuleContext rule = ruleMap.get(wo.getApprovalRuleId()); 
						wo.setApprovalRule(rule);
						
						if (rule.getApprovers() != null) {
							List<Long> currentPreviousSteps = previousSteps != null ? previousSteps.get(wo.getId()) : null;
							if (currentPreviousSteps != null) {
								List<ApproverContext> waitingApprovals = rule.getApprovers().stream().filter(a -> !currentPreviousSteps.contains(a.getId())).collect(Collectors.toList());
								wo.setWaitingApprovals(waitingApprovals);
							}
							else {
								wo.setWaitingApprovals(rule.getApprovers());
							}
							User currentUser = AccountUtil.getCurrentUser();
							for(ApproverContext approverContext: wo.getWaitingApprovals()) {
								switch(approverContext.getTypeEnum()) {
									case USER:
										if (currentUser.getOuid() == approverContext.getUserId()) {
											wo.setCanCurrentUserApprove(true);
										}
										else if (approverContext.getUserId() == -1 && approverContext.getFieldId() > 0) {
											FacilioField field = modBean.getField(approverContext.getFieldId());
											Map<String,Object> userObj = (Map<String, Object>) FieldUtil.getAsProperties(wo).get(field.getName());
											if (userObj != null) {
												Long ouid = (Long) userObj.get("id");
												if (ouid != null && ouid == currentUser.getOuid()) {
													wo.setCanCurrentUserApprove(true);
												}
											}
										}
										break;
									case ROLE:
										if (currentUser.getRoleId() == approverContext.getRoleId()) {
											wo.setCanCurrentUserApprove(true);
										}
										break;
									case GROUP:
										if (groupIds == null) {
											List<Group> myGroups = AccountUtil.getGroupBean().getMyGroups(currentUser.getOuid());
											if (myGroups != null && !myGroups.isEmpty()) {
												groupIds = myGroups.stream().map(gp -> gp.getGroupId()).collect(Collectors.toList());
											}
											else {
												groupIds = new ArrayList<>();
											}
										}
										if (groupIds.contains(approverContext.getGroupId())) {
											wo.setCanCurrentUserApprove(true);
										}
										break;
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	private void setAvailableStates(Context context, List<WorkOrderContext> workOrders) throws Exception {
		Map<String, List<WorkflowRuleContext>> stateFlows = StateFlowRulesAPI.getAvailableStates(workOrders);
		for(WorkOrderContext workorder: workOrders) {
			if (workorder.getModuleState() == null) {
				continue;
			}
			String key = workorder.getStateFlowId() + "_" + workorder.getModuleState().getId();
			if(stateFlows.containsKey(key)) {
				List<WorkflowRuleContext> evaluateStateFlowAndExecuteActions = StateFlowRulesAPI.evaluateStateFlowAndExecuteActions(new ArrayList<>(stateFlows.get(key)), ContextNames.WORK_ORDER, workorder, context);
				workorder.setCanCurrentUserApprove(CollectionUtils.isNotEmpty(evaluateStateFlowAndExecuteActions));
			}
		}
		context.put("stateFlows", stateFlows);
	}

}
