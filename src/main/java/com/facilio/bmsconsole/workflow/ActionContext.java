package com.facilio.bmsconsole.workflow;

import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;

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
		this.template = TemplateAPI.getDefaultTemplate(defaultTemplateId);
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
	
	public Template template;
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	JSONObject templateJson;
	public JSONObject getTemplateJson() {
		return templateJson;
	}
	public void setTemplateJson(JSONObject templateJson) {
		this.templateJson = templateJson;
	}
	
	private Boolean status;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public boolean isActive() {
		if(status != null) {
			return status.booleanValue();
		}
		return false;
	}
	
	public boolean executeAction(Map<String, Object> placeHolders, Context context, WorkflowRuleContext currentRule, Object currentRecord) throws Exception {
		if(template != null) {
			JSONObject actionObj = template.getTemplate(placeHolders);
			actionType.performAction(actionObj, context, currentRule, currentRecord);
		}
		else {
			actionType.performAction(FieldUtil.getAsJSON(placeHolders), context, currentRule, currentRecord);
		}
		return true;
	}
}
