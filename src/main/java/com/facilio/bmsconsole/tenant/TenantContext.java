package com.facilio.bmsconsole.tenant;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TenantContext extends ModuleBaseWithCustomFields{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long inTime;
	public long getInTime() {
		return inTime;
	}
	public void setInTime(long inTime) {
		this.inTime = inTime;
	}
	
	private long outTime;
	public long getOutTime() {
		return outTime;
	}
	public void setOutTime(long outTime) {
		this.outTime = outTime;
	}

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
	
	private User contact;
	public User getContact() {
		return contact;
	}
	public void setContact(User contact) {
		this.contact = contact;
	}

	
	private long logoId = -1;
	public long getLogoId() {
		return logoId;
	}
	public void setLogoId(long logoId) {
		this.logoId = logoId;
	}

	private String logoUrl;
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	private ZoneContext zone = null;
	public ZoneContext getZone() {
		return zone;
	}
	public void setZone(ZoneContext zone) {
		this.zone = zone;
	}
	
	private BaseSpaceContext space;
	public BaseSpaceContext getSpace() {
		return space;
	}
	public void setSpace(BaseSpaceContext space) {
		this.space = space;
	}

	private List<UtilityAsset> utilityAssets;
	public List<UtilityAsset> getUtilityAssets() {
		return utilityAssets;
	}
	public void setUtilityAssets(List<UtilityAsset> utilityAssets) {
		this.utilityAssets = utilityAssets;
	}
	public List<UtilityAsset> getUtilityAssetsOfUtility(int utilityId) {
		
		List<UtilityAsset> utilityAssets1 = null;
		
		if(utilityAssets != null && !utilityAssets.isEmpty()) {
			
			for(UtilityAsset utilityAsset :utilityAssets) {
				
				if(utilityId == utilityAsset.getUtility()) {
					if(utilityAssets1 == null) {
						utilityAssets1 = new ArrayList<>();
					}
					utilityAssets1.add(utilityAsset);
				}
			}
		}
		return utilityAssets1;
	}
	
	private File tenantLogo;
	public File getTenantLogo() {
		return tenantLogo;
	}
	public void setTenantLogo(File tenantLogo) {
		this.tenantLogo = tenantLogo;
	}
	
	private String tenantLogoFileName;
	public String getTenantLogoFileName() {
		return tenantLogoFileName;
	}
	public void setTenantLogoFileName(String tenantLogoFileName) {
		this.tenantLogoFileName = tenantLogoFileName;
	}
	
	private  String tenantLogoContentType;
	public String getTenantLogoContentType() {
		return tenantLogoContentType;
	}
	public void setTenantLogoContentType(String tenantLogoContentType) {
		this.tenantLogoContentType = tenantLogoContentType;
	}
	
   private long siteId = -1;
	
	public long getSiteId() {
		return this.siteId;
	}
	
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private List<TenantUserContext> tenantUsers;
	public List<TenantUserContext> getTenantUsers() {
		return tenantUsers;
	}
	public void setTenantUsers(List<TenantUserContext> users) {
		this.tenantUsers = users;
	}
	
    private Status status;
	public Status getStatusEnum() {
		return status;
	}
	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	
	public static enum Status {
		ACTIVE(),
		EXPIRED();
		
		public int getValue() {
			return ordinal()+1;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	
	
}
