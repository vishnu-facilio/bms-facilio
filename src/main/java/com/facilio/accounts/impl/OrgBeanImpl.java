package com.facilio.accounts.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.UserUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.copyAssetReadingCommand;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

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
	public Organization getPortalOrg(long portalId) throws Exception {
		
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


        if(portalInfo.getCustomDomain() != null) {
            org.setDomain(portalInfo.getCustomDomain()); 
        } else { 
            org.setDomain(org.getDomain() + "." + FacilioProperties.getConfig("portal.domain"));
        } 
	             
	   return org;
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
	public List<User> getAllOrgUsers(long orgId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
		;
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("USER_TYPE", "userType", String.valueOf(AccountConstants.UserType.USER.getValue()), NumberOperators.EQUALS));
				
		User currentUser = AccountUtil.getCurrentAccount().getUser();
		if(currentUser == null){
			return null;
		}
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
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			UserUtil.setIAMUserProps(props, orgId, false);
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
	
	@Override
	public List<User> getOrgUsers(long orgId, boolean status) throws Exception {
		List<User> userList = getOrgUsers(orgId, null);
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
	public List<User> getOrgUsers(long orgId, Criteria criteria) throws Exception {
		List<Map<String, Object>> props = fetchOrgUserProps(orgId, criteria);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			UserUtil.setIAMUserProps(props, orgId, false);
			for(Map<String, Object> prop : props) {
				User user = UserBeanImpl.createUserFromProps(prop, true, false, false);
				if(user.getUserType() == UserType.USER.getValue()) {
					users.add(user);
				}
			}
			return users;
		}
		return null;
	}
	
	@Override
	public Map<Long, User> getOrgUsersAsMap(long orgId, Criteria criteria) throws Exception {
		List<Map<String, Object>> props = fetchOrgUserProps(orgId, criteria);
		if (props != null && !props.isEmpty()) {
			Map<Long, User> users = new HashMap<>();
			UserUtil.setIAMUserProps(props, orgId, true);
			for(Map<String, Object> prop : props) {
				User user = UserBeanImpl.createUserFromProps(prop, true, false, false);
				if(user.getUserType() == UserType.USER.getValue()) {
					users.put(user.getId(), user);
				}
			}
			return users;
		}
		return null;
	}
	
	private List<Map<String, Object>> fetchOrgUserProps (long orgId, Criteria criteria) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				;
		
		if(criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
						
		return selectBuilder.get();
	}

	public List<User> getActiveOrgUsers(long orgId) throws Exception {

		List<User> userList = getOrgUsers(orgId, null);
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
		
		if(superAdminRole == null) {
			return null;
		}
		
		List<FacilioField> fields = new ArrayList<>();
		 fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField(AccountConstants.getAppOrgUserModule()));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin("Role")
				.on("ORG_Users.ROLE_ID = Role.ROLE_ID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ROLE_ID", "roleId", String.valueOf(superAdminRole.getRoleId()), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			UserUtil.setIAMUserProps(props, orgId, false);
			if(CollectionUtils.isNotEmpty(props)) {
				return UserBeanImpl.createUserFromProps(props.get(0), true, false, false);
			}
		}
		return null;
	}
	

	public List getEnergyMeterList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, "all");
		
		FacilioChain getEnergyMeterListChain = FacilioChainFactory.getEnergyMeterListChain();
		getEnergyMeterListChain.execute(context);
		
		return ((List<EnergyMeterContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST));
	}
		
	public int getFeatureLicense() throws Exception{

//    	String orgidString = String.valueOf(orgId);
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getFeatureLicenseFields())
				.table(AccountConstants.getFeatureLicenseModule().getTableName());
//				.andCondition(CriteriaAPI.getCondition(AccountConstants.getOrgIdField(AccountConstants.getFeatureLicenseModule()), orgidString, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			Map<String, Object> modulemap=props.get(0);
			return (int) modulemap.get("module");
		}
		return -1;
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
    public List<User> getOrgPortalUsers(long orgId) throws Exception { 
       
		List<Map<String, Object>> props = fetchOrgUserProps(orgId, null);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			UserUtil.setIAMUserProps(props, orgId, false);
			for(Map<String, Object> prop : props) {
				User user = UserBeanImpl.createUserFromProps(prop, true, false, false);
				if(user.getUserType() == UserType.REQUESTER.getValue()) {
					user.setFacilioAuth(true);
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
	public void copyReadingValue(List<Map<String, Object>> prop, FacilioModule module,long orgId) throws Exception {
		// TODO Auto-generated method stub
		copyAssetReadingCommand.insertAssetCopyValue(prop,module,orgId);
	} 
	
	
}