package com.facilio.accounts.dto;

import java.util.List;

import com.facilio.license.LicenseContext.FacilioLicense;

public class User extends IAMUser {
	private static final long serialVersionUID = 1L;

	public User() {
	}
	
	public User(IAMUser user) {
		setUid(user.getUid());
		setName(user.getName());
		setEmail(user.getEmail());
		setPhotoId(user.getPhotoId());
		setTimezone(user.getTimezone());
		setLanguage(user.getLanguage());
		setPhone(user.getPhone());
		setMobile(user.getMobile());
		setStreet(user.getStreet());
		setCity(user.getCity());
		setState(user.getState());
		setZip(user.getZip());
		setCountry(user.getCountry());
		setDomainName(user.getDomainName());
		setOrgId(user.getOrgId());
	}

	
	private Boolean inviteAcceptStatus;
	public Boolean getInviteAcceptStatus() {
		return inviteAcceptStatus;
	}
	public void setInviteAcceptStatus(Boolean inviteAcceptStatus) {
		this.inviteAcceptStatus = inviteAcceptStatus;
	}
	public boolean isInviteAcceptStatus(){
		if(inviteAcceptStatus!=null){
			return inviteAcceptStatus.booleanValue();
		}
		return false;
	}
	private boolean facilioAuth;
	public boolean isFacilioAuth() {
		return facilioAuth;
	}
	public void setFacilioAuth(boolean facilioAuth) {
		this.facilioAuth = facilioAuth;
	}
	
	private long ouid;
	public long getOuid() {
		return ouid;
	}
	public void setOuid(long ouid) {
		this.ouid = ouid;
	}
	
	private long invitedTime;
	public long getInvitedTime() {
		return invitedTime;
	}
	public void setInvitedTime(long invitedTime) {
		this.invitedTime = invitedTime;
	}
	
	private long roleId;
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	private Role role;
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	private int userType;
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
	
	private long portalId;
	public long getPortalId() {
		return portalId;
	}
	public void setPortalId(long portalId) {
		this.portalId = portalId;
	}
	
//	private String serverName;
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
		return 0;
	}
	public void setLicense(int licenseValue) {
		this.license = FacilioLicense.valueOf(licenseValue);
	}
	
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
	
//	@Override
	public long getId() {
		return ouid;
	}
//	@Override
	public void setId(long id) {
		this.ouid = id;
	}

	public boolean isPortalUser() {
		if(portalId > 0) {
			return true;
		}
		return false;
	}

	
}
