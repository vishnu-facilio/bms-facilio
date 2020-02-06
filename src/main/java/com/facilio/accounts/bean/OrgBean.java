package com.facilio.accounts.bean;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.IAMUser.AppType;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public interface OrgBean {

	public boolean updateOrg(long orgId, Organization org) throws Exception;
	
	public boolean deleteOrg(long orgId) throws Exception;
	
	public Organization getOrg(long orgId) throws Exception;
	
	public Organization getOrg(String orgDomain) throws Exception;
	
	public Organization getPortalOrg(long portalId, AppType appType) throws Exception;
	
	public PortalInfoContext getPortalInfo(long id, boolean isPortalID) throws Exception;
	
//	public PortalInfoContext getPortalInfo(long o) throws Exception;

//    Organization getPortalOrg(String orgDomain) throws Exception;

	public List<User> getOrgPortalUsers(long orgId) throws Exception;

	public List<User> getAllOrgUsers(long orgId) throws Exception;
	
	public List<User> getOrgUsers(long orgId, boolean status) throws Exception;
	
	public List<User> getOrgUsers(long orgId, Criteria criteria) throws Exception;
	
	public Map<Long, User> getOrgUsersAsMap(long orgId, Criteria criteria) throws Exception;

	public List<User> getActiveOrgUsers(long orgId) throws Exception;

	public User getSuperAdmin(long orgId) throws Exception;
	
	public List getEnergyMeterList() throws Exception ;

	public long getFeatureLicense() throws Exception;
	
	public boolean isFeatureEnabled(FeatureLicense featureLicense) throws Exception;

	public long addLicence(long summodule) throws Exception;
	
	public JSONObject orgInfo() throws Exception;
	
	public void updateLoggerLevel (int level, long orgId) throws Exception;
	
	public List<Organization> getOrgs() throws Exception;
	
	public void copyReadingValue(List<Map<String,Object>> porp, FacilioModule module, long targetOrgId, long targetAssetId, long timeDiff, List<FacilioField> fields, long targetId)throws Exception;
}