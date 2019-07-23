package com.iam.accounts.bean;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.db.criteria.Criteria;

public interface IAMOrgBean {

	public Organization createOrgv2(Organization org) throws Exception;
	
	public boolean updateOrgv2(long orgId, Organization org) throws Exception;
	
	public boolean deleteOrgv2(long orgId) throws Exception;
	
	public Organization getOrgv2(long orgId) throws Exception;
	
	public Organization getOrgv2(String orgDomain) throws Exception;

	public List<User> getAllOrgUsersv2(long orgId) throws Exception;
	
	public List<User> getOrgUsersv2(long orgId, boolean status) throws Exception;
	
	public List<User> getOrgUsersv2(long orgId, Criteria criteria) throws Exception;
	
	public Map<Long, User> getOrgUsersAsMapv2(long orgId, Criteria criteria) throws Exception;

	public List<User> getActiveOrgUsersv2(long orgId) throws Exception;

	
}