package com.facilio.iam.accounts.util;

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
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.aws.util.AwsUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.modules.fields.FacilioField;

public class IAMUserUtil {

	private static final Logger logger = Logger.getLogger(IAMUserUtil.class.getName());
	public static final String JWT_DELIMITER = "#";


	public static IAMUser addSuperAdmin(org.json.simple.JSONObject signupInfo, Long orgId) throws Exception {

		String name = (String) signupInfo.get("name");
		String email = (String) signupInfo.get("email");
		String phone = (String) signupInfo.get("phone");
		String password = (String) signupInfo.get("password");
		String timezone = (String) signupInfo.get("timezone");
		Locale locale = (Locale) signupInfo.get("locale");

		IAMUser userObj = IAMUtil.getUserBean().getFacilioUser(email, orgId, null);
		if (userObj != null) {
			throw new AccountException(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS,
					"This user is not permitted to do this action.");
		}

		IAMUser user = new IAMUser();
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
		user.setDefaultOrg(true);
		user.setUserStatus(true);
		user.setPassword(password);
		if (AwsUtil.isDevelopment()) {
			user.setUserVerified(true);
		}
		IAMUtil.getUserBean().signUpSuperAdminUserv2(orgId, user);
		return user;
	}

	public static long addUser(IAMUser user, long orgId) throws Exception {
		if ((user != null) && orgId > 0) {
				return IAMUtil.getTransactionalUserBean().addUserv2(orgId, user);
		} else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
	}

	public static IAMUser resetPassword(String invitetoken, String password) throws Exception {
		return IAMUtil.getUserBean().resetPasswordv2(invitetoken, password);

	}

	public static boolean changePassword(String password, String newPassword, long uId, long orgId) throws Exception {
		IAMUser user = IAMUtil.getUserBean().getFacilioUser(orgId, uId);
		Boolean verifyOldPassword = verifyPasswordv2(user.getEmail(), user.getDomainName(), password);
		if (verifyOldPassword != null && verifyOldPassword) {
			user.setPassword(newPassword);
			IAMUtil.getUserBean().updateUserv2(user);
			return true;
		} else {
			return false;
		}
	}

	public static IAMUser acceptInvite(String inviteToken, String password) throws Exception {
		return IAMUtil.getTransactionalUserBean().acceptInvitev2(inviteToken, password);
	}

	public static String verifyLoginPassword(String userName, String password, String userAgent, String userType,
			String ipAddress, String domain) throws Exception {
		return validateLoginv2(userName, password, userAgent, userType, ipAddress, domain, true);
	}

	public static IAMUser verifyEmail(String invitetoken) throws Exception {
		return IAMUtil.getUserBean().verifyEmailv2(invitetoken);
	}

	public static boolean updateUser(IAMUser user, long orgId) throws Exception {
			return IAMUtil.getUserBean().updateUserv2(user);
	}

	public static boolean deleteUser(IAMUser user, long orgId) throws Exception {
			user.setOrgId(orgId);
			return IAMUtil.getUserBean().deleteUserv2(user);
	}

	public static boolean verifyUser(long userId) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(com.facilio.iam.accounts.util.IAMAccountConstants.getAccountsUserModule().getTableName()).fields(com.facilio.iam.accounts.util.IAMAccountConstants.getAccountsUserFields())
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
	
	public static IAMUser validateUserInviteToken(String token) throws Exception {
		return IAMUtil.getUserBean().validateUserInvitev2(token);
	}
	
	public static boolean acceptUser(IAMUser user) throws Exception {
		return IAMUtil.getUserBean().acceptUserv2(user);
	}
	
	public static boolean disableUser(IAMUser user) throws Exception {
		return IAMUtil.getUserBean().disableUserv2(user.getOrgId(), user.getUid());
	}
	
	public static boolean enableUser(IAMUser user) throws Exception {
		return IAMUtil.getUserBean().enableUserv2(user.getOrgId(), user.getUid());
	}
	
	public static boolean updateUserPhoto(long uid, long fileId) throws Exception {
		return IAMUtil.getUserBean().updateUserPhoto(uid, fileId);
	}
	
	public static boolean updateUserStatus(IAMUser user) throws Exception {
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
	
	public static IAMAccount getPermalinkAccount(String token, List<String> urls) throws Exception {
		return IAMUtil.getUserBean().getPermalinkAccount(token, urls);
	}
	
	public static IAMAccount verifiyFacilioToken(String idToken, boolean overrideSessionCheck, String orgDomain, String portalDomain)
			throws AccountException {
		System.out.println("verifiyFacilioToken() :idToken :"+idToken);
		try {
			DecodedJWT decodedjwt = validateJWT(idToken, "auth0");
			if(decodedjwt != null) {
				String uId = null;
				if (decodedjwt.getSubject().contains(JWT_DELIMITER)) {
					uId = decodedjwt.getSubject().split(JWT_DELIMITER)[0];
				}
				else {
					uId = decodedjwt.getSubject().split("_")[0];
				}
				IAMAccount account = null;
				try {
					Long.parseLong(uId);
					account = IAMUtil.getUserBean().verifyUserSessionv2(uId, idToken, orgDomain);
				}
				catch(NumberFormatException e) {
					account = IAMUtil.getUserBean().verifyUserSessionUsingEmail(uId, idToken, portalDomain);
				}
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
	
	public static String createJWT(String id, String issuer, String subject, long ttlMillis) {
		 
		try {
		    Algorithm algorithm = Algorithm.HMAC256("secret");
		    
		    String key = subject + JWT_DELIMITER + System.currentTimeMillis();
		    JWTCreator.Builder builder = JWT.create().withSubject(key)
	        .withIssuer(issuer);
		    
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
		return validateLoginv2(emailaddress, password, null, null, null, domain, false);
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
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.DOMAIN_NAME", "domainName", domain, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("USER_VERIFIED", "userVerified", "1", NumberOperators.EQUALS));
			
			logger.info("Domain  " + domain);
			logger.info("Email Address  " + emailAddress);
			logger.info("PAssword  " + password);
			
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
			String ipAddress, String domain, boolean startUserSession) throws Exception {

		if (verifyPasswordv2(emailaddress, domain, password)) {

			IAMUser user = IAMUtil.getUserBean().getFacilioUser(emailaddress, -1, domain);
			if (user != null) {
				long uid = user.getUid();
				String jwt = createJWT("id", "auth0", String.valueOf(user.getUid()),
						System.currentTimeMillis() + 24 * 60 * 60000);
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

	public static String getEncodedToken(IAMUser user) throws Exception {
		return IAMUtil.getUserBean().getEncodedTokenv2(user);
	}
	
	public static List<Map<String, Object>> getUserSessions(long uId, Boolean isActive) throws Exception {
		return IAMUtil.getUserBean().getUserSessionsv2(uId, isActive);
	}
	
	public static IAMUser getUser(long uId, long orgId) throws Exception {
		return IAMUtil.getUserBean().getFacilioUser(orgId, uId);
	}
	
	public static IAMUser getUser(String email, String orgDomain, String portalDomain) throws Exception {
		return IAMUtil.getUserBean().getFacilioUser(email, orgDomain, portalDomain);
	}
	
	public static List<Organization> getUserOrgs(long uId) throws Exception {
		return IAMUtil.getUserBean().getOrgsv2(uId);
	}
	
	public static Organization getDefaultOrg(long uId) throws Exception {
		return IAMUtil.getUserBean().getDefaultOrgv2(uId);
	}
	
	public static void clearUserSessions(long uid, String email) throws Exception {
		IAMUtil.getUserBean().clearAllUserSessionsv2(uid, email);
	}
}
