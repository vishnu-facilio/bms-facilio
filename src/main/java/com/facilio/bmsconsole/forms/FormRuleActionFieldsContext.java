package com.facilio.bmsconsole.forms;

import com.facilio.db.criteria.Criteria;

public class FormRuleActionFieldsContext {
	
	long id;
	long orgId;
	long formRuleActionId = -1;
	long formFieldId = -1;
	long formSectionId = -1;
	public long getFormSectionId() {
		return formSectionId;
	}

	public void setFormSectionId(long formSectionId) {
		this.formSectionId = formSectionId;
	}
	String actionMeta;
	long criteriaId = -1;
	Criteria criteria;
	
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
	
	public long getFormRuleActionId() {
		return formRuleActionId;
	}

	public void setFormRuleActionId(long formRuleActionId) {
		this.formRuleActionId = formRuleActionId;
	}
	
	public long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
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

}
