package com.facilio.bmsconsole.context;

import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public abstract class NotificationPreference {
	
	public NotificationPreference(String name, FacilioForm form) {
		this.name = name;
//		this.workflowRule = workflowRule;
		this.form = form;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

//	private WorkflowRuleContext workflowRule;
//	@JSON(serialize = false)
//	public WorkflowRuleContext getWorkflowRule() {
//		return workflowRule;
//	}
//	public void setWorkflowRule(WorkflowRuleContext workflowRule) {
//		this.workflowRule = workflowRule;
//	}
	
	private FacilioForm form;
	public FacilioForm getForm() {
		return form;
	}
	public void setForm(FacilioForm form) {
		this.form = form;
	}
	
	public abstract WorkflowRuleContext substitute(Map<String, Object> map, ModuleBaseWithCustomFields record) throws Exception;
}
