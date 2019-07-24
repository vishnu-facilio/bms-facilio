package com.iam.accounts.util;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.dto.User;
import com.facilio.aws.util.AwsUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.iam.accounts.exceptions.AccountException;

public class UserUtil {

	private static final Logger LOGGER = Logger.getLogger(UserUtil.class.getName());

	public static User addSuperAdmin(org.json.simple.JSONObject signupInfo, Long orgId) throws Exception {

		String name = (String) signupInfo.get("name");
		String email = (String) signupInfo.get("email");
		String phone = (String) signupInfo.get("phone");
		String password = (String) signupInfo.get("password");
		String serverName = (String) signupInfo.get("servername");
		String timezone = (String) signupInfo.get("timezone");
		Locale locale = (Locale) signupInfo.get("locale");

		User userObj = AuthUtill.getUserBean().getFacilioUserv3(email, orgId, null);
		if (userObj != null) {
			throw new AccountException(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS,
					"This user is not permitted to do this action.");
		}

		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setUserVerified(false);
		user.setTimezone(timezone);
		user.setLanguage(locale.getLanguage());
		user.setCountry(locale.getCountry());
		//setting app domain for super admin
		user.setCity("app");
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
		AuthUtill.getUserBean().createUserv2(orgId, user);
		return user;
	}

	public static long addUser(User user, long orgId, String currentUserEmail) throws Exception {
		if ((user != null) && orgId > 0) {
			if (AuthUtill.getUserBean().getFacilioUserv3(currentUserEmail, orgId, null) != null) {
				return AuthUtill.getTransactionalUserBean().addUserv2(orgId, user);
			} else {
				throw new AccountException(AccountException.ErrorCode.USER_DOESNT_EXIST_IN_ORG,
						"This user is not permitted to do this action.");
			}
		} else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
	}

	public static User resetPassword(String invitetoken, String password) throws Exception {
		return AuthUtill.getUserBean().resetPasswordv2(invitetoken, password);

	}

	public static boolean changePassword(String password, String newPassword, long uId, long orgId) throws Exception {
		User user = AuthUtill.getUserBean().getUserv2(orgId, uId);
		Boolean verifyOldPassword = AuthUtill.verifyPasswordv2(user.getEmail(), user.getCity(), password);
		if (verifyOldPassword != null && verifyOldPassword) {
			user.setPassword(newPassword);
			AuthUtill.getUserBean().updateUserv2(user);
			return true;
		} else {
			return false;
		}
	}

	public static User acceptInvite(String inviteToken, String password) throws Exception {
		return AuthUtill.getTransactionalUserBean().acceptInvitev2(inviteToken, password);
	}

	public static String verifyLoginPassword(String userName, String password, String userAgent, String userType,
			String ipAddress, String domain, boolean isPortalUser) throws Exception {
		return AuthUtill.validateLoginv2(userName, password, userAgent, userType, ipAddress, domain, true, isPortalUser );
	}

	public static User verifyEmail(String invitetoken) throws Exception {
		return AuthUtill.getUserBean().verifyEmailv2(invitetoken);
	}

	public static boolean updateUser(User user, long orgId, String currentUserEmail) throws Exception {
		if (AuthUtill.getUserBean().getFacilioUserv3(currentUserEmail, orgId, null) != null) {
			return AuthUtill.getUserBean().updateUserv2(user);
		} else {
			throw new AccountException(AccountException.ErrorCode.USER_DOESNT_EXIST_IN_ORG,
					"This user is not permitted to do this action.");
		}

	}

	public static boolean deleteUser(User user, long orgId, String currentUserEmail) throws Exception {
		if (AuthUtill.getUserBean().getFacilioUserv3(currentUserEmail, orgId, null) != null) {
			return AuthUtill.getUserBean().deleteUserv2(user.getOuid());
		} else {
			throw new AccountException(AccountException.ErrorCode.USER_DOESNT_EXIST_IN_ORG,
					"This user is not permitted to do this action.");
		}

	}

	public static boolean verifyUser(long userId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(com.iam.accounts.util.AccountConstants.getAccountsOrgUserFields())
				.table(com.iam.accounts.util.AccountConstants.getAccountsOrgUserModule().getTableName()).andCustomWhere("ORG_USERID = ?", userId);

		List<Map<String, Object>> props = selectBuilder.get();
		Long ouid = null;
		if (props != null && !props.isEmpty()) {
			Map<String, Object> prop = props.get(0);
			ouid = (Long) prop.get("uid");
		}

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(com.iam.accounts.util.AccountConstants.getAccountsUserModule().getTableName()).fields(com.iam.accounts.util.AccountConstants.getAccountsUserFields())
				.andCustomWhere("USERID = ?", ouid);
		Map<String, Object> prop = new HashMap<>();
		prop.put("userVerified", true);
		if(updateBuilder.update(prop) > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static User validateUserInviteToken(String token) throws Exception {
		return AuthUtill.getUserBean().validateUserInvitev2(token);
	}
	
	public static boolean resendInvite(long orgId, long userId) throws Exception {
		return AuthUtill.getUserBean().resendInvitev2(orgId, userId);
	}
	
	public static boolean acceptUser(User user) throws Exception {
		return AuthUtill.getUserBean().acceptUserv2(user);
	}
	
	public static boolean disableUser(User user) throws Exception {
		return AuthUtill.getUserBean().disableUserv2(user.getOrgId(), user.getUid());
	}
	
	public static boolean enableUser(User user) throws Exception {
		return AuthUtill.getUserBean().enableUserv2(user.getOrgId(), user.getUid());
	}
	
	public static boolean updateUserPhoto(long uid, long fileId) throws Exception {
		return AuthUtill.getUserBean().updateUserPhoto(uid, fileId);
	}
	
	public static boolean updateUserStatus(User user) throws Exception {
		if(user.getUserStatus()) {
			return AuthUtill.getUserBean().enableUserv2(user.getOrgId(), user.getUid());
		}
		else {
			return AuthUtill.getUserBean().disableUserv2(user.getOrgId(), user.getUid());
		}
	}
	
}
