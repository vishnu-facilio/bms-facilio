package com.facilio.accounts.bean;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.db.criteria.Criteria;

public interface OrgBean {

	public long createOrg(Organization org) throws Exception;
	
	public long createOrgv2(Organization org) throws Exception;
	
	public boolean updateOrg(long orgId, Organization org) throws Exception;
	
	public boolean deleteOrg(long orgId) throws Exception;
	
	public Organization getOrg(long orgId) throws Exception;
	
	public Organization getOrg(String orgDomain) throws Exception;

    Organization getPortalOrg(String orgDomain) throws Exception;

	public List<User> getOrgPortalUsers(long orgId) throws Exception;

	public List<User> getAllOrgUsers(long orgId) throws Exception;
	
	public List<User> getOrgUsers(long orgId, boolean status) throws Exception;
	
	public List<User> getOrgUsers(long orgId, Criteria criteria) throws Exception;
	
	public Map<Long, User> getOrgUsersAsMap(long orgId, Criteria criteria) throws Exception;

	public List<User> getActiveOrgUsers(long orgId) throws Exception;

	public List<User> getRequesters(long orgId) throws Exception;
	
	public long getPortalId() throws Exception ;
	
	public User getSuperAdmin(long orgId) throws Exception;
	
	public void testTransaction(String prefix) throws Exception;
	
	public List getEnergyMeterList() throws Exception ;

	public int getFeatureLicense() throws Exception;

	public long addLicence(long summodule) throws Exception;
	
	public JSONObject orgInfo() throws Exception;
	
	Organization getPortalOrg(Long portalId) throws Exception;
	
	public void updateLoggerLevel (int level, long orgId) throws Exception;
}