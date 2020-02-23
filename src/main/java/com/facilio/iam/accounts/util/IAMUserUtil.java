package com.facilio.iam.accounts.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;



public class IAMUserUtil {

	private static final Logger logger = Logger.getLogger(IAMUserUtil.class.getName());
	
	public static final String JWT_DELIMITER = "#";
	private static Logger log = LogManager.getLogger(IAMUserUtil.class.getName());
	
	public static long addUser(IAMUser user, long orgId, int identifier, String appDomain) throws Exception {
		if ((user != null) && orgId > 0) {
			return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getTransactionalUserBean().addUserv3(orgId, user, identifier, appDomain));
		} else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
	}

	public static IAMUser resetPassword(String invitetoken, String password, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().resetPasswordv2(invitetoken, password, appDomain));

	}

	public static boolean changePassword(String password, String newPassword, long uId, long orgId, String userType, String appDomain) throws Exception {
		
		Boolean verifyOldPassword = FacilioService.runAsServiceWihReturn(() -> IAMUtil.getTransactionalUserBean().verifyPassword(orgId, uId, password, appDomain));

		if (verifyOldPassword != null && verifyOldPassword) {
			IAMUser userToBeUpdated = new IAMUser();
			userToBeUpdated.setPassword(newPassword);
			userToBeUpdated.setUid(uId);
			List<FacilioField> fieldsToBeUpdated = new ArrayList<FacilioField>();
			fieldsToBeUpdated.add(IAMAccountConstants.getUserPasswordField());
			
			FacilioService.runAsService(() -> IAMUtil.getUserBean().updateUserv2(userToBeUpdated, fieldsToBeUpdated));
			return true;
		} else {
			return false;
		}
	}

	public static IAMUser acceptInvite(String inviteToken, String password, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getTransactionalUserBean().acceptInvitev2(inviteToken, password, appDomain));
	}

	public static String verifyLoginPasswordv3(String userName, String password, String appDomainName, String userAgent, String userType,
			String ipAddress) throws Exception {
		return validateLoginv3(userName, password, appDomainName, userAgent, userType, ipAddress,true);
	}
	
	public static String verifyLoginWithoutPassword(String emailaddress, String userAgent, String userType,
			String ipAddress, String domain, AppType appType) throws Exception {
	
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().generateTokenForWithoutPassword(emailaddress, userAgent, userType, ipAddress, domain, true, appType));
	}

	public static IAMUser verifyEmail(String invitetoken, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyEmailv2(invitetoken, appDomain));
	}

	public static boolean updateUser(IAMUser user, long orgId) throws Exception {
		List<FacilioField> fieldsToBeUpdated = new ArrayList<FacilioField>();
		fieldsToBeUpdated.addAll(IAMAccountConstants.getAccountsUserFields());
		fieldsToBeUpdated.add(IAMAccountConstants.getUserPasswordField());
		
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().updateUserv2(user, fieldsToBeUpdated));
	}

	public static boolean deleteUser(long userId, long orgId, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().deleteUserv2(userId, orgId, appDomain));
	}

	public static boolean verifyUser(long userId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyUser(userId));
	}
	
	public static IAMUser validateUserInviteToken(String token, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().validateUserInvitev2(token, appDomain));
	}
	
	public static boolean acceptUser(IAMUser user, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().acceptUserv2(user, appDomain));
	}
	
	public static boolean disableUser(long userId, long orgId, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().disableUserv2(orgId, userId, appDomain));
	}
	
	public static boolean enableUser(long userId, long orgId, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().enableUserv2(orgId, userId, appDomain));
	}
	
	public static String updateUserPhoto(long uid, User user) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().updateUserPhoto(uid, user));
	}
	
	public static boolean updateUserStatus(IAMUser user, String appDomain) throws Exception {
		if(user.getUserStatus()) {
			return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().enableUserv2(user.getOrgId(), user.getUid(), appDomain));
		}
		else {
			return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().disableUserv2(user.getOrgId(), user.getUid(), appDomain));
		}
	}
	
	public static String generatePermalinkForUrl(String url, long uId, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().generatePermalinkForURL(url, uId, orgId));
	}

	public static String generatePermalink(long uId, long orgId, JSONObject sessionInfo) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().generatePermalinkForURL(uId, orgId, sessionInfo));
	}
	
	public static boolean verifyPermalinkForUrl(String token, List<String> urls) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyPermalinkForURL(token, urls));
	}
	
	public static IAMAccount getPermalinkAccount(String token, List<String> urls, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getPermalinkAccount(token, urls, appDomain));
	}
	
	public static String generateAuthToken(String username, String password, String appDomain) throws Exception {
		return validateLoginv3(username, password, appDomain, null, null, null, true);
	}

	public static boolean logOut(long uId, String facilioToken) throws Exception {
		// end user session
		try {
			if (facilioToken != null) {
				return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().endUserSessionv2(uId, facilioToken));
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	public static String getEncodedToken(IAMUser user, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getEncodedTokenv2(user, appDomain));
	}

	public static long startUserSession(long uid, String token, String ipAddress, String userAgent, String userType) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().startUserSessionv2(uid, token, ipAddress, userAgent, userType));
	}

	public static List<Map<String, Object>> getUserSessions(long uId, Boolean isActive) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getUserSessionsv2(uId, isActive));
	}
	
	public static List<Organization> getUserOrgs(long uId, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getOrgsv2(uId, appDomain));
	}
	
	public static Organization getDefaultOrg(long uId, String appDomain) throws Exception {
	//	return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getDefaultOrgv2(uId));
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getDefaultOrgv3(uId, appDomain));
	}
	
	public static void clearUserSessions(long uid) throws Exception {
		FacilioService.runAsService(() -> IAMUtil.getUserBean().clearAllUserSessionsv2(uid));
	}
	
	public static boolean setDefaultOrg(long uid, long orgId, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().setDefaultOrgv2(uid, orgId, appDomain));
	}
	
	public static boolean rollbackUserAdded(long userId, long orgId, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().deleteUserv2(userId, orgId, appDomain));
	}
	
	public static boolean addUserMobileSettings(UserMobileSetting userMobileSetting) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().addUserMobileSetting(userMobileSetting));
	}
	
	public static boolean removeUserMobileSettings(String mobileInstanceId, boolean isFromPortal) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().removeUserMobileSetting(mobileInstanceId, isFromPortal));
	}
	
	public static List<Map<String, Object>> getUserMobileSettingInstanceIds(List<Long> uIds) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getMobileInstanceIds(uIds));
	}
	
	public static Organization getOrg(String currentOrgDomain, long uId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getOrgv2(currentOrgDomain, uId));
	}
	
	public static Object getPermalinkDetails(String token) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getPermalinkDetails(token));
	}
	
	public static AppDomain getAppDomain(String domain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getAppDomain(domain));
	}
	
	public static String validateLoginv3(String username, String password, String appDomainName, String userAgent, String userType,
			String ipAddress, boolean startUserSession) throws Exception {
       return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().validateAndGenerateTokenV3(username, password, appDomainName, userAgent, userType, ipAddress, startUserSession));
		
	}
	
	public static IAMAccount verifiyFacilioTokenv3(String idToken, boolean overrideSessionCheck, String orgDomain, String appDomain, String userType)
			throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyFacilioTokenv3(idToken, overrideSessionCheck, appDomain, userType, orgDomain));
	}

	public static List<IAMUser> getUserDatav3(String uids, long orgId, boolean shouldFetchDeleted, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getUserDataForUidsv3(uids, orgId, shouldFetchDeleted, appDomain));
	}
	
	public static Map<String, Object> getUserForEmailOrPhone(String email, String appDomain, boolean isPhone, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getUserForEmailOrPhone(email, appDomain, isPhone, orgId));
	}
	
	public static Map<String, Object> getUserForUsername(String username, String appDomain, boolean isPhone, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getUserForUsername(username, appDomain, orgId));
	}
	
	
	public static IAMUser getFacilioUser(long orgId, long uId, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getFacilioUser(orgId, uId, appDomain, true));
	}
	
	public static String createJWT(String id, String issuer, String subject, long ttlMillis) {
		 
		try {
		    Algorithm algorithm = Algorithm.HMAC256("secret");
		    
		    String key = subject + JWT_DELIMITER + System.currentTimeMillis();
		    JWTCreator.Builder builder = JWT.create().withSubject(key)
	        .withIssuer(issuer);
		    
		    return builder.sign(algorithm);
		} catch (UnsupportedEncodingException | JWTCreationException exception){
			log.info("exception occurred while creating JWT "+ exception.toString());
		    //UTF-8 encoding not supported
		}
		return null;
	}

	public static void setIAMUserPropsv3(List<Map<String, Object>> actualPropsList, long orgId, boolean shouldFetchDeleted, String appDomain) throws Exception {
		if(CollectionUtils.isNotEmpty(actualPropsList)) {
			List<Map<String, Object>> finalMap = new ArrayList<Map<String,Object>>();
			StringJoiner userIds = new StringJoiner(",");
			for(Map<String, Object> map : actualPropsList) {
				userIds.add(String.valueOf((long)map.get("uid")));
			}
			List<IAMUser> iamUsers = getIAMUserPropsv3(userIds.toString(), orgId, shouldFetchDeleted, appDomain);
			if (CollectionUtils.isNotEmpty(iamUsers)) {
				for(Map<String, Object> map : actualPropsList) {
					long uId = (long)map.get("uid");
					List<IAMUser> result = iamUsers.stream()  
	                .filter(user -> user.getUid() == uId)     
	                .collect(Collectors.toList());
					if(CollectionUtils.isNotEmpty(result)) {
						map.putAll(FieldUtil.getAsProperties(result.get(0)));
						finalMap.add(map);
					}
				}
			}
			actualPropsList.clear();
			actualPropsList.addAll(finalMap);
		}
	}
	
	public static Map<String, Object> getUserFromUsername(String username, String appDomain) throws Exception {
		return getUserForUsername(username, appDomain, false, -1);
	}
	
	public static List<IAMUser> getIAMUserPropsv3(String userIds, long orgId, boolean shouldFetchDeleted, String appDomain) throws Exception {
		return getUserDatav3(userIds, orgId, shouldFetchDeleted, appDomain);
	}
	
	public static Map<String, Object> getUserFromPhone(String phone, String appDomain) throws Exception {
		return getUserForEmailOrPhone(phone, appDomain, true, -1);
	}
	
	public static Map<String, Object> getUserFromEmailOrPhoneForOrg(String emailOrPhone, String appDomain) throws Exception {
		return getUserForEmailOrPhone(emailOrPhone, appDomain, true, AccountUtil.getCurrentOrg().getOrgId());
	}
	
	
}
