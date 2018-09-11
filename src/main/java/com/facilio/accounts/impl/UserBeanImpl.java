package com.facilio.accounts.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.impl.OrgBeanImpl;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.accounts.util.AccountEmailTemplate;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.EncryptionUtil;
import com.facilio.bmsconsole.util.SMSUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.LRUCache;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class UserBeanImpl implements UserBean {

	private static final long INVITE_LINK_EXPIRE_TIME = (7 * 24 * 60 * 60 * 1000L);
	private static final String USER_TOKEN_REGEX = "#";
	private static Logger log = LogManager.getLogger(UserBeanImpl.class.getName());

	private long getUid(String email) throws Exception {
		
		FacilioField uid = new FacilioField();
		uid.setName("uid");
		uid.setDataType(FieldType.NUMBER);
		uid.setColumnName("USERID");
		uid.setModule(AccountConstants.getFacilioUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(uid);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getFacilioUserModule().getTableName())
				.andCustomWhere("email = ?", email);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return (long) props.get(0).get("uid");
		}
		return -1;
	}

	private long getPortalUid(long portalId, String email) throws Exception {

		FacilioField uid = new FacilioField();
		uid.setName("uid");
		uid.setDataType(FieldType.NUMBER);
		uid.setColumnName("USERID");
		uid.setModule(AccountConstants.getPortalUserModule());

		List<FacilioField> fields = new ArrayList<>();
		fields.add(uid);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getPortalUserModule().getTableName())
				.andCustomWhere("email = ? and PORTALID = ?", email, portalId);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return (long) props.get(0).get("uid");
		}
		return -1;
	}
	
	private long addUserEntry(User user) throws Exception {
		return addUserEntry(user, true, false);
	}

	private long addUserEntry(User user, boolean emailVerificationRequired, boolean isPortalUser) throws Exception {

		List<FacilioField> fields = AccountConstants.getUserFields();
		fields.add(AccountConstants.getUserPasswordField());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getUserModule().getTableName())
				.fields(fields);

		Map<String, Object> props = FieldUtil.getAsProperties(user);
	
		if(user.password()!=null)
		{
		props.put("password", user.password());
		}
		insertBuilder.addRecord(props);
		insertBuilder.save();
		long userId = (Long) props.get("id");
		user.setUid(userId);
		if(emailVerificationRequired) {
			sendEmailRegistration(user);
		}
		return userId;
	}
	
	
	private void addFacilioUser(User user){
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.getInstance().getConnection();
			pstmt = conn.prepareStatement("INSERT INTO faciliousers(username, email, mobile, USERID) VALUES(?,?,?,?)");
			pstmt.setString(1, user.getEmail());
			pstmt.setString(2, user.getEmail());
			if (user.getMobile() == null || user.getMobile().isEmpty()){
				user.setMobile(String.valueOf(user.getUid()));
			}
 			pstmt.setString(3, user.getMobile());
			pstmt.setLong(4, user.getUid());
			pstmt.executeUpdate();
		} catch (Exception e){
			log.info("Exception occurred ", e);
		} finally {
			try {
				if(pstmt!= null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}
		}
	}
	
	private void addAdminConsoleFacilioUser(User user){
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.getInstance().getConnection();
			pstmt = conn.prepareStatement("INSERT INTO faciliousers(username, email, mobile, USERID) VALUES(?,?,?,?)");
			pstmt.setString(1, user.getEmail());
			pstmt.setString(2, user.getEmail());
			if (user.getMobile() == null || user.getMobile().isEmpty()){
				user.setMobile(String.valueOf(user.getUid()));
			}
 			pstmt.setString(3, user.getMobile());
			pstmt.setLong(4, user.getUid());
			pstmt.executeUpdate();
		} catch (Exception e){
			log.info("Exception occurred ", e);
		} finally {
			try {
				if(pstmt!= null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}
		}
	}
	
	public boolean updateUser(User user) throws Exception {
		return updateUserEntry(user);
	}

	private boolean updateUserEntry(User user) throws Exception {
		List<FacilioField> fields = AccountConstants.getUserFields();
		fields.add(AccountConstants.getUserPasswordField());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getUserModule().getTableName())
				.fields(fields)
				.andCustomWhere("USERID = ?", user.getUid());
		

		Map<String, Object> props = FieldUtil.getAsProperties(user);
		if(user.password()!=null)
		{
		props.put("password", user.password());
		}
		int updatedRows = updateBuilder.update(props);
		
		GenericDeleteRecordBuilder relDeleteBuilder = new GenericDeleteRecordBuilder()
				                               .table(ModuleFactory.getShiftUserRelModule().getTableName())
				                               .andCondition(CriteriaAPI.getCondition("ORG_USERID", "userId", String.valueOf(user.getOuid()), NumberOperators.EQUALS));
		relDeleteBuilder.delete();		
		
		if (user.getShiftId() != null) {
			insertShiftRel(user.getOuid(), user.getShiftId());
		}
		
		if (updatedRows > 0) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public long createUser(long orgId, User user) throws Exception {
		
		User orgUser = getFacilioUser(orgId, user.getEmail());
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
			user.setUid(uid);
			addFacilioUser(user);
			user.setDefaultOrg(true);
		}
		user.setUid(uid);
		user.setOrgId(orgId);
		user.setUserType(AccountConstants.UserType.USER.getValue());
		user.setUserStatus(true);
		user.setAccessibleSpace(getAccessibleSpaceList(uid));
		return addToORGUsers(user);
	}
	
	private long addToORGUsers(User user) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InsertRecordBuilder<ResourceContext> insertRecordBuilder = new InsertRecordBuilder<ResourceContext>()
																		.moduleName(FacilioConstants.ContextNames.RESOURCE)
																		.fields(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE))
																		;
		ResourceContext resource = new ResourceContext();
		resource.setName(user.getEmail());
		resource.setResourceType(ResourceType.USER);
		
		long id = insertRecordBuilder.insert(resource);
		user.setId(id);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getOrgUserModule().getTableName())
				.fields(AccountConstants.getOrgUserFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		context.put(FacilioConstants.ContextNames.MODULE_LIST, modBean.getSubModules(FacilioConstants.ContextNames.USERS, FacilioModule.ModuleType.READING));
		
		Chain addRDMChain = FacilioChainFactory.addResourceRDMChain();
 		addRDMChain.execute(context);
		
		return id;
	}
	
	@Override
	public long inviteAdminConsoleUser(long orgId, User user) throws Exception {
		return inviteUser(orgId, user);
	}

	@Override
	public long inviteUser(long orgId, User user) throws Exception {

		if(user.getRoleId() == 0) {
			throw new AccountException(AccountException.ErrorCode.ROLE_ID_IS_NULL, "RoleID is Null " + user.getEmail());
		}

		User orgUser = getFacilioUser(orgId, user.getEmail());
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
			if( (AccountUtil.getCurrentOrg() != null) && (user.getTimezone() == null) ) {
				user.setTimezone(AccountUtil.getCurrentOrg().getTimezone());
			}
			if( (AccountUtil.getCurrentUser() != null) && (user.getLanguage() == null) ) {
				user.setLanguage(AccountUtil.getCurrentUser().getLanguage());
			}
			uid = addUserEntry(user, false, false);
			user.setUid(uid);
			addFacilioUser(user);
			user.setDefaultOrg(true);
		}
		user.setUid(uid);
		user.setOrgId(orgId);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setUserStatus(true);
		user.setUserType(AccountConstants.UserType.USER.getValue());
		long ouid = addToORGUsers(user);

		user.setOuid(ouid);
		Long shiftId = user.getShiftId();
		
		if (shiftId != null) {
			insertShiftRel(ouid, shiftId);
		}
		
		sendInvitation(ouid, user);
		if(user.getAccessibleSpace() != null) {
			addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());
		}
		if(user.getGroups() != null) {
			addAccessibleTeam(user.getOuid(), user.getGroups());
		}
		return ouid;
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
	
	public long inviteRequester(long orgId, User user) throws Exception {
		if(AccountUtil.getCurrentOrg() != null) {
			Organization portalOrg = AccountUtil.getOrgBean().getPortalOrg(AccountUtil.getCurrentOrg().getDomain());
			user.setPortalId(portalOrg.getPortalId());
			return addRequester(orgId, user, false, true);
		}
		return 0L;
	}


	private User getUserFromToken(String userToken){
		String[] tokenPortal = userToken.split("&");
		String token = EncryptionUtil.decode(tokenPortal[0]);
		String[] userObj = token.split(USER_TOKEN_REGEX);
		User user = null;
		if(userObj.length == 4) {
			user = new User();
			user.setOuid(Long.parseLong(userObj[0]));
			user.setUid(Long.parseLong(userObj[1]));
			user.setEmail(userObj[2]);
			user.setInvitedTime(Long.parseLong(userObj[3]));
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

	private String getEncodedToken(User user) {
		return EncryptionUtil.encode(user.getOuid() + USER_TOKEN_REGEX + user.getUid()+ USER_TOKEN_REGEX + user.getEmail() + USER_TOKEN_REGEX + System.currentTimeMillis());
	}

	private String getUserLink(User user, String url) {
		String inviteToken = getEncodedToken(user);
		String hostname = "";
		if(user.isPortalUser())
		{
			try {
				Organization org = AccountUtil.getOrgBean().getPortalOrg(user.getPortalId());
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
	
	public void sendInvitation(long ouid, User user) throws Exception {
		user.setOuid(ouid);
		String inviteLink = getUserLink(user,"/invitation/");
		Map<String, Object> placeholders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(user), placeholders);
		CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeholders);
		CommonCommandUtil.appendModuleNameInKey(null, "inviter", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeholders);
		placeholders.put("invitelink", inviteLink);
		if (user.getEmail().equals(user.getMobile())) {
			
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@ SMS invite Sent");
			try{
			SMSUtil.sendUserLink(user, inviteLink);
			}
			catch (Exception e)
			{
			CommonCommandUtil.emailException(user.getEmail(), "cannot send sms for the user", e, null);
			}
		} else {
			if(user.isPortalUser()) {
				//System.out.println("Adding portal users"+user);
				AccountEmailTemplate.PORTAL_SIGNUP.send(placeholders);

			}
			else {
			AccountEmailTemplate.INVITE_USER.send(placeholders);
			}
		}
	}
	
	public boolean sendResetPasswordLink(User user) throws Exception {

		String inviteLink = getUserLink(user, "/fconfirm_reset_password/");
		Map<String, Object> placeholders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("invitelink", inviteLink);
		
		AccountEmailTemplate.RESET_PASSWORD.send(placeholders);
		return true;
	}
	
	private void sendEmailRegistration(User user) throws Exception {
		
		String inviteLink = getUserLink(user, "/emailregistration/");
		Map<String, Object> placeholders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("invitelink", inviteLink);
		if (user.getEmail().contains("@facilio.com")) {
			 AccountEmailTemplate.EMAIL_VERIFICATION.send(placeholders);
		}
		else {
			AccountEmailTemplate.ALERT_EMAIL_VERIFICATION.send(placeholders);	
		}

	}

	@Override
	public User verifyEmail(String token){
		User user = getUserFromToken(token);

		if(user != null) {
			if((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
				try {
					user.setUserVerified(true);
					updateUser(user);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				return user;
			}
		}
		return null;
	}

	@Override
	public User resetPassword(String token, String password){
		User user = getUserFromToken(token);

		if(user != null) {
			if ((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
				try {
					user.setPassword(password);
					user.setUserVerified(true);
					updateUser(user);
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
				return user;
			}
		}
		return null;
	}

	@Override
	public User validateUserInvite(String token){
		User user = getUserFromToken(token);
		if(user != null){
			try {
				user = getUserWithPassword(user.getOuid());
			} catch (Exception e) {
				user = null;
			}
		}
		return user;
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
			sendInvitation(ouid, user);
			return true;
		}
		return false;
	}

	@Override
	public boolean acceptInvite(String token, String password) throws Exception {

		User user = getUserFromToken(token);

		if(user != null) {
			FacilioField inviteAcceptStatus = new FacilioField();
			inviteAcceptStatus.setName("inviteAcceptStatus");
			inviteAcceptStatus.setDataType(FieldType.BOOLEAN);
			inviteAcceptStatus.setColumnName("INVITATION_ACCEPT_STATUS");
			inviteAcceptStatus.setModule(AccountConstants.getOrgUserModule());

			FacilioField isDefaultOrg = new FacilioField();
			isDefaultOrg.setName("isDefaultOrg");
			isDefaultOrg.setDataType(FieldType.BOOLEAN);
			isDefaultOrg.setColumnName("ISDEFAULT");
			isDefaultOrg.setModule(AccountConstants.getOrgUserModule());

			List<FacilioField> fields = new ArrayList<>();
			fields.add(inviteAcceptStatus);
			fields.add(isDefaultOrg);

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getOrgUserModule().getTableName())
					.fields(fields)
					.andCustomWhere("ORG_USERID = ?", user.getOuid());

			Map<String, Object> props = new HashMap<>();
			props.put("inviteAcceptStatus", true);
			props.put("isDefaultOrg", true);

			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				user = AccountUtil.getUserBean().getUser(user.getOuid());
				user.setUserVerified(true);
				user.setPassword(password);
				updateUser(user);
				// LicenseApi.updateUsedLicense(user.getLicenseEnum());
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean updateUser(long ouid, User user) throws Exception {
		
		boolean userUpdateStatus = updateUserEntry(user);
		System.out.println("User status ----->"+ userUpdateStatus) ;
		if (userUpdateStatus) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getOrgUserModule().getTableName())
					.fields(AccountConstants.getOrgUserFields())
					.andCustomWhere("ORG_USERID = ?", ouid);

			if(user.getAccessibleSpace() != null) {
				deleteAccessibleSpace(user.getOuid());
				addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());
			}
			if(user.getGroups() != null) {
				deleteAccessibleGroups(user.getOuid());
				addAccessibleTeam(user.getOuid(), user.getGroups());
			}

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
		
		//Fetching and adding only if it's not present already
		FacilioModule module = AccountConstants.getUserMobileSettingModule();
		List<FacilioField> fields = AccountConstants.getUserMobileSettingFields();
		
		UserMobileSetting currentSetting = getUserMobileSetting(userMobileSetting.getUserId(), userMobileSetting.getMobileInstanceId(), module, fields);
		if (currentSetting == null) {
			addUserMobileSetting(userMobileSetting, module, fields);
		}
		else {
			userMobileSetting.setUserMobileSettingId(currentSetting.getUserMobileSettingId());
			userMobileSetting.setUserId(-1);
			userMobileSetting.setMobileInstanceId(null);
			updateUserMobileSetting(userMobileSetting, module, fields);
		}
	}
	
	private UserMobileSetting getUserMobileSetting(long userId, String instance, FacilioModule module, List<FacilioField> fields) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField userIdField = fieldMap.get("userId");
		FacilioField instanceField = fieldMap.get("mobileInstanceId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.andCondition(CriteriaAPI.getCondition(userIdField, String.valueOf(userId), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(instanceField, instance, StringOperators.IS))
														;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), UserMobileSetting.class);
		}
		return null;
	}
	
	private long addUserMobileSetting (UserMobileSetting userMobileSetting, FacilioModule module, List<FacilioField> fields) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields);
		
		return insertBuilder.insert(FieldUtil.getAsProperties(userMobileSetting));
	}
	
	private void updateUserMobileSetting (UserMobileSetting userMobileSetting, FacilioModule module, List<FacilioField> fields) throws Exception {
		FacilioField idField = FieldFactory.getAsMap(fields).get("userMobileSettingId");
		long id = userMobileSetting.getUserMobileSettingId();
		userMobileSetting.setUserMobileSettingId(-1);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(fields)
														.andCondition(CriteriaAPI.getCondition(idField, String.valueOf(id), PickListOperators.IS))
														;
		updateBuilder.update(FieldUtil.getAsProperties(userMobileSetting));
	}
	
	@Override
	public void removeUserMobileSetting(String mobileInstanceId) throws Exception {
		
		List<FacilioField> fields = AccountConstants.getUserMobileSettingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getUserMobileSettingModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("mobileInstanceId"), mobileInstanceId, StringOperators.IS));
		
		builder.delete();
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
			User user =  createUserFromProps(props.get(0));
			user.setAccessibleSpace(getAccessibleSpaceList(ouid));
			return user;
		}
		return null;
	}

	public User getUserWithPassword(long ouid) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		fields.add(AccountConstants.getUserPasswordField());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("ORG_USERID = ? AND DELETED_TIME = -1", ouid);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0));
			user.setAccessibleSpace(getAccessibleSpaceList(ouid));
			return user;
		}
		return null;
	}

	@Override
	public User getUser(String email) throws Exception {
		
		/*List<FacilioField> fields = new ArrayList<>();
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
		}*/
		return null;
	}

	@Override
	public User getFacilioUser(String email) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("faciliousers")
				.on("Users.USERID = faciliousers.USERID")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("(faciliousers.email = ? or faciliousers.username = ?) AND USER_STATUS = 1 AND DELETED_TIME = -1 and ISDEFAULT = ?", email, email, true);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0));
			user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
		System.out.println(user.getEmail()+"$$$$$$$$$$$$$$$$$$$$$"+ user.getMobile());
			return user;
		}
		
		return null;
	}
	
	@Override
	public User getFacilioUser(String email, String orgDomain) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("faciliousers")
				.on("Users.USERID = faciliousers.USERID")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.innerJoin("Organizations")
				.on("ORG_Users.ORGID=Organizations.ORGID")
				.andCustomWhere("(faciliousers.email = ? or faciliousers.mobile = ? ) AND ORG_Users.DELETED_TIME = -1 AND Organizations.DELETED_TIME = -1 AND Organizations.FACILIODOMAINNAME = ?", email, email, orgDomain);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0));
			user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
			return user;
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
				.on("faciliorequestors.USERID = Users.USERID")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("faciliorequestors.EMAIL = ? AND DELETED_TIME=-1 AND USER_VERIFIED=1 AND faciliorequestors.PORTALID = ?", email, portalId);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return createUserFromProps(props.get(0));
		}
		return null;
	}
	

	public User getPortalUsers(String email, long portalId) throws Exception {
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
				.on("faciliorequestors.USERID = Users.USERID")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("faciliorequestors.EMAIL = ? AND DELETED_TIME=-1  AND faciliorequestors.PORTALID = ?", email, portalId);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return createUserFromProps(props.get(0));
		}
		return null;
	}
	
	
	
	
	public User getPortalUser(long uid, long portalId) throws Exception {
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
				.on("faciliorequestors.USERID = Users.USERID")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("faciliorequestors.USERID = ? AND DELETED_TIME=-1  AND faciliorequestors.PORTALID = ?", uid, portalId);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			System.out.println(props);
			return createUserFromProps(props.get(0));
		}
		
		return  null ;

	}

	public User getPortalUser(long uid) throws Exception {
		return getPortalUser(uid, AccountUtil.getOrgBean().getPortalId());
	}
	
	
	
	@Override
	public List<User> getUsers(Criteria criteria, List<Long>... ouids) throws Exception {
		
		List<Map<String, Object>> props = fetchUserProps(criteria, ouids);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				User user = createUserFromProps(prop);
				user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
				users.add(user);
			}
			return users;
		}
		return null;
	}
	
	@Override
	public Map<Long, User> getUsersAsMap(Criteria criteria, List<Long>... ouids) throws Exception {
		List<Map<String, Object>> props = fetchUserProps(criteria, ouids);
		if (props != null && !props.isEmpty()) {
			Map<Long, User> users = new HashMap<>();
			for(Map<String, Object> prop : props) {
				User user = createUserFromProps(prop);
				user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
				users.put(user.getId(), user);
			}
			return users;
		}
		return null;
	}
	
	private List<Map<String, Object>> fetchUserProps (Criteria criteria, List<Long>... ouids) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		List<FacilioField> orgUserFields = AccountConstants.getOrgUserFields();
		fields.addAll(orgUserFields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getOrgUserModule()))
				.andCriteria(criteria);
				
		if (ouids != null && ouids.length == 1) {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(orgUserFields);
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ouid"), ouids[0], NumberOperators.EQUALS));
		}
		
		return selectBuilder.get();
	}

	@Override
	public User getFacilioUser(long orgId, String email) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("faciliousers")
				.on("Users.USERID = faciliousers.USERID")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("ORGID = ? AND (faciliousers.email = ? or faciliousers.mobile = ?) AND DELETED_TIME = -1", orgId, email, email);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			User user =  createUserFromProps(props.get(0));
			user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
			return user;
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
			User user =  createUserFromProps(props.get(0));
			user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
			return user;
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

		return addRequester(orgId, user, true,true);
	}
	@Override
	public long createRequestor(long orgId, User user) throws Exception {
		// TODO Auto-generated method stub
		return addRequester(orgId,  user,true,false);
	}
	
	private User getPortalUserForInternal(String email, long portalId) throws Exception {
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
				.on("faciliorequestors.USERID = Users.USERID")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.andCustomWhere("faciliorequestors.EMAIL = ? AND DELETED_TIME=-1 AND faciliorequestors.PORTALID = ?", email, portalId);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return createUserFromProps(props.get(0));
		}
		return null;
	}
	
	private long addRequester(long orgId, User user, boolean emailVerification, boolean updateifexist) throws Exception {
		User portalUser = getPortalUserForInternal(user.getEmail(), user.getPortalId());
		if (portalUser != null) {
//			log.info("Email Already Registered ");
//			if(!updateifexist)
//			{
//			Exception e = new Exception("Email Already Registered");
//			throw e;
//			}
			log.info("Requester email already exists in the portal for org: "+ orgId+", ouid: "+ portalUser.getOuid());
			return portalUser.getOuid();
		}
		
		long uid = getPortalUid(user.getPortalId(), user.getEmail());
		if (uid == -1) {
			uid = addUserEntry(user, false, true);
			user.setDefaultOrg(true);
		}
		user.setUid(uid);
		user.setOrgId(orgId);
		user.setUserType(AccountConstants.UserType.REQUESTER.getValue());
		user.setUserStatus(true);
		long ouid = addToORGUsers(user);
		
		addFacilioRequestor(user);
		
		if (emailVerification) {
			sendInvitation(ouid, user);
		}
		
		return ouid;
	}

	private void addFacilioRequestor(User user){
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.getInstance().getConnection();
			pstmt = conn.prepareStatement("INSERT INTO faciliorequestors(PORTALID, username, email,  USERID) VALUES(?,?,?,?)");
			pstmt.setLong(1, user.getPortalId());
			pstmt.setString(2, user.getEmail());
			pstmt.setString(3, user.getEmail());
			pstmt.setLong(4, user.getUid());
			pstmt.executeUpdate();
		} catch (Exception e){
			log.info("Exception occurred ", e);
		} finally {
			try {
				if(pstmt!= null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}
			try {
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				log.info("Exception occurred ", e);
			}
		}
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
		
		Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);

		for(Long bsid : accessibleSpace) {
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
	private void addAccessibleTeam (long uid, List<Long> groups) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getGroupMemberModule().getTableName())
				.fields(AccountConstants.getGroupMemberFields());
		for(Long group : groups) {
			Map<String, Object> props = new HashMap<>();
			props.put("ouid", uid);
			props.put("groupId", group);
			props.put("memberRole", GroupMemberRole.MEMBER.getMemberRole());
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
	
	private void deleteAccessibleGroups(long uid) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getGroupMemberModule().getTableName())
				.andCustomWhere("ORG_USERID = ?", uid);
		builder.delete();
	}
	
	static Map<Long, List<Long>> getAccessibleSpaceList(Collection<Long> uids) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getAccessbileSpaceFields())
				.table(ModuleFactory.getAccessibleSpaceModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ORG_USER_ID", "ouid", Strings.join(uids, ','), NumberOperators.EQUALS));
		
		Map<Long, List<Long>> ouidsVsAccessibleSpace = new HashMap<>();
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				if (ouidsVsAccessibleSpace.get(prop.get("ouid")) == null) {
					ouidsVsAccessibleSpace.put((long) prop.get("ouid"), new ArrayList<>());
				}
				ouidsVsAccessibleSpace.get(prop.get("ouid")).add((Long) prop.get("bsid"));
			}
			return ouidsVsAccessibleSpace;
		}
		return Collections.emptyMap();
	}

	static List<Long> getAccessibleSpaceList (long uid) throws Exception {
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
	
	static List<Long> getAccessibleGroupList (long uid) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupMemberFields())
				.table(AccountConstants.getGroupMemberModule().getTableName())
				.andCustomWhere("ORG_USERID = ?", uid);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Long> bsids = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				bsids.add((Long) prop.get("groupId"));
			}
			return bsids;
		}
		return null;

		}

