package com.facilio.fw.auth;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.iam.accounts.dto.Account;

public class LoginUtil {

	public static final String IDTOKEN_COOKIE_NAME = "fc.idToken";
	
	public static void updateAccount(Account account, boolean addUserEntryIfNotExists) throws Exception {
		if(account == null) {
			return;
		}
		
		if (account.getUser() == null || account.getOrg() == null) {
			return;
		}
		
		String email = account.getUser().getEmail();
		
		User user = null;
		
//		HttpServletRequest request = ServletActionContext.getRequest();
//		String currentOrgDomain = FacilioCookie.getUserCookie(request, "fc.currentOrg");
//		if (currentOrgDomain == null) {
//			currentOrgDomain = request.getHeader("X-Current-Org"); 
//		}
		
//		if (currentOrgDomain != null) {
//			user = AccountUtil.getUserBean().getFacilioUser(email, currentOrgDomain, null);
//		}

//		if (user == null) {
			user = AccountUtil.getUserBean().getFacilioUser(email);
//		}
		
//		Organization org = null;
		
		/*if (user == null) {
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
				createUser.setTimezone(AccountUtil.getCurrentAccount().getTimeZone());
				createUser.setLanguage(locale.getLanguage());
				createUser.setCountry(locale.getCountry());
				createUser.setPhone(cognitoUser.getPhoneNumber());
				createUser.setInviteAcceptStatus(true);
				createUser.setDefaultOrg(true);
				createUser.setUserStatus(true);
				createUser.setInvitedTime(System.currentTimeMillis());
				
			}
		} else { */
//			org = AccountUtil.getOrgBean().getOrg(user.getOrgId());
		//}
//		Account account = new Account(org, user);
		account.setUser(user);
		
//		return account;
	}

	public static Account getPortalAccount(String email, long portalId) throws Exception {
		User user = AccountUtil.getUserBean().getPortalUsers(email, portalId);
		Organization org = null;
		if (user == null) {
			throw new Exception("user not found");
		} else {
			org = AccountUtil.getOrgBean().getOrg(user.getOrgId());
		}
		return new Account(org, user);
	}

}