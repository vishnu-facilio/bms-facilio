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
		user = AccountUtil.getUserBean().getFacilioUser(email);
		account.setUser(user);
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