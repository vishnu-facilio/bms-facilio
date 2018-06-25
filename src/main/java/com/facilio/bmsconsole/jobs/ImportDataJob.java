package com.facilio.bmsconsole.jobs;

import java.util.logging.Logger;

import org.apache.log4j.LogManager;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportStatus;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
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
		
		LOGGER.severe("IMPORT DATA JOB CALLED");
		try {
			long jobId = jc.getJobId();
			
			
			ImportProcessContext importProcessContext = ImportAPI.getImportProcessContext(jobId);
			
			if(importProcessContext.getImportJobMetaJson() != null ) {
				Long assetId = (Long) importProcessContext.getImportJobMetaJson().get("assetId");
				importProcessContext.setAssetId(assetId);
			}
			
			ProcessXLS.processImport(importProcessContext);
			
			if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ENERGY_METER)) {
				ReadingsAPI.updateReadingDataMeta();
			}
			
			ImportAPI.updateImportProcess(importProcessContext,ImportStatus.IMPORTED);
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
	}

}
