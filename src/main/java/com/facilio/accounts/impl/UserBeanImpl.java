package com.facilio.accounts.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountEmailTemplate;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.UserUtil;
import com.facilio.aws.util.FacilioProperties;
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
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.json.simple.JSONObject;

;

public class UserBeanImpl implements UserBean {

	private static Logger log = LogManager.getLogger(UserBeanImpl.class.getName());

	private long getUid(String email) throws Exception {
		return getUser(email).getUid();
	}

	@Override
	public boolean updateUser(User user) throws Exception {
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
	public void createUser(long orgId, User user) throws Exception {
		try {
			user.setUserStatus(true);
			if(IAMUserUtil.addUser(user, orgId) > 0) {
				createUserEntry(orgId, user, false);
			}
		}
		catch(Exception e) {
			IAMUserUtil.rollbackUserAdded(user, AccountUtil.getCurrentOrg().getOrgId());
			throw e;
		}
		
	}
	
	@Override
	public void createUserEntry(long orgId, User user, boolean isSignUpEmailVerificationNeeded) throws Exception {

		if (isSignUpEmailVerificationNeeded && !user.isUserVerified()) {
			sendEmailRegistration(user);
		}
		user.setOrgId(orgId);
		user.setUserType(AccountConstants.UserType.USER.getValue());
		user.setUserStatus(true);
		//user.setAccessibleSpace(getAccessibleSpaceList(user.getOuid()));
		addToAppORGUsers(user, true);
		addUserDetail(user);
		
	}
	
	private void addToAppORGUsers(User user, boolean isEmailVerificationNeeded) throws Exception {
		
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
		if((long)props.get("ouid") > 0) {
			if(isEmailVerificationNeeded && !user.isUserVerified() && !user.isInviteAcceptStatus()) {
				sendInvitation(user, false);
			}
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		context.put(FacilioConstants.ContextNames.MODULE_LIST,
				modBean.getSubModules(FacilioConstants.ContextNames.USERS, FacilioModule.ModuleType.READING));

		FacilioChain addRDMChain = FacilioChainFactory.addResourceRDMChain();
		addRDMChain.execute(context);
	}
	
	private void sendInvitation(User user, boolean registration) throws Exception {
		Map<String, Object> placeholders = new HashMap<>();
		//CommonCommandUtil.appendModuleNameInKey(null, "toUser", FieldUtil.getAsProperties(user), placeholders);
		//CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()),
		//		placeholders);
		placeholders.put("toUser",user);
		placeholders.put("org",AccountUtil.getCurrentOrg());
		placeholders.put("inviter",AccountUtil.getCurrentUser());
		
	//	CommonCommandUtil.appendModuleNameInKey(null, "inviter",
	//			FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeholders);
		addBrandPlaceHolders("brandUrl", placeholders);

		if (user.isPortalUser()) {
			String inviteLink = getUserLink(user, "/invitation/");
			if (registration) {
				inviteLink = getUserLink(user, "/emailregistration/");
			}
			placeholders.put("invitelink", inviteLink);
			AccountEmailTemplate.PORTAL_SIGNUP.send(placeholders, true);

		} else {
			String inviteLink = getUserLink(user, "/invitation/");
			placeholders.put("invitelink", inviteLink);

			AccountEmailTemplate.INVITE_USER.send(placeholders, true);
		}

	}


	public void addUserDetail(User user) throws Exception {
		if (user.getRoleId() == 0) {
			throw new AccountException(AccountException.ErrorCode.ROLE_ID_IS_NULL, "RoleID is Null " + user.getEmail());
		}
//		Long shiftId = user.getShiftId();
//
//		if (shiftId != null) {
//			insertShiftRel(user.getOuid(), shiftId);
//		}

		if (user.getAccessibleSpace() != null) {
			addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());
		}
		if (user.getGroups() != null) {
			addAccessibleTeam(user.getOuid(), user.getGroups());
		}

		// add user in shift relation table
		ShiftContext defaultShift = ShiftAPI.getDefaultShift();
		if (defaultShift != null) {
			ShiftAPI.insertShiftUserRel(defaultShift.getId(), user.getOuid());
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
		return new User(iamUser);
		
	}

	@Override
	public boolean resendInvite(long ouid) throws Exception {

		User appUser = getUser(ouid, false);
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
			sendInvitation(appUser, false);
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
			UserUtil.setIAMUserProps(props, AccountUtil.getCurrentOrg().getOrgId(), false);
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
	public boolean deleteUser(long ouid) throws Exception {
		User user = getUser(ouid, false);
		return IAMUserUtil.deleteUser(user, AccountUtil.getCurrentOrg().getOrgId());
	}
	
	@Override
	public boolean disableUser(long ouid) throws Exception {
		User user = getUser(ouid, false);
		return IAMUserUtil.disableUser(user);
	}

	@Override
	public boolean enableUser(long ouid) throws Exception {
		User user = getUser(ouid, false);
		return IAMUserUtil.enableUser(user);
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
			UserUtil.setIAMUserProps(props, AccountUtil.getCurrentOrg().getOrgId(), fetchDeleted);
			if(CollectionUtils.isNotEmpty(props)) {
				User user = createUserFromProps(props.get(0), true, true, false);
				return user;
			}
		}
		return null;
	}
	
	@Override
	public User getUser(long orgId, long userId) throws Exception {

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField());
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("ORG_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		
		GenericSelectRecordBuilder selectBuilder = fetchUserSelectBuilder(criteria, orgId, null);
		
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			UserUtil.setIAMUserProps(props, orgId, false);
			if(CollectionUtils.isNotEmpty(props)) {
				User user = createUserFromProps(props.get(0), true, true, false);
				return user;
			}
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
			UserUtil.setIAMUserProps(props, (long)props.get(0).get("orgId"), false);
			if(CollectionUtils.isNotEmpty(props)) {
				User user = createUserFromProps(props.get(0), withRole, true, false);
				return user;
			}
		}
		return null;
	}


	@Override
	public User getUserFromPhone(String phone) throws Exception {

		Map<String, Object> props = UserUtil.getUserFromPhone(phone);
		if (props != null && !props.isEmpty()) {
			return getAppUser(props, true, true, false);
		}
		return null;
	}

	@Override
	public User getUser(String emailOrPhone, String portalDomain) throws Exception {

		if(StringUtils.isAllEmpty(portalDomain)) {
			portalDomain = "app";
		}
		
		Map<String, Object> props = UserUtil.getUserFromEmailOrPhone(emailOrPhone, portalDomain);
		if (props != null && !props.isEmpty()) {
			return getAppUser(props, true, true, false);
		}

		return null;
	}

	private User getPortalUser(long uid, long portalId) throws Exception {
		FacilioModule portalInfoModule = AccountConstants.getPortalInfoModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField(portalInfoModule));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin(portalInfoModule.getTableName())
				.on("ORG_Users.ORGID = "+portalInfoModule.getTableName()+".ORGID")
				.andCustomWhere("PortalInfo.PORTALID="+ portalId + " and ORG_Users.USERID = "+uid);
		

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			UserUtil.setIAMUserProps(props, AccountUtil.getCurrentOrg().getOrgId(), false);
			if(CollectionUtils.isNotEmpty(props)) {
				return createUserFromProps(props.get(0), true, false, true);
			}
		}

