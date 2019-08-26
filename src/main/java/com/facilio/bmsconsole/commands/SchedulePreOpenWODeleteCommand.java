package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

import java.util.List;

public class SchedulePreOpenWODeleteCommand extends FacilioCommand {
    private boolean isStatusChange;

    public SchedulePreOpenWODeleteCommand(boolean isStatusChange) {
        this.isStatusChange = isStatusChange;
    }
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        PreventiveMaintenance pm = (PreventiveMaintenance)  context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

        if (this.isStatusChange && !pm.isActive()) {
            PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(recordIds, 1);
            for (long recordId: recordIds) {
                FacilioTimer.deleteJob(recordId, "ScheduleDeletePreOpenJob");
                FacilioTimer.scheduleOneTimeJob(recordId, "ScheduleDeletePreOpenJob", 1, "priority");
            }
        }

        return false;
    }
}
