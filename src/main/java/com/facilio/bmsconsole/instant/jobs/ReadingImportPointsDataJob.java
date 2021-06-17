/**
 * 
 */
package com.facilio.bmsconsole.instant.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.PointsProcessContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ImportPointsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.job.InstantJob;

/**
 * @author facilio
 *
 */
public class ReadingImportPointsDataJob extends InstantJob{

	private static final Logger LOGGER = Logger.getLogger(ReadingImportPointsDataJob.class.getName());
	private org.apache.log4j.Logger log = LogManager.getLogger(ReadingImportPointsDataJob.class.getName());

	@Override
	public void execute(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		PointsProcessContext importProcessContext = (PointsProcessContext) context.get(ImportPointsAPI.ImportPointsConstants.POINTS_PROCESS_CONTEXT); 
		try {
			LOGGER.severe("IMPORT POINTS DATA LOG JOB CALLED -- ");
			
			if(importProcessContext.getImportJobMeta() != null) {
				if(!importProcessContext.getImportJobMetaJson().isEmpty() ) {
					long controllerId = (Long) importProcessContext.getImportJobMetaJson().get("controllerId");
					if(controllerId != -1) {
						importProcessContext.setControllerId(controllerId);
					}
				}
			}	
			
			FacilioChain c = TransactionChainFactory.parseImportPointsData();
			c.execute(context);
			
			boolean hasDuplicates = (boolean) context.get(ImportPointsAPI.ImportPointsConstants.HAS_DUPLICATE_ENTRIES);
			
			if(hasDuplicates) {
				importProcessContext.setStatus(PointsProcessContext.ImportStatus.RESOLVE_VALIDATION.getValue());
				ImportPointsAPI.updateImportProcess(importProcessContext);
			}
			else {
				importProcessContext.setStatus(PointsProcessContext.ImportStatus.IN_PROGRESS.getValue());
				ImportPointsAPI.updateImportProcess(importProcessContext);
				FacilioTimer.scheduleOneTimeJobWithDelay(importProcessContext.getId(), "importData" , 10, "priority");
			}
			
			LOGGER.severe("IMPORT POINTS DATA LOG JOB COMPLETED -- ");
			
		} catch(Exception e) {
			String message;
			if(e instanceof ImportMandatoryFieldsException) {
				ImportMandatoryFieldsException importFieldException = (ImportMandatoryFieldsException) e;
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
					importProcessContext.setStatus(PointsProcessContext.ImportStatus.PARSING_FAILED.getValue());
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
