package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class ToolsContext extends ModuleBaseWithCustomFields{
private static final long serialVersionUID = 1L;
	
	private String name;
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

	private String serialNumber;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	private ToolsCategoryContext category;
	public ToolsCategoryContext getCategory() {
		return category;
	}

	public void setCategory(ToolsCategoryContext category) {
		this.category = category;
	}

	private ToolsStatusContext status;

	public ToolsStatusContext getStatus() {
		return status;
	}

	public void setStatus(ToolsStatusContext status) {
		this.status = status;
	}

	private Unit issuingUnit;
	public Unit getIssuingUnitEnum() {
		return issuingUnit;
	}

	public void setIssuingUnit(Unit issuingUnit) {
		this.issuingUnit = issuingUnit;
	}
	public int getIssuingUnit() {
		if (issuingUnit != null) {
			return issuingUnit.getUnitId();
		}
		return -1;
	}
	public void setIssuingUnit(int issuingUnit) {
		this.issuingUnit = Unit.valueOf(issuingUnit);
	}
	
	private long ttime;
	public long getTtime() {
		return ttime;
	}

	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	private long modifiedTime;
	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

}
