package com.facilio.bmsconsole.imports.command;

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
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.logging.Logger;

public class ImportProcessCommand extends FacilioCommand implements PostTransactionCommand {
    private static final Logger LOGGER = Logger.getLogger(ImportProcessCommand.class.getName());
    private ImportProcessContext importProcessContext = null;
    private String exceptionMessage;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long importProcessId = (long) context.get(FacilioConstants.ContextNames.ID);
        try {
            importProcessContext = ImportAPI.getImportProcessContext(importProcessId);

            FacilioModule module = importProcessContext.getModule();
            FacilioChain chain = ImportChainUtil.getImportChain(module.getName());
            FacilioContext importChainContext = chain.getContext();
            importChainContext.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
            importChainContext.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
            chain.execute();

        } catch (Exception exception) {
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
            LOGGER.severe("Import Data Error Catch -- " + importProcessId + ": " + exceptionMessage);

            if (sendExceptionMail) {
                CommonCommandUtil.emailException(getClass().getSimpleName(),
                        "Import failed - orgid -- " + AccountUtil.getCurrentOrg().getId(), exception);
            }
            throw exception;
        }
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        if (StringUtils.isNotEmpty(exceptionMessage)) {
            handleException();
        } else {
            // update the status of as imported
            importProcessContext.setStatus(ImportProcessContext.ImportStatus.IMPORTED.getValue());
            LOGGER.info("importProcessContext -- " + importProcessContext);
            ImportAPI.updateImportProcess(importProcessContext);
        }
        return false;
    }

    @Override
    public void onError() throws Exception {
        handleException();
    }

    private void handleException() throws Exception {
        if (importProcessContext != null) {
            importProcessContext.setStatus(ImportProcessContext.ImportStatus.FAILED.getValue());
            JSONObject meta = importProcessContext.getImportJobMetaJson();
            if (meta == null) {
                meta = new JSONObject();
            }
            meta.put("importError", exceptionMessage);
            meta.put("errorMessage", exceptionMessage);
            importProcessContext.setImportJobMeta(meta.toJSONString());
            ImportAPI.updateImportProcess(importProcessContext);
        }
    }
}
