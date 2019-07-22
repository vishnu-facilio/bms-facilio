package com.iam.accounts.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.impl.SampleGenericInsertRecordBuilder;
import com.facilio.accounts.impl.SampleGenericSelectBuilder;
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
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.LRUCache;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.iam.accounts.bean.UserBeanv2;
import com.iam.accounts.util.AccountConstants;
import com.iam.accounts.util.AuthUtill;

;

public class UserBeanImplv2 implements UserBeanv2 {

	private static final long INVITE_LINK_EXPIRE_TIME = (7 * 24 * 60 * 60 * 1000L);
	private static final String USER_TOKEN_REGEX = "#";
	private static Logger log = LogManager.getLogger(UserBeanImplv2.class.getName());

	
	private long addUserEntryv2(User user) throws Exception {
		return addUserEntryv2(user, false, false);
	}

	private long addUserEntryv2(User user, boolean emailVerificationRequired, boolean isPortalUser) throws Exception {

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

	
	private long addUserEntry(User user, boolean emailVerificationRequired, boolean isPortalUser) throws Exception {

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
	
	
	public boolean updateUserv2(User user) throws Exception {
		return updateUserEntryv2(user);
	}

	private boolean updateUserEntryv2(User user) throws Exception {
		List<FacilioField> fields = AccountConstants.getAccountsUserFields();
		fields.add(AccountConstants.getUserPasswordField());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("USERID = ?", user.getUid());
		

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


	private long inviteUserv2(long orgId, User user, boolean sendInvitation) throws Exception {

		User orgUser = getFacilioUserv3(user.getEmail(), user.getCity());
		if (orgUser != null) {
			throw new AccountException(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS, "This user already exists in your organization.");
		}
		else {
			long uid = addUserEntryv2(user, false, false);
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
	public long inviteUserv2(long orgId, User user) throws Exception {
		return  inviteUserv2(orgId, user, true);
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
		UserBeanImplv2 us = new UserBeanImplv2();
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
				.fields(fields)
				.andCustomWhere("ORGID = ? AND USERID = ?", orgId, userId);

		Map<String, Object> props = new HashMap<>();
		props.put("invitedTime", System.currentTimeMillis());
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean acceptInvitev2(String token, String password) throws Exception {
		User user = getUserFromToken(token);
		user.setPassword(password);
		long orgId=(user.getOrgId());
		if(AuthUtill.getTransactionalUserBean(orgId).acceptUserv2(user)) {
			return true;
		}
		return false;
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
					.fields(fields)
					.andCustomWhere("ORG_USERID = ?", user.getOuid());

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
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.andCustomWhere("ORG_USERID = ? AND DELETED_TIME = -1", ouid);

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
				.fields(fields)
				.andCustomWhere("ORG_USERID = ? AND DELETED_TIME = -1", ouid);
		
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
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("ORGID = ? AND USERID = ? AND DELETED_TIME = -1", orgId, uId);
				
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
				.fields(fields)
				.andCustomWhere("ORGID = ? AND USERID = ? AND DELETED_TIME = -1", orgId, uid);
		
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
				.fields(fields)
				.andCustomWhere("USERID = ? AND DELETED_TIME = -1", uid);
		
		Map<String, Object> props = new HashMap<>();
		props.put("isDefaultOrg", false);
		
		updateBuilder.update(props);
		
		GenericUpdateRecordBuilder updateBuilder1 = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAccountsOrgUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("ORGID =? AND ORG_USERID = ? AND DELETED_TIME = -1", orgId, uid);
		
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
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND ORG_USERID = ? AND DELETED_TIME = -1", AccountUtil.getCurrentOrg().getId(), ouid);
		
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
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND USERID = ? AND DELETED_TIME = -1", orgId, userId);
		
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
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.andCustomWhere("ORG_USERID = ? AND DELETED_TIME = -1", ouid);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		log.info(selectBuilder.toString() + " query returned null");
		return null;
	}

	@Override
	public User getUserFromEmailv2(String email) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		fields.addAll(AccountConstants.getAccountsOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND EMAIL = ? AND DELETED_TIME = -1 and ISDEFAULT = ?", AccountUtil.getCurrentOrg().getId(), email, true);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}
	
	@Override
	public User getUserFromPhonev2(String phone) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		fields.addAll(AccountConstants.getAccountsOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND (PHONE = ? OR MOBILE = ?) AND DELETED_TIME = -1 and ISDEFAULT = ?", AccountUtil.getCurrentOrg().getId(), phone, phone, true);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}


	@Override
	public List<User> getUsersv2(Criteria criteria, Collection<Long>... ouids) throws Exception {
		
		List<Map<String, Object>> props = fetchUserProps(criteria, ouids);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, true, true, false);
				users.add(user);
			}
			return users;
		}
		return null;
	}
	
	@Override
	public Map<Long, User> getUsersAsMapv2(Criteria criteria, Collection<Long>... ouids) throws Exception {
		List<Map<String, Object>> props = fetchUserProps(criteria, ouids);
		if (props != null && !props.isEmpty()) {
			Map<Long, User> users = new HashMap<>();
			for(Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, true, true, false);
				users.put(user.getId(), user);
			}
			return users;
		}
		return null;
	}
	
	private List<Map<String, Object>> fetchUserProps (Criteria criteria, Collection<Long>... ouids) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAccountsUserFields());
		List<FacilioField> orgUserFields = AccountConstants.getAccountsOrgUserFields();
		fields.addAll(orgUserFields);
		
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getOrgUserModule()))
				;
		
		if (criteria != null && !criteria.isEmpty()) {
			selectBuilder.andCriteria(criteria);
		}
				
		if (ouids != null && ouids.length == 1) {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(orgUserFields);
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ouid"), ouids[0], NumberOperators.EQUALS));
		}
		
		return selectBuilder.get();
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
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND EMAIL = ? AND DELETED_TIME = -1", orgId, email);
		
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
				.on("Organizations.ORGID = Account_ORG_Users.ORGID")
				.andCustomWhere("Account_ORG_Users.USERID = ? AND Organizations.DELETED_TIME=-1 AND Account_ORG_Users.DELETED_TIME=-1", uid);
		
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
				.on("Organizations.ORGID = Account_ORG_Users.ORGID")
				.andCustomWhere("Account_ORG_Users.USERID = ? AND Account_ORG_Users.ISDEFAULT = ? AND Organizations.DELETED_TIME=-1", uid, true);
		
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
				.fields(fields)
				.andCustomWhere("USERID = ?", uid);

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
					.fields(fields)
					.andCustomWhere("USERID = ? AND TOKEN = ?", uid, token);

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
				.on("Account_Users.USERID =UserSessions.USERID")
				.andCustomWhere("Users.USERID = ?", uid);
		
		if (isActive != null) {
			selectBuilder.andCustomWhere("UserSessions.IS_ACTIVE = ?", isActive);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props;
		}
		return null;
	
	}
	@Override
	public boolean verifyUserSessionv2(String email, String token) throws Exception {
		List sessions = (List) LRUCache.getUserSessionCache().get(email);
		if (sessions == null) {
			sessions = new ArrayList<>();
		}
		if (sessions.contains(token)) {
			return true;
		}
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(AccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID = UserSessions.USERID")
				.andCustomWhere("(Account_Users.EMAIL = ? ) AND UserSessions.TOKEN = ? AND UserSessions.IS_ACTIVE = ?", email, token, true);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			sessions.add(token);
			LRUCache.getUserSessionCache().put(email, sessions);
			return true;
		}
		return false;
	}

