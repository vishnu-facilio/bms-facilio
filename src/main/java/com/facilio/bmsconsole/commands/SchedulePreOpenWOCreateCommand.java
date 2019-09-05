package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.util.BmsJobUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class SchedulePreOpenWOCreateCommand extends FacilioCommand {
    private boolean isStatusChange;
    private boolean isBulkUpdate;

    public SchedulePreOpenWOCreateCommand(boolean b, boolean isStatusChange) {
        this.isBulkUpdate = b;
        this.isStatusChange = isStatusChange;
    }

    public SchedulePreOpenWOCreateCommand() {}

    @Override
    public boolean executeCommand(Context context) throws Exception {
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

        long delay = 300;

        for (Long id: pmIds) {
            FacilioTimer.deleteJob(id, "SchedulePMBackgroundJob");
            BmsJobUtil.deleteJobWithProps(id, "ScheduleNewPM");
            FacilioTimer.scheduleOneTimeJob(id, "SchedulePMBackgroundJob", delay, "priority");
        }
        return false;
    }
}
