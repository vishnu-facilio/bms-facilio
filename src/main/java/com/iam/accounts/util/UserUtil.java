package com.iam.accounts.util;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.accounts.dto.User;
import com.facilio.aws.util.AwsUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.iam.accounts.dto.Account;
import com.iam.accounts.exceptions.AccountException;
import com.iam.accounts.exceptions.AccountException.ErrorCode;

public class UserUtil {

	private static final Logger logger = Logger.getLogger(UserUtil.class.getName());
	public static final String JWT_DELIMITER = "#";


	public static User addSuperAdmin(org.json.simple.JSONObject signupInfo, Long orgId) throws Exception {

		String name = (String) signupInfo.get("name");
		String email = (String) signupInfo.get("email");
		String phone = (String) signupInfo.get("phone");
		String password = (String) signupInfo.get("password");
		String serverName = (String) signupInfo.get("servername");
		String timezone = (String) signupInfo.get("timezone");
		Locale locale = (Locale) signupInfo.get("locale");

		User userObj = IAMUtil.getUserBean().getFacilioUser(email, orgId, null);
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
		user.setDomainName("app");
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
		IAMUtil.getUserBean().signUpSuperAdminUserv2(orgId, user);
		return user;
	}

	public static long addUser(User user, long orgId, String currentUserEmail) throws Exception {
		if ((user != null) && orgId > 0) {
			if (IAMUtil.getUserBean().getFacilioUser(currentUserEmail, orgId, null) != null) {
				return IAMUtil.getTransactionalUserBean().addUserv2(orgId, user);
			} else {
				throw new AccountException(AccountException.ErrorCode.USER_DOESNT_EXIST_IN_ORG,
						"This user is not permitted to do this action.");
			}
		} else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
	}

	public static User resetPassword(String invitetoken, String password) throws Exception {
		return IAMUtil.getUserBean().resetPasswordv2(invitetoken, password);

	}

	public static boolean changePassword(String password, String newPassword, long uId, long orgId) throws Exception {
		User user = IAMUtil.getUserBean().getFacilioUser(orgId, uId);
		Boolean verifyOldPassword = verifyPasswordv2(user.getEmail(), user.getDomainName(), password);
		if (verifyOldPassword != null && verifyOldPassword) {
			user.setPassword(newPassword);
			IAMUtil.getUserBean().updateUserv2(user);
			return true;
		} else {
			return false;
		}
	}

	public static User acceptInvite(String inviteToken, String password) throws Exception {
		return IAMUtil.getTransactionalUserBean().acceptInvitev2(inviteToken, password);
	}

	public static String verifyLoginPassword(String userName, String password, String userAgent, String userType,
			String ipAddress, String domain, boolean isPortalUser) throws Exception {
		return validateLoginv2(userName, password, userAgent, userType, ipAddress, domain, true, isPortalUser );
	}

	public static User verifyEmail(String invitetoken) throws Exception {
		return IAMUtil.getUserBean().verifyEmailv2(invitetoken);
	}

	public static boolean updateUser(User user, long orgId, String currentUserEmail) throws Exception {
		if (IAMUtil.getUserBean().getFacilioUser(currentUserEmail, orgId, null) != null) {
			return IAMUtil.getUserBean().updateUserv2(user);
		} else {
			throw new AccountException(AccountException.ErrorCode.USER_DOESNT_EXIST_IN_ORG,
					"This user is not permitted to do this action.");
		}

	}

	public static boolean deleteUser(User user, long orgId, String currentUserEmail) throws Exception {
		if (IAMUtil.getUserBean().getFacilioUser(currentUserEmail, orgId, null) != null) {
			user.setOrgId(orgId);
			return IAMUtil.getUserBean().deleteUserv2(user);
		} else {
			throw new AccountException(AccountException.ErrorCode.USER_DOESNT_EXIST_IN_ORG,
					"This user is not permitted to do this action.");
		}

	}

