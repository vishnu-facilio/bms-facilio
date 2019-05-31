package com.facilio.bmsconsole.context;

import com.facilio.fs.FileStoreFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
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

	private InventoryCategoryContext category;

	public InventoryCategoryContext getCategory() {
		return category;
	}

	public void setCategory(InventoryCategoryContext category) {
		this.category = category;
	}

	private ToolTypesStatusContext status;

	public ToolTypesStatusContext getStatus() {
		return status;
	}

	public void setStatus(ToolTypesStatusContext status) {
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

	public Boolean isRotating;

	public Boolean getIsRotating() {
		return isRotating;
	}

	public void setIsRotating(Boolean individualTracking) {
		this.isRotating = individualTracking;
	}

	public boolean isRotating() {
		if (isRotating != null) {
			return isRotating.booleanValue();
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

	private double currentQuantity = -1;

	public double getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(double currentQuantity) {
		this.currentQuantity = currentQuantity;
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

	private long lastPurchasedDate = -1;

	public long getLastPurchasedDate() {
		return lastPurchasedDate;
	}

	public void setLastPurchasedDate(long lastPurchasedDate) {
		this.lastPurchasedDate = lastPurchasedDate;
	}

	private double lastPurchasedPrice = -1;

	public double getLastPurchasedPrice() {
		return lastPurchasedPrice;
	}

	public void setLastPurchasedPrice(double lastPurchasedPrice) {
		this.lastPurchasedPrice = lastPurchasedPrice;
	}

	private long lastIssuedDate = -1;

	public long getLastIssuedDate() {
		return lastIssuedDate;
	}

	public void setLastIssuedDate(long lastIssuedDate) {
		this.lastIssuedDate = lastIssuedDate;
	}
	
	private Boolean isApprovalNeeded;

	public Boolean getIsApprovalNeeded() {
		return isApprovalNeeded;
	}

	public void setIsApprovalNeeded(Boolean isApprovalNeeded) {
		this.isApprovalNeeded = isApprovalNeeded;
	}

	public boolean isApprovalNeeded() {
		if (isApprovalNeeded != null) {
			return isApprovalNeeded.booleanValue();
		}
		return false;
	}

}
