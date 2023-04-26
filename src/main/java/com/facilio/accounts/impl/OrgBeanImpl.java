package com.facilio.accounts.impl;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.accounts.util.AccountUtil.LicenseMapping;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.copyAssetReadingCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.util.FacilioStreamUtil.distinctByKey;

@Log4j
public class OrgBeanImpl implements OrgBean {

	@Override
	public boolean updateOrg(long orgId, Organization org) throws Exception {
		return IAMOrgUtil.updateOrg(orgId, org);
	}

	@Override
	public boolean deleteOrg(long orgId) throws Exception {
		return IAMOrgUtil.deleteOrg(orgId);
	}

	@Override
	public Organization getOrg(long orgId) throws Exception {
		return IAMOrgUtil.getOrg(orgId);
	}

	@Override
	public Organization getOrg(String orgDomain) throws Exception {
		return IAMOrgUtil.getOrg(orgDomain);
	}

	@Override
	public List<Organization> getOrgs() throws Exception {
		return IAMOrgUtil.getOrgs();
	}

	@Override
	public PortalInfoContext getPortalInfo(long id, boolean isPortalId) throws Exception {
		FacilioModule portalInfoModule = AccountConstants.getPortalInfoModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getPortalCustomDomainFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(portalInfoModule.getTableName());
		if (isPortalId) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("PORTALID", "portalId", String.valueOf(id), NumberOperators.EQUALS));
		} else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(id), NumberOperators.EQUALS));
		}
		Map<String, Object> portalMap = selectBuilder.fetchFirst();
		return FieldUtil.getAsBeanFromMap(portalMap, PortalInfoContext.class);
	}

	@Override
	public List<User> getAppUsers(long orgId, long appId, boolean checkAccessibleSites)
			throws Exception {
		return getAppUsers(orgId, appId, checkAccessibleSites, false, -1, -1);
	}

	@Override
	public List<Map<String, Object>> getOrgUserApps(long orgUserId) throws Exception {
		List<Map<String, Object>> orgUserApps = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgUserAppsFields())
				.table("ORG_User_Apps")
				.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ORG_USERID", "orgUserId", orgUserId+"", NumberOperators.EQUALS)).get();
		return orgUserApps;
	}

	@Override
	public List<User> getAppUsers(long orgId, long appId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage) throws Exception {

		return getAppUsers(orgId, appId, -1, checkAccessibleSites, fetchNonAppUsers, offset, perPage);
	}

	private List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage) throws Exception {
		return getAppUsers(orgId,appId,ouId,checkAccessibleSites,fetchNonAppUsers,offset,perPage,null,null);
	}

	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus,boolean status) throws Exception {
		return getAppUsers( orgId,  appId,  ouId,  checkAccessibleSites,  fetchNonAppUsers,  offset,  perPage, searchQuery, inviteAcceptStatus, status,null,null,null,null);
	}

	@Override
	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus,boolean status,List<Long> teamId,List<Long> applicationIds,List<Long> defaultIds,Criteria criteria) throws Exception {
		return getAppUsers( orgId,  appId,  ouId,  checkAccessibleSites,  fetchNonAppUsers,  offset,  perPage, searchQuery, inviteAcceptStatus, status,teamId,applicationIds,defaultIds,criteria, null, null);
	}

	@Override
	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage, String searchQuery, Boolean inviteAcceptStatus, boolean status, List<Long> teamId, List<Long> applicationIds, List<Long> defaultIds, Criteria criteria, String orderBy, String orderType) throws Exception {
		List<User> users = getAppUsers( orgId,  appId,  ouId,  checkAccessibleSites,  fetchNonAppUsers,  -1,  -1, searchQuery, inviteAcceptStatus, teamId, applicationIds,defaultIds, criteria, orderBy, orderType);
		List<User> finalList = new ArrayList<>();
		int recordsAdded = 0,actualRecordsSkipped = 0;
		if(CollectionUtils.isNotEmpty(users)) {
			users = users.stream().filter(distinctByKey(user -> user.getOuid())).collect(Collectors.toList());
			for (User user : users) {
				if (perPage <= recordsAdded) {
					break;
				}
				if(user.isActive() == status) {
					if (offset > -1 && actualRecordsSkipped < offset) {
						actualRecordsSkipped++;
					} else {
						recordsAdded++;
						finalList.add(user);
					}
				}
			}
			if(CollectionUtils.isNotEmpty(finalList)){
				return finalList;
			}
			return null;
		}
		return null;
	}

	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus) throws Exception {
		return getAppUsers(orgId, appId, ouId, checkAccessibleSites, fetchNonAppUsers, offset, perPage,searchQuery,inviteAcceptStatus,null,null,null,null);
	}
	@Override
	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage,String searchQuery,Boolean inviteAcceptStatus,List<Long> teamId,List<Long> applicationIds,List<Long> defaultIds,Criteria criteria) throws Exception {
		return getAppUsers( orgId,  appId,  ouId,  checkAccessibleSites,  fetchNonAppUsers, offset , perPage , searchQuery, inviteAcceptStatus, teamId, applicationIds, defaultIds, criteria, null, null);
	}

	public List<User> getAppUsers(long orgId, long appId, long ouId, boolean checkAccessibleSites, boolean fetchNonAppUsers, int offset, int perPage, String searchQuery, Boolean inviteAcceptStatus, List<Long> teamId, List<Long> applicationIds, List<Long> defaultIds, Criteria criteria, String orderBy, String orderType) throws Exception {
		User currentUser = AccountUtil.getCurrentAccount().getUser();
		if(currentUser == null){
			return null;
		}

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getApplicationIdField());
		fields.add(AccountConstants.getRoleIdField());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
				.innerJoin("People")
				.on("People.ID = ORG_Users.PEOPLE_ID");

		String sortBy = null, sortType = null;
		if(CollectionUtils.isNotEmpty(defaultIds)){																					// defaultIdAndOrderBy
			sortBy = MessageFormat.format("FIELD ( ORG_Users.ORG_USERID, {0} ) DESC",StringUtils.join(defaultIds,","));
		}
		if(StringUtils.isNotEmpty(orderBy)) {
			sortBy = StringUtils.isNotEmpty(sortBy) ? sortBy + ", " + orderBy : orderBy;											// customSortBy
			sortType = StringUtils.isNotEmpty(orderType) ? orderType : "ASC";														// customSortType
			sortBy = sortBy + " " + sortType;
		}
		if(StringUtils.isNotEmpty(sortBy)) {
			selectBuilder.orderBy(sortBy);
		}

		if(CollectionUtils.isNotEmpty(teamId)){
			selectBuilder.innerJoin("FacilioGroupMembers").on("FacilioGroupMembers.ORG_USERID = ORG_Users.ORG_USERID");
			selectBuilder.andCondition(CriteriaAPI.getCondition("FacilioGroupMembers.GROUPID", "groupId", StringUtils.join(teamId, ','), NumberOperators.EQUALS));
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));

		if(ouId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ORG_USERID", "orgUserId", String.valueOf(ouId), NumberOperators.EQUALS));
		}
		if(appId > 0) {
			if (fetchNonAppUsers) {
				List<Long> appUserIds = getAppUserIds(appId, selectBuilder, fieldMap);
				if (appUserIds != null) {
					selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ouid"), appUserIds, NumberOperators.NOT_EQUALS));
				}
			} else {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("applicationId"), String.valueOf(appId), NumberOperators.EQUALS));
			}
		}
		else if(CollectionUtils.isNotEmpty(applicationIds)){
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("applicationId"), StringUtils.join(applicationIds,","), NumberOperators.EQUALS));
		}

		if(checkAccessibleSites) {
			List<Long> accessibleSpace = currentUser.getAccessibleSpace();
			String siteIdCondition = "";
			if (accessibleSpace != null && !accessibleSpace.isEmpty()) {
				Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);
				List<Long> siteIds = new ArrayList<>();
				for (BaseSpaceContext baseSpace: idVsBaseSpace.values()) {
					if (baseSpace.getSiteId() > 0) {
						siteIds.add(baseSpace.getSiteId());
					}
				}

				if (!siteIds.isEmpty()) {
					siteIdCondition = StringUtils.join(siteIds, ',');
					siteIdCondition = "(" + siteIdCondition + ")";
				}
			}

			long currentSiteId = AccountUtil.getCurrentSiteId();

			String whereCondition = "";
			if (currentSiteId > 0 && !siteIdCondition.isEmpty()) {
				whereCondition = "((not exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID)) OR (exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID AND Accessible_Space.SITE_ID = " + String.valueOf(currentSiteId);
				whereCondition += " and Accessible_Space.SITE_ID IN" + siteIdCondition + ")))" ;
			} else if (currentSiteId > 0 && siteIdCondition.isEmpty()) {
				whereCondition = "((not exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID)) OR (exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID AND Accessible_Space.SITE_ID = " + String.valueOf(currentSiteId) + ")))";
			} else if (currentSiteId <= 0 && !siteIdCondition.isEmpty()) {
				whereCondition = "((not exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID)) OR (exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID ";
				whereCondition += " and Accessible_Space.SITE_ID IN" + siteIdCondition + ")))" ;
			}


			if(!whereCondition.isEmpty()) {
				selectBuilder
						.andCustomWhere(whereCondition);
			}
		}

		if(perPage > 0 && offset >= 0) {
			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}
		if(!StringUtils.isEmpty(searchQuery))
		{
			selectBuilder.andCriteria(getUserSearchCriteria(searchQuery));
		}
		if(inviteAcceptStatus != null)
		{
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.INVITATION_ACCEPT_STATUS","inviteAcceptStatus", String.valueOf(inviteAcceptStatus), BooleanOperators.IS));
		}
		if(criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		List<Map<String, Object>> props = selectBuilder.get();
		List<ScopingContext> scopingList = ApplicationApi.getAllScoping();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
			AppDomain appDomain = null;
			if(props != null && !props.isEmpty()){
				if(appId > 0) {
					appDomain = ApplicationApi.getAppDomainForApplication((long) props.get(0).get("applicationId"));
				}
				RoleBean roleBean = (RoleBean) BeanFactory.lookup("RoleBean", orgId);

				List<Role> roles = roleBean.getRolesForApps(appId > 0 ? Collections.singletonList(appId) : null);

				Map<Long, Role> roleMap = new HashMap<>();
				for(Role role : roles){
					roleMap.put(role.getId(), role);
				}

				Map<Long, ScopingContext> scopingMap = new HashMap<>();
				if(CollectionUtils.isNotEmpty(scopingList)) {
					for (ScopingContext scoping : scopingList) {
						scopingMap.put(scoping.getId(), scoping);
					}
				}

				Map<Long, List<Long>> accessibleSpaceListMap = UserBeanImpl.getAllUsersAccessibleSpaceList();
				Map<Long, List<Long>> accessibleGroupListMap = UserBeanImpl.getAllUsersAccessibleGroupList();

				for(Map<String, Object> prop : props) {
					User user = FieldUtil.getAsBeanFromMap(prop, User.class);
					user.setId((long)prop.get("ouid"));
					if(prop.get("applicationId") != null){
						if(appDomain != null) {
							user.setAppType(appDomain.getAppType());
							user.setAppDomain(appDomain);
							user.setAppType(appDomain.getAppType());
						}
						user.setApplicationId((long)prop.get("applicationId"));
						if(user.getScopingId() != null && user.getScopingId() > 0){
							user.setScoping(scopingMap.get(user.getScopingId()));
						}
					}
					if(user.getRoleId() > 0){
						user.setRole(roleMap.get(user.getRoleId()));
					}
					if(accessibleSpaceListMap != null) {
						user.setAccessibleSpace(accessibleSpaceListMap.get(user.getOuid()));
					}
					if(accessibleGroupListMap != null) {
						user.setGroups(accessibleGroupListMap.get(user.getOuid()));
					}
					users.add(user);
				}
				return users;
			}
		}
		return null;
	}
	@Override
	public User getAppUser(long orgId, long appId, long ouId) throws Exception {
		List<User> users = getAppUsers(orgId, appId, ouId, false, false, -1, -1);
		if(CollectionUtils.isNotEmpty(users)) {
			return users.get(0);
		}
		return null;
	}

	@Override
	public Long getAppUsersCount(long orgId, long appId, boolean fetchNonAppUsers) throws Exception {
		return getAppUsersCount(orgId, appId, fetchNonAppUsers,null,null);
	}

	@Override
	public Long getAppUsersCount(long orgId, long appId, boolean fetchNonAppUsers,String searchQuery,Boolean inviteAcceptStatus) throws Exception {
		User currentUser = AccountUtil.getCurrentAccount().getUser();
		if(currentUser == null){
			return null;
		}

		List<FacilioField> fields = new ArrayList<>();

		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getApplicationIdField());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> selectedFields = new ArrayList<>();
		selectedFields.addAll(FieldFactory.getCountField(AccountConstants.getAppOrgUserModule(), fieldMap.get("ouid")));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectedFields)
				.table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
				.innerJoin("People")
				.on("People.ID = ORG_Users.PEOPLE_ID")
				.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS))
				;

		if(appId > 0) {
			if (fetchNonAppUsers) {
				List<Long> appUserIds = getAppUserIds(appId, selectBuilder, fieldMap);
				if (appUserIds != null) {
					selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ouid"), appUserIds, NumberOperators.NOT_EQUALS));
				}
			} else {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("applicationId"), String.valueOf(appId), NumberOperators.EQUALS));
			}
		}
		if(!StringUtils.isEmpty(searchQuery))
		{
			selectBuilder.andCriteria(getUserSearchCriteria(searchQuery));
		}
		if(inviteAcceptStatus != null)
		{
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.INVITATION_ACCEPT_STATUS","inviteAcceptStatus", String.valueOf(inviteAcceptStatus), BooleanOperators.IS));
		}
		List<Map<String, Object>> props = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			return (Long)props.get(0).get("count");
		}

		return null;
	}

	private List<Long> getAppUserIds(long appId, GenericSelectRecordBuilder builder, Map<String, FacilioField> fieldMap) throws Exception {
		List<Long> userIds = null;

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder(builder)
				.select(Collections.singletonList(fieldMap.get("ouid")))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("applicationId"), String.valueOf(appId), NumberOperators.EQUALS));
		;

		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			userIds = props.stream().map(prop -> (long) prop.get("ouid")).collect(Collectors.toList());
		}
		return userIds;
	}

	@Override
	public List<User> getOrgUsers(long orgId, boolean status) throws Exception {
		List<User> userList = getDefaultAppUsers(orgId);
		if(CollectionUtils.isNotEmpty(userList)) {
			List<User> finalUserList = new ArrayList<User>();
			for(User user : userList) {
				if(user.isActive() == status) {
					finalUserList.add(user);
				}
			}
			return finalUserList;
		}
		return null;
	}

	@Override
	public List<User> getDefaultAppUsers(long orgId) throws Exception {
		return getAppUsers(orgId, ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP), false);
	}

	@Override
	public List<User> getDefaultAppUsers(long orgId, String applinkName) throws Exception {
		return getAppUsers(orgId, ApplicationApi.getApplicationIdForLinkName(applinkName), false);
	}

	public Map<String, Object> getOrgUser(long iamOrgUserId) throws Exception {
		List<FacilioField> fields = new ArrayList<>(AccountConstants.getAppOrgUserFields());

		return new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.andCondition(CriteriaAPI.getCondition("IAM_ORG_USERID", "iamOrgUserId", iamOrgUserId+"", NumberOperators.EQUALS))
				.get().get(0);
	}

	@Override
	public Map<Long, User> getOrgUsersAsMap(long orgId) throws Exception {

		List<User> userList = getDefaultAppUsers(orgId);
		Map<Long, User> map = new HashMap<Long, User>();
		if(CollectionUtils.isNotEmpty(userList)) {
			for(User u : userList) {
				map.put(u.getId(), u);
			}
			return map;
		}
		return null;
	}

	@Override
	public Map<Long, User> getOrgUsersAsMap(long orgId, String LinkName) throws Exception {

		List<User> userList = getDefaultAppUsers(orgId, LinkName);
		Map<Long, User> map = new HashMap<Long, User>();
		if(CollectionUtils.isNotEmpty(userList)) {
			for(User u : userList) {
				map.put(u.getId(), u);
			}
			return map;
		}
		return null;
	}

	private List<Map<String, Object>> fetchOrgUserProps (long orgId, Criteria criteria, List<Long> applicationIds) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_User_Apps.ORG_USERID = ORG_Users.ORG_USERID")

				;

		if(criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		if(CollectionUtils.isNotEmpty(applicationIds)) {
			StringJoiner idString = new StringJoiner(",");
			for(long id : applicationIds) {
				idString.add(String.valueOf(id));
			}
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID", "applicationId", idString.toString(), NumberOperators.EQUALS));
		}

		return selectBuilder.get();
	}

	public List<User> getActiveOrgUsers(long orgId) throws Exception {

		List<User> userList = getDefaultAppUsers(orgId);
		if(CollectionUtils.isNotEmpty(userList)) {
			List<User> finalUserList = new ArrayList<User>();
			for(User user : userList) {
				if(user.isActive() && user.isUserVerified() && user.isInviteAcceptStatus()) {
					finalUserList.add(user);
				}
			}
			return finalUserList;
		}
		return null;
	}

	@Override
	public User getSuperAdmin(long orgId) throws Exception {
		Role superAdminRole = AccountUtil.getRoleBean(orgId).getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN, false);
		long applicationId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		if(SignupUtil.maintenanceAppSignup()) {
			superAdminRole = AccountUtil.getRoleBean(orgId).getRole(orgId, FacilioConstants.DefaultRoleNames.MAINTENANCE_SUPER_ADMIN, false);
			applicationId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
		}
		if(superAdminRole == null) {
			return null;
		}

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getOrgIdField(AccountConstants.getAppOrgUserModule()));
		fields.add(AccountConstants.getRoleIdField());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ROLE_ID", "roleId", String.valueOf(superAdminRole.getRoleId()), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID", "applicationId", String.valueOf(applicationId), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
			if(CollectionUtils.isNotEmpty(props)) {
				return UserBeanImpl.createUserFromProps(props.get(0), true, false, null);
			}
		}
		return null;
	}

	private Map<String,Long> getPreProdFeatureLicense() throws Exception{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getPreProdFeatureLicenseFields())
				.table(AccountConstants.getPreProdFeatureLicenseModule().getTableName());

		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			Map<String, Object> moduleMap = props.get(0);
			Map<String,Long> licenseMap= new HashMap<>();
			for (LicenseMapping val:LicenseMapping.values()) {
				licenseMap.put(val.getLicenseKey(),moduleMap.containsKey(val.getLicenseKey()) ? (long) moduleMap.get(val.getLicenseKey()) : 0);
			}
			return licenseMap;
		}
		else {
			return new HashMap<>();
		}
	}

	@Override
	public Map<String,Long> getFeatureLicense() throws Exception{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getFeatureLicenseFields())
				.table(AccountConstants.getFeatureLicenseModule().getTableName());
