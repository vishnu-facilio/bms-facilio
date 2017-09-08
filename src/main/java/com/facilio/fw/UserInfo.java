package com.facilio.fw;

import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.RoleContext;
import com.facilio.bmsconsole.util.OrgApi;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Current user object.
 *
 * You can get current user object by UserInfo.getCurrentUser();
 * 
 * Also, you can check if the current user has 'admin' role by UserInfo.getCurrentUser().hasRole('admin')
 * 
 */

public class UserInfo {

	// cognito props
	private String cognitoId = "";
	private String userName;
	private String email;
	private boolean emailVerified;
	private String phoneNumber;
	private boolean phoneNumberVerified;
	private String locale;
	private String timeZone;
	private JSONObject additionalProps;

	// db props
	private long userId;
	private long orgId;
	private long orgUserId;
	private String name;
	private boolean isActive;
	private RoleContext role;
	private String subdomain;

	private static ThreadLocal<UserInfo> userlocal = new ThreadLocal<UserInfo>();

	public static UserInfo getCurrentUser()
	{
		return userlocal.get();
	}

	public static  void setCurrentUser(UserInfo userinfo)
	{
		userlocal.set(userinfo);
	}

	public static void cleanup()
	{
		userlocal.remove();
	}

	public static ThreadLocal<UserInfo> getUserlocal() {
		return userlocal;
	}

	public static void setUserlocal(ThreadLocal<UserInfo> userlocal) {
		UserInfo.userlocal = userlocal;
	}
	
	public String getCognitoId() {
		return cognitoId;
	}

	public void setCognitoId(String cognitoId) {
		this.cognitoId = cognitoId;
	}

	public String getSubdomain() throws Exception {
		if (this.subdomain == null) {
			this.subdomain = OrgApi.getOrgDomainFromId(orgId);
		}
		return subdomain;
	}

	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isPhoneNumberVerified() {
		return phoneNumberVerified;
	}

	public void setPhoneNumberVerified(boolean phoneNumberVerified) {
		this.phoneNumberVerified = phoneNumberVerified;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getOrgUserId() {
		return orgUserId;
	}

	public void setOrgUserId(long orgUserId) {
		this.orgUserId = orgUserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public RoleContext getRole() {
		return role;
	}

	public void setRole(RoleContext role) {
		this.role = role;
	}
	
	public void setAdditionalProps(JSONObject additionalProps) {
		this.additionalProps = additionalProps;
	}
	
	public void addAdditionalProperty(String key, String value) {
		if (this.additionalProps == null) {
			this.additionalProps = new JSONObject();
		}
		this.additionalProps.put(key, value);
	}
	
	public String getAdditionalProperty(String key) {
		if (this.additionalProps != null) {
			return this.additionalProps.get(key).toString();
		}
		return null;
	}
	
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> properties = mapper.convertValue(this, Map.class);
		if (properties != null) {
			return properties.toString();
		}
		else {
			return "null";
		}
	}
}