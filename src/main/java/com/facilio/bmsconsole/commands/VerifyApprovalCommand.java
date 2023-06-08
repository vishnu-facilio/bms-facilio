package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;

@Log4j
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

					boolean cannotEdit = false;
					if (stateContext.isRecordLocked()) {
						if ((stateTransitionId == null || stateTransitionId == -1)) {
							cannotEdit = true;
						}
						if ( workOrder != null && workOrder.getRequester() != null && workOrder.getRequester().getUid() > 0) {
							cannotEdit = false;
							skipChecking = true;
							User requester = workOrder.getRequester();
							workOrder = new WorkOrderContext();
							workOrder.setRequester(requester);
							context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
						}
					}

					// -99 will be set when he approved the module record, so should go to the if-block
					if (record.getApprovalFlowId() == -99 || (record.getApprovalFlowId() > 0 && record.getApprovalStatus() != null)) {
						if (!skipChecking) {
							FacilioStatus status = TicketAPI.getStatus(record.getApprovalStatus().getId());
							logRecordIdAndParentModuleId(record.getId(), stateContext.getParentModuleId());
							FacilioUtil.throwIllegalArgumentException(status.isRequestedState(), "In Approval process, cannot edit meanwhile");
						}
					}
 					else if ((stateContext.isRecordLocked())) {
						logRecordIdAndParentModuleId(record.getId(), stateContext.getParentModuleId());
 						FacilioUtil.throwIllegalArgumentException(cannotEdit, "Record with lock cannot be updated");
					}
				}
			}
		}
		
		return false;
	}
	private void logRecordIdAndParentModuleId(long recordId, long parentModuleId){
		LOGGER.info("Record ID: " + recordId);
		LOGGER.info("Parent Module ID: " + parentModuleId);
	}
}
