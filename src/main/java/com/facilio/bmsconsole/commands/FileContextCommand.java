package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.FileContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.logging.Logger;

public class FileContextCommand implements Command {
	private static Logger LOGGER = Logger.getLogger(AttachmentContextCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
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
