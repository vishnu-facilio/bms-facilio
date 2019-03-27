package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCreateWOJob implements Command {
    private boolean isStatusChange;
    private boolean isBulkUpdate;

    public ScheduleCreateWOJob(boolean b, boolean isStatusChange) {
        this.isBulkUpdate = b;
        this.isStatusChange = isStatusChange;
    }

    public  ScheduleCreateWOJob() {}

    @Override
    public boolean execute(Context context) throws Exception {
        List<PreventiveMaintenance> pms =  (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
        PreventiveMaintenance pm = (PreventiveMaintenance)  context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
        List<Long> pmIds = new ArrayList<>();
        if (isStatusChange) {
            if (context.get(FacilioConstants.ContextNames.RECORD_ID_LIST) != null) {
                if (pm != null && pm.isActive()) { //allows only active
                    pmIds.addAll((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
                }
            }
        } else {
            if (isBulkUpdate) {
                if (CollectionUtils.isNotEmpty(pms)) {
                    pms.forEach(i -> pmIds.add(i.getId()));
                }
            } else {
                if (pm != null && pm.getId() > 0) {
                    pmIds.add(pm.getId());
                }
            }
        }
        if (context.get(FacilioConstants.ContextNames.SKIP_WO_CREATION) != null && (Boolean) context.get(FacilioConstants.ContextNames.SKIP_WO_CREATION)) {
            return false;
        }
        for (Long id: pmIds) {
            FacilioTimer.deleteJob(id, "ScheduleNewPM");
            FacilioTimer.scheduleOneTimeJob(id, "ScheduleNewPM", 5, "facilio");
        }
        PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(pmIds, true);
        return false;
    }
}
