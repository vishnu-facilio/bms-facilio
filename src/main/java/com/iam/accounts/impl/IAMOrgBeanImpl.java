package com.iam.accounts.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.iam.accounts.bean.IAMOrgBean;
import com.iam.accounts.exceptions.AccountException;
import com.iam.accounts.util.IAMAccountConstants;

public class IAMOrgBeanImpl implements IAMOrgBean {

	@Override
	public boolean updateOrgv2(long orgId, Organization org) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getOrgModule().getTableName())
				.fields(IAMAccountConstants.getOrgFields());

		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
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
		deletedTime.setModule(IAMAccountConstants.getOrgModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(deletedTime);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getOrgModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
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
				.select(IAMAccountConstants.getOrgFields())
				.table(IAMAccountConstants.getOrgModule().getTableName());
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return createOrgFromProps(props.get(0));
		}
		return null;
	}

	@Override
	public Organization getOrgv2(String orgDomain) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table(IAMAccountConstants.getOrgModule().getTableName());
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("FACILIODOMAINNAME", "domainName", orgDomain, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return createOrgFromProps(props.get(0));
		}
		return null;
	}

		
	private Organization createOrgFromProps(Map<String, Object> prop) throws Exception {
		Organization org = FieldUtil.getAsBeanFromMap(prop, Organization.class);
		if (org.getLogoId() > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStoreFromOrg(org.getId());
			org.setLogoUrl(fs.getPrivateUrl(org.getLogoId(), false));
			org.setOriginalUrl(fs.orginalFileUrl(org.getLogoId()));
		}
		return org;
	}
	
    
    @Override
	public List<IAMUser> getAllOrgUsersv2(long orgId) throws Exception {
		
    	List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<IAMUser> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				IAMUser user = IAMUserBeanImpl.createUserFromProps(prop, true, true, false);
				users.add(user);
			}
			return users;
		}
		return null;
	}
	
	@Override
	public List<IAMUser> getOrgUsersv2(long orgId, boolean status) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(AccountConstants.getAppOrgUserFields());
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("userStatus"), String.valueOf(status), NumberOperators.EQUALS));
		return getOrgUsersv2(orgId, criteria);
	}
	
	@Override
	public List<IAMUser> getOrgUsersv2(long orgId, Criteria criteria) throws Exception {
		List<Map<String, Object>> props = fetchOrgUserProps(orgId, criteria);
		if (props != null && !props.isEmpty()) {
			List<IAMUser> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				users.add(IAMUserBeanImpl.createUserFromProps(prop, true, false, false));
			}
			return users;
		}
		return null;
	}
	
	@Override
	public Map<Long, IAMUser> getOrgUsersAsMapv2(long orgId, Criteria criteria) throws Exception {
		List<Map<String, Object>> props = fetchOrgUserProps(orgId, criteria);
		if (props != null && !props.isEmpty()) {
			Map<Long, IAMUser> users = new HashMap<>();
			for(Map<String, Object> prop : props) {
				IAMUser user = IAMUserBeanImpl.createUserFromProps(prop, true, false, false);
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
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("account_Users.USERID = Account_ORG_Users.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
	
		return selectBuilder.get();
	}

	public List<IAMUser> getActiveOrgUsersv2(long orgId) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("INVITATION_ACCEPT_STATUS", "invitationAcceptStatus", "1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("USER_VERIFIED", "userVerified", "1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<IAMUser> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				users.add(IAMUserBeanImpl.createUserFromProps(prop, true, false, false));
			}
			return users;
		}
		return null;
	}
	
	
	@Override
	public Organization createOrgv2(Organization org) throws Exception {
		// TODO Auto-generated method stub
		Organization existingOrg = getOrgv2(org.getDomain());
		if (existingOrg != null) {
			throw new AccountException(AccountException.ErrorCode.ORG_DOMAIN_ALREADY_EXISTS, "The given domain already registered.");
		}
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getOrgModule().getTableName())
				.fields(IAMAccountConstants.getOrgFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(org);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		long orgId = (Long) props.get("id");
		org.setId(orgId);
		return org;
	}

	@Override
	public void updateLoggerLevel(int level, long orgId) throws Exception {
		 // TODO Auto-generated method stub 
        List<FacilioField> fields = IAMAccountConstants.getOrgFields(); 
 
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder() 
                .table(IAMAccountConstants.getOrgModule().getTableName()) 
                .fields(fields) 
                .andCondition(CriteriaAPI.getOrgIdCondition(orgId, IAMAccountConstants.getOrgModule())); 
         
         
         
        Map<String, Object> props = new HashMap<>(); 
        props.put("loggerLevel",level); 
         updateBuilder.update(props); 
	} 
	
	
}