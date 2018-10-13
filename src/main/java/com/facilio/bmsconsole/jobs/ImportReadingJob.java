package com.facilio.bmsconsole.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ImportReadingJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(ImportReadingJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		ImportProcessContext importProcessContext = null;
		Long jobId = jc.getJobId();

		LOGGER.severe("IMPORT READING JOB CALLED -- "+jobId);
		try {
			importProcessContext = ImportAPI.getImportProcessContext(jobId);
			FacilioContext context = new FacilioContext();
			context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
			Chain importReadingChain = FacilioChainFactory.getImportReadingChain();
			importReadingChain.execute(context);

			importProcessContext.setStatus(ImportProcessContext.ImportStatus.IMPORTED.getValue());
			ImportAPI.updateImportProcess(importProcessContext);
			LOGGER.severe("READING IMPORT COMPLETE -- "+jobId);
		} 
		catch (Exception e) {
			try {
				if (importProcessContext != null) {
					ImportAPI.updateImportProcess(importProcessContext, ImportProcessContext.ImportStatus.FAILED);
				}
			} catch (Exception a) {
				System.out.println(a);
			}
			CommonCommandUtil.emailException("Import Failed",
					"Import failed - orgid -- " + AccountUtil.getCurrentOrg().getId(), e);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