	@Override
	public void clearUserSessionv2(long uid, String email, String token) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getUserSessionModule().getTableName())
				.andCustomWhere("USERID = ? AND TOKEN = ?", uid, token);

		builder.delete();
		
		LRUCache.getUserSessionCache().remove(email);
	}

	@Override
	public void clearAllUserSessionsv2(long uid, String email) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getUserSessionModule().getTableName())
				.andCustomWhere("USERID = ?", uid);

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
	
	public String generatePermalinkForURL(String url, User user) throws Exception {
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		if(user == null)
		{
		//	user = AccountUtil.getCurrentUser();
		}
		long uid = user.getUid();
		long ouid = user.getId();
		
		String tokenKey = orgId + "-" + ouid;
		String jwt = CognitoUtil.createJWT("id", "auth0", tokenKey, System.currentTimeMillis() + 24 * 60 * 60000, false);
		
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
    
    public boolean verifyPermalinkForURL(String token, List<String> urls) throws Exception {
    	
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getUserSessionFields())
				.table("UserSessions")
				.andCustomWhere("UserSessions.TOKEN = ? AND UserSessions.IS_ACTIVE = ?", token, true);
		
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
		User orgUser = getFacilioUserv3(user.getEmail(), user.getCity());
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
					.fields(AccountConstants.getAccountsOrgUserFields())
					.andCustomWhere("ORG_USERID = ?", ouid);

		    Map<String, Object> props = FieldUtil.getAsProperties(user);
			
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String generatePermalinkForURLv2(String url, User user) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyPermalinkForURLv2(String token, List<String> url) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User getFacilioUserv3(String email, String userDomain) throws Exception {
		// TODO Auto-generated method stub
	
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
				.on("Account_ORG_Users.ORGID=Organizations.ORGID")
				.andCustomWhere("(Account_Users.EMAIL= ? or Account_USERS.mobile = ? ) AND Account_Users.CITY = ? AND Account_ORG_Users.DELETED_TIME = -1 AND Organizations.DELETED_TIME = -1 ", email, email, userDomain);

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
		return getFacilioUserv3(email, "app");
	}

		
}
