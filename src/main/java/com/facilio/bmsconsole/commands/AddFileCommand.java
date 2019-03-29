package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.io.File;

public class AddFileCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		File newFile = (File) context.get(FacilioConstants.ContextNames.FILE);
		String newFileName = (String) context.get(FacilioConstants.ContextNames.FILE_NAME);
		String newFileContentType = (String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);

		FileStore fs = FileStoreFactory.getInstance().getFileStore();


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
