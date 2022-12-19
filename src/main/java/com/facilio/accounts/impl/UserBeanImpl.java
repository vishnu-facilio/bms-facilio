package com.facilio.accounts.impl;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.accounts.dto.*;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.iam.accounts.util.*;
import com.facilio.modules.*;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.json.simple.JSONObject;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.accounts.util.AccountEmailTemplate;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.modules.fields.FacilioField;

import static com.facilio.constants.FacilioConstants.INVITATION_EXPIRY_DAYS;

@Log4j
public class UserBeanImpl implements UserBean {

	private static Logger log = LogManager.getLogger(UserBeanImpl.class.getName());

	@Override
	public boolean updateUser(User user) throws Exception {

		if (user.getRoleId() > 0) {
			Role role = AccountUtil.getRoleBean().getRole(user.getRoleId());
			FacilioUtil.throwIllegalArgumentException(role == null || role.getName().equals(AccountConstants.DefaultRole.SUPER_ADMIN), "Invalid role specified for user");
		}

		//Preventing the following props from updating
		user.setEmail(null);
		user.setUserName(null);
		user.setPassword(null);

		if(IAMUserUtil.updateUser(user, AccountUtil.getCurrentOrg().getOrgId())) {
			return updateUserEntry(user);
		}
		return false;
		
	}

	private boolean updateUserEntry(User user) throws Exception {
		GenericUpdateRecordBuilder updateOrgUserBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAppOrgUserModule().getTableName())
				.fields(AccountConstants.getAppOrgUserFields())
				.andCustomWhere("ORG_USERID = ?", user.getOuid());

