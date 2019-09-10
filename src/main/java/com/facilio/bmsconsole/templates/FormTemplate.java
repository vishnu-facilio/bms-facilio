package com.facilio.bmsconsole.templates;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.forms.FacilioForm;

public class FormTemplate extends Template {
	
	private long formId = -1;
	public long getFormId() {
		return formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
	
	private FacilioForm form;
	public FacilioForm getForm() {
		return form;
	}
	public void setForm(FacilioForm form) {
		this.form = form;
	}
	
	@Override
	public JSONObject getOriginalTemplate() throws Exception {
		return null;
	}
	
	

}
