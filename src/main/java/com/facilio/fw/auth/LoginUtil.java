package com.facilio.fw.auth;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;

public class LoginUtil {

	public static final String IDTOKEN_COOKIE_NAME = "fc.idToken";
	
	public static Account getAccount(CognitoUser cognitoUser, boolean addUserEntryIfNotExists) throws Exception {
		if(cognitoUser==null) {
			return null;
		}
		
		User user = null;
		
		HttpServletRequest request = ServletActionContext.getRequest();
		String currentOrgDomain = LoginUtil.getUserCookie(request, "fc.currentOrg");
		
		if (currentOrgDomain != null) {
			user = AccountUtil.getUserBean().getFacilioUser(cognitoUser.getEmail(), currentOrgDomain);
		}
		if (user == null) {
			user = AccountUtil.getUserBean().getFacilioUser(cognitoUser.getEmail());
		}
		
		Organization org = null;
		
		if (user == null) {
			org = AccountUtil.getCurrentOrg();
			if (org != null) {
				Locale locale = request.getLocale();
				if (locale == null) {
					locale = Locale.US;
				}
				User createUser = new User();
				createUser.setName(cognitoUser.getName());
				createUser.setEmail(cognitoUser.getEmail());
				createUser.setUserVerified(false);
				createUser.setTimezone(org.getTimezone());
				createUser.setLanguage(locale.getLanguage());
				createUser.setCountry(locale.getCountry());
				createUser.setPhone(cognitoUser.getPhoneNumber());
				createUser.setInviteAcceptStatus(true);
				createUser.setDefaultOrg(true);
				createUser.setUserStatus(true);
				createUser.setInvitedTime(System.currentTimeMillis());
				
				// long ouid = AccountUtil.getUserBean().createUser(org.getOrgId(), createUser);
				
				// user = AccountUtil.getUserBean().getUser(ouid);
			}
		} else {
			org = AccountUtil.getOrgBean().getOrg(user.getOrgId());
		}
		Account account = new Account(org, user);
		
		account.setDeviceType(request.getHeader("X-Device-Type"));
		account.setAppVersion(request.getHeader("X-App-Version"));
		
		return account;
	}

	public static Account getPortalAccount(CognitoUser cognitoUser, long portalId) throws Exception {

		User user = AccountUtil.getUserBean().getPortalUser(cognitoUser.getEmail(), portalId);
		Organization org = null;
		if (user == null) {
			throw new Exception("user not found");
		} else {
			org = AccountUtil.getOrgBean().getOrg(user.getOrgId());
		}
		return new Account(org, user);
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
					if (domain != null) {
						cookie.setDomain(domain.substring(1));
					}
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