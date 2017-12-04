package com.facilio.fw.auth;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Chain;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;

public class LoginUtil {

	public static final String IDTOKEN_COOKIE_NAME = "fc.idToken";
	
	public static Account getAccount(CognitoUser cognitoUser, boolean addUserEntryIfNotExists) throws Exception {
		
		User user = AccountUtil.getUserBean().getUser(cognitoUser.getEmail());
		Organization org = null;
		
		if (user == null) {
			JSONObject userAttributes = CognitoUtil.getUserAttributes(cognitoUser.getEmail());
			if (userAttributes == null) {
				throw new Exception("This user not associated with any organization.");
			}
			
			String orgName = (String) userAttributes.get("custom:orgName");
			String orgDomain = (String) userAttributes.get("custom:orgDomain");
			if (orgName == null || orgName.equals("")) {
				orgName = orgDomain;
			}
			
			org = AccountUtil.getOrgBean().getOrg(orgDomain);
			if (org == null) {
				Map<String, String> signupInfo = new HashMap<String, String>();
				signupInfo.put("name", cognitoUser.getName());
				signupInfo.put("email", cognitoUser.getEmail());
				signupInfo.put("cognitoId", cognitoUser.getCognitoId());
				signupInfo.put("phone", cognitoUser.getPhoneNumber());
				signupInfo.put("companyname", orgName);
				signupInfo.put("domainname", orgDomain);
				
				FacilioContext signupContext = new FacilioContext();
				signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, signupInfo);
				
				Chain c = FacilioChainFactory.getOrgSignupChain();
				c.execute(signupContext);
				
				org = AccountUtil.getOrgBean().getOrg(orgDomain);
				user = AccountUtil.getUserBean().getUser(cognitoUser.getEmail());
			}
			else {
				HttpServletRequest request = ServletActionContext.getRequest();
				Locale locale = request.getLocale();
				if (locale == null) {
					locale = Locale.US;
				}
				
				Role adminRole = AccountUtil.getRoleBean().getRole(org.getOrgId(), AccountConstants.DefaultRole.ADMINISTRATOR);
				
				User createUser = new User();
				createUser.setName(cognitoUser.getName());
				createUser.setEmail(cognitoUser.getEmail());
				createUser.setCognitoId(cognitoUser.getCognitoId());
				createUser.setUserVerified(true);
				createUser.setTimezone(org.getTimezone());
				createUser.setLanguage(locale.getLanguage());
				createUser.setCountry(locale.getCountry());
				createUser.setPhone(cognitoUser.getPhoneNumber());
				createUser.setRoleId(adminRole.getRoleId());
				createUser.setInviteAcceptStatus(true);
				createUser.setDefaultOrg(true);
				createUser.setUserStatus(true);
				createUser.setInvitedTime(System.currentTimeMillis());
				
				long ouid = AccountUtil.getUserBean().createUser(org.getOrgId(), createUser);
				
				user = AccountUtil.getUserBean().getUser(ouid);
			}
		}
		else {
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