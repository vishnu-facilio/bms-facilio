package com.facilio.bmsconsole.commands;

import java.io.File;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class UploadBimFileCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FileStore fs = FacilioFactory.getFileStore();
		
		String fileUploadFileName = (String) context.get(FacilioConstants.ContextNames.FILE_NAME);
		File fileUpload = (File) context.get(FacilioConstants.ContextNames.FILE);
		String fileUploadContentType = (String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);
		
		long fileId = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
		
		context.put(FacilioConstants.ContextNames.FILE_ID, fileId);
		return false;
	}

}
