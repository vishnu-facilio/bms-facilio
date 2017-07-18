package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class DeleteAttachmentCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> attachmentIdList = (List<Long>) context.get(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST);
		
		if (attachmentIdList != null && !attachmentIdList.isEmpty()) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			fs.deleteFiles(attachmentIdList);
		}
		
		return false;
	}

}
