package com.facilio.bmsconsole.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;

public class AddAttachmentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<File> attachmentList = (List<File>) context.get(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST);
		List<String> attachmentName = (List<String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME);
		List<String> attachmentContentType = (List<String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE);

		FileStore fs = FacilioFactory.getFileStore();

		List<Long> attachmentIds = new ArrayList<Long>();

		if (attachmentList != null && !attachmentList.isEmpty()) {
			for (int i=0; i< attachmentList.size(); i++) {
				File file = attachmentList.get(i);
				String fileName = attachmentName.get(i);
				String contentType = attachmentContentType.get(i);
//				long fileId = fs.addFile(fileName, file, contentType);
					int[] resize = {80, 120, 360};
					long fileId = fs.addFile(fileName, file, contentType, resize);
				attachmentIds.add(fileId);
			}

			context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, attachmentIds);
		}
		return false;
	}

}
