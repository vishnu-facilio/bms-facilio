package com.facilio.iam.accounts.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.iam.accounts.bean.IAMOrgBean;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class IAMOrgBeanImpl implements IAMOrgBean {

	@Override
	public boolean updateOrgv2(long orgId, Organization org) throws Exception {
		if (org.getLogo() != null) {
			FileStore fs;
			fs = FacilioFactory.getFileStore();
			long fileId = fs.addFile(org.getLogoFileName(), org.getLogo(), org.getLogoContentType());
			org.setLogoId(fileId);
		}
		
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
			return IAMOrgUtil.createOrgFromProps(props.get(0));
		}
		return null;
	}

	@Override
	public Organization getOrgv2(String orgDomain) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table(IAMAccountConstants.getOrgModule().getTableName());
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("FACILIODOMAINNAME", "domainName", orgDomain, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return IAMOrgUtil.createOrgFromProps(props.get(0));
		}
		return null;
	}
	
	@Override
	public List<Organization> getOrgs() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table(IAMAccountConstants.getOrgModule().getTableName());
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Organization> orgList = new ArrayList<Organization>();
			for(Map<String, Object> org : props) {
				orgList.add(IAMOrgUtil.createOrgFromProps(org));
			}
			return orgList;
		}
		return null;
	}

	
    @Override
	public List<IAMUser> getAllOrgUsersv2(long orgId) throws Exception {
		
    	List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountUserAppOrgsFields());
		fields.add(IAMAccountConstants.getOrgIdField());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_User_Apps")
				.on("Account_Users.USERID = Account_User_Apps.USERID")
				.innerJoin("Account_User_Apps_Orgs")
				.on("Account_User_Apps.ID = Account_User_Apps_Orgs.ACCOUNT_USER_APPID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<IAMUser> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				IAMUser user = IAMUtil.getUserBean().createUserFromProps(prop);
				users.add(user);
			}
			return users;
		}
		return null;
	}
	
    @Override
   	public List<IAMUser> getAllOrgUsersv2(long orgId, String appDomain) throws Exception {
   		
       	List<FacilioField> fields = new ArrayList<>();
   		fields.addAll(IAMAccountConstants.getAccountsUserFields());
   		fields.addAll(IAMAccountConstants.getAccountUserAppOrgsFields());
   		fields.add(IAMAccountConstants.getOrgIdField());
   		fields.add(IAMAccountConstants.getAppDomainField());
   		
   		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
   				.select(fields)
   				.table("Account_Users")
   				.innerJoin("Account_User_Apps")
   				.on("Account_Users.USERID = Account_User_Apps.USERID")
   				.innerJoin("App_Domain")
   				.on("App_Domain.ID = Account_User_Apps.APP_DOMAIN_ID")
   				.innerJoin("Account_User_Apps_Orgs")
   				.on("Account_User_Apps.ID = Account_User_Apps_Orgs.ACCOUNT_USER_APPID");
   		
   		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
   		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
   	
   	
   		if(StringUtils.isNotEmpty(appDomain)) {
   			selectBuilder.andCondition(CriteriaAPI.getCondition("DOMAIN", "domain", appDomain, StringOperators.IS));
   		}
   		
   		List<Map<String, Object>> props = selectBuilder.get();
   		if (props != null && !props.isEmpty()) {
   			List<IAMUser> users = new ArrayList<>();
   			for(Map<String, Object> prop : props) {
   				IAMUser user = IAMUtil.getUserBean().createUserFromProps(prop);
				users.add(user);
   			}
   			return users;
   		}
   		return null;
   	}
   	
	@Override
	public List<IAMUser> getOrgUsersv2(long orgId, boolean status, String appDomain) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountUserAppOrgsFields());
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("userStatus"), String.valueOf(status), NumberOperators.EQUALS));
		
		if(StringUtils.isNotEmpty(appDomain)) {
   			criteria.addAndCondition(CriteriaAPI.getCondition("DOMAIN", "domain", appDomain, StringOperators.IS));
   		}
   		
		List<Map<String, Object>> props = fetchOrgUserProps(orgId, criteria);
		if (props != null && !props.isEmpty()) {
			List<IAMUser> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				users.add(IAMUtil.getUserBean().createUserFromProps(prop));
			}
			return users;
		}
		return null;
	
	}
	
	private List<Map<String, Object>> fetchOrgUserProps (long orgId, Criteria criteria) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountUserAppOrgsFields());
		fields.add(IAMAccountConstants.getAppDomainField());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_User_Apps")
				.on("Account_Users.USERID = Account_User_Apps.USERID")
				.innerJoin("App_Domain")
				.on("App_Domain.ID = Account_User_Apps.APP_DOMAIN_ID")
				.innerJoin("Account_User_Apps_Orgs")
				.on("Account_User_Apps.ID = Account_User_Apps_Orgs.ACCOUNT_USER_APPID");
			
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
	
		return selectBuilder.get();
	}

	
	@Override
	public Organization createOrgv2(Organization org) throws Exception {
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
		List<FacilioField> fields = IAMAccountConstants.getOrgFields(); 
 
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder() 
                .table(IAMAccountConstants.getOrgModule().getTableName()) 
                .fields(fields) 
                .andCondition(CriteriaAPI.getOrgIdCondition(orgId, IAMAccountConstants.getOrgModule())); 
         
         
         
        Map<String, Object> props = new HashMap<>(); 
        props.put("loggerLevel",level); 
         updateBuilder.update(props); 
	}

	@Override
	public IAMAccount signUpOrg(JSONObject jObj, Locale locale) throws Exception {
		Organization org = addOrg(jObj, locale);
		if(org.getOrgId() > 0) {
    		IAMUser user = addSuperAdmin(jObj, org.getOrgId());
    		IAMAccount iamAccount = IAMUtil.getCurrentAccount(org, user);
    		return iamAccount;
		}
		return null;
	} 
	
	private Organization addOrg(org.json.simple.JSONObject signupInfo, Locale locale) throws Exception{
    	String companyName = (String) signupInfo.get("companyname");
		String orgDomain = (String) signupInfo.get("domainname");
		 
        Organization orgObj = IAMUtil.getOrgBean().getOrgv2(orgDomain);
        if(orgObj != null) {
            throw new AccountException(ErrorCode.ORG_DOMAIN_ALREADY_EXISTS, "Org Domain Name already exists");
        }

		String timezone = (String) signupInfo.get("timezone");
		if (locale == null) {
			locale = Locale.US;
		}
		if (timezone == null) {
			Calendar calendar = Calendar.getInstance(locale);
			TimeZone timezoneObj = calendar.getTimeZone();
			if (timezoneObj == null) {
				timezone = "America/Los_Angeles";
			}
			else {
				timezone = timezoneObj.getID();
			}
		}
		signupInfo.put("locale", locale);
		
		Organization org = new Organization();
		org.setName(companyName);
		org.setDomain(orgDomain);
		org.setCountry(locale.getCountry());
		org.setTimezone(timezone);
		org.setCreatedTime(System.currentTimeMillis());
		
		return IAMUtil.getOrgBean().createOrgv2(org);
    }

	private static IAMUser addSuperAdmin(org.json.simple.JSONObject signupInfo, Long orgId) throws Exception {

		String name = (String) signupInfo.get("name");
		String email = (String) signupInfo.get("email");
		String phone = (String) signupInfo.get("phone");
		String password = (String) signupInfo.get("password");
		String timezone = (String) signupInfo.get("timezone");
		Locale locale = (Locale) signupInfo.get("locale");

		IAMUser user = new IAMUser();
		user.setName(name);
		user.setEmail(email);
		user.setUserVerified(false);
		user.setTimezone(timezone);
		user.setLanguage(locale.getLanguage());
		user.setCountry(locale.getCountry());
		if (phone != null) {
			user.setPhone(phone);
		}
		user.setDefaultOrg(true);
		user.setUserStatus(true);
		user.setPassword(password);
		if (FacilioProperties.isDevelopment()) {
			user.setUserVerified(true);
		}
	//	IAMUtil.getUserBean().signUpSuperAdminUserv2(orgId, user);
		IAMUtil.getUserBean().signUpSuperAdminUserv3(orgId, user, 1, AccountUtil.getDefaultAppDomain() );
		
		return user;
	}

	@Override
	public boolean rollbackSignUpOrg(long orgId, long superAdminUserId) throws Exception {
		// TODO Auto-generated method stub
		if(IAMUtil.getUserBean().deleteUserv2(superAdminUserId, orgId, AccountUtil.getDefaultAppDomain())) {
			return deleteSignedUpOrgv2(orgId);
		}
		return false;
	}
	
	private boolean deleteSignedUpOrgv2(long orgId) throws Exception {
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.add(IAMAccountConstants.getOrgDeletedTimeField());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getOrgModule().getTableName())
				.fields(fields);

		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("deletedTime", System.currentTimeMillis());
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

}