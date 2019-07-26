package com.iam.accounts.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.util.StringUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.SampleGenericInsertRecordBuilder;
import com.facilio.accounts.impl.SampleGenericSelectBuilder;
import com.facilio.accounts.impl.SampleGenericUpdateRecordBuilder;
import com.facilio.accounts.util.AccountUtil;
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
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.LRUCache;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.iam.accounts.bean.IAMUserBean;
import com.iam.accounts.dto.Account;
import com.iam.accounts.exceptions.AccountException;
import com.iam.accounts.exceptions.AccountException.ErrorCode;
import com.iam.accounts.util.AccountConstants;
import com.iam.accounts.util.AuthUtill;

;

public class IAMUserBeanImpl implements IAMUserBean {

	private static final long INVITE_LINK_EXPIRE_TIME = (7 * 24 * 60 * 60 * 1000L);
	private static final String USER_TOKEN_REGEX = "#";
	private static Logger log = LogManager.getLogger(IAMUserBeanImpl.class.getName());

	
	private long addUserEntryv2(User user) throws Exception {

		if (StringUtils.isNullOrEmpty(user.getCity())) {
			user.setCity("app");
		}
		
		User existingUser = getUserv2(user.getEmail(), user.getCity());
		if(existingUser == null) {
			List<FacilioField> fields = AccountConstants.getAccountsUserFields();
			fields.add(AccountConstants.getUserPasswordField());
			GenericInsertRecordBuilder insertBuilder = new SampleGenericInsertRecordBuilder()
					.table(AccountConstants.getAccountsUserModule().getTableName())
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

	
	public boolean updateUserv2(User user) throws Exception {
		return updateUserEntryv2(user);
	}

	private boolean updateUserEntryv2(User user) throws Exception {
		List<FacilioField> fields = AccountConstants.getAccountsUserFields();
		fields.add(AccountConstants.getUserPasswordField());
		GenericUpdateRecordBuilder updateBuilder = new SampleGenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
		
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		int updatedRows = updateBuilder.update(props);
		
		return (updatedRows > 0);
	}
	
//	@Override
//	public long inviteAdminConsoleUserv2(long orgId, User user) throws Exception {
//		long userId = inviteUserv2(orgId, user, false);
//		if(userId > 0) {
//			acceptUserv2(user);
//		}
//		return userId;
//	}


	private long addUserv2(long orgId, User user, boolean sendInvitation) throws Exception {

		User orgUser = getFacilioUserv3(user.getEmail(), orgId, null);
		if (orgUser != null) {
			throw new AccountException(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS, "This user already exists in your organization.");
		}
		else {
			long uid = addUserEntryv2(user);
			user.setUid(uid);
			user.setDefaultOrg(true);
		}
		user.setOrgId(orgId);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setUserStatus(true);
		long ouid = addToORGUsersv2(user);
		return ouid;
	}

	@Override
	public long addUserv2(long orgId, User user) throws Exception {
		return  addUserv2(orgId, user, true);
	}

	public User getUserFromToken(String userToken){
		String[] tokenPortal = userToken.split("&");
		String token = EncryptionUtil.decode(tokenPortal[0]);
		String[] userObj = token.split(USER_TOKEN_REGEX);
		User user = null;
		if(userObj.length == 5) {
			user = new User();
			user.setOrgId(Long.parseLong(userObj[0]));
			user.setOuid(Long.parseLong(userObj[1]));
			user.setUid(Long.parseLong(userObj[2]));
			user.setEmail(userObj[3]);
			user.setInvitedTime(Long.parseLong(userObj[4]));
			if(tokenPortal.length > 1) {
				String[] portalIdString = tokenPortal[1].split("=");
				if(portalIdString.length > 1){
					int portalId = Integer.parseInt(portalIdString[1].trim());
					user.setPortalId(portalId);
				}
			}
		}
		return user;
	}

	public static void main(String []args)
	{
		IAMUserBeanImpl us = new IAMUserBeanImpl();
		User s = us.getUserFromToken("xSb_ezHQ_udcFU8l5P67wq_z809tlkMMIZxMbHAV0hbs9TKfyRniDoVCfmvVGF3wl4nuHLJ53Ho=");
		System.out.println(s.getEmail());
		System.out.println(s.getUid());
		System.out.println(s.getOuid());
		System.out.println(s.getPortalId());
		System.out.println(s.getOrgId());

		
	}
	
	
	@Override
	public User verifyEmailv2(String token){
		User user = getUserFromToken(token);

		if(user != null) {
			if((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
				try {
					user.setUserVerified(true);
					updateUserv2(user);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				return user;
			}
		}
		return null;
	}

	@Override
	public User resetPasswordv2(String token, String password){
		User user = getUserFromToken(token);

		if(user != null) {
			long orgId=user.getOrgId();
			if ((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
				try {
					user.setPassword(password);
					user.setUserVerified(true);
					AuthUtill.getTransactionalUserBean(orgId).updateUserv2(user);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				return user;
			}
		}
		return null;
	}

	@Override
	public User validateUserInvitev2(String token){
		User user = getUserFromToken(token);
		if(user != null){
			try {
				user = getUserWithPassword(user.getOuid());
			} catch (Exception e) {
				log.info("exception validating user invite "+ user, e);
				user = null;
			}
		}
		return user;
	}

	@Override
	public boolean resendInvitev2(long orgId, long userId) throws Exception {
		
		User user = getUserv2(orgId, userId);
		if (user.getInviteAcceptStatus()) {
			// invitation already accepted
			return false;
		}
		
		FacilioField invitedTime = new FacilioField();
		invitedTime.setName("invitedTime");
		invitedTime.setDataType(FieldType.NUMBER);
		invitedTime.setColumnName("INVITEDTIME");
		invitedTime.setModule(AccountConstants.getAccountsOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(invitedTime);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>();
		props.put("invitedTime", System.currentTimeMillis());
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public User acceptInvitev2(String token, String password) throws Exception {
		User user = getUserFromToken(token);
		user.setPassword(password);
		long orgId=(user.getOrgId());
		if(AuthUtill.getTransactionalUserBean(orgId).acceptUserv2(user)) {
			return user;
		}
		return null;
	}

	public boolean acceptUserv2(User user) throws Exception {
		if(user != null) {
			FacilioField inviteAcceptStatus = new FacilioField();
			inviteAcceptStatus.setName("inviteAcceptStatus");
			inviteAcceptStatus.setDataType(FieldType.BOOLEAN);
			inviteAcceptStatus.setColumnName("INVITATION_ACCEPT_STATUS");
			inviteAcceptStatus.setModule(AccountConstants.getAccountsOrgUserModule());

			FacilioField isDefaultOrg = new FacilioField();
			isDefaultOrg.setName("isDefaultOrg");
			isDefaultOrg.setDataType(FieldType.BOOLEAN);
			isDefaultOrg.setColumnName("ISDEFAULT");
			isDefaultOrg.setModule(AccountConstants.getAccountsOrgUserModule());

			FacilioField userStatus = new FacilioField();
			userStatus.setName("userStatus");
			userStatus.setDataType(FieldType.BOOLEAN);
			userStatus.setColumnName("USER_STATUS");
			userStatus.setModule(AccountConstants.getAccountsOrgUserModule());

			List<FacilioField> fields = new ArrayList<>();
			fields.add(inviteAcceptStatus);
			fields.add(userStatus);
			fields.add(isDefaultOrg);

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAccountsOrgUserModule().getTableName())
					.fields(fields);
			
			updateBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(user.getOuid()), NumberOperators.EQUALS));
		
			Map<String, Object> props = new HashMap<>();
			props.put("inviteAcceptStatus", true);
			props.put("isDefaultOrg", true);
			props.put("userStatus", true);

			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				String password = user.getPassword();
				user = getInvitedUser(user.getOuid());
				if(user != null) {
					user.setUserVerified(true);
					user.setPassword(password);
					updateUserv2(user);
					// LicenseApi.updateUsedLicense(user.getLicenseEnum());
					return true;
				}
			}
		}
		return false;
	}

	private User getInvitedUser(long ouid) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		fields.addAll(AccountConstants.getAccountsOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());

		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false); //Giving as false because user cannot accept invite via portal APIs
			return user;
		}
		return null;
	}

	
	@Override
	public boolean deleteUserv2(long ouid) throws Exception {
		
		FacilioField deletedTime = new FacilioField();
		deletedTime.setName("deletedTime");
		deletedTime.setDataType(FieldType.NUMBER);
		deletedTime.setColumnName("DELETED_TIME");
		deletedTime.setModule(AccountConstants.getAccountsOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(deletedTime);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid), NumberOperators.EQUALS));
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
	public boolean disableUserv2(long orgId, long uId) throws Exception {
		
		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(AccountConstants.getAccountsOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);
		
		GenericUpdateRecordBuilder updateBuilder = new SampleGenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsOrgUserModule().getTableName())
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
		userStatus.setModule(AccountConstants.getAccountsOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsOrgUserModule().getTableName())
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
		isDefaultOrg.setModule(AccountConstants.getAccountsOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(isDefaultOrg);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>();
		props.put("isDefaultOrg", false);
		
		updateBuilder.update(props);
		
		GenericUpdateRecordBuilder updateBuilder1 = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields)
				;
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
	