//				.andCondition(CriteriaAPI.getCondition(AccountConstants.getOrgIdField(AccountConstants.getFeatureLicenseModule()), orgidString, StringOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();
//		if (CollectionUtils.isNotEmpty(props)) {
//			Map<String, Object> moduleMap = props.get(0);
//			return ((long) moduleMap.get("module"));
//		}
//		else {
//			throw new IllegalArgumentException("Invalid org id for geting feature license");
//		}

		if (CollectionUtils.isNotEmpty(props)) {
			Map<String, Object> moduleMap = props.get(0);
			Map<String,Long> licenseMap= new HashMap<String, Long>();
			for (LicenseMapping val:LicenseMapping.values()) {
				licenseMap.put(val.getLicenseKey(),moduleMap.containsKey(val.getLicenseKey()) ? (long) moduleMap.get(val.getLicenseKey()) : 0);
			}

			return licenseMap;
		}
		else {
			throw new IllegalArgumentException("Invalid org id for geting feature license");
		}
	}

	@Override
	public boolean isFeatureEnabled(FeatureLicense featureLicense) throws Exception {
		return (getFeatureLicense().get(featureLicense.getGroup().getLicenseKey()) & featureLicense.getLicense()) == featureLicense.getLicense();

//		if(featureLicense.getGroup()==1) {
//			return (getFeatureLicense().get(FacilioConstants.LicenseKeys.GROUP_1_LICENSE) & featureLicense.getLicense()) == featureLicense.getLicense();
//		}
//		else {
//			return (getFeatureLicense().get(FacilioConstants.LicenseKeys.GROUP_2_LICENSE) & featureLicense.getLicense()) == featureLicense.getLicense();
//		}
	}

	public int addLicence(Map<LicenseMapping, Long> summodule) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getFeatureLicenseModule().getTableName())
				.fields(AccountConstants.getFeatureLicenseFields());

		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));

		Map<String, Object> props = new HashMap<>();
		for (Map.Entry<LicenseMapping, Long> entry : summodule.entrySet()) {
			props.put(entry.getKey().getLicenseKey(), entry.getValue());
//			props.put(FacilioConstants.LicenseKeys.LICENSE2, summodule.get(FacilioConstants.LicenseKeys.LICENSE2));
		}
		int value = updateBuilder.update(props);
		try {
			if (!FacilioProperties.isPreProd()) {
				addPreProdLicence(summodule);
			}
		} catch (Exception e) {
			LOGGER.info("Add prepord license error");
		}
		return value;
	}

	private int addPreProdLicence(Map<LicenseMapping, Long> summodule) throws Exception {

		Map<LicenseMapping, Long> prodValue = new HashMap<>(summodule);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getPreProdFeatureLicenseModule().getTableName())
				.fields(AccountConstants.getPreProdFeatureLicenseFields());

		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));

		Map<String,Long> preprodExistingValue = getPreProdFeatureLicense();
		if(MapUtils.isNotEmpty(preprodExistingValue)) {
			for (AccountUtil.FeatureLicense eachLicense : AccountUtil.FeatureLicense.values()) {
				Long val = preprodExistingValue.get(eachLicense.getGroup().getLicenseKey());
				if (val != null && (val & eachLicense.getLicense()) == eachLicense.getLicense()) {
					if (!((prodValue.get(eachLicense.getGroup()) & eachLicense.getLicense()) == eachLicense.getLicense())) {
						prodValue.put(eachLicense.getGroup(), prodValue.get(eachLicense.getGroup()) + eachLicense.getLicense());
					}
				}
			}
		}

		Map<String, Object> props = new HashMap<>();
		for (Map.Entry<LicenseMapping, Long> entry : prodValue.entrySet()) {
			props.put(entry.getKey().getLicenseKey(), entry.getValue());
		}
		int value = updateBuilder.update(props);
		return value;
	}
	public JSONObject orgInfo() throws Exception {
		JSONObject result = new JSONObject();
		FacilioModule module = AccountConstants.getOrgInfoModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgInfoFields())
				.table(module.getTableName());
