package com.facilio.bmsconsole.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.PostTransactionCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;

public class ImportReadingCommand extends FacilioCommand implements PostTransactionCommand {
	private static final Logger LOGGER = Logger.getLogger(ImportReadingCommand.class.getName());
	private ImportProcessContext importProcessContext = null;
	private Long jobId;
	private String exceptionMessage = null;
	private StackTraceElement[] stack = null;
	private boolean sendExceptionMail = false;

	@Override
	public boolean executeCommand(Context commandContext) throws Exception {
		// TODO Auto-generated method stub

		importProcessContext = null;
		jobId = (Long) commandContext.get(ImportAPI.ImportProcessConstants.JOB_ID);

		LOGGER.info("IMPORT READING JOB CALLED -- " + jobId);
		try {
			importProcessContext = ImportAPI.getImportProcessContext(jobId);

			if (importProcessContext.getStatus().intValue() > ImportProcessContext.ImportStatus.IN_PROGRESS.getValue()) {
				LOGGER.severe("Old job is picked this should not Happen");
				throw new IllegalArgumentException("Exiting Reading Import - Running old job this should not happen");
			}
			FacilioChain importReadingChain = TransactionChainFactory.getImportReadingChain();
			importReadingChain.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT,
					importProcessContext);
			importReadingChain.execute();

			
		} catch (Exception e) {
			if (e instanceof ImportParseException) {
				ImportParseException importParseException = (ImportParseException) e;
				exceptionMessage = importParseException.getClientMessage();
			} else {
				sendExceptionMail = true;
				exceptionMessage = e.getMessage();
			}
			stack = e.getStackTrace();
			
			throw e;
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
			ImportAPI.updateImportProcess(importProcessContext);
			LOGGER.info("READING IMPORT COMPLETE -- " + jobId);
		}
		return false;
	}
	
	public void onError() {
		constructErrorMessage();
	}
	
	public void constructErrorMessage() {
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
				LOGGER.severe("Import failed");
			}
		} catch (Exception a) {
			CommonCommandUtil.emailException("Import Exception Handling Failed",
					"Import Exception Handling failed - orgid -- " + AccountUtil.getCurrentOrg().getId(), a);
			LOGGER.log(Level.SEVERE, a.getMessage(), a);
		}
		
		
	}

}
