package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.identity.client.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.bean.UserBean;
import com.facilio.identity.client.dto.User;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserPackageBeanImpl implements PackageBean<PeopleUserContextExtendedProps> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        UserBean userBean = IdentityClient.getDefaultInstance().getUserBean();

        int offset = 0;
        int limit = 2500;
        boolean selectCompleted = false;
        Map<Long, Long> userIdsMap = new HashMap<>();
        List<Long> currIamOrgUserIds = new ArrayList<>();
        List<Map<String, Object>> currOrgUserProps = new ArrayList<>();

        do {
            currIamOrgUserIds = userBean.getOrgUserIds(orgId, true, offset, limit);
            if (CollectionUtils.isEmpty(currIamOrgUserIds)) {
                selectCompleted = true;
                continue;
            }

            currOrgUserProps = getOrgUserProps(null, currIamOrgUserIds);

            if (CollectionUtils.isNotEmpty(currOrgUserProps)) {
                for (Map<String, Object> prop : currOrgUserProps) {
                    if(prop.containsKey("orgUserId")) {
                        userIdsMap.put((long) prop.get("orgUserId"), -1L);
                    }
                }
            }

            offset = offset + currIamOrgUserIds.size();
            if (currIamOrgUserIds.size() < limit) {
                selectCompleted = true;
            }

        } while (!selectCompleted);

        return userIdsMap;
    }

    @Override
    public Map<Long, PeopleUserContextExtendedProps> fetchComponents(List<Long> orgUserIds) throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();

        List<User> userList = new ArrayList<>();
        List<Map<String, Object>> orgUserProps = new ArrayList<>();
        Map<Long, UserInfo> userInfoMapForThreadLocal = new HashMap<>();
        Map<Long, PeopleUserContextExtendedProps> peopleUserContextMap = new HashMap<>();

        int fromIndex = 0;
        List<Long> idsSubList;
        int toIndex = Math.min(orgUserIds.size(), 500);
        UserBean userBean = IdentityClient.getDefaultInstance().getUserBean();

        while (fromIndex < orgUserIds.size()) {
            idsSubList = orgUserIds.subList(fromIndex, toIndex);
            List<Map<String, Object>> currOrgUserProps = getOrgUserProps(idsSubList, null);
            if (CollectionUtils.isNotEmpty(currOrgUserProps)) {
                Set<Long> userIds = currOrgUserProps.stream().filter(prop -> prop.containsKey("userId"))
                                .map(prop -> (Long) prop.get("userId")).collect(Collectors.toSet());

                // list of AccountUser Context
                List<User> currUserList = userBean.getUserList(orgId, new ArrayList<>(userIds), true);

                orgUserProps.addAll(currOrgUserProps);
                if (CollectionUtils.isNotEmpty(currUserList)) {
                    userList.addAll(currUserList);
                }
            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 500), orgUserIds.size());
        }

        if (CollectionUtils.isEmpty(orgUserProps)) {
            return peopleUserContextMap;
        }

        Map<Long, User> userIdVsUserObject = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userList)) {
            userIdVsUserObject = userList.stream().collect(Collectors.toMap(User::getUid, user -> user, (a, b) -> b));
        }

        List<Role> allRoles = PackageBeanUtil.getAllRoles();
        Map<Long, Role> roleIdVsRole = new HashMap<>();
        if (CollectionUtils.isNotEmpty(allRoles)) {
            roleIdVsRole = allRoles.stream().collect(Collectors.toMap(Role::getRoleId, Function.identity()));
        }

        List<ApplicationContext> allApplications = ApplicationApi.getAllApplicationsWithOutFilter();
        Map<Long, ApplicationContext> appIdVsApp = new HashMap<>();
        if (CollectionUtils.isNotEmpty(allApplications)) {
            appIdVsApp = allApplications.stream().collect(Collectors.toMap(ApplicationContext::getId, Function.identity()));
        }

        for (Map<String, Object> userProp : orgUserProps) {
            long userId = (long) userProp.getOrDefault("userId", -1L);
            long roleId = (long) userProp.getOrDefault("roleId", -1L);
            long orgUserId = (long) userProp.getOrDefault("orgUserId", -1L);
            long applicationId = (long) userProp.getOrDefault("applicationId", -1L);

            User user = userIdVsUserObject.get(userId);
            if (user == null) {
                continue;
            }

            PeopleUserContextExtendedProps peopleUserContext = new PeopleUserContextExtendedProps();
            if (peopleUserContextMap.containsKey(orgUserId)) {
                peopleUserContext = peopleUserContextMap.get(orgUserId);
            }

            peopleUserContext.setUser(user);

            List<PeopleUserContextExtendedProps.RoleAppScopingProps> roleAppScopingProps = peopleUserContext.getRoleAppScopingProps();
            roleAppScopingProps = CollectionUtils.isEmpty(roleAppScopingProps) ? new ArrayList<>() : roleAppScopingProps;

            PeopleUserContextExtendedProps.RoleAppScopingProps appScopingProp = new PeopleUserContextExtendedProps.RoleAppScopingProps();
            appScopingProp.setApplicationContext(appIdVsApp.get(applicationId));
            appScopingProp.setApplicationId(applicationId);
            appScopingProp.setRole(roleIdVsRole.get(roleId));
            appScopingProp.setRoleId(roleId);

            roleAppScopingProps.add(appScopingProp);
            peopleUserContext.setRoleAppScopingProps(roleAppScopingProps);

            peopleUserContextMap.put(orgUserId, peopleUserContext);

            UserInfo userInfoObj = new UserInfo(user.getUsername(), user.getIdentifier());
            userInfoMapForThreadLocal.put(orgUserId, userInfoObj);
        }

        PackageUtil.addUserInfoForXML(userInfoMapForThreadLocal);
        return peopleUserContextMap;
    }

    @Override
    public void convertToXMLComponent(PeopleUserContextExtendedProps peopleUser, XMLBuilder element) throws Exception {
        // TODO - Handle Scoping, PermissionSets, SecurityPolicyId
        User user = peopleUser.getUser();
        PeopleContext people = peopleUser.getPeople();

        element.element(PackageConstants.UserConstants.EMAIL).text(user.getEmail());
        element.element(PackageConstants.UserConstants.NAME).text(user.getName());
        element.element(PackageConstants.UserConstants.USER_NAME).text(user.getUsername());
        element.element(PackageConstants.UserConstants.IDENTIFIER).text(user.getIdentifier());
        element.element(PackageConstants.UserConstants.USERID).text(String.valueOf(user.getUid()));
        element.element(PackageConstants.UserConstants.IS_SUPER_USER)
                .text(String.valueOf(user.getIsSuperUser() != null ? user.getIsSuperUser() : false));
        element.element(PackageConstants.UserConstants.DELETED_TIME).text(String.valueOf(user.getDeletedTime()));

        if (CollectionUtils.isNotEmpty(peopleUser.getRoleAppScopingProps())) {
            XMLBuilder roleAppScopingList = element.element(PackageConstants.UserConstants.ROLE_APP_SCOPING_LIST);
            for (PeopleUserContextExtendedProps.RoleAppScopingProps roleAppScoping : peopleUser.getRoleAppScopingProps()) {
                String roleName = roleAppScoping.getRole() != null ? roleAppScoping.getRole().getName() : null;
                String appLinkName = roleAppScoping.getApplicationContext() != null ? roleAppScoping.getApplicationContext().getLinkName() : null;

                XMLBuilder roleAppScopingElement = roleAppScopingList.element(PackageConstants.UserConstants.ROLE_APP_SCOPING);
                roleAppScopingElement.element(PackageConstants.UserConstants.ROLE).text(roleName);
                roleAppScopingElement.element(PackageConstants.UserConstants.APP_LINK_NAME).text(appLinkName);
            }
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        List<Role> allRoles = PackageBeanUtil.getAllRoles();
        Map<String, Long> roleNameVsRoleId = CollectionUtils.isEmpty(allRoles) ? new HashMap<>()
                                : allRoles.stream().collect(Collectors.toMap(Role::getName, Role::getRoleId, (a, b) -> b));

        List<ApplicationContext> allApplications = ApplicationApi.getAllApplicationsWithOutFilter();
        Map<Long, ApplicationContext> appIdVsApp = new HashMap<>();
        Map<String, Long> appNameVsAppId = new HashMap<>();
        if (CollectionUtils.isNotEmpty(allApplications)) {
            for (ApplicationContext app : allApplications) {
                appIdVsApp.put(app.getId(), app);
                appNameVsAppId.put(app.getLinkName(), app.getId());
            }
        }

        Set<Long> peopleWithUserAccess = new HashSet<>();
        Map<Long, com.facilio.identity.client.dto.AppDomain> appIdVsAppDomain = getAppIdVsAppDomain(new ArrayList<>(appIdVsApp.keySet()));

        com.facilio.accounts.dto.User superAdminUser = AccountUtil.getOrgBean().getSuperAdmin(orgId);
        Map<Long, Long> superAdminAppIdVsOrgUserId = getSuperAdminOrgUserId(orgId, superAdminUser.getUid());
        Set<Long> superAdminDefaultAppIds = superAdminAppIdVsOrgUserId.keySet();

        Map<String, List<String>> deletedUserNameVsIdentifiers = new HashMap<>();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        Map<String, Map<String, Long>> userNameVsIdentifierVsOrgUserId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder element = idVsData.getValue();
            List<PeopleUserContext> peopleUserContextList = constructPeopleUserFromBuilder(element, appNameVsAppId, roleNameVsRoleId);

            for (PeopleUserContext peopleUserContext : peopleUserContextList) {
                ApplicationContext appContext = appIdVsApp.getOrDefault(peopleUserContext.getApplicationId(), null);
                String appLinkName = appContext != null ? appContext.getLinkName() : null;
                User user = peopleUserContext.getUser();
                peopleUserContext.getUser().setAppDomain(appIdVsAppDomain.get(peopleUserContext.getApplicationId()));

                long orgUserId = -1;
                if (user.getEmail().equals(superAdminUser.getEmail()) && superAdminDefaultAppIds.contains(peopleUserContext.getApplicationId())) {
                    // OrgUserId for SuperAdmin for DefaultApps is generated on Org Creation, so only find mapping
                    orgUserId = superAdminAppIdVsOrgUserId.getOrDefault(peopleUserContext.getApplicationId(), -1L);
                } else if (userNameVsIdentifierVsOrgUserId.containsKey(user.getUsername()) && userNameVsIdentifierVsOrgUserId.get(user.getUsername()).containsKey(user.getIdentifier())
                            && !(deletedUserNameVsIdentifiers.containsKey(user.getUsername()) && deletedUserNameVsIdentifiers.get(user.getUsername()).contains(user.getIdentifier()))) {
                    // OrgUserId of a user is unique on a constraint (USERNAME, IDENTIFIER)
                    // OrgUserId is same for a user when present in both Tenant & Occupant Portal, Main & Maintenance App
                    // If a user is deleted, do not reuse old orgUserId. Create a new orgUserId
                    orgUserId = userNameVsIdentifierVsOrgUserId.get(user.getUsername()).get(user.getIdentifier());
                    if (user.getDeletedTime() < 0 || (peopleUserContext.getRoleId() > 0 && peopleUserContext.getApplicationId() > 0)) {
                        peopleUserContext.setOrgUserId(orgUserId);
                        ApplicationUserUtil.addOrgUserApps(peopleUserContext);
                    }
                } else {
                    if (user.getDeletedTime() > 0) {
                        // Deleted User
                        if (deletedUserNameVsIdentifiers.containsKey(user.getUsername()) && deletedUserNameVsIdentifiers.get(user.getUsername()).contains(user.getIdentifier())) {
                            orgUserId = userNameVsIdentifierVsOrgUserId.get(user.getUsername()).get(user.getIdentifier());
                        } else {
                            orgUserId = addSandboxUser(peopleUserContext, false, false, true, null);
                            deletedUserNameVsIdentifiers.computeIfAbsent(user.getUsername(), k -> new ArrayList<>());
                            deletedUserNameVsIdentifiers.get(user.getUsername()).add(user.getIdentifier());
                        }
                    } else if (peopleUserContext.getRoleId() < 0 && peopleUserContext.getApplicationId() < 0) {
                        // User with no app access
                        orgUserId = addSandboxUser(peopleUserContext, false, false, false, null);
                    } else if (StringUtils.isNotEmpty(appLinkName) && PORTAL_APP_LINKNAMES.contains(appLinkName)) {
                        // Portal Users
                        orgUserId = addSandboxUser(peopleUserContext, true, true, false, appLinkName);
                    } else {
                        // Maintenance/ Custom AppUsers
                        orgUserId = addSandboxUser(peopleUserContext, true, false, false, null);
                    }
                }

                if (orgUserId > 0) {
                    uniqueIdentifierVsComponentId.put(idVsData.getKey(), orgUserId);
                    userNameVsIdentifierVsOrgUserId.computeIfAbsent(user.getUsername(), k -> new HashMap<>());
                    userNameVsIdentifierVsOrgUserId.get(user.getUsername()).put(user.getIdentifier(), orgUserId);
                }

                if (peopleUserContext.getPeopleId() > 0) {
                    peopleWithUserAccess.add(peopleUserContext.getPeopleId());
                }
            }
        }
        updateUserAccess(new ArrayList<>(peopleWithUserAccess));

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        // Skipping User update
        /*
        List<Role> allRoles = PackageBeanUtil.getAllRoles();
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        Map<String, Long> roleNameVsRoleId = CollectionUtils.isEmpty(allRoles) ? new HashMap<>()
                : allRoles.stream().collect(Collectors.toMap(Role::getName, Role::getRoleId, (a, b) -> b));

        FacilioModule facilioModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE);
        List<PeopleContext> peopleModuleData = (List<PeopleContext>) PackageBeanUtil.getModuleData(null, facilioModule, PeopleContext.class, false);
        Map<String, PeopleContext> peopleIdVsPeople = new HashMap<>();
        if (CollectionUtils.isNotEmpty(peopleModuleData)) {
            peopleIdVsPeople = peopleModuleData.stream().collect(Collectors.toMap(PeopleContext::getEmail, Function.identity()));
        }

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long userId = idVsData.getKey();
            XMLBuilder element = idVsData.getValue();
            List<PeopleUserContext> peopleUserContextList = constructPeopleUserFromBuilder(element, appNameVsAppId, roleNameVsRoleId, peopleIdVsPeople);

            for (PeopleUserContext peopleUserContext : peopleUserContextList) {
                peopleUserContext.getUser().setOuid(userId);
                peopleUserContext.setOrgUserId(userId);
                updateUser(peopleUserContext);
            }
        }
         */
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    @Override
    public void addPickListConf() throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        UserBean userBean = IdentityClient.getDefaultInstance().getUserBean();

        int offset = 0;
        int limit = 2500;
        boolean selectCompleted = false;
        List<Long> currIamOrgUserIds = new ArrayList<>();
        List<Map<String, Object>> orgUserProps = new ArrayList<>();
        List<Map<String, Object>> currOrgUserProps = new ArrayList<>();

        do {
            currIamOrgUserIds = userBean.getOrgUserIds(orgId, true, offset, limit);
            if (CollectionUtils.isEmpty(currIamOrgUserIds)) {
                selectCompleted = true;
                continue;
            }

            currOrgUserProps = getOrgUserProps(null, currIamOrgUserIds);
            if (CollectionUtils.isNotEmpty(currOrgUserProps)) {
                orgUserProps.addAll(currOrgUserProps);
            }

            offset = offset + currIamOrgUserIds.size();
            if (currIamOrgUserIds.size() < limit) {
                selectCompleted = true;
            }
        } while (!selectCompleted);

        if (CollectionUtils.isEmpty(orgUserProps)) {
            return;
        }

        Set<Long> userIds = orgUserProps.stream().filter(prop -> prop.containsKey("userId"))
                .map(prop -> (Long) prop.get("userId")).collect(Collectors.toSet());

        // list of AccountUser Context
        List<Long> userIdsList = new ArrayList<>(userIds);
        List<User> userList = getUserListForIds(userBean, orgId, userIdsList);

        Map<Long, User> userIdVsUserObject = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userList)) {
            userIdVsUserObject = userList.stream().collect(Collectors.toMap(User::getUid, user -> user, (a, b) -> b));
        }

        for (Map<String, Object> userProp : orgUserProps) {
            long userId = (long) userProp.getOrDefault("userId", -1L);
            long orgUserId = (long) userProp.getOrDefault("orgUserId", -1L);

            User user = userIdVsUserObject.get(userId);
            if (user == null) {
                continue;
            }

            PackageUtil.addUserInfoForContext(user.getUsername(), user.getIdentifier(), orgUserId);
        }
    }

    private final List<String> PORTAL_APP_LINKNAMES = new ArrayList<String>() {{
        add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
    }};

    private static Map<Long, Long> getSuperAdminOrgUserId(long orgId, long uid) throws Exception {
        List<Map<String, Object>> records = ApplicationUserUtil.getOrgAppUsers(uid, -1L);
        Map<Long, Long> appIdVsOrgUserId = new HashMap<>();
        if (CollectionUtils.isNotEmpty(records)) {
            for (Map<String, Object> record : records) {
                long ouid = (Long) record.getOrDefault("ouid", -1L);
                long propOrgId = (Long) record.getOrDefault("orgId", -1L);
                long applicationId = (Long) record.getOrDefault("applicationId", -1L);

                if (propOrgId == orgId && ouid > 0 && applicationId > 0) {
                    appIdVsOrgUserId.put(applicationId, ouid);
                }
            }
        }
        return appIdVsOrgUserId;
    }

    private List<PeopleUserContext> constructPeopleUserFromBuilder(XMLBuilder element, Map<String, Long> appNameVsAppId, Map<String, Long> roleNameVsRoleId) throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        List<PeopleUserContext> peopleUserContextExtendedProps = new ArrayList<>();

        XMLBuilder roleAppScopeElementList = element.getElement(PackageConstants.UserConstants.ROLE_APP_SCOPING_LIST);
        if (roleAppScopeElementList != null) {
            List<XMLBuilder> roleAppScopeElementsList = element.getElementList(PackageConstants.UserConstants.ROLE_APP_SCOPING);
            for (XMLBuilder roleAppScopeElement : roleAppScopeElementsList) {
                String roleName = roleAppScopeElement.getElement(PackageConstants.UserConstants.ROLE).getText();
                String appLinkName = roleAppScopeElement.getElement(PackageConstants.UserConstants.APP_LINK_NAME).getText();

                long roleId = (StringUtils.isNotEmpty(roleName) && roleNameVsRoleId.containsKey(roleName)) ? roleNameVsRoleId.get(roleName) : -1;
                long appId = (StringUtils.isNotEmpty(appLinkName) && appNameVsAppId.containsKey(appLinkName)) ? appNameVsAppId.get(appLinkName) : -1;

                String email = element.getElement(PackageConstants.UserConstants.EMAIL).getText();
                String name = element.getElement(PackageConstants.UserConstants.NAME).getText();
                String userName = element.getElement(PackageConstants.UserConstants.USER_NAME).getText();
                String identifier = element.getElement(PackageConstants.UserConstants.IDENTIFIER).getText();
                long userId = Long.parseLong(element.getElement(PackageConstants.UserConstants.USERID).getText());
                long peopleId = PackageUtil.getPeopleId(email);
                long deletedTime = Long.parseLong(element.getElement(PackageConstants.UserConstants.DELETED_TIME).getText());
                boolean isSuperUser = Boolean.parseBoolean(element.getElement(PackageConstants.UserConstants.IS_SUPER_USER).getText());

                PeopleContext peopleContext = new PeopleContext();
                peopleContext.setId(peopleId);

                User user = new User();
                user.setUid(userId);
                user.setOrgId(orgId);
                user.setEmail(email);
                user.setName(name);
                user.setUsername(userName);
                user.setIdentifier(identifier);
                user.setIsSuperUser(isSuperUser);
                user.setDeletedTime(deletedTime);
                if (user.getSecurityPolicyId() == -1) {
                    user.setSecurityPolicyId(-99);
                }

                PeopleUserContext peopleUserContext = new PeopleUserContext();
                peopleUserContext.setPeople(peopleContext);
                peopleUserContext.setPeopleId(peopleId);
                peopleUserContext.setUser(user);
                peopleUserContext.setUid(userId);
                peopleUserContext.setRoleId(roleId);
                peopleUserContext.setApplicationId(appId);

                peopleUserContextExtendedProps.add(peopleUserContext);
            }
        }

        return peopleUserContextExtendedProps;
    }

    private long addUser(PeopleUserContext peopleUser) throws Exception {
        com.facilio.accounts.dto.AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(peopleUser.getApplicationId());
        FacilioUtil.throwIllegalArgumentException(appDomainObj == null, "Invalid App Domain");

        com.facilio.identity.client.dto.AppDomain appDomain = IdentityClient.getDefaultInstance().getAppDomainBean().getAppDomain(appDomainObj.getDomain());
        peopleUser.getUser().setAppDomain(appDomain);

        FacilioChain addUserChain = FacilioChainFactory.addUserChain();
        FacilioContext context = addUserChain.getContext();
        context.put(FacilioConstants.ContextNames.PASSWORD, null);
        context.put(FacilioConstants.ContextNames.USER, peopleUser);
        context.put(FacilioConstants.ContextNames.SEND_INVITE, false);

        addUserChain.execute(context);
        return peopleUser.getOrgUserId();
    }

    private void updateUser(PeopleUserContext peopleUser) throws Exception{
        FacilioChain updateUserChain = FacilioChainFactory.updateUserChain();
        FacilioContext context = updateUserChain.getContext();
        context.put(FacilioConstants.ContextNames.USER, peopleUser);

        updateUserChain.execute();
    }

    private long addOrUpdatePortalUser(PeopleUserContext peopleUser, String appLinkName) throws Exception {
        FacilioChain addOrUpdatePortalUserChain = FacilioChainFactory.addOrUpdatePortalUserChain();
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.USER, peopleUser);
        context.put(FacilioConstants.ContextNames.SEND_INVITE, false);
        context.put(FacilioConstants.ContextNames.APP_LINKNAME, appLinkName);

        addOrUpdatePortalUserChain.execute(context);
        return peopleUser.getOrgUserId();
    }

    private long addSandboxUser(PeopleUserContext peopleUserContext, boolean addAppAccess, boolean isPortal, boolean markAsDeleted, String appLinkName) throws Exception {
        FacilioChain facilioChain = FacilioChainFactory.addSandboxUserChain();

        FacilioContext context = facilioChain.getContext();
        context.put(FacilioConstants.ContextNames.USER, peopleUserContext);
        context.put(FacilioConstants.ContextNames.IS_PORTAL_USER, isPortal);
        context.put(FacilioConstants.ContextNames.APP_LINKNAME, appLinkName);
        context.put(FacilioConstants.ContextNames.ADD_APP_ACCESS, addAppAccess);
        context.put(FacilioConstants.ContextNames.IS_DELETED_USER, markAsDeleted);
        facilioChain.execute();

        peopleUserContext = (PeopleUserContext) context.get(FacilioConstants.ContextNames.USER);
        return peopleUserContext.getOrgUserId();
    }

    private List<Map<String, Object>> getOrgUserProps(List<Long> orgUserIds, List<Long> iamOrgUserIds) throws Exception {
        if (CollectionUtils.isEmpty(iamOrgUserIds) && CollectionUtils.isEmpty(orgUserIds)) {
            return null;
        }
        FacilioModule orgUserModule = ModuleFactory.getOrgUserModule();
        FacilioModule orgUserAppsModule = AccountConstants.getOrgUserAppsModule();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("userId", "USERID", orgUserModule));
            add(FieldFactory.getNumberField("orgUserId", "ORG_USERID", orgUserModule));
            add(FieldFactory.getNumberField("iamOrgUserId", "IAM_ORG_USERID", orgUserModule));
            add(FieldFactory.getNumberField("roleId", "ROLE_ID", orgUserAppsModule));
            add(FieldFactory.getNumberField("applicationId", "APPLICATION_ID", orgUserAppsModule));
        }};

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(orgUserModule.getTableName())
                .leftJoin(orgUserAppsModule.getTableName())
                .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID");

        if (CollectionUtils.isNotEmpty(iamOrgUserIds)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.IAM_ORG_USERID", "iamOrgUserId", StringUtils.join(iamOrgUserIds, ","), NumberOperators.EQUALS));
        } else if (CollectionUtils.isNotEmpty(orgUserIds)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORG_USERID", "orgUserId", StringUtils.join(orgUserIds, ","), NumberOperators.EQUALS));
        }

        List<Map<String, Object>> propsList = selectRecordBuilder.get();
        return propsList;
    }

    private List<User> getUserListForIds(UserBean userBean, long orgId, List<Long> userIdsList) throws Exception {
        int fromIndex = 0;
        List<Long> idsSubList;
        List<User> userList = new ArrayList<>();
        int toIndex = Math.min(userIdsList.size(), 500);

        while (fromIndex < userIdsList.size()) {
            idsSubList = userIdsList.subList(fromIndex, toIndex);
            List<User> currUserList = userBean.getUserList(orgId, idsSubList, true);

            if (CollectionUtils.isNotEmpty(currUserList)) {
                userList.addAll(currUserList);
            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 500), userIdsList.size());
        }
        return userList;
    }

    private Map<Long, com.facilio.identity.client.dto.AppDomain> getAppIdVsAppDomain(List<Long> appIds) throws Exception {
        Map<Long, com.facilio.identity.client.dto.AppDomain> appIdVsAppDomain = new HashMap<>();
        for (Long appId : appIds) {
            com.facilio.accounts.dto.AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
            if (appDomainObj != null) {
                com.facilio.identity.client.dto.AppDomain appDomain = IdentityClient.getDefaultInstance().getAppDomainBean().getAppDomain(appDomainObj.getDomain());
                if (appDomain != null) {
                    appIdVsAppDomain.put(appId, appDomain);
                }
            }
        }
        return appIdVsAppDomain;
    }

    private void updateUserAccess(List<Long> recordIds) throws Exception {
        if (CollectionUtils.isEmpty(recordIds)) {
            return;
        }
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);

        FacilioField idField = FieldFactory.getIdField(module);
        List<FacilioField> whereFields = new ArrayList<>(Collections.singleton(idField));
        FacilioField isUserField = FieldFactory.getBooleanField("user", "IS_USER", module);

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

        for (Long recordId : recordIds) {
            GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateVal.addUpdateValue("user", true);
            updateVal.addWhereValue(idField.getName(), recordId);
            batchUpdateList.add(updateVal);
        }

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Collections.singletonList(isUserField));

        updateRecordBuilder.batchUpdate(whereFields, batchUpdateList);
    }
}
