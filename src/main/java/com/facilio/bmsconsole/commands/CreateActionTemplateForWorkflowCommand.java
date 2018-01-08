package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.constants.FacilioConstants;

public class CreateActionTemplateForWorkflowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject templateContent = (JSONObject) context.get(FacilioConstants.Workflow.ACTION_TEMPLATE);
		
		if(templateContent != null && !templateContent.isEmpty()) {
			UserTemplate.Type type = UserTemplate.Type.getType(((Long)templateContent.get("type")).intValue());
			
			switch(type) {
				case EMAIL:
					EMailTemplate emailTemplate = new EMailTemplate();
					emailTemplate.setSubject((String) templateContent.get("subject"));
					emailTemplate.setMessage((String) templateContent.get("message"));
					emailTemplate.setFrom((String) templateContent.get("from"));
					emailTemplate.setTo((String) templateContent.get("to"));
					emailTemplate.setName((String) templateContent.get("name"));
					context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
					break;
				case SMS:
					SMSTemplate smsTemplate  = new SMSTemplate();
					smsTemplate.setTo((String) templateContent.get("to"));
					smsTemplate.setMessage((String) templateContent.get("message"));
					smsTemplate.setName((String) templateContent.get("name"));
					context.put(FacilioConstants.Workflow.TEMPLATE, smsTemplate);
			}
			
		}
		else {
			throw new IllegalArgumentException("Invalid Template during addition of Template");
		}
		return false;
	}

}