	public static boolean verifyUser(long userId) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(com.iam.accounts.util.IAMAccountConstants.getAccountsUserModule().getTableName()).fields(com.iam.accounts.util.IAMAccountConstants.getAccountsUserFields())
				.andCustomWhere("USERID = ?", userId);
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
		return IAMUtil.getUserBean().validateUserInvitev2(token);
	}
	
	public static boolean resendInvite(long orgId, long userId) throws Exception {
		return IAMUtil.getUserBean().resendInvitev2(orgId, userId);
	}
	
	public static boolean acceptUser(User user) throws Exception {
		return IAMUtil.getUserBean().acceptUserv2(user);
	}
	
	public static boolean disableUser(User user) throws Exception {
		return IAMUtil.getUserBean().disableUserv2(user.getOrgId(), user.getUid());
	}
	
	public static boolean enableUser(User user) throws Exception {
		return IAMUtil.getUserBean().enableUserv2(user.getOrgId(), user.getUid());
	}
	
	public static boolean updateUserPhoto(long uid, long fileId) throws Exception {
		return IAMUtil.getUserBean().updateUserPhoto(uid, fileId);
	}
	
	public static boolean updateUserStatus(User user) throws Exception {
		if(user.getUserStatus()) {
			return IAMUtil.getUserBean().enableUserv2(user.getOrgId(), user.getUid());
		}
		else {
			return IAMUtil.getUserBean().disableUserv2(user.getOrgId(), user.getUid());
		}
	}
	
	public static String generatePermalinkForUrl(String url, long uId, long orgId) throws Exception {
		return IAMUtil.getUserBean().generatePermalinkForURL(url, uId, orgId);
	}
	
	public static boolean verifyPermalinkForUrl(String token, List<String> urls) throws Exception {
		return IAMUtil.getUserBean().verifyPermalinkForURL(token, urls);
	}
	
	public static Account getPermalinkAccount(String token, List<String> urls) throws Exception {
		return IAMUtil.getUserBean().getPermalinkAccount(token, urls);
	}
	
	public static Account verifiyFacilioToken(String idToken, boolean isPortalUser, boolean overrideSessionCheck, String orgDomain)
			throws AccountException {
		System.out.println("verifiyFacilioToken() :idToken :"+idToken);
		try {
			DecodedJWT decodedjwt = validateJWT(idToken, "auth0");
			if(decodedjwt != null) {
				String email = null;
				if (decodedjwt.getSubject().contains(JWT_DELIMITER)) {
					email = decodedjwt.getSubject().split(JWT_DELIMITER)[0];
				}
				else {
					email = decodedjwt.getSubject().split("_")[0];
				}
				Account account = IAMUtil.getUserBean().verifyUserSessionv2(email, idToken, orgDomain);
				if (overrideSessionCheck || account != null) {
					return account;
				} else {
					return null;
				}
			}
			return null;
		}
		catch (AccountException e) {
			throw e;
		}
		catch (Exception e) {
			logger.info("Exception occurred "+e.toString());
			return null;
		}
	}
	
	public static String createJWT(String id, String issuer, String subject, long ttlMillis,boolean isPortalUser) {
		 
		try {
		    Algorithm algorithm = Algorithm.HMAC256("secret");
		    
		    String key = subject + JWT_DELIMITER + System.currentTimeMillis();
		    JWTCreator.Builder builder = JWT.create().withSubject(key)
	        .withIssuer(issuer);
		    builder = builder.withClaim("portaluser", isPortalUser);
		    
		    return builder.sign(algorithm);
		} catch (UnsupportedEncodingException | JWTCreationException exception){
			logger.info("exception occurred while creating JWT "+ exception.toString());
		    //UTF-8 encoding not supported
		}
		return null;
	}

	public static DecodedJWT validateJWT(String token, String issuer) {
		try {
			Algorithm algorithm = Algorithm.HMAC256("secret");
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build(); // Reusable verifier instance

			DecodedJWT jwt = verifier.verify(token);
			System.out.println("\ndecoded " + jwt.getSubject());
			System.out.println("\ndecoded " + jwt.getClaims());

			return jwt;
		} catch (UnsupportedEncodingException | JWTVerificationException exception) {
			logger.info("exception occurred while decoding JWT "+ exception.toString());
			// UTF-8 encoding not supported
			return null;

		}
	}

	public static String generateAuthToken(String emailaddress, String password, String domain) throws Exception {
		return validateLoginv2(emailaddress, password, null, null, null, domain, false, false);
	}

	public static String generateportalAuthToken(String emailaddress, String password, String domain) throws Exception {
		return validateLoginv2(emailaddress, password, null, null, null, domain, false, true);
	}

	public static boolean verifyPasswordv2(String emailAddress, String domain, String password) throws Exception {
		boolean passwordValid = false;
		try {
			if (StringUtils.isEmpty(domain)) {
				domain = "app";
			}
			
			List<FacilioField> fields = new ArrayList<>();
			fields.addAll(IAMAccountConstants.getAccountsUserFields());
			fields.add(IAMAccountConstants.getUserPasswordField());
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table("Account_Users");
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", emailAddress, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.CITY", "city", domain, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("USER_VERIFIED", "userVerified", "1", NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();

			if (CollectionUtils.isNotEmpty(props)) {
				Map<String, Object> result = props.get(0);
				String storedPass = (String)result.get("password");
				if (storedPass.equals(password)) {
					passwordValid = true;
				}
			} else {
				logger.info("No records found for  " + emailAddress);
				throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "User doesn't exists");
			}

		} catch (SQLException | RuntimeException e) {
			logger.info("Exception while verifying password, "+ e.toString());
		} 
		return passwordValid;
	}

	public static String validateLoginv2(String emailaddress, String password, String userAgent, String userType,
			String ipAddress, String domain, boolean startUserSession, boolean isPortalUser) throws Exception {

		if (verifyPasswordv2(emailaddress, domain, password)) {

			User user = IAMUtil.getUserBean().getFacilioUser(emailaddress, -1, domain);
			if (user != null) {
				long uid = user.getUid();
				String jwt = createJWT("id", "auth0", emailaddress,
						System.currentTimeMillis() + 24 * 60 * 60000, isPortalUser);
				if (startUserSession) {
					IAMUtil.getUserBean().startUserSessionv2(uid, emailaddress, jwt, ipAddress, userAgent, userType);
				}
				return jwt;
			}
			throw new AccountException(ErrorCode.EMAIL_ALREADY_EXISTS, "User is deactivated, Please contact admin to activate.");

		}
		throw new AccountException(ErrorCode.EMAIL_ALREADY_EXISTS, "Invalid Password");
	}
	
	public static boolean logOut(long uId, String facilioToken, String userEmail) throws Exception {
		// end user session
		try {
			if (facilioToken != null) {
				return IAMUtil.getUserBean().endUserSessionv2(uId, userEmail, facilioToken);
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	public static String getResetPasswordToken(User user) throws Exception {
		return IAMUtil.getUserBean().getEncodedTokenv2(user);
	}
	
	public static boolean sendResetPasswordLink(User user) throws Exception {
		return IAMUtil.getUserBean().sendResetPasswordLinkv2(user);
	}

	public static List<Map<String, Object>> getUserSessions(long uId, Boolean isActive) throws Exception {
		return IAMUtil.getUserBean().getUserSessionsv2(uId, isActive);
	}
	
	public static User getUser(long uId, long orgId) throws Exception {
		return IAMUtil.getUserBean().getFacilioUser(orgId, uId);
	}

}
