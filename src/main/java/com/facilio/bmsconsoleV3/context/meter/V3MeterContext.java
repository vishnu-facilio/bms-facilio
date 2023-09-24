package com.facilio.bmsconsoleV3.context.meter;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
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
	private String uniqueId;
	private V3UtilityTypeContext utilityType;
	private MeterType meterType;
	private V3MeterContext parentMeter;
	private V3BaseSpaceContext meterLocation;
	private String manufacturer;
	private String model;
	private String serialNumber;
	private Long purchasedDate;
	private Long retireDate;
	public Boolean isCommissioned;
	VirtualMeterTemplateContext virtualMeterTemplate;
	public Boolean isCheckMeter;
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


	public V3MeterContext getParentMeter() {
		return parentMeter;
	}

	public void setParentMeter(V3MeterContext parentMeter) {
		this.parentMeter = parentMeter;
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
	
	public long getLocalId() {
		return super.getLocalId();
	}
	public void setLocalId(long localId) {
		super.setLocalId(localId);
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

	public String getUtilityTypeModuleName() {
		return utilityTypeModuleName;
	}
	public void setUtilityTypeModuleName(String utilityTypeModuleName) {
		this.utilityTypeModuleName = utilityTypeModuleName;
	}

	public Integer getMeterType() {
		if (meterType == null) {
			return null;
		}
		return meterType.getIndex();
	}

	public void setMeterType(Integer meterType) {
		if (meterType != null) {
			this.meterType = MeterType.valueOf(meterType);
		} else {
			this.meterType = null;
		}
	}

	public void setMeterTypeEnum(MeterType meterType) {
		this.meterType = meterType;
	}

	public MeterType getMeterTypeEnum() {
		return meterType;
	}
	@AllArgsConstructor
	@Getter
	public static enum MeterType implements FacilioIntEnum {

		PHYSICAL("physical"),
		VIRTUAL("virtual");

		public int getVal() {
			return ordinal() + 1;
		}
		String name;
		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return this.name;
		}
		private static final MeterType[] CREATION_TYPES = MeterType.values();
		public static MeterType valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}
	}
}
