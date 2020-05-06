package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class VerifyApprovalCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ModuleBaseWithCustomFields> oldRecords = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		Long stateTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
		boolean skipChecking = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_APPROVAL_CHECK, false);
		
		if (oldRecords != null && !oldRecords.isEmpty()) {
			for (ModuleBaseWithCustomFields record : oldRecords) {
				if (record.getModuleState() != null) {
					FacilioStatus stateContext = StateFlowRulesAPI.getStateContext(record.getModuleState().getId());
					
					if ((stateContext.isRecordLocked())) {
						boolean cannotEdit = false;
						if ((stateTransitionId == null || stateTransitionId == -1)) {
							cannotEdit = true;
						}
						if ( workOrder != null && workOrder.getRequester() != null) {
							cannotEdit = false;
							skipChecking = true;
							User requester = workOrder.getRequester();
							workOrder = new WorkOrderContext();
							workOrder.setRequester(requester);
							context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
						}
						if (cannotEdit) {
							throw new IllegalArgumentException("Record with lock cannot be updated");
						}
					}

					if (record.getApprovalFlowId() > 0 && record.getApprovalStatus() != null) {
						if (!skipChecking) {
							FacilioStatus status = TicketAPI.getStatus(record.getApprovalStatus().getId());
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
