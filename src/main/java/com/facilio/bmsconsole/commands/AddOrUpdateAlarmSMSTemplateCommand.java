package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AddOrUpdateAlarmSMSTemplateCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String sms = (String) context.get(FacilioConstants.Workflow.NOTIFICATION_SMS);
		
		if(sms != null && !sms.isEmpty()) {
			SMSTemplate smsTemplate = (SMSTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), "New Alarm Raised", Type.SMS);
			
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
		
		TemplateAPI.updateSMSTemplate(AccountUtil.getCurrentOrg().getOrgId(), updatedTemplate, smsTemplate.getId());
	}
	
	private SMSTemplate addTemplate(String phone) throws Exception {
		SMSTemplate smsTemplate = new SMSTemplate();
		JSONObject alarmSmsJson = TemplateAPI.getDefaultTemplate(6).getOriginalTemplate(); //Default template id of ALARM_CREATION_SMS is 6
		
		smsTemplate.setName("New Alarm Raised");
		smsTemplate.setType(Template.Type.SMS);
//		emailTemplate.setFrom((String) alarmMailJson.get("sender"));
		smsTemplate.setTo(phone);
		smsTemplate.setMessage((String) alarmSmsJson.get("message"));
		
		long id = TemplateAPI.addSMSTemplate(AccountUtil.getCurrentOrg().getOrgId(), smsTemplate);
		smsTemplate.setId(id);
		
		return smsTemplate;
	}
}
