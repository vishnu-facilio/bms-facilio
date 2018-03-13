package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class GetTemplatesOfTypeCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Type templateType = (Type) context.get(FacilioConstants.ContextNames.TEMPLATE_TYPE);
		if (templateType != null) {
			context.put(FacilioConstants.ContextNames.TEMPLATE_LIST, TemplateAPI.getTemplatesOfType(templateType));
		}
		return false;
	}

}
