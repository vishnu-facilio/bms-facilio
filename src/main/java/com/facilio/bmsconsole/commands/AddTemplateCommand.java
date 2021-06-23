package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class AddTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Template template = (Template) context.get(FacilioConstants.Workflow.TEMPLATE);
		
		if(template != null) {
			TemplateAPI.addTemplate(template);
		}
		return false;
	}
	
}
