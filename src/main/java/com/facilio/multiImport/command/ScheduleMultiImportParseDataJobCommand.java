package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.job.ParseAndLogMultiImportDataJob;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

public class ScheduleMultiImportParseDataJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);

        FacilioTimer.deleteJob(importId, ParseAndLogMultiImportDataJob.JOB_NAME);
        FacilioTimer.scheduleOneTimeJobWithTimestampInSec(importId, ParseAndLogMultiImportDataJob.JOB_NAME, 10, "priority");

        ImportDataDetails importDataDetails = (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);

        importDataDetails.setStatus(ImportDataStatus.PARSING_STARTED);
        MultiImportApi.updateImportStatus(importDataDetails);
        return false;
    }
}
