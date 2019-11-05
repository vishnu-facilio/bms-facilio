package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class InsuranceContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;

	private String insuranceCompany;
	private long validFrom;
	private long validTill;
	private VendorContext vendor;
	public String getInsuranceCompany() {
		return insuranceCompany;
	}
	public void setInsuranceCompany(String insuranceCompany) {
		this.insuranceCompany = insuranceCompany;
	}
	public long getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(long validFrom) {
		this.validFrom = validFrom;
	}
	public long getValidTill() {
		return validTill;
	}
	public void setValidTill(long validTill) {
		this.validTill = validTill;
	}
	public VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}
	
	private InsuranceType insuranceType;
	public int getInsuranceType() {
		if (insuranceType != null) {
			return insuranceType.getIndex();
		}
		return -1;
	}
	public void setInsuranceType(int insuranceType) {
		this.insuranceType = InsuranceType.valueOf(insuranceType);
	}
	public InsuranceType getInsuranceTypeEnum() {
		return insuranceType;
	}
	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public static enum InsuranceType implements FacilioEnum {
		TENANT, VENDOR;

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static InsuranceType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	
}
