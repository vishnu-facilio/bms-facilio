package com.iam.accounts.bean;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.iam.accounts.dto.Account;

public interface IAMUserBean {
	
	public long createUserv2(long orgId, User user) throws Exception;
	
	public long addUserv2(long orgId, User user) throws Exception;
	
//	public long inviteAdminConsoleUserv2(long orgId, User user) throws Exception;
	
	User verifyEmailv2(String token);

	User resetPasswordv2(String token, String password);

	User validateUserInvitev2(String token);

	public boolean resendInvitev2(long orgId, long userId) throws Exception;
	
	public User acceptInvitev2(String token, String password) throws Exception;
	
	public boolean updateUserv2(long ouid, User user) throws Exception;
	
	public boolean updateUserv2(User user) throws Exception;
	
	public boolean deleteUserv2(long ouid) throws Exception;

	public boolean disableUserv2(long orgId, long uid) throws Exception;
	
	public boolean enableUserv2(long orgId, long uid) throws Exception;
	
	public boolean setDefaultOrgv2(long uid, long orgId) throws Exception;
	
	public User getUserv2(long ouid) throws Exception;
	
	public User getUserv2(long orgId, String email) throws Exception;
	
	public List<Organization> getOrgsv2(long uid) throws Exception;
	
	public Organization getDefaultOrgv2(long uid) throws Exception;
	
	public boolean updateUserPhoto(long uid, long fileId) throws Exception;

	public User getFacilioUserv3(String email, String orgDomain, String portalDomain) throws Exception;
	
    public User getFacilioUserv3(String email, long orgId, String portalDomain) throws Exception;
    
    public User getFacilioUserv3(String email) throws Exception;
    
    public long startUserSessionv2(long uid, String email, String token, String ipAddress, String userAgent, String userType) throws Exception;
    
    public boolean endUserSessionv2(long uid, String email, String token) throws Exception;
    
    public List<Map<String, Object>> getUserSessionsv2(long uid, Boolean isActive) throws Exception;
    
    public void clearUserSessionv2(long uid, String email, String token) throws Exception;
    
    public void clearAllUserSessionsv2(long uid, String email) throws Exception;
    
    public Account verifyUserSessionv2(String email, String token, String orgDomain) throws Exception;
    
 	public boolean acceptUserv2(User user) throws Exception;
	
	public String getEncodedTokenv2(User user) throws Exception;
	
	public User getUserv2(long orgId, long userId) throws Exception;
	
	public User getUserv2(String email, String portalDomain) throws Exception;
	
	public String generatePermalinkForURL(String url, long uid, long orgId) throws Exception;
	    
	public boolean verifyPermalinkForURL(String token, List<String> url) throws Exception;

	public Account getPermalinkAccount(String token, List<String> url) throws Exception ;
	
	
}
