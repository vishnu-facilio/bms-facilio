package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AddOrUpdateAlarmEMailTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String email = (String) context.get(FacilioConstants.Workflow.NOTIFICATION_EMAIL);
		
		if(email != null && !email.isEmpty()) {
			EMailTemplate emailTemplate = (EMailTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), "New Alarm Raised", Type.EMAIL);
			
			if(emailTemplate != null) {
				updateTemplate(emailTemplate, email);
				context.put(FacilioConstants.ContextNames.RESULT, true);
			}
			else {
				emailTemplate = addTemplate(email);
				context.put(FacilioConstants.Workflow.WORKFLOW, "Create Alarm - EMail");
				context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
			}
		}
		
		return false;
	}
	
	private void updateTemplate(EMailTemplate emailTemplate, String email) throws Exception {
		String to = emailTemplate.getTo();
		EMailTemplate updatedTemplate = new EMailTemplate();
		updatedTemplate.setTo(to+", "+email);
		
		TemplateAPI.updateEmailTemplate(AccountUtil.getCurrentOrg().getOrgId(), updatedTemplate, emailTemplate.getId());
	}
	
	private EMailTemplate addTemplate(String email) throws Exception {
		EMailTemplate emailTemplate = new EMailTemplate();
		JSONObject alarmMailJson = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION,5).getOriginalTemplate(); //Default template id of ALARM_CREATION_EMAIL is 5
		
		emailTemplate.setName("New Alarm Raised");
		emailTemplate.setType(Template.Type.EMAIL);
		emailTemplate.setFrom((String) alarmMailJson.get("sender"));
		emailTemplate.setTo(email);
		emailTemplate.setSubject((String) alarmMailJson.get("subject"));
		emailTemplate.setMessage((String) alarmMailJson.get("message"));
		
		long id = TemplateAPI.addEmailTemplate(AccountUtil.getCurrentOrg().getOrgId(), emailTemplate);
		emailTemplate.setId(id);
		
		return emailTemplate;
	}
	
}
