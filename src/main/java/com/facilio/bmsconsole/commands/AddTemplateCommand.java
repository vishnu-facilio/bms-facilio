package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class AddTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Template template = (Template) context.get(FacilioConstants.Workflow.TEMPLATE);
		
		if(template != null) {
			TemplateAPI.addTemplate(template);
		}
		return false;
	}
	
}
