package com.facilio.bmsconsole.tenant;

public class UtilityAsset {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long tenantId = -1;
	public long getTenantId() {
		return tenantId;
	}
	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}
	
	private FacilioUtility utility;
	public FacilioUtility getUtilityEnum() {
		return utility;
	}
	public void setUtility(FacilioUtility utility) {
		this.utility = utility;
	}
	public int getUtility() {
		if (utility != null) {
			return utility.getValue();
		}
		return -1;
	}
	public void setUtility(int utility) {
		this.utility = FacilioUtility.valueOf(utility);
	}

	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
}