		if(user.getAccessibleSpace() != null) {
			deleteAccessibleSpace(user.getOuid());
			addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());
		}
		if(user.getGroups() != null) {
			deleteAccessibleGroups(user.getOuid());
			addAccessibleTeam(user.getOuid(), user.getGroups());
		}
		Map<String, Object> orgUserprops = FieldUtil.getAsProperties(user);
		
		int appUpdatedRows = updateOrgUserBuilder.update(orgUserprops);
		int appOrgUserRows = 1;
		if(user.getApplicationId() > 0) {
			GenericUpdateRecordBuilder updateAppUserBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getOrgUserAppsModule().getTableName())
					.fields(Collections.singletonList(AccountConstants.getRoleIdField()))
					.andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(user.getApplicationId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(user.getOuid()), NumberOperators.EQUALS));
			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put("roleId", user.getRoleId());
			appOrgUserRows = updateAppUserBuilder.update(updateMap);
		}
		if (appUpdatedRows > 0 && appOrgUserRows > 0) {
			return true;
		}
		return false;
	}

	private void deleteAccessibleSpace(long ouId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getAccessibleSpaceModule().getTableName())
				;	
		builder.andCondition(CriteriaAPI.getCondition("ORG_USER_ID", "orgUserId", String.valueOf(ouId), NumberOperators.EQUALS));
		
		builder.delete();
	}
	
	private void deleteAccessibleGroups(long ouId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getGroupMemberModule().getTableName())
				;
		builder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouId), NumberOperators.EQUALS));
		builder.delete();
	}
	
	
	
	
	@Override
	public void createUser(long orgId, User user, String identifier, boolean isEmailVerificationNeeded, boolean isSelfSignup) throws Exception {
		try {

			if (user.getRoleId() > 0) {
				Role role = AccountUtil.getRoleBean().getRole(user.getRoleId());
				FacilioUtil.throwIllegalArgumentException(role == null || role.getName().equals(AccountConstants.DefaultRole.SUPER_ADMIN), "Invalid role specified for user");
			}

			user.setUserStatus(true);
		
			if(IAMUserUtil.addUser(user, orgId, identifier) > 0) {
				if(ApplicationApi.checkIfUserAlreadyPresentInApp(user.getUid(), user.getApplicationId(), orgId) == null) {
					createUserEntry(orgId, user, isSelfSignup, isEmailVerificationNeeded);
				}
				else {
					throw new AccountException(ErrorCode.USER_ALREADY_EXISTS_IN_APP, "User already exists in app");
				}
			}
		}
		catch(Exception e) {
			if(e instanceof AccountException && ((AccountException) e).getErrorCode() == ErrorCode.USER_ALREADY_EXISTS_IN_APP) {
				throw e;
			} else if (e instanceof InvocationTargetException && ((InvocationTargetException) e).getTargetException() instanceof AccountException) {
				throw (AccountException) ((InvocationTargetException) e).getTargetException();
			} else if (e instanceof InvocationTargetException && ((InvocationTargetException) e).getTargetException() instanceof IAMUserException) {
				throw (IAMUserException) ((InvocationTargetException) e).getTargetException();
			}
			log.error("Exception occurred while creating user ", e);
			IAMUserUtil.rollbackUserAdded(user.getUid(), AccountUtil.getCurrentOrg().getOrgId());
			throw e;
		}
		
	}
	
	@Override
	public void createUserEntry(long orgId, User user, boolean isSignUp, boolean isEmailVerificationNeeded) throws Exception {

		if (isSignUp && !user.isUserVerified()) {
			sendEmailRegistration(user);
		}
		user.setOrgId(orgId);
		user.setUserStatus(true);
		addToORGUsers(user);
		addToORGUsersApps(user, isEmailVerificationNeeded);

	}
	
	private User checkIfExistsInOrgUsers(long uId, long orgId) throws Exception {
		List<FacilioField> orgUserFields = AccountConstants.getAppOrgUserFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(orgUserFields)
				.table("ORG_Users");
		selectBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		
		
		List<Map<String , Object>> mapList = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(mapList)) {
			IAMUserUtil.setIAMUserPropsv3(mapList, orgId, false);
			if(CollectionUtils.isNotEmpty(mapList)) {
				return createUserFromProps(mapList.get(0), false, false, null);
			}
		}
		return null;

	}
	
	private void addToORGUsers(User user) throws Exception {
		
		User orgUser = checkIfExistsInOrgUsers(user.getUid(), user.getOrgId());
		if(orgUser == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	
			InsertRecordBuilder<ResourceContext> insertRecordBuilder = new InsertRecordBuilder<ResourceContext>()
					.moduleName(FacilioConstants.ContextNames.RESOURCE)
					.fields(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE));
			ResourceContext resource = new ResourceContext();
			resource.setName(user.getEmail());
			resource.setResourceType(ResourceType.USER);
	
			long id = insertRecordBuilder.insert(resource);
			user.setId(id);
	
			List<FacilioField> fields = new ArrayList<FacilioField>();
			fields.addAll(AccountConstants.getAppOrgUserFields());
			fields.add(AccountConstants.getOrgIdField(AccountConstants.getAppOrgUserModule()));
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(AccountConstants.getAppOrgUserModule().getTableName()).fields(fields);
	
			Map<String, Object> props = FieldUtil.getAsProperties(user);
			insertBuilder.addRecord(props);
			insertBuilder.save();
	
			user.setOuid((long) props.get("ouid"));
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
			context.put(FacilioConstants.ContextNames.MODULE_LIST,
					modBean.getSubModules(FacilioConstants.ContextNames.USERS, FacilioModule.ModuleType.READING));
	
			FacilioChain addRDMChain = FacilioChainFactory.addResourceRDMChain();
			addRDMChain.execute(context);
			addUserDetail(user);
		}
		else {
			user.setOuid(orgUser.getOuid());
			user.setInviteAcceptStatus(orgUser.isInviteAcceptStatus());
		}
	}
	
	@Override
	public long addToORGUsersApps(User user, boolean isEmailVerificationNeeded) throws Exception {
		
		List<FacilioField> fields = AccountConstants.getOrgUserAppsFields();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getOrgUserAppsModule().getTableName()).fields(fields);

		Map<String, Object> props = new HashMap<String, Object>();
		props.put("ouid", user.getOuid());
		props.put("applicationId", user.getApplicationId());
		props.put("orgId", user.getOrgId());
		props.put("roleId", user.getRoleId());
		ScopingContext defaultScoping = ApplicationApi.getDefaultScopingForApp(user.getApplicationId());
		if(user.getScoping() != null){
			props.put("scopingId", user.getScoping().getId());
		}
		else if(defaultScoping != null) {
			props.put("scopingId", defaultScoping.getId());
		}

		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		if(props.get("id") != null) {
			if(isEmailVerificationNeeded) {
				if(!user.isInviteAcceptStatus()) {
					sendInvitation(user, false, true);
				}
				else {
					sendInvitation(user, false, false);
				}
			}
			return (long)props.get("id");
		}
		return -1;
	
	}
	
	@Override
	public int deleteUserFromApps(User user, long applicationId) throws Exception {
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getOrgUserAppsModule().getTableName());
		
		deleteBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouId", String.valueOf(user.getOuid()), NumberOperators.EQUALS));
		if(applicationId > 0) {
			deleteBuilder.andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(applicationId), NumberOperators.EQUALS));
		}

		return deleteBuilder.delete();
	}
	
	@Override
	public int deletePeopleForUser(User user) throws Exception {
		return PeopleAPI.deletePeopleForUser(user.getOuid());
	}
	
	private void sendInvitation(User user, boolean registration, boolean isInvitation) throws Exception {
		Map<String, Object> placeholders = new HashMap<>();
		placeholders.put("toUser",user);
		placeholders.put("org",AccountUtil.getCurrentOrg());
		placeholders.put("inviter",AccountUtil.getCurrentUser());
		
		addBrandPlaceHolders("brandUrl", placeholders);
		AppDomain appDomainObj = user.getAppDomain();
		if (appDomainObj != null && (appDomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO
			&& appDomainObj.getAppDomainTypeEnum() != AppDomainType.DEVELOPER)) {
			String inviteLink = getUserLink(user, "/invitation/", appDomainObj);
			if (registration) {
				inviteLink = getUserLink(user, "/emailregistration/", appDomainObj);
			}
			placeholders.put("invitelink", inviteLink);
			placeholders.put("appType", ApplicationApi.getApplicationName(user.getApplicationId()));
			placeholders.put("appLink",AccountUtil.getAppLink(appDomainObj));
			
			if(isInvitation) {
				AccountEmailTemplate.PORTAL_SIGNUP.send(placeholders, true);
			}
			else {
				AccountEmailTemplate.ADDED_TO_APP_EMAIL.send(placeholders, true);
			}
		} else {
			if (appDomainObj != null && appDomainObj.getAppDomainTypeEnum() == AppDomainType.DEVELOPER) {
				List<AppDomain> appDomains = IAMAppUtil.getAppDomain(AppDomainType.FACILIO, AccountUtil.getCurrentOrg().getOrgId());
				appDomainObj = appDomains.get(0);
			}

			String inviteLink = getUserLink(user, "/invitation/", appDomainObj);
			placeholders.put("appType", ApplicationApi.getApplicationName(user.getApplicationId()));
			
			placeholders.put("invitelink", inviteLink);
			placeholders.put("expiryDate", getExpiryDate());
			addBrandPlaceHolders("brandName", placeholders);
			addBrandPlaceHolders("supportemail", placeholders);
			if(isInvitation) {
				AccountEmailTemplate.INVITE_USER.send(placeholders, true);
			}
			else {
				AccountEmailTemplate.ADDED_TO_APP_EMAIL.send(placeholders, true);
			}
		}
		
	}


	public void addUserDetail(User user) throws Exception {
		if (CollectionUtils.isNotEmpty(user.getAccessibleSpace())) {
			addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());
		}
		if(user.getAppDomain() != null && user.getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) {
			if (user.getGroups() != null) {
				addAccessibleTeam(user.getOuid(), user.getGroups());
			}
			if (user.getRoleId() == 0) {
				throw new AccountException(AccountException.ErrorCode.ROLE_ID_IS_NULL, "RoleID is Null " + user.getEmail());
			}
		}
	}

	@Override
	public User verifyEmail(String token) throws Exception {
		IAMUser iamUser = IAMUserUtil.verifyEmail(token);

		if (iamUser != null) {
			User user = new User(iamUser);
			try {
				user.setUserVerified(true);
				updateUserEntry(user);
			} catch (Exception e) {
				log.info("Exception occurred ", e);
			}
			return user;
		}
		return null;
	}

	@Override
	public User validateUserInvite(String token) throws Exception {
		IAMUser iamUser = IAMUserUtil.validateUserInviteToken(token);
		if(iamUser != null && iamUser.isActive()) {
			return new User(iamUser);
		}
		return null;
		
	}

	@Override
	public boolean resendInvite(User appUser) throws Exception {

		FacilioField invitedTime = new FacilioField();
		invitedTime.setName("invitedTime");
		invitedTime.setDataType(FieldType.NUMBER);
		invitedTime.setColumnName("INVITEDTIME");
		invitedTime.setModule(AccountConstants.getAppOrgUserModule());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(invitedTime);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getAppOrgUserModule().getTableName())
				.fields(fields);
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(appUser.getUid()), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(appUser.getOrgId()), NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>();
		props.put("invitedTime", System.currentTimeMillis());
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			sendInvitation(appUser, false, true);
			return true;
		}
		return false;
	}

	@Override
	public boolean acceptInvite(String token, String password) throws Exception {
		IAMUser iamUser = IAMUserUtil.acceptInvite(token, password);
		if(iamUser != null) {
		   return AccountUtil.getTransactionalUserBean(iamUser.getOrgId()).acceptUser(new User(iamUser));	
		}
		return false;
	}

	
	
	private User getInvitedUser(long ouid) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("ORG_Users");
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "org_UserId", String.valueOf(ouid), NumberOperators.EQUALS));
		selectBuilder.andCriteria(criteria);
	
	
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
			if(CollectionUtils.isNotEmpty(props)) {
				User user = createUserFromProps(props.get(0), false, false, null); // Giving as false because user cannot
																					// accept invite via portal APIs
				return user;
			}
		}
		return null;
	}

	@Override
	public void addUserMobileSetting(UserMobileSetting userMobileSetting) throws Exception {
		userMobileSetting.setFromPortal(AccountUtil.getCurrentAccount().getUser().isPortalUser());
		IAMUserUtil.addUserMobileSettings(userMobileSetting);
	}

	
	@Override
	public void removeUserMobileSetting(String mobileInstanceId) throws Exception {
		boolean isPortal = AccountUtil.getCurrentAccount().getUser().isPortalUser();
		IAMUserUtil.removeUserMobileSettings(mobileInstanceId, isPortal);

	}

	@Override
	public boolean deleteUser(long ouId, boolean shouldDeletePeople) throws Exception {
		User appUser = getUser(ouId, false);
		if(appUser != null) {
			if(IAMUserUtil.deleteUser(appUser.getUid(), appUser.getOrgId())) {
				deleteUserFromApps(appUser, -1);
				if(shouldDeletePeople) {
					deletePeopleForUser(appUser);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public List<User> getUsers(List<Long> ouids, boolean fetchDeleted) throws Exception {
		List<FacilioField> fields = new ArrayList<>(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("ORG_Users");
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", StringUtils.join(ouids,  ","), NumberOperators.EQUALS));

		selectBuilder.andCriteria(criteria);
		List<Map<String, Object>> props = selectBuilder.get();
		List<User> users = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), fetchDeleted);
			if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(props)) {
				for (Map<String, Object> prop: props) {

					User user = createUserFromProps(prop, false, false, null);
					users.add(user);
				}
				return users;
			}
		}
		return Collections.EMPTY_LIST;
	}
	
	@Override
	public User getUser(long ouid, boolean fetchDeleted) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("ORG_Users")
				;
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid), NumberOperators.EQUALS));
		
		selectBuilder.andCriteria(criteria);
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), fetchDeleted);
			if(CollectionUtils.isNotEmpty(props)) {
				User user = createUserFromProps(props.get(0), false, false, null);
				return user;
			}
		}
		return null;
	}

	@Override
	public User getUser(long appId, long ouId) throws Exception {

		GenericSelectRecordBuilder selectRecordBuilder = fetchUserSelectBuilder(appId, null, AccountUtil.getCurrentOrg().getOrgId(), Collections.singletonList(ouId));

		List<Map<String, Object>> props = selectRecordBuilder.get();
		List<User> users=populateProps(props);

		return CollectionUtils.isNotEmpty(users)?users.get(0):null;
	}
	@Override
	public List<User> getUsers(long appId, Set<Long> ouids) throws Exception {

		GenericSelectRecordBuilder selectRecordBuilder = fetchUserSelectBuilder(appId, null, AccountUtil.getCurrentOrg().getOrgId(),ouids);

		List<Map<String, Object>> props = selectRecordBuilder.get();
		List<User> users=populateProps(props);

		return CollectionUtils.isNotEmpty(users)?users:null;

	}
	private List<User> populateProps(List<Map<String, Object>> props) throws Exception{
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
			if(CollectionUtils.isNotEmpty(props)) {
				List<User> users=new ArrayList<>();
				for(Map<String,Object> prop:props){
					User user = createUserFromProps(prop, true, false, null);
					users.add(user);
				}
				return users;
			}
		}
		return null;
	}
	
	@Override
	public boolean disableUser(long orgId, long userId) throws Exception {
		return IAMUserUtil.disableUser(userId, orgId);
	}

	@Override
	public boolean enableUser(long orgId, long userId) throws Exception {
		return IAMUserUtil.enableUser(userId, orgId);
	}

	//verified
	@Override
	public User getUser(long appId, long orgId, long userId, String appDomain) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
			
		GenericSelectRecordBuilder selectRecordBuilder = fetchUserSelectBuilder(appId, criteria, orgId, null);
		AppDomain appDomainObj = null;
		if(appId > 0){
			if(StringUtils.isEmpty(appDomain)){
				appDomain = AccountUtil.getDefaultAppDomain();
			}
			appDomainObj = IAMAppUtil.getAppDomain(appDomain);
			selectRecordBuilder.innerJoin("Application").on("Application.ID = ORG_User_Apps.APPLICATION_ID");
			selectRecordBuilder.andCondition(CriteriaAPI.getCondition("DOMAIN_TYPE", "domainType", String.valueOf(appDomainObj.getAppDomainType()), NumberOperators.EQUALS));
		}
		List<Map<String , Object>> mapList = selectRecordBuilder.get();
		if(CollectionUtils.isNotEmpty(mapList)) {
			IAMUserUtil.setIAMUserPropsv3(mapList, orgId, false);
			User user = createUserFromProps(mapList.get(0), true, true, appDomainObj);
			if(user.isActive()) {
				return user;
			}
		}
		LOGGER.error("get user is returning null");
		return null;
	}

	@Override
	public User getUserInternal(long ouid) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());

		FacilioField orgId = new FacilioField();
		orgId.setName("orgId");
		orgId.setDataType(FieldType.NUMBER);
		orgId.setColumnName("ORGID");
		orgId.setModule(AccountConstants.getAppOrgUserModule());
		fields.add(orgId);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("ORG_Users");
				
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid), NumberOperators.EQUALS));
		
		selectBuilder.andCriteria(criteria);
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, (long)props.get(0).get("orgId"), false);
			if(CollectionUtils.isNotEmpty(props)) {
				User user = createUserFromProps(props.get(0), false, true, null);
				return user;
			}
		}
		return null;
	}


	@Override
	public User getUserFromPhone(String phone, String identifier, long orgId) throws Exception {

		Map<String, Object> props = IAMUserUtil.getUserFromPhone(phone, identifier, orgId);
		if (props != null && !props.isEmpty()) {
			return getAppUser(-1, props, false, true, false);
		}
		return null;
	}
	
	@Override
	public User getUserFromEmail(String email, String identifier, long orgId) throws Exception {

		Map<String, Object> props = IAMUserUtil.getUserForEmail(email, identifier , orgId);
		if (props != null && !props.isEmpty()) {
			return getAppUser(-1, props, false, true, false);
		}
		return null;
	}

	@Override
	public User getUserFromEmail(String email, String identifier, long orgId, boolean fetchInactive) throws Exception {

		Map<String, Object> props = IAMUserUtil.getUserForEmail(email, identifier , orgId, fetchInactive);
		if (props != null && !props.isEmpty()) {
			return getAppUser(-1, props, false, true, false);
		}
		return null;
	}

	@Override
	public User getAppUserForUserName(String username, long appId, long orgId) throws Exception {

		AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);
		Map<String, Object> props = IAMUserUtil.getUserForUsername(username, orgId, appDomain.getIdentifier());
		if (props != null && !props.isEmpty()) {
			return getAppUser(appId, props, true, true, false);
		}

		return null;
	}

	//verified
	@Override
	public List<User> getUsers(Criteria criteria, boolean fetchOnlyActiveUsers, boolean fetchDeleted, Collection<Long>... ouids) throws Exception {

		List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId(), ouids);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), fetchDeleted);
			for (Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, true, true, null);
				if(fetchOnlyActiveUsers) {
					if(user.isActive()) {
						users.add(user);
					}
					continue;
				}
				users.add(user);
			}
			return users;
		}
		return null;
	}
	public Long getOrgUsersCountForRole(Collection<Long> roleIds) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getCountField())
				.table(AccountConstants.getOrgUserAppsModule().getTableName());
		selectBuilder.andCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", StringUtils.join(roleIds, ","), NumberOperators.EQUALS));
		List<Map<String, Object>> result = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(result)) {
			return ((Number) result.get(0).get("count")).longValue();
		}
		return null;
	}
	private List<Long> getOrgUsersWithRole(Collection<Long> roleIds) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgUserAppsFields())
				.table(AccountConstants.getOrgUserAppsModule().getTableName())
				;
		selectBuilder.andCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", StringUtils.join(roleIds, ","), NumberOperators.EQUALS));
		List<Map<String, Object>> mapList = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(mapList)) {
			List<Long> orgUserIds = new ArrayList<>();
			for(Map<String, Object> map : mapList) {
				orgUserIds.add((Long)map.get("ouid"));
			}
			return orgUserIds;
		}
		return null;

	}

	@Override
	public Map<Long, List<User>> getUsersWithRoleAsMap(Collection<Long> roleIds) throws Exception {

		List<Long> orgUserIDs = getOrgUsersWithRole(roleIds);
		if(CollectionUtils.isNotEmpty(orgUserIDs)) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "ouid", StringUtils.join(orgUserIDs,","), NumberOperators.EQUALS));
			List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId());
			if (props != null && !props.isEmpty()) {
				IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
				Map<Long, List<User>> userMap = new HashMap<>();
				for (Map<String, Object> prop : props) {
					User user = createUserFromProps(prop, false, false, null);

					List<User> users = userMap.get(user.getRoleId());
					if (users == null) {
						users = new ArrayList<>();
						userMap.put(user.getRoleId(), users);
					}
					users.add(user);
				}
				return userMap;
			}
		}
		return null;
	}

	@Override
	public List<User> getUsersWithRole(long roleId) throws Exception {

		List<Long> orgUserIDs = getOrgUsersWithRole(Collections.singleton(roleId));
		if(CollectionUtils.isNotEmpty(orgUserIDs)) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "ouid", StringUtils.join(orgUserIDs,","), NumberOperators.EQUALS));
			List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId());
			if (props != null && !props.isEmpty()) {
				IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
				List<User> users = new ArrayList<>();
				for (Map<String, Object> prop : props) {
					User user = createUserFromProps(prop, false, false, null);
					users.add(user);
				}
				return users;
			}
		}
		return null;
	}

	@Override
	public List<User> getUsersWithRoleAndAccessibleSpace(long roleId, long spaceId) throws Exception {

		BaseSpaceContext space = SpaceAPI.getBaseSpace(spaceId);
		if (space != null) {
			Set<Long> parentIds = getSpaceParentIds(space);
			List<Long> orgUserIDs = getOrgUsersWithRole(Collections.singleton(roleId));
			if(CollectionUtils.isNotEmpty(orgUserIDs)) {
				Criteria criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getCondition("ORG_USERID", "ouid", StringUtils.join(orgUserIDs, ","), NumberOperators.EQUALS));
				List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId());
				if (props != null && !props.isEmpty()) {
					List<User> users = new ArrayList<>();
					IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
					for (Map<String, Object> prop : props) {
						User user = createUserFromProps(prop, false, true, null);
						if (user.getAccessibleSpace() == null || user.getAccessibleSpace().isEmpty()
								|| !Collections.disjoint(parentIds, user.getAccessibleSpace())) {
							users.add(user);
						}
					}
					return users;
				}
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
		if (space.getSpaceId5() != -1) {
			parentIds.add(space.getSpaceId5());
		}
		parentIds.add(space.getId());
		return parentIds;
	}

	//verified
	@Override
	public Map<Long, User> getUsersAsMap(Criteria criteria, Collection<Long>... ouids) throws Exception {
		List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId(), ouids);
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), true);
			Map<Long, User> users = new HashMap<>();
			for (Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, true, true, null);
				users.put(user.getId(), user);
			}
			return users;
		}
		return null;
	}

	//verified
	private List<Map<String, Object>> fetchORGUserProps(Criteria criteria, long orgId, Collection<Long>... ouids) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = fetchUserSelectBuilder(-1, criteria, orgId, ouids);
		return selectBuilder.get();
	}

	@Override
	public List<Organization> getOrgs(long uId) throws Exception {
		return IAMUserUtil.getOrgsForUser(uId);
	}

	@Override
	public Organization getDefaultOrg(long uid) throws Exception {
		return IAMUserUtil.getDefaultOrg(uid);
	}

	@Override
	public long inviteRequester(long orgId, User user, boolean isEmailVerificationNeeded, boolean shouldThrowExistingUserError, String identifier, boolean addPeople, boolean isSelfSignup) throws Exception {
		try {
			if (AccountUtil.getCurrentOrg() != null) {
				Organization org = AccountUtil.getOrgBean().getOrg(AccountUtil.getCurrentOrg().getDomain());
				user.setOrgId(org.getOrgId());
				user.setIdentifier(identifier);
				user.setUserType(AccountConstants.UserType.REQUESTER.getValue());
				user.setUserStatus(true);
				
				createUser(orgId, user, user.getIdentifier(), isEmailVerificationNeeded, isSelfSignup);
				if(addPeople) {
					PeopleAPI.addPeopleForRequester(user);
				}
				return user.getOuid();
			}
		}
		catch(AccountException ex) {
			log.error("Exception while inviting user" + ex);
			if(ex.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS_IN_ORG_PORTAL) {
				throw ex;
			}
			if(ex.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS_IN_APP) {
				throw ex;
			}
			if(ex.getErrorCode() == ErrorCode.INVALID_APP_DOMAIN) {
				throw ex;
			}
			try{
				IAMUserUtil.rollbackUserAdded(user.getUid(), AccountUtil.getCurrentOrg().getOrgId());
			}
			catch (Exception exception){
				log.error("Exception while inviting user" + exception);
			}
			throw ex;
		}
		catch(Exception e) {
			log.error("Exception while inviting user" + e);
			try{
				IAMUserUtil.rollbackUserAdded(user.getUid(), AccountUtil.getCurrentOrg().getOrgId());
				if (e instanceof IAMUserException) {
					log.error("Exception while inviting user", e);
					throw e;
				}
			}
			catch (Exception exception){
				log.error("Exception while inviting user", exception);
				if (e instanceof IAMUserException) {
					log.error("Exception while inviting user",e);
					throw e;
				}
			}
			throw e;
		}
		return 0L;
	}


	@Override
	public String updateUserPhoto(long uid, User user) throws Exception {
		return IAMUserUtil.updateUserPhoto(uid, user);
	}

	@Override
	public boolean deleteUserPhoto(long uid, long photoId) throws Exception {
		return IAMUserUtil.deleteUserPhoto(uid, photoId);
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
		
		FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);

		for (Long group : groups) {
			Map<String, Object> props = new HashMap<>();
			props.put("ouid", uid);
			props.put("groupId", group);
//			props.put(FacilioConstants.ContextNames.PEOPLE,PeopleAPI.getPeopleForId(PeopleAPI.getPeopleIdForUser(uid)).getId());
			props.put("moduleId",module.getModuleId());
			props.put("memberRole", GroupMemberRole.MEMBER.getMemberRole());

			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
	}

	public static Map<Long, List<Long>> getAccessibleSpaceList(Collection<Long> uids) throws Exception {
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

	public static Map<Long, List<Long>> getAllUsersAccessibleSpaceList() throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getAccessbileSpaceFields())
				.table(ModuleFactory.getAccessibleSpaceModule().getTableName());

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
	public static Map<Long, List<Long>> getAllUsersAccessibleGroupList() throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupMemberFields())
				.table(AccountConstants.getGroupMemberModule().getTableName());

		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, List<Long>> grpMap = new HashMap<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				if (grpMap.get(prop.get("ouid")) == null) {
					grpMap.put((long) prop.get("ouid"), new ArrayList<>());
				}
				grpMap.get(prop.get("ouid")).add((Long) prop.get("groupId"));
			}
			return grpMap;
		}
		return Collections.emptyMap();

	}

	public static User createUserFromProps(Map<String, Object> prop, boolean fetchRole, boolean fetchSpace,
			AppDomain appDomain) throws Exception {
		User user = FieldUtil.getAsBeanFromMap(prop, User.class);
		user.setId((long)prop.get("ouid"));
		if (fetchRole) {
			if (user.getRoleId() > 0) {
				RoleBean roleBean = null;
				if (AccountUtil.getCurrentOrg() == null) {
					roleBean = AccountUtil.getRoleBean(user.getOrgId());
				} else {
					roleBean = AccountUtil.getRoleBean();
				}
				user.setRole(roleBean.getRole(user.getRoleId(), false));
			}
		}

		if(prop.get("applicationId") != null){
			if(appDomain == null){
				appDomain = ApplicationApi.getAppDomainForApplication((long) prop.get("applicationId"));
			}
			user.setAppDomain(appDomain);
			user.setApplicationId((long)prop.get("applicationId"));
			user.setAppType(appDomain.getAppType());
			if(prop.get("scopingId") != null) {
				user.setScopingId((long)prop.get("scopingId"));
			}
			if(user.getScopingId() > 0) {
				user.setScoping(ApplicationApi.getScoping(user.getScopingId()));
			}
		}
		
		
		if (fetchSpace) {
			UserBean userBean = (UserBean) BeanFactory.lookup("UserBean", user.getOrgId());
			user.setAccessibleSpace(userBean.getAccessibleSpaceList(user.getOuid()));
		}
		
		return user;
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

	@Override
	public boolean sendResetPasswordLinkv2(User user, String appDomain) throws Exception {
		AppDomain appDomainObj = IAMAppUtil.getAppDomain(appDomain);
		String inviteLink = getUserLink(user, "/confirmpassword/", appDomainObj);
		Map<String, Object> placeholders = new HashMap<>();
		//CommonCommandUtil.appendModuleNameInKey(null, "toUser", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("toUser", user);
		placeholders.put("invitelink", inviteLink);

		String formattedTime = getExpiryDate();
		placeholders.put("expiryDate", formattedTime);
		addBrandPlaceHolders("supportemail", placeholders);
		addBrandPlaceHolders("brandUrl", placeholders);
		addBrandPlaceHolders("brandLogo", placeholders);
		addBrandPlaceHolders("brandName", placeholders);
		
		AccountEmailTemplate.RESET_PASSWORD.send(placeholders, true);
		return true;
	}

	private String getExpiryDate() {
		long creationTime = System.currentTimeMillis();
		Instant creationInstant = Instant.ofEpochMilli(creationTime);
		Instant plus7Days = creationInstant.plus(INVITATION_EXPIRY_DAYS, ChronoUnit.DAYS);
		String formattedTime = DateTimeUtil.getFormattedTime(plus7Days.toEpochMilli());
		return formattedTime;
	}


	@Override
	public boolean acceptUser(User user) throws Exception {
		// TODO Auto-generated method stub
		    User appUser = getUser(-1, user.getOrgId(), user.getUid(), null);
		    if(appUser != null && appUser.isActive()) {
				FacilioField inviteAcceptStatus = new FacilioField();
				inviteAcceptStatus.setName("inviteAcceptStatus");
				inviteAcceptStatus.setDataType(FieldType.BOOLEAN);
				inviteAcceptStatus.setColumnName("INVITATION_ACCEPT_STATUS");
				inviteAcceptStatus.setModule(AccountConstants.getAppOrgUserModule());
	
				List<FacilioField> fields = new ArrayList<>();
				fields.add(inviteAcceptStatus);
			//	fields.add(userStatus);
				//fields.add(isDefaultOrg);
	
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(AccountConstants.getAppOrgUserModule().getTableName()).fields(fields)
						.andCustomWhere("ORG_USERID = ?", appUser.getOuid());
	
				Map<String, Object> props = new HashMap<>();
				props.put("inviteAcceptStatus", true);
				//props.put("isDefaultOrg", true);
			//	props.put("userStatus", true);
	
				int updatedRows = updateBuilder.update(props);
				if (updatedRows > 0) {
					appUser = getInvitedUser(appUser.getOuid());
					if (appUser != null) {
						updateUserEntry(appUser);
						updateUserStatusInPeople(user.getPeopleId());
						// LicenseApi.updateUsedLicense(user.getLicenseEnum());
						return true;
					}
				}
		    }
		return false;
	}

	public static void updateUserStatusInPeople(long peopleId) throws Exception {

		ModuleBean bean = Constants.getModBean();
		FacilioModule module = bean.getModule(FacilioConstants.ContextNames.PEOPLE);
		FacilioField userStatusField = bean.getField("user", module.getName());

		Map<String,Object> prop = new HashMap<>();
		prop.put("user",true);

		UpdateRecordBuilder<V3PeopleContext> updateRecordBuilder = new UpdateRecordBuilder<V3PeopleContext>()
				.fields(Collections.singletonList(userStatusField))
				.andCondition(CriteriaAPI.getIdCondition(peopleId,module))
				.module(module);
		updateRecordBuilder.updateViaMap(prop);

	}

	@Override
	public boolean verifyUser(long userId) throws Exception {
		return IAMUserUtil.verifyUser(userId) ;
	}

	
	@Override
	public String generatePermalinkForURL(String url, User user) throws Exception {
		String token = IAMUserUtil.generatePermalinkForUrl(url, user.getUid(), AccountUtil.getCurrentOrg().getOrgId());
		return token;
	}

	@Override
	public String generatePermalink(User user, JSONObject sessionInfo) throws Exception {
		String token = IAMUserUtil.generatePermalink(user.getUid(), AccountUtil.getCurrentOrg().getOrgId(), sessionInfo);
		return token;
	}

	@Override
	public boolean verifyPermalinkForURL(String token, List<String> urls) throws Exception {
		return IAMUserUtil.verifyPermalinkForUrl(token, urls);
		
	}

	@Override
	public Account getPermalinkAccount(long appId, IAMAccount iamAccount) throws Exception {
		// TODO Auto-generated method stub
	  if(iamAccount != null) {
	      User user = getUser(appId, iamAccount.getUser().getOrgId(), iamAccount.getUser().getUid(), null);
	      Account account = new Account(iamAccount.getOrg(), user);
	      return account;
	  }
	  return null;
	}

	@Override
	public List<Map<String, Object>> getUserSessions(long uid, Boolean isActive) throws Exception {
		// TODO Auto-generated method stub
		return IAMUserUtil.getUserSessions(uid, isActive);
	}

	private void sendEmailRegistration(User user) throws Exception {

		String inviteLink = getUserLink(user, "/emailregistration/", user.getAppDomain());
		Map<String, Object> placeholders = new HashMap<>();
		placeholders.put("toUser",user);
		//CommonCommandUtil.appendModuleNameInKey(null, "toUser", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("invitelink", inviteLink);
		if(user.getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) {
			if (user.getEmail().contains("@facilio.com") || FacilioProperties.isOnpremise()) {
				addBrandPlaceHolders("brandName", placeholders);
				addBrandPlaceHolders("brandUrl", placeholders);
				addBrandPlaceHolders("brandLogo", placeholders);
				AccountEmailTemplate.EMAIL_VERIFICATION.send(placeholders, true);
			} else {
				AccountEmailTemplate.ALERT_EMAIL_VERIFICATION.send(placeholders, true);
			}
		}
		else {
			placeholders.put("org",AccountUtil.getCurrentOrg());
			addBrandPlaceHolders("brandName", placeholders);
			addBrandPlaceHolders("brandUrl", placeholders);
			placeholders.put("appType", ApplicationApi.getApplicationName(user.getApplicationId()));
			addBrandPlaceHolders("brandLogo", placeholders);
			AccountEmailTemplate.PORTAL_SELF_SIGNUP.send(placeholders, true);
		}

	}
	
	private String getUserLink(User user, String url, AppDomain appDomainObj) throws Exception {
		String inviteToken = IAMUserUtil.getEncodedToken(user);
		if (StringUtils.isEmpty(inviteToken)) {
			throw new IllegalArgumentException("Unable to generate invite link for the user.");
		}
		String hostname = "";
		if (appDomainObj != null && StringUtils.isNotEmpty(appDomainObj.getDomain())) {
			long orgId = appDomainObj.getOrgId();  // using this when reset password link is called
			if (AccountUtil.getCurrentOrg() != null) {
				orgId = AccountUtil.getCurrentOrg().getOrgId();
			}
			List<AppDomain> appDomains = IAMOrgUtil.getCustomAppDomain(appDomainObj.getAppDomainTypeEnum(), orgId);
			for (AppDomain appDomain: appDomains) {
				if (appDomain.getDomainTypeEnum() == AppDomain.DomainType.CUSTOM) {
					appDomainObj = appDomain;
					break;
				}
			}
			hostname = FacilioProperties.getAppProtocol()+ "://" + appDomainObj.getDomain();
//				if (appDomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
//					hostname = hostname + "/service";
//				} else {
//					hostname = hostname + "/app";
//				}
			hostname = hostname + "/auth";
		}

		String logMessage = "username: " + user.getUserName();
		logMessage += " userObject: " + FieldUtil.getAsJSON(user);
		if (appDomainObj != null) {
			logMessage += " appDomain: " + appDomainObj.getDomain();
			logMessage += " domain: " + FieldUtil.getAsJSON(appDomainObj);
		}
		logMessage += " isInviteTokenEmpty: " + StringUtils.isEmpty(inviteToken);
		LOGGER.error("[inviteTokenEmptyLog] " + logMessage);

		return hostname + url + inviteToken;
	}
	
	public static GenericSelectRecordBuilder fetchUserSelectBuilder (long appId, Criteria criteria, long orgId, Collection<Long>... ouids) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		List<FacilioField> orgUserFields = AccountConstants.getAppOrgUserFields();
		fields.addAll(orgUserFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
		;
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		if(appId > 0) {
			fields.addAll(AccountConstants.getOrgUserAppsFields());

			fields.add(AccountConstants.getApplicationIdField());
			fields.add(AccountConstants.getRoleIdField());
			selectBuilder.innerJoin("ORG_User_Apps").on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID");
			selectBuilder.andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId), NumberOperators.EQUALS));
		}
		
		
		if (criteria != null && !criteria.isEmpty()) {
			selectBuilder.andCriteria(criteria);
		}

		if (ouids != null && ouids.length == 1) {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(orgUserFields);
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ouid"), ouids[0], NumberOperators.EQUALS));
		}

		return selectBuilder;
	}

	@Override
	//verified
	public User getUser(String emailOrPhone, String identifier) throws Exception {
		// TODO Auto-generated method stub
		Organization currentOrg = AccountUtil.getCurrentOrg();
		if (currentOrg == null) {
			throw new IllegalArgumentException("Organization cannot be empty");
		}
		
		Map<String, Object> props = IAMUserUtil.getUserForUsername(emailOrPhone, currentOrg.getOrgId(), identifier);
		if (props != null && !props.isEmpty()) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf((long)props.get("uid")), NumberOperators.EQUALS));
			
			GenericSelectRecordBuilder selectRecordBuilder = fetchUserSelectBuilder(-1, criteria, currentOrg.getOrgId(), null);
			List<Map<String , Object>> mapList = selectRecordBuilder.get();
			if(CollectionUtils.isNotEmpty(mapList)) {
				IAMUserUtil.setIAMUserPropsv3(mapList, currentOrg.getOrgId(), false);
				mapList.get(0).putAll(props);
				User user = createUserFromProps(mapList.get(0), false, true, null);
				return user;
			}
		}

		return null;
	}

	@Override
	public boolean setDefaultOrg(long orgId, long userId) throws Exception {
		return IAMUserUtil.setDefaultOrg(userId, orgId);
	}

	//verified
	private User getAppUser(long appId, Map<String, Object> props, boolean fetchRole, boolean fetchSpace, boolean isPortalUser) throws Exception {
		Criteria criteria = new Criteria();
		
		criteria.addAndCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf((long)props.get("uid")), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("IAM_ORG_USERID", "iamOrgUserId", String.valueOf((long)props.get("iamOrgUserId")), NumberOperators.EQUALS));

		GenericSelectRecordBuilder selectRecordBuilder = fetchUserSelectBuilder(appId, criteria, (long)props.get("orgId"), null);
		List<Map<String , Object>> mapList = selectRecordBuilder.get();
		if(CollectionUtils.isNotEmpty(mapList)) {
			mapList.get(0).putAll(props);
			User user = createUserFromProps(mapList.get(0), fetchRole, fetchSpace, null);
			return user;
		}
		return null;

	}
	
	private void addBrandPlaceHolders(String prop, Map<String, Object> placeHolder) {
		String brandVal = null;
		switch(prop) {
			case "brandUrl":
				brandVal = "www." + FacilioProperties.getConfig("rebrand.domain");
				break;
			case "brandName": 
				brandVal = FacilioProperties.getConfig("rebrand.brand");
				break;
			case "brandLogo":
				String domain = FacilioProperties.getDomain();
				boolean isFacilioDomain = true;
				if (domain.equals("facilio")) {
					brandVal = "https://facilio.com/assets/images/logo.png";
				}
				else {
					isFacilioDomain = false;
					String staticUrl = FacilioProperties.getConfig("static.url");
					brandVal = staticUrl+"/statics/logo/"+domain+".png";
				}
				placeHolder.put("facilioDomain", isFacilioDomain);
				break;
		}
		
		placeHolder.put(prop, brandVal != null ? brandVal : FacilioProperties.getConfig("rebrand." + prop));
	}

	
	public List<Map<String, String>> getUserDetailsForUserManagement(String email) throws Exception {
		List<Map<String, Object>> userData = IAMUserUtil.getUserData(email, -1, null);

		List<Long> iamOrgUserId = new ArrayList<>();
		List<Long> orgUserIds = new ArrayList<>();
		Map<Long, Map<String, Object>> orgUserIdVsUser = new HashMap<>();
		List<Long> orgIds = new ArrayList<>();
		for (Map<String, Object> user: userData) {
			iamOrgUserId.add((long) user.get("iamOrgUserId"));
			orgIds.add((long) user.get("orgId"));
			Map<String, Object> orgUser = AccountUtil.getOrgBean((long) user.get("orgId")).getOrgUser((long) user.get("iamOrgUserId"));
			orgUserIds.add((long) orgUser.get("ouid"));
			orgUserIdVsUser.put((long) orgUser.get("ouid"), user);
		}

		LOGGER.error("get UserData orgIds: " + orgIds.size() + " iamOrgUserId: "+ iamOrgUserId.size());

		List<Map<String, Object>> orgUserApps = new ArrayList<>();
		for (int i = 0; i < orgIds.size(); i++) {
			long orgId = orgIds.get(i);
			orgUserApps.addAll(AccountUtil.getOrgBean(orgId).getOrgUserApps(orgUserIds.get(i)));
		}

		LOGGER.error("get orgUserApps: " + orgUserApps.size());

		List<Long> applicationIds = new ArrayList<>();
		orgIds = new ArrayList<>();
		for (Map<String, Object> orgUserApp: orgUserApps) {
			applicationIds.add((long) orgUserApp.get("applicationId"));
			orgIds.add((long) orgUserApp.get("orgId"));
		}

		List<Map<String, Object>> apps = new ArrayList<>();
		for (int i = 0; i < orgIds.size(); i++) {
			apps.addAll(AccountUtil.getOrgBean(orgIds.get(i)).getApplication(applicationIds.get(i)));
		}

		LOGGER.error("get Application apps: " + apps.size());

		Map<Long, Map<String, Object>> appIdVsApp = new HashMap<>();
		for (Map<String, Object> app: apps) {
			appIdVsApp.put((long) app.get("id"), app);
		}

		List<Map<String, String>> result = new ArrayList<>();

		for (Map<String, Object> orgUser: orgUserApps) {
			Map<String, String> res = new HashMap<>();
			res.put("iamOrgUserId", String.valueOf(orgUser.get("ouid")));
			long applicationId = (long) orgUser.get("applicationId");
			Map<String, Object> application = appIdVsApp.get(applicationId);
			res.put("orgId", String.valueOf(orgUser.get("orgId")));
			res.put("applicationName", (String) application.get("name"));
			res.put("applicationId", String.valueOf(orgUser.get("applicationId")));
			res.put("userId", String.valueOf(orgUserIdVsUser.get(orgUser.get("ouid")).get("uid")));
			result.add(res);
		}

		return result;
	}
}
