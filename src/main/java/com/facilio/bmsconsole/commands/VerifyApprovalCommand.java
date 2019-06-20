package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;

public class VerifyApprovalCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkOrderContext> oldWos = (List<WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		if (oldWos != null && !oldWos.isEmpty() && workOrder != null) {
			workOrder.setApprovalRuleId(-1); //Approval Rule ID cannot be changed by user action
			List<Long> ruleIds = new ArrayList<>();
			for (WorkOrderContext wo : oldWos) {
				if (wo.getModuleState() != null) {
					FacilioStatus stateContext = StateFlowRulesAPI.getStateContext(wo.getModuleState().getId());
					Long stateTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
					if (stateContext.isRecordLocked() && (stateTransitionId == null || stateTransitionId == -1)) {
						throw new IllegalArgumentException("Workorder with lock cannot be updated");
					}
				}
				else {
					if (wo.getApprovalStateEnum() == ApprovalState.REQUESTED) {
						ruleIds.add(wo.getApprovalRuleId());
						
						if (workOrder.getApprovalStateEnum() != ApprovalState.APPROVED && workOrder.getApprovalStateEnum() != ApprovalState.REJECTED) {
							throw new IllegalArgumentException("Work Request has to be either approved or rejected. It can't be updated until then.");
						}
					}
				}
			}
			
			if (!ruleIds.isEmpty()) {
				List<WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRules(ruleIds);
				Map<Long, ApprovalRuleContext> ruleMap = rules.stream()
														.collect(Collectors.toMap(WorkflowRuleContext::getId, r -> (ApprovalRuleContext) r));
				
				List<WorkOrderContext> newWos = new ArrayList<>();
				for (WorkOrderContext wo : oldWos) {
					if (wo.getApprovalStateEnum() == ApprovalState.REQUESTED) {
						ApprovalRuleContext rule = ruleMap.get(wo.getApprovalRuleId()); 
						if (!rule.hasApprovalPermission(wo)) {
							throw new IllegalArgumentException("You are not authorised to perform this operation");
						}
						else if (!rule.verified(wo, workOrder.getApprovalStateEnum())) {
							WorkOrderContext newWo = FieldUtil.cloneBean(workOrder, WorkOrderContext.class);
							newWo.setId(wo.getId());
							newWo.setApprovalState(ApprovalState.REQUESTED);
							newWos.add(newWo);
						}
					}
				}
				
				if (!newWos.isEmpty()) {
					context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, newWos);
				}
			}
		}
		
		return false;
	}
}
