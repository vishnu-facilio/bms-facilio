package com.facilio.license;

import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;

public class LicenseContext {
	
	
	private long id;
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public Integer getTotalLicense() {
		return totalLicense;
	}
	public void setTotalLicense(Integer totalLicense) {
		this.totalLicense = totalLicense;
	}
	public Integer getUsedLicense() {
		return usedLicense;
	}
	public void setUsedLicense(Integer usedLicense) {
		this.usedLicense = usedLicense;
	}
	public long getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(long expiryDate) {
		this.expiryDate = expiryDate;
	}

	private Integer totalLicense;
	private Integer usedLicense = 0;
	private long expiryDate;
	private Integer licensetype;
	
	public Integer getLicensetype() {
		return licensetype;
	}
	public void setLicensetype(Integer licensetype) {
		this.licensetype = licensetype;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private FacilioLicense license;
	public FacilioLicense getLicenseEnum() {
		return license;
	}
	public void setLicense(FacilioLicense license) {
		this.license = license;
	}
	public int getLicense() {
		if (license != null) {
			return license.getValue();
		}
		return -1;
	}
	public void setLicense(int licenseValue) {
		this.license = FacilioLicense.valueOf(licenseValue);
	}


	public enum FacilioLicense {
		ADMINSTRATOR(10),
		TECHNICIAN(10),
		AGENT(0),
		CONTRACTOR(0);	
		
		public int getDefaultLicenses() {
			return defaultLicenses;
		}
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static FacilioLicense valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}


		private FacilioLicense(int defaultLicenses) {
			this.defaultLicenses = defaultLicenses;
		}

		private int defaultLicenses;
		

	}
	
}
