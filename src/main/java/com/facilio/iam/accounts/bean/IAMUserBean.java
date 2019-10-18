package com.facilio.iam.accounts.bean;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;

public interface IAMUserBean {
	
	public long addUserv2(long orgId, IAMUser user) throws Exception;
	
//	public long inviteAdminConsoleUserv2(long orgId, User user) throws Exception;
	
	IAMUser verifyEmailv2(String token) throws Exception;

	IAMUser resetPasswordv2(String token, String password) throws Exception;
	
	public boolean verifyPasswordv2(String email, String domain, String password) throws Exception;

	IAMUser validateUserInvitev2(String token) throws Exception;

	public IAMUser acceptInvitev2(String token, String password) throws Exception;
	
	public boolean updateUserv2(IAMUser user, List<FacilioField> fields) throws Exception;
	
	public boolean deleteUserv2(IAMUser user) throws Exception;

	public boolean disableUserv2(long orgId, long uid) throws Exception;
	
	public boolean enableUserv2(long orgId, long uid) throws Exception;
	
	public boolean setDefaultOrgv2(long uid, long orgId) throws Exception;
	
	public IAMUser getFacilioUser(long orgId, String email) throws Exception;
	
	public List<Organization> getOrgsv2(long uid) throws Exception;
	
	public Organization getDefaultOrgv2(long uid) throws Exception;
	
	public Organization getOrgv2(String currentOrgDomain, long uid) throws Exception;
	
	public boolean updateUserPhoto(long uid, long fileId) throws Exception;

	public IAMUser getFacilioUser(String email, String orgDomain, String portalDomain) throws Exception;
	
    public IAMUser getFacilioUser(String email, long orgId, String portalDomain) throws Exception;
    
    public IAMUser getFacilioUser(String email) throws Exception;
    
    public long startUserSessionv2(long uid, String email, String token, String ipAddress, String userAgent, String userType) throws Exception;
    
    public boolean endUserSessionv2(long uid, String email, String token) throws Exception;
    
    public List<Map<String, Object>> getUserSessionsv2(long uid, Boolean isActive) throws Exception;
    
    public void clearUserSessionv2(long uid, String email, String token) throws Exception;
    
    public void clearAllUserSessionsv2(long uid, String email) throws Exception;
    
    public IAMAccount verifyUserSessionv2(String uId, String token, String orgDomain) throws Exception;
    
    //for backward compatibility
    public IAMAccount verifyUserSessionUsingEmail(String email, String token, String portalDomain) throws Exception;
    
    public boolean acceptUserv2(IAMUser user) throws Exception;
	
	public String getEncodedTokenv2(IAMUser user) throws Exception;
	
	public IAMUser getFacilioUser(long orgId, long userId) throws Exception;
	
	public IAMUser getFacilioUser(String email, String portalDomain) throws Exception;
	
	public String generatePermalinkForURL(String url, long uid, long orgId) throws Exception;
	    
	public boolean verifyPermalinkForURL(String token, List<String> url) throws Exception;

	public IAMAccount getPermalinkAccount(String token, List<String> url) throws Exception ;

	public long signUpSuperAdminUserv2(long orgId, IAMUser user) throws Exception;

	public IAMAccount getAccount(long userId, String orgDomain) throws Exception;

	public Map<Long, Map<String, Object>> getUserData(Criteria criteria, long orgId, boolean shouldFetchDeleted) throws Exception;

	public Map<Long, Map<String, Object>> getUserDataForUids(List<Long> userIds, long orgId, boolean shouldFetchDeleted) throws Exception;
	
	public String validateAndGenerateToken(String emailaddress, String password, String userAgent, String userType,
			String ipAddress, String domain, boolean startUserSession) throws Exception ;

	public IAMAccount verifyFacilioToken(String idToken, boolean overrideSessionCheck, String orgDomain, String portalDomain) throws Exception;
	
	public boolean verifyUser(long userId) throws Exception;
	
	public boolean addUserMobileSetting(UserMobileSetting userMobileSetting) throws Exception;
	
	public boolean removeUserMobileSetting(String mobileInstanceId, boolean isPortal) throws Exception;
	
	public List<Map<String, Object>> getMobileInstanceIds(List<Long> uIds) throws Exception;
	

}