//				.andCustomWhere("ORGID = ?", orgId);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				result.put(prop.get("name"), prop.get("value"));
			}

		}

		return result;
	}

	@Override
	public List<User> getOrgPortalUsers(long orgId, long appId) throws Exception {
		return getAppUsers(orgId, appId, false);
	}

	@Override
	public List<User> getRequesterTypeUsers(long orgId, boolean status) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.add(AccountConstants.getRoleIdField());

		String mainAppLinkNames = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP +","+ FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP;

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				;
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));

		fields.add(AccountConstants.getApplicationIdField());
		selectBuilder.innerJoin("ORG_User_Apps")
				.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
				.innerJoin("Application")
				.on("Application.ID = ORG_User_Apps.APPLICATION_ID")
		;
		selectBuilder.andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", mainAppLinkNames, StringOperators.ISN_T));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<User> users = new ArrayList<>();
			IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
			for(Map<String, Object> prop : props) {
				User user = UserBeanImpl.createUserFromProps(prop, false, false, null);
				if(status) {
					if(user.isActive()) {
						users.add(user);
					}
				}
				else {
					users.add(user);
				}
			}
			return users;
		}
		return null;
	}

	@Override
	public void updateLoggerLevel(int level, long orgId) throws Exception {
		// TODO Auto-generated method stub
		IAMOrgUtil.updateLoggerLevel(level, orgId);
	}

	@Override
	public void copyReadingValue(List<Map<String, Object>> prop, FacilioModule module,long orgId,long assetId,long timeDiff, List<FacilioField> fields , long targetmodId) throws Exception {
		// TODO Auto-generated method stub
		copyAssetReadingCommand.insertAssetCopyValue(prop,module,orgId,assetId,timeDiff,fields , targetmodId);
	}

	@Override
	public Organization getPortalOrg(long portalId, AppDomainType appType) throws Exception {

		PortalInfoContext portalInfo = getPortalInfo(portalId, true);
		if (portalInfo == null) {
			throw new IllegalArgumentException("Portal not found");
		}
		Organization org = getOrg(portalInfo.getOrgId());
		if (org == null) {
			throw new IllegalArgumentException("Organization not found");
		}
		org.setPortalId(portalId);

		FileStore fs = FacilioFactory.getFileStoreFromOrg(org.getId());
		org.setLogoUrl(fs.getPrivateUrl(org.getLogoId()));
		org.setOriginalUrl(fs.orginalFileUrl(org.getLogoId()));

		if(appType == AppDomainType.SERVICE_PORTAL) {
			if(portalInfo.getCustomDomain() != null) {
				org.setDomain(portalInfo.getCustomDomain());
			} else {
				org.setDomain(org.getDomain() + "." + FacilioProperties.getOccupantAppDomain());
			}
		}
		else if(appType == AppDomainType.TENANT_PORTAL) {
			org.setDomain(org.getDomain() + FacilioProperties.getTenantAppDomain());
		}
		else if(appType == AppDomainType.VENDOR_PORTAL) {
			org.setDomain(org.getDomain() + FacilioProperties.getVendorAppDomain());
		}
		else if(appType == AppDomainType.CLIENT_PORTAL) {
			org.setDomain(org.getDomain() + FacilioProperties.getClientAppDomain());
		}
		else if(appType == AppDomainType.EMPLOYEE_PORTAL) {
			org.setDomain(org.getDomain() + FacilioProperties.getEmployeeAppDomain());
		}


		return org;
	}

	@Override
	public List getEnergyMeterList() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, "all");

		FacilioChain getEnergyMeterListChain = FacilioChainFactory.getEnergyMeterListChain();
		getEnergyMeterListChain.execute(context);

		return ((List<EnergyMeterContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST));
	}
	@Override
	public Unit getOrgDisplayUnit ( int metricId ) throws Exception {
		List<FacilioField> fields = FieldFactory.getOrgUnitsFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOrgUnitsModule().getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("metric"),String.valueOf(metricId),NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Map<String, Object> prop = props.get(0);
			int unitid = (int)prop.get("unit");
			return Unit.valueOf(unitid);
		}
		return Unit.valueOf(Metric.valueOf(metricId).getSiUnitId());
	}

	@Override
	public Unit getOrgDisplayUnit ( Metric metric ) throws Exception {
		return getOrgDisplayUnit(metric.getMetricId());
	}

	@Override
	public boolean updateOrgUnit ( int metric,int unit ) throws Exception {
		List<FacilioField> fields = FieldFactory.getOrgUnitsFields();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getOrgUnitsModule().getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("metric"),String.valueOf(metric),NumberOperators.EQUALS));

		Map<String, Object> props = new HashMap<>();
		props.put("unit",unit);
		int updatedRows = updateBuilder.update(props);
		return (updatedRows > 0);
	}

	@Override
	public void updateOrgUnitsList ( JSONObject metricUnitMap ) throws Exception {
		Objects.requireNonNull(metricUnitMap,"Exception occurrend while updating Org Display unit map is null");
		List<OrgUnitsContext> orgUnitsContexts = AccountUtil.getOrgBean().getOrgUnitsList();
		List<FacilioField> fields = FieldFactory.getOrgUnitsFields();
		FacilioModule module = ModuleFactory.getOrgUnitsModule();
		for(OrgUnitsContext orgUnitsContext :orgUnitsContexts) {
			Integer unit = (Integer) metricUnitMap.get(orgUnitsContext.getMetric());
			if(!unit.equals(orgUnitsContext.getUnit())) {

				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(module.getTableName())
						.fields(fields)
						.andCondition(CriteriaAPI.getIdCondition(orgUnitsContext.getId(),module));

				Map<String, Object> props = new HashMap<>();
				props.put("unit", unit);
				updateBuilder.update(props);
			}
		}
	}

	@Override
	public List<OrgUnitsContext> getOrgUnitsList () throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOrgUnitsModule().getTableName())
				.select(FieldFactory.getOrgUnitsFields());

		List<Map<String, Object>> props = selectBuilder.get();

		if (props != null && !props.isEmpty()) {
			List<OrgUnitsContext> orgUnitsContexts = new ArrayList<>();
			for(Map<String, Object> prop:props) {
				OrgUnitsContext reportContext = FieldUtil.getAsBeanFromMap(prop, OrgUnitsContext.class);
				orgUnitsContexts.add(reportContext);
			}
			return orgUnitsContexts;
		}
		return null;
	}

	@Override
	public void runDemoRollup ( long orgId,long timeDuration ) throws Exception {
		FacilioChain demoRollupChain = TransactionChainFactory.demoRollUpChain();
		demoRollupChain.getContext().put(FacilioConstants.ContextNames.DEMO_ROLLUP_EXECUTION_TIME, timeDuration);
		demoRollupChain.getContext().put(FacilioConstants.ContextNames.DEMO_ROLLUP_JOB_ORG, orgId);
		demoRollupChain.execute();
	}

	@Override
	public List<Map<String, Object>> getApplication(long appId) throws Exception {
		return new GenericSelectRecordBuilder()
				.select(FieldFactory.getApplicationFields())
				.table("Application")
				.andCondition(CriteriaAPI.getCondition("Application.ID", "id", appId+"", NumberOperators.EQUALS))
				.get();
	}
	private Criteria getUserSearchCriteria(String searchQuery)
	{
		Criteria criteria = new Criteria();
		Condition condition_name = new Condition();
		condition_name.setColumnName("People.Name");
		condition_name.setFieldName("name");
		condition_name.setOperator(StringOperators.CONTAINS);
		condition_name.setValue(searchQuery);
		criteria.addOrCondition(condition_name);

		Condition condition_email = new Condition();
		condition_email.setColumnName("People.EMAIL");
		condition_email.setFieldName("email");
		condition_email.setOperator(StringOperators.CONTAINS);
		condition_email.setValue(searchQuery);
		criteria.addOrCondition(condition_email);


		return criteria;
	}

	public Long getOrgUserIdForPeople(long peopleID,long appID) throws Exception {
		FacilioField userId = new FacilioField();
		userId.setName("orgUserId");
		userId.setDataType(FieldType.NUMBER);
		userId.setColumnName("ORG_USERID");
		userId.setModule(ModuleFactory.getOrgUserModule());

		List<FacilioField> fields = new ArrayList<>();
		fields.add(userId);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields).table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_User_Apps.ORG_USERID = ORG_Users.ORG_USERID")
				.andCondition(CriteriaAPI.getCondition("ORG_Users.PEOPLE_ID","peopleId",String.valueOf(peopleID),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID","appId",String.valueOf(appID),NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Long Id = (Long) props.get(0).get("orgUserId");
			return Id;
		}
		return null;
	}
	public Long getDefaultApplicationId() throws Exception {
		if(SignupUtil.maintenanceAppSignup()) {
			return ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
		}
		return ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
	}
}