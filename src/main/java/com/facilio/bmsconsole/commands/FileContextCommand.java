package com.facilio.bmsconsole.commands;

import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FileContext;
import com.facilio.constants.FacilioConstants;

public class FileContextCommand extends FacilioCommand {
	private static Logger LOGGER = Logger.getLogger(AttachmentContextCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub			
			Long fileId = (Long) context.get(FacilioConstants.ContextNames.FILE_ID);
			if(fileId != null) {
					FileContext file = new FileContext();
					file.setFileId(fileId);
										
				context.put(FacilioConstants.ContextNames.FILE_CONTEXT_LIST, file);
			}
		return false;
	}

}