		return null;
	}

	@Override
	public User getPortalUser(long uid) throws Exception {
		PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(AccountUtil.getCurrentOrg().getId(), false);
		return getPortalUser(uid, portalInfo.getPortalId());
	}

	@Override
	public List<User> getUsers(Criteria criteria, boolean fetchOnlyActiveUsers, boolean fetchDeleted, Collection<Long>... ouids) throws Exception {

		List<Map<String, Object>> props = fetchORGUserProps(criteria, AccountUtil.getCurrentOrg().getOrgId(), ouids);
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			UserUtil.setIAMUserProps(props, AccountUtil.getCurrentOrg().getOrgId(), fetchDeleted);
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
			UserUtil.setIAMUserProps(props, AccountUtil.getCurrentOrg().getOrgId(), false);
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
			UserUtil.setIAMUserProps(props, AccountUtil.getCurrentOrg().getOrgId(), false);
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
				UserUtil.setIAMUserProps(props, AccountUtil.getCurrentOrg().getOrgId(), false);
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
			UserUtil.setIAMUserProps(props, AccountUtil.getCurrentOrg().getOrgId(), true);
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
		
		GenericSelectRecordBuilder selectBuilder = fetchUserSelectBuilder(criteria, orgId, ouids);
		return selectBuilder.get();
	}

	@Override
	public User getUser(long orgId, String email) throws Exception {

		Map<String, Object> props = UserUtil.getUserFromEmailOrPhoneForOrg(email);
		if (props != null && !props.isEmpty()) {
			return getAppUser(props, true, true, false);
		}
		return null;
	}

	@Override
	public List<Organization> getOrgs(long uId) throws Exception {
		return IAMUserUtil.getUserOrgs(uId);
	}

	@Override
	public Organization getDefaultOrg(long uid) throws Exception {
		return IAMUserUtil.getDefaultOrg(uid);
	}

	@Override
	public long inviteRequester(long orgId, User user, boolean isEmailVerificationNeeded, boolean shouldThrowExistingUserError) throws Exception {
		try {
			if (AccountUtil.getCurrentOrg() != null) {
				Organization org = AccountUtil.getOrgBean().getOrg(AccountUtil.getCurrentOrg().getDomain());
				PortalInfoContext portalInfo = AccountUtil.getOrgBean().getPortalInfo(org.getId(), false);
				org.setPortalId(portalInfo.getPortalId());
				user.setOrgId(org.getOrgId());
				user.setDomainName(org.getDomain());
				user.setPortalId(org.getPortalId());
				user.setId(addRequester(orgId, user, isEmailVerificationNeeded, shouldThrowExistingUserError));
				return user.getOuid();
			}
		}
		catch(AccountException ex) {
			if(ex.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS_IN_ORG_PORTAL) {
				throw ex;
			}
			IAMUserUtil.rollbackUserAdded(user, AccountUtil.getCurrentOrg().getOrgId());
			throw ex;
		}
		catch(Exception e) {
			IAMUserUtil.rollbackUserAdded(user, AccountUtil.getCurrentOrg().getOrgId());
			throw e;
		}
		return 0L;
	}

	private long addRequester(long orgId, User user, boolean emailVerification, boolean shouldThrowExistingUserError)
			throws Exception {
		IAMUser iamUser = IAMUserUtil.getUser(user.getEmail(), user.getDomainName(), user.getDomainName());
		if (iamUser != null) {
			User portalUser = new User(iamUser);
			log.info("Requester email " + iamUser.getEmail() +" already exists in the portal for org: " + orgId);
			if(shouldThrowExistingUserError) {
				throw new AccountException(ErrorCode.USER_ALREADY_EXISTS_IN_ORG_PORTAL, "This user already exists in the org portal");
			}
			return getUser(portalUser.getEmail(), iamUser.getDomainName()).getOuid();
		}
		if(IAMUserUtil.addUser(user, orgId) > 0) {
			user.setOrgId(orgId);
			user.setUserType(AccountConstants.UserType.REQUESTER.getValue());
			user.setUserStatus(true);
			addToAppORGUsers(user, emailVerification);
			if (user.getAccessibleSpace() != null) {
				addAccessibleSpace(user.getOuid(), user.getAccessibleSpace());
			}
			return user.getOuid();
		
		}
		return -1;
	}


	@Override
	public boolean updateUserPhoto(long uid, long fileId) throws Exception {
		return IAMUserUtil.updateUserPhoto(uid, fileId);
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
		if (user.getPhotoId() > 0) {

				FileStore fs = FacilioFactory.getFileStoreFromOrg(user.getOrgId(), user.getOuid());
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
		String inviteLink = getUserLink(user, "/fconfirm_reset_password/");
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
		    User appUser = getUser(user.getOrgId(), user.getUid());
			FacilioField inviteAcceptStatus = new FacilioField();
			inviteAcceptStatus.setName("inviteAcceptStatus");
			inviteAcceptStatus.setDataType(FieldType.BOOLEAN);
			inviteAcceptStatus.setColumnName("INVITATION_ACCEPT_STATUS");
			inviteAcceptStatus.setModule(AccountConstants.getAppOrgUserModule());

//			FacilioField isDefaultOrg = new FacilioField();
//			isDefaultOrg.setName("isDefaultOrg");
//			isDefaultOrg.setDataType(FieldType.BOOLEAN);
//			isDefaultOrg.setColumnName("ISDEFAULT");
//			isDefaultOrg.setModule(AccountConstants.getAppOrgUserModule());

//			FacilioField userStatus = new FacilioField();
//			userStatus.setName("userStatus");
//			userStatus.setDataType(FieldType.BOOLEAN);
//			userStatus.setColumnName("USER_STATUS");
//			userStatus.setModule(AccountConstants.getAppOrgUserModule());

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
	public Account getPermalinkAccount(IAMAccount iamAccount) throws Exception {
		// TODO Auto-generated method stub
	  if(iamAccount != null) {
	      User user = getUser(iamAccount.getUser().getOrgId(), iamAccount.getUser().getUid());
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

		String inviteLink = getUserLink(user, "/emailregistration/");
		Map<String, Object> placeholders = new HashMap<>();
		placeholders.put("toUser",user);
		//CommonCommandUtil.appendModuleNameInKey(null, "toUser", FieldUtil.getAsProperties(user), placeholders);
		placeholders.put("invitelink", inviteLink);
		if (user.getEmail().contains("@facilio.com") || FacilioProperties.isOnpremise()) {
			addBrandPlaceHolders("brandName", placeholders);
			addBrandPlaceHolders("brandUrl", placeholders);
			addBrandPlaceHolders("brandLogo", placeholders);
			AccountEmailTemplate.EMAIL_VERIFICATION.send(placeholders, true);
		} else {
			AccountEmailTemplate.ALERT_EMAIL_VERIFICATION.send(placeholders, true);
		}

	}
	
	private String getUserLink(User user, String url) throws Exception {
		String inviteToken = IAMUserUtil.getEncodedToken(user);
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
			return FacilioProperties.getConfig("clientapp.url") + "/app" + url + inviteToken;
		}
		return hostname + url + inviteToken;
	}
	
	public static GenericSelectRecordBuilder fetchUserSelectBuilder (Criteria criteria, long orgId,  Collection<Long>... ouids) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		List<FacilioField> orgUserFields = AccountConstants.getAppOrgUserFields();
		fields.addAll(orgUserFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
		;
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		
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
	public User getUser(String emailOrPhone) throws Exception {
		// TODO Auto-generated method stub
		Organization currentOrg = AccountUtil.getCurrentOrg();
		if (currentOrg == null) {
			throw new IllegalArgumentException("Organization cannot be empty");
		}
		
		Map<String, Object> props = UserUtil.getUserFromEmailOrPhoneForOrg(emailOrPhone);
		if (props != null && !props.isEmpty()) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf((long)props.get("uid")), NumberOperators.EQUALS));
			
			GenericSelectRecordBuilder selectRecordBuilder = fetchUserSelectBuilder(criteria, currentOrg.getOrgId(), null);
			List<Map<String , Object>> mapList = selectRecordBuilder.get();
			if(CollectionUtils.isNotEmpty(mapList)) {
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

	private User getAppUser(Map<String, Object> props, boolean fetchRole, boolean fetchSpace, boolean isPortalUser) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("IAM_ORG_USERID", "iamOrgUserId", String.valueOf((long)props.get("iamOrgUserId")), NumberOperators.EQUALS));
		
		GenericSelectRecordBuilder selectRecordBuilder = fetchUserSelectBuilder(criteria, (long)props.get("orgId"), null);
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
