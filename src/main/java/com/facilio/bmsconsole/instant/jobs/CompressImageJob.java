package com.facilio.bmsconsole.instant.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fs.FileInfo;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.tasker.job.InstantJob;

public class CompressImageJob extends InstantJob {
	private static final Logger LOGGER = LogManager.getLogger(CompressImageJob.class.getName());

	@Override
	public void execute(FacilioContext context) throws Exception {
		String namespace = (String) context.get(ContextNames.FILE_NAME_SPACE);
		long fileId = (long) context.get(ContextNames.FILE_ID);
		
		FileStore fs = FacilioFactory.getFileStore();
		FileInfo fileInfo = fs.getFileInfo(namespace, fileId, true);
		
		fs.addCompressedFile(namespace, fileId,fileInfo);
	}

}
