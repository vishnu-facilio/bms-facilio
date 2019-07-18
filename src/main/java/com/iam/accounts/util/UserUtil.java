package com.iam.accounts.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.transaction.FacilioConnectionPool;

public class UserUtil {

	private static final Logger LOGGER = Logger.getLogger(UserUtil.class.getName());

	public static void inviteUser(long orgId, User user) throws Exception {
		AuthUtill.getTransactionalUserBean().inviteUserv2(orgId, user);
	}

	public static User addSuperAdmin(org.json.simple.JSONObject signupInfo, Long orgId) throws Exception {

		String name = (String) signupInfo.get("name");
		String email = (String) signupInfo.get("email");
		String phone = (String) signupInfo.get("phone");
		String password = (String) signupInfo.get("password");
		String serverName = (String) signupInfo.get("servername");
		String timezone = (String) signupInfo.get("timezone");
		Locale locale = (Locale) signupInfo.get("locale");

		User userObj = AuthUtill.getUserBean().getFacilioUserv2(email);
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
		if ((user != null) && (AccountUtil.getCurrentOrg() != null)) {
			if (AuthUtill.getUserBean().getFacilioUserv2(orgId, currentUserEmail) != null) {
				return AuthUtill.getTransactionalUserBean().inviteUserv2(orgId, user);
			} else {
				throw new AccountException(AccountException.ErrorCode.NOT_PERMITTED,
						"This user is not permitted to do this action.");
			}
		} else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
	}

	public static User resetPassword(String invitetoken, String password) throws Exception {
		return AuthUtill.getUserBean().resetPasswordv2(invitetoken, password);

	}

	public static void sendResetPasswordLink(User user) throws Exception {
		AuthUtill.getTransactionalUserBean(user.getOrgId()).sendResetPasswordLinkv2(user);
	}

	public static boolean changePassword(String password, String userEmail, String newPassword) throws Exception {
		User user = AuthUtill.getUserBean().getFacilioUserv2(userEmail);
		Boolean verifyOldPassword = verifyPassword(userEmail, password);
		if (verifyOldPassword != null && verifyOldPassword) {
			user.setPassword(newPassword);
			AuthUtill.getUserBean().updateUserv2(user);
			return true;
		} else {
			return false;
		}
	}

	private static Boolean verifyPassword(String emailaddress, String password) {
		boolean passwordValid = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(
					"SELECT Users.password,Users.EMAIL FROM Users inner join faciliousers on Users.USERID=faciliousers.USERID WHERE (faciliousers.email = ? or faciliousers.username = ?) and USER_VERIFIED=1");
			pstmt.setString(1, emailaddress);
			pstmt.setString(2, emailaddress);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String storedPass = rs.getString("password");
				String emailindb = rs.getString(2);
				LOGGER.info("Stored : " + storedPass);
				LOGGER.info("UserGiv: " + password);
				if (storedPass.equals(password)) {
					passwordValid = true;
				}
			} else {
				LOGGER.log(Level.INFO, "No records found");
				return null;
			}

		} catch (SQLException | RuntimeException e) {
			LOGGER.log(Level.INFO, "Exception while verifying password, ", e);
		} finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}

		return passwordValid;
	}

	public static User acceptInvite(String inviteToken, String password) throws Exception {
		return AuthUtill.getTransactionalUserBean().acceptInvitev2(inviteToken, password);
	}

	public static String verifyLoginPassword(String userName, String password, String userAgent, String userType,
			String ipAddress) throws Exception {
		return AuthUtill.verifyPasswordv2(userName, password, userAgent, userType, ipAddress, true, false);
	}

	public static User verifyEmail(String invitetoken) throws Exception {
		return AuthUtill.getUserBean().verifyEmailv2(invitetoken);
	}

	public static boolean updateUser(User user, long orgId, String currentUserEmail) throws Exception {
		if (AuthUtill.getUserBean().getFacilioUserv2(orgId, currentUserEmail) != null) {
			return AuthUtill.getUserBean().updateUserv2(user);
		} else {
			throw new AccountException(AccountException.ErrorCode.NOT_PERMITTED,
					"This user is not permitted to do this action.");
		}

	}

	public static boolean deleteUser(User user, long orgId, String currentUserEmail) throws Exception {
		if (AuthUtill.getUserBean().getFacilioUserv2(orgId, currentUserEmail) != null) {
			return AuthUtill.getUserBean().deleteUserv2(user.getOuid());
		} else {
			throw new AccountException(AccountException.ErrorCode.NOT_PERMITTED,
					"This user is not permitted to do this action.");
		}

	}

	public static boolean verifyUser(long userId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getAppOrgUserFields())
				.table(AccountConstants.getAppOrgUserModule().getTableName()).andCustomWhere("ORG_USERID = ?", userId);

		List<Map<String, Object>> props = selectBuilder.get();
		Long ouid = null;
		if (props != null && !props.isEmpty()) {
			Map<String, Object> prop = props.get(0);
			ouid = (Long) prop.get("uid");
		}

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAppUserModule().getTableName()).fields(AccountConstants.getAppUserFields())
				.andCustomWhere("USERID = ?", ouid);
		Map<String, Object> prop = new HashMap<>();
		prop.put("userVerified", true);
		updateBuilder.update(prop);

		return true;
	}
	
	public static boolean changeUserStatus(User user, long orgId, String currentUserEmail) throws Exception {
		if (AuthUtill.getUserBean().getFacilioUserv2(orgId, currentUserEmail) != null) {
			return AuthUtill.getUserBean().updateUserv2(user);
		} else {
			throw new AccountException(AccountException.ErrorCode.NOT_PERMITTED,
					"This user is not permitted to do this action.");
		}
	}
}
