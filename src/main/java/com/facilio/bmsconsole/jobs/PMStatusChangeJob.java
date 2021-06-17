package com.facilio.bmsconsole.jobs;

import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

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
