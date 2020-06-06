/**
 * 
 */
package com.facilio.bmsconsole.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.PointsProcessContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ImportPointsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

/**
 * @author facilio
 *
 */
public class ImportPointsDataJob extends FacilioJob{

	private static final Logger LOGGER = Logger.getLogger(ImportPointsDataJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		PointsProcessContext importProcessContext = null;
		try {
			long jobId = jc.getJobId();
			
			LOGGER.severe("IMPORT DATA JOB CALLED -- "+jobId);
			
			importProcessContext = ImportPointsAPI.getImportProcessContext(jobId);
			
			if(importProcessContext.getImportJobMeta() != null) {
				if(!importProcessContext.getImportJobMetaJson().isEmpty() ) {
					Long assetId = (Long) importProcessContext.getImportJobMetaJson().get("assetId");
					if(assetId != null) {
						importProcessContext.setAssetId(assetId);
					}
				}
			}	
			if(!importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET)) {
				FacilioContext context = new FacilioContext();
				context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
				FacilioChain importChain = TransactionChainFactory.getImportPointsChain();
				importChain.execute(context);
			}
			else {
				if(importProcessContext.getImportSetting() == PointsProcessContext.ImportSetting.INSERT_SKIP.getValue() ||
						importProcessContext.getImportSetting() == PointsProcessContext.ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == PointsProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue()
						|| importProcessContext.getImportSetting() == PointsProcessContext.ImportSetting.BOTH.getValue() || importProcessContext.getImportSetting() == PointsProcessContext.ImportSetting.BOTH_NOT_NULL.getValue() ) {
					FacilioContext context = new FacilioContext();
					context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
					FacilioChain importChain = TransactionChainFactory.getImportChain();
					importChain.execute(context);
				}
				else {
					FacilioContext context = new FacilioContext();
					context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
					FacilioChain bulkAssetImportChain = TransactionChainFactory.getBulkAssetImportChain();
					bulkAssetImportChain.execute(context);
				}
			}
			
//			if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
//				ReadingsAPI.updateReadingDataMeta(true);
//			}
			
			importProcessContext.setStatus(PointsProcessContext.ImportStatus.IMPORTED.getValue());
			LOGGER.severe("importProcessContext -- " +importProcessContext);
			ImportPointsAPI.updateImportProcess(importProcessContext);
			LOGGER.severe("IMPORT DATA JOB COMPLETED -- " +jobId);
		} catch(Exception e) {
			
			try {
				
				CommonCommandUtil.emailException("Import Failed", "Import failed - orgid -- "+AccountUtil.getCurrentOrg().getId(), e);
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				
				String message;
				if(e instanceof ImportParseException) {
					ImportParseException importParseException = (ImportParseException) e;
					message = importParseException.getClientMessage();
				}
				else {
					message = e.getMessage();
				}
				if(importProcessContext != null) {
					JSONObject meta = importProcessContext.getImportJobMetaJson();
					if(meta != null && !meta.isEmpty()) {
						meta.put("errorMessage", message);
					}
					else {
						meta = new JSONObject();
						meta.put("errorMessage", message);
					}
					importProcessContext.setImportJobMeta(meta.toJSONString());
					importProcessContext.setStatus(PointsProcessContext.ImportStatus.FAILED.getValue());
					ImportPointsAPI.updateImportProcess(importProcessContext);
					LOGGER.severe("Import failed: " + message);
				}
			}
			catch(Exception e1) {
				CommonCommandUtil.emailException("Import Exception Handling failed", "Import Exception Handling failed - orgid -- "+AccountUtil.getCurrentOrg().getId(), e1);
				LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
			}
			
			try {
				FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
			}
			catch(Exception transactionException) {
				LOGGER.severe(transactionException.toString());
			}
			
		}
		
	}
	public void handleTimeOut() {
		// TODO Auto-generated method stub
		LOGGER.info("Time out called during import");
	 	super.handleTimeOut();
	}
	}


