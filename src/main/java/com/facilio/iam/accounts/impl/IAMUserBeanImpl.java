package com.facilio.iam.accounts.impl;

import java.io.UnsupportedEncodingException;
import java.security.interfaces.RSAKey;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.accounts.dto.AccountUserApp;
import com.facilio.accounts.dto.AccountUserAppOrg;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.EncryptionUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fw.LRUCache;
import com.facilio.iam.accounts.bean.IAMUserBean;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.nimbusds.jose.JOSEException;

;

public class IAMUserBeanImpl implements IAMUserBean {

	private static final String USER_TOKEN_REGEX = "#";
	private static Logger log = LogManager.getLogger(IAMUserBeanImpl.class.getName());
	public static final String JWT_DELIMITER = "#";

	public boolean updateUserv2(IAMUser user, List<FacilioField> fields) throws Exception {
		return updateUserEntryv2(user, fields);
	}

	private boolean updateUserEntryv2(IAMUser user, List<FacilioField> fields) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountsUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
		
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		int updatedRows = updateBuilder.update(props);
		
		return (updatedRows > 0);
	}
	

	@Override
	public long signUpSuperAdminUserv3(long orgId, IAMUser user, int identifier, String appDomain) throws Exception {
		return  addUserv3(orgId, user, identifier, appDomain);
	}

	@Override
	public long addUserv3(long orgId, IAMUser user, int identifier, String appDomain) throws Exception {
		return  addUserV3(orgId, user, false, identifier, appDomain);
	}

	public IAMUser getUserFromToken(String userToken, String appDomain) throws Exception{
		String[] tokenPortal = userToken.split("&");
		String token = EncryptionUtil.decode(tokenPortal[0]);
		String[] userObj = token.split(USER_TOKEN_REGEX);
		IAMUser user = null;
		if(userObj.length == 4) {
			user = getFacilioUser(Long.parseLong(userObj[0]), Long.parseLong(userObj[1]), appDomain, true);
		}
		return user;
	}

	public static void main(String []args)
	{
		IAMUserBeanImpl us = new IAMUserBeanImpl();
		IAMUser s;
		try {
			s = us.getUserFromToken("xSb_ezHQ_udcFU8l5P67wq_z809tlkMMIZxMbHAV0hbs9TKfyRniDoVCfmvVGF3wl4nuHLJ53Ho=", AccountUtil.getDefaultAppDomain());System.out.println(s.getEmail());
			System.out.println(s.getUid());
//			System.out.println(s.getOuid());
//			System.out.println(s.getPortalId());
			System.out.println(s.getOrgId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	
	
	@Override
	public IAMUser verifyEmailv2(String token, String appDomain) throws Exception{
		IAMUser user = getUserFromToken(token, appDomain);

		if(user != null) {
//			if((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
				try {
					user.setUserVerified(true);
					Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
					List<FacilioField> fields = new ArrayList<FacilioField>();
					fields.add(fieldMap.get("userVerified"));
					
					updateUserv2(user, fields);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				return user;
//			}
		}
		return null;
	}

	@Override
	public IAMUser resetPasswordv2(String token, String password, String appDomain) throws Exception{
		IAMUser user = getUserFromToken(token, appDomain);

		if(user != null) {
			long orgId=user.getOrgId();
//			if ((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
				try {
					user.setPassword(password);
					user.setUserVerified(true);
					Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
					List<FacilioField> fields = new ArrayList<FacilioField>();
					fields.add(fieldMap.get("userVerified"));
					fields.add(IAMAccountConstants.getUserPasswordField());
					
					IAMUtil.getTransactionalUserBean().updateUserv2(user, fields);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				return user;
//			}
		}
		return null;
	}

	@Override
	public IAMUser validateUserInvitev2(String token, String appDomain) throws  Exception{
		IAMUser user = getUserFromToken(token, appDomain);
		return user;
	}

	@Override
	public IAMUser acceptInvitev2(String token, String password, String appDomain) throws Exception {
		IAMUser user = getUserFromToken(token, appDomain);
		user.setPassword(password);
		long orgId=(user.getOrgId());
		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
		if(IAMUtil.getTransactionalUserBean().acceptUserv2(user, appDomain)) {
			return user;
		}
		return null;
	}

	public boolean acceptUserv2(IAMUser user, String appDomain) throws Exception {
		if(user != null && user.isActive()) {
			FacilioField isDefaultOrg = new FacilioField();
			isDefaultOrg.setName("isDefaultOrg");
			isDefaultOrg.setDataType(FieldType.BOOLEAN);
			isDefaultOrg.setColumnName("ISDEFAULT");
			isDefaultOrg.setModule(IAMAccountConstants.getAccountUserAppOrgsModule());

			FacilioField userStatus = new FacilioField();
			userStatus.setName("userStatus");
			userStatus.setDataType(FieldType.BOOLEAN);
			userStatus.setColumnName("USER_STATUS");
			userStatus.setModule(IAMAccountConstants.getAccountUserAppOrgsModule());

			List<FacilioField> fields = new ArrayList<>();
			fields.add(userStatus);
			fields.add(isDefaultOrg);

			GenericUpdateRecordBuilder updateBuilder = getUpdateBuilder(fields);
			
			updateBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ORGID", "orgId", String.valueOf(user.getOrgId()), NumberOperators.EQUALS));
			if(StringUtils.isEmpty(appDomain)) {
				appDomain = AccountUtil.getDefaultAppDomain();
			}
			AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
			updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
				
			Map<String, Object> props = new HashMap<>();
			//props.put("isDefaultOrg", true);
			props.put("userStatus", true);

			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				String password = user.getPassword();
				if(user != null) {
					user.setUserVerified(true);
					user.setPassword(password);
					Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
					List<FacilioField> fieldsToBeUpdated = new ArrayList<FacilioField>();
					fieldsToBeUpdated.add(fieldMap.get("userVerified"));
					fieldsToBeUpdated.add(IAMAccountConstants.getUserPasswordField());
					updateUserv2(user, fieldsToBeUpdated);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean deleteUserv2(long userId, long orgId, String appDomain) throws Exception {
		IAMUser user = getFacilioUser(orgId, userId, appDomain, false);
		if(user != null) {
			FacilioField deletedTime = new FacilioField();
			deletedTime.setName("deletedTime");
			deletedTime.setDataType(FieldType.NUMBER);
			deletedTime.setColumnName("DELETED_TIME");
			deletedTime.setModule(IAMAccountConstants.getAccountUserAppOrgsModule());
			
			if(user.isDefaultOrg()) {
				updateDefaultOrgForUser(user.getUid(), user.getOrgId(), appDomain);
			}
			
			List<FacilioField> fields = new ArrayList<>();
			fields.add(deletedTime);
			
			GenericUpdateRecordBuilder updateBuilder = getUpdateBuilder(fields);
			
			updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(user.getOrgId()), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
			
			if(StringUtils.isNotEmpty(appDomain)) {
				AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
				updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
			}	
			
			Map<String, Object> props = new HashMap<>();
			props.put("deletedTime", System.currentTimeMillis());
			
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
			return false;
		}
		else {
			throw new AccountException(ErrorCode.USER_ALREADY_DELETED, "IAMUser is already deleted");
		}
	}

	private void updateDefaultOrgForUser(long uId, long currentOrg, String appDomain) throws Exception {
		List<Organization> orgs = getOrgsv2(uId, appDomain);
		if(CollectionUtils.isNotEmpty(orgs)) {
			for(Organization org: orgs) {
				if(org.getOrgId() != currentOrg) {
					setDefaultOrgv2(uId, org.getOrgId(), appDomain);
					break;
				}
			}
		}
	}
	
	@Override
	public boolean disableUserv2(long orgId, long uId, String appDomain) throws Exception {
		
		IAMUser user = getFacilioUser(orgId, uId, appDomain, true);
		if(user == null) {
			return false;
		}
		if(user.isDefaultOrg()) {
			updateDefaultOrgForUser(user.getUid(), user.getOrgId(), appDomain);
		}
		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(IAMAccountConstants.getAccountUserAppOrgsModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);
		
		GenericUpdateRecordBuilder updateBuilder = getUpdateBuilder(fields);
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.USERID", "userId", String.valueOf(uId), NumberOperators.EQUALS));
		
		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
		AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
		updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
		
		
		Map<String, Object> props = new HashMap<>();
		props.put("userStatus", false);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	private GenericUpdateRecordBuilder getUpdateBuilder(List<FacilioField> fields) {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountUserAppOrgsModule().getTableName())
				.innerJoin("Account_User_Apps")
				.on("Account_User_Apps.ID = Account_User_Apps_Orgs.ACCOUNT_USER_APPID")
				.innerJoin("Account_Users")
				.on("Account_Users.USERID = Account_User_Apps.USERID")
				.fields(fields);
		
		return updateBuilder;
	}

	@Override
	public boolean enableUserv2(long orgId, long uid, String appDomain) throws Exception {
		
		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(IAMAccountConstants.getAccountUserAppOrgsModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);
		
		GenericUpdateRecordBuilder updateBuilder = getUpdateBuilder(fields);
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		
		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
		AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
		updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
		
		
		Map<String, Object> props = new HashMap<>();
		props.put("userStatus", true);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setDefaultOrgv2(long uid, long orgId, String appDomain) throws Exception {
		
		FacilioField isDefaultOrg = new FacilioField();
		isDefaultOrg.setName("isDefaultOrg");
		isDefaultOrg.setDataType(FieldType.BOOLEAN);
		isDefaultOrg.setColumnName("ISDEFAULT");
		isDefaultOrg.setModule(IAMAccountConstants.getAccountUserAppOrgsModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(isDefaultOrg);
		
		GenericUpdateRecordBuilder updateBuilder = getUpdateBuilder(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		
		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
		AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
		updateBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>();
		props.put("isDefaultOrg", false);
		
		updateBuilder.update(props);
		
		GenericUpdateRecordBuilder updateBuilder1 = getUpdateBuilder(fields);
		updateBuilder1.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder1.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder1.andCondition(CriteriaAPI.getCondition("Account_User_Apps.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		updateBuilder1.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
		
		Map<String, Object> props1 = new HashMap<>();
		props1.put("isDefaultOrg", true);
		
		updateBuilder1.update(props1);
		return true;
	}

	@Override
	public List<Organization> getOrgsv2(long uid, String appDomain) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
   				.select(IAMAccountConstants.getOrgFields())
   				.table("Organizations")
   				.innerJoin("Account_User_Apps_Orgs")
   				.on("Organizations.ORGID = Account_User_Apps_Orgs.ORGID")
   				.innerJoin("Account_User_Apps")
   				.on("Account_User_Apps.ID = Account_User_Apps_Orgs.ACCOUNT_USER_APPID");
   			
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		
		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
		AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Organization> orgs = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				orgs.add(IAMOrgUtil.createOrgFromProps(prop));
			}
			return orgs;
		}
		return null;
	}

	@Override
	public String updateUserPhoto(long uid, User user) throws Exception {
		
		FileStore fs = FacilioFactory.getFileStore();
		long fileId = fs.addFile(user.getAvatarFileName(), user.getAvatar(), user.getAvatarContentType());
		String url = fs.newPreviewFileUrl("user", fileId);
		FacilioField photoId = new FacilioField();
		photoId.setName("photoId");
		photoId.setDataType(FieldType.NUMBER);
		photoId.setColumnName("PHOTO_ID");
		photoId.setModule(IAMAccountConstants.getAccountsUserModule());
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.add(photoId);
		
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountsUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
	
		Map<String, Object> props = new HashMap<>();
		props.put("photoId", fileId);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return url;
		}
		return null;
	}

	@Override
	public long startUserSessionv2(long uid, String email, String token, String ipAddress, String userAgent, String userType) throws Exception {
		TransactionManager transactionManager = null;
		try {
			transactionManager = FacilioTransactionManager.INSTANCE.getTransactionManager();

			Transaction transaction = transactionManager.getTransaction();
			if (transaction == null) {
				transactionManager.begin();
			}
			List<FacilioField> fields = IAMAccountConstants.getUserSessionFields();

			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(IAMAccountConstants.getUserSessionModule().getTableName())
					.fields(fields);

			Map<String, Object> props = new HashMap<>();
			props.put("uid", uid);
			props.put("sessionType", IAMAccountConstants.SessionType.USER_LOGIN_SESSION.getValue());
			props.put("token", token);
			props.put("startTime", System.currentTimeMillis());
			props.put("isActive", true);
			props.put("ipAddress", ipAddress);
			props.put("userAgent", userAgent);
			props.put("userType", userType);

			insertBuilder.addRecord(props);
			insertBuilder.save();
			transactionManager.commit();
			long sessionId = (Long) props.get("id");
			return sessionId;
		} catch (Exception e) {
			if (transactionManager != null) {
				transactionManager.rollback();
			}
			log.info("exception while adding user session transaction ", e);
		}
		return -1L;
	}

	@Override
	public boolean endUserSessionv2(long uid, String email, String token) throws Exception {
		boolean status = false;
		TransactionManager transactionManager = null;
		try {
			transactionManager = FacilioTransactionManager.INSTANCE.getTransactionManager();
			Transaction transaction = transactionManager.getTransaction();
			if (transaction == null) {
				transactionManager.begin();
			}
			List<FacilioField> fields = IAMAccountConstants.getUserSessionFields();

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(IAMAccountConstants.getUserSessionModule().getTableName())
					.fields(fields);
			
			updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("TOKEN", "token", token, StringOperators.IS));
		

			Map<String, Object> props = new HashMap<>();
			props.put("endTime", System.currentTimeMillis());
			props.put("isActive", false);

			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				LRUCache.getUserSessionCache().remove(String.valueOf(uid));
				status = true;
			}
			transactionManager.commit();
		} catch (Exception e) {
			if (transactionManager != null) {
				transactionManager.rollback();
			}
			log.info("exception while adding ending user session ", e);
		}
		return status;
	}

	@Override
	public List<Map<String, Object>> getUserSessionsv2(long uid, Boolean isActive) throws Exception
	{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID =UserSessions.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		
		if (isActive != null) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.IS_ACTIVE", "isActive", isActive ? "1" : "0", NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props;
		}
		return null;
	
	}
	
	@Override
	public void clearUserSessionv2(long uid, String email, String token) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getUserSessionModule().getTableName());
		
		builder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		builder.andCondition(CriteriaAPI.getCondition("TOKEN", "token", token, StringOperators.IS));
		
		builder.delete();
		
		LRUCache.getUserSessionCache().remove(String.valueOf(uid));
	}

	@Override
	public void clearAllUserSessionsv2(long uid, String email) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getUserSessionModule().getTableName());
		builder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		builder.delete();
		
		//LRUCache.getUserSessionCache().remove(email);
		LRUCache.getUserSessionCache().remove(String.valueOf(uid));
		
	}
	
	@Override
	public IAMUser createUserFromProps(Map<String, Object> prop) throws Exception {
		IAMUser user = FieldUtil.getAsBeanFromMap(prop, IAMUser.class);
		if (user.getPhotoId() > 0) {
			FileStore fs = FacilioFactory.getFileStoreFromOrg(user.getOrgId(), -1);
			user.setAvatarUrl(fs.newPreviewFileUrl("user", user.getPhotoId()));
		}
		if(prop.get("domain") != null && StringUtils.isNotEmpty((String)prop.get("domain"))){
			AppDomain appDomainObj = getAppDomain((String)prop.get("domain"));
			user.setAppDomain(appDomainObj);
		}


		return user;
	}

	@Override
	public String generatePermalinkForURL(long uid, long orgId, JSONObject sessionInfo) throws Exception {
		Organization org = IAMUtil.getOrgBean().getOrgv2(orgId);
		String tokenKey = orgId + "-" + uid ;
		String jwt = createJWT("id", "auth0", tokenKey, System.currentTimeMillis() + 24 * 60 * 60000);

		List<FacilioField> fields = IAMAccountConstants.getUserSessionFields();

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getUserSessionModule().getTableName())
				.fields(fields);

		Map<String, Object> props = new HashMap<>();
		props.put("uid", uid);
		props.put("sessionType", IAMAccountConstants.SessionType.PERMALINK_SESSION.getValue());
		props.put("token", jwt);
		props.put("startTime", System.currentTimeMillis());
		props.put("isActive", true);
		props.put("sessionInfo", sessionInfo.toJSONString());

		insertBuilder.addRecord(props);
		insertBuilder.save();
		long sessionId = (Long) props.get("id");

		return jwt;
	}

	@Override
	public String generatePermalinkForURL(String url, long uid, long orgId) throws Exception {

		JSONObject sessionInfo = new JSONObject();
		sessionInfo.put("allowUrls", url);

		return generatePermalinkForURL(uid, orgId, sessionInfo);
	}
    
	@Override
    public boolean verifyPermalinkForURL(String token, List<String> urls) throws Exception {
    	
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("UserSessions");
    	
    	selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", token, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "email", "1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<String, Object> session = props.get(0);
			Long startTime = (Long) session.get("startTime");
			String sessionInfo = (String) session.get("sessionInfo");
			
			if ((startTime + 2678400000l) < System.currentTimeMillis()) { // one month
				// expired
				return false;
			}
			
			if (urls != null) {
				JSONParser parser = new JSONParser();
				try {
					JSONObject sessionInfoObj = (JSONObject) parser.parse(sessionInfo);
					String allowUrls = (String) sessionInfoObj.get("allowUrls");
					List<String> allowedUrlList = Arrays.asList(allowUrls.split(","));
					
					for (String url : urls) {
						if (allowedUrlList.contains(url)) {
							// url valid
							return true;
						}
					}
				} catch (ParseException e) {
					log.info("Exception occurred ", e);
				}
			}
			else {
				// token valid
				return true;
			}
		}
		return false;
    }

	
	@Override
	public String getEncodedTokenv2(IAMUser user, String appDomain) throws Exception {
		IAMUser iamUser = getFacilioUser(user.getOrgId(), user.getUid(), appDomain, true);
		if(iamUser != null) {
			return EncryptionUtil.encode(iamUser.getOrgId()+ USER_TOKEN_REGEX + iamUser.getUid()+ USER_TOKEN_REGEX + iamUser.getEmail() + USER_TOKEN_REGEX + System.currentTimeMillis());
		}
		return "";
	}

	
	@Override
	public IAMAccount getPermalinkAccount(String token, List<String> urls, String appDomain) throws Exception {
		// TODO Auto-generated method stub
		if(verifyPermalinkForURL(token, urls)) {
			DecodedJWT decodedjwt = validateJWT(token, "auth0");
	
			String[] tokens = null;
			if (decodedjwt.getSubject().contains(JWT_DELIMITER)) {
				tokens = decodedjwt.getSubject().split(JWT_DELIMITER)[0].split("-");
			}
			else {
				tokens = decodedjwt.getSubject().split("_")[0].split("-");
			}
			long orgId = Long.parseLong(tokens[0]);
			long uid = Long.parseLong(tokens[1]);
			
			IAMAccount currentAccount = new IAMAccount(IAMUtil.getOrgBean().getOrgv2(orgId), getFacilioUser(orgId, uid, appDomain, true));
			return currentAccount;
		}
		return null;
	}


	public static String createJWT(String id, String issuer, String subject, long ttlMillis) {
		 
		try {
		    Algorithm algorithm = Algorithm.HMAC256("secret");
		    
		    String key = subject + JWT_DELIMITER + System.currentTimeMillis();
		    JWTCreator.Builder builder = JWT.create().withSubject(key)
	        .withIssuer(issuer);
		    
		    return builder.sign(algorithm);
		} catch (UnsupportedEncodingException | JWTCreationException exception){
			log.info("exception occurred while creating JWT "+ exception.toString());
		    //UTF-8 encoding not supported
		}
		return null;
	}

	public static DecodedJWT validateJWT(String token, String issuer) {
		try {
			Algorithm algorithm = Algorithm.HMAC256("secret");
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build(); // Reusable verifier instance

			DecodedJWT jwt = verifier.verify(token);
			System.out.println("\ndecoded " + jwt.getSubject());
			System.out.println("\ndecoded " + jwt.getClaims());

			return jwt;
		} catch (UnsupportedEncodingException | JWTVerificationException exception) {
			log.info("exception occurred while decoding JWT "+ exception.toString());
			// UTF-8 encoding not supported
			return null;

		}
	}


	@Override
	public boolean verifyUser(long userId) throws Exception {
		// TODO Auto-generated method stub
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(com.facilio.iam.accounts.util.IAMAccountConstants.getAccountsUserModule().getTableName()).fields(com.facilio.iam.accounts.util.IAMAccountConstants.getAccountsUserFields())
				.andCustomWhere("USERID = ?", userId);
		Map<String, Object> prop = new HashMap<>();
		prop.put("userVerified", true);
		if(updateBuilder.update(prop) > 0) {
			return true;
		}
		else {
			return false;
		}
	}


	@Override
	public boolean addUserMobileSetting(UserMobileSetting userMobileSetting) throws Exception {
		// TODO Auto-generated method stub
//		if (userMobileSetting.getUserId() == -1) {
//			userMobileSetting.setUserId(getUserForEmailOrPhone(userMobileSetting.getEmail(), false,).getUid());
//		}
		if (userMobileSetting.getCreatedTime() == -1) {
			userMobileSetting.setCreatedTime(System.currentTimeMillis());
		}
		
		// Fetching and adding only if it's not present already
		FacilioModule module = IAMAccountConstants.getUserMobileSettingModule();
		List<FacilioField> fields = IAMAccountConstants.getUserMobileSettingFields();

		UserMobileSetting currentSetting = getUserMobileSetting(userMobileSetting.getUserId(),
				userMobileSetting.getMobileInstanceId(), module, fields, userMobileSetting.getFromPortal());
		if (currentSetting == null) {
			long id = addUserMobileSetting(userMobileSetting, module, fields);
			if(id > 0) {
				return true;
			}
		} else {
			userMobileSetting.setUserMobileSettingId(currentSetting.getUserMobileSettingId());
			userMobileSetting.setUserId(-1);
			userMobileSetting.setMobileInstanceId(null);
			if(updateUserMobileSetting(userMobileSetting, module, fields) > 0) {
				return true;
			}
		}
		return false;
	}
	
	private long addUserMobileSetting(UserMobileSetting userMobileSetting, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
				.fields(fields);

		return insertBuilder.insert(FieldUtil.getAsProperties(userMobileSetting));
	}

	private int updateUserMobileSetting(UserMobileSetting userMobileSetting, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		FacilioField idField = FieldFactory.getAsMap(fields).get("userMobileSettingId");
		long id = userMobileSetting.getUserMobileSettingId();
		userMobileSetting.setUserMobileSettingId(-1);

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getCondition(idField, String.valueOf(id), PickListOperators.IS));
		return updateBuilder.update(FieldUtil.getAsProperties(userMobileSetting));
	}
	
	private UserMobileSetting getUserMobileSetting(long userId, String instance, FacilioModule module,
			List<FacilioField> fields, boolean isPortal) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField userIdField = fieldMap.get("userId");
		FacilioField instanceField = fieldMap.get("mobileInstanceId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(userIdField, String.valueOf(userId), PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(instanceField, instance, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fromPortal"), String.valueOf(isPortal),
						BooleanOperators.IS));
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), UserMobileSetting.class);
		}
		return null;
	}


	@Override
	public boolean removeUserMobileSetting(String mobileInstanceId, boolean isPortal) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = IAMAccountConstants.getUserMobileSettingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getUserMobileSettingModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("mobileInstanceId"), mobileInstanceId,
						StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fromPortal"), String.valueOf(isPortal),
						BooleanOperators.IS));

		if(builder.delete() > 0) {
			return true;
		}
		return false;
	}


	@Override
	public List<Map<String, Object>> getMobileInstanceIds(List<Long> uIds) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = new ArrayList<>();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getUserMobileSettingFields());
		
		fields.add(fieldMap.get("mobileInstanceId"));
		fields.add(fieldMap.get("fromPortal"));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("User_Mobile_Setting")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userId"), uIds, NumberOperators.EQUALS))
				.orderBy("USER_MOBILE_SETTING_ID");

		List<Map<String, Object>> props = selectBuilder.get();
		return props;
	}


	@Override
	public Organization getOrgv2(String currentOrgDomain, long uid) throws Exception {
		// TODO Auto-generated method stub
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("Account_User_Apps_Orgs")
				.on("Organizations.ORGID = Account_User_Apps_Orgs.ORGID")
				.innerJoin("Account_User_Apps")
				.on("Account_User_Apps.ID = Account_User_Apps_Orgs.ACCOUNT_USER_APPID")
				;
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("FACILIODOMAINNAME", "domainName", currentOrgDomain, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return IAMOrgUtil.createOrgFromProps(props.get(0));
		}
		return null;
	}


	@Override
	public Object getPermalinkDetails(String token) throws Exception {
		// TODO Auto-generated method stub
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(IAMAccountConstants.getUserSessionFields())
                .table("UserSessions");

        selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", token, StringOperators.IS));
        selectBuilder.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "email", "1", NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            Map<String, Object> session = props.get(0);
            Object sessionInfo = session.get("sessionInfo");
            return sessionInfo;
        }
        return null;


	}
	
	public IAMUser getFacilioUserV3(String username, int identifier) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.andCondition(CriteriaAPI.getCondition("Account_Users.EMAIL","email" , username, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("Account_Users.IDENTIFIER","identifier" , String.valueOf(identifier), NumberOperators.EQUALS));
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0));
			return user;
		}
		return null;
	}
	
	private long addUserV3(long orgId, IAMUser user, boolean emailRegRequired, int identifier, String appDomain) throws Exception {

		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
		AppDomain appDomainObj = getAppDomain(appDomain);
		IAMUser appUser = getFacilioUserV3(user.getEmail(), identifier);
		if (appUser != null) {
			if(!StringUtils.equals(appDomain, AccountUtil.getDefaultAppDomain())) {
				long accountUserAppId = checkIfUserAlreadyPresentInApp(appUser.getUid(), appDomainObj.getId(), orgId);
				if(accountUserAppId > 0) {
					throw new AccountException(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS, "This user already exists in your organization.");
				}
			}
			else {
				user.setUid(appUser.getUid());
			}
		}
		else {
			addUserEntryV3(user, orgId);
		}
		IAMUser userExistsForAnyOrg = getFacilioUserFromUserIdv3(user.getUid(), appDomainObj.getId(), null);
		if(userExistsForAnyOrg != null) {
			user.setDefaultOrg(false);
		}
		else {
			user.setDefaultOrg(true);
		}
		user.setUserStatus(true);
		user.setOrgId(orgId);
		long accoutUserAppId = addAccountUserAppEntry(appDomainObj.getId(), user.getUid());
		long ouId = addAccountUserAppOrgEntry(accoutUserAppId, orgId, user);
		user.setIamOrgUserId(ouId);
		return ouId;
	}

	private long addUserEntryV3(IAMUser user, long orgId) throws Exception {

		List<FacilioField> fields = IAMAccountConstants.getAccountsUserFields();
		fields.add(IAMAccountConstants.getUserPasswordField());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getAccountsUserModule().getTableName())
				.fields(fields);

		Map<String, Object> props = FieldUtil.getAsProperties(user);

		insertBuilder.addRecord(props);
		insertBuilder.save();
		long userId = (Long) props.get("id");
		user.setUid(userId);
		
		
		return userId;
	}
	
	
	private long checkIfUserAlreadyPresentInApp(long userId, long appDomainId, long orgId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountUserAppsFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_User_Apps")
				.innerJoin("Account_User_Apps_Orgs")
				.on("Account_User_Apps.ID = Account_User_Apps_Orgs.ACCOUNT_USER_APPID")
				.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID","appDomainId" , String.valueOf(appDomainId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ORGID","orgId" , String.valueOf(orgId), NumberOperators.EQUALS));
				
		List<Map<String, Object>> props = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			Map<String, Object> map = props.get(0);
			return (long)map.get("id");
		}
		return -1;
		
	}
	
	@Override
	public long addAppDomain(String domainName, int groupType, int appType) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAppDomainFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("App_Domain")
				.andCondition(CriteriaAPI.getCondition("App_Domain.DOMAIN","domainName" , domainName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("App_Domain.APP_DOMAIN_TYPE","appDomainType" , String.valueOf(appType), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("App_Domain.APP_GROUP_TYPE","groupType" , String.valueOf(groupType), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			Map<String, Object> map = props.get(0);
			return (long)map.get("id");
		}
		else {
			return addNewAppDomain(domainName, groupType, appType);
		}
		
	}
	
	@Override
	public int deleteAppDomain(long appDomainId) throws Exception {
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table("App_Domain")
				.andCondition(CriteriaAPI.getCondition("App_Domain.ID","id" , String.valueOf(appDomainId), NumberOperators.EQUALS));
				
		return deleteBuilder.delete();
		
	}
	
	private static long addNewAppDomain(String domain_name, int group_type, int app_type) throws Exception {

		if(StringUtils.isNotEmpty(domain_name)) {
			
			List<FacilioField> fields = IAMAccountConstants.getAppDomainFields();
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(IAMAccountConstants.getAppDomainModule().getTableName())
					.fields(fields);
	
			AppDomain appDomain = new AppDomain(domain_name, app_type, group_type);
			Map<String, Object> props = FieldUtil.getAsProperties(appDomain);
	
			insertBuilder.addRecord(props);
			insertBuilder.save();
			long appdomainId = (Long) props.get("id");
			return appdomainId;
		}
		
		return -1;
			
	}
	
	private long addAccountUserAppEntry(long appDomainId, long userId) throws Exception {

		List<FacilioField> fields = IAMAccountConstants.getAccountUserAppsFields();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getAccountUserAppsModule().getTableName())
				.fields(fields);

		AccountUserApp accUserApp = new AccountUserApp(userId, appDomainId);
		Map<String, Object> props = FieldUtil.getAsProperties(accUserApp);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		long accUserAppId = (Long) props.get("id");
		return accUserAppId;
			
	}
	
	private long addAccountUserAppOrgEntry(long accUserAppId, long orgId, IAMUser user) throws Exception {

		List<FacilioField> fields = IAMAccountConstants.getAccountUserAppOrgsFields();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getAccountUserAppOrgsModule().getTableName())
				.fields(fields);

		AccountUserAppOrg accUserAppOrg = new AccountUserAppOrg(accUserAppId, orgId, user.isDefaultOrg(), user.isActive());
		Map<String, Object> props = FieldUtil.getAsProperties(accUserAppOrg);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		long accUserAppOrgId = (Long) props.get("id");
		return accUserAppOrgId;
			
	}


	@Override
	public AppDomain getAppDomain(String domain) throws Exception {
		// TODO Auto-generated method stub
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");
				
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.DOMAIN", "domain", domain, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanFromMap(props.get(0), AppDomain.class);
		}
		return null;

	}

	public static AppDomain getAppDomain(long domainId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");
				
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ID", "id", String.valueOf(domainId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanFromMap(props.get(0), AppDomain.class);
		}
		return null;

	}


	@Override
	public String validateAndGenerateTokenV3(String emailaddress, String password, String appDomainName,
			String userAgent, String userType, String ipAddress, boolean startUserSession) throws Exception {
		long validUid = verifyPasswordv3(emailaddress, password, appDomainName, userType);
		if (validUid > 0) {
			//IAMUser user = getFacilioUser(emailaddress, -1, domain);
			String jwt = createJWT("id", "auth0", String.valueOf(validUid),
					System.currentTimeMillis() + 24 * 60 * 60000);
			if (startUserSession) {
				startUserSessionv2(validUid, emailaddress, jwt, ipAddress, userAgent, userType);
			}
			return jwt;
			//throw new AccountException(ErrorCode.USER_DEACTIVATED_FROM_THE_ORG, "User is deactivated, Please contact admin to activate.");

		}
		throw new AccountException(ErrorCode.ERROR_VALIDATING_CREDENTIALS, "Invalid Password");
	}
	
	
	@Override
	public long verifyPasswordv3(String emailAddress, String password, String appDomainName, String userType) throws Exception {
		try {
			if (StringUtils.isEmpty(appDomainName)) {
				appDomainName = AccountUtil.getDefaultAppDomain();
			}
			
			List<FacilioField> fields = new ArrayList<>();
			fields.addAll(IAMAccountConstants.getAccountsUserFields());
			fields.add(IAMAccountConstants.getUserPasswordField());
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table("Account_Users")
					.innerJoin("Account_User_Apps")
					.on("Account_Users.USERID = Account_User_Apps.USERID")
					.innerJoin("App_Domain")
					.on("Account_User_Apps.APP_DOMAIN_ID = App_Domain.ID")
					.innerJoin("Account_User_Apps_Orgs")
					.on("Account_User_Apps_Orgs.ACCOUNT_USER_APPID = Account_User_Apps.ID")
				
					;
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", emailAddress, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.DOMAIN", "domain", appDomainName, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("USER_VERIFIED", "userVerified", "1", NumberOperators.EQUALS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
			
			log.info("App Domain  " + appDomainName);
			log.info("Email Address  " + emailAddress);
			log.info("PAssword  " + password);
			
			List<Map<String, Object>> props = selectBuilder.get();

			if (CollectionUtils.isNotEmpty(props)) {
				Map<String, Object> result = props.get(0);
				String storedPass = (String)result.get("password");
				if (storedPass.equals(password)) {
					return (long)result.get("uid");
				}
			} else {
				log.info("No records found for  " + emailAddress);
				throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "User doesn't exists");
			}

		} catch (SQLException | RuntimeException e) {
			log.info("Exception while verifying password, "+ e.toString());
		} 
		return -1;
	}


	@Override
	public IAMAccount verifyFacilioTokenv3(String idToken, boolean overrideSessionCheck, String appDomain,
			String userType, String orgDomain) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("verifiyFacilioToken() :idToken :"+idToken);
		try {
			DecodedJWT decodedjwt = validateJWT(idToken, "auth0");
			if(decodedjwt != null) {
				String uId = null;
				if (decodedjwt.getSubject().contains(JWT_DELIMITER)) {
					uId = decodedjwt.getSubject().split(JWT_DELIMITER)[0];
				}
				else {
					uId = decodedjwt.getSubject().split("_")[0];
				}
				IAMAccount account = null;
				long userId = Long.parseLong(uId);
				if(overrideSessionCheck) {
					account = IAMUtil.getUserBean().getAccountv3(userId, appDomain, orgDomain);
				}
				else {
					account = IAMUtil.getUserBean().verifyUserSessionv3(uId, idToken, appDomain, userType, orgDomain);
				}
			
				return account;
			}
			return null;
		}
		catch (AccountException e) {
			throw e;
		}
		catch (Exception e) {
			log.info("Exception occurred "+e.toString());
			return null;
		}

	}


	@Override
	public IAMAccount verifyUserSessionv3(String uId, String token, String appDomain, String userType, String orgDomain)
			throws Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> sessions = (List<Map<String, Object>>) LRUCache.getUserSessionCache().get(uId);
		if (sessions == null) {
			sessions = new ArrayList<>();
		}
		for (Map<String, Object> session : sessions) {
			String sessionToken = (String) session.get("token");
			if (Objects.equals(sessionToken, token)) {
				IAMAccount account = getAccountv3((long) session.get("uid"), appDomain, orgDomain);
				if(account != null) {
					account.setUserSessionId((long) session.get("id"));
				}
				return account;
			}
		}
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID = UserSessions.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", uId, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", token, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.IS_ACTIVE", "isActive", "1", NumberOperators.EQUALS));
		
		Map<String, Object> props = selectBuilder.fetchFirst();
		if (MapUtils.isNotEmpty(props)) {
			sessions.add(props);
			LRUCache.getUserSessionCache().put(uId, sessions);
			IAMAccount account = getAccountv3((long) props.get("uid"), appDomain, orgDomain);
			if(account != null && account.getUser() != null) {
				account.setUserSessionId((long)props.get("id"));
			}
			return account;
		}
		return null;
	}
	
	private GenericSelectRecordBuilder getSelectBuilder(List<FacilioField> fields) {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_User_Apps")
				.on("Account_Users.USERID = Account_User_Apps.USERID")
				.innerJoin("App_Domain")
				.on("Account_User_Apps.APP_DOMAIN_ID = App_Domain.ID")
				.innerJoin("Account_User_Apps_Orgs")
				.on("Account_User_Apps_Orgs.ACCOUNT_USER_APPID = Account_User_Apps.ID")
				.innerJoin("Organizations")
				.on("Organizations.ORGID = Account_User_Apps_Orgs.ORGID")
				;
		
		return selectBuilder;
	}

	public IAMUser getFacilioUserFromUserIdv3(long userId, long appDomainId, String orgDomain) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.add(IAMAccountConstants.getUserPasswordField());
		fields.add(IAMAccountConstants.getAppDomainField());
		
		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("USER_VERIFIED", "userVerified", "1", NumberOperators.EQUALS));
		
		if (!StringUtils.isEmpty(orgDomain)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.FACILIODOMAINNAME", "facilioDomainName", orgDomain, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		} 
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();


		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0));
			return user;
		}
		return null;
	}
	
	@Override
	public IAMAccount getAccountv3(long userId, String appDomain, String orgDomain) throws Exception {
		AppDomain appDomainObj = getAppDomain(appDomain);
		if(appDomainObj == null) {
			throw new AccountException(ErrorCode.INVALID_APP_DOMAIN, "Invalid app domain");
		}
		IAMUser user = getFacilioUserFromUserIdv3(userId, appDomainObj.getId(), orgDomain);
		if (user == null) {
			throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "IAMUser doesn't exists in the current Org");
		}
		Organization org = IAMUtil.getOrgBean().getOrgv2(user.getOrgId());
		IAMAccount account = new IAMAccount(org, user);
		return account;
	}
	
	@Override
	public Organization getDefaultOrgv3(long uid, String appDomain) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("Account_User_Apps_Orgs")
				.on("Organizations.ORGID = Account_User_Apps_Orgs.ORGID")
				.innerJoin("Account_User_Apps")
				.on("Account_User_Apps.ID = Account_User_Apps_Orgs.ACCOUNT_USER_APPID")
				;
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		
		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
		AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
	
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return IAMOrgUtil.createOrgFromProps(props.get(0));
		}
		return null;
	}
	
	@Override
	public List<IAMUser> getUserDataForUidsv3(String userIds, long orgId, boolean shouldFetchDeleted, String appDomain) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountUserAppOrgsFields());
		fields.add(IAMAccountConstants.getAppDomainField());
		
		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);

		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ID", "userId", userIds, NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		if(!shouldFetchDeleted) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		}
		
		if(StringUtils.isNotEmpty(appDomain)) {
			AppDomain appDomainObj = getAppDomain(appDomain);
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			List<IAMUser> iamUsers = new ArrayList<IAMUser>();
			for(Map<String, Object> map : props) {
				iamUsers.add(createUserFromProps(map));
			}
			return iamUsers;
		}
		return null;
		
	}

	@Override
	public boolean verifyPassword(long orgId, long userId, String oldPassword, String appDomain) throws Exception {
		// TODO Auto-generated method stub

		IAMUser iamUser = getFacilioUser(orgId, userId, appDomain, true);
		if(iamUser == null) {
			return false;
		}
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.add(IAMAccountConstants.getUserPasswordField());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				;
		selectBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			Map<String, Object> result = props.get(0);
			String storedPass = (String)result.get("password");
			if (storedPass.equals(oldPassword)) {
				return true;
			}
		} 
		return false;
	}

	@Override
	public Map<String, Object> getUserForEmailOrPhone(String emailOrPhone, String appDomain, boolean isPhone, long orgId) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountUserAppOrgsFields());
		fields.add(IAMAccountConstants.getAppDomainField());
		
		
		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);

		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.USER_STATUS", "status", "1", NumberOperators.EQUALS));
		
		if(StringUtils.isNotEmpty(appDomain)) {
			AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
		}
	
		if(isPhone) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.PHONE", "phone", emailOrPhone, StringOperators.IS));
			criteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", emailOrPhone, StringOperators.IS));
			selectBuilder.andCriteria(criteria);
		}
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", emailOrPhone, StringOperators.IS));
		}
		
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	
	}

	@Override
	public IAMUser getFacilioUser(long orgId, long userId, String appDomain, boolean checkStatus) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountUserAppOrgsFields());
		
		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);

		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		if(checkStatus) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.USER_STATUS", "status", "1", NumberOperators.EQUALS));
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		
		if(StringUtils.isNotEmpty(appDomain)) {
			AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps.APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomainObj.getId()), NumberOperators.EQUALS));
		}
	
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_User_Apps_Orgs.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> mapList = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(mapList)) {
			IAMUser iamUser = createUserFromProps(mapList.get(0));
			return iamUser;
		}
		return null;
	
	}

	@Override
	public AppDomain getAppDomain(AppDomainType type) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");
				
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_DOMAIN_TYPE", "appDomainType", String.valueOf(type), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanFromMap(props.get(0), AppDomain.class);
		}
		return null;

	}
		
}
