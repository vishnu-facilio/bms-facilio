package com.facilio.iam.accounts.bean;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.sso.DomainSSO;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.db.criteria.Criteria;

public interface IAMOrgBean {

	public IAMAccount signUpOrg(JSONObject jObj, Locale locale) throws Exception;
	
	public Organization createOrgv2(Organization org) throws Exception;
	
	public boolean updateOrgv2(long orgId, Organization org) throws Exception;
	
	public boolean deleteOrgv2(long orgId) throws Exception;
	
	public Organization getOrgv2(long orgId) throws Exception;
	
	public Organization getOrgv2(String orgDomain) throws Exception;

	public void updateLoggerLevel(int level, long orgId) throws Exception;

	public boolean rollbackSignUpOrg(long orgId, long superAdminUserId) throws Exception;
	
	public List<Organization> getOrgs() throws Exception ;

	public List<Organization> getOrgs(List<Long> orgIds) throws Exception ;

 	public List<IAMUser> getAllOrgUsersv2(long orgId) throws Exception;

 	public boolean addOrUpdateDomainSSO(DomainSSO sso) throws Exception;
 	
 	public boolean addOrUpdateAccountSSO(long orgId, AccountSSO sso) throws Exception;
	
	public AccountSSO getAccountSSO(long orgId) throws Exception;

	public List<AccountSSO> getAccountSSO(List<Long> orgIds) throws Exception;

	public DomainSSO getDomainSSODetails(long domainSSOId) throws Exception;

	public DomainSSO getDomainSSODetails(String domain) throws Exception;
	
	public AccountSSO getAccountSSO(String orgDomain) throws Exception;

	public boolean deleteDomainSSO(String domain) throws Exception;

	public boolean deleteAccountSSO(long orgId) throws Exception;

	public void enableTotp(long orgId, AppDomain.GroupType groupType) throws Exception;
	
	public void disableTotp(long orgId, AppDomain.GroupType groupType) throws Exception;

	public boolean updateDomainSSOStatus(String domain, boolean status) throws Exception;
}