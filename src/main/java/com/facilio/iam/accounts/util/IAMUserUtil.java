package com.facilio.iam.accounts.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;

public class IAMUserUtil {

	private static final Logger logger = Logger.getLogger(IAMUserUtil.class.getName());
	

	
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
	
	public static String generateAuthToken(String emailaddress, String password, String appDomain) throws Exception {
		return validateLoginv3(emailaddress, password, appDomain, null, null, null, true);
	}

	public static boolean logOut(long uId, String facilioToken, String userEmail) throws Exception {
		// end user session
		try {
			if (facilioToken != null) {
				return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().endUserSessionv2(uId, userEmail, facilioToken));
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	public static String getEncodedToken(IAMUser user, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getEncodedTokenv2(user, appDomain));
	}

	public static long startUserSession(long uid, String email, String token, String ipAddress, String userAgent, String userType) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().startUserSessionv2(uid, email, token, ipAddress, userAgent, userType));
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
	
	public static void clearUserSessions(long uid, String email) throws Exception {
		FacilioService.runAsService(() -> IAMUtil.getUserBean().clearAllUserSessionsv2(uid, email));
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
	
	public static String validateLoginv3(String emailaddress, String password, String appDomainName, String userAgent, String userType,
			String ipAddress, boolean startUserSession) throws Exception {
       return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().validateAndGenerateTokenV3(emailaddress, password, appDomainName, userAgent, userType, ipAddress, startUserSession));
		
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
	
	public static IAMUser getFacilioUser(long orgId, long uId, String appDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getFacilioUser(orgId, uId, appDomain, true));
	}
	
}
