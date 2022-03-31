package com.facilio.bmsconsole.imports.job;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportLookupModuleValueNotFoundException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.imports.config.ImportChainUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.json.simple.JSONObject;

import java.util.logging.Logger;

public class ImportDataJob extends FacilioJob {

    private static final Logger LOGGER = Logger.getLogger(ImportDataJob.class.getName());
    public static final String JOB_NAME = "V3_importDataJob";

    @Override
    public void execute(JobContext jobContext) throws Exception {
        ImportProcessContext importProcessContext = null;
        try {
            importProcessContext = ImportAPI.getImportProcessContext(jobContext.getJobId());

            FacilioModule module = importProcessContext.getModule();
            FacilioChain chain = ImportChainUtil.getImportChain(module.getName());
            FacilioContext context = chain.getContext();
            context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
            context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
            chain.execute();


            // update the status of as imported
            importProcessContext.setStatus(ImportProcessContext.ImportStatus.IMPORTED.getValue());
            LOGGER.info("importProcessContext -- " + importProcessContext);
            ImportAPI.updateImportProcess(importProcessContext);
        } catch (Exception exception) {
            String exceptionMessage;
            boolean sendExceptionMail = false;
            if (exception instanceof ImportParseException) {
                ImportParseException importParseException = (ImportParseException) exception;
                exceptionMessage = importParseException.getClientMessage();
            } else if (exception instanceof ImportFieldValueMissingException) {
                ImportFieldValueMissingException importFieldValueMissingException = (ImportFieldValueMissingException) exception;
                exceptionMessage = importFieldValueMissingException.getClientMessage();
            } else if (exception instanceof ImportMandatoryFieldsException) {
                ImportMandatoryFieldsException importAssetMandExp = (ImportMandatoryFieldsException) exception;
                exceptionMessage = importAssetMandExp.getClientMessage();
            } else if (exception instanceof ImportLookupModuleValueNotFoundException) {
                ImportLookupModuleValueNotFoundException importModMissing = (ImportLookupModuleValueNotFoundException) exception;
                exceptionMessage = importModMissing.getClientMessage();
            } else {
                sendExceptionMail = true;
                exceptionMessage = exception.getMessage();
            }
            LOGGER.severe("Import Data Error Catch -- " + jobContext.getJobId() + " " + exceptionMessage);

            if (importProcessContext != null) {
                importProcessContext.setStatus(ImportProcessContext.ImportStatus.FAILED.getValue());
                JSONObject meta = importProcessContext.getImportJobMetaJson();
                if (meta == null) {
                    meta = new JSONObject();
                }
                meta.put("importError", exceptionMessage);
                importProcessContext.setImportJobMeta(meta.toJSONString());
                ImportAPI.updateImportProcess(importProcessContext);
            }

            if (sendExceptionMail) {
                CommonCommandUtil.emailException(getClass().getSimpleName(),
                        "Import failed - orgid -- " + AccountUtil.getCurrentOrg().getId(), exception);
            }
            throw exception;
        }
    }
}
