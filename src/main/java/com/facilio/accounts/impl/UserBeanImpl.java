package com.facilio.accounts.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountEmailTemplate;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.EncryptionUtil;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UserBeanImpl implements UserBean {

	private long getUid(String email) throws Exception {
		
		FacilioField uid = new FacilioField();
		uid.setName("uid");
		uid.setDataType(FieldType.NUMBER);
		uid.setColumnName("USERID");
		uid.setModule(AccountConstants.getUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(uid);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getUserModule().getTableName())
				.andCustomWhere("EMAIL = ?", email);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return (long) props.get(0).get("uid");
		}
		return -1;
	}
	
	private long addUserEntry(User user) throws Exception {
		return addUserEntry(user, true);
	}

	private long addUserEntry(User user, boolean emailVerificationRequired) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getUserModule().getTableName())
				.fields(AccountConstants.getUserFields());

		Map<String, Object> props = FieldUtil.getAsProperties(user);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		if(emailVerificationRequired) {
			sendEmailRegistration(user.getOrgId(), user);
		}
		return (Long) props.get("id");
	}
	
	public boolean updateUser(User user) throws Exception {
		return updateUserEntry(user.getEmail(), user);
	}

	private boolean updateUserEntry(String email, User user) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getUserModule().getTableName())
				.fields(AccountConstants.getUserFields())
				.andCustomWhere("EMAIL = ?", email);

		Map<String, Object> props = FieldUtil.getAsProperties(user);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public long createUser(long orgId, User user) throws Exception {
		
		User orgUser = getUser(orgId, user.getEmail());
		if (orgUser != null) {
			if (orgUser.getUserType() == AccountConstants.UserType.REQUESTER.getValue()) {
				orgUser.setUserType(AccountConstants.UserType.USER.getValue());
				updateUser(orgUser.getId(), orgUser);
			}
			else {
				throw new Exception("user_already_exists");
			}
		}
		
		long uid = getUid(user.getEmail());
		if (uid == -1) {
			uid = addUserEntry(user);
			user.setDefaultOrg(true);
		}
		user.setUid(uid);
		user.setOrgId(orgId);
		user.setUserType(AccountConstants.UserType.USER.getValue());
		user.setUserStatus(true);
		user.setAccessibleSpace(getAccessibleSpaceList(uid));
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(AccountConstants.getOrgUserFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		long ouid = (Long) props.get("id");
		return ouid;
	}

	@Override
	public long inviteUser(long orgId, User user) throws Exception {
		
		User orgUser = getUser(orgId, user.getEmail());
		if (orgUser != null) {
			if (orgUser.getUserType() == AccountConstants.UserType.REQUESTER.getValue()) {
				orgUser.setUserType(AccountConstants.UserType.USER.getValue());
				updateUser(orgUser.getId(), orgUser);
				return orgUser.getId();
			}
			else {
				throw new AccountException(AccountException.ErrorCode.EMAIL_ALREADY_EXISTS, "This user already exists in your organization.");
			}
		}
		
		long uid = getUid(user.getEmail());
		if (uid == -1) {
			uid = addUserEntry(user, false);
			user.setDefaultOrg(true);
		}
		user.setUid(uid);
		user.setOrgId(orgId);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setUserType(AccountConstants.UserType.USER.getValue());
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(AccountConstants.getOrgUserFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		long ouid = (Long) props.get("id");
		user.setOuid(ouid);
		
		sendInvitation(orgId, ouid, user);
		addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());

		return ouid;
	}

	private String getEncodedToken(String value) {
		return EncryptionUtil.encode(value + "#" + System.currentTimeMillis());
	}

	private String getUserLink(String value, String url) {
		String inviteToken = getEncodedToken(value);
		return AwsUtil.getConfig("clientapp.url") + url + inviteToken;
	}
	
	private boolean sendInvitation(long orgId, long ouid, User user) throws Exception {
		String inviteLink = getUserLink(""+ouid,  "/app/invitation/");
		Map<String, Object> placeholders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(user), placeholders);
		CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeholders);
		CommonCommandUtil.appendModuleNameInKey(null, "inviter", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeholders);
		placeholders.put("invitelink", inviteLink);
		
		AccountEmailTemplate.INVITE_USER.send(placeholders);
		return true;
	}
	
	public boolean sendResetPassword(User user) throws Exception {

		String inviteLink = getUserLink(user.getEmail(), "/app/fconfirm_reset_password/");
		Map<String, Object> placeholders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("invitelink", inviteLink);
		
		AccountEmailTemplate.RESET_PASSWORD.send(placeholders);
		return true;
	}
	
	private boolean sendEmailRegistration(long orgId, User user) throws Exception {
			String inviteLink = getUserLink(user.getEmail(), "/app/emailregistration/");
			Map<String, Object> placeholders = new HashMap<>();
			CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(user), placeholders);
			placeholders.put("invitelink", inviteLink);
			
			AccountEmailTemplate.EMAIL_VERIFICATION.send(placeholders);
			return true;
		}
	@Override
	public boolean resendInvite(long ouid) throws Exception {
		
		User user = getUser(ouid);
		if (user.getInviteAcceptStatus()) {
			// invitation already accepted
			return false;
		}
		
		FacilioField invitedTime = new FacilioField();
		invitedTime.setName("invitedTime");
		invitedTime.setDataType(FieldType.NUMBER);
		invitedTime.setColumnName("INVITEDTIME");
		invitedTime.setModule(AccountConstants.getOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(invitedTime);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("ORG_USERID = ?", ouid);

		Map<String, Object> props = new HashMap<>();
		props.put("invitedTime", System.currentTimeMillis());
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			sendInvitation(user.getOrgId(), ouid, user);
			return true;
		}
		return false;
	}

	@Override
	public boolean acceptInvite(long ouid, String cognitoId) throws Exception {
		
		FacilioField inviteAcceptStatus = new FacilioField();
		inviteAcceptStatus.setName("inviteAcceptStatus");
		inviteAcceptStatus.setDataType(FieldType.BOOLEAN);
		inviteAcceptStatus.setColumnName("INVITATION_ACCEPT_STATUS");
		inviteAcceptStatus.setModule(AccountConstants.getOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(inviteAcceptStatus);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("ORG_USERID = ?", ouid);

		Map<String, Object> props = new HashMap<>();
		props.put("inviteAcceptStatus", true);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateUser(long ouid, User user) throws Exception {
		
		boolean userUpdateStatus = updateUserEntry(user.getEmail(), user);
		System.out.println("User status ----->"+ userUpdateStatus) ;
		if (userUpdateStatus) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getOrgUserModule().getTableName())
					.fields(AccountConstants.getOrgUserFields())
					.andCustomWhere("ORG_USERID = ?", ouid);

			deleteAccessibleSpace(user.getOuid());
			addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());

			Map<String, Object> props = FieldUtil.getAsProperties(user);
			
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addUserMobileSetting(UserMobileSetting userMobileSetting) throws Exception {
		
		if(userMobileSetting.getUserId() == -1) {
			userMobileSetting.setUserId(getUid(userMobileSetting.getEmail()));
		}
		if (userMobileSetting.getCreatedTime() == -1) {
			userMobileSetting.setCreatedTime(System.currentTimeMillis());
		}
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getUserMobileSettingModule().getTableName())
				.fields(AccountConstants.getUserMobileSettingFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(userMobileSetting);
		insertBuilder.addRecord(props);
		insertBuilder.save();
	}

	@Override
	public boolean deleteUser(long ouid) throws Exception {
		
		FacilioField deletedTime = new FacilioField();
		deletedTime.setName("deletedTime");
		deletedTime.setDataType(FieldType.NUMBER);
		deletedTime.setColumnName("DELETED_TIME");
		deletedTime.setModule(AccountConstants.getOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(deletedTime);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
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
	public boolean disableUser(long ouid) throws Exception {
		
		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(AccountConstants.getOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("ORG_USERID = ? AND DELETED_TIME = -1", ouid);
		
		Map<String, Object> props = new HashMap<>();
		props.put("userStatus", false);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean enableUser(long ouid) throws Exception {
		
		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(AccountConstants.getOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("ORG_USERID = ? AND DELETED_TIME = -1", ouid);
		
		Map<String, Object> props = new HashMap<>();
		props.put("userStatus", true);
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setDefaultOrg(long uid, long orgId) throws Exception {
		
		FacilioField isDefaultOrg = new FacilioField();
		isDefaultOrg.setName("isDefaultOrg");
		isDefaultOrg.setDataType(FieldType.BOOLEAN);
		isDefaultOrg.setColumnName("ISDEFAULT");
		isDefaultOrg.setModule(AccountConstants.getOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(isDefaultOrg);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("USERID = ? AND DELETED_TIME = -1", uid);
		
		Map<String, Object> props = new HashMap<>();
		props.put("isDefaultOrg", false);
		
		updateBuilder.update(props);
		
		GenericUpdateRecordBuilder updateBuilder1 = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("ORGID =? AND ORG_USERID = ? AND DELETED_TIME = -1", orgId, uid);
		
		Map<String, Object> props1 = new HashMap<>();
		props1.put("isDefaultOrg", true);
		
		updateBuilder1.update(props1);
		return true;
	}

	@Override
	public User getUser(long ouid) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("ORG_USERID = ? AND DELETED_TIME = -1", ouid);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  FieldUtil.getAsBeanFromMap(props.get(0), User.class);
			user.setAccessibleSpace(getAccessibleSpaceList(ouid));
			return user;
		}
		return null;
	}

	@Override
	public User getUser(String email) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("EMAIL = ? AND DELETED_TIME = -1 and ISDEFAULT = ?", email, true);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  FieldUtil.getAsBeanFromMap(props.get(0), User.class);
			user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
			return user;
//			return FieldUtil.getAsBeanFromMap(props.get(0), User.class);
		}
		return null;
	}

	public User getPortalUser(String email, long portalId) throws Exception {
		FacilioModule portalInfoModule = AccountConstants.getPortalInfoModule();
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getPortalUserFields());
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		fields.add(FieldFactory.getOrgIdField(portalInfoModule));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("faciliorequestors")
				.innerJoin("PortalInfo")
				.on("faciliorequestors.PORTALID = PortalInfo.PORTALID")
				.innerJoin("Users")
				.on("faciliorequestors.EMAIL = Users.EMAIL")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("faciliorequestors.EMAIL = ? AND faciliorequestors.PORTALID = ?", email, portalId);

		List<Map<String, Object>> props = selectBuilder.get();
		System.out.println(props);
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), User.class);
		}
		return null;
	}

	@Override
	public List<User> getUsers(Criteria criteria) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getOrgUserModule()))
				.andCriteria(criteria);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				User user = FieldUtil.getAsBeanFromMap(prop, User.class);
				user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
				users.add(user);
