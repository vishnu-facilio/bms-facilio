package com.facilio.bmsconsole.modules;

import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;

public class LookupField extends FacilioField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		
		if(specialType != null && LookupSpecialTypeUtil.isSpecialType(specialType)) {
			return LookupSpecialTypeUtil.getLookupIcon(specialType);
		}
		if(lookupModule != null)
		{
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
		return null;
	}
}
