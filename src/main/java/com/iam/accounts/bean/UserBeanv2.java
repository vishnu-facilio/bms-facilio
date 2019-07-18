package com.iam.accounts.bean;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.db.criteria.Criteria;

public interface UserBeanv2 {
	
	public long createUserv2(long orgId, User user) throws Exception;
	
	public long inviteUserv2(long orgId, User user) throws Exception;
	
	public long inviteAdminConsoleUserv2(long orgId, User user) throws Exception;
	
	public long inviteRequesterv2(long orgId, User user) throws Exception;

	User verifyEmailv2(String token);

	User resetPasswordv2(String token, String password);

	User validateUserInvitev2(String token);

	public boolean resendInvitev2(long ouid) throws Exception;
	
	public User acceptInvitev2(String token, String password) throws Exception;
	
	public boolean updateUserv2(long ouid, User user) throws Exception;
	
	public boolean updateUserv2(User user) throws Exception;
	
	public boolean sendResetPasswordLinkv2(User user) throws Exception;
	
	public void addUserMobileSettingv2(UserMobileSetting userMobileSetting) throws Exception;
	
	public void removeUserMobileSettingv2(String mobileInstanceId) throws Exception;
	
	public boolean deleteUserv2(long ouid) throws Exception;

	public boolean disableUserv2(long ouid) throws Exception;
	
	public boolean enableUserv2(long ouid) throws Exception;
	
	public boolean setDefaultOrgv2(long uid, long orgId) throws Exception;
	
	public User getUserv2(long ouid) throws Exception;
	
	public User getUserInternalv2(long ouid) throws Exception;
	
	public User getUserInternalv2(long ouid, boolean withRole) throws Exception;
	
	public User getUserFromEmailv2(String email) throws Exception;
	
	public User getUserFromPhonev2(String phone) throws Exception;
	
	public List<User> getUsersv2(Criteria criteria, Collection<Long>... ouids) throws Exception;
	
	public Map<Long, User> getUsersAsMapv2(Criteria criteria, Collection<Long>... ouids) throws Exception;

	public User getUserv2(long orgId, String email) throws Exception;
	
	public List<Organization> getOrgsv2(long uid) throws Exception;
	
	public Organization getDefaultOrgv2(long uid) throws Exception;
	
	public long addRequesterv2(long orgId, User user) throws Exception;
	
	public long createRequestorv2(long orgId, User user) throws Exception;

	
	public boolean updateUserPhoto(long uid, long fileId) throws Exception;

    public User getFacilioUserv2(String email) throws Exception;
    
    public User getFacilioUserv2(String email, String orgDomain) throws Exception;
    
    public User getFacilioUserv2(long orgId, String email) throws Exception;

    public User getPortalUser(String email, long portalId) throws Exception;
    
    public User getPortalUserv2(String email, long portalId) throws Exception;
    
    public User getPortalUsersv2(String email, long portalId) throws Exception;
    
    public User getPortalUserv2(long uid) throws Exception;
    
    public long startUserSessionv2(long uid, String email, String token, String ipAddress, String userAgent, String userType) throws Exception;
    
    public boolean endUserSessionv2(long uid, String email, String token) throws Exception;
    
    public List<Map<String, Object>> getUserSessionsv2(long uid, Boolean isActive) throws Exception;
    
    public void clearUserSessionv2(long uid, String email, String token) throws Exception;
    
    public void clearAllUserSessionsv2(long uid, String email) throws Exception;
    
    public boolean verifyUserSessionv2(String email, String token) throws Exception;
    
    public String generatePermalinkForURLv2(String url, User user) throws Exception;
    
    public boolean verifyPermalinkForURLv2(String token, List<String> url) throws Exception;

	public boolean acceptUserv2(User user) throws Exception;

}
