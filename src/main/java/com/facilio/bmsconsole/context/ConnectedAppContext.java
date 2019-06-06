package com.facilio.bmsconsole.context;

import com.facilio.fs.FileStoreFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ConnectedAppContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long orgId;
	private String name;
	private String linkName;
	private String description;
	private long logoId;
	private String baseUrl;
	private Boolean isActive = true;
	private Boolean samlEnabled;
	private String spEntityId;
	private String spAcsUrl;
	private String spLogoutUrl;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLinkName() {
		if (this.linkName == null && this.name != null && !this.name.trim().isEmpty()) {
			this.linkName = this.name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		}
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getLogoId() {
		return logoId;
	}
	public String getLogoUrl() throws Exception {
		if (this.logoId > 0) {
			return FileStoreFactory.getInstance().getFileStore().getPrivateUrl(this.logoId);
		}
		return null;
	}
	public void setLogoId(long logoId) {
		this.logoId = logoId;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public Boolean isActive() {
		return isActive;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Boolean getSamlEnabled() {
		return samlEnabled;
	}
	public void setSamlEnabled(Boolean samlEnabled) {
		this.samlEnabled = samlEnabled;
	}
	public String getSpEntityId() {
		return spEntityId;
	}
	public void setSpEntityId(String spEntityId) {
		this.spEntityId = spEntityId;
	}
	public String getSpAcsUrl() {
		return spAcsUrl;
	}
	public void setSpAcsUrl(String spAcsUrl) {
		this.spAcsUrl = spAcsUrl;
	}
	public String getSpLogoutUrl() {
		return spLogoutUrl;
	}
	public void setSpLogoutUrl(String spLogoutUrl) {
		this.spLogoutUrl = spLogoutUrl;
	}
}