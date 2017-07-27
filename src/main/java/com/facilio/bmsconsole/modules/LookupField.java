package com.facilio.bmsconsole.modules;

import com.facilio.constants.FacilioConstants;

public class LookupField extends FacilioField {
	
	private FacilioModule lookupModule;
	public FacilioModule getLookupModule() {
		return lookupModule;
	}
	public void setLookupModule(FacilioModule lookupModule) {
		this.lookupModule = lookupModule;
	}
	
	private String specialType;
	public String getSpecialType() {
		return specialType;
	}
	public void setSpecialType(String specialType) {
		this.specialType = specialType;
	}
	
	public String getLookupIcon() {
		
		if (FacilioConstants.ContextNames.USERS.equalsIgnoreCase(specialType)) {
			return "fa fa-user";
		}
		else if (FacilioConstants.ContextNames.GROUPS.equalsIgnoreCase(specialType)) {
			return "fa fa-users";
		}
		
		String lookupModuleName = lookupModule.getName();
		if ("locations".equalsIgnoreCase(lookupModuleName)) {
			return "fa fa-map-marker";
		}
		if ("assets".equalsIgnoreCase(lookupModuleName)) {
			return "fa fa-tablet";
		}
		else if ("building".equalsIgnoreCase(lookupModuleName)) {
			return "fa fa-building";
		}
		return "fa fa-search";
	}
}
