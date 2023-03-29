package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.job.MultiImportDataJob;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

public class ScheduleMultiImportDataJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);

        // remove any existing job
        FacilioTimer.deleteJob(importId, MultiImportDataJob.JOB_NAME);
        FacilioTimer.scheduleOneTimeJobWithTimestampInSec(importId, MultiImportDataJob.JOB_NAME, 2, "priority");
        return false;
    }
}
