package com.facilio.bmsconsole.context;

import java.io.File;
import java.util.List;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ClientContext extends ModuleBaseWithCustomFields {
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

	private String primaryContactName;

	public String getPrimaryContactName() {
		return primaryContactName;
	}

	public void setPrimaryContactName(String primaryContactName) {
		this.primaryContactName = primaryContactName;
	}

	private String primaryContactEmail;

	public String getPrimaryContactEmail() {
		return primaryContactEmail;
	}

	public void setPrimaryContactEmail(String primaryContactEmail) {
		this.primaryContactEmail = primaryContactEmail;
	}

	private String primaryContactPhone;

	public String getPrimaryContactPhone() {
		return primaryContactPhone;
	}

	public void setPrimaryContactPhone(String primaryContactPhone) {
		this.primaryContactPhone = primaryContactPhone;
	}

	private String website;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	private LocationContext address;

	public LocationContext getAddress() {
		return address;
	}

	public void setAddress(LocationContext address) {
		this.address = address;
	}

	private String avatarUrl;

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	private File avatar;

	public File getAvatar() {
		return avatar;
	}

	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}

	private String avatarFileName;

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

	private String avatarContentType;

	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
	private List<Long> siteIds;
	public List<Long> getSiteIds() {
		return siteIds;
	}
	public void setSiteIds(List<Long> siteIds) {
		this.siteIds = siteIds;
	}
	

}
