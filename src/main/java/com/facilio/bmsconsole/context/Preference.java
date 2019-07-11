package com.facilio.bmsconsole.context;

import java.util.Map;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;

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
	
	public abstract void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception;
	public abstract void disable(Long recordId, Long moduleId) throws Exception;

}
