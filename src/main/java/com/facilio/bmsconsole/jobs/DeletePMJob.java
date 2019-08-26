package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

import java.util.Collections;
import java.util.List;

public class DeletePMJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        long pmId = jc.getJobId();
        List<Long> pmIds = Collections.singletonList(pmId);
        PreventiveMaintenanceAPI.deleteScheduledWorkorders(pmIds);
        PreventiveMaintenanceAPI.deletePMs(pmIds);
    }
}
