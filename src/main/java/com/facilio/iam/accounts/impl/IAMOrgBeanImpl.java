package com.facilio.iam.accounts.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.iam.accounts.bean.IAMOrgBean;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
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
   		fields.add(IAMAccountConstants.getOrgIdField());
   		
   		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
   				.select(fields)
   				.table("Account_Users")
   				.innerJoin("Account_ORG_Users")
   				.on("Account_ORG_Users.USERID = Account_Users.USERID")
   				;
   		
   		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
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
		
		//initialiseOrgMfaSettings(orgId);
		return org;
	}
	
	public static void initialiseOrgMfaSettings(long orgId) throws Exception{
		if(orgId <= 0) {
			throw new IllegalArgumentException("Invalid orgId");
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getMfaSettings().getTableName())
				.fields(IAMAccountConstants.getOrgMfaFields());
		Map<String,Object> props = new HashMap<>();
		props.put("orgId", orgId);
		props.put("totpEnabled",false);
		props.put("motpEnabled",false);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
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

		if (FacilioProperties.isProduction() && !FacilioProperties.isOnpremise()) {
			org.setDbName("bms_3");
		}
		
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
	    AppDomain appDomain = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());
		IAMUtil.getUserBean().signUpSuperAdminUserv3(orgId, user, appDomain.getIdentifier());
		
		return user;
	}

	@Override
	public boolean rollbackSignUpOrg(long orgId, long superAdminUserId) throws Exception {
		// TODO Auto-generated method stub
		if(IAMUtil.getUserBean().deleteUserv2(superAdminUserId, orgId)) {
			IAMUtil.getUserBean().deleteDefaultAppDomains(orgId);
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

	@Override
	public boolean addOrUpdateAccountSSO(long orgId, AccountSSO sso) throws Exception {
		AccountSSO dbSso = getAccountSSO(orgId);
		
		if (dbSso == null) {
			sso.setOrgId(orgId);
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.fields(IAMAccountConstants.getAccountSSOFields())
					.table(IAMAccountConstants.getAccountSSOModule().getTableName());
			
			Map<String, Object> props = FieldUtil.getAsProperties(sso);
			props.put("ssoType", sso.getSsoType());
			insertBuilder.addRecord(props);
			insertBuilder.save();
			
			long ssoId = (Long) props.get("id");
			sso.setId(ssoId);
			return true;
		}
		else {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(IAMAccountConstants.getAccountSSOModule().getTableName())
					.fields(IAMAccountConstants.getAccountSSOFields());

			updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
			
			if (sso.getId() > 0) {
				updateBuilder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(sso.getId()), NumberOperators.EQUALS));
			}
			else {
				updateBuilder.andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(sso.getSsoType()), NumberOperators.EQUALS));
			}
			
			Map<String, Object> props = FieldUtil.getAsProperties(sso);
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	@Override
	public AccountSSO getAccountSSO(long orgId) throws Exception {



		List<Map<String, Object>> props = getAccountSSODetails(Collections.singletonList(orgId));
		if (props != null && !props.isEmpty()) {
			AccountSSO sso = FieldUtil.getAsBeanFromMap(props.get(0), AccountSSO.class);
			return sso;
		}
		return null;
	}

	private List<Map<String, Object>> getAccountSSODetails(List<Long> orgIds) throws Exception{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAccountSSOFields())
				.table(IAMAccountConstants.getAccountSSOModule().getTableName())
				.innerJoin("Organizations")
				.on("Account_SSO.ORGID = Organizations.ORGID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_SSO.ORGID", "orgId", StringUtils.join(orgIds, ","), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		return selectBuilder.get();
	}

	@Override
	public List<AccountSSO> getAccountSSO(List<Long> orgIds) throws Exception {
		List<Map<String, Object>> accountSSODetails = getAccountSSODetails(orgIds);
		if (CollectionUtils.isEmpty(accountSSODetails)) {
			return Collections.emptyList();
		}
		return accountSSODetails.stream().map(i -> FieldUtil.getAsBeanFromMap(i, AccountSSO.class)).collect(Collectors.toList());
	}

	@Override
	public AccountSSO getAccountSSO(String orgDomain) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAccountSSOFields())
				.table(IAMAccountConstants.getAccountSSOModule().getTableName())
				.innerJoin("Organizations")
   				.on("Account_SSO.ORGID = Organizations.ORGID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.FACILIODOMAINNAME", "domain", orgDomain, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			AccountSSO sso = FieldUtil.getAsBeanFromMap(props.get(0), AccountSSO.class);
			return sso;
		}
		return null;
	}

	@Override
	public boolean deleteAccountSSO(long orgId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getAccountSSOModule().getTableName());
		
		builder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		
		builder.delete();
		return true;
	}

	@Override
	public Map<String, Boolean> getMfaSettings(long orgId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgMfaFields())
				.table(IAMAccountConstants.getMfaSettings().getTableName())
		        .andCondition(CriteriaAPI.getCondition("OrgMFASettings.ORGID", "orgId",orgId + "", NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			
			Map<String,Object> x = props.get(0);
			Map<String,Boolean> result = new HashMap<>();
			result.put("totpEnabled",(Boolean) x.get("totpEnabled"));
			result.put("motpEnabled",(Boolean) x.get("motpEnabled"));
			return result;
			
		}
		return null;
	}
    private boolean changeMfaSettings(long orgId,boolean value,String type) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getMfaSettings().getTableName())
				.fields(IAMAccountConstants.getOrgMfaFields())
				.andCondition(CriteriaAPI.getCondition("OrgMFASettings.ORGID", "orgId",orgId + "", NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>(); 
        props.put(type,value); 
        updateBuilder.update(props); 
		
		return true;
	}
    public void enableTotp(long orgId) throws Exception {
    	
    	changeMfaSettings(orgId,true,"totpEnabled");
   
    }
    public void disableTotp(long orgId) throws Exception{
    	
    	changeMfaSettings(orgId,false,"totpEnabled");
    }
}