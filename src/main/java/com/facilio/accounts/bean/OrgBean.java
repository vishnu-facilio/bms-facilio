package com.facilio.accounts.bean;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.OrgUnitsContext;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.accounts.util.AccountUtil.LicenseMapping;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public interface OrgBean {

	public boolean updateOrg(long orgId, Organization org) throws Exception;

	public boolean deleteOrg(long orgId) throws Exception;

	public Organization getOrg(long orgId) throws Exception;

	public Organization getOrg(String orgDomain) throws Exception;

	public PortalInfoContext getPortalInfo(long id, boolean isPortalID) throws Exception;

	public List<User> getOrgPortalUsers(long orgId, long appId) throws Exception;

	public List<User> getRequesterTypeUsers(long orgId, boolean status) throws Exception;

	public List<User> getAppUsers(long orgId, long appId, boolean checkAccessibleSites) throws Exception;

	public Long getAppUsersCount(long orgId, long appId, boolean fetchNonAppUsers) throws Exception;

	public Long getAppUsersCount(long orgId, long appId, boolean fetchNonAppUsers,String searchQuery,Boolean inviteAcceptStatus) throws Exception;

	public List<User> getAppUsers(long orgId, long appId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage) throws Exception;

	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus) throws Exception;

	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus,boolean status,List<Long> teamId,List<Long> applicationIds,List<Long> defaultIds,Criteria criteria) throws Exception;

	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus,boolean status,List<Long> teamId,List<Long> applicationIds,List<Long> defaultIds,Criteria criteria, String orderBy, String orderType) throws Exception;

	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus,boolean status) throws Exception;

	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus,List<Long> teamId,List<Long> applicationIds,List<Long> defaultIds,Criteria criteria) throws Exception;

	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus,List<Long> teamId,List<Long> applicationIds,List<Long> defaultIds,Criteria criteria, String orderBy, String orderType) throws Exception;

	public User getAppUser(long orgId, long appId, long ouId) throws Exception;

	public List<User> getOrgUsers(long orgId, boolean status) throws Exception;

	public List<User> getDefaultAppUsers(long orgId) throws Exception;

	public List<User> getDefaultAppUsers(long orgId, String linkName) throws Exception;

	public Map<Long, User> getOrgUsersAsMap(long orgId) throws Exception;

	public Map<Long, User> getOrgUsersAsMap(long orgId, String appLinkName) throws Exception;

	public List<User> getActiveOrgUsers(long orgId) throws Exception;

	public User getSuperAdmin(long orgId) throws Exception;

	public Map<String, Long> getFeatureLicense() throws Exception;

	public boolean isFeatureEnabled(FeatureLicense featureLicense) throws Exception;

	public int addLicence(Map<LicenseMapping, Long> licenseMap) throws Exception;

	public JSONObject orgInfo() throws Exception;

	public void updateLoggerLevel (int level, long orgId) throws Exception;

	public List<Organization> getOrgs() throws Exception;

	public List<Organization> getOrgsForMigration() throws Exception;

	public Organization getPortalOrg(long portalId, AppDomainType appType) throws Exception;

	public void copyReadingValue(List<Map<String,Object>> porp, FacilioModule module, long targetOrgId, long targetAssetId, long timeDiff, List<FacilioField> fields, long targetId)throws Exception;

	public List getEnergyMeterList() throws Exception;

	public Unit getOrgDisplayUnit(int metricId) throws Exception;

	public Unit getOrgDisplayUnit(Metric metric) throws Exception;

	public boolean updateOrgUnit(int metric,int unit) throws Exception;

	public void updateOrgUnitsList(JSONObject metricUnitMap) throws Exception;

	public List<OrgUnitsContext> getOrgUnitsList() throws Exception;

	void runDemoRollup(long orgId, long timeDuration) throws Exception;

	List<Map<String, Object>> getOrgUserApps(long orgUserId) throws Exception;

	List<Map<String, Object>> getApplication(long appId) throws Exception;

	Map<String, Object> getOrgUser(long iamOrgUserId) throws Exception;

	public Long getDefaultApplicationId() throws Exception;

	public Long getOrgUserIdForPeople(long peopleID,long appID) throws Exception;
	void setApplicationStatus(String[] applications, long orgId) throws Exception;
	List<ApplicationContext> getAllApplications(long orgId) throws Exception;
}