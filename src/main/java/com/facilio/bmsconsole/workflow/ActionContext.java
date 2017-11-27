package com.facilio.bmsconsole.workflow;

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
}
