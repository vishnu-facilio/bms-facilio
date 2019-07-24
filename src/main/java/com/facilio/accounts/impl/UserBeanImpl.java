package com.facilio.accounts.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Chain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountEmailTemplate;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
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
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.iam.accounts.exceptions.AccountException;
import com.iam.accounts.util.AuthUtill;
import com.iam.accounts.util.UserUtil;

;

public class UserBeanImpl implements UserBean {

	private static final long INVITE_LINK_EXPIRE_TIME = (7 * 24 * 60 * 60 * 1000L);
	private static final String USER_TOKEN_REGEX = "#";
	private static Logger log = LogManager.getLogger(UserBeanImpl.class.getName());

	private long getUid(String email) throws Exception {
		return getFacilioUser(email).getUid();
	}

	private long addUserEntry(User user, boolean emailVerificationRequired) throws Exception {
		User existingUser = getUserv2(user.getEmail(),"app");
		if(existingUser == null) {
			
			List<FacilioField> fields = AccountConstants.getAppUserFields();
			GenericInsertRecordBuilder insertBuilder = new SampleGenericInsertRecordBuilder()
					.table(AccountConstants.getAppUserModule().getTableName()).fields(fields);
	
			Map<String, Object> props = FieldUtil.getAsProperties(user);
	
			insertBuilder.addRecord(props);
			insertBuilder.save();
			if (emailVerificationRequired) {
				sendEmailRegistration(user);
			}
			return user.getUid();
			}
		return existingUser.getUid();
	}

	private void addAdminConsoleFacilioUser(User user) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.getInstance().getConnection();
			pstmt = conn.prepareStatement("INSERT INTO faciliousers(username, email, mobile, USERID) VALUES(?,?,?,?)");
			pstmt.setString(1, user.getEmail());
			pstmt.setString(2, user.getEmail());
			if (user.getMobile() == null || user.getMobile().isEmpty()) {
				user.setMobile(String.valueOf(user.getUid()));
			}
			pstmt.setString(3, user.getMobile());
			pstmt.setLong(4, user.getUid());
			pstmt.executeUpdate();
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}
		}
	}

	@Override
	public boolean updateUser(User user) throws Exception {
		if(UserUtil.updateUser(user, AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail())) {
			return updateUserEntry(user);
		}
		return false;
		
	}

	private boolean updateUserEntry(User user) throws Exception {
		List<FacilioField> fields = AccountConstants.getAppUserFields();
		fields.add(AccountConstants.getUserPasswordField());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAppUserModule().getTableName()).fields(fields);
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
	
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		int updatedRows = updateBuilder.update(props);

		GenericDeleteRecordBuilder relDeleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getShiftUserRelModule().getTableName()).andCondition(CriteriaAPI
						.getCondition("ORG_USERID", "userId", String.valueOf(user.getOuid()), NumberOperators.EQUALS));
		relDeleteBuilder.delete();

		if (user.getShiftId() != null) {
			insertShiftRel(user.getOuid(), user.getShiftId());
		}

		return (updatedRows > 0);
	}

	@Override
	public void createUser(long orgId, User user) throws Exception {
		
		if(UserUtil.addUser(user, orgId, AccountUtil.getCurrentUser().getEmail()) > 0) {
			createUserEntry(orgId, user);
		}
		
	}
	
	@Override
	public void createUserEntry(long orgId, User user) throws Exception {

		long uid = addUserEntry(user, true);
		user.setUid(uid);
		user.setOrgId(orgId);
		user.setUserType(AccountConstants.UserType.USER.getValue());
		user.setUserStatus(true);
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean", user.getOrgId());
		user.setAccessibleSpace(userBean.getAccessibleSpaceList(user.getOuid()));
		addToORGUsers(user);
		addUserDetail(user);
	}
	

	private void addToORGUsers(User user) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		InsertRecordBuilder<ResourceContext> insertRecordBuilder = new InsertRecordBuilder<ResourceContext>()
				.moduleName(FacilioConstants.ContextNames.RESOURCE)
				.fields(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE));
		ResourceContext resource = new ResourceContext();
		resource.setName(user.getEmail());
		resource.setResourceType(ResourceType.USER);

		long id = insertRecordBuilder.insert(resource);
		user.setId(id);

		GenericInsertRecordBuilder insertBuilder = new SampleGenericInsertRecordBuilder()
				.table(AccountConstants.getAppOrgUserModule().getTableName()).fields(AccountConstants.getAppOrgUserFields());

		Map<String, Object> props = FieldUtil.getAsProperties(user);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		user.setOuid((long) props.get("ouid"));
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		context.put(FacilioConstants.ContextNames.MODULE_LIST,
				modBean.getSubModules(FacilioConstants.ContextNames.USERS, FacilioModule.ModuleType.READING));

		Chain addRDMChain = FacilioChainFactory.addResourceRDMChain();
		addRDMChain.execute(context);
		
	}

