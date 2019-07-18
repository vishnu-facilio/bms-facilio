package com.iam.accounts.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.iam.accounts.bean.OrgBeanv2;

public class OrgBeanImplv2 implements OrgBeanv2 {

	@Override
	public boolean updateOrgv2(long orgId, Organization org) throws Exception {
		
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
	public boolean deleteOrgv2(long orgId) throws Exception {
		
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
	public Organization getOrgv2(long orgId) throws Exception {
		
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
	public Organization getOrgv2(String orgDomain) throws Exception {
		
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
	public Organization getPortalOrgv2(Long portalId) throws Exception {
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
	public Organization getPortalOrgv2(String orgDomain) throws Exception {
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
    public List<User> getOrgPortalUsersv2(long orgId) throws Exception {
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
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.addAll(AccountConstants.getAppUserFields());
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
				User user = UserBeanImplv2.createUserFromProps(prop, true, true, true);
				user.setFacilioAuth(true);
				users.add(user);
			}
			return users;
		}
        return null;
    }

    @Override
	public List<User> getAllOrgUsersv2(long orgId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		//		fields.addAll(FieldFactory.getShiftUserRelModuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
//				.leftJoin("Shift_User_Rel")
//				.on("ORG_Users.ORG_USERID = Shift_User_Rel.ORG_USERID")
				.andCustomWhere("ORG_Users.ORGID = ? AND USER_TYPE = ? AND DELETED_TIME = -1", orgId, AccountConstants.UserType.USER.getValue());
		
		if(AccountUtil.getCurrentAccount().getUser() == null){
			return null;
		}
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				User user = UserBeanImplv2.createUserFromProps(prop, true, true, false);
				users.add(user);
			}
			return users;
		}
		return null;
	}
	
	@Override
	public List<User> getOrgUsersv2(long orgId, boolean status) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(AccountConstants.getAppOrgUserFields());
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("userStatus"), String.valueOf(status), NumberOperators.EQUALS));
		return getOrgUsersv2(orgId, criteria);
	}
	
	@Override
	public List<User> getOrgUsersv2(long orgId, Criteria criteria) throws Exception {
		List<Map<String, Object>> props = fetchOrgUserProps(orgId, criteria);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				users.add(UserBeanImplv2.createUserFromProps(prop, true, false, false));
			}
			return users;
		}
		return null;
	}
	
	@Override
	public Map<Long, User> getOrgUsersAsMapv2(long orgId, Criteria criteria) throws Exception {
		List<Map<String, Object>> props = fetchOrgUserProps(orgId, criteria);
		if (props != null && !props.isEmpty()) {
			Map<Long, User> users = new HashMap<>();
			for(Map<String, Object> prop : props) {
				User user = UserBeanImplv2.createUserFromProps(prop, true, false, false);
				users.put(user.getId(), user);
			}
			return users;
		}
		return null;
	}
	
	private List<Map<String, Object>> fetchOrgUserProps (long orgId, Criteria criteria) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		
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

	public List<User> getActiveOrgUsersv2(long orgId) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());

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
				users.add(UserBeanImplv2.createUserFromProps(prop, true, false, false));
			}
			return users;
		}
		return null;
	}
	
	public List<User> getRequestersv2(long orgId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		
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
				users.add(UserBeanImplv2.createUserFromProps(prop, true, false, false));
			}
			return users;
		}
		return null;
	}
	
	
	public long getPortalIdv2( ) throws Exception {
		Organization org = AccountUtil.getCurrentOrg();
		if(org.getPortalId() > 0) {
			return org.getPortalId();
		}
		Organization portalOrg = AccountUtil.getOrgBean().getPortalOrg(org.getDomain());
		long portalId = portalOrg.getPortalId();
		org.setPortalId(portalId);
		return portalId;
	}
	
	
	public JSONObject orgInfov2() throws Exception{
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
	public Organization createOrgv2(Organization org) throws Exception {
		// TODO Auto-generated method stub
		Organization existingOrg = getOrgv2(org.getDomain());
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
		org.setId(orgId);
		return org;
	}

}