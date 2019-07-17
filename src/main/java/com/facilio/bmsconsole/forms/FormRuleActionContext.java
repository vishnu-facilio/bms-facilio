package com.facilio.bmsconsole.forms;

import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;

public class FormRuleActionContext {
	long id = -1;
	long orgId = -1;
	long formRuleId = -1;
	long formFieldId = -1;
	String actionMeta;
	FormActionType actionType;
	
	
	public void executeAction(FacilioContext facilioContext) throws Exception {
		facilioContext.put(FormRuleAPI.FORM_RULE_ACTION_CONTEXT, this);
		actionType.performAction(facilioContext);
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getFormRuleId() {
		return formRuleId;
	}
	public void setFormRuleId(long formRuleId) {
		this.formRuleId = formRuleId;
	}
	public long getFormFieldId() {
		return formFieldId;
	}
	public void setFormFieldId(long formFieldId) {
		this.formFieldId = formFieldId;
	}
	public String getActionMeta() {
		return actionMeta;
	}
	public void setActionMeta(String actionMeta) {
		this.actionMeta = actionMeta;
	}
	public FormActionType getActionTypeEnum() {
		return actionType;
	}
	public int getActionType() {
		return actionType.getVal();
	}
	public void setActionType(int actionType) {
		this.actionType = FormActionType.getActionType(actionType);
	}
}
