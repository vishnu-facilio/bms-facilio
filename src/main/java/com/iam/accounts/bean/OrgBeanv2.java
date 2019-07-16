package com.iam.accounts.bean;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.db.criteria.Criteria;

public interface OrgBeanv2 {

	public Organization createOrgv2(Organization org) throws Exception;
	
	public boolean updateOrgv2(long orgId, Organization org) throws Exception;
	
	public boolean deleteOrgv2(long orgId) throws Exception;
	
	public Organization getOrgv2(long orgId) throws Exception;
	
	public Organization getOrgv2(String orgDomain) throws Exception;

    Organization getPortalOrgv2(String orgDomain) throws Exception;

	public List<User> getOrgPortalUsersv2(long orgId) throws Exception;

	public List<User> getAllOrgUsersv2(long orgId) throws Exception;
	
	public List<User> getOrgUsersv2(long orgId, boolean status) throws Exception;
	
	public List<User> getOrgUsersv2(long orgId, Criteria criteria) throws Exception;
	
	public Map<Long, User> getOrgUsersAsMapv2(long orgId, Criteria criteria) throws Exception;

	public List<User> getActiveOrgUsersv2(long orgId) throws Exception;

	public List<User> getRequestersv2(long orgId) throws Exception;
	
	public long getPortalIdv2() throws Exception ;
	
	public JSONObject orgInfov2() throws Exception;
	
	Organization getPortalOrgv2(Long portalId) throws Exception;
}