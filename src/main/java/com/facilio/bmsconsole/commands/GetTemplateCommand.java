package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class GetTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (id != -1) {
			Template template = TemplateAPI.getTemplate(id);
			if (template instanceof WorkorderTemplate) {
				WorkorderTemplate woTemplate = (WorkorderTemplate) template;
				if(woTemplate.getWorkorder().getResource() != null) {
					woTemplate.setResource(ResourceAPI.getResource(woTemplate.getWorkorder().getResource().getId()));
				}
			}
			context.put(FacilioConstants.ContextNames.TEMPLATE, template);
		}
		return false;
	}

}
