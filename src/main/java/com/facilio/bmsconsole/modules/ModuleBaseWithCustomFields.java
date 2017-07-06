package com.facilio.bmsconsole.modules;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork2.util.Element;

public class ModuleBaseWithCustomFields {
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	@Element( value = java.lang.String.class )
	private Map<String, Object> customProps = new HashMap<>();
	public Map<String, Object> getCustomProps() {
		return customProps;
	}
	public void setCustomProps(Map<String, Object> customProps) {
		this.customProps = customProps;
	}
	public void setCustomProp(String key, Object value) {
		customProps.put(key, value);
	}
	public Object getCustomProp(String key) {
		return customProps.get(key);
	}
}
