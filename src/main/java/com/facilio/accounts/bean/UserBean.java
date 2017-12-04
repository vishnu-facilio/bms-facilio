package com.facilio.accounts.bean;

import java.util.List;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;

public interface UserBean {
	
	public long createUser(long orgId, User user) throws Exception;
	
	public long inviteUser(long orgId, User user) throws Exception;
	
	public boolean acceptInvite(long ouid, String cognitoId) throws Exception;
	
	public boolean updateUser(long ouid, User user) throws Exception;
	
	public boolean deleteUser(long ouid) throws Exception;
	
	public boolean disableUser(long ouid) throws Exception;
	
	public boolean enableUser(long ouid) throws Exception;
	
	public boolean setDefaultOrg(long uid, long orgId) throws Exception;
	
	public User getUser(long ouid) throws Exception;
	
	public User getUser(String email) throws Exception;
	
	public User getUser(long orgId, String email) throws Exception;
	
	public List<Organization> getOrgs(long uid) throws Exception;
	
	public Organization getDefaultOrg(long uid) throws Exception;
	
	public long addRequester(long orgId, User user) throws Exception;
}
