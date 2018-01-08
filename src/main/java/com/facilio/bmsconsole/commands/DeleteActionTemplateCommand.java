package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteActionTemplateCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long templateId = (Long) context.get(FacilioConstants.Workflow.TEMPLATE_ID);
		if(templateId!=null){
			TemplateAPI.deleteTemplate(templateId);
		}
		return false;
	}

}
