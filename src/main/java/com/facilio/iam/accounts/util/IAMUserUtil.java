package com.facilio.iam.accounts.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.APIClient;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;



public class IAMUserUtil {

	private static final Logger logger = Logger.getLogger(IAMUserUtil.class.getName());
	
	public static final String JWT_DELIMITER = "#";
	private static Logger log = LogManager.getLogger(IAMUserUtil.class.getName());
	
	public static long addUser(IAMUser user, long orgId, String identifier) throws Exception {
		if ((user != null) && orgId > 0) {
			return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getTransactionalUserBean().addUserv3(orgId, user, identifier));
		} else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
	}

	public static IAMUser resetPassword(String invitetoken, String password , String confirmPassword) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().resetPasswordv2(invitetoken, password,confirmPassword));

	}

	public static SecurityPolicy getUserSecurityPolicy(String email, AppDomain.GroupType groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getUserSecurityPolicy(email, groupType));
	}

	public static SecurityPolicy getUserSecurityPolicy(long uid) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getUserSecurityPolicy(uid));
	}

	public static void validatePasswordWithSecurityPolicy(long uid, String password) throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().validatePasswordWithSecurityPolicy(uid, password));
	}

	public static String cryptWithMD5(String password) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().cryptWithMD5(password));
	}

	public static String isSessionExpired(long uid, long orgId, long sessionId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().isSessionExpired(uid, sessionId));
	}

	public static String isSessionExpired(long uid, long orgId, long sessionId,AppDomain appdomainObj) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().isSessionExpired(uid, sessionId,appdomainObj));
	}
	public static long getSessionExpiry(long uid, long sessionId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getSessionExpiry(uid, sessionId));
	}

	public static String addProxySession(String proxyUsername, String proxiedUserName, long proxiedSessionId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().addProxySession(proxyUsername, proxiedUserName, proxiedSessionId));
	}

	public static boolean changePassword(String password, String newPassword, long uId, long orgId, String userType) throws Exception {
		
		Boolean verifyOldPassword = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getTransactionalUserBean().verifyPassword(orgId, uId, password));

		if (verifyOldPassword != null && verifyOldPassword) {
			IAMUser userToBeUpdated = new IAMUser();
			userToBeUpdated.setUid(uId);
			userToBeUpdated.setPwdLastUpdatedTime(System.currentTimeMillis());
			validatePasswordWithSecurityPolicy(uId, newPassword);
			String encryptedPassword = cryptWithMD5(newPassword);
			userToBeUpdated.setPassword(encryptedPassword);

			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
			List<FacilioField> fieldsToBeUpdated = new ArrayList<FacilioField>();
			fieldsToBeUpdated.add(IAMAccountConstants.getUserPasswordField());
			fieldsToBeUpdated.add(fieldMap.get("pwdLastUpdatedTime"));
			
			FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().updateUserv2(userToBeUpdated, fieldsToBeUpdated));
			FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().savePreviousPassword(uId, encryptedPassword));

			List<Map<String, Object>> userSessionsv2 = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getUserSessionsv2(uId, true));

			String token = "";
			if (CollectionUtils.isNotEmpty(userSessionsv2)) {
				for (Map<String, Object> userSession: userSessionsv2) {
					long id = (Long) userSession.get("id");
					if (AccountUtil.getCurrentAccount().getUserSessionId() == id) {
						token = (String) userSession.get("token");
						break;
					}
				}
			}

			if (StringUtils.isEmpty(token)) {
				logger.error("Session token not matched for " + uId);
			}

			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("TOKEN", "token", token, StringOperators.ISN_T));
			boolean status = IAMUtil.getTransactionalUserBean().endUserSessionv2(uId, criteria);
			if (status) {
				logger.info("Cleared sessions for " + uId);
			}

			return true;
		} else {
			return false;
		}
	}

	public static IAMUser acceptInvite(String inviteToken, String password) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getTransactionalUserBean().acceptInvitev2(inviteToken, password));
	}

	public static String verifyLoginPasswordv3(String userName, String password, String appDomainName, String userAgent, String userType,
			String ipAddress) throws Exception {
		return validateLoginv3(userName, password, appDomainName, userAgent, userType, ipAddress,true);
	}
	
	public static String verifyLoginWithoutPassword(String emailaddress, String userAgent, String userType,
													String ipAddress, String appDomain, IAMAccountConstants.SocialLogin socialLogin) throws Exception {
	
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().generateTokenForWithoutPassword(emailaddress, userAgent, userType, ipAddress, true, appDomain, socialLogin));
	}

	public static Map<String, Object> generatePropsForWithoutPassword(String emailaddress, String userAgent, String userType,
																	  String ipAddress, String appDomain, boolean isProxySession) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().generatePropsForWithoutPassword(emailaddress, userAgent, userType, ipAddress, appDomain, isProxySession));
	}

	public static IAMUser verifyEmail(String invitetoken) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().verifyEmailv2(invitetoken));
	}

	public static void updateUserMfaSettingsStatus(long userId,boolean value) throws Exception{
		FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().updateUserMfaSettingsStatus(userId,value));
	}

	public static boolean updateUser(IAMUser user, long orgId) throws Exception {
		List<FacilioField> fieldsToBeUpdated = new ArrayList<FacilioField>();
		fieldsToBeUpdated.addAll(IAMAccountConstants.getAccountsUserFields());
		fieldsToBeUpdated.add(IAMAccountConstants.getUserPasswordField());
		
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().updateUserv2(user, fieldsToBeUpdated));
	}

	public static boolean deleteUser(long userId, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().deleteUserv2(userId, orgId));
	}

	public static boolean verifyUser(long userId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().verifyUser(userId));
	}
	
	public static IAMUser validateUserInviteToken(String token) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().validateUserInvitev2(token));
	}
	
	public static boolean acceptUser(IAMUser user) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().acceptUserv2(user));
	}
	
	public static String updateUserPhoto(long uid, User user) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().updateUserPhoto(uid, user));
	}

	public static boolean deleteUserPhoto(long uid, long photoId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().deleteUserPhoto(uid, photoId));
	}
	
	public static String generatePermalinkForUrl(String url, long uId, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().generatePermalinkForURL(url, uId, orgId));
	}

	public static String generatePermalink(long uId, long orgId, JSONObject sessionInfo) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().generatePermalinkForURL(uId, orgId, sessionInfo));
	}
	
	public static boolean verifyPermalinkForUrl(String token, List<String> urls) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().verifyPermalinkForURL(token, urls));
	}
	
	public static IAMAccount getPermalinkAccount(String token, List<String> urls) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getPermalinkAccount(token, urls));
	}
	
	public static String generateAuthToken(String username, String password, String appDomain) throws Exception {
		return validateLoginv3(username, password, appDomain, null, null, null, true);
	}

	public static boolean isUserInProxyList(String username) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().isUserInProxyList(username));
	}

	public static boolean logOut(long uId, String facilioToken) throws Exception {
		// end user session
		try {
			if (facilioToken != null) {
				return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().endUserSessionv2(uId, facilioToken));
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	public static String getEncodedToken(IAMUser user) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getEncodedTokenv2(user));
	}

	public static long startUserSession(long uid, String token, String ipAddress, String userAgent, String userType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().startUserSessionv2(uid, token, ipAddress, userAgent, userType));
	}

	public static List<Map<String, Object>> getUserSessions(long uId, Boolean isActive) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getUserSessionsv2(uId, isActive));
	}
	
	public static Organization getDefaultOrg(long uId) throws Exception {
	//	return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getDefaultOrgv2(uId));
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getDefaultOrgv3(uId));
	}

	public static Organization getDefaultOrg(long uId, Organization.OrgType orgType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getDefaultOrgv3(uId, orgType));
	}
	
	public static void clearUserSessions(long uid) throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().clearAllUserSessionsv2(uid));
	}
	
	public static boolean setDefaultOrg(long uid, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().setDefaultOrgv2(uid, orgId));
	}
	
	public static boolean rollbackUserAdded(long userId, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().deleteUserv2(userId, orgId));
	}

	public static boolean addUserMobileSettings(UserMobileSetting userMobileSetting) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().addUserMobileSetting(userMobileSetting));
	}

	public static int updateUserMobileFcmToken(UserMobileSetting userMobileSetting, FacilioModule module,HashMap<String,Object> updateValue) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().updateUserMobileFcmToken(userMobileSetting,module,updateValue));
	}
	
	public static boolean removeUserMobileSettings(String mobileInstanceId, boolean isFromPortal) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().removeUserMobileSetting(mobileInstanceId, isFromPortal));
	}

	public static boolean removeUserMobileSetting(String mobileInstanceId, String appLinkName) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().removeUserMobileSetting(mobileInstanceId, appLinkName));
	}

	public static List<UserMobileSetting> getUserMobileSettingInstanceIds(List<Long> uIds, String appLinkName) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getMobileInstanceIds(uIds,appLinkName));
	}

	public static List<UserMobileSetting> getUserMobileSettings(List<Long> mobileInstanceIds) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getUserMobileInstance(mobileInstanceIds));
	}

	public static Organization getOrg(String currentOrgDomain, long uId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getOrgv2(currentOrgDomain, uId));
	}

	public static Organization getOrg(String currentOrgDomain, long uId, Organization.OrgType orgType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getOrgv2(currentOrgDomain, uId, orgType));
	}

	public static List<Organization> getOrgsForUser(long uId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getOrgsv2(uId));
	}

	public static List<Organization> getOrgsForUser(long uId, Organization.OrgType orgType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getOrgsv2(uId, orgType));
	}
	
	public static Object getPermalinkDetails(String token) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getPermalinkDetails(token));
	}
	
	public static String validateLoginv3(String username, String password, String appDomainName, String userAgent, String userType,
			String ipAddress, boolean startUserSession) throws Exception {
       return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().validateAndGenerateTokenV3(username, password, appDomainName, userAgent, userType, ipAddress, startUserSession));
		
	}

	public static Map<String, Object> validateAndGenerateTokenV3(String emailaddress, String password, String appDomainName, String userAgent, String userType,
																		String ipAddress) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().validateAndGenerateTokenV3(emailaddress, password, appDomainName, userAgent, userType, ipAddress));
	}

	public static long verifyPasswordv3(String username, String password, String appDomainName, String userType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().verifyPasswordv3(username, password, appDomainName, userType));
	}

	public static String getEmailFromDigest(String digest) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getEmailFromDigest(digest));
	}

	public static String getEmailFromDigest(String digest, IAMAccountConstants.SessionType sessionType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getEmailFromDigest(digest, sessionType));
	}

	public static String validateDigestAndDomain(String domain, String digest, AppDomain.GroupType groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().validateDigestAndDomain(domain, digest, groupType));
	}
	
	public static IAMAccount verifiyFacilioTokenv3(String idToken, boolean overrideSessionCheck, String userType)
			throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().verifyFacilioTokenv3(idToken, overrideSessionCheck, userType));
	}

	public static List<IAMUser> getUserDatav3(String uids, long orgId, boolean shouldFetchDeleted) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getUserDataForUidsv3(uids, orgId, shouldFetchDeleted));
	}
	
	public static Map<String, Object> getUserForPhone(String phone, String identifier, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getUserForPhone(phone, orgId, identifier));
	}
	
	public static Map<String, Object> getUserForEmail(String email, String identifier, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getUserForEmail(email, orgId, identifier));
	}

	public static Map<String, Object> getUserForEmail(String email, String identifier, long orgId, boolean fetchInactive) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getUserForEmail(email, orgId, identifier, fetchInactive));
	}
	
	public static Map<String, Object> getUserForUsername(String username, long orgId, String identifier) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getUserForUsername(username, orgId, identifier));
	}

	public static Map<String, Object> getLoginModes(String userName, String domain, AppDomain appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getLoginModes(userName, domain, appDomain));
	}

	public static Map<String, Object> getLoginModes(String userName, AppDomain.AppDomainType appDomainType, AppDomain.GroupType groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getLoginModes(userName, appDomainType, groupType));
	}

	public static String generateTotpSessionToken(String userName, String token) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().generateTotpSessionToken(userName, token));
	}

	public static String generateMFAConfigSessionToken(String userName, String token) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().generateMFAConfigSessionToken(userName, token));
	}

	public static String generateProxyUserSessionToken(String proxyUser) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().generateProxyUserSessionToken(proxyUser));
	}

	public static String decodeProxyUserToken(String token) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().decodeProxyUserToken(token));
	}

	public static IAMUser resetExpiredPassword(String digest, String password,String confirmPassword) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().resetExpiredPassword(digest, password,confirmPassword));
	}

	public static String generatePWDPolicyPWDResetToken(String userName, AppDomain.GroupType groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().generatePWDPolicyPWDResetToken(userName, groupType));
	}
	
	public static boolean disableUser(long userId, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().disableUser(orgId, userId));
	}
	
	public static boolean enableUser(long userId, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().enableUser(orgId, userId));
	}
	
	public static IAMUser getFacilioUser(long orgId, long uId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getFacilioUser(orgId, uId, true));
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

	public static void setIAMUserPropsv3(List<Map<String, Object>> actualPropsList, long orgId, boolean shouldFetchDeleted) throws Exception {
		if(CollectionUtils.isNotEmpty(actualPropsList)) {
			List<Map<String, Object>> finalMap = new ArrayList<Map<String,Object>>();
			StringJoiner userIds = new StringJoiner(",");
			for(Map<String, Object> map : actualPropsList) {
				userIds.add(String.valueOf((long)map.get("iamOrgUserId")));
			}
			List<IAMUser> iamUsers = getIAMUserPropsv3(userIds.toString(), orgId, shouldFetchDeleted);
			if (CollectionUtils.isNotEmpty(iamUsers)) {
				for(Map<String, Object> map : actualPropsList) {
					long iamOrgUserId = (long)map.get("iamOrgUserId");
					List<IAMUser> result = iamUsers.stream()  
	                .filter(user -> user.getIamOrgUserId() == iamOrgUserId)     
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
	
	public static List<IAMUser> getIAMUserPropsv3(String userIds, long orgId, boolean shouldFetchDeleted) throws Exception {
		return getUserDatav3(userIds, orgId, shouldFetchDeleted);
	}
	
	public static Map<String, Object> getUserFromPhone(String phone, String identifier, long orgId) throws Exception {
		return getUserForPhone(phone, identifier, orgId);
	}

	public static Map<String, Object> getUserMfaSettings(String username, AppDomain.GroupType groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getUserMfaSettings(username, groupType));
	}

	public static Map<String,Object> getUserMfaSettings(long userId) throws Exception{
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getUserMfaSettings(userId));
	}

	public static boolean updateUserMfaSettingsSecretKey(long userId,String value) throws Exception{
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().updateUserMfaSettingsSecretKey(userId, value) );
	}

	public static boolean clearUserMfaSettings(long userId) throws Exception{
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().clearUserMfaSettings(userId));
	}

	public static  List<Map<String, Object>> getUserData(String username, AppDomain.GroupType groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> IAMUtil.getUserBean().getUserData(username, groupType));
	}

	// IAM client
	public static Integer lookupUserDC(String userName, GroupType groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().lookupUserDC(userName, groupType));
	}

	// IAM
	public static Integer findDCForUser(String username, GroupType groupType) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().findDCForUser(username, groupType));
	}

	public static void deleteDCLookup(String username, GroupType groupType) throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().deleteDCLookup(username, groupType));
	}
	
	public static long addDCLookup(Map<String, Object> props) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getTransactionalUserBean().addDCLookup(props));
	}

	public static List<Map<String, Object>> getUserData(String username, long orgId, String identifier) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getUserData(username, orgId, identifier));
	}

	public static IAMAccount fetchAccountByOauth2ClientId(String oauth2ClientId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().fetchAccountByOauth2ClientId(oauth2ClientId));
	}

	public static IAMAccount fetchAccountByAPIKey(String apiKey) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().fetchAccountByAPIKey(apiKey));
	}

	public static String createApiKey(long orgId, long userId, String name) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().createApiKey(orgId, userId, name));
	}

	public static Map<String, String> createOauth2Client(long orgId, long userId, String name) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().createOauth2Client(orgId, userId, name));
	}

	public static IAMAccount verifyProxyToken(String proxyToken, IAMAccount proxyUser) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().verifyProxyToken(proxyToken, proxyUser));
	}

	public static Map<String, Object> getUserSession(long sessionId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getUserSession(sessionId));
	}

	public static Map<String, Object> getProxySession(String proxyToken) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getProxySession(proxyToken));
	}

	public static void deleteApiClient(long orgId, long userId, long id) throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().deleteApiClient(orgId, userId, id));
	}

	public static List<APIClient> getApiClientList(long orgId, long userId) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().getApiClientList(orgId, userId));
	}

	public static boolean totpChecking(String code, long uid) throws Exception{
		Map<String, Object> values = getUserMfaSettings(uid);
		String totpKey = (String) values.get("totpSecret");

		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

		return verifier.isValidCode(totpKey,code);
	}
}
