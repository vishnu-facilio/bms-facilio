package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TemplateFileContext;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class FetchTemplateAttachmentsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (id != -1) {
			List<TemplateFileContext> templateFiles = TemplateAPI.fetchTemplateFiles(id);
		}
		else {
			throw new IllegalArgumentException("ID cannot be null");
		}
		
		return false;
	}

}
