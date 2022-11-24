package com.facilio.bmsconsole.jobs;

import java.util.Collections;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.v3.util.V3Util;

import lombok.extern.log4j.Log4j;

/*
    TODO(4): Test
    If the resources has Assigned to,
        - ensure if the WO goes to ASSIGNED STATE and
        - if it doesn't have any assigned - WO goes to OPEN/SUBMITTED STATE
 */
@Log4j
public class OpenScheduleWOV2 extends FacilioJob {
    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {
            long woId = jobContext.getJobId();
            
            V3Util.postCreateRecord(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(woId) , null, null, null);
            
        } catch (Exception e) {
            CommonCommandUtil.emailException("OpenScheduledWOV2", ""+jobContext.getJobId(), e);
            LOGGER.error("WorkOrder Status Change failed: ", e);
            throw e;
        }
    }

}
