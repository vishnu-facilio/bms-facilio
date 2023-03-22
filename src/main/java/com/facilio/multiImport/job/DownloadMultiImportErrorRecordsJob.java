package com.facilio.multiImport.job;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

import java.util.List;
import java.util.logging.Logger;

public class DownloadMultiImportErrorRecordsJob extends FacilioJob {
    private static final Logger LOGGER = Logger.getLogger(DownloadMultiImportErrorRecordsJob.class.getName());
    public static final String JOB_NAME = "DownloadMultiImportErrorRecordsJob";
    ImportDataDetails importDataDetails = null;
    Long importId = null;
    @Override
    public void execute(JobContext jobContext) throws Exception {

        try{
            importId = jobContext.getJobId();
            LOGGER.info("DownloadMultiImportErrorRecordsJob called for importId---------- " + importId);
            importDataDetails = MultiImportApi.getImportData(importId);

            List<ImportFileContext> importFiles = MultiImportApi.getImportFilesByImportId(importId, true);

            importDataDetails.setImportFiles(importFiles);

            FacilioChain chain = MultiImportChainUtil.getDownloadErrorRecordsChain();
            FacilioContext context = chain.getContext();

            context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
            context.put(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, importDataDetails);

            chain.execute();
            LOGGER.info("DownloadMultiImportErrorRecordsJob Completed for importId---------- " + importId);
        }catch (Exception e){
            LOGGER.severe("Exception :"+e);
            LOGGER.severe("DownloadMultiImportErrorRecordsJob Failed for importId---------- " + importId);
        }


    }
}
