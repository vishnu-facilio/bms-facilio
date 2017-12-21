package com.facilio.accounts.bean;

import java.util.List;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;

public interface OrgBean {

	public long createOrg(Organization org) throws Exception;
	
	public boolean updateOrg(long orgId, Organization org) throws Exception;
	
	public boolean deleteOrg(long orgId) throws Exception;
	
	public Organization getOrg(long orgId) throws Exception;
	
	public Organization getOrg(String orgDomain) throws Exception;
	
	public List<User> getAllOrgUsers(long orgId) throws Exception;
	
	public List<User> getOrgUsers(long orgId, boolean status) throws Exception;
	
	public List<User> getRequesters(long orgId) throws Exception;
	
	public User getSuperAdmin(long orgId) throws Exception;
	
	public void testTransaction() throws Exception;
}