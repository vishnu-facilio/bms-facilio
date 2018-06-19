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
	
	private TenantUtility utility;
	public TenantUtility getUtilityEnum() {
		return utility;
	}
	public void setUtility(TenantUtility utility) {
		this.utility = utility;
	}
	public TenantUtility getUtility() {
		return utility;
	}
	public void setUtility(int utility) {
		this.utility = TenantUtility.valueOf(utility);
	}

	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
}
