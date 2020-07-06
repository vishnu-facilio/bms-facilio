package com.facilio.bmsconsoleV3.context;

import java.io.File;
import java.util.List;

import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.v3.context.V3Context;

public class V3WatchListContext extends V3Context {

    private static final long serialVersionUID = 1L;
    
    private String name;
	private String email;
	private String phone;
	private Long avatarId;
	private String avatarUrl;
	private File avatar;
	private String avatarFileName;
	private String avatarContentType;
	public Boolean isBlocked;
	public Boolean isVip;
	private List<V3VisitorLoggingContext> visitorLogs;
	private String aliases;
	private String remarks;
	private String physicalDescription;
	
	public Long getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(Long avatarId) {
		this.avatarId = avatarId;
	}

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
	
	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public File getAvatar() {
		return avatar;
	}

	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
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

	public List<V3VisitorLoggingContext> getVisitorLogs() {
		return visitorLogs;
	}

	public void setVisitorLogs(List<V3VisitorLoggingContext> visitorLogs) {
		this.visitorLogs = visitorLogs;
	}
	
	public String getAliases() {
		return aliases;
	}

	public void setAliases(String aliases) {
		this.aliases = aliases;
	}

	public String getPhysicalDescription() {
		return physicalDescription;
	}

	public void setPhysicalDescription(String physicalDescription) {
		this.physicalDescription = physicalDescription;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}	

}
