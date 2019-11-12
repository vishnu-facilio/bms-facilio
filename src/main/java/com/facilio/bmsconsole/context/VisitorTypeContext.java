package com.facilio.bmsconsole.context;

import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.FacilioUtil;

public class VisitorTypeContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean isEnabled;
	

	
	public boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
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
	
	@Override
	public String toString() {
		return name;
	}
}
