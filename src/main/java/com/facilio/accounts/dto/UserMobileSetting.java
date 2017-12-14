package com.facilio.accounts.dto;

public class UserMobileSetting {
	
	private long userMobileSettingId = -1;
	private long userId = -1;
	private String mobileInstanceId;
	private String email;
	
	public long getUserMobileSettingId() {
		return userMobileSettingId;
	}
	public void setUserMobileSettingId(long userMobileSettingId) {
		this.userMobileSettingId = userMobileSettingId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getMobileInstanceId() {
		return mobileInstanceId;
	}
	public void setMobileInstanceId(String mobileInstanceId) {
		this.mobileInstanceId = mobileInstanceId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}