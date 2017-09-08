package com.facilio.fw.auth;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.OrgApi;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.UserInfo;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;

public class LoginUtil {

	public static final String IDTOKEN_COOKIE_NAME = "fc.idToken";
	
	public static UserInfo getUserInfo(CognitoUser cognitoUser) throws Exception {
		return getUserInfo(null,cognitoUser);
	}
	
	public static UserInfo getUserInfo(String threadpoolname,CognitoUser cognitoUser) throws Exception {
		
		UserInfo userInfo = new UserInfo();
		
		UserContext usrCtx = UserAPI.getUser(cognitoUser.getEmail());
		if (usrCtx == null) {
			
			JSONObject userAttributes = CognitoUtil.getUserAttributes(cognitoUser.getEmail());
			if (userAttributes == null) {
				throw new Exception("This user not associated with any organization.");
			}
			
			String userSubDomain = (String) (userAttributes.containsKey("custom:orgDomain") ? userAttributes.get("custom:orgDomain") : userAttributes.get("custom:orgName"));
			String name = (cognitoUser.getName() == null) ? cognitoUser.getName() : cognitoUser.getUserName();
			
			OrgApi.createOrganization(null, userSubDomain, userSubDomain, name, cognitoUser.getEmail(), cognitoUser.getCognitoId(), true);
			
			usrCtx = UserAPI.getUser(cognitoUser.getEmail());
			userInfo.setSubdomain(userSubDomain);
		}
		
		userInfo.setCognitoId(cognitoUser.getCognitoId());
		userInfo.setUserName(cognitoUser.getUserName());
		userInfo.setEmail(cognitoUser.getEmail());
		userInfo.setEmailVerified(cognitoUser.isEmailVerified());
		userInfo.setPhoneNumber(cognitoUser.getPhoneNumber());
		userInfo.setPhoneNumberVerified(cognitoUser.isPhoneNumberVerified());
		userInfo.setLocale(cognitoUser.getLocale());
		userInfo.setTimeZone(cognitoUser.getTimezone());
		userInfo.setAdditionalProps(cognitoUser.getAdditionalProps());
		
		userInfo.setUserId(usrCtx.getUserId());
		userInfo.setOrgUserId(usrCtx.getOrgUserId());
		userInfo.setOrgId(usrCtx.getOrgId());
		userInfo.setName(usrCtx.getName());
		userInfo.setActive(usrCtx.getUserStatus());
		userInfo.setRole(usrCtx.getRole());
		return userInfo;
	}
	
	public static String getUserCookie(HttpServletRequest request, String key) {
		Cookie cookies[] = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(key)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
	public static boolean eraseUserCookie(HttpServletRequest request, HttpServletResponse response, String key, String domain) {
		Cookie cookies[] = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(key)) {
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setDomain(domain.substring(1));
					cookie.setMaxAge(0);
					response.addCookie(cookie);
					return true;
				}
			}
		}
		return false;
	}
	
	public static void addUserCookie(HttpServletResponse response, String key, String value, String domain) {
		
		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		cookie.setDomain(domain.substring(1));
		response.addCookie(cookie);
	}
}