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
	
	private Boolean deleted;
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public boolean isDeleted() {
		if (deleted != null) {
			return deleted.booleanValue();
		}
		return false;
	}

	@Element( value = java.lang.String.class )
	private Map<String, Object> data = null;
	
	@JsonAnyGetter
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public void addData(Map<String, Object> data) {
		if(this.data != null) {
			this.data.putAll(data);
		}
		else {
			this.data = data;
		}
	}
	
	
	@JsonAnySetter
	public void setDatum(String key, Object value) {
		if(data == null) {
			data = new HashMap<>();
		}
		data.put(key, value);
	}
	
	public Object getDatum(String key) {
		if(data == null) {
			return null;
		}
		return data.get(key);
	}
}
