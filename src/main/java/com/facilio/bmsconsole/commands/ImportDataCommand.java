package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportAssetMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportDataCommand extends FacilioCommand implements PostTransactionCommand {

	private static final Logger LOGGER = Logger.getLogger(ImportDataCommand.class.getName());

	private ImportProcessContext importProcessContext = null;
	private Long jobId;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	private boolean sendExceptionMail = false;

	@Override
	public boolean executeCommand(Context commandContext) throws Exception {
		// TODO Auto-generated method stub
		try {
			jobId = (Long) commandContext.get(ImportAPI.ImportProcessConstants.JOB_ID);

			LOGGER.info("IMPORT DATA JOB COMMAND CALLED -- " + jobId);

			importProcessContext = ImportAPI.getImportProcessContext(jobId);

			if (importProcessContext.getImportJobMeta() != null) {
				if (!importProcessContext.getImportJobMetaJson().isEmpty()) {
					Long assetId = (Long) importProcessContext.getImportJobMetaJson().get("assetId");
					if (assetId != null) {
						importProcessContext.setAssetId(assetId);
					}
				}
			}
			if (!importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET)) {
				FacilioChain importChain = TransactionChainFactory.getImportChain();
				importChain.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
				importChain.execute();
			} else {
				if (importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.INSERT_SKIP.getValue()
						|| importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE
								.getValue()
						|| importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL
								.getValue()
						|| importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.BOTH.getValue()
						|| importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.BOTH_NOT_NULL
								.getValue()) {
					FacilioChain importChain = TransactionChainFactory.getImportChain();
					importChain.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
					importChain.execute();
				} else {
					FacilioChain bulkAssetImportChain = TransactionChainFactory.getBulkAssertImportChain();
					bulkAssetImportChain.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
					bulkAssetImportChain.execute();
				}
			}

//			if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
//				ReadingsAPI.updateReadingDataMeta(true);
//			}

		} catch (Exception exception) {
			if (exception instanceof ImportParseException) {
				ImportParseException importParseException = (ImportParseException) exception;
				exceptionMessage = importParseException.getClientMessage();
			} else if (exception instanceof ImportFieldValueMissingException) {
				ImportFieldValueMissingException importFieldValueMissingException = (ImportFieldValueMissingException) exception;
				exceptionMessage = importFieldValueMissingException.getClientMessage();
			} else if (exception instanceof ImportAssetMandatoryFieldsException) { 
				ImportAssetMandatoryFieldsException importAssetMandExp = (ImportAssetMandatoryFieldsException) exception;
				exceptionMessage = importAssetMandExp.getClientMessage();
			} else {
				sendExceptionMail = true;
				exceptionMessage = exception.getMessage();
			}
			stack = exception.getStackTrace();
			LOGGER.severe("Import Data Error Catch -- " + jobId + " " + exceptionMessage);
			throw exception;
		}
		return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		// TODO Auto-generated method stub
		if (exceptionMessage != null) {
			constructErrorMessage();
		} else {
			importProcessContext.setStatus(ImportProcessContext.ImportStatus.IMPORTED.getValue());
			LOGGER.info("importProcessContext -- " + importProcessContext);
			ImportAPI.updateImportProcess(importProcessContext);
			LOGGER.info("IMPORT DATA JOB COMMAND COMPLETED -- " + jobId);
		}
		return false;
	}
	public void onError() throws Exception {
		constructErrorMessage();
	}
	public void constructErrorMessage() throws Exception {

		try {
			Exception mailExp = new Exception(exceptionMessage);
			if (stack != null) {
				mailExp.setStackTrace(stack);
			}
			if (sendExceptionMail) {
				CommonCommandUtil.emailException("Import Failed",
						"Import failed - orgid -- " + AccountUtil.getCurrentOrg().getId(), mailExp);
			}
			LOGGER.log(Level.SEVERE, exceptionMessage);

			
			if (importProcessContext != null) {
				JSONObject meta = importProcessContext.getImportJobMetaJson();
				if (meta != null && !meta.isEmpty()) {
					meta.put("errorMessage", exceptionMessage);
				} else {
					meta = new JSONObject();
					meta.put("errorMessage", exceptionMessage);
				}
				importProcessContext.setImportJobMeta(meta.toJSONString());
				importProcessContext.setStatus(ImportProcessContext.ImportStatus.FAILED.getValue());
				ImportAPI.updateImportProcess(importProcessContext);
				LOGGER.severe("Import failed: " + exceptionMessage);
			}
		} catch (Exception e1) {
			CommonCommandUtil.emailException("Import Exception Handling failed",
					"Import Exception Handling failed - orgid -- " + AccountUtil.getCurrentOrg().getId(), e1);
			LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
		}

	}
}
