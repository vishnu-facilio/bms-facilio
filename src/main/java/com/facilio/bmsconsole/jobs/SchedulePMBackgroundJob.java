package com.facilio.bmsconsole.jobs;

import java.util.Collections;
import java.util.List;

import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SchedulePMBackgroundJob extends FacilioJob {

    private static final Logger LOGGER = Logger.getLogger(SchedulePMBackgroundJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        LOGGER.log(Level.ERROR, "SchedulePMBackgroundJob -> execute(JobContext): ");
        long pmId = jc.getJobId();
        List<Long> pmIds = Collections.singletonList(pmId);
        PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(pmIds, 1);
        PreventiveMaintenanceAPI.schedulePM(pmId, PreventiveMaintenanceAPI.ScheduleActions.INIT, -1);
    }

}
