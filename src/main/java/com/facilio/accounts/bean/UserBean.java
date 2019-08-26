package com.facilio.accounts.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.db.criteria.Criteria;

public interface UserBean {
	
	public void createUser(long orgId, User user) throws Exception;
	
//	public long inviteAdminConsoleUser(long orgId, User user) throws Exception;
	
	public long inviteRequester(long orgId, User user, boolean isEmailVerificationNeeded) throws Exception;

	User verifyEmail(String token) throws Exception;

	User validateUserInvite(String token) throws Exception;

	public boolean resendInvite(long ouid) throws Exception;
	
	public boolean acceptInvite(String token, String password) throws Exception;
	
	public boolean updateUser(User user) throws Exception;
	
	public void addUserMobileSetting(UserMobileSetting userMobileSetting) throws Exception;
	
	public void removeUserMobileSetting(String mobileInstanceId) throws Exception;
	
	public boolean deleteUser(long ouid) throws Exception;

	public boolean disableUser(long ouid) throws Exception;
	
	public boolean enableUser(long ouid) throws Exception;
	
	public User getUser(long ouid) throws Exception;
	
	public User getUser(long orgId, long userId) throws Exception;

	public User getUserInternal(long ouid, boolean withRole) throws Exception;
	
	public User getUserFromPhone(String phone) throws Exception;
	
	public List<User> getUsers(Criteria criteria, Collection<Long>... ouids) throws Exception;
	
	public Map<Long, List<User>> getUsersWithRoleAsMap(Collection<Long> roleIds) throws Exception;
	
	public List<User> getUsersWithRole(long roleId) throws Exception;
	
	public List<User> getUsersWithRoleAndAccessibleSpace (long roleId, long spaceId) throws Exception;
	
	public Map<Long, User> getUsersAsMap(Criteria criteria, Collection<Long>... ouids) throws Exception;

	public User getUser(long orgId, String email) throws Exception;
	
	public List<Organization> getOrgs(long uid) throws Exception;
	
	public Organization getDefaultOrg(long uid) throws Exception;
	
	public boolean updateUserPhoto(long uid, long fileId) throws Exception;

    public User getUser(String email, String portalDomain) throws Exception;
    
    public User getUser(String email) throws Exception;
    
    public User getPortalUsers(String email, long portalId) throws Exception;
    
    public User getPortalUser(long uid) throws Exception;
    
    public String generatePermalinkForURL(String url, User user) throws Exception;
    
    public boolean verifyPermalinkForURL(String token, List<String> url) throws Exception;
    
    public Account getPermalinkAccount(String token, List<String> url) throws Exception ;

    List<Long> getAccessibleSpaceList (long uid) throws Exception;
    List<Long> getAccessibleGroupList (long uid) throws Exception;

	public boolean acceptUser(User user) throws Exception;

	public HashMap<Long, Set<Long>> getUserSites(List<Long> users) throws Exception;
	
	public boolean sendResetPasswordLinkv2(User user) throws Exception;
	
	public boolean verifyUser(long userId) throws Exception;

	public List<Map<String, Object>> getUserSessions(long uid, Boolean isActive) throws Exception;

	public void createUserEntry(long orgId, User user, boolean isEmailVerificationNeeded) throws Exception;

	public boolean setDefaultOrg(long orgId, long userId) throws Exception;

	
}
