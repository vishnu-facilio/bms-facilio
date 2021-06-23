package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class SchedulePMWorkOrderGenerationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long pmId = (Long) context.get(FacilioConstants.ContextNames.PM_ID);
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.SCHEDULE_GENERATION_TIME);
        List<Long> pmIds = Collections.singletonList(pmId);
        PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(pmIds, 1);
        //PreventiveMaintenanceAPI.markAsScheduling(pmIds);

        FacilioTimer.deleteJob(pmId, "SchedulePMBackgroundJob");
        PreventiveMaintenanceAPI.schedulePM(pmId, PreventiveMaintenanceAPI.ScheduleActions.GENERATION, endTime);
        return false;
    }

}
