package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllWOTemplatesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<JSONTemplate> woTemplates = TemplateAPI.getAllWOTemplates();
		context.put(FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE_LIST, woTemplates);
		return false;
	}
	
}
