package com.facilio.accounts.dto;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class User {
	
	private long uid;
	private String name;
	private String cognitoId;
	private boolean userVerified;
	private String email;
	private long photoId;
	private String timezone;
	private String language;
	private String phone;
	private String mobile;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String country;
	private long ouid;
	private long orgId;
	private long invitedTime;
	private boolean isDefaultOrg;
	private boolean userStatus;
	private boolean inviteAcceptStatus;
	private long roleId;
	private int userType;
	
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getName() {
		if (this.name == null && this.email != null) {
			return this.email.substring(0, this.email.indexOf("@"));
		}
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCognitoId() {
		return cognitoId;
	}
	public void setCognitoId(String cognitoId) {
		this.cognitoId = cognitoId;
	}
	public boolean isUserVerified() {
		return userVerified;
	}
	public void setUserVerified(boolean userVerified) {
		this.userVerified = userVerified;
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
	public String getAvatarUrl() throws Exception {
		if (this.photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getPrivateUrl(this.photoId);
		}
		return null;
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
	public long getInvitedTime() {
		return invitedTime;
	}
	public void setInvitedTime(long invitedTime) {
		this.invitedTime = invitedTime;
	}
	public boolean getIsDefaultOrg() {
		return isDefaultOrg;
	}
	public void setDefaultOrg(boolean isDefaultOrg) {
		this.isDefaultOrg = isDefaultOrg;
	}
	public boolean getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(boolean userStatus) {
		this.userStatus = userStatus;
	}
	public boolean getInviteAcceptStatus() {
		return inviteAcceptStatus;
	}
	public void setInviteAcceptStatus(boolean inviteAcceptStatus) {
		this.inviteAcceptStatus = inviteAcceptStatus;
	}
	public long getRoleId() {
		return roleId;
	}
	public Role getRole() throws Exception {
		return AccountUtil.getRoleBean().getRole(roleId);
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
}