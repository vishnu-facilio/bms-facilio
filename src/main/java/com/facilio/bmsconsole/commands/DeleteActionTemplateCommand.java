package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteActionTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long templateId = (Long) context.get(FacilioConstants.Workflow.TEMPLATE_ID);
		if(templateId!=null && templateId != -1){
			TemplateAPI.deleteTemplate(templateId);
		}
		return false;
	}

}
