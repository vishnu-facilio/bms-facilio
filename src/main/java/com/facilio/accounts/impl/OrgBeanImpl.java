package com.facilio.accounts.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.copyAssetReadingCommand;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class OrgBeanImpl implements OrgBean {

	@Override
	public boolean updateOrg(long orgId, Organization org) throws Exception {
		return IAMOrgUtil.updateOrg(orgId, org);
	}

	@Override
	public boolean deleteOrg(long orgId) throws Exception {
		return IAMOrgUtil.deleteOrg(orgId);
	}

	@Override
	public Organization getOrg(long orgId) throws Exception {
		return IAMOrgUtil.getOrg(orgId);
	}

	@Override
	public Organization getOrg(String orgDomain) throws Exception {
		return IAMOrgUtil.getOrg(orgDomain);
	}
	
	@Override
	public List<Organization> getOrgs() throws Exception {
		return IAMOrgUtil.getOrgs();
	}
	
	@Override
	public PortalInfoContext getPortalInfo(long id, boolean isPortalId) throws Exception {
		FacilioModule portalInfoModule = AccountConstants.getPortalInfoModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getPortalCustomDomainFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(portalInfoModule.getTableName());
		if (isPortalId) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("PORTALID", "portalId", String.valueOf(id), NumberOperators.EQUALS));
		} else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(id), NumberOperators.EQUALS));
		}
		Map<String, Object> portalMap = selectBuilder.fetchFirst();
		return FieldUtil.getAsBeanFromMap(portalMap, PortalInfoContext.class);
	}
	
	@Override
	public List<User> getAppUsers(long orgId, long appId, boolean checkAccessibleSites)
			throws Exception {
		return getAppUsers(orgId, appId, checkAccessibleSites, false);
	}
	
    @Override
	public List<User> getAppUsers(long orgId, long appId, boolean checkAccessibleSites, boolean fetchNonAppUsers) throws Exception {
    	
	    	User currentUser = AccountUtil.getCurrentAccount().getUser();
		if(currentUser == null){
			return null;
		}
		
	    	if(appId <= 0) {
	    		appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
	    	}
	    	
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getApplicationIdField());
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
				.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS))
				;
		
		if (fetchNonAppUsers) {
			List<Long> appUserIds = getAppUserIds(appId, selectBuilder, fieldMap);
			if (appUserIds != null) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ouid"), appUserIds, NumberOperators.NOT_EQUALS));
			}
			
			appId = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId();
		}
		
		selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("applicationId"), String.valueOf(appId), NumberOperators.EQUALS));
		
		if(checkAccessibleSites) {
			List<Long> accessibleSpace = currentUser.getAccessibleSpace();
			String siteIdCondition = "";
			if (accessibleSpace != null && !accessibleSpace.isEmpty()) {
				Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);
				List<Long> siteIds = new ArrayList<>();
				for (BaseSpaceContext baseSpace: idVsBaseSpace.values()) {
					if (baseSpace.getSiteId() > 0) {
						siteIds.add(baseSpace.getSiteId());
					}
				}
				
				if (!siteIds.isEmpty()) {
					siteIdCondition = StringUtils.join(siteIds, ',');
					siteIdCondition = "(" + siteIdCondition + ")";
				}
			}
			
			long currentSiteId = AccountUtil.getCurrentSiteId();
			
			String whereCondition = "";
			if (currentSiteId > 0 && !siteIdCondition.isEmpty()) {
				whereCondition = "((not exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID)) OR (exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID AND Accessible_Space.SITE_ID = " + String.valueOf(currentSiteId);
				whereCondition += " and Accessible_Space.SITE_ID IN" + siteIdCondition + ")))" ;
			} else if (currentSiteId > 0 && siteIdCondition.isEmpty()) {
				whereCondition = "((not exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID)) OR (exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID AND Accessible_Space.SITE_ID = " + String.valueOf(currentSiteId) + ")))"; 
			} else if (currentSiteId <= 0 && !siteIdCondition.isEmpty()) {
				whereCondition = "((not exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID)) OR (exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID ";
				whereCondition += " and Accessible_Space.SITE_ID IN" + siteIdCondition + ")))" ;
			}
			
			
			if(!whereCondition.isEmpty()) {
				selectBuilder
					.andCustomWhere(whereCondition);
			}
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
			for(Map<String, Object> prop : props) {
				User user = UserBeanImpl.createUserFromProps(prop, true, true, false);
				UserBean userBean = (UserBean) BeanFactory.lookup("UserBean", user.getOrgId());
				user.setGroups(userBean.getAccessibleGroupList(user.getOuid()));
				users.add(user);
			}
			return users;
		}
		return null;
	}
    
    private List<Long> getAppUserIds(long appId, GenericSelectRecordBuilder builder, Map<String, FacilioField> fieldMap) throws Exception {
    		List<Long> userIds = null;
    		
    		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder(builder)
    					.select(Collections.singletonList(fieldMap.get("ouid")))
    					.andCondition(CriteriaAPI.getCondition(fieldMap.get("applicationId"), String.valueOf(appId), NumberOperators.EQUALS));
    					;
    		
    		List<Map<String, Object>> props = selectBuilder.get();
    		if (CollectionUtils.isNotEmpty(props)) {
    			userIds = props.stream().map(prop -> (long) prop.get("ouid")).collect(Collectors.toList());
    		}
    		return userIds;
    }
	
	@Override
	public List<User> getOrgUsers(long orgId, boolean status) throws Exception {
		List<User> userList = getDefaultAppUsers(orgId);
		if(CollectionUtils.isNotEmpty(userList)) {
			List<User> finalUserList = new ArrayList<User>();
			for(User user : userList) {
				if(user.isActive() == status) {
					finalUserList.add(user);
				}
			}
			return finalUserList;
		}
		return null;
	}
	
	@Override
	public List<User> getDefaultAppUsers(long orgId) throws Exception {
		return getAppUsers(orgId, ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP), false);
	}
	
	@Override
	public Map<Long, User> getOrgUsersAsMap(long orgId) throws Exception {
		
		List<User> userList = getDefaultAppUsers(orgId);
		Map<Long, User> map = new HashMap<Long, User>();
		if(CollectionUtils.isNotEmpty(userList)) {
			for(User u : userList) {
				map.put(u.getId(), u);
			}
			return map;
		}
		return null;
	}
	
	private List<Map<String, Object>> fetchOrgUserProps (long orgId, Criteria criteria, List<Long> applicationIds) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_User_Apps.ORG_USERID = ORG_Users.ORG_USERID")
				
				;
		
		if(criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		if(CollectionUtils.isNotEmpty(applicationIds)) {
			StringJoiner idString = new StringJoiner(",");
			for(long id : applicationIds) {
				idString.add(String.valueOf(id));
			}
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID", "applicationId", idString.toString(), NumberOperators.EQUALS));
		}
							
		return selectBuilder.get();
	}

	public List<User> getActiveOrgUsers(long orgId) throws Exception {

		List<User> userList = getDefaultAppUsers(orgId);
		if(CollectionUtils.isNotEmpty(userList)) {
			List<User> finalUserList = new ArrayList<User>();
			for(User user : userList) {
				if(user.isActive() && user.isUserVerified() && user.isInviteAcceptStatus()) {
					finalUserList.add(user);
				}
			}
			return finalUserList;
		}
		return null;
	}
	
	@Override
	public User getSuperAdmin(long orgId) throws Exception {
		
		Role superAdminRole = AccountUtil.getRoleBean().getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN, false);
		long applicationId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		
		if(superAdminRole == null) {
			return null;
		}
		
		List<FacilioField> fields = new ArrayList<>();
		 fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField(AccountConstants.getAppOrgUserModule()));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
				.innerJoin("Role")
				.on("ORG_Users.ROLE_ID = Role.ROLE_ID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ROLE_ID", "roleId", String.valueOf(superAdminRole.getRoleId()), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID", "applicationId", String.valueOf(applicationId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
			if(CollectionUtils.isNotEmpty(props)) {
				return UserBeanImpl.createUserFromProps(props.get(0), true, false, false);
			}
		}
		return null;
	}
	

	public long getFeatureLicense() throws Exception{

//    	String orgidString = String.valueOf(orgId);
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getFeatureLicenseFields())
				.table(AccountConstants.getFeatureLicenseModule().getTableName());