/*	@Override
	public long addUserLicense(long orgId, long roleid, Integer number_of_users) throws Exception {

		List<FacilioField> fields = AccountConstants.getUserLicenseFields();

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getUserLicenseModule().getTableName())
				.fields(fields);

		Map<String, Object> props = new HashMap<>();
		props.put("orgId", orgId);
		props.put("roleId", roleid);
		props.put("numberofusers", number_of_users);

		insertBuilder.addRecord(props);
		insertBuilder.save();
		long Id = (Long) props.get("id");
		return Id;
	}
	
	@Override
	public void deleteUserLicense(long id) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getUserLicenseModule().getTableName())
				.andCustomWhere("id = ?", id);

		builder.delete();
	}

	
	@Override
	public boolean updateUserLicense(long id, Integer number_of_users) throws Exception {

		List<FacilioField> fields = AccountConstants.getUserLicenseFields();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getUserLicenseModule().getTableName())
				.fields(fields)
				.andCustomWhere("ID = ?", id);

		Map<String, Object> props = new HashMap<>();
		props.put("numberofusers", number_of_users);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	
	public Map<Long, Integer> getUserRoleLicenseMap(long orgid) throws Exception {
		
		List<FacilioField> fields = AccountConstants.getUserLicenseFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getUserLicenseModule().getTableName())
				.andCustomWhere("ORGID = ?", orgid);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<Long, Integer> map = new HashMap<>();  
			for (Map<String, Object> prop : props) {
				map.put((Long) prop.get("roleId"), (Integer) prop.get("numberofusers"));
			}
			return map;
		}
		return null;
	}

	public Integer getAvailableRoleLicense(long orgid, long roleid) throws Exception {
		
		List<FacilioField> fields = AccountConstants.getUserLicenseFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getUserLicenseModule().getTableName())
				.andCustomWhere("ORGID = ? AND ROLE_ID = ?", orgid, roleid );
		
		List<FacilioField> orgfields = AccountConstants.getOrgUserFields();
		GenericSelectRecordBuilder selectBuilder1 = new GenericSelectRecordBuilder()
		.select(orgfields)
		.table(AccountConstants.getOrgUserModule().getTableName())
		.andCustomWhere("ORGID = ? AND ROLE_ID = ? AND USER_STATUS = 1 AND DELETED_TIME = -1", orgid, roleid );
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<Map<String, Object>> props2 = selectBuilder1.get();
		if (props != null && !props.isEmpty()) {
			Integer allowedUsers =  (Integer) props.get(0).get("numberofusers");
			Integer activeUsers =	 props2.size();
			return (allowedUsers - activeUsers);
		}
		return Integer.MAX_VALUE;
	}
	
	public Integer getAvailableUserLicense(long orgid) throws Exception {
		
		
		List<FacilioField> orgfields = AccountConstants.getOrgUserFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
		.select(orgfields)
		.table(AccountConstants.getOrgUserModule().getTableName())
		.andCustomWhere("ORGID = ? AND USER_STATUS = 1 AND DELETED_TIME = -1", orgid );
		
		Map<String, Object> orgStaff = CommonCommandUtil.getOrgInfo(AccountUtil.getCurrentOrg().getOrgId(), "staff");
		List<Map<String, Object>> props = selectBuilder.get();
		Integer activeUsers = 0;
		Integer overallLicensedUsers = 1; // how many max users can be allowed in trail period
		if (props != null) {
			activeUsers = props.size();			
		}
		if (orgStaff.get("value") != null) {
			overallLicensedUsers = (Integer) orgStaff.get("value");
		}
		return ( overallLicensedUsers - activeUsers );
		
	}*/
	
	@Override
	public long startUserSession(long uid, String email, String token, String ipAddress, String userAgent) throws Exception {

		List<FacilioField> fields = AccountConstants.getUserSessionFields();

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getUserSessionModule().getTableName())
				.fields(fields);

		Map<String, Object> props = new HashMap<>();
		props.put("uid", uid);
		props.put("token", token);
		props.put("startTime", System.currentTimeMillis());
		props.put("isActive", true);
		props.put("ipAddress", ipAddress);
		props.put("userAgent", userAgent);

		insertBuilder.addRecord(props);
		insertBuilder.save();
		long sessionId = (Long) props.get("id");
		return sessionId;
	}

	@Override
	public boolean endUserSession(long uid, String email, String token) throws Exception {

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
			return true;
		}
		return false;
	}
	@Override
	public List<Map<String, Object>> getUserSessions(long uid, Boolean isActive) throws Exception
	{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getUserSessionFields())
				.table("Users")
				.innerJoin("UserSessions")
				.on("Users.USERID = UserSessions.USERID")
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
	public boolean verifyUserSession(String email, String token) throws Exception {
		
		List sessions = (List) LRUCache.getUserSessionCache().get(email);
		if (sessions == null) {
			sessions = new ArrayList<>();
		}
		if (sessions.contains(token)) {
			return true;
		}
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getUserSessionFields())
				.table("Users")
				.innerJoin("faciliousers")
				.on("Users.USERID = faciliousers.USERID")
				.innerJoin("UserSessions")
				.on("Users.USERID = UserSessions.USERID")
				.andCustomWhere("(faciliousers.email = ? or faciliousers.username = ? ) AND UserSessions.TOKEN = ? AND UserSessions.IS_ACTIVE = ?",email, email, token, true);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			sessions.add(token);
			LRUCache.getUserSessionCache().put(email, sessions);
			return true;
		}
		return false;
	}

	@Override
	public void clearUserSession(long uid, String email, String token) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getUserSessionModule().getTableName())
				.andCustomWhere("USERID = ? AND TOKEN = ?", uid, token);

		builder.delete();
		
		LRUCache.getUserSessionCache().remove(email);
	}

	@Override
	public void clearAllUserSessions(long uid, String email) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getUserSessionModule().getTableName())
				.andCustomWhere("USERID = ?", uid);

		builder.delete();
		
		LRUCache.getUserSessionCache().remove(email);
	}
	
	static User createUserFromProps(Map<String, Object> prop) throws Exception {
		User user = FieldUtil.getAsBeanFromMap(prop, User.class);
		if (user.getPhotoId() > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStoreFromOrg(user.getOrgId(), user.getOuid());
			user.setAvatarUrl(fs.getPrivateUrl(user.getPhotoId()));
		}
		if (user.getRoleId() > 0) {
			user.setRole(AccountUtil.getRoleBean().getRole(user.getRoleId()));
		}
		return user;
	}


}