//	@Override
//	public long inviteAdminConsoleUser(long orgId, User user) throws Exception {
////		long userId = inviteUser(user, false);
////		if (userId > 0) {
////			acceptUser(user);
////		}
////		return userId;
//		return -1;
//	}

	public void addUserDetail(User user) throws Exception {
		if (user.getRoleId() == 0) {
			throw new AccountException(AccountException.ErrorCode.ROLE_ID_IS_NULL, "RoleID is Null " + user.getEmail());
		}
		Long shiftId = user.getShiftId();

		if (shiftId != null) {
			insertShiftRel(user.getOuid(), shiftId);
		}

		if (user.getAccessibleSpace() != null) {
			addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());
		}
		if (user.getGroups() != null) {
			addAccessibleTeam(user.getOuid(), user.getGroups());
		}
		if (AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE)) {
			List<ShiftContext> orgShifts = ShiftAPI.getAllShifts();
			if (CollectionUtils.isNotEmpty(orgShifts)) {
				for (ShiftContext shift : orgShifts) {
					if (shift.getDefaultShift()) {
						ShiftAPI.insertShiftUserRel(shift.getId(), user.getOuid());
						break;
					}
				}
			}
		}
	}

	private void insertShiftRel(long uid, Long shiftId)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		GenericInsertRecordBuilder shiftRelInsertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getShiftUserRelModule().getTableName())
				.fields(FieldFactory.getShiftUserRelModuleFields());

		ShiftUserRelContext rel = new ShiftUserRelContext();
		rel.setOuid(uid);
		rel.setShiftId(shiftId);

		Map<String, Object> relProps = FieldUtil.getAsProperties(rel);
		shiftRelInsertBuilder.addRecord(relProps);
		shiftRelInsertBuilder.save();
	}

	private String getUserLink(User user, String url) throws Exception {
		String inviteToken = AuthUtill.getResetPasswordToken(user);
		String hostname = "";
		if (user.isPortalUser()) {
			try {
				Organization org = AccountUtil.getOrgBean().getPortalOrg(user.getPortalId());
				hostname = "https://" + org.getDomain() + "/service";
				inviteToken = inviteToken + "&portalid=" + user.getPortalId();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}

		} else {
			// hostname="https://app."+user.getServerName();
			return AwsUtil.getConfig("clientapp.url") + "/app" + url + inviteToken;
		}
		return hostname + url + inviteToken;
	}

	public void sendInvitation(long ouid, User user) throws Exception {
		this.sendInvitation(ouid, user, false);
	}

	public void sendInvitation(long ouid, User user, boolean registration) throws Exception {
		user.setOuid(ouid);
		Map<String, Object> placeholders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "toUser", FieldUtil.getAsProperties(user), placeholders);
		CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeholders);
		CommonCommandUtil.appendModuleNameInKey(null, "inviter", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeholders);
		
			if(user.isPortalUser()) {
				String inviteLink = getUserLink(user,"/invitation/");
				if(registration)
				{
					inviteLink = getUserLink(user,"/emailregistration/");
				}
				placeholders.put("invitelink", inviteLink);
				AccountEmailTemplate.PORTAL_SIGNUP.send(placeholders);

		}
		
		 else {
			String inviteLink = getUserLink(user, "/invitation/");
			placeholders.put("invitelink", inviteLink);

			AccountEmailTemplate.INVITE_USER.send(placeholders);
		}

		String inviteLink = getUserLink(user, "/fconfirm_reset_password/");
		Map<String, Object> placeholders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "toUser", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("invitelink", inviteLink);
		
		AccountEmailTemplate.RESET_PASSWORD.send(placeholders);
		return true;
	}

	private void sendEmailRegistration(User user) throws Exception {

		String inviteLink = getUserLink(user, "/emailregistration/");
		Map<String, Object> placeholders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "toUser", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("invitelink", inviteLink);
		if (user.getEmail().contains("@facilio.com") || AwsUtil.disableCSP()) {
			AccountEmailTemplate.EMAIL_VERIFICATION.send(placeholders);
		} else {
			AccountEmailTemplate.ALERT_EMAIL_VERIFICATION.send(placeholders);
		}

	}

	@Override
	public User verifyEmail(String token) throws Exception {
		User user = UserUtil.verifyEmail(token);

		if (user != null) {
			if ((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
				try {
					user.setUserVerified(true);
					updateUserEntry(user);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				return user;
			}
		}
		return null;
	}

	@Override
	public User validateUserInvite(String token) throws Exception {
		User user = UserUtil.validateUserInviteToken(token);
		return user;
		
	}

	@Override
	public boolean resendInvite(long ouid) throws Exception {

		User user = getUser(ouid);
		if (user.getInviteAcceptStatus()) {
			// invitation already accepted
			return false;
		}

		if(UserUtil.resendInvite(user.getOrgId(), user.getUid())) {
			FacilioField invitedTime = new FacilioField();
			invitedTime.setName("invitedTime");
			invitedTime.setDataType(FieldType.NUMBER);
			invitedTime.setColumnName("INVITEDTIME");
			invitedTime.setModule(AccountConstants.getAppOrgUserModule());
	
			List<FacilioField> fields = new ArrayList<>();
			fields.add(invitedTime);
	
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAppOrgUserModule().getTableName()).fields(fields);
			
			updateBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid), NumberOperators.EQUALS));
		
			Map<String, Object> props = new HashMap<>();
			props.put("invitedTime", System.currentTimeMillis());
	
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				sendInvitation(ouid, user);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean acceptInvite(String token, String password) throws Exception {
		User user = UserUtil.acceptInvite(token, password);
		if(user != null) {
		   return AccountUtil.getTransactionalUserBean().acceptUser(user);	
		}
		return false;
	}

	
	
	private User getInvitedUser(long ouid) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("Users")
				.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID");
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "org_UserId", String.valueOf(ouid), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
		selectBuilder.andCriteria(criteria);
	
	
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user = createUserFromProps(props.get(0), true, true, false); // Giving as false because user cannot
																				// accept invite via portal APIs
			return user;
		}
		return null;
	}

	@Override
	public void addUserMobileSetting(UserMobileSetting userMobileSetting) throws Exception {

		if (userMobileSetting.getUserId() == -1) {
			userMobileSetting.setUserId(getUid(userMobileSetting.getEmail()));
		}
		if (userMobileSetting.getCreatedTime() == -1) {
			userMobileSetting.setCreatedTime(System.currentTimeMillis());
		}
		userMobileSetting.setFromPortal(AccountUtil.getCurrentAccount().getUser().isPortalUser());

		// Fetching and adding only if it's not present already
		FacilioModule module = AccountConstants.getUserMobileSettingModule();
		List<FacilioField> fields = AccountConstants.getUserMobileSettingFields();

		UserMobileSetting currentSetting = getUserMobileSetting(userMobileSetting.getUserId(),
				userMobileSetting.getMobileInstanceId(), module, fields);
		if (currentSetting == null) {
			addUserMobileSetting(userMobileSetting, module, fields);
		} else {
			userMobileSetting.setUserMobileSettingId(currentSetting.getUserMobileSettingId());
			userMobileSetting.setUserId(-1);
			userMobileSetting.setMobileInstanceId(null);
			updateUserMobileSetting(userMobileSetting, module, fields);
		}
	}

	private UserMobileSetting getUserMobileSetting(long userId, String instance, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField userIdField = fieldMap.get("userId");
		FacilioField instanceField = fieldMap.get("mobileInstanceId");
		boolean isPortal = AccountUtil.getCurrentAccount().getUser().isPortalUser();

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

	private long addUserMobileSetting(UserMobileSetting userMobileSetting, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
				.fields(fields);

		return insertBuilder.insert(FieldUtil.getAsProperties(userMobileSetting));
	}

	private void updateUserMobileSetting(UserMobileSetting userMobileSetting, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		FacilioField idField = FieldFactory.getAsMap(fields).get("userMobileSettingId");
		long id = userMobileSetting.getUserMobileSettingId();
		userMobileSetting.setUserMobileSettingId(-1);

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getCondition(idField, String.valueOf(id), PickListOperators.IS));
		updateBuilder.update(FieldUtil.getAsProperties(userMobileSetting));
	}

	@Override
	public void removeUserMobileSetting(String mobileInstanceId) throws Exception {

		boolean isPortal = AccountUtil.getCurrentAccount().getUser().isPortalUser();

		List<FacilioField> fields = AccountConstants.getUserMobileSettingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getUserMobileSettingModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("mobileInstanceId"), mobileInstanceId,
						StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fromPortal"), String.valueOf(isPortal),
						BooleanOperators.IS));

		builder.delete();
	}

	@Override
	public boolean deleteUser(long ouid) throws Exception {

		User user = getUser(ouid);
		if(UserUtil.deleteUser(user, AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail())) {
			FacilioField deletedTime = new FacilioField();
			deletedTime.setName("deletedTime");
			deletedTime.setDataType(FieldType.NUMBER);
			deletedTime.setColumnName("DELETED_TIME");
			deletedTime.setModule(AccountConstants.getAppOrgUserModule());
	
			List<FacilioField> fields = new ArrayList<>();
			fields.add(deletedTime);
	
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAppOrgUserModule().getTableName());
			
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "org_UserId", String.valueOf(ouid), NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
			updateBuilder.andCriteria(criteria);
			
			Map<String, Object> props = new HashMap<>();
			props.put("deletedTime", System.currentTimeMillis());
	
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean disableUser(long ouid) throws Exception {
		User user = getUser(ouid);
		if(UserUtil.disableUser(user)) {
			FacilioField userStatus = new FacilioField();
			userStatus.setName("userStatus");
			userStatus.setDataType(FieldType.BOOLEAN);
			userStatus.setColumnName("USER_STATUS");
			userStatus.setModule(AccountConstants.getAppOrgUserModule());
	
			List<FacilioField> fields = new ArrayList<>();
			fields.add(userStatus);
	
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAppOrgUserModule().getTableName()).fields(fields)
					;
			
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "org_UserId", String.valueOf(ouid), NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
			updateBuilder.andCriteria(criteria);
	
			Map<String, Object> props = new HashMap<>();
			props.put("userStatus", false);
	
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean enableUser(long ouid) throws Exception {
		User user = getUser(ouid);
		if(UserUtil.enableUser(user)) {
			FacilioField userStatus = new FacilioField();
			userStatus.setName("userStatus");
			userStatus.setDataType(FieldType.BOOLEAN);
			userStatus.setColumnName("USER_STATUS");
			userStatus.setModule(AccountConstants.getAppOrgUserModule());
	
			List<FacilioField> fields = new ArrayList<>();
			fields.add(userStatus);
	
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAppOrgUserModule().getTableName()).fields(fields)
					;
			
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "org_UserId", String.valueOf(ouid), NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
			updateBuilder.andCriteria(criteria);
	
			Map<String, Object> props = new HashMap<>();
			props.put("userStatus", true);
	
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean setDefaultOrg(long uid, long orgId) throws Exception {

		FacilioField isDefaultOrg = new FacilioField();
		isDefaultOrg.setName("isDefaultOrg");
		isDefaultOrg.setDataType(FieldType.BOOLEAN);
		isDefaultOrg.setColumnName("ISDEFAULT");
		isDefaultOrg.setModule(AccountConstants.getAppOrgUserModule());

		List<FacilioField> fields = new ArrayList<>();
		fields.add(isDefaultOrg);

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAppOrgUserModule().getTableName()).fields(fields)
				;
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
		updateBuilder.andCriteria(criteria);
		
		Map<String, Object> props = new HashMap<>();
		props.put("isDefaultOrg", false);

		updateBuilder.update(props);

		GenericUpdateRecordBuilder updateBuilder1 = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAppOrgUserModule().getTableName()).fields(fields)
				.andCustomWhere("ORGID =? AND USERID = ? AND DELETED_TIME = -1", orgId, uid);

		Criteria criteria2 = new Criteria();
		criteria2.addAndCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		criteria2.addAndCondition(CriteriaAPI.getCondition("USERID", "orgUserId", String.valueOf(uid), NumberOperators.EQUALS));
		criteria2.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
		
		updateBuilder1.andCriteria(criteria2);
		
		
		Map<String, Object> props1 = new HashMap<>();
		props1.put("isDefaultOrg", true);

		updateBuilder1.update(props1);
		return true;
	}

	@Override
	public User getUser(long ouid) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder().select(fields).table("Users")
				.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID");
	
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
		
		selectBuilder.andCriteria(criteria);
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user = createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}
	
	@Override
	public User getUser(long orgId, long userId) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder().select(fields).table("Users")
				.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID");
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
		selectBuilder.andCriteria(criteria);
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user = createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}

