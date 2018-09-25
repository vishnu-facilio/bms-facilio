package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class VerifyApprovalCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkOrderContext> oldWos = (List<WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		if (oldWos != null && !oldWos.isEmpty() && workOrder != null) {
			workOrder.setApprovalRuleId(-1); //Approval Rule ID cannot be changed by user action
			List<Long> workflowIds = new ArrayList<>();
			for (WorkOrderContext wo : oldWos) {
				if (wo.getApprovalStateEnum() == ApprovalState.REQUESTED) {
					workflowIds.add(wo.getApprovalRuleId());
					
					if (workOrder.getApprovalStateEnum() != ApprovalState.APPROVED && workOrder.getApprovalStateEnum() != ApprovalState.REJECTED) {
						throw new IllegalArgumentException("Work Request has to be either approved or rejected. It can't be updated until then.");
					}
				}
			}
			
			if (!workflowIds.isEmpty()) {
				List<WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRules(workflowIds);
				Map<Long, ApprovalRuleContext> ruleMap = rules.stream()
														.collect(Collectors.toMap(WorkflowRuleContext::getId, r -> (ApprovalRuleContext) r));
				
				for (WorkOrderContext wo : oldWos) {
					if (wo.getApprovalStateEnum() == ApprovalState.REQUESTED) {
						if (!ruleMap.get(wo.getApprovalRuleId()).hasApprovalPermission()) {
							throw new IllegalArgumentException("You are not authorised to perform this operation");
						}
					}
				}
			}
		}
		
		return false;
	}

}
