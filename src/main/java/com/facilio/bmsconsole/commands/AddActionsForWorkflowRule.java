package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.PushNotificationTemplate;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.context.WorkflowContext;

public class AddActionsForWorkflowRule implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<ActionContext> actions = (List<ActionContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_ACTION);
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		
		if (actions != null && !actions.isEmpty()) {
			for(ActionContext action:actions) {
				System.out.println(action.getTemplateJson());
				switch (action.getActionTypeEnum()) {
					case EMAIL_NOTIFICATION:
					case BULK_EMAIL_NOTIFICATION:
						setEmailTemplate(action);
						break;
					case SMS_NOTIFICATION:
					case BULK_SMS_NOTIFICATION:
						setSMSTemplate(action);
						break;
					case PUSH_NOTIFICATION:
						setMobileTemplate(action);
						break;
					case ADD_ALARM:
						setAlarmTemplate(action, rule);
						break;
					case CREATE_WO_FROM_ALARM:
						setWorkorderTemplate(action, rule);
						break;
					default:
						break;
				}
				action.setTemplateId(TemplateAPI.addTemplate(action.getTemplate()));
			}
			ActionAPI.addActions(actions);
			
			if(rule != null) {
				ActionAPI.addWorkflowRuleActionRel(rule.getId(), actions);
			}
			rule.setActions(actions);
		}
		return false;
	}
	
	private void setEmailTemplate(ActionContext action) {
		EMailTemplate emailTemplate = new EMailTemplate();
		emailTemplate.setFrom("noreply@${org.domain}.facilio.com");
		String toAdresses = action.getTemplateJson().get("to").toString();
//		toAdresses = toAdresses.substring(1, toAdresses.length()-1);
		
		if(toAdresses.contains(",")) {
			action.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
		}
		
		emailTemplate.setTo(toAdresses);
		emailTemplate.setName((String) action.getTemplateJson().get("name"));
		emailTemplate.setSubject((String) action.getTemplateJson().get("subject"));
		emailTemplate.setMessage((String) action.getTemplateJson().get("message"));
		emailTemplate.setType(Type.EMAIL);
		action.setTemplate(emailTemplate);
		
		checkAndSetWorkflow(action.getTemplateJson(), emailTemplate);
	}
	
	private void setSMSTemplate(ActionContext action) {
		SMSTemplate smsTemplate = new SMSTemplate();
		String toPhones = action.getTemplateJson().get("to").toString();
//		toPhones = toPhones.substring(1, toPhones.length()-1);
		
		if(toPhones.contains(",")) {
			action.setActionType(ActionType.BULK_SMS_NOTIFICATION);
		}
		smsTemplate.setName((String) action.getTemplateJson().get("name"));
		smsTemplate.setTo(toPhones);
		smsTemplate.setMessage((String) action.getTemplateJson().get("body"));
		smsTemplate.setType(Type.SMS);
		action.setTemplate(smsTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), smsTemplate);
	}
	
	private void setMobileTemplate(ActionContext action) {
		PushNotificationTemplate pushNotificationTemplate = new PushNotificationTemplate();
		pushNotificationTemplate.setTo((String) action.getTemplateJson().get("id"));
		pushNotificationTemplate.setBody((String) action.getTemplateJson().get("body"));	// TODO needs to save only message...now saving entire json structure
		pushNotificationTemplate.setName((String) action.getTemplateJson().get("name"));
		pushNotificationTemplate.setType(Type.PUSH_NOTIFICATION);
		
		action.setTemplate(pushNotificationTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), pushNotificationTemplate);
	}
	
	private void checkAndSetWorkflow(JSONObject templateJson, Template template) {
		if (templateJson.containsKey("workflow")) {
			Map<String, Object> workflow = (Map<String, Object>) templateJson.get("workflow");
			WorkflowContext workflowContext = FieldUtil.getAsBeanFromMap(workflow, WorkflowContext.class);
			template.setWorkflow(workflowContext);
		}
	}
	
	private void setAlarmTemplate(ActionContext action, WorkflowRuleContext rule) throws Exception {
		List<Map> alarmFieldMatcher = (ArrayList) action.getTemplateJson().get("fieldMatcher");
		JSONObject content = new JSONObject();
		for(Map alarmField:alarmFieldMatcher) {
			content.put(alarmField.get("field").toString(), alarmField.get("value").toString());
		}
		if (rule instanceof ReadingRuleContext) {
			ReadingRuleContext readingRule = (ReadingRuleContext) rule;
			
			AlarmType alarmType = null;
			if (readingRule.getAssetCategoryId() != -1) {
				alarmType = AlarmAPI.getAlarmTypeFromAssetCategory(readingRule.getAssetCategoryId());
			}
			else {
				alarmType = AlarmAPI.getAlarmTypeFromResource(readingRule.getResourceId()); 
			}
			if (alarmType != null) {
				content.put("alarmType", alarmType.getIntVal());
			}
		}
		JSONTemplate alarmTemplate = new JSONTemplate();
		alarmTemplate.setName(rule.getName()+"_alarm_template");
		alarmTemplate.setContent(content.toJSONString());
		alarmTemplate.setType(Type.ALARM);
		action.setTemplate(alarmTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), alarmTemplate);
	}
	
	private void setWorkorderTemplate(ActionContext action, WorkflowRuleContext rule) throws Exception {
		JSONObject woJson = action.getTemplateJson();
		WorkorderTemplate woTemplate = new WorkorderTemplate();
		woTemplate.setWorkorder(FieldUtil.getAsBeanFromJson(woJson, WorkOrderContext.class));
		if (woTemplate.getName() == null || woTemplate.getName().isEmpty()) {
			woTemplate.setName(rule.getName()+"_WO_Template");
		}
		action.setTemplate(woTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), woTemplate);
	}
	
}
