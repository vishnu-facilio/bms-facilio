package com.facilio.bmsconsole.jobs;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportStatus;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ImportDataJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		
		try {
			long jobId = jc.getJobId();
			
			
			ImportProcessContext importProcessContext = ImportAPI.getImportProcessContext(jobId);
			
			if(importProcessContext.getImportJobMetaJson() != null ) {
				Long assetId = (Long) importProcessContext.getImportJobMetaJson().get("assetId");
				importProcessContext.setAssetId(assetId);
			}
			
			ProcessXLS.processImport(importProcessContext);
			
			ImportAPI.updateImportProcess(importProcessContext,ImportStatus.IMPORTED);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