//				.andCondition(CriteriaAPI.getCondition(AccountConstants.getOrgIdField(AccountConstants.getFeatureLicenseModule()), orgidString, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			Map<String, Object> modulemap=props.get(0);
			return ((long) modulemap.get("module"));
		}
		return -1;
	}
	
	@Override
	public boolean isFeatureEnabled(FeatureLicense featureLicense) throws Exception {
		return AccountUtil.isFeatureEnabled(featureLicense);
	}

	public long  addLicence(long summodule) throws Exception{
    	GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getFeatureLicenseModule().getTableName())
				.fields(AccountConstants.getFeatureLicenseFields());
    	
    	updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
	
		Map<String, Object> props = new HashMap<>();
		props.put("module", summodule);
	 long value = updateBuilder.update(props);
	 return value;
	}
	
	public JSONObject orgInfo() throws Exception{
		JSONObject result = new JSONObject();
    	FacilioModule module = AccountConstants.getOrgInfoModule();
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgInfoFields())
				.table(module.getTableName());
//				.andCustomWhere("ORGID = ?", orgId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				result.put(prop.get("name"), prop.get("value"));
			}
			
		}
			
		return result;
	}

	@Override 
    public List<User> getOrgPortalUsers(long orgId, long appId) throws Exception { 
      	return getAppUsers(orgId, appId, false);
    }
	
	@Override 
    public List<User> getRequesterTypeUsers(long orgId, boolean status) throws Exception { 
  		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		
		String mainAppLinkNames = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP +","+ FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP;
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
		;
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
	
		fields.add(AccountConstants.getApplicationIdField());
		selectBuilder.innerJoin("ORG_User_Apps")
			.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
			.innerJoin("Application")
			.on("Application.ID = ORG_User_Apps.APPLICATION_ID")
			;
		selectBuilder.andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", mainAppLinkNames, StringOperators.ISN_T));
					
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
			for(Map<String, Object> prop : props) {
				User user = UserBeanImpl.createUserFromProps(prop, false, false, false);
				if(status) {
					if(user.isActive()) {
						users.add(user);
					}
				}
				else {
					users.add(user);
				}
			}
			return users;
		}
      	return null;
    }

	@Override
	public void updateLoggerLevel(int level, long orgId) throws Exception {
		 // TODO Auto-generated method stub 
		IAMOrgUtil.updateLoggerLevel(level, orgId);
	}

	@Override
	public void copyReadingValue(List<Map<String, Object>> prop, FacilioModule module,long orgId,long assetId,long timeDiff, List<FacilioField> fields , long targetmodId) throws Exception {
		// TODO Auto-generated method stub
		copyAssetReadingCommand.insertAssetCopyValue(prop,module,orgId,assetId,timeDiff,fields , targetmodId);
	} 
	
	@Override
	public Organization getPortalOrg(long portalId, AppDomainType appType) throws Exception {
		
		PortalInfoContext portalInfo = getPortalInfo(portalId, true);
		if (portalInfo == null) {
			throw new IllegalArgumentException("Portal not found");
		}
		Organization org = getOrg(portalInfo.getOrgId());
		if (org == null) {
			throw new IllegalArgumentException("Organization not found");
		}
		org.setPortalId(portalId);

			FileStore fs = FacilioFactory.getFileStoreFromOrg(org.getId());
			org.setLogoUrl(fs.getPrivateUrl(org.getLogoId(), true));
			org.setOriginalUrl(fs.orginalFileUrl(org.getLogoId()));

		if(appType == AppDomainType.SERVICE_PORTAL) {
	        if(portalInfo.getCustomDomain() != null) {
	            org.setDomain(portalInfo.getCustomDomain()); 
	        } else { 
	            org.setDomain(org.getDomain() + "." + FacilioProperties.getOccupantAppDomain());
	        } 
		}
		else if(appType == AppDomainType.TENANT_PORTAL) {
			org.setDomain(org.getDomain() + FacilioProperties.getTenantAppDomain());
		}
		else if(appType == AppDomainType.VENDOR_PORTAL) {
			org.setDomain(org.getDomain() + FacilioProperties.getVendorAppDomain());
		}
		else if(appType == AppDomainType.CLIENT_PORTAL) {
			org.setDomain(org.getDomain() + FacilioProperties.getClientAppDomain());
		}
		
	             
	   return org;
	}
	
	@Override
	public List getEnergyMeterList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, "all");
		
		FacilioChain getEnergyMeterListChain = FacilioChainFactory.getEnergyMeterListChain();
		getEnergyMeterListChain.execute(context);
		
		return ((List<EnergyMeterContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST));
	}
	
}