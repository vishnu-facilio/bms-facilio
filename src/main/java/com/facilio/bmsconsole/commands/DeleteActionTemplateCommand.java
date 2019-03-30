package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteActionTemplateCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long templateId = (Long) context.get(FacilioConstants.Workflow.TEMPLATE_ID);
		if(templateId!=null && templateId != -1){
			TemplateAPI.deleteTemplate(templateId);
		}
		return false;
	}

}
