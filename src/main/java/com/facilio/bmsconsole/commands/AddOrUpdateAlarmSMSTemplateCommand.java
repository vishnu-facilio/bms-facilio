package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.DefaultTemplates;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate.Type;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class AddOrUpdateAlarmSMSTemplateCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String sms = (String) context.get(FacilioConstants.Workflow.NOTIFICATION_SMS);
		
		if(sms != null && !sms.isEmpty()) {
			SMSTemplate smsTemplate = (SMSTemplate) TemplateAPI.getTemplate(OrgInfo.getCurrentOrgInfo().getOrgid(), "New Alarm Raised", Type.SMS);
			
			if(smsTemplate != null) {
				updateTemplate(smsTemplate, sms);
				context.put(FacilioConstants.ContextNames.RESULT, true);
			}
			else {
				smsTemplate = addTemplate(sms);
				context.put(FacilioConstants.Workflow.WORKFLOW, "Create Alarm - SMS");
				context.put(FacilioConstants.Workflow.TEMPLATE, smsTemplate);
			}
		}
		
		return false;
	}
	
	private void updateTemplate(SMSTemplate smsTemplate, String phone) throws Exception {
		String to = smsTemplate.getTo();
		SMSTemplate updatedTemplate = new SMSTemplate();
		updatedTemplate.setTo(to+", "+phone);
		
		TemplateAPI.updateSMSTemplate(OrgInfo.getCurrentOrgInfo().getOrgid(), updatedTemplate, smsTemplate.getId());
	}
	
	private SMSTemplate addTemplate(String phone) throws Exception {
		SMSTemplate smsTemplate = new SMSTemplate();
		JSONObject alarmSmsJson = DefaultTemplates.ALARM_CREATION_SMS.getOriginalTemplate();
		
		smsTemplate.setName("New Alarm Raised");
		smsTemplate.setType(UserTemplate.Type.SMS);
//		emailTemplate.setFrom((String) alarmMailJson.get("sender"));
		smsTemplate.setTo(phone);
		smsTemplate.setMsg((String) alarmSmsJson.get("message"));
		
		long id = TemplateAPI.addSMSTemplate(OrgInfo.getCurrentOrgInfo().getOrgid(), smsTemplate);
		smsTemplate.setId(id);
		
		return smsTemplate;
	}
}
