package com.facilio.bmsconsoleV3.context.meter;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class V3UtilityTypeContext extends V3Context {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private Long parentUtilityTypeId;
	private Long meterModuleID;
	private String moduleName;
	private String displayName;
	private Boolean isDefault;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getParentUtilityTypeId() {
		return parentUtilityTypeId;
	}

	public void setParentUtilityTypeId(Long parentUtilityTypeId) {
		this.parentUtilityTypeId = parentUtilityTypeId;
	}
	

	public Long getMeterModuleID() {
		return meterModuleID;
	}
	public void setMeterModuleID(Long meterModuleID) {
		this.meterModuleID = meterModuleID;
	}


	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}


	public String getDisplayName() {
		if (displayName != null && !displayName.isEmpty()) {
			return displayName;
		} else {
			return name;
		}
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	@Getter
	@Setter
	private Boolean hasReading;

	public boolean hasReadingData() {
		if(hasReading == null) {
			return false;
		}
		return hasReading;
	}
}
