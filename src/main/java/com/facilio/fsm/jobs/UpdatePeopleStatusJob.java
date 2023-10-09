package com.facilio.fsm.jobs;

import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class UpdatePeopleStatusJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(com.facilio.fsm.jobs.UpdatePeopleStatusJob.class.getName());

    @Override
    public void execute(JobContext jobContext) throws Exception {
        LOGGER.debug("updating people status");
        V3PeopleAPI.updatePeopleStatus();
    }
}

