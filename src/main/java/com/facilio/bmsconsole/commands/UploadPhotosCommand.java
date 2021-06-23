package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;

public class UploadPhotosCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long parentId = -1;
		if(context.get(FacilioConstants.ContextNames.PARENT_ID) != null) {
			parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		}
		List<File> attachmentList = (List<File>) context.get(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST);
		List<String> attachmentName = (List<String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME);
		List<String> attachmentContentType = (List<String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE);

		FileStore fs = FacilioFactory.getFileStore();

		List<PhotosContext> photos = new ArrayList<>();

		if (attachmentList != null && !attachmentList.isEmpty()) {
			for (int i=0; i< attachmentList.size(); i++) {
				File file = attachmentList.get(i);
				String fileName = attachmentName.get(i);
				String contentType = attachmentContentType.get(i);

				int[] resize = {80, 120};
				long fileId = fs.addFile(fileName, file, contentType, resize);
				if	(!(fileId > 0) ){
					throw new Exception("File not added");
				}
				PhotosContext photo = new PhotosContext();
				photo.setParentId(parentId);
				photo.setPhotoId(fileId);
				photos.add(photo);
			}
			
			context.put(FacilioConstants.ContextNames.PHOTOS, photos);
		}
		return false;
	}

}
