package com.facilio.iam.accounts.impl;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.util.EncryptionUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
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
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;

;

public class IAMUserBeanImpl implements IAMUserBean {

	private static final String USER_TOKEN_REGEX = "#";
	private static Logger log = LogManager.getLogger(IAMUserBeanImpl.class.getName());
	public static final String JWT_DELIMITER = "#";


	
	private long addUserEntryv2(IAMUser user, boolean emailVerificationRequired) throws Exception {

		if (StringUtils.isEmpty(user.getDomainName())) {
			user.setDomainName("app");
		}
		
		IAMUser existingUser = getFacilioUser(user.getEmail(), user.getDomainName());
		if(existingUser == null) {
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
		return existingUser.getUid();
	}

	
	public boolean updateUserv2(IAMUser user) throws Exception {
		return updateUserEntryv2(user);
	}

	private boolean updateUserEntryv2(IAMUser user) throws Exception {
		List<FacilioField> fields = IAMAccountConstants.getAccountsUserFields();
		fields.add(IAMAccountConstants.getUserPasswordField());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountsUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
		
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		int updatedRows = updateBuilder.update(props);
		
		return (updatedRows > 0);
	}
	

	private long addUserv2(long orgId, IAMUser user, boolean emailRegRequired) throws Exception {

		IAMUser orgUser = getFacilioUser(user.getEmail(), orgId, user.getDomainName());
		if (orgUser != null) {
			throw new AccountException(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS, "This user already exists in your organization.");
		}
		else {
			long uid = addUserEntryv2(user, emailRegRequired);
			user.setUid(uid);
		}
		IAMUser userExistsForAnyOrg = getFacilioUserFromUserId(user.getUid(), null);
		if(userExistsForAnyOrg != null) {
			user.setDefaultOrg(false);
		}
		else {
			user.setDefaultOrg(true);
		}
		user.setUserStatus(true);
		user.setOrgId(orgId);
		long ouid = addToORGUsersv2(user);
		return ouid;
	}

	@Override
	public long signUpSuperAdminUserv2(long orgId, IAMUser user) throws Exception {
		return  addUserv2(orgId, user, true);
	}

	@Override
	public long addUserv2(long orgId, IAMUser user) throws Exception {
		return  addUserv2(orgId, user, false);
	}

	public IAMUser getUserFromToken(String userToken){
		String[] tokenPortal = userToken.split("&");
		String token = EncryptionUtil.decode(tokenPortal[0]);
		String[] userObj = token.split(USER_TOKEN_REGEX);
		IAMUser user = null;
		if(userObj.length == 4) {
			user = new IAMUser();
			user.setOrgId(Long.parseLong(userObj[0]));
//			user.setOuid(Long.parseLong(userObj[1]));
			user.setUid(Long.parseLong(userObj[1]));
			user.setEmail(userObj[2]);
//			user.setInvitedTime(Long.parseLong(userObj[4]));
//			if(tokenPortal.length > 1) {
//				String[] portalIdString = tokenPortal[1].split("=");
//				if(portalIdString.length > 1){
//					int portalId = Integer.parseInt(portalIdString[1].trim());
//					user.setPortalId(portalId);
//				}
//			}
		}
		return user;
	}

	public static void main(String []args)
	{
		IAMUserBeanImpl us = new IAMUserBeanImpl();
		IAMUser s = us.getUserFromToken("xSb_ezHQ_udcFU8l5P67wq_z809tlkMMIZxMbHAV0hbs9TKfyRniDoVCfmvVGF3wl4nuHLJ53Ho=");
		System.out.println(s.getEmail());
		System.out.println(s.getUid());
//		System.out.println(s.getOuid());
//		System.out.println(s.getPortalId());
		System.out.println(s.getOrgId());

		
	}
	
	
	@Override
	public IAMUser verifyEmailv2(String token){
		IAMUser user = getUserFromToken(token);

		if(user != null) {
//			if((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
				try {
					user.setUserVerified(true);
					updateUserv2(user);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				return user;
//			}
		}
		return null;
	}

	@Override
	public IAMUser resetPasswordv2(String token, String password){
		IAMUser user = getUserFromToken(token);

		if(user != null) {
			long orgId=user.getOrgId();
//			if ((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
				try {
					user.setPassword(password);
					user.setUserVerified(true);
					IAMUtil.getTransactionalUserBean(orgId).updateUserv2(user);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				return user;
//			}
		}
		return null;
	}

	@Override
	public IAMUser validateUserInvitev2(String token){
		IAMUser user = getUserFromToken(token);
		return user;
	}

	@Override
	public IAMUser acceptInvitev2(String token, String password) throws Exception {
		IAMUser user = getUserFromToken(token);
		user.setPassword(password);
		long orgId=(user.getOrgId());
		if(IAMUtil.getTransactionalUserBean(orgId).acceptUserv2(user)) {
			return user;
		}
		return null;
	}

	public boolean acceptUserv2(IAMUser user) throws Exception {
		if(user != null) {
			FacilioField isDefaultOrg = new FacilioField();
			isDefaultOrg.setName("isDefaultOrg");
			isDefaultOrg.setDataType(FieldType.BOOLEAN);
			isDefaultOrg.setColumnName("ISDEFAULT");
			isDefaultOrg.setModule(IAMAccountConstants.getAccountsOrgUserModule());

			FacilioField userStatus = new FacilioField();
			userStatus.setName("userStatus");
			userStatus.setDataType(FieldType.BOOLEAN);
			userStatus.setColumnName("USER_STATUS");
			userStatus.setModule(IAMAccountConstants.getAccountsOrgUserModule());

			List<FacilioField> fields = new ArrayList<>();
			fields.add(userStatus);
			fields.add(isDefaultOrg);

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(IAMAccountConstants.getAccountsOrgUserModule().getTableName())
					.fields(fields);
			updateBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(user.getOrgId()), NumberOperators.EQUALS));
			
			Map<String, Object> props = new HashMap<>();
			//props.put("isDefaultOrg", true);
			props.put("userStatus", true);

			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				String password = user.getPassword();
				if(user != null) {
					user.setUserVerified(true);
					user.setPassword(password);
					updateUserv2(user);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean deleteUserv2(IAMUser user) throws Exception {
		
		IAMUser userData = getFacilioUser(user.getOrgId(), user.getUid());
		if(userData != null) {
			FacilioField deletedTime = new FacilioField();
			deletedTime.setName("deletedTime");
			deletedTime.setDataType(FieldType.NUMBER);
			deletedTime.setColumnName("DELETED_TIME");
			deletedTime.setModule(IAMAccountConstants.getAccountsOrgUserModule());
			
			if(user.isDefaultOrg()) {
				updateDefaultOrgForUser(user.getUid(), user.getOrgId());
			}
			
			List<FacilioField> fields = new ArrayList<>();
			fields.add(deletedTime);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(IAMAccountConstants.getAccountsOrgUserModule().getTableName())
					.fields(fields);
			
			updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(userData.getUid()), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(userData.getOrgId()), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
			
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

	private void updateDefaultOrgForUser(long uId, long currentOrg) throws Exception {
		List<IAMUser> orgUsers = getFacilioOrgUserList(uId);
		if(CollectionUtils.isNotEmpty(orgUsers)) {
			for(IAMUser u : orgUsers) {
				if(u.getOrgId() != currentOrg) {
					setDefaultOrgv2(orgUsers.get(0).getUid(), u.getOrgId());
					break;
				}
			}
		}
		
	}
	@Override
	public boolean disableUserv2(long orgId, long uId) throws Exception {
		
		IAMUser user = getFacilioUser(orgId, uId);
		if(user == null) {
			return false;
		}
		if(user.isDefaultOrg()) {
			updateDefaultOrgForUser(user.getUid(), user.getOrgId());
		}
		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(IAMAccountConstants.getAccountsOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uId), NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>();
		props.put("userStatus", false);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean enableUserv2(long orgId, long uid) throws Exception {
		
		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(IAMAccountConstants.getAccountsOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields);
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
	
		
		Map<String, Object> props = new HashMap<>();
		props.put("userStatus", true);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setDefaultOrgv2(long uid, long orgId) throws Exception {
		
		FacilioField isDefaultOrg = new FacilioField();
		isDefaultOrg.setName("isDefaultOrg");
		isDefaultOrg.setDataType(FieldType.BOOLEAN);
		isDefaultOrg.setColumnName("ISDEFAULT");
		isDefaultOrg.setModule(IAMAccountConstants.getAccountsOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(isDefaultOrg);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>();
		props.put("isDefaultOrg", false);
		
		updateBuilder.update(props);
		
		GenericUpdateRecordBuilder updateBuilder1 = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields)
				;
		
		updateBuilder1.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder1.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder1.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
	
		Map<String, Object> props1 = new HashMap<>();
		props1.put("isDefaultOrg", true);
		
		updateBuilder1.update(props1);
		return true;
	}

	@Override
	public IAMUser getFacilioUser(long orgId, long userId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(null,false);
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedtime", String.valueOf(-1), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0), true, true);
			return user;
		}
		return null;
	}
	
	@Override
	public IAMUser getFacilioUser(long orgId, String email) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(null,false);
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("EMAIL", "email", email, StringOperators.IS));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0), true, true);
			return user;
		}
		return null;
	}
	
	@Override
	public List<Organization> getOrgsv2(long uid) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("Account_ORG_Users")
				.on("Organizations.ORGID = Account_ORG_Users.ORGID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		
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
	public Organization getDefaultOrgv2(long uid) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("Account_ORG_Users")
				.on("Organizations.ORGID = Account_ORG_Users.ORGID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return IAMOrgUtil.createOrgFromProps(props.get(0));
		}
		return null;
	}
	
	@Override
	public boolean updateUserPhoto(long uid, long fileId) throws Exception {
		
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
			return true;
		}
		return false;
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
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		
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
	public IAMAccount verifyUserSessionv2(String uId, String token, String orgDomain) throws Exception {		
		List<Map<String, Object>> sessions = (List<Map<String, Object>>) LRUCache.getUserSessionCache().get(uId);
		if (sessions == null) {
			sessions = new ArrayList<>();
		}
		for (Map<String, Object> session : sessions) {
			String sessionToken = (String) session.get("token");
			if (Objects.equals(sessionToken, token)) {
				return getAccount((long) session.get("uid"), orgDomain);
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
			return getAccount((long) props.get("uid"), orgDomain);
		}
		return null;
		
	}
	
	@Override
	public IAMAccount getAccount(long userId, String orgDomain) throws Exception {
		IAMUser user = getFacilioUserFromUserId(userId, orgDomain);
		if (user == null) {
			throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "IAMUser doesn't exists in the current Org");
		}
		Organization org = IAMUtil.getOrgBean().getOrgv2(user.getOrgId());
		IAMAccount account = new IAMAccount(org, user);
		return account;
	}
	
	private IAMAccount getAccount(String email, String portalDomain) throws Exception {
		IAMUser user = getFacilioUser(email, portalDomain);
		if (user == null) {
			throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "IAMUser doesn't exists in the current Org");
		}
		Organization org = IAMUtil.getOrgBean().getOrgv2(user.getOrgId());
		IAMAccount account = new IAMAccount(org, user);
		return account;
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
	
	static IAMUser createUserFromProps(Map<String, Object> prop, boolean fetchRole, boolean fetchSpace) throws Exception {
		IAMUser user = FieldUtil.getAsBeanFromMap(prop, IAMUser.class);
//		if (user.getPhotoId() > 0) {
//			FileStore fs = FileStoreFactory.getInstance().getFileStoreFromOrg(user.getOrgId(), -1);
//			user.setAvatarUrl(fs.getPrivateUrl(user.getPhotoId(), isPortalRequest));
//			user.setOriginalUrl(fs.orginalFileUrl(user.getPhotoId()));
//		}
		return user;
	}
	
	@Override
	public String generatePermalinkForURL(String url, long uid, long orgId) throws Exception {
		
		Organization org = IAMUtil.getOrgBean().getOrgv2(orgId);
		String tokenKey = orgId + "-" + uid ;
		String jwt = createJWT("id", "auth0", tokenKey, System.currentTimeMillis() + 24 * 60 * 60000);
		
		JSONObject sessionInfo = new JSONObject();
		sessionInfo.put("allowUrls", url);
		
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

	
	private long addToORGUsersv2(IAMUser user) throws Exception {
		
		if(user.getUid() > 0) {
			IAMUser userExistsInOrg = getFacilioUser(user.getOrgId(), user.getUid());
			if(userExistsInOrg != null) {
				throw new AccountException(ErrorCode.USER_ALREADY_EXISTS_IN_ORG, "The user already exist in thie organization");
			}
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(IAMAccountConstants.getAccountsOrgUserFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, props.get("id"));
		user.setIamOrgUserId((Long)props.get("id"));
//		user.setOuid((Long)props.get("id"));
		return (Long)props.get("id");
	}
	
	@Override
	public String getEncodedTokenv2(IAMUser user) throws Exception {
		IAMUser iamUser = getFacilioUser(user.getOrgId(), user.getUid());
		if(iamUser != null) {
			return EncryptionUtil.encode(iamUser.getOrgId()+ USER_TOKEN_REGEX + iamUser.getUid()+ USER_TOKEN_REGEX + iamUser.getEmail() + USER_TOKEN_REGEX + System.currentTimeMillis());
		}
		return "";
	}

	
	private GenericSelectRecordBuilder getFacilioUserBuilder(String portalDomain, boolean includePortal) {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountsOrgUserFields());
		fields.add(IAMAccountConstants.getOrgIdField(IAMAccountConstants.getOrgModule()));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
					.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.innerJoin("Organizations")
					.on("Account_ORG_Users.ORGID=Organizations.ORGID");

		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		
		if (includePortal) {
			if (StringUtils.isEmpty(portalDomain)) {
				portalDomain = "app";
			}
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.DOMAIN_NAME", "portalDomain", portalDomain, StringOperators.IS));
			
		}
		
		return selectBuilder;
	}
	
	public IAMUser getFacilioUserFromUserId(long userId, String orgDomain) throws Exception {
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(null, false);
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		selectBuilder.andCriteria(userEmailCriteria);
		
		if (!StringUtils.isEmpty(orgDomain)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.FACILIODOMAINNAME", "facilioDomainName", orgDomain, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
			
		} 
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0), true, true);
			return user;
		}
		return null;
	}
	
	
	private List<IAMUser> getFacilioOrgUserList(long userId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(null, false);
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		selectBuilder.andCriteria(userEmailCriteria);
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<IAMUser> userList = new ArrayList<IAMUser>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> user : props) {
				userList.add(createUserFromProps(user, true, true));
			}
		return userList;	
		}
		return null;
	}
	
	@Override
	public IAMUser getFacilioUser(String email, String orgDomain, String portalDomain) throws Exception {
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(portalDomain, true);
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", email, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", email, StringOperators.IS));
		selectBuilder.andCriteria(userEmailCriteria);
		
		if (!StringUtils.isEmpty(orgDomain)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.FACILIODOMAINNAME", "facilioDomainName", orgDomain, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
			
		} 
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		}
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0), true, true);
			return user;
		}
		return null;
	}

	@Override
	public IAMUser getFacilioUser(String email, long orgId, String portalDomain) throws Exception {
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(portalDomain, true);
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", email, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", email, StringOperators.IS));
		selectBuilder.andCriteria(userEmailCriteria);
		
		if (orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
			
		} 
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0), true, true);
			return user;
		}
		return null;
	}

	@Override
	public IAMUser getFacilioUser(String email) throws Exception {
		// TODO Auto-generated method stub
		return getFacilioUser(email, -1, null);
	}

	@Override
	public IAMUser getFacilioUser(String email, String portalDomain) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users");
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", email, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", email, StringOperators.IS));
			
		selectBuilder.andCriteria(userEmailCriteria);
		if (StringUtils.isEmpty(portalDomain)) {
			portalDomain = "app";
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.DOMAIN_NAME", "portalDomain", portalDomain, StringOperators.IS));
	
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0), true, true);
			return user;
		}
		return null;
	}


	@Override
	public IAMAccount getPermalinkAccount(String token, List<String> urls) throws Exception {
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
			
			IAMAccount currentAccount = new IAMAccount(IAMUtil.getOrgBean().getOrgv2(orgId), getFacilioUser(orgId, uid));
			return currentAccount;
		}
		return null;
	}


	@Override
	public IAMAccount verifyUserSessionUsingEmail(String email, String token, String portalDomain) throws Exception {
		String cacheKey = email + "###" + portalDomain;
		List<Map<String, Object>> sessions = (List<Map<String, Object>>) LRUCache.getUserSessionCache().get(cacheKey);
		if (sessions == null) {
			sessions = new ArrayList<>();
		}
		for (Map<String, Object> session : sessions) {
			String sessionToken = (String) session.get("token");
			if (Objects.equals(sessionToken, token)) {
				return getAccount(email, portalDomain);
			}
		}
		if(org.apache.commons.lang3.StringUtils.isEmpty(portalDomain)) {
			portalDomain = "app";
		}
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID = UserSessions.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", email, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.DOMAIN_NAME", "domainName", portalDomain, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", token, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.IS_ACTIVE", "isActive", "1", NumberOperators.EQUALS));
		
	
		Map<String, Object> props = selectBuilder.fetchFirst();
		if (MapUtils.isNotEmpty(props)) {
			sessions.add(props);
			LRUCache.getUserSessionCache().put(cacheKey, sessions);
			return getAccount(email, portalDomain);
		}
		return null;
	}


	@Override
	public Map<Long, Map<String, Object>> getUserData(Criteria criteria, long orgId, boolean shouldFetchDeleted) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountsOrgUserFields());
		fields.add(IAMAccountConstants.getOrgIdField(IAMAccountConstants.getOrgModule()));
		
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(null, false);
				
		if(criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		if(!shouldFetchDeleted) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedtime", String.valueOf(-1), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			Map<Long, Map<String, Object>> userMap = new HashMap<>();
			for (Map<String, Object> prop : list) {
				userMap.put((long)prop.get("iamOrgUserId"), prop);
			}
			return userMap;
		}
		return null;
	}

	@Override
	public Map<Long, Map<String, Object>> getUserDataForUids(List<Long> userIds, long orgId, boolean shouldFetchDeleted) throws Exception {
		// TODO Auto-generated method stub
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsOrgUserFields());
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("iamOrgUserId"), userIds, NumberOperators.EQUALS));
		
		return getUserData(criteria, orgId, shouldFetchDeleted);
		
	}

	@Override
	public boolean verifyPasswordv2(String emailAddress, String domain, String password) throws Exception {
		// TODO Auto-generated method stub
		boolean passwordValid = false;
		try {
			if (StringUtils.isEmpty(domain)) {
				domain = "app";
			}
			
			List<FacilioField> fields = new ArrayList<>();
			fields.addAll(IAMAccountConstants.getAccountsUserFields());
			fields.add(IAMAccountConstants.getUserPasswordField());
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table("Account_Users");
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", emailAddress, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.DOMAIN_NAME", "domainName", domain, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("USER_VERIFIED", "userVerified", "1", NumberOperators.EQUALS));
			
			log.info("Domain  " + domain);
			log.info("Email Address  " + emailAddress);
			log.info("PAssword  " + password);
			
			List<Map<String, Object>> props = selectBuilder.get();

			if (CollectionUtils.isNotEmpty(props)) {
				Map<String, Object> result = props.get(0);
				String storedPass = (String)result.get("password");
				if (storedPass.equals(password)) {
					passwordValid = true;
				}
			} else {
				log.info("No records found for  " + emailAddress);
				throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "User doesn't exists");
			}

		} catch (SQLException | RuntimeException e) {
			log.info("Exception while verifying password, "+ e.toString());
		} 
		return passwordValid;
	}


	@Override
	public String validateAndGenerateToken(String emailaddress, String password, String userAgent, String userType,
			String ipAddress, String domain, boolean startUserSession) throws Exception {
		// TODO Auto-generated method stub
		if (verifyPasswordv2(emailaddress, domain, password)) {

			IAMUser user = getFacilioUser(emailaddress, -1, domain);
			if (user != null) {
				long uid = user.getUid();
				String jwt = createJWT("id", "auth0", String.valueOf(user.getUid()),
						System.currentTimeMillis() + 24 * 60 * 60000);
				if (startUserSession) {
					startUserSessionv2(uid, emailaddress, jwt, ipAddress, userAgent, userType);
				}
				return jwt;
			}
			throw new AccountException(ErrorCode.USER_DEACTIVATED_FROM_THE_ORG, "User is deactivated, Please contact admin to activate.");

		}
		throw new AccountException(ErrorCode.ERROR_VALIDATING_CREDENTIALS, "Invalid Password");
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
	public IAMAccount verifyFacilioToken(String idToken, boolean overrideSessionCheck, String orgDomain,
			String portalDomain) throws Exception {
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
				try {
					long userId = Long.parseLong(uId);
					if(overrideSessionCheck) {
						account = IAMUtil.getUserBean().getAccount(userId, orgDomain);
					}
					else {
						account = IAMUtil.getUserBean().verifyUserSessionv2(uId, idToken, orgDomain);
					}
				}
				catch(NumberFormatException e) {
					account = IAMUtil.getUserBean().verifyUserSessionUsingEmail(uId, idToken, portalDomain);
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
	
	
}
