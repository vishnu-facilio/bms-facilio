package com.facilio.bmsconsole.context;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork2.util.Element;

public class ModuleBaseWithCustomFields {
	
	@Element( value = java.lang.String.class )
	private Map<Object, Object> customProps = new HashMap<>();
	public Map<Object, Object> getCustomProps() {
		return customProps;
	}
	public void setCustomProps(Map<Object, Object> customProps) {
		this.customProps = customProps;
	}
	public void setCustomProp(Object key, Object value) {
		customProps.put(key, value);
	}
	public Object getCustomProp(Object key) {
		return customProps.get(key);
	}
}
