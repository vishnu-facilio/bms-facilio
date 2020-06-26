package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class VerifyApprovalCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> oldRecords = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
        Long stateTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
        boolean skipChecking = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_APPROVAL_CHECK, false);

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        V3WorkOrderContext workOrder = wos.get(0);
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
                            workOrder = new V3WorkOrderContext();
                            workOrder.setRequester(requester);
                            context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
                        }
                    }

                    // -99 will be set when he approved the module record, so should go to the if-block
                    if (record.getApprovalFlowId() == -99 || (record.getApprovalFlowId() > 0 && record.getApprovalStatus() != null)) {
                        if (!skipChecking) {
                            FacilioStatus status = TicketAPI.getStatus(record.getApprovalStatus().getId());
                            if (status.isRequestedState()) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "In Approval process, cannot edit meanwhile");
                            }
                        }
                    }
                    else if ((stateContext.isRecordLocked())) {
                        if (cannotEdit) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Record with lock cannot be updated");
                        }
                    }


                }
            }
        }

        return false;
    }
}
