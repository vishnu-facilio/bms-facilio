package com.facilio.accounts.bean;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.bmsconsole.criteria.Criteria;

public interface UserBean {
	
	public long createUser(long orgId, User user) throws Exception;
	
	public long inviteUser(long orgId, User user) throws Exception;
	
	public long inviteRequester(long orgId, User user) throws Exception;

	User verifyEmail(String token);

	User resetPassword(String token, String password);

	User validateUserInvite(String token);

	public boolean resendInvite(long ouid) throws Exception;
	
	public boolean acceptInvite(String token, String password) throws Exception;
	
	public boolean updateUser(long ouid, User user) throws Exception;
	
	public boolean updateUser(User user) throws Exception;
	
	public boolean sendResetPasswordLink(User user) throws Exception;
	
	public void addUserMobileSetting(UserMobileSetting userMobileSetting) throws Exception;
	
	public void removeUserMobileSetting(String mobileInstanceId) throws Exception;
	
	public boolean deleteUser(long ouid) throws Exception;

	public boolean disableUser(long ouid) throws Exception;
	
	public boolean enableUser(long ouid) throws Exception;
	
	public boolean setDefaultOrg(long uid, long orgId) throws Exception;
	
	public User getUser(long ouid) throws Exception;
	
	public User getUser(String email) throws Exception;
	
	public List<User> getUsers(Criteria criteria, List<Long>... ouids) throws Exception;

	User getFacilioUser(long orgId, String email) throws Exception;

	public User getUser(long orgId, String email) throws Exception;
	
	public List<Organization> getOrgs(long uid) throws Exception;
	
	public Organization getDefaultOrg(long uid) throws Exception;
	
	public long addRequester(long orgId, User user) throws Exception;
	
	public boolean updateUserPhoto(long uid, long fileId) throws Exception;

    public User getFacilioUser(String email) throws Exception;
    
    public User getFacilioUser(String email, String orgDomain) throws Exception;

    public User getPortalUser(String email, long portalId) throws Exception;
    
    public long startUserSession(long uid, String email, String token, String ipAddress, String userAgent) throws Exception;
    
    public boolean endUserSession(long uid, String email, String token) throws Exception;
    
    public List<Map<String, Object>> getUserSessions(long uid, Boolean isActive) throws Exception;
    
//    public long addUserLicense(long orgid, long roleid, Integer number_of_users) throws Exception;
//    
//    public boolean updateUserLicense(long id, Integer number_of_users) throws Exception;
//   
//    public Integer getAvailableRoleLicense(long orgid, long roleid) throws Exception;
//    public Map<Long, Integer> getUserRoleLicenseMap(long orgid) throws Exception;
//    
//    public Integer getAvailableUserLicense(long orgid) throws Exception;
//    
//    public void deleteUserLicense(long id) throws Exception;
    
    public void clearUserSession(long uid, String email, String token) throws Exception;
    
    public void clearAllUserSessions(long uid, String email) throws Exception;
    
    public boolean verifyUserSession(String email, String token) throws Exception;
}
