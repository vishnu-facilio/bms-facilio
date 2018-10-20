package com.facilio.fw.auth;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.fw.auth.CognitoUtil.CognitoUser;

public class LoginUtil {

	public static final String IDTOKEN_COOKIE_NAME = "fc.idToken";
	
	public static Account getAccount(CognitoUser cognitoUser, boolean addUserEntryIfNotExists) throws Exception {
		if(cognitoUser==null) {
			return null;
		}
		
		User user = null;
		
		HttpServletRequest request = ServletActionContext.getRequest();
		String currentOrgDomain = FacilioCookie.getUserCookie(request, "fc.currentOrg");
		
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

}