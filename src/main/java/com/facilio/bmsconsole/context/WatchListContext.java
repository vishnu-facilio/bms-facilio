package com.facilio.bmsconsole.context;

import java.io.File;
import java.util.List;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class WatchListContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;

	private String name;
	private String email;
	private String phone;
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	private long avatarId;
	public long getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(long avatarId) {
		this.avatarId = avatarId;
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

	private  String avatarContentType;

	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
	public Boolean isBlocked;

	public Boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public boolean isBlocked() {
		if (isBlocked != null) {
			return isBlocked.booleanValue();
		}
		return false;
	}
	public Boolean isVip;

	public Boolean getIsVip() {
		return isVip;
	}

	public void setIsVip(Boolean isVip) {
		this.isVip = isVip;
	}

	public boolean isVip() {
		if (isVip != null) {
			return isVip.booleanValue();
		}
		return false;
	}
	
	private List<VisitorLoggingContext> visitorLogs ;


	public List<VisitorLoggingContext> getVisitorLogs() {
		return visitorLogs;
	}

	public void setVisitorLogs(List<VisitorLoggingContext> visitorLogs) {
		this.visitorLogs = visitorLogs;
	}
	
	private String aliases;
	public String getAliases() {
		return aliases;
	}

	public void setAliases(String aliases) {
		this.aliases = aliases;
	}

	private String physicalDescription;


	public String getPhysicalDescription() {
		return physicalDescription;
	}

	public void setPhysicalDescription(String physicalDescription) {
		this.physicalDescription = physicalDescription;
	}
	
	private String remarks;


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	

}
