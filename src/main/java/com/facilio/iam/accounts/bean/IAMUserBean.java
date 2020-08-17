 package com.facilio.iam.accounts.bean;

import java.util.List;
import java.util.Map;

import com.facilio.iam.accounts.util.IAMAccountConstants;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.modules.fields.FacilioField;

public interface IAMUserBean {
	
	public long addUserv3(long orgId, IAMUser user, String identifier) throws Exception;
		
	
//	public long inviteAdminConsoleUserv2(long orgId, User user) throws Exception;
	
	IAMUser verifyEmailv2(String token) throws Exception;

	IAMUser resetPasswordv2(String token, String password) throws Exception;
	
	IAMUser validateUserInvitev2(String token) throws Exception;

	public IAMUser acceptInvitev2(String token, String password) throws Exception;
	
	public boolean updateUserv2(IAMUser user, List<FacilioField> fields) throws Exception;
	
	public boolean deleteUserv2(long userId, long orgId) throws Exception;

	public boolean setDefaultOrgv2(long uid, long orgId) throws Exception;
	
	public List<Organization> getOrgsv2(long uid) throws Exception;
	
	public Organization getOrgv2(String currentOrgDomain, long uid) throws Exception;
	
	public String updateUserPhoto(long uid, User user) throws Exception;

	public long startUserSessionv2(long uid, String token, String ipAddress, String userAgent, String userType) throws Exception;
    
    public boolean endUserSessionv2(long uid, String token) throws Exception;
    
    public List<Map<String, Object>> getUserSessionsv2(long uid, Boolean isActive) throws Exception;
    
    public void clearUserSessionv2(long uid, String token) throws Exception;
    
    public void clearAllUserSessionsv2(long uid) throws Exception;
    
    public boolean acceptUserv2(IAMUser user) throws Exception;
	
	public String getEncodedTokenv2(IAMUser user) throws Exception;
	
	public IAMUser getFacilioUser(long orgId, long userId, boolean checkStatus) throws Exception;
	
	public String generatePermalinkForURL(String url, long uid, long orgId) throws Exception;

	public String generatePermalinkForURL(long uid, long orgId, JSONObject sessionInfo) throws Exception;
	    
	public boolean verifyPermalinkForURL(String token, List<String> url) throws Exception;

	public IAMAccount getPermalinkAccount(String token, List<String> url) throws Exception ;

	public long signUpSuperAdminUserv3(long orgId, IAMUser user, String identifier) throws Exception;
		
	public boolean verifyUser(long userId) throws Exception;
	
	public boolean addUserMobileSetting(UserMobileSetting userMobileSetting) throws Exception;
	
	public boolean removeUserMobileSetting(String mobileInstanceId, boolean isPortal) throws Exception;
	
	public List<Map<String, Object>> getMobileInstanceIds(List<Long> uIds) throws Exception;
	
	public Object getPermalinkDetails(String token) throws Exception;
	
    public String validateAndGenerateTokenV3(String emailaddress, String password, String appDomainName, String userAgent, String userType,
			String ipAddress, boolean startUserSession) throws Exception ;

    String getEmailFromDigest(String digest) throws Exception;

    public long verifyPasswordv3(String username, String password, String appDomainName, String userType) throws Exception;
    
    public IAMAccount verifyFacilioTokenv3(String idToken, boolean overrideSessionCheck, String userType) throws Exception;
	
    public IAMAccount verifyUserSessionv3(String uId, String token, String userType) throws Exception;
    
    public IAMAccount getAccountv3(long userId) throws Exception;
    
    public Organization getDefaultOrgv3(long uid) throws Exception;
	
    public List<IAMUser> getUserDataForUidsv3(String userIds, long orgId, boolean shouldFetchDeleted) throws Exception;
	    	
    public Map<String, Object> getUserForEmail(String emailOrPhone, long orgId, String identifier) throws Exception;
    
    public Map<String, Object> getUserForPhone(String phone, long orgId, String identifier) throws Exception;
    
    public IAMUser createUserFromProps(Map<String, Object> prop) throws Exception;
    
    public Map<String, Object> getUserForUsername(String username, long orgId, String identifier) throws Exception;

	public IAMUser getFacilioUserV3(String username, String identifier) throws Exception;
	
	public boolean disableUser(long orgId, long userId) throws Exception;
	
	public boolean enableUser(long orgId, long userId) throws Exception;
	
	public boolean verifyPassword(long orgId, long userId, String oldPassword) throws Exception;
	
	public void addAppDomains(List<AppDomain> appDomains) throws Exception;
	
	public int deleteAppDomain(long id) throws Exception;
    
    public List<AppDomain> getAppDomain(AppDomainType type, long orgId) throws Exception;
    
    public AppDomain getAppDomain(String domain) throws Exception;

	public List<AppDomain> getAppDomainForType(Integer domainType, Long orgId) throws Exception;
    
    public List<AppDomain> getPortalAppDomains() throws Exception;
    
    public AppDomain getAppDomain(long appDomainId) throws Exception;
    
	public int deleteAppDomains(List<Long> appDomainIds) throws Exception;
    
	public void deleteDefaultAppDomains(long orgId) throws Exception;
	
	public List<AppDomain> getAppDomainsForOrg(long orgId) throws Exception;
    
	public String generateTokenForWithoutPassword(String emailaddress, String userAgent, String userType,
			String ipAddress, boolean startUserSession, String appDomain, IAMAccountConstants.SocialLogin socialLogin) throws Exception;

	public boolean deleteUserPhoto(long uid, long photoId) throws Exception;

	public Map<String, Object> getLoginModes(String userName, String domain, AppDomain appDomain) throws Exception;
}
