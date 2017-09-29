package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class AddTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		UserTemplate template = (UserTemplate) context.get(FacilioConstants.ContextNames.TEMPLATE);
		
		if(template != null) {
			template.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			if(template instanceof EMailTemplate) {
				long id = TemplateAPI.addEmailTemplate((EMailTemplate) template);
				template.setId(id);
			}
			else if(template instanceof SMSTemplate) {
				long id = TemplateAPI.addSMSTemplate((SMSTemplate) template);
				template.setId(id);
			}
		}
		return false;
	}
	
}
