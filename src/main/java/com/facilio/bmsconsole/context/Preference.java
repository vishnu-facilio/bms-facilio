package com.facilio.bmsconsole.context;

import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public abstract class Preference {
	
	public Preference(String name, FacilioForm form) {
		this.name = name;
		this.form = form;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private FacilioForm form;
	public FacilioForm getForm() {
		return form;
	}
	public void setForm(FacilioForm form) {
		this.form = form;
	}
	
	public abstract WorkflowRuleContext subsituteAndEnable(Map<String, Object> map, Long recordId) throws Exception;
}
