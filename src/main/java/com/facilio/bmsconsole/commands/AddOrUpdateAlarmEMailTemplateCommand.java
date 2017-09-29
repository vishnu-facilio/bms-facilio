package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.DefaultTemplates;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate.Type;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class AddOrUpdateAlarmEMailTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String email = (String) context.get(FacilioConstants.Workflow.NOTIFICATION_EMAIL);
		
		if(email != null && !email.isEmpty()) {
			EMailTemplate emailTemplate = (EMailTemplate) TemplateAPI.getTemplate(OrgInfo.getCurrentOrgInfo().getOrgid(), "New Alarm Raised", Type.EMAIL);
			
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
		
		TemplateAPI.updateEmailTemplate(OrgInfo.getCurrentOrgInfo().getOrgid(), updatedTemplate, emailTemplate.getId());
	}
	
	private EMailTemplate addTemplate(String email) throws Exception {
		EMailTemplate emailTemplate = new EMailTemplate();
		JSONObject alarmMailJson = DefaultTemplates.ALARM_CREATION_EMAIL.getOriginalTemplate();
		
		emailTemplate.setName("New Alarm Raised");
		emailTemplate.setType(UserTemplate.Type.EMAIL);
		emailTemplate.setFrom((String) alarmMailJson.get("sender"));
		emailTemplate.setTo(email);
		emailTemplate.setSubject((String) alarmMailJson.get("subject"));
		emailTemplate.setBody((String) alarmMailJson.get("message"));
		
		long id = TemplateAPI.addEmailTemplate(OrgInfo.getCurrentOrgInfo().getOrgid(), emailTemplate);
		emailTemplate.setId(id);
		
		return emailTemplate;
	}
	
}
