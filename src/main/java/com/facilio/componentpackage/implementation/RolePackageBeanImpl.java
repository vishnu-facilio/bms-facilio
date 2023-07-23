package com.facilio.componentpackage.implementation;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.RoleApp;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.NewPermissionContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class RolePackageBeanImpl implements PackageBean<Role> {
    // TODO - Handle Permission (Module) & NewPermission (Tabs)
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getRoleIdVsAppId();
    }

    @Override
    public Map<Long, Role> fetchComponents(List<Long> ids) throws Exception {
        List<Role> roles = getRolesForIds(ids);
        Map<Long, Role> roleIdVsRole = new HashMap<>();
        if (CollectionUtils.isNotEmpty(roles)) {
            roles.forEach(role -> roleIdVsRole.put(role.getRoleId(), role));
            List<NewPermission> newPermissions = getNewPermissionForIds(ids);
            if(newPermissions!=null) {
                for (NewPermission newPermission : newPermissions) {
                    if (roleIdVsRole.containsKey(newPermission.getRoleId())) {
                        if (roleIdVsRole.get(newPermission.getRoleId()).getNewPermissions() == null) {
                            roleIdVsRole.get(newPermission.getRoleId()).setNewPermissions(new ArrayList<>());
                        }
                        roleIdVsRole.get(newPermission.getRoleId()).getNewPermissions().add(newPermission);
                    }
                }
            }
        }
        return roleIdVsRole;
    }
    @Override
    public void convertToXMLComponent(Role role, XMLBuilder roleElement) throws Exception {
        RoleBean roleBean = AccountUtil.getRoleBean();
        Map<Long, String> appIdVsLinkName = new HashMap<>();
        List<Long> appIds = new ArrayList<>();
        Map<Long, List<RoleApp>> rolesAppsMapping = roleBean.getRolesAppsMapping(Collections.singletonList(role.getRoleId()));
        List<NewPermission> newPermissions = role.getNewPermissions();


        if (MapUtils.isNotEmpty(rolesAppsMapping)) {
            List<RoleApp> roleAppList = rolesAppsMapping.get(role.getRoleId());
            appIds = roleAppList.stream().map(RoleApp::getApplicationId).collect(Collectors.toList());
            appIdVsLinkName = ApplicationApi.getAppLinkNamesForIds(appIds);
        }

        roleElement.element(PackageConstants.NAME).text(role.getName());
        roleElement.element(PackageConstants.DESCRIPTION).text(role.getDescription());
        roleElement.element(PackageConstants.RoleConstants.IS_PRIVILEGED_ROLE).text(String.valueOf(role.isPrevileged()));
        roleElement.element(PackageConstants.RoleConstants.IS_SUPER_ADMIN).text(String.valueOf(role.isSuperAdmin()));

        if (MapUtils.isNotEmpty(appIdVsLinkName)) {
            XMLBuilder roleAppMapping = roleElement.element(PackageConstants.RoleConstants.ROLE_APP_MAPPING);
            for (String appLinkName : appIdVsLinkName.values()) {
                roleAppMapping.element(PackageConstants.AppXMLConstants.APP_LINK_NAME).text(appLinkName);
            }
        }
        if (newPermissions!=null &&!newPermissions.isEmpty()) {
            XMLBuilder permissionsElement = roleElement.element(PackageConstants.RoleConstants.PERMISSIONS);
            for (NewPermission newPermission : newPermissions){
                NewPermissionContext newPermissionContext = (NewPermissionContext)newPermission;
                XMLBuilder permissionElement = permissionsElement.element(PackageConstants.RoleConstants.PERMISSION);
                if(newPermissionContext.getApplicationContext() != null) {
                    permissionElement.element(PackageConstants.RoleConstants.APPLICATION).text(newPermissionContext.getApplicationContext().getLinkName());
                }
                if (newPermissionContext.getWebTabContext() != null) {
                    permissionElement.element(PackageConstants.RoleConstants.WEB_TAB_NAME).text(newPermissionContext.getWebTabContext().getRoute());
                }
                permissionElement.element(PackageConstants.RoleConstants.PERMISSION_VALUE).text(String.valueOf(newPermissionContext.getPermission()));
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
        RoleBean roleBean = AccountUtil.getRoleBean();
        Organization currentOrg = AccountUtil.getCurrentOrg();
        Map<String, Long> uniqueIdentifierVsRoleId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder roleElement = idVsData.getValue();
            String roleName = roleElement.getElement(PackageConstants.NAME).getText();

            Role dbRole = roleBean.getRole(currentOrg.getOrgId(), roleName);
            if (dbRole != null) {
                uniqueIdentifierVsRoleId.put(uniqueIdentifier, dbRole.getRoleId());
            }
        }
        return uniqueIdentifierVsRoleId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        Organization currentOrg = AccountUtil.getCurrentOrg();
        RoleBean roleBean = AccountUtil.getRoleBean();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder roleElement = idVsData.getValue();
            Role role = constructRoleFromBuilder(roleElement);

            Role dbRole = roleBean.getRole(currentOrg.getOrgId(), role.getName());
            List<RoleApp> roleAppList = constructRoleAppsFromBuilder(roleElement, appNameVsAppId);

            // Skip adding/ updating role if Role_App mapping is empty
            if (CollectionUtils.isEmpty(roleAppList)) {
                LOGGER.info("####Sandbox - Skipping adding role since RoleAppList is null for Rolename - " + role.getName());
                continue;
            }

            long roleId = -1;
            if (dbRole == null) {
                roleId = addRole(role, null, roleAppList);
            } else {
                roleId = dbRole.getRoleId();
                role.setRoleId(roleId);

                updateRole(role, null, roleAppList);
            }

            uniqueIdentifierVsComponentId.put(idVsData.getKey(), roleId);
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
                long roleId = idVsData.getKey();
                XMLBuilder roleElement = idVsData.getValue();
                Role role = constructRoleFromBuilder(roleElement);
                List<RoleApp> roleAppList = constructRoleAppsFromBuilder(roleElement, appNameVsAppId);

                if (CollectionUtils.isEmpty(roleAppList)) {
                    LOGGER.info("####Sandbox - Skipping updating role since RoleAppList is null");
                    continue;
                }
                role.setRoleId(roleId);
                updateRole(role, null, roleAppList);
            }
    }


    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        Map<Long, Map<String, Long>> appIdVsRouteNameVsTabId = PackageBeanUtil.getAllWebTabs();
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            long roleId = idVsData.getKey();
            XMLBuilder roleElement = idVsData.getValue();
            List<NewPermission> permissions = constructNewPermissionsFromBuilder(roleElement, appIdVsRouteNameVsTabId, appNameVsAppId);
            updateNewPermission(roleId, permissions);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        RoleBean roleBean = AccountUtil.getRoleBean();
        for (long id : ids) {
            roleBean.deleteRole(id);
        }
    }

    private long addRole(Role role, List<NewPermission> permission, List<RoleApp> roleAppList) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ROLE, role);
        context.put(FacilioConstants.ContextNames.PERMISSIONS, permission);
        context.put(FacilioConstants.ContextNames.ROLES_APPS, roleAppList);

        Command addRole = FacilioChainFactory.getAddWebTabRoleCommmand();
        addRole.execute(context);

        long roleId = (long) context.get(FacilioConstants.ContextNames.ROLE_ID);
        return roleId;
    }

    private void updateRole(Role role, List<NewPermission> permission, List<RoleApp> roleAppList) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ROLE, role);
        context.put(FacilioConstants.ContextNames.PERMISSIONS, permission);
        context.put(FacilioConstants.ContextNames.ROLES_APPS, roleAppList);

        Command updateRole = FacilioChainFactory.getUpdateWebTabRoleCommand();
        updateRole.execute(context);
    }

    private List<NewPermission> getNewPermissionForIds(List<Long> ids) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getNewPermissionFields())
                .table(ModuleFactory.getNewPermissionModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", StringUtils.join(ids, ","), NumberOperators.EQUALS));;
        List<Map<String,Object>> permissions = selectBuilder.get();
        if(!permissions.isEmpty()) {
            List<NewPermissionContext> newPermissions = FieldUtil.getAsBeanListFromMapList(permissions, NewPermissionContext.class);
            List<NewPermission> newPermissionList = ApplicationApi.setAppLinkNameAndRouteForNewPermissions(newPermissions);
            return newPermissionList;
        }
        return null;
    }

    private void updateNewPermission(long roleId, List<NewPermission> permissions) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ROLE_ID, roleId);
        context.put(FacilioConstants.ContextNames.PERMISSIONS, permissions);
        Command updateNewPermission = FacilioChainFactory.updateWebTabRolePermissionCommand();
        updateNewPermission.execute(context);
    }

    private Map<Long, Long> getRoleIdVsAppId() throws Exception {
        Map<Long, Long> roleIdVsParentId = new HashMap<>();
        FacilioModule module = AccountConstants.getRoleModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getNumberField("roleId", "ROLE_ID", module));
        }};

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(module.getTableName());

        List<Map<String, Object>> props = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                roleIdVsParentId.put((Long) prop.get("roleId"), -1L);
            }
        }
        return roleIdVsParentId;
    }

    private List<Role> getRolesForIds(Collection<Long> ids) throws Exception {
        FacilioModule module = AccountConstants.getRoleModule();
        List<FacilioField> fields = AccountConstants.getRoleFields();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", StringUtils.join(ids, ","), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        return FieldUtil.getAsBeanListFromMapList(props, Role.class);
    }

    private Role constructRoleFromBuilder(XMLBuilder roleElement) {
        String name = roleElement.getElement(PackageConstants.NAME).getText();
        String description = roleElement.getElement(PackageConstants.DESCRIPTION).getText();
        boolean isSuperAdmin = Boolean.parseBoolean(roleElement.getElement(PackageConstants.RoleConstants.IS_SUPER_ADMIN).getText());
        boolean isPriveleged = Boolean.parseBoolean(roleElement.getElement(PackageConstants.RoleConstants.IS_PRIVILEGED_ROLE).getText());

        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setIsSuperAdmin(isSuperAdmin);
        role.setIsPrevileged(isPriveleged);

        return role;
    }

    private List<RoleApp> constructRoleAppsFromBuilder(XMLBuilder roleElement, Map<String, Long> appNameVsAppId) {
        List<RoleApp> roleAppList = null;
        XMLBuilder roleAppElement = roleElement.getElement(PackageConstants.RoleConstants.ROLE_APP_MAPPING);
        if (roleAppElement != null) {
            roleAppList = new ArrayList<>();
            List<XMLBuilder> elementList = roleAppElement.getElementList(PackageConstants.AppXMLConstants.APP_LINK_NAME);
            for (XMLBuilder xmlBuilder : elementList) {
                String appLinkName = xmlBuilder.getText();
                long appId = appNameVsAppId.containsKey(appLinkName) ? appNameVsAppId.get(appLinkName) : -1;

                if (appId > 0) {
                    roleAppList.add( new RoleApp(appId, -1));
                }
            }
        }
        return roleAppList;
    }

    private List<NewPermission> constructNewPermissionsFromBuilder(XMLBuilder roleElement, Map<Long, Map<String, Long>> appIdVsRouteNameVsTabId, Map<String,Long> appNameVsAppId) {
        List<NewPermission> newPermissions = null;
        NewPermission newPermission = null;
        List<XMLBuilder> newPermissionsElement = roleElement.getElementList(PackageConstants.RoleConstants.PERMISSIONS);
        if (newPermissionsElement != null) {
            newPermissions = new ArrayList<>();
            for (XMLBuilder xmlBuilder : newPermissionsElement) {
                List<XMLBuilder> permissionElement = xmlBuilder.getElementList(PackageConstants.RoleConstants.PERMISSION);
                for(XMLBuilder permission:permissionElement) {
                    String appName = permission.getElement(PackageConstants.RoleConstants.APPLICATION).getText();
                    long appId = appNameVsAppId.containsKey(appName) ? appNameVsAppId.get(appName) : -1;
                    Map<String, Long> tabNameVsTabId = appIdVsRouteNameVsTabId.containsKey(appId) ? appIdVsRouteNameVsTabId.get(appId) : null;
                    newPermission = new NewPermission();
                    String webTabName = permission.getElement(PackageConstants.RoleConstants.WEB_TAB_NAME).getText();
                    long permissionValue = Long.parseLong(permission.getElement(PackageConstants.RoleConstants.PERMISSION_VALUE).getText());
                    long tabId = tabNameVsTabId.containsKey(webTabName) ? tabNameVsTabId.get(webTabName) : -1;
                    newPermission.setTabId(tabId);
                    newPermission.setPermission(permissionValue);
                    newPermissions.add(newPermission);
                }
            }
        }
        return newPermissions;
    }
}
