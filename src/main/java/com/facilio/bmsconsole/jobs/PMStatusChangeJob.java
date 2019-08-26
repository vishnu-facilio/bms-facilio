package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PMStatusChangeJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        long pmId = jc.getJobId();
        String jobName = jc.getJobName();
        int status;
        switch (jobName) {
            case "PMCreateStatusJob": status = 1;break;
            default: status = 0;
        }

    }
}
