package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class UpdateWorkorderFieldsForUpdateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        List<V3WorkOrderContext> oldWos = (List<V3WorkOrderContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
        Long lastSyncTime = (Long) context.get(FacilioConstants.ContextNames.LAST_SYNC_TIME);
            // For syncing, only one workorder will be there

        if(CollectionUtils.isNotEmpty(wos)) {
            V3WorkOrderContext oldWoForSync = oldWos.get(0);
            if (lastSyncTime != null && oldWoForSync.getModifiedTime() > lastSyncTime) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "The workorder was modified after the last sync");
            }
            if (wos.get(0).getSyncTime() != null && oldWoForSync.getModifiedTime() > wos.get(0).getSyncTime()) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "The workorder was modified after the last sync");
            }
            updateWODetails(wos.get(0), oldWos.get(0));
        }
        return false;
    }

    private void updateWODetails (V3WorkOrderContext wo, V3WorkOrderContext oldWo) throws Exception {
        if(oldWo.getAssignedBy() == null || // initially when assignedTo/Team is updated for the first time
                (oldWo.getAssignedTo() != null && wo.getAssignedTo() != null && oldWo.getAssignedTo().getId() != wo.getAssignedTo().getId()) || // assignedTo is changed
                (oldWo.getAssignedTo() == null && wo.getAssignedTo() != null && wo.getAssignedTo().getId() > 0)  || // assigned to is updated after team is added
                (oldWo.getAssignmentGroup() != null && wo.getAssignmentGroup() != null && oldWo.getAssignmentGroup().getId() != wo.getAssignmentGroup().getId()) || // team is changed
                (oldWo.getAssignmentGroup() == null && wo.getAssignmentGroup() != null && wo.getAssignmentGroup().getId() > 0) // team is changed
        ) {
            V3TicketAPI.updateTicketAssignedBy(wo);
        }
        wo.setModifiedTime(wo.getCurrentTime());
        if(wo.getStatus() != null &&  wo.getStatus().getId() > 0) {
            FacilioStatus statusObj = V3TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), wo.getStatus().getId());
            wo.setStatus(statusObj);
        }
        if (wo.getDueDate() != null && wo.getDueDate() > 0) {
            wo.setEstimatedEnd(wo.getDueDate());
        }
    }
}
