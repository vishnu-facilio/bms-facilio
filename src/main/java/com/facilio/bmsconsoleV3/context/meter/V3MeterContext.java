package com.facilio.bmsconsoleV3.context.meter;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class V3MeterContext extends V3Context {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private String moduleName;
	private String qrVal;
	private Long uniqueIdNumber;
	private V3UtilityTypeContext utilityType;
	private Long parentMeterId;
	private V3ResourceContext servingTo;
	private V3BaseSpaceContext meterLocation;
	private String manufacturer;
	private String model;
	private String serialNumber;
	private String tagNumber;
	private String partNumber;
	private Double unitPrice;
	private String supplier;
	private Long purchasedDate;
	private Long retireDate;
	private Long warrantyExpiryDate;
	public Boolean isCommissioned;
	public Boolean isVirtual;
	VirtualMeterTemplateContext virtualMeterTemplate;
	public Boolean isCheckMeter;
	public Boolean isBillable;
	private String utilityTypeModuleName;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}


	public V3UtilityTypeContext getUtilityType() {
		return utilityType;
	}

	public void setUtilityType(V3UtilityTypeContext utilityType) {
		this.utilityType = utilityType;
	}


	public Long getParentMeterId() {
		return parentMeterId;
	}

	public void setParentMeterId(Long parentMeterId) {
		this.parentMeterId = parentMeterId;
	}

	public V3ResourceContext getServingTo() {
		return servingTo;
	}

	public void setServingTo(V3ResourceContext servingTo) {
		this.servingTo = servingTo;
	}

	public V3BaseSpaceContext getMeterLocation() {
		return meterLocation;
	}


	public void setMeterLocation(V3BaseSpaceContext meterLocation) {
		this.meterLocation = meterLocation;
	}


	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}


	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}


	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	public String getTagNumber() {
		return tagNumber;
	}

	public void setTagNumber(String tagNumber) {
		this.tagNumber = tagNumber;
	}


	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}


	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}


	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}


	public Long getPurchasedDate() {
		return purchasedDate;
	}

	public void setPurchasedDate(Long purchasedDate) {
		this.purchasedDate = purchasedDate;
	}


	public Long getRetireDate() {
		return retireDate;
	}

	public void setRetireDate(Long retireDate) {
		this.retireDate = retireDate;
	}


	public Long getWarrantyExpiryDate() {
		return warrantyExpiryDate;
	}

	public void setWarrantyExpiryDate(Long warrantyExpiryDate) {
		this.warrantyExpiryDate = warrantyExpiryDate;
	}
	
	public long getLocalId() {
		return super.getLocalId();
	}
	public void setLocalId(long localId) {
		super.setLocalId(localId);
	}

	


	public String getUrl() {
		return FacilioProperties.getConfig("clientapp.url") + "/app/at/asset/all/" + getId() + "/overview";
	}

	

	public Boolean getIsCommissioned() {
		return isCommissioned;
	}
	public void setIsCommissioned(Boolean isCommissioned) {
		this.isCommissioned = isCommissioned;
	}
	public boolean isCommissioned() {
		if(isCommissioned != null) {
			return isCommissioned.booleanValue();
		}
		return false;
	}


	public Boolean getIsVirtual() {
		return isVirtual;
	}
	public void setIsVirtual(Boolean isVirtual) {
		this.isVirtual = isVirtual;
	}
	public boolean isVirtual() {
		if(isVirtual != null) {
			return isVirtual.booleanValue();
		}
		return false;
	}


	public Boolean getIsCheckMeter() {
		return isCheckMeter;
	}
	public void setIsCheckMeter(Boolean isCheckMeter) {
		this.isCheckMeter = isCheckMeter;
	}
	public boolean isCheckMeter() {
		if(isCheckMeter != null) {
			return isCheckMeter.booleanValue();
		}
		return false;
	}


	public Boolean getIsBillable() {
		return isBillable;
	}
	public void setIsBillable(Boolean isBillable) {
		this.isBillable = isBillable;
	}
	public boolean isBillable() {
		if(isBillable != null) {
			return isBillable.booleanValue();
		}
		return false;
	}

	public String getUtilityTypeModuleName() {
		return utilityTypeModuleName;
	}
	public void setUtilityTypeModuleName(String utilityTypeModuleName) {
		this.utilityTypeModuleName = utilityTypeModuleName;
	}
}
