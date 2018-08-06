package com.facilio.bmsconsole.tenant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.BaseSpaceContext;

public class TenantContext {
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
	
	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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

	private long spaceId = -1;
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
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
}
