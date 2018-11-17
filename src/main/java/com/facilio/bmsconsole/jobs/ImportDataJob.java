package com.facilio.bmsconsole.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ImportAPI.ImportProcessConstants;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ImportDataJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(ImportDataJob.class.getName());
	private org.apache.log4j.Logger log = LogManager.getLogger(ImportDataJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		ImportProcessContext importProcessContext = null;
		try {
			long jobId = jc.getJobId();
			
			LOGGER.severe("IMPORT DATA JOB CALLED -- "+jobId);
			
			importProcessContext = ImportAPI.getImportProcessContext(jobId);
			
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
				Chain importChain = TransactionChainFactory.getImportChain();
				importChain.execute(context);
			}
			else {
				if(importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue()
						|| importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.BOTH.getValue() || importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.BOTH_NOT_NULL.getValue() ) {
					FacilioContext context = new FacilioContext();
					context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
					Chain importChain = TransactionChainFactory.getImportChain();
					importChain.execute(context);
				}
				else {
					FacilioContext context = new FacilioContext();
					context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
					Chain bulkAssetImportChain = TransactionChainFactory.getBulkAssertImportChain();
					bulkAssetImportChain.execute(context);
				}
			}
			
//			if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
//				ReadingsAPI.updateReadingDataMeta(true);
//			}
			
			importProcessContext.setStatus(ImportProcessContext.ImportStatus.IMPORTED.getValue());
			ImportAPI.updateImportProcess(importProcessContext);
			LOGGER.severe("IMPORT DATA JOB COMPLETED -- " +jobId);
		} catch(Exception e) {
			try {
				if(importProcessContext != null) {
					JSONObject meta = importProcessContext.getImportJobMetaJson();
					if(meta != null && !meta.isEmpty()) {
						meta.put("errorMessage", e.getMessage());
					}
					else {
						meta = new JSONObject();
						meta.put("errorMessage", e.getMessage());
					}
					importProcessContext.setImportJobMeta(meta.toJSONString());
					importProcessContext.setStatus(ImportProcessContext.ImportStatus.FAILED.getValue());
					ImportAPI.updateImportProcess(importProcessContext);
					LOGGER.severe("Import failed");
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