		Map<String, Object> props1 = new HashMap<>();
		props1.put("isDefaultOrg", true);
		
		updateBuilder1.update(props1);
		return true;
	}

	@Override
	public User getUserv2(long ouid) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		fields.addAll(AccountConstants.getAccountsOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "userId", String.valueOf(ouid), NumberOperators.EQUALS));
	
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}
	
	@Override
	public User getUserv2(long orgId, long userId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		fields.addAll(AccountConstants.getAccountsOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}
	
	private User getUserWithPassword(long ouid) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		fields.addAll(AccountConstants.getAccountsOrgUserFields());
		fields.add(AccountConstants.getUserPasswordField());
		fields.add(AccountConstants.getOrgIdField());

		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgId", String.valueOf(ouid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		log.info(selectBuilder.toString() + " query returned null");
		return null;
	}

	@Override
	public User getUserv2(long orgId, String email) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		fields.addAll(AccountConstants.getAccountsOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("EMAIL", "email", email, StringOperators.IS));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}
	
	@Override
	public List<Organization> getOrgsv2(long uid) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("Account_ORG_Users")
				.on("Organizations.ORGID = Account_ORG_Users.ORGID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Organization> orgs = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				orgs.add(FieldUtil.getAsBeanFromMap(prop, Organization.class));
			}
			return orgs;
		}
		return null;
	}

	@Override
	public Organization getDefaultOrgv2(long uid) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("Account_ORG_Users")
				.on("Organizations.ORGID = Account_ORG_Users.ORGID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), Organization.class);
		}
		return null;
	}
	
	@Override
	public boolean updateUserPhoto(long uid, long fileId) throws Exception {
		
		FacilioField photoId = new FacilioField();
		photoId.setName("photoId");
		photoId.setDataType(FieldType.NUMBER);
		photoId.setColumnName("PHOTO_ID");
		photoId.setModule(AccountConstants.getAccountsUserModule());
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.add(photoId);
		
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsUserModule().getTableName())
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
			List<FacilioField> fields = AccountConstants.getUserSessionFields();

			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(AccountConstants.getUserSessionModule().getTableName())
					.fields(fields);

			Map<String, Object> props = new HashMap<>();
			props.put("uid", uid);
			props.put("sessionType", AccountConstants.SessionType.USER_LOGIN_SESSION.getValue());
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
			List<FacilioField> fields = AccountConstants.getUserSessionFields();

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getUserSessionModule().getTableName())
					.fields(fields);
			
			updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("TOKEN", "token", token, StringOperators.IS));
		

			Map<String, Object> props = new HashMap<>();
			props.put("endTime", System.currentTimeMillis());
			props.put("isActive", false);

			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				LRUCache.getUserSessionCache().remove(email);
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
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(AccountConstants.getUserSessionFields())
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
	public Account verifyUserSessionv2(String email, String token, String orgDomain) throws Exception {		
		List<Map<String, Object>> sessions = (List<Map<String, Object>>) LRUCache.getUserSessionCache().get(email);
		if (sessions == null) {
			sessions = new ArrayList<>();
		}
		for (Map<String, Object> session : sessions) {
			String sessionToken = (String) session.get("token");
			if (Objects.equals(sessionToken, token)) {
				return getAccount((long) session.get("uid"), orgDomain);
			}
		}
		
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(AccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID = UserSessions.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", email, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", token, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.IS_ACTIVE", "isActive", "1", NumberOperators.EQUALS));
		
	
		Map<String, Object> props = selectBuilder.fetchFirst();
		if (MapUtils.isNotEmpty(props)) {
			sessions.add(props);
			LRUCache.getUserSessionCache().put(email, sessions);
			return getAccount((long) props.get("uid"), orgDomain);
		}
		return null;
		
	}
	
	private Account getAccount(long userId, String orgDomain) throws Exception {
		User user = getFacilioUserv3(userId, orgDomain);
		if (user == null) {
			throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "User doesn't exists in the current Org");
		}
		Organization org = AuthUtill.getOrgBean().getOrgv2(user.getOrgId());
		Account account = new Account(org, user);
		return account;
	}

	@Override
	public void clearUserSessionv2(long uid, String email, String token) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getUserSessionModule().getTableName());
		
		builder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		builder.andCondition(CriteriaAPI.getCondition("TOKEN", "token", token, StringOperators.IS));
		
		builder.delete();
		
		LRUCache.getUserSessionCache().remove(email);
	}

	@Override
	public void clearAllUserSessionsv2(long uid, String email) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getUserSessionModule().getTableName());
		builder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		builder.delete();
		
		LRUCache.getUserSessionCache().remove(email);
	}
	
	static User createUserFromProps(Map<String, Object> prop, boolean fetchRole, boolean fetchSpace, boolean isPortalRequest) throws Exception {
		User user = FieldUtil.getAsBeanFromMap(prop, User.class);
		if (user.getPhotoId() > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStoreFromOrg(user.getOrgId(), user.getOuid());
			user.setAvatarUrl(fs.getPrivateUrl(user.getPhotoId(), isPortalRequest));
			user.setOriginalUrl(fs.orginalFileUrl(user.getPhotoId()));
		}
		return user;
	}
	
	@Override
	public String generatePermalinkForURL(String url, long uid, long orgId) throws Exception {
		
		Organization org = AuthUtill.getOrgBean().getOrgv2(orgId);
		User user = getFacilioUserv3(uid, org.getDomain());
		String tokenKey = orgId + "-" + user.getOuid();
		String jwt = AuthUtill.createJWT("id", "auth0", tokenKey, System.currentTimeMillis() + 24 * 60 * 60000, false);
		
		JSONObject sessionInfo = new JSONObject();
		sessionInfo.put("allowUrls", url);
		
		List<FacilioField> fields = AccountConstants.getUserSessionFields();

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getUserSessionModule().getTableName())
				.fields(fields);

		Map<String, Object> props = new HashMap<>();
		props.put("uid", uid);
		props.put("sessionType", AccountConstants.SessionType.PERMALINK_SESSION.getValue());
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
				.select(AccountConstants.getUserSessionFields())
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
	public long createUserv2(long orgId, User user) throws Exception {
		// TODO Auto-generated method stub
		User orgUser = getFacilioUserv3(user.getEmail(), orgId, null);
		if (orgUser != null) {
			throw new Exception("user_already_exists");
		}
		else {
			long uid = addUserEntryv2(user);
			user.setUid(uid);
			user.setDefaultOrg(true);
		}
		user.setOrgId(orgId);
		user.setUserStatus(true);
		//AccountUtil.setCurrentAccount(orgId);
		return addToORGUsersv2(user);
	}
	
	private long addToORGUsersv2(User user) throws Exception {
				
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(AccountConstants.getAccountsOrgUserFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, props.get("id"));
		user.setOuid((Long)props.get("id"));
		return (Long)props.get("id");
	}
	
	@Override
	public String getEncodedTokenv2(User user) {
		return EncryptionUtil.encode(user.getOrgId()+ USER_TOKEN_REGEX +user.getUid() + USER_TOKEN_REGEX + user.getUid()+ USER_TOKEN_REGEX + user.getEmail() + USER_TOKEN_REGEX + System.currentTimeMillis());
	}

	
	@Override
	public boolean updateUserv2(long ouid, User user) throws Exception {
		// TODO Auto-generated method stub
		boolean userUpdateStatus = updateUserEntryv2(user);
		if (userUpdateStatus) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAccountsOrgUserModule().getTableName())
					.fields(AccountConstants.getAccountsOrgUserFields());
			
			updateBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid), NumberOperators.EQUALS));
			
		    Map<String, Object> props = FieldUtil.getAsProperties(user);
			
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
		}
		return false;
	}

	private GenericSelectRecordBuilder getFacilioUserBuilder(String portalDomain, boolean includePortal) {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		fields.addAll(AccountConstants.getAccountsOrgUserFields());
		fields.add(AccountConstants.getOrgIdField(AccountConstants.getOrgModule()));
		
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
					.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.innerJoin("Organizations")
					.on("Account_ORG_Users.ORGID=Organizations.ORGID");

		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		
		if (includePortal) {
			if (StringUtils.isNullOrEmpty(portalDomain)) {
				portalDomain = "app";
			}
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.CITY", "portalDomain", portalDomain, StringOperators.IS));
		}
		
		return selectBuilder;
	}
	
	public User getFacilioUserv3(long userId, String orgDomain) throws Exception {
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(null, false);
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		selectBuilder.andCriteria(userEmailCriteria);
		
		if (!StringUtils.isNullOrEmpty(orgDomain)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.FACILIODOMAINNAME", "facilioDomainName", orgDomain, StringOperators.IS));
		} 
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}
	
	@Override
	public User getFacilioUserv3(String email, String orgDomain, String portalDomain) throws Exception {
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(portalDomain, true);
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", email, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", email, StringOperators.IS));
		selectBuilder.andCriteria(userEmailCriteria);
		
		if (!StringUtils.isNullOrEmpty(orgDomain)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.FACILIODOMAINNAME", "facilioDomainName", orgDomain, StringOperators.IS));
		} 
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}

	@Override
	public User getFacilioUserv3(String email, long orgId, String portalDomain) throws Exception {
		GenericSelectRecordBuilder selectBuilder = getFacilioUserBuilder(portalDomain, true);
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", email, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", email, StringOperators.IS));
		selectBuilder.andCriteria(userEmailCriteria);
		
		if (orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		} 
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}

	@Override
	public User getFacilioUserv3(String email) throws Exception {
		// TODO Auto-generated method stub
		return getFacilioUserv3(email, -1, null);
	}

	@Override
	public User getUserv2(String email, String portalDomain) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users");
		
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", email, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", email, StringOperators.IS));
			
		selectBuilder.andCriteria(userEmailCriteria);
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.CITY", "city", portalDomain, StringOperators.IS));
		
				
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}


	@Override
	public Account getPermalinkAccount(String token, List<String> urls) throws Exception {
		// TODO Auto-generated method stub
		if(verifyPermalinkForURL(token, urls)) {
			DecodedJWT decodedjwt = AuthUtill.validateJWT(token, "auth0");
	
			String[] tokens = null;
			if (decodedjwt.getSubject().contains(AuthUtill.JWT_DELIMITER)) {
				tokens = decodedjwt.getSubject().split(AuthUtill.JWT_DELIMITER)[0].split("-");
			}
			else {
				tokens = decodedjwt.getSubject().split("_")[0].split("-");
			}
			long orgId = Long.parseLong(tokens[0]);
			long ouid = Long.parseLong(tokens[1]);
			
			Account currentAccount = new Account(AccountUtil.getOrgBean().getOrg(orgId), AccountUtil.getUserBean().getUserInternal(ouid, false));
			return currentAccount;
		}
		return null;
	}

		
}
