package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.AssignmentTemplate;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.bmsconsole.workflow.PushNotificationTemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.bmsconsole.workflow.WebNotificationTemplate;
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
			else if(template instanceof PushNotificationTemplate) {
				long id = TemplateAPI.addPushNotificationTemplate(AccountUtil.getCurrentOrg().getOrgId(), (PushNotificationTemplate) template);
				template.setId(id);
			}
			else if(template instanceof WebNotificationTemplate) {
				long id = TemplateAPI.addWebNotificationTemplate(AccountUtil.getCurrentOrg().getOrgId(), (WebNotificationTemplate) template);
				template.setId(id);
			}
			else if(template instanceof JSONTemplate) {
				long id = TemplateAPI.addJsonTemplate(AccountUtil.getCurrentOrg().getOrgId(), (JSONTemplate) template);
				template.setId(id);
			}
			else if (template instanceof AssignmentTemplate) {
				long id = TemplateAPI.addAssignmentTemplate(AccountUtil.getCurrentOrg().getOrgId(), (AssignmentTemplate) template);
				template.setId(id);
			}
		}
		return false;
	}
	
}
