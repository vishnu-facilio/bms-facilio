package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportAssetMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.InstantJob;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportDataLogJob extends InstantJob{

	private static final Logger LOGGER = Logger.getLogger(ImportDataJob.class.getName());
	private org.apache.log4j.Logger log = LogManager.getLogger(ImportDataJob.class.getName());

	@Override
	public void execute(FacilioContext context) throws Exception{
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT); 
		try {
			LOGGER.severe("IMPORT DATA LOG JOB CALLED -- ");
			
			if(importProcessContext.getImportJobMeta() != null) {
				if(!importProcessContext.getImportJobMetaJson().isEmpty() ) {
					Long assetId = (Long) importProcessContext.getImportJobMetaJson().get("assetId");
					if(assetId != null) {
						importProcessContext.setAssetId(assetId);
					}
				}
			}	
			
			Chain c = FacilioChainFactory.parseImportData();
			c.execute(context);
			
			boolean hasDuplicates = (boolean) context.get(ImportAPI.ImportProcessConstants.HAS_DUPLICATE_ENTRIES);
			
			if(hasDuplicates) {
				importProcessContext.setStatus(ImportProcessContext.ImportStatus.RESOLVE_VALIDATION.getValue());
				LOGGER.severe("importProcessContext -- " +importProcessContext);
				ImportAPI.updateImportProcess(importProcessContext);
			}
			else {
				importProcessContext.setStatus(ImportProcessContext.ImportStatus.IN_PROGRESS.getValue());
				LOGGER.severe("importProcessContext -- " +importProcessContext);
				ImportAPI.updateImportProcess(importProcessContext);
				FacilioTimer.scheduleOneTimeJob(importProcessContext.getId(), "importData" , 5, "priority", 1);	
			}
			
			LOGGER.severe("IMPORT DATA LOG JOB COMPLETED -- ");
			
		} catch(Exception e) {
			String message;
			if(e instanceof ImportAssetMandatoryFieldsException) {
				ImportAssetMandatoryFieldsException importFieldException = (ImportAssetMandatoryFieldsException) e;
				message = importFieldException.getClientMessage();
			}
			else if(e instanceof ImportFieldValueMissingException) {
				ImportFieldValueMissingException importFieldException = (ImportFieldValueMissingException) e;
				message = importFieldException.getClientMessage();
			}
			else {
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
					importProcessContext.setStatus(ImportProcessContext.ImportStatus.PARSING_FAILED.getValue());
					ImportAPI.updateImportProcess(importProcessContext);
					LOGGER.severe("Import failed: " + message);
					}
			} catch(Exception a) {
				System.out.println(a);
			}
			CommonCommandUtil.emailException("Import Failed", "Import failed - orgid -- "+AccountUtil.getCurrentOrg().getId(), e);
			log.info("Exception occurred ", e);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
