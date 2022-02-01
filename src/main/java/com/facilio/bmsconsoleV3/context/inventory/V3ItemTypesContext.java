package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.v3.context.V3Context;

public class V3ItemTypesContext extends V3Context {

    private static final long serialVersionUID = 1L;
    
    private String name;
	private String description;
	private String serialNumber;
	private V3InventoryCategoryContext category;
	private Double minimumQuantity;
	public Boolean isRotating;
	private Long photoId;
	private Double currentQuantity;
	private Long lastPurchasedDate;
	private Double lastPurchasedPrice;
	private Long lastIssuedDate;
	public Boolean isConsumable;
	private Boolean isApprovalNeeded;
	private Double sellingPrice;
	
	public Double getMinimumQuantity() {
		return minimumQuantity;
	}

	public void setMinimumQuantity(Double minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}

	public Long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
	}

	public Double getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(Double currentQuantity) {
		this.currentQuantity = currentQuantity;
	}

	public Long getLastPurchasedDate() {
		return lastPurchasedDate;
	}

	public void setLastPurchasedDate(Long lastPurchasedDate) {
		this.lastPurchasedDate = lastPurchasedDate;
	}

	public Double getLastPurchasedPrice() {
		return lastPurchasedPrice;
	}

	public void setLastPurchasedPrice(Double lastPurchasedPrice) {
		this.lastPurchasedPrice = lastPurchasedPrice;
	}

	public Long getLastIssuedDate() {
		return lastIssuedDate;
	}

	public void setLastIssuedDate(Long lastIssuedDate) {
		this.lastIssuedDate = lastIssuedDate;
	}

	public Double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(Double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

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

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public V3InventoryCategoryContext getCategory() {
		return category;
	}

	public void setCategory(V3InventoryCategoryContext category) {
		this.category = category;
	}

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
