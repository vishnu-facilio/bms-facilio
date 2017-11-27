package com.facilio.bmsconsole.workflow;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ActionContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	public int getActionTypeVal() {
		if(actionType != null) {
			return actionType.getVal();
		}
		return -1;
	}
	public void setActionTypeVal(int actionTypeVal) {
		this.actionType = ActionType.getActionType(actionTypeVal);
		
		if(this.actionType == null) {
			throw new IllegalArgumentException("Invalid action type val. No ActionType exists for value : "+actionTypeVal);
		}
	}
	
	private ActionType actionType;
	public ActionType getActionType() {
		return actionType;
	}
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	public void setActionType(int actionTypeVal) {
		this.actionType = ActionType.getActionType(actionTypeVal);
	}
	
	private int defaultTemplateId = -1;
	public int getDefaultTemplateId() {
		return defaultTemplateId;
	}
	public void setDefaultTemplateId(int defaultTemplateId) {
		this.template = DefaultTemplates.getDefaultTemplate(defaultTemplateId);
		if(template == null) {
			throw new IllegalArgumentException("No DefaultTemplate exists with value : "+defaultTemplateId);
		}
		this.defaultTemplateId = defaultTemplateId;
	}
	
	private long templateId = -1;
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	
	public ActionTemplate template;
	public ActionTemplate getTemplate() {
		return template;
	}
	public void setTemplate(ActionTemplate template) {
		this.template = template;
	}
	
//	public void setTemplate(JSONObject template) {
//		JSONArray recipients = (JSONArray) template.get("to");
//		String to = "";
//		for (int i=0; i< recipients.size(); i++) {
//			if (i != 0) {
//				to += ",";
//			}
//			to += (String) recipients.get(i);
//		}
//		
//		if (this.getActionType().getVal() == ActionType.EMAIL_NOTIFICATION.getVal() || this.getActionType().getVal() == ActionType.BULK_EMAIL_NOTIFICATION.getVal()) {			
//			
//			
//			EMailTemplate emailTemplate = new EMailTemplate();
//			emailTemplate.setTo(to);
//			emailTemplate.setSubject((String) template.get("subject"));
//			emailTemplate.setBody((String) template.get("body"));
//			
//			this.template = emailTemplate;
//		}
//		else if (this.getActionType().getVal() == ActionType.SMS_NOTIFICATION.getVal() || this.getActionType().getVal() == ActionType.BULK_SMS_NOTIFICATION.getVal()) {
//			
//			SMSTemplate smsTemplate = new SMSTemplate();
//			smsTemplate.setTo(to);
//			smsTemplate.setMsg((String) template.get("body"));
//			
//			this.template = smsTemplate;
//		}
//	}
}
