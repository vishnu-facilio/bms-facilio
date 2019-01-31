package com.facilio.bmsconsole.instant.jobs;

import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportTimeColumnParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.job.InstantJob;
import com.facilio.wms.endpoints.PubSubManager;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.util.WmsApi;

public class ImportLogJob extends InstantJob{
	private static Logger LOGGER = Logger.getLogger(ImportLogJob.class.getName());
	private org.apache.log4j.Logger log = LogManager.getLogger(ImportLogJob.class.getName());
	@Override
	public void execute(FacilioContext context) throws Exception{
		PubSubManager pubsub = PubSubManager.getInstance();
		LOGGER.severe("----Beginning Import Log Job-----");
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		System.out.println(context);
		System.out.print(context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT));
		
		try {
			Chain dataParseChain = FacilioChainFactory.parseReadingDataForImport();
			dataParseChain.execute(context);
			
			importProcessContext.setStatus(ImportProcessContext.ImportStatus.BEGIN_VALIDATION.getValue());
			ImportAPI.updateImportProcess(importProcessContext);
			
			JSONObject hasDuplicates = new JSONObject();
			if((boolean)context.get(ImportAPI.ImportProcessConstants.HAS_DUPLICATE_ENTRIES)) {
				hasDuplicates.put("hasDuplicates",true);
			}
			else {
				hasDuplicates.put("hasDuplicates", false);
			}
			
			
			pubsub.publishImportStatusChange(importProcessContext.getOrgId(), importProcessContext.getId(), hasDuplicates);
			
			
			LOGGER.severe("------Import Log Job Finished------");
			
		
		}catch(Exception e) {
			String Message;
			String message;
			e.printStackTrace();
			LOGGER.severe(e.toString());
			if(e instanceof ImportParseException) {
				ImportParseException importParseException = (ImportParseException) e;
				message = importParseException.getClientMessage();
			}
			else if(e instanceof ImportTimeColumnParseException) {
				ImportTimeColumnParseException timeException = (ImportTimeColumnParseException) e;
				message= timeException.getClientMessage();
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
					LOGGER.severe("Parsing failed:" + message);
					}
			} catch(Exception a) {
				System.out.println(a);
			}
			
			
			JSONObject parseError = new JSONObject();
			parseError.put(ImportAPI.ImportProcessConstants.PARSING_ERROR, true);
			parseError.put(ImportAPI.ImportProcessConstants.PARSING_ERROR_MESSAGE, message);
			
			pubsub.publishImportStatusChange(importProcessContext.getOrgId(), importProcessContext.getId(), parseError);
			
			
			CommonCommandUtil.emailException("Import Failed", "Import failed - orgid -- "+AccountUtil.getCurrentOrg().getId(), e);
			log.info("Exception occurred ", e);
			LOGGER.severe(e.getMessage());
		}
		
	}
}
