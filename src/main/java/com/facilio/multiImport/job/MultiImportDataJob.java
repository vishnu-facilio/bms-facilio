package com.facilio.multiImport.job;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

import java.util.List;
import java.util.logging.Logger;

public class MultiImportDataJob extends FacilioJob {
    private static final Logger LOGGER = Logger.getLogger(MultiImportDataJob.class.getName());
    public static final String JOB_NAME = "MultiImportDataJob";

    private static final long BATCH_LIMIT = 5000;
    ImportDataDetails importDataDetails = null;
    Long importId = null;

    @Override
    public void execute(JobContext jobContext) throws Exception {

        try {
            importId = jobContext.getJobId();
            LOGGER.info("MultiImportDataJob called for importId---------- " + importId);
            importDataDetails = MultiImportApi.getImportData(importId);

            List<ImportFileContext> importFiles = MultiImportApi.getImportFilesByImportId(importId, true);

            importDataDetails.setImportFiles(importFiles);

            FacilioChain chain = MultiImportChainUtil.getImportChain();
            FacilioContext context = chain.getContext();

            context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
            context.put(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, importDataDetails);

            chain.execute();

            checkRemainingRecordsCountAndScheduleNextJob(jobContext);

        } catch (Exception ex) {
            LOGGER.severe("Error Occured in MultiImportDataJob  -- importId: " + importId + "  Exception:" + ex);
            throw ex;
        }

    }

    private void checkRemainingRecordsCountAndScheduleNextJob(JobContext jobContext) {
        if (importDataDetails == null) {
            return;
        }
        ImportDataStatus importStatus = importDataDetails.getStatusEnum();
        if (importStatus == ImportDataStatus.IMPORT_COMPLETED || importStatus == ImportDataStatus.IMPORT_FAILED) {
            return;
        }

        long totalImportRecordsCount = importDataDetails.getTotalRecords();
        long processedRecordsCount = importDataDetails.getProcessedRecordsCount();
        long remainingRecords = totalImportRecordsCount - processedRecordsCount;

        if (remainingRecords > 0) {
            LOGGER.info("Remaining records to be process " + remainingRecords);
            LOGGER.info("Next MultiImportDataJob scheduled for importId---------- " + importId);
            jobContext.setNextExecutionTime((System.currentTimeMillis() + (10 * 1000)) / 1000);
        }

    }

}