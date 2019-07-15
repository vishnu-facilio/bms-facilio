package com.facilio.accounts.dto;

import java.util.List;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.license.LicenseContext.FacilioLicense;

public class AccountUser {

	private static final long serialVersionUID = 1L;
	private long uid;
	private String name;
	private String email;
	private long photoId;
	private String timezone;
	private String password;
	private String language;
	private String phone;
	private String mobile;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String country;
	private long orgId;
	private long invitedTime;
	private int userType;
	private boolean facilioAuth;
	private long portalId;
	private String serverName;
	public boolean portal_verified;
	private FacilioLicense license;
	
	
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
	
	public String getName() {
		if (this.name == null && this.email != null && !this.email.isEmpty()) {
			return this.email.substring(0, this.email.indexOf("@"));
		}
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@JSON(serialize=false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	private Boolean userVerified;
	public Boolean getUserVerified() {
		return userVerified;
	}
	public void setUserVerified(Boolean userVerified) {
		this.userVerified = userVerified;
	}
	public boolean isUserVerified(){
		if(userVerified!=null){
			return userVerified.booleanValue();
		}
		return false;
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
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
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getInvitedTime() {
		return invitedTime;
	}
	public void setInvitedTime(long invitedTime) {
		this.invitedTime = invitedTime;
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
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}

	public boolean isFacilioAuth() {
		return facilioAuth;
	}

	public void setFacilioAuth(boolean facilioAuth) {
		this.facilioAuth = facilioAuth;
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

	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder("User [").append(uid).append(", ").append(email).append("]").toString();
	}
	
	public long getId() {
		return uid;
	}
	public void setId(long id) {
		this.uid = id;
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

}
