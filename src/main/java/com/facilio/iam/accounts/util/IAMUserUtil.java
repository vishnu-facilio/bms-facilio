package com.facilio.iam.accounts.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.IAMUser.AppType;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import org.json.simple.JSONObject;

public class IAMUserUtil {

	private static final Logger logger = Logger.getLogger(IAMUserUtil.class.getName());
	

	
	public static long addUser(IAMUser user, long orgId) throws Exception {
		if ((user != null) && orgId > 0) {
			return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getTransactionalUserBean().addUserv2(orgId, user));
		} else {
			throw new IllegalArgumentException("User Object cannot be null");
		}
	}

	public static IAMUser resetPassword(String invitetoken, String password) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().resetPasswordv2(invitetoken, password));

	}

	public static boolean changePassword(String password, String newPassword, long uId, long orgId, String userType) throws Exception {
		IAMUser user = AccountUtil.getUserBean().getUser(orgId, uId);
		if(user == null) {
			return false;
		}
		Boolean verifyOldPassword = verifyPasswordv2(user.getEmail(), user.getDomainName(), password, userType, user.getAppTypeEnum());
		if (verifyOldPassword != null && verifyOldPassword) {
			user.setPassword(newPassword);
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
			List<FacilioField> fieldsToBeUpdated = new ArrayList<FacilioField>();
			fieldsToBeUpdated.add(IAMAccountConstants.getUserPasswordField());
			
			FacilioService.runAsService(() -> IAMUtil.getUserBean().updateUserv2(user, fieldsToBeUpdated));
			return true;
		} else {
			return false;
		}
	}

	public static IAMUser acceptInvite(String inviteToken, String password) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getTransactionalUserBean().acceptInvitev2(inviteToken, password));
	}

	public static String verifyLoginPassword(String userName, String password, String userAgent, String userType,
			String ipAddress, String domain, AppType appType) throws Exception {
		return validateLoginv2(userName, password, userAgent, userType, ipAddress, domain, true, appType);
	}
	
	public static String verifyLoginWithoutPassword(String emailaddress, String userAgent, String userType,
			String ipAddress, String domain, AppType appType) throws Exception {
	
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().generateTokenForWithoutPassword(emailaddress, userAgent, userType, ipAddress, domain, true, appType));
	}

	public static IAMUser verifyEmail(String invitetoken) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyEmailv2(invitetoken));
	}

	public static boolean updateUser(IAMUser user, long orgId) throws Exception {
		List<FacilioField> fieldsToBeUpdated = new ArrayList<FacilioField>();
		fieldsToBeUpdated.addAll(IAMAccountConstants.getAccountsUserFields());
		fieldsToBeUpdated.add(IAMAccountConstants.getUserPasswordField());
		
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().updateUserv2(user, fieldsToBeUpdated));
	}

	public static boolean deleteUser(IAMUser user, long orgId) throws Exception {
		user.setOrgId(orgId);
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().deleteUserv2(user));
	}

	public static boolean verifyUser(long userId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyUser(userId));
	}
	
	public static IAMUser validateUserInviteToken(String token) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().validateUserInvitev2(token));
	}
	
	public static boolean acceptUser(IAMUser user) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().acceptUserv2(user));
	}
	
	public static boolean disableUser(IAMUser user) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().disableUserv2(user.getOrgId(), user.getUid()));
	}
	
	public static boolean enableUser(IAMUser user) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().enableUserv2(user.getOrgId(), user.getUid()));
	}
	
	public static String updateUserPhoto(long uid, User user) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().updateUserPhoto(uid, user));
	}
	
	public static boolean updateUserStatus(IAMUser user) throws Exception {
		if(user.getUserStatus()) {
			return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().enableUserv2(user.getOrgId(), user.getUid()));
		}
		else {
			return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().disableUserv2(user.getOrgId(), user.getUid()));
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
	
	public static IAMAccount getPermalinkAccount(String token, List<String> urls) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getPermalinkAccount(token, urls));
	}
	
	public static IAMAccount verifiyFacilioToken(String idToken, boolean overrideSessionCheck, String orgDomain, String portalDomain, String userType, AppType appType)
			throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyFacilioToken(idToken, overrideSessionCheck, orgDomain, portalDomain, userType, appType));
	}
	
	public static String generateAuthToken(String emailaddress, String password, String domain, AppType appType) throws Exception {
			return validateLoginv2(emailaddress, password, null, null, null, domain, true, AppType.SERVICE_PORTAL);
	}

	public static boolean verifyPasswordv2(String emailAddress, String domain, String password, String userType, AppType appType) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyPasswordv2(emailAddress, domain, password, userType, appType));
	}

	public static String validateLoginv2(String emailaddress, String password, String userAgent, String userType,
			String ipAddress, String domain, boolean startUserSession, AppType appType) throws Exception {
       return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().validateAndGenerateToken(emailaddress, password, userAgent, userType, ipAddress, domain, startUserSession, appType));
		
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

	public static String getEncodedToken(IAMUser user) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getEncodedTokenv2(user));
	}

	public static long startUserSession(long uid, String email, String token, String ipAddress, String userAgent, String userType) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().startUserSessionv2(uid, email, token, ipAddress, userAgent, userType));
	}

	public static List<Map<String, Object>> getUserSessions(long uId, Boolean isActive) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getUserSessionsv2(uId, isActive));
	}
	
	public static IAMUser getUser(long uId, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getFacilioUser(orgId, uId));
	}
	
	public static IAMUser getUser(String email, String orgDomain, String portalDomain) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getFacilioUser(email, orgDomain, portalDomain));
	}
	
	public static List<Organization> getUserOrgs(long uId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getOrgsv2(uId));
	}
	
	public static Organization getDefaultOrg(long uId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getDefaultOrgv2(uId));
	}
	
	public static void clearUserSessions(long uid, String email) throws Exception {
		FacilioService.runAsService(() -> IAMUtil.getUserBean().clearAllUserSessionsv2(uid, email));
	}
	
	public static boolean setDefaultOrg(long uid, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().setDefaultOrgv2(uid, orgId));
	}
	public static Map<Long, Map<String, Object>> getUserData(List<Long> uids, long orgId, boolean shouldFetchDeleted) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getUserDataForUids(uids, orgId, shouldFetchDeleted));
	}
	
	public static Map<Long,Map<String, Object>> getIAMOrgUserData(Criteria criteria, long orgId, boolean shouldFetchDeleted) throws Exception {
		return  FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getUserData(criteria, orgId, shouldFetchDeleted));
	}
	
	public static boolean rollbackUserAdded(IAMUser user, long orgId) throws Exception {
		user.setOrgId(orgId);
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().deleteUserv2(user));
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
	
}
