package com.iam.accounts.util;

import java.util.Locale;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;

public class UserUtil {

	public static void inviteUser(long orgId, User user) throws Exception {
		AuthUtill.getTransactionalUserBean().inviteUserv2(orgId, user);
	}

		public static Long addSuperAdmin(org.json.simple.JSONObject signupInfo, Long orgId) throws Exception {

		String name = (String) signupInfo.get("name");
		String email = (String) signupInfo.get("email");
		String phone = (String) signupInfo.get("phone");
		String password = (String) signupInfo.get("password");
		String serverName = (String) signupInfo.get("servername");
		String timezone = (String) signupInfo.get("timezone");
		Locale locale = (Locale) signupInfo.get("locale");

		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setUserVerified(false);
		user.setTimezone(timezone);
		user.setLanguage(locale.getLanguage());
		user.setCountry(locale.getCountry());
		if (phone != null) {
			user.setPhone(phone);
		}
		user.setInviteAcceptStatus(true);
		user.setDefaultOrg(true);
		user.setUserStatus(true);
		user.setInvitedTime(System.currentTimeMillis());
		user.setPassword(password);
		user.setServerName(serverName);
		if (AwsUtil.isDevelopment()) {
			user.setUserVerified(true);
		}
		long ouid = AuthUtill.getUserBean().createUserv2(orgId, user);
		return ouid;
	}

	public static long addUser(User user, long orgId) throws Exception {
		if ((user != null) && (AccountUtil.getCurrentOrg() != null)) {
			return AuthUtill.getTransactionalUserBean().inviteUserv2(orgId, user);
		} else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
	}

}
