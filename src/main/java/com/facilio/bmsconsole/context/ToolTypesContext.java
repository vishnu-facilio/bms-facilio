package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fs.FileStoreFactory;
import com.facilio.unitconversion.Unit;

public class ToolTypesContext extends ModuleBaseWithCustomFields {
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

	private Unit unit;

	public Unit getUnitEnum() {
		return unit;
	}

	public void setUnit(Unit issuingUnit) {
		this.unit = issuingUnit;
	}

	public int getUnit() {
		if (unit != null) {
			return unit.getUnitId();
		}
		return -1;
	}

	public void setUnit(int issuingUnit) {
		this.unit = Unit.valueOf(issuingUnit);
	}

	public Boolean individualTracking;

	public Boolean getIndividualTracking() {
		return individualTracking;
	}

	public void setIndividualTracking(Boolean individualTracking) {
		this.individualTracking = individualTracking;
	}

	public boolean isIndividualTracking() {
		if (individualTracking != null) {
			return individualTracking.booleanValue();
		}
		return false;
	}

	private long photoId;
	
	public String getPhotoUrl() throws Exception {
		if (this.photoId > 0) {
			return FileStoreFactory.getInstance().getFileStore().getPrivateUrl(this.photoId);
		}
		return null;
	}

	public long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	@Override
	public long getLocalId() {
		// TODO Auto-generated method stub
		return super.getLocalId();
	}

	@Override
	public void setLocalId(long localId) {
		// TODO Auto-generated method stub
		super.setLocalId(localId);
	}

}
