package com.facilio.accounts.dto;

import java.io.Serializable;
import java.util.List;

import com.facilio.license.LicenseContext.FacilioLicense;

public class AppUser implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long uid;
	private String name;
	private String email;
	private long photoId;
	private String timezone;
	private String phone;
	private String mobile;
	private long ouid;
	private long orgId;
	private long roleId;
	private long portalId;
	private String serverName;
	private FacilioLicense license;
	private Long shiftId;
	public boolean portal_verified;
	
	
	public boolean getPortal_verified() {
		if(portal_verified) {
			return portal_verified;
		}
		return false;
	}
	public void setPortal_verified(boolean portal_verified) {
		
		this.portal_verified = portal_verified;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
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
		return 0;
	}
	public void setLicense(int licenseValue) {
		this.license = FacilioLicense.valueOf(licenseValue);
	}

	
	public String getName() {
		if (this.name == null && this.email != null && !this.email.isEmpty()) {
			return this.email.substring(0, this.email.indexOf("@"));
		}
		return this.name;
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
	public long getPhotoId() {
		return photoId;
	}
	
	private String originalUrl;
	public String getOriginalUrl() {
		return originalUrl;
	}
	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	private String avatarUrl;
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public long getOuid() {
		return ouid;
	}
	public long getId() {
		return ouid;
	}
	public void setId(long id) {
		this.ouid = id;
	}
	public void setOuid(long ouid) {
		this.ouid = ouid;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private Boolean isDefaultOrg;
	public Boolean getIsDefaultOrg() {
		return isDefaultOrg;
	}
	public void setDefaultOrg(Boolean isDefaultOrg) {
		this.isDefaultOrg = isDefaultOrg;
	}
	public boolean isDefaultOrg() {
		if(isDefaultOrg != null) {
			return isDefaultOrg.booleanValue();
		}
		return false;
	}
	
	private Boolean userStatus;
	public Boolean getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(Boolean userStatus) {
		this.userStatus = userStatus;
	}
	public boolean isActive() {
		if(userStatus != null) {
			return userStatus.booleanValue();
		}
		return false;
	}
	public long getRoleId() {
		return roleId;
	}
	
	private Role role;
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public long getPortalId() {
		return portalId;
	}

	public boolean isPortalUser()
	{
		if(portalId>0)
		{
			return true;
		}
		return false;
	}
	public void setPortalId(long portalId) {
		this.portalId = portalId;
	}


	private List<Long> accessibleSpace;
	public List<Long> getAccessibleSpace() {
		return accessibleSpace;
	}
	public void setAccessibleSpace(List<Long> accessibleSpace) {
		this.accessibleSpace = accessibleSpace;
	}
	private List<Long> groups;


	public List<Long> getGroups() {
		return groups;
	}
	public void setGroups(List<Long> groups) {
		this.groups = groups;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Long getShiftId() {
		return shiftId;
	}
	public void setShiftId(Long shiftId) {
		this.shiftId = shiftId;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder("User [").append(ouid).append(", ").append(email).append("]").toString();
	}
}
