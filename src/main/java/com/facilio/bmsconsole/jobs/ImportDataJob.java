package com.facilio.bmsconsole.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportStatus;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ImportDataJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(ImportDataJob.class.getName());
	private org.apache.log4j.Logger log = LogManager.getLogger(ImportDataJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		ImportProcessContext importProcessContext = null;
		
		LOGGER.severe("IMPORT DATA JOB CALLED");
		try {
			long jobId = jc.getJobId();
			
			importProcessContext = ImportAPI.getImportProcessContext(jobId);
			
			if(!importProcessContext.getImportJobMetaJson().isEmpty() ) {
				Long assetId = (Long) importProcessContext.getImportJobMetaJson().get("assetId");
				if(assetId != null) {
					importProcessContext.setAssetId(assetId);
				}
			}
			
			if(!importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET)) {
				FacilioContext context = new FacilioContext();
				context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
				Chain importChain = FacilioChainFactory.getImportChain();
				importChain.execute(context);
			}
			else {
				FacilioContext context = new FacilioContext();
				context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
				Chain bulkAssetImportChain = FacilioChainFactory.getBulkAssertImportChain();
				bulkAssetImportChain.execute(context);
			}
			
			
			if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
				ReadingsAPI.updateReadingDataMeta();
			}
			
			importProcessContext.setStatus(ImportProcessContext.ImportStatus.IMPORTED.getValue());
			ImportAPI.updateImportProcess(importProcessContext);
			LOGGER.severe("IMPORT DATA JOB COMPLETED");
		}
		catch(Exception e) {
			try {
				if(importProcessContext != null) {
					ImportAPI.updateImportProcess(importProcessContext,ImportProcessContext.ImportStatus.FAILED);
					}
			}
			catch(Exception a) {
				System.out.println(a);
			}
			CommonCommandUtil.emailException("Import Failed", "Import failed - orgid -- "+AccountUtil.getCurrentOrg().getId(), e);
			log.info("Exception occurred ", e);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}