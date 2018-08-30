package com.facilio.accounts.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

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
		
		AccountUtil.getRoleBean().createSuperdminRoles(orgId);
		
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
			return createOrgFromProps(props.get(0));
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
			return createOrgFromProps(props.get(0));
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
			org = createOrgFromProps(result);
			if(result.get("customDomain") != null) {
				org.setDomain((String)result.get("customDomain"));
			} else {
				org.setDomain((String)result.get("domain")+"."+ AwsUtil.getConfig("portal.domain"));
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
			org = createOrgFromProps(props.get(0));
		}
		return org;
	}
	
	private Organization createOrgFromProps(Map<String, Object> prop) throws Exception {
		Organization org = FieldUtil.getAsBeanFromMap(prop, Organization.class);
		if (org.getLogoId() > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStoreFromOrg(org.getId());
			org.setLogoUrl(fs.getPrivateUrl(org.getLogoId()));
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
				User user = UserBeanImpl.createUserFromProps(prop);
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
				.andCustomWhere("ORGID = ? AND USER_TYPE = ? AND DELETED_TIME = -1", orgId, AccountConstants.UserType.USER.getValue());
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				User user = UserBeanImpl.createUserFromProps(prop);
				user.setAccessibleSpace(UserBeanImpl.getAccessibleSpaceList(user.getOuid()));
				user.setGroups(UserBeanImpl.getAccessibleGroupList(user.getOuid()));
				users.add(user);
			}
			return users;
		}
		return null;
	}
	
	@Override
	public List<User> getOrgUsers(long orgId, boolean status) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND USER_STATUS = ? AND USER_TYPE = ? AND DELETED_TIME = -1", orgId, status, AccountConstants.UserType.USER.getValue());
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				users.add(UserBeanImpl.createUserFromProps(prop));
			}
			return users;
		}
		return null;
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
				users.add(UserBeanImpl.createUserFromProps(prop));
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
				users.add(UserBeanImpl.createUserFromProps(prop));
			}
			return users;
		}
		return null;
	}
	
	@Override
	public User getSuperAdmin(long orgId) throws Exception {
		
		Role superAdminRole = AccountUtil.getRoleBean().getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		
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
			return UserBeanImpl.createUserFromProps(props.get(0));
		}
		return null;
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