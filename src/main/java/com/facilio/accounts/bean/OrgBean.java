package com.facilio.accounts.bean;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.OrgUnitsContext;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.PortalInfoContext;
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
	
	public List<User> getAppUsers(long orgId, long appId, boolean checkAccessibleSites, boolean fetchNonAppUsers) throws Exception;
	
	public List<User> getOrgUsers(long orgId, boolean status) throws Exception;
	
	public List<User> getDefaultAppUsers(long orgId) throws Exception;
	
	public Map<Long, User> getOrgUsersAsMap(long orgId) throws Exception;

	public List<User> getActiveOrgUsers(long orgId) throws Exception;

	public User getSuperAdmin(long orgId) throws Exception;
	
	public long getFeatureLicense() throws Exception;
	
	public boolean isFeatureEnabled(FeatureLicense featureLicense) throws Exception;

	public int addLicence(long summodule) throws Exception;
	
	public JSONObject orgInfo() throws Exception;
	
	public void updateLoggerLevel (int level, long orgId) throws Exception;
	
	public List<Organization> getOrgs() throws Exception;
	
	public Organization getPortalOrg(long portalId, AppDomainType appType) throws Exception;
		
	public void copyReadingValue(List<Map<String,Object>> porp, FacilioModule module, long targetOrgId, long targetAssetId, long timeDiff, List<FacilioField> fields, long targetId)throws Exception;
	
	public List getEnergyMeterList() throws Exception;

	public Unit getOrgDisplayUnit(int metricId) throws Exception;

	public Unit getOrgDisplayUnit(Metric metric) throws Exception;

	public boolean updateOrgUnit(int metric,int unit) throws Exception;

	public void updateOrgUnitsList(JSONObject metricUnitMap) throws Exception;

	public List<OrgUnitsContext> getOrgUnitsList() throws Exception;
}