package com.facilio.iam.accounts.util;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.db.criteria.Criteria;
import com.facilio.service.FacilioService;

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

	public static boolean changePassword(String password, String newPassword, long uId, long orgId) throws Exception {
		IAMUser user = IAMUtil.getUserBean().getFacilioUser(orgId, uId);
		if(user == null) {
			return false;
		}
		Boolean verifyOldPassword = verifyPasswordv2(user.getEmail(), user.getDomainName(), password);
		if (verifyOldPassword != null && verifyOldPassword) {
			user.setPassword(newPassword);
			FacilioService.runAsService(() -> IAMUtil.getUserBean().updateUserv2(user));
			return true;
		} else {
			return false;
		}
	}

	public static IAMUser acceptInvite(String inviteToken, String password) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getTransactionalUserBean().acceptInvitev2(inviteToken, password));
	}

	public static String verifyLoginPassword(String userName, String password, String userAgent, String userType,
			String ipAddress, String domain) throws Exception {
		return validateLoginv2(userName, password, userAgent, userType, ipAddress, domain, true);
	}

	public static IAMUser verifyEmail(String invitetoken) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyEmailv2(invitetoken));
	}

	public static boolean updateUser(IAMUser user, long orgId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().updateUserv2(user));
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
	
	public static boolean updateUserPhoto(long uid, long fileId) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().updateUserPhoto(uid, fileId));
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
	
	public static boolean verifyPermalinkForUrl(String token, List<String> urls) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyPermalinkForURL(token, urls));
	}
	
	public static IAMAccount getPermalinkAccount(String token, List<String> urls) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().getPermalinkAccount(token, urls));
	}
	
	public static IAMAccount verifiyFacilioToken(String idToken, boolean overrideSessionCheck, String orgDomain, String portalDomain)
			throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyFacilioToken(idToken, overrideSessionCheck, orgDomain, portalDomain));
	}
	
		public static String generateAuthToken(String emailaddress, String password, String domain) throws Exception {
		return validateLoginv2(emailaddress, password, null, null, null, domain, true);
	}

	public static boolean verifyPasswordv2(String emailAddress, String domain, String password) throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().verifyPasswordv2(emailAddress, domain, password));
	}

	public static String validateLoginv2(String emailaddress, String password, String userAgent, String userType,
			String ipAddress, String domain, boolean startUserSession) throws Exception {
       return FacilioService.runAsServiceWihReturn(() -> IAMUtil.getUserBean().validateAndGenerateToken(emailaddress, password, userAgent, userType, ipAddress, domain, startUserSession));
		
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
	
}