//	@Override
//	public User getUserInternal(long ouid) throws Exception {
//		return getUserInternal(ouid, true);
//	}

	@Override
	public User getUserInternal(long ouid, boolean withRole) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());

		FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(AccountConstants.getAppOrgUserModule());
		fields.add(orgId);

		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder().select(fields).table("Users")
				.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID");
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
		
		selectBuilder.andCriteria(criteria);
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user = createUserFromProps(props.get(0), withRole, true, false);
			return user;
		}
		return null;
	}

//	@Override
//	public User getUserFromEmail(String email) throws Exception {
//
//		List<FacilioField> fields = new ArrayList<>();
//		fields.addAll(AccountConstants.getAppUserFields());
//		fields.addAll(AccountConstants.getAppOrgUserFields());
//		fields.add(AccountConstants.getOrgIdField());
//
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("Users")
//				.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID");
//			
//		Criteria criteria = new Criteria();
//		criteria.addAndCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
//		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
//		criteria.addAndCondition(CriteriaAPI.getCondition("EMAIL", "email", email, StringOperators.IS));
//		criteria.addAndCondition(CriteriaAPI.getCondition("ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
//				
//		selectBuilder.andCriteria(criteria);
//		List<Map<String, Object>> props = selectBuilder.get();
//		if (props != null && !props.isEmpty()) {
//			User user = createUserFromProps(props.get(0), true, true, false);
//			return user;
//		}
//		return null;
//	}

	@Override
	public User getUserFromPhone(String phone) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("Users")
				.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID");
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		
		Criteria criteria2 = new Criteria();
		criteria2.addAndCondition(CriteriaAPI.getCondition("PHONE", "phone", phone, StringOperators.IS));
		criteria2.addOrCondition(CriteriaAPI.getCondition("MOBILE", "mobile", phone, StringOperators.IS));
		
		selectBuilder.andCriteria(criteria);
		selectBuilder.andCriteria(criteria2);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user = createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}

	@Override
	public User getFacilioUser(String emailOrPhone) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder().select(fields).table("Users")
				.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID");
			
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Users.EMAIL", "email", emailOrPhone, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Users.MOBILE", "mobile", emailOrPhone, StringOperators.IS));
		selectBuilder.andCriteria(userEmailCriteria);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
		
		Organization currentOrg = AccountUtil.getCurrentOrg();
		if (currentOrg == null) {
			throw new IllegalArgumentException("Organization cannot be empty");
		}
		criteria.addAndCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(currentOrg.getOrgId()), NumberOperators.EQUALS));
		
		selectBuilder.andCriteria(criteria);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user = createUserFromProps(props.get(0), true, true, false);
			return user;
		}

		return null;
	}

	@Override
	public User getPortalUsers(String email, long portalId) throws Exception {
		FacilioModule portalInfoModule = AccountConstants.getPortalInfoModule();

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField(portalInfoModule));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID AND ORG_Users.USER_TYPE=2")
				.innerJoin(portalInfoModule.getTableName())
				.on("ORG_Users.ORGID = Users.USERID")
				.andCustomWhere("PortalInfo.PORTALID="+ portalId + " and Users.EMAIL = "+email);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return createUserFromProps(props.get(0), true, false, true);
		}
		return null;
	}

	private User getPortalUser(long uid, long portalId) throws Exception {
		FacilioModule portalInfoModule = AccountConstants.getPortalInfoModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField(portalInfoModule));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID AND Account_ORG_Users.USER_TYPE=2")
				.innerJoin(portalInfoModule.getTableName())
				.on("Account_ORG_Users.ORGID = Account_Users.USERID")
				.andCustomWhere("PortalInfo.PORTALID="+ portalId + " and Account_Users.USERID = "+uid);
		

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return createUserFromProps(props.get(0), true, false, true);
		}

		return null;
	}

	@Override
	public User getPortalUser(long uid) throws Exception {
		PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(AccountUtil.getCurrentOrg().getId(), false);
		return getPortalUser(uid, portalInfo.getPortalId());
	}

	@Override
	public List<User> getUsers(Criteria criteria, Collection<Long>... ouids) throws Exception {

		List<Map<String, Object>> props = fetchUserProps(criteria, ouids);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, true, true, false);
				users.add(user);
			}
			return users;
		}
		return null;
	}

	@Override
	public Map<Long, List<User>> getUsersWithRoleAsMap(Collection<Long> roleIds) throws Exception {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getConditionFromList("ROLE_ID", "roleId", roleIds, PickListOperators.IS));
		List<Map<String, Object>> props = fetchUserProps(criteria);
		if (props != null && !props.isEmpty()) {
			Map<Long, List<User>> userMap = new HashMap<>();
			for (Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, false, false, false);

				List<User> users = userMap.get(user.getRoleId());
				if (users == null) {
					users = new ArrayList<>();
					userMap.put(user.getRoleId(), users);
				}
				users.add(user);
			}
			return userMap;
		}
		return null;
	}

	@Override
	public List<User> getUsersWithRole(long roleId) throws Exception {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(
				CriteriaAPI.getCondition("ROLE_ID", "roleId", String.valueOf(roleId), PickListOperators.IS));
		List<Map<String, Object>> props = fetchUserProps(criteria);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, false, false, false);
				users.add(user);
			}
			return users;
		}
		return null;
	}

	@Override
	public List<User> getUsersWithRoleAndAccessibleSpace(long roleId, long spaceId) throws Exception {

		BaseSpaceContext space = SpaceAPI.getBaseSpace(spaceId);
		if (space != null) {
			Set<Long> parentIds = getSpaceParentIds(space);

			Criteria criteria = new Criteria();
			criteria.addAndCondition(
					CriteriaAPI.getCondition("ROLE_ID", "roleId", String.valueOf(roleId), PickListOperators.IS));
			List<Map<String, Object>> props = fetchUserProps(criteria);
			if (props != null && !props.isEmpty()) {
				List<User> users = new ArrayList<>();
				for (Map<String, Object> prop : props) {
					User user = createUserFromProps(prop, false, true, false);
					if (user.getAccessibleSpace() == null || user.getAccessibleSpace().isEmpty()
							|| !Collections.disjoint(parentIds, user.getAccessibleSpace())) {
						users.add(user);
					}
				}
				return users;
			}
		}
		return null;
	}

	private Set<Long> getSpaceParentIds(BaseSpaceContext space) {
		Set<Long> parentIds = new HashSet<>();
		if (space.getSiteId() != -1) {
			parentIds.add(space.getSiteId());
		}
		if (space.getBuildingId() != -1) {
			parentIds.add(space.getBuildingId());
		}
		if (space.getFloorId() != -1) {
			parentIds.add(space.getFloorId());
		}
		if (space.getSpaceId1() != -1) {
			parentIds.add(space.getSpaceId1());
		}
		if (space.getSpaceId2() != -1) {
			parentIds.add(space.getSpaceId2());
		}
		if (space.getSpaceId3() != -1) {
			parentIds.add(space.getSpaceId3());
		}
		if (space.getSpaceId4() != -1) {
			parentIds.add(space.getSpaceId4());
		}
		parentIds.add(space.getId());
		return parentIds;
	}

	@Override
	public Map<Long, User> getUsersAsMap(Criteria criteria, Collection<Long>... ouids) throws Exception {
		List<Map<String, Object>> props = fetchUserProps(criteria, ouids);
		if (props != null && !props.isEmpty()) {
			Map<Long, User> users = new HashMap<>();
			for (Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, true, true, false);
				users.put(user.getId(), user);
			}
			return users;
		}
		return null;
	}

	private List<Map<String, Object>> fetchUserProps(Criteria criteria, Collection<Long>... ouids) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		List<FacilioField> orgUserFields = AccountConstants.getAppOrgUserFields();
		fields.addAll(orgUserFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("Users")
				.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID")
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getOrgUserModule()))
		;

		if (criteria != null && !criteria.isEmpty()) {
			selectBuilder.andCriteria(criteria);
		}

		if (ouids != null && ouids.length == 1) {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(orgUserFields);
			selectBuilder
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("ouid"), ouids[0], NumberOperators.EQUALS));
		}

		return selectBuilder.get();
	}

	@Override
	public User getUser(long orgId, String email) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("Users")
				.innerJoin("ORG_Users").on("Users.USERID = ORG_Users.USERID");

		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("EMAIL", "email", String.valueOf(email), StringOperators.IS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
				
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user = createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}

	@Override
	public List<Organization> getOrgs(long uid) throws Exception {
		return AuthUtill.getUserBean().getOrgsv2(uid);
	}

	@Override
	public Organization getDefaultOrg(long uid) throws Exception {
		return AuthUtill.getUserBean().getDefaultOrgv2(uid);
	}

	@Override
	public long inviteRequester(long orgId, User user) throws Exception {
		if (AccountUtil.getCurrentOrg() != null) {
			Organization org = AccountUtil.getOrgBean().getOrg(AccountUtil.getCurrentOrg().getDomain());
			PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(org.getId(), false);
			org.setPortalId(portalInfo.getPortalId());
			
			user.setPortalId(org.getPortalId());
			user.setId(addRequester(orgId, user, false, true));
			if (user.getAccessibleSpace() != null) {
				addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());
			}
			return user.getOuid();
		}
		return 0L;
	}

	@Override
	public long addRequester(long orgId, User user) throws Exception {
		// TODO Auto-generated method stub
		return addRequester(orgId, user, false, false);
	}

	private long addRequester(long orgId, User user, boolean emailVerification, boolean updateifexist)
			throws Exception {
		User portalUser = AuthUtill.getUserBean().getFacilioUserv3(user.getEmail(), user.getCity(), user.getCity());
		if (portalUser != null) {
			log.info("Requester email already exists in the portal for org: " + orgId + ", ouid: "
					+ portalUser.getOuid());
			return getFacilioUser(portalUser.getEmail()).getOuid();
		}
		if(AuthUtill.getUserBean().createUserv2(AccountUtil.getCurrentOrg().getId(), user) > 0) {
			addUserEntry(user, true);
			user.setDefaultOrg(true);
			user.setOrgId(orgId);
			user.setUserType(AccountConstants.UserType.REQUESTER.getValue());
			user.setUserStatus(true);
			addToORGUsers(user);
		
		}
		return -1;
	}


	@Override
	public boolean updateUserPhoto(long uid, long fileId) throws Exception {

		if(UserUtil.updateUserPhoto(uid, fileId)) {
			FacilioField photoId = new FacilioField();
			photoId.setName("photoId");
			photoId.setDataType(FieldType.NUMBER);
			photoId.setColumnName("PHOTO_ID");
			photoId.setModule(AccountConstants.getAppUserModule());
	
			List<FacilioField> fields = new ArrayList<FacilioField>();
			fields.add(photoId);
	
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAppUserModule().getTableName()).fields(fields)
					.andCustomWhere("USERID = ?", uid);
	
			Map<String, Object> props = new HashMap<>();
			props.put("photoId", fileId);
	
			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
		}
		return false;
	}

	private void addAccessibleSpace(long uid, List<Long> accessibleSpace) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getAccessibleSpaceModule().getTableName())
				.fields(AccountConstants.getAccessbileSpaceFields());

		Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);

		for (Long bsid : accessibleSpace) {
			Map<String, Object> props = new HashMap<>();
			props.put("ouid", uid);
			props.put("bsid", bsid);
			props.put("siteId", getParentSiteId(bsid, idVsBaseSpace));
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
	}

	private long getParentSiteId(long baseSpaceId, Map<Long, BaseSpaceContext> idVsBaseSpace) {
		BaseSpaceContext baseSpace = idVsBaseSpace.get(baseSpaceId);
		if (baseSpace.getSpaceTypeEnum() == SpaceType.SITE) {
			return baseSpace.getId();
		}
		return baseSpace.getSiteId();
	}

	private void addAccessibleTeam(long uid, List<Long> groups) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getGroupMemberModule().getTableName())
				.fields(AccountConstants.getGroupMemberFields());
		for (Long group : groups) {
			Map<String, Object> props = new HashMap<>();
			props.put("ouid", uid);
			props.put("groupId", group);
			props.put("memberRole", GroupMemberRole.MEMBER.getMemberRole());
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
	}

	static Map<Long, List<Long>> getAccessibleSpaceList(Collection<Long> uids) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getAccessbileSpaceFields())
				.table(ModuleFactory.getAccessibleSpaceModule().getTableName()).andCondition(CriteriaAPI
						.getCondition("ORG_USER_ID", "ouid", Strings.join(uids, ','), NumberOperators.EQUALS));

		Map<Long, List<Long>> ouidsVsAccessibleSpace = new HashMap<>();
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				if (ouidsVsAccessibleSpace.get(prop.get("ouid")) == null) {
					ouidsVsAccessibleSpace.put((long) prop.get("ouid"), new ArrayList<>());
				}
				ouidsVsAccessibleSpace.get(prop.get("ouid")).add((Long) prop.get("bsid"));
			}
			return ouidsVsAccessibleSpace;
		}
		return Collections.emptyMap();
	}

	public List<Long> getAccessibleSpaceList(long uid) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getAccessbileSpaceFields())
				.table(ModuleFactory.getAccessibleSpaceModule().getTableName()).andCustomWhere("ORG_USER_ID = ?", uid);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Long> bsids = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				bsids.add((Long) prop.get("bsid"));
			}
			return bsids;
		}
		return null;

	}

	public List<Long> getAccessibleGroupList(long uid) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupMemberFields())
				.table(AccountConstants.getGroupMemberModule().getTableName()).andCustomWhere("ORG_USERID = ?", uid);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Long> bsids = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				bsids.add((Long) prop.get("groupId"));
			}
			return bsids;
		}
		return null;

	}

	static User createUserFromProps(Map<String, Object> prop, boolean fetchRole, boolean fetchSpace,
			boolean isPortalRequest) throws Exception {
		User user = FieldUtil.getAsBeanFromMap(prop, User.class);
		if (user.getPhotoId() > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStoreFromOrg(user.getOrgId(), user.getOuid());
			user.setAvatarUrl(fs.getPrivateUrl(user.getPhotoId(), isPortalRequest));
			user.setOriginalUrl(fs.orginalFileUrl(user.getPhotoId()));
		}

		if (fetchRole) {
			if (user.getRoleId() > 0) {
				RoleBean roleBean = null;
				if (AccountUtil.getCurrentOrg() == null) {
					System.out.print("User org ID : " + user.getOrgId());
					roleBean = AccountUtil.getRoleBean(user.getOrgId());
				} else {
					roleBean = AccountUtil.getRoleBean();
				}
				user.setRole(roleBean.getRole(user.getRoleId(), false));
			}
		}

		if (fetchSpace) {
			UserBean userBean = (UserBean) BeanFactory.lookup("UserBean", user.getOrgId());
			user.setAccessibleSpace(userBean.getAccessibleSpaceList(user.getOuid()));
		}
		if(user.getUserType() == UserType.REQUESTER.getValue()) {
			PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(AccountUtil.getCurrentOrg().getOrgId(), false);
			user.setPortalId(portalInfo.getPortalId());
		}
		

		return user;
	}

	public String generatePermalinkForURL(String url, User user) throws Exception {

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		if (user == null) {
			user = AccountUtil.getCurrentUser();
		}
		long uid = user.getUid();
		long ouid = user.getId();

		String tokenKey = orgId + "-" + ouid;
		String jwt = AuthUtill.createJWT("id", "auth0", tokenKey, System.currentTimeMillis() + 24 * 60 * 60000,
				false);

		JSONObject sessionInfo = new JSONObject();
		sessionInfo.put("allowUrls", url);

		List<FacilioField> fields = AccountConstants.getUserSessionFields();

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getUserSessionModule().getTableName()).fields(fields);

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
				.select(AccountConstants.getUserSessionFields()).table("UserSessions")
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
			} else {
				// token valid
				return true;
			}
		}
		return false;
	}

	@Override
	public HashMap<Long, Set<Long>> getUserSites(List<Long> users) throws Exception {
		List<FacilioField> accessibleSpaceFields = AccountConstants.getAccessbileSpaceFields();
		Map<String, FacilioField> accessibleFieldMap = FieldFactory.getAsMap(accessibleSpaceFields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(accessibleSpaceFields)
				.table(ModuleFactory.getAccessibleSpaceModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(accessibleFieldMap.get("ouid"), users, NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		HashMap<Long, Set<Long>> userSites = new HashMap<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				Set<Long> sites = userSites.get(prop.get("ouid"));
				if (sites == null) {
					sites = new HashSet<>();
					userSites.put((Long) prop.get("ouid"), sites);
				}
				sites.add((Long) prop.get("siteId"));
			}
		}
		return userSites;
	}

//	@Override
//	public User getPortalUser(String email, long portalId) throws Exception {
//		// TODO Auto-generated method stub
//		return getPortalUsers(email, portalId);
//	}

	@Override
	public boolean sendResetPasswordLinkv2(User user) throws Exception {

		String inviteLink = getUserLinkv2(user, "/fconfirm_reset_password/");
		Map<String, Object> placeholders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("invitelink", inviteLink);
		
		AccountEmailTemplate.RESET_PASSWORD.send(placeholders);
		return true;
	}
	
	private String getUserLinkv2(User user, String url) throws Exception {
		String inviteToken = AuthUtill.getResetPasswordToken(user);
		String hostname = "";
		if(user.isPortalUser())
		{
			try {
				PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(user.getPortalId(), true);
				Organization org = AccountUtil.getOrgBean().getOrg(portalInfo.getOrgId());
				org.setPortalId(portalInfo.getPortalId());
				
				hostname = "https://"+org.getDomain()+"/service";
				inviteToken = inviteToken +"&portalid="+user.getPortalId();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}
			
		}
		else
		{
		//	hostname="https://app."+user.getServerName();
		 return AwsUtil.getConfig("clientapp.url") +"/app"+ url + inviteToken;
		}
		return hostname + url + inviteToken;
	}

	@Override
	public boolean acceptUser(User user) throws Exception {
		// TODO Auto-generated method stub
		if(UserUtil.acceptUser(user)) {
			FacilioField inviteAcceptStatus = new FacilioField();
			inviteAcceptStatus.setName("inviteAcceptStatus");
			inviteAcceptStatus.setDataType(FieldType.BOOLEAN);
			inviteAcceptStatus.setColumnName("INVITATION_ACCEPT_STATUS");
			inviteAcceptStatus.setModule(AccountConstants.getAppOrgUserModule());

			FacilioField isDefaultOrg = new FacilioField();
			isDefaultOrg.setName("isDefaultOrg");
			isDefaultOrg.setDataType(FieldType.BOOLEAN);
			isDefaultOrg.setColumnName("ISDEFAULT");
			isDefaultOrg.setModule(AccountConstants.getAppOrgUserModule());

			FacilioField userStatus = new FacilioField();
			userStatus.setName("userStatus");
			userStatus.setDataType(FieldType.BOOLEAN);
			userStatus.setColumnName("USER_STATUS");
			userStatus.setModule(AccountConstants.getAppOrgUserModule());

			List<FacilioField> fields = new ArrayList<>();
			fields.add(inviteAcceptStatus);
			fields.add(userStatus);
			fields.add(isDefaultOrg);

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAppOrgUserModule().getTableName()).fields(fields)
					.andCustomWhere("ORG_USERID = ?", user.getOuid());

			Map<String, Object> props = new HashMap<>();
			props.put("inviteAcceptStatus", true);
			props.put("isDefaultOrg", true);
			props.put("userStatus", true);

			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				String password = user.getPassword();
				user = getInvitedUser(user.getOuid());
				if (user != null) {
					user.setUserVerified(true);
					user.setPassword(password);
					updateUserEntry(user);
					// LicenseApi.updateUsedLicense(user.getLicenseEnum());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean verifyUser(long userId) throws Exception {
		// TODO Auto-generated method stub
		if(UserUtil.verifyUser(userId)) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(AccountConstants.getAppOrgUserFields())
					.table(AccountConstants.getAppOrgUserModule().getTableName()).andCustomWhere("ORG_USERID = ?", userId);

			List<Map<String, Object>> props = selectBuilder.get();
			Long ouid = null;
			if (props != null && !props.isEmpty()) {
				Map<String, Object> prop = props.get(0);
				ouid = (Long) prop.get("uid");
			}

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAppUserModule().getTableName()).fields(AccountConstants.getAppUserFields())
					.andCustomWhere("USERID = ?", ouid);
			Map<String, Object> prop = new HashMap<>();
			prop.put("userVerified", true);
			return true;
		}
		return false;
	}

	
	private User getUserv2(String email, String portalDomain) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new SampleGenericSelectBuilder()
				.select(fields)
				.table("Users");
		
		Criteria userEmailCriteria = new Criteria();
		userEmailCriteria.addAndCondition(CriteriaAPI.getCondition("Users.EMAIL", "email", email, StringOperators.IS));
		userEmailCriteria.addOrCondition(CriteriaAPI.getCondition("Users.MOBILE", "mobile", email, StringOperators.IS));
		
		selectBuilder.andCriteria(userEmailCriteria);
		selectBuilder.andCondition(CriteriaAPI.getCondition("Users.CITY", "city", portalDomain, StringOperators.IS));
		
		
				
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0), true, true, false);
			return user;
		}
		return null;
	}

	
}
