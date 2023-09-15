package com.facilio.iam.accounts.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.*;
import com.facilio.accounts.sso.DomainSSO;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

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
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class IAMOrgBeanImpl implements IAMOrgBean {

	private static final String DOMAIN_PATTERN = "[a-z0-9]+";
	private static final String SANDBOX_DOMAIN_PATTERN = "[a-z0-9_]+";

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
		return getOrgv2(orgDomain, Organization.OrgType.PRODUCTION);
	}

	@Override
	public Organization getOrgv2(String orgDomain, Organization.OrgType orgType) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table(IAMAccountConstants.getOrgModule().getTableName());
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("FACILIODOMAINNAME", "domainName", orgDomain, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));

		if(orgType != null && orgType != Organization.OrgType.PRODUCTION) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_TYPE", "orgType", String.valueOf(orgType.getIndex()), StringOperators.IS));
		} else {
			Criteria orgTypeCriteria = new Criteria();
			orgTypeCriteria.addAndCondition(CriteriaAPI.getCondition("ORG_TYPE", "orgType", String.valueOf(Organization.OrgType.PRODUCTION.getIndex()), StringOperators.IS));
			orgTypeCriteria.addOrCondition(CriteriaAPI.getCondition("ORG_TYPE", "orgType", null, CommonOperators.IS_EMPTY));
			selectBuilder.andCriteria(orgTypeCriteria);
		}

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
	public List<Organization> getOrgs(List<Long> orgIds) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table(IAMAccountConstants.getOrgModule().getTableName());
		selectBuilder.andCondition(CriteriaAPI.getCondition(IAMAccountConstants.getOrgIdField(), orgIds, NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Organization> orgList = new ArrayList<Organization>();
			for(Map<String, Object> org : props) {
				orgList.add(IAMOrgUtil.createOrgFromProps(org));
			}
			return orgList;
		}
		return Collections.emptyList();
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
		Organization existingOrg = getOrgv2(org.getDomain(), Organization.OrgType.valueOf(org.getOrgType()));
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
		props.put("groupType", 1);
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
		Organization.OrgType orgType = (signupInfo.get("orgType") != null) ?
				(Organization.OrgType) signupInfo.get("orgType") : Organization.OrgType.PRODUCTION;
		if((orgType == Organization.OrgType.SANDBOX && !orgDomain.matches(SANDBOX_DOMAIN_PATTERN))
		   || (orgType != Organization.OrgType.SANDBOX && !orgDomain.matches(DOMAIN_PATTERN))) {
			throw new AccountException(ErrorCode.INVALID_ORG_DOMAIN, "Org domain cannot contain capital letters,space or any special characters");
		}
		 
        Organization orgObj = IAMUtil.getOrgBean().getOrgv2(orgDomain, (Organization.OrgType) signupInfo.get("orgType"));
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
		org.setLanguage((String) signupInfo.get("language"));
		org.setOrgType((signupInfo.get("orgType") != null) ? (Organization.OrgType) signupInfo.get("orgType"):Organization.OrgType.PRODUCTION);
		if(signupInfo.containsKey("productionOrgId")) {
			org.setProductionOrgId((long) signupInfo.get("productionOrgId"));
		}

		String dataSource = (String) signupInfo.get("dataSource");
		if (StringUtils.isNotEmpty(dataSource)) {
			org.setDataSource(dataSource);
		}

		String dbName = (String) signupInfo.get("dbName");
		if (StringUtils.isNotEmpty(dbName)) {
			org.setDbName(dbName);
		}
		else if (FacilioProperties.isProduction() && !FacilioProperties.isOnpremise()) {
			String defaultDbForNewOrg = FacilioProperties.getDefaultAppDBForNewOrg();
			if (StringUtils.isNotEmpty(defaultDbForNewOrg)) {
				org.setDbName(defaultDbForNewOrg);
			}
			String defaultDataSourceForNewOrg = FacilioProperties.getDefaultDataSourceForNewOrg();
			if(StringUtils.isNotEmpty(defaultDataSourceForNewOrg)) {
				org.setDataSource(defaultDataSourceForNewOrg);
			}
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

		Boolean isSandbox = (Organization.OrgType.SANDBOX).equals((signupInfo.get("orgType") != null) ? (Organization.OrgType) signupInfo.get("orgType") : Organization.OrgType.PRODUCTION);

		if(!isSandbox) {
			user.setPassword(password);
		}
		if (FacilioProperties.isDevelopment() || isSandbox) {
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

	@Override
	public boolean rollbackSignUpOrgv2(long orgId, long superAdminUserId) throws Exception {
		// TODO Auto-generated method stub
		IAMUtil.getUserBean().deleteDefaultAppDomains(orgId);
		return true;
	}

	@Override
	public int rollbackDefaultJobs(long orgId) throws Exception{
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getJobsModule().getTableName());

		deleteBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", orgId+"", NumberOperators.EQUALS));
		return deleteBuilder.delete();
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
	public boolean addOrUpdateDomainSSO(DomainSSO domainSSO) throws Exception {
		var dbSso = getDomainSSODetailsByAppDomainId(domainSSO.getAppDomainId());
		if (dbSso == null) {
			return addDomainSSOEntry(domainSSO);
		} else {
			domainSSO.setId(dbSso.getId());
			return updateDomainSSOEntry(domainSSO);
		}
	}

	private boolean addDomainSSOEntry(DomainSSO domainSSO) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(IAMAccountConstants.getDomainSSOFields())
				.table(IAMAccountConstants.getDomainSSOModule().getTableName());

		Map<String, Object> props = FieldUtil.getAsProperties(domainSSO);
		props.put("ssoType", domainSSO.getSsoType());
		insertBuilder.addRecord(props);
		insertBuilder.save();

		long ssoId = (Long) props.get("id");
		domainSSO.setId(ssoId);
		return true;
	}

	private boolean updateDomainSSOEntry(DomainSSO domainSSO) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getDomainSSOModule().getTableName())
				.fields(IAMAccountConstants.getDomainSSOFields());

		updateBuilder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(domainSSO.getId()), NumberOperators.EQUALS));

		Map<String, Object> props = FieldUtil.getAsProperties(domainSSO);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		else {
			return false;
		}
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

	private DomainSSO getDomainSSODetailsByAppDomainId(long appDomainId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getDomainSSOFields())
				.table(IAMAccountConstants.getDomainSSOModule().getTableName())
				.innerJoin("App_Domain")
				.on("App_Domain.ID = Domain_SSO.APP_DOMAIN_ID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ID", "appDomainId", appDomainId+"", NumberOperators.EQUALS));
		var maps = selectBuilder.get();
		if (CollectionUtils.isEmpty(maps)) {
			return null;
		}

		DomainSSO sso = FieldUtil.getAsBeanFromMap(maps.get(0), DomainSSO.class);
		return sso;
	}

	@Override
	public DomainSSO getDomainSSODetails(long domainSSOId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getDomainSSOFields())
				.table(IAMAccountConstants.getDomainSSOModule().getTableName());

		selectBuilder.andCondition(CriteriaAPI.getCondition("ID", "id", domainSSOId+"", NumberOperators.EQUALS));
		var maps = selectBuilder.get();
		if (CollectionUtils.isEmpty(maps)) {
			return null;
		}

		DomainSSO sso = FieldUtil.getAsBeanFromMap(maps.get(0), DomainSSO.class);
		return sso;
	}

	@Override
	public DomainSSO getDomainSSODetails(String domain) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getDomainSSOFields())
				.table(IAMAccountConstants.getDomainSSOModule().getTableName())
				.innerJoin("App_Domain")
				.on("App_Domain.ID = Domain_SSO.APP_DOMAIN_ID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.DOMAIN", "domain", domain, StringOperators.IS));
		var maps = selectBuilder.get();
		if (CollectionUtils.isEmpty(maps)) {
			return null;
		}

		DomainSSO sso = FieldUtil.getAsBeanFromMap(maps.get(0), DomainSSO.class);
		return sso;
	}

	public DomainSSO getDomainSSODetails(long orgId, AppDomain.AppDomainType appDomainType, AppDomain.GroupType groupType, AppDomain.DomainType domainType) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getDomainSSOFields())
				.table(IAMAccountConstants.getDomainSSOModule().getTableName())
				.innerJoin("App_Domain")
				.on("App_Domain.ID = Domain_SSO.APP_DOMAIN_ID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_DOMAIN_TYPE", "appDomainType", appDomainType.getIndex()+"", StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_GROUP_TYPE", "appGroupType", groupType.getIndex()+"", StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.DOMAIN_TYPE", "appGroupType", domainType.getIndex()+"", StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ORGID", "orgId", orgId+"", NumberOperators.EQUALS));
		var maps = selectBuilder.get();
		if (CollectionUtils.isEmpty(maps)) {
			return null;
		}

		DomainSSO sso = FieldUtil.getAsBeanFromMap(maps.get(0), DomainSSO.class);
		return sso;
	}
	
	public List<Map<String, Object>> getDomainSSODetails(long orgId, AppDomain.AppDomainType appDomainType, AppDomain.GroupType groupType) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getDomainSSOFields())
				.table(IAMAccountConstants.getDomainSSOModule().getTableName())
				.innerJoin("App_Domain")
				.on("App_Domain.ID = Domain_SSO.APP_DOMAIN_ID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_DOMAIN_TYPE", "appDomainType", appDomainType.getIndex()+"", StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_GROUP_TYPE", "appGroupType", groupType.getIndex()+"", StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ORGID", "orgId", orgId+"", NumberOperators.EQUALS));
		return selectBuilder.get();
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
	public boolean updateDomainSSOStatus(String domain, boolean status) throws Exception {
		var appDomain = IAMAppUtil.getAppDomain(domain);
		var domainSSODetails = IAMOrgUtil.getDomainSSODetails(appDomain.getDomain());

		List<FacilioField> domainSSOFields = IAMAccountConstants.getDomainSSOFields();
		Map<String, FacilioField> domainSSOFieldMap = FieldFactory.getAsMap(domainSSOFields);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getDomainSSOModule().getTableName())
				.fields(Arrays.asList(domainSSOFieldMap.get("isCreateUser")));

		updateBuilder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(domainSSODetails.getId()), NumberOperators.EQUALS));

		Map<String, Object> props = new HashMap<>();
		props.put("isCreateUser", status);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean deleteDomainSSO(String domain) throws Exception {
		var domainSSODetails = IAMOrgUtil.getDomainSSODetails(domain);

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getDomainSSOModule().getTableName());

		builder.andCondition(CriteriaAPI.getIdCondition(domainSSODetails.getId(), IAMAccountConstants.getDomainSSOModule()));
		return builder.delete() > 0;
	}

	@Override
	public boolean deleteAccountSSO(long orgId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getAccountSSOModule().getTableName());
		
		builder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		
		builder.delete();
		return true;
	}

    private boolean changeMfaSettings(long orgId, boolean value, String type, AppDomain.GroupType groupType) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getMfaSettings().getTableName())
				.fields(IAMAccountConstants.getOrgMfaFields())
				.andCondition(CriteriaAPI.getCondition("OrgMFASettings.ORGID", "orgId",orgId + "", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("OrgMFASettings.APP_GROUP_TYPE", "groupType",groupType.getIndex()+"", NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>(); 
        props.put(type,value); 
        updateBuilder.update(props); 
		
		return true;
	}
    public void enableTotp(long orgId, AppDomain.GroupType groupType) throws Exception {
    	
    	changeMfaSettings(orgId,true,"totpEnabled", groupType);
   
    }
    public void disableTotp(long orgId, AppDomain.GroupType groupType) throws Exception{
    	
    	changeMfaSettings(orgId,false,"totpEnabled", groupType);
    }

	public List<AppDomain> getCustomAppDomain(AppDomain.AppDomainType type, long orgId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");

		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_DOMAIN_TYPE", "appDomainType", String.valueOf(type.getIndex()), EnumOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.DOMAIN_TYPE", "domainType", String.valueOf(AppDomain.DomainType.CUSTOM.getIndex()), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (org.apache.commons.collections.CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanListFromMapList(props, AppDomain.class);
		}
		return Collections.EMPTY_LIST;
	}

	public boolean addOrUpdateDomainLink(AppDomainLink domainLink) throws Exception {
		AppDomainLink existsDomainLink = getDomainLink(domainLink.getAppDomainId(), domainLink.getLinkTypeEnum());
		if (existsDomainLink == null) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.fields(IAMAccountConstants.getAppDomainLinkFields())
					.table(IAMAccountConstants.getAppDomainLinkModule().getTableName());

			Map<String, Object> props = FieldUtil.getAsProperties(domainLink);
			insertBuilder.addRecord(props);
			insertBuilder.save();

			long ssoId = (Long) props.get("id");
			domainLink.setId(ssoId);
			return true;
		}
		else {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.fields(IAMAccountConstants.getAppDomainLinkFields())
					.table(IAMAccountConstants.getAppDomainLinkModule().getTableName());

			updateBuilder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(existsDomainLink.getId()), NumberOperators.EQUALS));

			Map<String, Object> props = FieldUtil.getAsProperties(domainLink);
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	public AppDomainLink getDomainLink(Long appDomainId, AppDomainLink.LinkType linkType) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainLinkFields())
				.table(IAMAccountConstants.getAppDomainLinkModule().getTableName());

		selectBuilder.andCondition(CriteriaAPI.getCondition("Domain_Links.APP_DOMAIN_ID", "appDomainId", appDomainId+"", NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			AppDomainLink domainLink = FieldUtil.getAsBeanFromMap(props.get(0), AppDomainLink.class);
			return domainLink;
		}
		return null;
	}

	public AppDomainLink getDomainLink(String domain, AppDomainLink.LinkType linkType) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainLinkFields())
				.table(IAMAccountConstants.getAppDomainLinkModule().getTableName())
				.innerJoin("App_Domain")
				.on("App_Domain.ID = Domain_Links.APP_DOMAIN_ID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.DOMAIN", "domain", domain, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Domain_Links.LINK_TYPE", "linkType", linkType.getIndex()+"", NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			AppDomainLink domainLink = FieldUtil.getAsBeanFromMap(props.get(0), AppDomainLink.class);
			return domainLink;
		}
		return null;
	}

	public boolean deleteDomainLink(String domain, AppDomainLink.LinkType linkType) throws Exception {
		AppDomainLink existsDomainLink = getDomainLink(domain, linkType);
		if (existsDomainLink != null) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(IAMAccountConstants.getAppDomainLinkModule().getTableName());

			builder.andCondition(CriteriaAPI.getIdCondition(existsDomainLink.getId(), IAMAccountConstants.getAppDomainLinkModule()));
			return builder.delete() > 0;
		}
		return false;
	}
}