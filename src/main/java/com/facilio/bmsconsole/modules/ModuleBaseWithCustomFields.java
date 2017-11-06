package com.facilio.bmsconsole.modules;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.opensymphony.xwork2.util.Element;

public class ModuleBaseWithCustomFields {
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Element( value = java.lang.String.class )
	private Map<String, Object> customProps = new HashMap<>();
	
	@JsonAnyGetter
	public Map<String, Object> getCustomProps() {
		return customProps;
	}
	public void setCustomProps(Map<String, Object> customProps) {
		this.customProps = customProps;
	}
	public void addCustomProps(Map<String, Object> customProps) {
		if(this.customProps != null) {
			this.customProps.putAll(customProps);
		}
		else {
			this.customProps = customProps;
		}
	}
	
	
	@JsonAnySetter
	public void setCustomProp(String key, Object value) {
		customProps.put(key, value);
	}
	
	public Object getCustomProp(String key) {
		return customProps.get(key);
	}
}
