package com.facilio.accounts.impl;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrgBeanImpl implements OrgBean {

	@Override
	public long createOrg(Organization org) throws Exception {
		
		Organization existingOrg = getOrg(org.getDomain());
		if (existingOrg != null) {
			throw new AccountException(AccountException.ErrorCode.ORG_DOMAIN_ALREADY_EXISTS, "The given domain already registered.");
		}
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getOrgModule().getTableName())
				.fields(AccountConstants.getOrgFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(org);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		long orgId = (Long) props.get("id");
		
		AccountUtil.getRoleBean(orgId).createSuperdminRoles(orgId);
		
		return orgId;
	}

	@Override
	public boolean updateOrg(long orgId, Organization org) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getOrgModule().getTableName())
				.fields(AccountConstants.getOrgFields())
				.andCustomWhere("ORGID = ? AND DELETED_TIME = -1", orgId);
		
		Map<String, Object> props = FieldUtil.getAsProperties(org);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteOrg(long orgId) throws Exception {
		
		FacilioField deletedTime = new FacilioField();
		deletedTime.setName("deletedTime");
		deletedTime.setDataType(FieldType.NUMBER);
		deletedTime.setColumnName("DELETED_TIME");
		deletedTime.setModule(AccountConstants.getOrgModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(deletedTime);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getOrgModule().getTableName())
				.fields(fields)
				.andCustomWhere("ORGID = ? AND DELETED_TIME = -1", orgId);
		
		Map<String, Object> props = new HashMap<>();
		props.put("deletedTime", System.currentTimeMillis());
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public Organization getOrg(long orgId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgFields())
				.table(AccountConstants.getOrgModule().getTableName())
				.andCustomWhere("ORGID = ? AND DELETED_TIME = -1", orgId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return createOrgFromProps(props.get(0), false);
		}
		return null;
	}

	@Override
	public Organization getOrg(String orgDomain) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgFields())
				.table(AccountConstants.getOrgModule().getTableName())
				.andCustomWhere("FACILIODOMAINNAME = ? AND DELETED_TIME = -1", orgDomain);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return createOrgFromProps(props.get(0), false);
		}
		return null;
	}

	@Override
	public Organization getPortalOrg(Long portalId) throws Exception {
		Organization org = null;
		FacilioModule portalInfoModule = AccountConstants.getPortalInfoModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getOrgFields());
		fields.addAll(AccountConstants.getPortalCustomDomainFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getOrgModule().getTableName())
				.innerJoin(portalInfoModule.getTableName())
				.on(AccountConstants.getOrgModule().getTableName()+".ORGID = PortalInfo.ORGID")
				.andCustomWhere("PORTALID = ? AND DELETED_TIME = -1", portalId);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<String, Object> result = props.get(0); 
			org = createOrgFromProps(result, true);
			if(result.get("customDomain") != null) {
				org.setDomain((String)result.get("customDomain"));
			} else {
				org.setDomain(result.get("domain") + "." + AwsUtil.getConfig("portal.domain"));
			}
			
		}
		return org;
	}
	
	@Override
	public Organization getPortalOrg(String orgDomain) throws Exception {
		Organization org = null;
		FacilioModule portalInfoModule = AccountConstants.getPortalInfoModule();
		FacilioField portalId = new FacilioField();
		portalId.setName("portalId");
		portalId.setDataType(FieldType.NUMBER);
		portalId.setColumnName("PORTALID");
		portalId.setModule(portalInfoModule);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(portalId);
		fields.addAll(AccountConstants.getOrgFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getOrgModule().getTableName())
				.innerJoin(portalInfoModule.getTableName())
				.on(AccountConstants.getOrgModule().getTableName()+".ORGID = PortalInfo.ORGID")
				.andCustomWhere("FACILIODOMAINNAME = ? AND DELETED_TIME = -1", orgDomain);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			org = createOrgFromProps(props.get(0), true);
		}
		return org;
	}
	
	private Organization createOrgFromProps(Map<String, Object> prop, boolean isPortalRequest) throws Exception {
		Organization org = FieldUtil.getAsBeanFromMap(prop, Organization.class);
		if (org.getLogoId() > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStoreFromOrg(org.getId());
			org.setLogoUrl(fs.getPrivateUrl(org.getLogoId(), isPortalRequest));
			org.setOriginalUrl(fs.orginalFileUrl(org.getLogoId()));
		}
		return org;
	}
	
    @Override
    public List<User> getOrgPortalUsers(long orgId) throws Exception {
		FacilioModule portalUsersModule = AccountConstants.getPortalUserModule();
		FacilioModule portalInfoModule = AccountConstants.getPortalInfoModule();
		FacilioField portalId = new FacilioField();
		portalId.setName("portalId");
		portalId.setDataType(FieldType.NUMBER);
		portalId.setColumnName("PORTALID");
		portalId.setModule(portalInfoModule);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(portalId);
		fields.addAll(AccountConstants.getPortalUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		fields.addAll(AccountConstants.getUserFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(portalUsersModule.getTableName())
				.innerJoin(portalInfoModule.getTableName())
				.on(portalUsersModule.getTableName()+".PORTALID = PortalInfo.PORTALID")
				.innerJoin("Users")
				.on(portalUsersModule.getTableName()+".USERID = Users.USERID")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID AND ORG_Users.USER_TYPE=2")
				.andCustomWhere("PortalInfo.ORGID="+ orgId);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				User user = UserBeanImpl.createUserFromProps(prop, true, true, true);
				user.setFacilioAuth(true);
				users.add(user);
			}
			return users;
		}
        return null;
    }

    @Override
	public List<User> getAllOrgUsers(long orgId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		fields.addAll(FieldFactory.getShiftUserRelModuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.leftJoin("Shift_User_Rel")
				.on("ORG_Users.ORG_USERID = Shift_User_Rel.ORG_USERID")
				.andCustomWhere("ORG_Users.ORGID = ? AND USER_TYPE = ? AND DELETED_TIME = -1", orgId, AccountConstants.UserType.USER.getValue());
		
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
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(AccountConstants.getOrgUserFields());
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("userStatus"), String.valueOf(status), NumberOperators.EQUALS));
		return getOrgUsers(orgId, criteria);
	}
	
	@Override
	public List<User> getOrgUsers(long orgId, Criteria criteria) throws Exception {
		List<Map<String, Object>> props = fetchOrgUserProps(orgId, criteria);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				users.add(UserBeanImpl.createUserFromProps(prop, true, false, false));
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
			for(Map<String, Object> prop : props) {
				User user = UserBeanImpl.createUserFromProps(prop, true, false, false);
				users.put(user.getId(), user);
			}
			return users;
		}
		return null;
	}
	
	private List<Map<String, Object>> fetchOrgUserProps (long orgId, Criteria criteria) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND USER_TYPE = ? AND DELETED_TIME = -1", orgId, AccountConstants.UserType.USER.getValue())
				.andCriteria(criteria);
				;
		
		return selectBuilder.get();
	}

	public List<User> getActiveOrgUsers(long orgId) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND USER_STATUS = 1 AND INVITATION_ACCEPT_STATUS = 1 AND USER_VERIFIED = 1 AND USER_TYPE = ? AND DELETED_TIME = -1", orgId, AccountConstants.UserType.USER.getValue());

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				users.add(UserBeanImpl.createUserFromProps(prop, true, false, false));
			}
			return users;
		}
		return null;
	}
	
	public List<User> getRequesters(long orgId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND DELETED_TIME = -1", orgId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				users.add(UserBeanImpl.createUserFromProps(prop, true, false, false));
			}
			return users;
		}
		return null;
	}
	
	@Override
	public User getSuperAdmin(long orgId) throws Exception {
		
		Role superAdminRole = AccountUtil.getRoleBean(orgId).getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN, false);
		
		if(superAdminRole == null) {
			return null;
		}
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		fields.add(FieldFactory.getOrgIdField(AccountConstants.getOrgUserModule()));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.innerJoin("Role")
				.on("ORG_Users.ROLE_ID = Role.ROLE_ID")
				.andCustomWhere("ORG_Users.ORGID = ? AND ORG_Users.ROLE_ID = ? AND DELETED_TIME = -1", orgId, superAdminRole.getRoleId());
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return UserBeanImpl.createUserFromProps(props.get(0), true, false, false);
		}
		return null;
	}
	
	public long getPortalId( ) throws Exception {
		Organization org = AccountUtil.getCurrentOrg();
		if(org.getPortalId() > 0) {
			return org.getPortalId();
		}
		Organization portalOrg = AccountUtil.getOrgBean().getPortalOrg(org.getDomain());
		long portalId = portalOrg.getPortalId();
		org.setPortalId(portalId);
		return portalId;
	}
	
	
	
	public List getEnergyMeterList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, "all");
		
		Chain getEnergyMeterListChain = FacilioChainFactory.getEnergyMeterListChain();
		getEnergyMeterListChain.execute(context);
		
		return ((List<EnergyMeterContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST));
	}
	
	
	
	public void testTransaction(String prefix) throws Exception
	{
		Organization org = new Organization();
	//	String prefix = "abi";
		org.setDomain(prefix+1);
		createOrg(org);
		org = new Organization();
		org.setDomain(prefix+2);
		createOrg(org);
		org = new Organization();
		org.setDomain(prefix+3);
		createOrg(org);
		org = new Organization();
		org.setDomain(prefix+4);
		createOrg(org);
		if(false)
		{
		throw new Exception();
		}
		org = new Organization();
		org.setDomain(prefix+5);
		createOrg(org);
		org = new Organization();
		org.setDomain(prefix+6);
		createOrg(org);
		
		return;
	}
}