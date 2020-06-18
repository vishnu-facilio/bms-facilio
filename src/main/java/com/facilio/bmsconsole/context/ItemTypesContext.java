package com.facilio.bmsconsole.context;

import com.facilio.services.factory.FacilioFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class ItemTypesContext extends ModuleBaseWithCustomFields {
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

	private ItemTypesStatusContext status;

	public ItemTypesStatusContext getStatus() {
		return status;
	}

	public void setStatus(ItemTypesStatusContext status) {
		this.status = status;
	}

	private Unit unit;

	public Unit getUnitEnum() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public int getUnit() {
		if (unit != null) {
			return unit.getUnitId();
		}
		return -1;
	}

	public void setUnit(int unit) {
		this.unit = Unit.valueOf(unit);
		;
	}

	private double minimumQuantity;

	public double getMinimumQuantity() {
		return minimumQuantity;
	}

	public void setMinimumQuantity(double minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
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

	public long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	public String getPhotoUrl() throws Exception {
		if (this.photoId > 0) {
			return FacilioFactory.getFileStore().getPrivateUrl(this.photoId);
		}
		return null;
	}

	private double currentQuantity = -1;

	public double getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(double currentQuantity) {
		this.currentQuantity = currentQuantity;
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
	
	public Boolean isConsumable;
	public Boolean getIsConsumable() {
		return isConsumable;
	}
	public void setIsConsumable(Boolean isConsumable) {
		this.isConsumable = isConsumable;
	}
	
	public boolean isConsumable() {
		if(isConsumable !=null ) {
			return isConsumable.booleanValue();
		}
		return false;
	}

}
