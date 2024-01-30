package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
public class AttendanceJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(AttendanceJob.class.getName());

    @Override
    public void execute(JobContext jobContext) throws Exception {
        LOGGER.debug("marking previous day attendance");
        AttendanceAPI.markAttendanceForPreviousDay();
    }

}