//				users.add(FieldUtil.getAsBeanFromMap(prop, User.class));
			}
			return users;
		}
		return null;
	}
	
	@Override
	public User getUser(long orgId, String email) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND EMAIL = ? AND DELETED_TIME = -1", orgId, email);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  FieldUtil.getAsBeanFromMap(props.get(0), User.class);
			user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
			return user;
//			return FieldUtil.getAsBeanFromMap(props.get(0), User.class);
		}
		return null;
	}
	
	@Override
	public List<Organization> getOrgs(long uid) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("ORG_Users")
				.on("Organizations.ORGID = ORG_Users.ORGID")
				.andCustomWhere("ORG_Users.USERID = ? AND Organizations.DELETED_TIME=-1", uid);
		
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
	public Organization getDefaultOrg(long uid) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("ORG_Users")
				.on("Organizations.ORGID = ORG_Users.ORGID")
				.andCustomWhere("ORG_Users.USERID = ? AND ORG_Users.ISDEFAULT = ? AND Organizations.DELETED_TIME=-1", uid, true);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), Organization.class);
		}
		return null;
	}
	
	@Override
	public long addRequester(long orgId, User user) throws Exception {
		
		User orgUser = getUser(orgId, user.getEmail());
		if (orgUser != null) {
			return orgUser.getId();
		}
		
		long uid = getUid(user.getEmail());
		if (uid == -1) {
			uid = addUserEntry(user);
			user.setDefaultOrg(true);
		}
		user.setUid(uid);
		user.setOrgId(orgId);
		user.setUserType(AccountConstants.UserType.REQUESTER.getValue());
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(AccountConstants.getOrgUserFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		long ouid = (Long) props.get("id");
		return ouid;
	}
	
	@Override
	public boolean updateUserPhoto(long uid, long fileId) throws Exception {
		
		FacilioField photoId = new FacilioField();
		photoId.setName("photoId");
		photoId.setDataType(FieldType.NUMBER);
		photoId.setColumnName("PHOTO_ID");
		photoId.setModule(AccountConstants.getUserModule());
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.add(photoId);
		
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getUserModule().getTableName())
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

	private void addAccessibleSpace (long uid, List<Long> accessibleSpace) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getAccessibleSpaceModule().getTableName())
				.fields(AccountConstants.getAccessbileSpaceFields());

		for(Long bsid : accessibleSpace) {
			Map<String, Object> props = new HashMap<>();
			props.put("ouid", uid);
			props.put("bsid", bsid);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
	}

	private void deleteAccessibleSpace(long uid) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getAccessibleSpaceModule().getTableName())
				.andCustomWhere("ORG_USER_ID = ?", uid);
		builder.delete();
	}

	public static List<Long> getAccessibleSpaceList (long uid) throws Exception {
	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(AccountConstants.getAccessbileSpaceFields())
			.table(ModuleFactory.getAccessibleSpaceModule().getTableName())
			.andCustomWhere("ORG_USER_ID = ?", uid);

	List<Map<String, Object>> props = selectBuilder.get();
	if (props != null && !props.isEmpty()) {
		List<Long> bsids = new ArrayList<>();
		for(Map<String, Object> prop : props) {
			bsids.add((Long) prop.get("bsid"));
		}
		return bsids;
	}
	return null;

	}

}
