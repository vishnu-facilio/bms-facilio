package com.facilio.modules.fields;

import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.modules.FacilioModule;

public class LookupField extends FacilioField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LookupField() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	
	protected LookupField(LookupField field) { // Do not forget to Handle here if new property is added
		super(field);
		this.lookupModule = field.lookupModule;
		this.specialType = field.specialType;
	}
	
	@Override
	public LookupField clone() {
		// TODO Auto-generated method stub
		return new LookupField(this);
	}


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
