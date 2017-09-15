package com.facilio.bmsconsole.workflow;

public class ActionContext {
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	public int getActionTypeVal() {
		return actionType.getVal();
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
	
	private int templateType;
	public int getTemplateType() {
		return templateType;
	}
	public void setTemplateType(int templateType) {
		this.template = DefaultTemplates.getDefaultTemplate(templateType);
		if(template == null) {
			throw new IllegalArgumentException("No DefaultTemplate exists with value : "+templateType);
		}
		this.templateType = templateType;
	}
	
	private long templateId;
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
