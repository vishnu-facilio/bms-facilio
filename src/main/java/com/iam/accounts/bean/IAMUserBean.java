package com.iam.accounts.bean;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;

public interface IAMUserBean {
	
	public long addUserv2(long orgId, IAMUser user) throws Exception;
	
//	public long inviteAdminConsoleUserv2(long orgId, User user) throws Exception;
	
	IAMUser verifyEmailv2(String token);

	IAMUser resetPasswordv2(String token, String password);

	IAMUser validateUserInvitev2(String token);

	public boolean resendInvitev2(long orgId, long userId) throws Exception;
	
	public IAMUser acceptInvitev2(String token, String password) throws Exception;
	
	public boolean updateUserv2(long ouid, IAMUser user) throws Exception;
	
	public boolean updateUserv2(IAMUser user) throws Exception;
	
	public boolean deleteUserv2(IAMUser user) throws Exception;

	public boolean disableUserv2(long orgId, long uid) throws Exception;
	
	public boolean enableUserv2(long orgId, long uid) throws Exception;
	
	public boolean setDefaultOrgv2(long uid, long orgId) throws Exception;
	
	public IAMUser getFacilioUser(long ouid) throws Exception;
	
	public IAMUser getFacilioUser(long orgId, String email) throws Exception;
	
	public List<Organization> getOrgsv2(long uid) throws Exception;
	
	public Organization getDefaultOrgv2(long uid) throws Exception;
	
	public boolean updateUserPhoto(long uid, long fileId) throws Exception;

	public IAMUser getFacilioUser(String email, String orgDomain, String portalDomain) throws Exception;
	
    public IAMUser getFacilioUser(String email, long orgId, String portalDomain) throws Exception;
    
    public IAMUser getFacilioUser(String email) throws Exception;
    
    public long startUserSessionv2(long uid, String email, String token, String ipAddress, String userAgent, String userType) throws Exception;
    
    public boolean endUserSessionv2(long uid, String email, String token) throws Exception;
    
    public List<Map<String, Object>> getUserSessionsv2(long uid, Boolean isActive) throws Exception;
    
    public void clearUserSessionv2(long uid, String email, String token) throws Exception;
    
    public void clearAllUserSessionsv2(long uid, String email) throws Exception;
    
    public IAMAccount verifyUserSessionv2(String email, String token, String orgDomain) throws Exception;
    
 	public boolean acceptUserv2(IAMUser user) throws Exception;
	
	public String getEncodedTokenv2(IAMUser user) throws Exception;
	
	public IAMUser getFacilioUser(long orgId, long userId) throws Exception;
	
	public IAMUser getFacilioUser(String email, String portalDomain) throws Exception;
	
	public String generatePermalinkForURL(String url, long uid, long orgId) throws Exception;
	    
	public boolean verifyPermalinkForURL(String token, List<String> url) throws Exception;

	public IAMAccount getPermalinkAccount(String token, List<String> url) throws Exception ;

	public long signUpSuperAdminUserv2(long orgId, IAMUser user) throws Exception;

	
}
