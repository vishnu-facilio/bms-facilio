package com.facilio.bmsconsole.commands;

import com.facilio.modules.FieldUtil;
import com.facilio.bmsconsole.templates.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.context.WorkflowContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Map;

public class CreateActionTemplateForWorkflowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject templateContent = (JSONObject) context.get(FacilioConstants.Workflow.ACTION_TEMPLATE);
		
		if(templateContent != null && !templateContent.isEmpty()) {
			Template.Type type = Template.Type.getType(((Long)templateContent.get("type")).intValue());
			
			WorkflowContext workflowContext = null;
			if (templateContent.containsKey("workflow")) {
				Map<String, Object> workflow = (Map<String, Object>)templateContent.get("workflow");
				workflowContext = FieldUtil.getAsBeanFromMap(workflow, WorkflowContext.class);
			}
			
			switch(type) {
				case EMAIL:
					EMailTemplate emailTemplate = new EMailTemplate();
					emailTemplate.setSubject((String) templateContent.get("subject"));
					emailTemplate.setMessage((String) templateContent.get("message"));
					emailTemplate.setFrom((String) templateContent.get("from"));
					emailTemplate.setTo((String) templateContent.get("to"));
					emailTemplate.setName((String) templateContent.get("name"));
					emailTemplate.setWorkflow(workflowContext);
					context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
					break;
				case SMS:
					SMSTemplate smsTemplate  = new SMSTemplate();
					smsTemplate.setTo((String) templateContent.get("to"));
					smsTemplate.setMessage((String) templateContent.get("message"));
					smsTemplate.setName((String) templateContent.get("name"));
					smsTemplate.setWorkflow(workflowContext);
					context.put(FacilioConstants.Workflow.TEMPLATE, smsTemplate);
					break;
				case PUSH_NOTIFICATION:
					PushNotificationTemplate pushNotificationTemplate = new PushNotificationTemplate();
					pushNotificationTemplate.setTo((String) templateContent.get("to"));
					pushNotificationTemplate.setBody((String) templateContent.get("body"));
					pushNotificationTemplate.setName((String) templateContent.get("name"));
					pushNotificationTemplate.setTitle((String) templateContent.get("title"));
					pushNotificationTemplate.setUrl((String) templateContent.get("URL"));
					pushNotificationTemplate.setWorkflow(workflowContext);
					context.put(FacilioConstants.Workflow.TEMPLATE, pushNotificationTemplate);
					break;
				case WEB_NOTIFICATION:
					WebNotificationTemplate webNotificationTemplate = new WebNotificationTemplate();
					webNotificationTemplate.setTo((String) templateContent.get("to"));
					webNotificationTemplate.setMessage((String) templateContent.get("message"));
					webNotificationTemplate.setName((String) templateContent.get("name"));
					webNotificationTemplate.setUrl((String) templateContent.get("URL"));
					webNotificationTemplate.setTitle((String) templateContent.get("title"));
					webNotificationTemplate.setWorkflow(workflowContext);
					context.put(FacilioConstants.Workflow.TEMPLATE, webNotificationTemplate);
					break;
			}
			
		}
		else {
			throw new IllegalArgumentException("Invalid Template during addition of Template");
		}
		return false;
	}

}
