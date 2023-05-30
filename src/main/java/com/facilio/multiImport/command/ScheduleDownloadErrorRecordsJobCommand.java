package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.job.DownloadMultiImportErrorRecordsJob;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

public class ScheduleDownloadErrorRecordsJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        ImportDataDetails importDataDetails = (ImportDataDetails)context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
        Long errorFileId = importDataDetails.getErrorFileId();

        if(errorFileId!=-1l){
            FileStore fs = FacilioFactory.getFileStore();
            String fileUrl = fs.getDownloadUrl(errorFileId);
            context.put(FacilioConstants.ContextNames.FILE_URL,fileUrl);
        }else{
            JobContext jobContext = FacilioTimer.getJob(importId,DownloadMultiImportErrorRecordsJob.JOB_NAME);
            if(jobContext == null && importDataDetails.getStatusEnum() == ImportDataStatus.IMPORT_COMPLETED){
                FacilioTimer.scheduleOneTimeJobWithTimestampInSec(importId, DownloadMultiImportErrorRecordsJob.JOB_NAME, 10, "priority");
            }
            else if(!jobContext.isActive()){
                FacilioTimer.deleteJob(importId, DownloadMultiImportErrorRecordsJob.JOB_NAME);
                FacilioTimer.scheduleOneTimeJobWithTimestampInSec(importId, DownloadMultiImportErrorRecordsJob.JOB_NAME, 10, "priority");
            }
            String message = "Downloading in progress";
            context.put(FacilioConstants.ContextNames.MESSAGE,message);
        }

        return false;
    }
}
