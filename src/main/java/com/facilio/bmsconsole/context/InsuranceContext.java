package com.facilio.bmsconsole.context;

import java.io.File;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class InsuranceContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;

	private String companyName;
	private long validFrom;
	private long validTill;
	private VendorContext vendor;
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String insuranceCompany) {
		this.companyName = insuranceCompany;
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

	private long insuranceId;
	public long getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(long insuranceId) {
		this.insuranceId = insuranceId;
	}

	private String insuranceUrl;
	
	public String getInsuranceUrl() {
		return insuranceUrl;
	}

	public void setInsuranceUrl(String insuranceUrl) {
		this.insuranceUrl = insuranceUrl;
	}

	private File insurance;
	public File getInsurance() {
		return insurance;
	}

	public void setInsurance(File insurance) {
		this.insurance = insurance;
	}

	private String insuranceFileName;
	public String getInsuranceFileName() {
		return insuranceFileName;
	}

	public void setInsuranceFileName(String insuranceFileName) {
		this.insuranceFileName = insuranceFileName;
	}

	private  String insuranceContentType;

	public String getInsuranceContentType() {
		return insuranceContentType;
	}

	public void setInsuranceContentType(String insuranceContentType) {
		this.insuranceContentType = insuranceContentType;
	}

	private User addedBy;
	public User getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(User addedBy) {
		this.addedBy = addedBy;
	}
	
	
	
}
