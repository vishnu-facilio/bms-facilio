 package com.facilio.iam.accounts.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.APIClient;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.modules.fields.FacilioField;

public interface IAMUserBean {
	
	public long addUserv3(long orgId, IAMUser user, String identifier) throws Exception;
		
	
//	public long inviteAdminConsoleUserv2(long orgId, User user) throws Exception;
	
	IAMUser verifyEmailv2(String token) throws Exception;

	IAMUser resetExpiredPassword(String digest, String password ,String confirmPassword) throws Exception;

	IAMUser resetPasswordv2(String token, String password , String confirmPassword) throws Exception;
	
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

	public boolean endUserSessionv2(long uid) throws Exception;

	public boolean endUserSessionv2(long uid, Criteria criteria) throws Exception;
    
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

	public int updateUserMobileFcmToken(UserMobileSetting userMobileSetting, FacilioModule module,HashMap<String,Object> updateValue) throws Exception;

	public boolean removeUserMobileSetting(String mobileInstanceId, String appLinkName) throws Exception;

	public List<UserMobileSetting> getMobileInstanceIds(List<Long> uIds, String appLinkName) throws Exception;

	public Object getPermalinkDetails(String token) throws Exception;

	public Map<String, Object> validateAndGenerateTokenV3(String emailaddress, String password, String appDomainName, String userAgent, String userType,
											 String ipAddress) throws Exception;
	
    public String validateAndGenerateTokenV3(String emailaddress, String password, String appDomainName, String userAgent, String userType,
			String ipAddress, boolean startUserSession) throws Exception;

	String addProxySession(String proxyUserName, String proxiedUserName, long proxiedSessionId) throws Exception;

    String getEmailFromDigest(String digest) throws Exception;

    String getEmailFromDigest(String digest, IAMAccountConstants.SessionType sessionType) throws Exception;
    
	public long verifyPasswordv3(String username, String password, String appDomainName, String userType) throws Exception;
    
    public IAMAccount verifyFacilioTokenv3(String idToken, boolean overrideSessionCheck, String userType) throws Exception;
	
    public IAMAccount verifyUserSessionv3(String uId, String token, String userType) throws Exception;

    IAMAccount fetchAccountByAPIKey(String apiKey) throws Exception;

    String createApiKey(long orgId, long userId, String name) throws Exception;

	Map<String, String> createOauth2Client(long orgId, long userId, String name) throws Exception;

	List<APIClient> getApiClientList(long orgId, long userId) throws Exception;

	void deleteApiClient(long orgId, long userId, long id) throws Exception;

    IAMAccount fetchAccountByOauth2ClientId(String token) throws Exception;

	public String isSessionExpired(long uid, long sessionId) throws Exception;

	public String isSessionExpired(long uid, long sessionId,AppDomain appdomainObj) throws Exception;

	public long getSessionExpiry(long uid, long sessionId) throws Exception;
    
    public IAMAccount getAccountv3(long userId) throws Exception;
    
    public Organization getDefaultOrgv3(long uid) throws Exception;
	
    public List<IAMUser> getUserDataForUidsv3(String userIds, long orgId, boolean shouldFetchDeleted) throws Exception;
	    	
    public Map<String, Object> getUserForEmail(String emailOrPhone, long orgId, String identifier) throws Exception;

	public Map<String, Object> getUserForEmail(String emailOrPhone, long orgId, String identifier, boolean fetchInactive) throws Exception;
    
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

	public String getPortalDomainUrlForUser(String username,AppDomainType appDomainType) throws Exception;

	public List<AppDomain> getPortalAppDomains() throws Exception;
    
    public AppDomain getAppDomain(long appDomainId) throws Exception;
    
	public int deleteAppDomains(List<Long> appDomainIds) throws Exception;
    
	public void deleteDefaultAppDomains(long orgId) throws Exception;
	
	public List<AppDomain> getAppDomainsForOrg(long orgId) throws Exception;
    
	public String generateTokenForWithoutPassword(String emailaddress, String userAgent, String userType,
			String ipAddress, boolean startUserSession, String appDomain, IAMAccountConstants.SocialLogin socialLogin) throws Exception;

	public boolean deleteUserPhoto(long uid, long photoId) throws Exception;

	long getUIDFromPWDResetToken(String digest) throws Exception;

	String generatePWDPolicyPWDResetToken(String userName, AppDomain.GroupType groupType) throws Exception;

	public Map<String, Object> getLoginModes(String userName, String domain, AppDomain appDomain) throws Exception;

	public Map<String, Object> getLoginModes(String userName, AppDomain.AppDomainType appDomainType, AppDomain.GroupType groupType) throws Exception;

	public String generateTotpSessionToken(String userName, String token) throws Exception;

	String generateMFAConfigSessionToken(String userName, String token) throws Exception;

	public boolean updateUserMfaSettingsSecretKey(long userId,String value) throws Exception;

	public Map<String, Object> getUserMfaSettings(long userId) throws Exception;

	SecurityPolicy getUserSecurityPolicy(String email, AppDomain.GroupType groupType) throws Exception;

	public Map<String, Object> getUserMfaSettings(String email, AppDomain.GroupType groupType) throws Exception;

	public void updateUserMfaSettingsStatus(long userId, boolean value) throws Exception;

	public boolean clearUserMfaSettings(long userId) throws Exception;

	String validateDigestAndDomain(String domain, String digest, AppDomain.GroupType groupType) throws Exception;

	List<Map<String, Object>> getUserData(String username, AppDomain.GroupType groupType) throws Exception;

	SecurityPolicy getUserSecurityPolicy(long uid) throws Exception;

	void validatePasswordWithSecurityPolicy(long uid, String password) throws Exception;

	String cryptWithMD5(String pass);

	void savePreviousPassword(long uid, String encryptedPassword) throws Exception;

	Integer lookupUserDC(String username, GroupType groupType) throws Exception;

	int findDCForUser(String username, GroupType groupType) throws Exception;
	
	long addDCLookup(Map<String, Object> props) throws Exception;

	long createSecurityPolicy(SecurityPolicy securityPolicy) throws Exception;

	void updateSecurityPolicy(SecurityPolicy securityPolicy) throws Exception;

	SecurityPolicy fetchSecurityPolicy(long id, long orgId) throws Exception;

	List<SecurityPolicy> fetchAllSecurityPolicies(long orgId) throws Exception;

	SecurityPolicy fetchDefaultSecurityPolicy(long orgId) throws Exception;

	void deleteSecurityPolicy(long id, long orgId) throws Exception;

	List<Map<String, Object>> getUserData(String username, long orgId, String identifier) throws Exception;

	void deleteDCLookup(String username, GroupType groupType) throws Exception;

	boolean isUserInProxyList(String username) throws Exception;

	Map<String, Object> generatePropsForWithoutPassword(String emailaddress, String userAgent, String userType,
														String ipAddress, String appDomain, boolean isProxySession) throws Exception;

	IAMAccount verifyProxyToken(String proxyToken, IAMAccount proxyUser) throws Exception;

	String generateProxyUserSessionToken(String proxyUser) throws Exception;

	String decodeProxyUserToken(String token) throws Exception;

	Map<String, Object> getUserSession(long sessionId) throws Exception;

	Map<String, Object> getProxySession(String proxyToken) throws Exception;
}
