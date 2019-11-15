package com.facilio.bmsconsole.context;

import java.util.Map;

import com.facilio.bmsconsole.forms.FacilioForm;

public abstract class Preference {
	
	public Preference(String name, String displayName, FacilioForm form, String desc) {
		this.name = name;
		this.description = desc;
		this.form = form;
		this.displayName = displayName;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	private FacilioForm form;
	public FacilioForm getForm() {
		return form;
	}
	public void setForm(FacilioForm form) {
		this.form = form;
	}
	
	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public abstract void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception;
	public abstract void disable(Long recordId, Long moduleId) throws Exception;

}
