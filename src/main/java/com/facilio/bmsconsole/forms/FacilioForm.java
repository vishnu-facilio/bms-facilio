package com.facilio.bmsconsole.forms;

import java.util.List;

import com.facilio.bmsconsole.modules.FacilioModule;

public class FacilioForm {
	private long id = -1;
	public long getId() {
		return this.id;
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
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	private long moduleId = -1;

	public long getModuleId() {
		if (this.module != null) {
			return this.module.getModuleId();
		}
		return this.moduleId;
	}
	
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private List<FormField> fields;
	
	public List<FormField> getFields() {
		return fields;
	}
	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}
	
	private FacilioModule module;
	
	public void setModule(FacilioModule module) {
		this.module = module;
	}
	
	public FacilioModule getModule() {
		return this.module;
	}
}
