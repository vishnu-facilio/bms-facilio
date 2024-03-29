package com.facilio.bmsconsole.commands;

import java.io.File;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;

public class AddFileCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		File newFile = (File) context.get(FacilioConstants.ContextNames.FILE);
		String newFileName = (String) context.get(FacilioConstants.ContextNames.FILE_NAME);
		String newFileContentType = (String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);

		FileStore fs = FacilioFactory.getFileStore();


		if (newFile != null) {
				File file = newFile;
				String fileName = newFileName;
				String contentType = newFileContentType;

				long fileId = fs.addFile(fileName, file, contentType);
			

			context.put(FacilioConstants.ContextNames.FILE_ID, fileId);
		}
		return false;
	}

}
