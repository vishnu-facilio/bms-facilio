package com.facilio.bmsconsole.modules;

import java.util.HashMap;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
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
	public FacilioModule getModule() throws Exception {
		if(moduleId > 0 ) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			return modBean.getModule(moduleId);
		}
		return null;
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
	
	private long localId;
	
	public long getLocalId() {
		return localId;
	}
	public void setLocalId(long localId) {
		this.localId = localId;
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
	
	private long sysCreatedTime = -1;
	public long getSysCreatedTime() {
		return sysCreatedTime;
	}
	public void setSysCreatedTime(long sysCreatedTime) {
		this.sysCreatedTime = sysCreatedTime;
	}
}
