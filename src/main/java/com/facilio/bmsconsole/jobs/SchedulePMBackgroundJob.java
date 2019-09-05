package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

import java.util.Collections;
import java.util.List;

public class SchedulePMBackgroundJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        long pmId = jc.getJobId();
        List<Long> pmIds = Collections.singletonList(pmId);
        PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(pmIds, 1);
        PreventiveMaintenanceAPI.schedulePM(pmId, PreventiveMaintenanceAPI.ScheduleActions.INIT, -1);
    }

}
