package com.facilio.bmsconsole.imports.job;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.imports.config.ImportChainUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.taskengine.job.InstantJob;
import org.json.simple.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class InsertDataLogJob extends InstantJob {

    public static final String JOB_NAME = "V3InsertDataLogJob";

    private static final Logger LOGGER = Logger.getLogger(InsertDataLogJob.class.getName());

    @Override
    public void execute(FacilioContext context) throws Exception {
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        try {
            FacilioModule module = importProcessContext.getModule();
            if (module == null) {
                throw new IllegalArgumentException("Module cannot be empty");
            }

            FacilioChain chain = ImportChainUtil.getParseChain(module.getName());
            FacilioContext parseContext = chain.getContext();
            parseContext.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
            parseContext.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
            chain.execute();

            boolean hasDuplicates = (boolean) parseContext.get(ImportAPI.ImportProcessConstants.HAS_DUPLICATE_ENTRIES);

            if(hasDuplicates) {
                importProcessContext.setStatus(ImportProcessContext.ImportStatus.RESOLVE_VALIDATION.getValue());
                ImportAPI.updateImportProcess(importProcessContext);
            }
            else {
                importProcessContext.setStatus(ImportProcessContext.ImportStatus.VALIDATION_COMPLETE.getValue());
                importProcessContext = ImportAPI.updateTotalRows(importProcessContext);
                ImportAPI.updateImportProcess(importProcessContext);
            }
        } catch(Exception e) {
            String message;
            ImportProcessContext.ImportStatus importStatus = ImportProcessContext.ImportStatus.PARSING_FAILED;
            boolean sendExceptionEmail = false;
            if(e instanceof ImportMandatoryFieldsException) {
                ImportMandatoryFieldsException importFieldException = (ImportMandatoryFieldsException) e;
                message = importFieldException.getClientMessage();
            }
            else if(e instanceof ImportFieldValueMissingException) {
                ImportFieldValueMissingException importFieldException = (ImportFieldValueMissingException) e;
                message = importFieldException.getClientMessage();
            } else if (e instanceof ImportParseException) {
                ImportParseException importParseException = (ImportParseException) e;
                message = importParseException.getClientMessage();
            } else if (e instanceof IllegalArgumentException) {
                message = e.getMessage();
                importStatus = ImportProcessContext.ImportStatus.FAILED;
            }
            else {
                sendExceptionEmail = true;
                message = e.getMessage();
            }
            try {
                if(importProcessContext != null) {
                    JSONObject meta = importProcessContext.getImportJobMetaJson();
                    if(meta != null && !meta.isEmpty()) {
                        meta.put("initialParseError", message);
                    }
                    else {
                        meta = new JSONObject();
                        meta.put("initialParseError", message);
                    }
                    importProcessContext.setImportJobMeta(meta.toJSONString());
                    importProcessContext.setStatus(importStatus.getValue());
                    ImportAPI.updateImportProcess(importProcessContext);
                    LOGGER.severe("Import failed: " + message);
                }
            } catch(Exception a) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            if (sendExceptionEmail) {
                CommonCommandUtil.emailException("V3 Import Failed", "Import failed - orgid -- " + AccountUtil.getCurrentOrg().getId(), e);
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
