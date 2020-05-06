package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;

public class VerifyApprovalCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkOrderContext> oldWos = (List<WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		if (oldWos != null && !oldWos.isEmpty() && workOrder != null) {
			for (WorkOrderContext wo : oldWos) {
				if (wo.getModuleState() != null) {
					FacilioStatus stateContext = StateFlowRulesAPI.getStateContext(wo.getModuleState().getId());
					Long stateTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
					if ((stateContext.isRecordLocked())) {
						boolean cannotEdit = false;
						if ((stateTransitionId == null || stateTransitionId == -1)) {
							cannotEdit = true;
						}
						if (workOrder.getRequester() != null) {
							cannotEdit = false;
							User requester = workOrder.getRequester();
							workOrder = new WorkOrderContext();
							workOrder.setRequester(requester);
							context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
						}
						if (cannotEdit) {
							throw new IllegalArgumentException("Workorder with lock cannot be updated");
						}
					}

					if (wo.getApprovalFlowId() > 0 && wo.getApprovalStatus() != null) {
						boolean skipChecking = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_APPROVAL_CHECK, false);
						if (!skipChecking) {
							FacilioStatus status = TicketAPI.getStatus(wo.getApprovalStatus().getId());
							if (status.isRequestedState()) {
								throw new IllegalArgumentException("In Approval process, cannot edit meanwhile");
							}
						}
					}
				}
			}
		}
		
		return false;
	}
}
