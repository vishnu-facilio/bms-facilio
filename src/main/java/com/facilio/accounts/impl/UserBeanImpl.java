package com.facilio.accounts.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.accounts.dto.*;
import com.facilio.util.FacilioUtil;
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
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.ShiftAPI;
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
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

;

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
		if (appUpdatedRows > 0) {
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
				if(ApplicationApi.checkIfUserAlreadyPresentInApp(user.getUid(), user.getApplicationId(), orgId) <= 0) {
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
			}
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
				return createUserFromProps(mapList.get(0), false, false, false);
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
		if (appDomainObj != null && appDomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
			String inviteLink = getUserLink(user, "/invitation/", appDomainObj);
			if (registration) {
				inviteLink = getUserLink(user, "/emailregistration/", appDomainObj);
			}
			placeholders.put("invitelink", inviteLink);
			placeholders.put("appType", ApplicationApi.getApplicationName(user.getApplicationId()));
			
			if(isInvitation) {
				AccountEmailTemplate.PORTAL_SIGNUP.send(placeholders, true);
			}
			else {
				AccountEmailTemplate.ADDED_TO_APP_EMAIL.send(placeholders, true);
			}
		} else {
			String inviteLink = getUserLink(user, "/invitation/", appDomainObj);
			placeholders.put("appType", ApplicationApi.getApplicationName(user.getApplicationId()));
			
			placeholders.put("invitelink", inviteLink);
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
	
			// add user in shift relation table
			ShiftContext defaultShift = ShiftAPI.getDefaultShift();
			if (defaultShift != null) {
				ShiftAPI.insertShiftUserRel(defaultShift.getId(), user.getOuid());
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
				User user = createUserFromProps(props.get(0), true, true, false); // Giving as false because user cannot
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
				User user = createUserFromProps(props.get(0), true, true, false);
				return user;
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

	@Override
	public User getUser(long appId, long orgId, long userId) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
			
		GenericSelectRecordBuilder selectRecordBuilder = fetchUserSelectBuilder(appId, criteria, orgId, null);
		List<Map<String , Object>> mapList = selectRecordBuilder.get();
		if(CollectionUtils.isNotEmpty(mapList)) {
			IAMUserUtil.setIAMUserPropsv3(mapList, orgId, false);
			User user = createUserFromProps(mapList.get(0), true, true, false);
			if(user.isActive()) {
				return user;
			}
		}
		return null;
	}

	@Override
	public User getUserInternal(long ouid, boolean withRole) throws Exception {

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
				User user = createUserFromProps(props.get(0), withRole, true, false);
				return user;
			}
		}
		return null;
	}


	@Override
	public User getUserFromPhone(String phone, String identifier, long orgId) throws Exception {

		Map<String, Object> props = IAMUserUtil.getUserFromPhone(phone, identifier, orgId);
		if (props != null && !props.isEmpty()) {
			return getAppUser(-1, props, true, true, false);
		}
		return null;
	}
	
	@Override
	public User getUserFromEmail(String email, String identifier, long orgId) throws Exception {

		Map<String, Object> props = IAMUserUtil.getUserForEmail(email, identifier , orgId);
		if (props != null && !props.isEmpty()) {
			return getAppUser(-1, props, true, true, false);
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

	@Override
	public List<User> getUsers(Criteria criteria, boolean fetchOnlyActiveUsers, boolean fetchDeleted, Collection<Long>... ouids) throws Exception {

		List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId(), ouids);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), fetchDeleted);
			for (Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, true, true, false);
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

	@Override
	public Map<Long, List<User>> getUsersWithRoleAsMap(Collection<Long> roleIds) throws Exception {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getConditionFromList("ROLE_ID", "roleId", roleIds, PickListOperators.IS));
		List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId());
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
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
		List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId());
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
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
			List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId());
			if (props != null && !props.isEmpty()) {
				List<User> users = new ArrayList<>();
				IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
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
		List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId(), ouids);
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), true);
			Map<Long, User> users = new HashMap<>();
			for (Map<String, Object> prop : props) {
				User user = createUserFromProps(prop, true, true, false);
				users.put(user.getId(), user);
			}
			return users;
		}
		return null;
	}

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
			if(ex.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS_IN_ORG_PORTAL) {
				throw ex;
			}
			if(ex.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS_IN_APP) {
				throw ex;
			}
			if(ex.getErrorCode() == ErrorCode.INVALID_APP_DOMAIN) {
				throw ex;
			}
			IAMUserUtil.rollbackUserAdded(user.getUid(), AccountUtil.getCurrentOrg().getOrgId());
			throw ex;
		}
		catch(Exception e) {
			IAMUserUtil.rollbackUserAdded(user.getUid(), AccountUtil.getCurrentOrg().getOrgId());
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

	public static User createUserFromProps(Map<String, Object> prop, boolean fetchRole, boolean fetchSpace,
			boolean isPortalRequest) throws Exception {
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
			AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication((long)prop.get("applicationId"));
			user.setAppDomain(appDomainObj);
			user.setApplicationId((long)prop.get("applicationId"));
			user.setAppType(appDomainObj.getAppType());
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
		AppDomain appDomainObj = IAMUserUtil.getAppDomain(appDomain);
		String inviteLink = getUserLink(user, "/fconfirm_reset_password/", appDomainObj);
		Map<String, Object> placeholders = new HashMap<>();
		//CommonCommandUtil.appendModuleNameInKey(null, "toUser", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("toUser", user);
		placeholders.put("invitelink", inviteLink);
		addBrandPlaceHolders("supportemail", placeholders);
		addBrandPlaceHolders("brandUrl", placeholders);
		addBrandPlaceHolders("brandLogo", placeholders);
		addBrandPlaceHolders("brandName", placeholders);
		
		AccountEmailTemplate.RESET_PASSWORD.send(placeholders, true);
		return true;
	}
	
	
	@Override
	public boolean acceptUser(User user) throws Exception {
		// TODO Auto-generated method stub
		    User appUser = getUser(-1, user.getOrgId(), user.getUid());
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
						// LicenseApi.updateUsedLicense(user.getLicenseEnum());
						return true;
					}
				}
		    }
		return false;
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
	      User user = getUser(appId, iamAccount.getUser().getOrgId(), iamAccount.getUser().getUid());
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
		String hostname = "";
		if (appDomainObj != null && StringUtils.isNotEmpty(appDomainObj.getDomain())) {
			hostname = "https://" + appDomainObj.getDomain();
			if (appDomainObj.getAppDomainTypeEnum() != AppDomainType.FACILIO) {
				hostname = hostname + "/service";
			} else {
				hostname = hostname + "/app";
			}
		}
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
			fields.add(AccountConstants.getApplicationIdField());
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
				User user = createUserFromProps(mapList.get(0), true, true, false);
				return user;
			}
		}

		return null;
	}

	@Override
	public boolean setDefaultOrg(long orgId, long userId) throws Exception {
		return IAMUserUtil.setDefaultOrg(userId, orgId);
	}

	private User getAppUser(long appId, Map<String, Object> props, boolean fetchRole, boolean fetchSpace, boolean isPortalUser) throws Exception {
		Criteria criteria = new Criteria();
		
		criteria.addAndCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf((long)props.get("uid")), NumberOperators.EQUALS));

		GenericSelectRecordBuilder selectRecordBuilder = fetchUserSelectBuilder(appId, criteria, (long)props.get("orgId"), null);
		List<Map<String , Object>> mapList = selectRecordBuilder.get();
		if(CollectionUtils.isNotEmpty(mapList)) {
			mapList.get(0).putAll(props);
			User user = createUserFromProps(mapList.get(0), fetchRole, fetchSpace, isPortalUser);
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

	
}
