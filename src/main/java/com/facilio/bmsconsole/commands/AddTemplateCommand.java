package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.AlarmTemplate;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.constants.FacilioConstants;

public class AddTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		UserTemplate template = (UserTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
		
		if(template != null) {
			if(template instanceof EMailTemplate) {
				long id = TemplateAPI.addEmailTemplate(AccountUtil.getCurrentOrg().getOrgId(), (EMailTemplate) template);
				template.setId(id);
			}
			else if(template instanceof SMSTemplate) {
				long id = TemplateAPI.addSMSTemplate(AccountUtil.getCurrentOrg().getOrgId(), (SMSTemplate) template);
				template.setId(id);
			}
			else if(template instanceof AlarmTemplate) {
				long id = TemplateAPI.addAlarmTemplate(AccountUtil.getCurrentOrg().getOrgId(), (AlarmTemplate) template);
				template.setId(id);
			}
		}
		return false;
	}
	
}
