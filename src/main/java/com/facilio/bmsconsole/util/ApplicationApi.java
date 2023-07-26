package com.facilio.bmsconsole.util;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.*;
import com.facilio.beans.UserScopeBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.signup.maintenanceApp.DefaultTabsAndTabGroups;

import com.facilio.bmsconsoleV3.signup.moduleconfig.AgentDataLoggerModule;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.identity.client.IdentityClient;
import com.facilio.modules.*;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;
import org.reflections.Reflections;

public class ApplicationApi {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(ApplicationApi.class);

    public static long addApplicationApi(ApplicationContext application) throws Exception {
        long appId = 0;
        if (application != null) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getApplicationModule().getTableName())
                    .fields(FieldFactory.getApplicationFields());
            appId = builder.insert(FieldUtil.getAsProperties(application));
        }
        return appId;
    }

    public static ApplicationContext getDefaultApplication() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName()).select(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getCondition("Application.IS_DEFAULT", "isDefault", String.valueOf(true),
                        BooleanOperators.IS));
        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);
        if (applications != null && !applications.isEmpty()) {
            return applications.get(0);
        }
        return null;
    }

    public static ApplicationContext getApplicationForId(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName()).select(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getIdCondition(appId, ModuleFactory.getApplicationModule()));
        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);
        if (applications != null && !applications.isEmpty()) {
            return applications.get(0);
        }
        return null;
    }

    public static ApplicationContext getApplicationForLinkName(String appLinkName) throws Exception {
        return getApplicationForLinkName(appLinkName,false);
    }
    public static ApplicationContext getApplicationForLinkName(String appLinkName,boolean skipCheck) throws Exception {
        // temp handling for newapp and newtenant linkname
        if (appLinkName.equals("app")) {
            appLinkName = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
        } else if (appLinkName.equals("newtenants")) {
            appLinkName = FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP;
        } else if (appLinkName.equals("digest")) {
            appLinkName = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
        } else if (appLinkName.equals("agent")) {
            appLinkName = FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP;
        } else if (appLinkName.equals("employee")) {
            appLinkName = FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP;
        }
        else if (appLinkName.equals("iwms")) {
            appLinkName = FacilioConstants.ApplicationLinkNames.IWMS_APP;
        }
        if(appLinkName.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) && !skipCheck) {
            appLinkName = SignupUtil.getSignupApplicationLinkName();
        }

        //development check need to be changed to preproduction and removed after testing
        boolean isPreProdSetup = false;
        if(SignupUtil.maintenanceAppSignup()) {
            if (FacilioProperties.isPreProd()) {
                isPreProdSetup = true;
                if (appLinkName.equals(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)) {
                    if (checkApplicationExisitsInDB(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) > 0) {
                        appLinkName = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
                    }
                }
            }
            else {
                if (appLinkName.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
                    if (checkApplicationExisitsInDB(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP) > 0) {
                        appLinkName = FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP;
                    }
                }
            }
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName()).select(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", appLinkName, StringOperators.IS));
        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);

        if (applications != null && !applications.isEmpty()) {
            ApplicationContext app = applications.get(0);
            if(isPreProdSetup && app != null && app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) && !skipCheck) {
                app.setName("Web Maintenance");
                app.setLinkName("maintenance");
                return app;
            }
            return app;
        }
        return null;
    }

    public static long checkApplicationExisitsInDB(String appLinkName) throws Exception {
        if (StringUtils.isNotEmpty(appLinkName)) {
            // temp handling for newapp and newtenant linkname
            if (appLinkName.equals("app")) {
                appLinkName = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
            } else if (appLinkName.equals("newtenants")) {
                appLinkName = FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP;
            }
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getApplicationModule().getTableName())
                    .select(FieldFactory.getApplicationFields())
                    .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", appLinkName, StringOperators.IS));
            List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                    ApplicationContext.class);
            if (applications != null && !applications.isEmpty()) {
                return applications.get(0).getId();
            }
        }
        return -1;
    }

    public static long getApplicationIdForLinkName(String appLinkName) throws Exception {
        ApplicationContext application = getApplicationForLinkName(appLinkName, true);
        if(application != null && application.getLinkName() != null){
           appLinkName = application.getLinkName();
        }
        if (StringUtils.isNotEmpty(appLinkName)) {
            // temp handling for newapp and newtenant linkname
            if (appLinkName.equals("app")) {
                appLinkName = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
            } else if (appLinkName.equals("newtenants")) {
                appLinkName = FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP;
            }
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getApplicationModule().getTableName())
                    .select(FieldFactory.getApplicationFields())
                    .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", appLinkName, StringOperators.IS));
            List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                    ApplicationContext.class);
            if (applications != null && !applications.isEmpty()) {
                return applications.get(0).getId();
            }
        }
        return -1;
    }

    public static List<ApplicationContext> getApplicationForLinkNames(List<String> appLinkNames) throws Exception {
        List<ApplicationContext> applications = new ArrayList<ApplicationContext>();
        if (appLinkNames != null && !appLinkNames.isEmpty()) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getApplicationModule().getTableName())
                    .select(FieldFactory.getApplicationFields())
                    .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", StringUtils.join(appLinkNames, ","),
                            StringOperators.IS));
            applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                    ApplicationContext.class);
        }
        return applications;
    }

    public static List<WebTabGroupContext> getWebTabgroups() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName()).select(FieldFactory.getWebTabGroupFields());
        List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(),
                WebTabGroupContext.class);
        return webTabGroups;
    }

    public static WebTabGroupContext getWebTabGroup(long webTabGroupId) throws Exception {
        FacilioModule module = ModuleFactory.getWebTabGroupModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(webTabGroupId, module));
        return FieldUtil.getAsBeanFromMap(builder.fetchFirst(), WebTabGroupContext.class);
    }

    public static void updateWebTabGroups(WebTabGroupContext webTabGroup) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName()).fields(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(webTabGroup.getId(), ModuleFactory.getWebTabGroupModule()));
        builder.update(FieldUtil.getAsProperties(webTabGroup));
    }

    public static List<WebTabGroupContext> getWebTabGroupsForLayoutId(long layoutId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName()).select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(layoutId),
                        NumberOperators.EQUALS));
        List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(),
                WebTabGroupContext.class);
        return webTabGroups;
    }

    public static List<ApplicationLayoutContext> getLayoutsForAppId(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationLayoutModule().getTableName())
                .select(FieldFactory.getApplicationLayoutFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                        NumberOperators.EQUALS));

        List<ApplicationLayoutContext> applicationLayouts = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationLayoutContext.class);
        return applicationLayouts;
    }

    public static List<ApplicationLayoutContext> getLayouts(Collection<Long> layoutIds) throws Exception {
        if (CollectionUtils.isEmpty(layoutIds)) {
            return null;
        }

        FacilioModule module = ModuleFactory.getApplicationLayoutModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getApplicationLayoutFields())
                .andCondition(CriteriaAPI.getIdCondition(layoutIds, module));
        List<ApplicationLayoutContext> applicationLayouts = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationLayoutContext.class);
        return applicationLayouts;
    }

    public static List<WebTabGroupContext> getWebTabGroups(Collection<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getWebTabGroupModule()));

        List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabGroupContext.class);

        return webTabGroups;
    }

    public static List<ApplicationLayoutContext> getAllLayouts() throws Exception {
        FacilioModule module = ModuleFactory.getApplicationLayoutModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getApplicationLayoutFields());
        return FieldUtil.getAsBeanListFromMapList(builder.get(), ApplicationLayoutContext.class);
    }

    public static List<WebTabContext> getAllWebTabs() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName())
                .select(FieldFactory.getWebTabFields());
        return FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);
    }

    public static void deleteWebTabWebGroupForTabIdAndGroupIds(long webTabId, Collection<Long> webTabGroupIds) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("WEBTAB_ID", "webTabId", String.valueOf(webTabId), NumberOperators.EQUALS));

        if (CollectionUtils.isNotEmpty(webTabGroupIds)) {
            builder.andCondition(CriteriaAPI.getCondition("WEBTAB_GROUP_ID", "tab_groupId", StringUtils.join(webTabGroupIds, ","), NumberOperators.EQUALS));
        }

        builder.delete();
    }

    public static void clearWebTabWebGroupCacheForGroupIds(Collection<Long> webTabGroupIds) throws Exception {
        if (CollectionUtils.isEmpty(webTabGroupIds)) {
            return;
        }
        List<WebTabGroupContext> webTabGroups = getWebTabGroups(webTabGroupIds);
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        FacilioCache<String, List<WebTabGroupCacheContext>> tabGroupCache = LRUCache.getWebTabGroupCache();

        for (WebTabGroupContext webTabGroup : webTabGroups) {
            long groupId = webTabGroup.getId();
            tabsCache.remove(CacheUtil.ORG_TAB_GROUP_KEY(AccountUtil.getCurrentOrg().getId(), groupId));
            tabGroupCache.remove(CacheUtil.ORG_APP_LAYOUT_KEY(AccountUtil.getCurrentOrg().getId(), webTabGroup.getLayoutId()));
        }
    }

    public static List<WebTabGroupContext> getWebTabGroupForLayoutID(ApplicationLayoutContext layout) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName()).select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(layout.getId()),
                        NumberOperators.EQUALS));

        List<WebTabGroupContext> grps = FieldUtil.getAsBeanListFromMapList(builder.get(),
                WebTabGroupContext.class);
        return grps;
    }

    public static ApplicationLayoutContext getLayoutForAppTypeDeviceType(long appId, String appType, int deviceType) throws Exception {
        List<ApplicationLayoutContext> layouts = getLayoutsForAppLayoutType(appId, appType, deviceType);
        return CollectionUtils.isNotEmpty(layouts) ? layouts.get(0) : null;
    }

    public static List<ApplicationLayoutContext> getLayoutsForAppLayoutType(long appId, String appType, int deviceType)
            throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationLayoutModule().getTableName())
                .select(FieldFactory.getApplicationLayoutFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                        NumberOperators.EQUALS));
        if (StringUtils.isNotEmpty(appType)) {
            builder.andCondition(
                    CriteriaAPI.getCondition("APP_TYPE", "appType", String.valueOf(appType), StringOperators.IS));
        }
        if (deviceType <= 0) {
            Boolean isFromMobile = AccountUtil.getCurrentAccount().isFromMobile();
            if (isFromMobile) {
                deviceType = 2;
            } else {
                deviceType = 1;
            }
        }
        builder.andCondition(CriteriaAPI.getCondition("DEVICE_TYPE", "deviceType", String.valueOf(deviceType),
                NumberOperators.EQUALS));

        List<ApplicationLayoutContext> applicationLayouts = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationLayoutContext.class);
        return applicationLayouts;
    }

    public static long getTabIdForRoute(WebTabContext webTab) throws Exception {
        if (StringUtils.isEmpty(webTab.getRoute())) {
            throw new IllegalArgumentException("Route cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName())
                .select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("ROUTE", "route", webTab.getRoute(), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(webTab.getApplicationId()), NumberOperators.EQUALS));

        Map<String, Object> map = builder.fetchFirst();
        return MapUtils.isNotEmpty(map) ? (long) map.get("id") : -1;
    }

    public static long getTabGroupIdForRoute(WebTabGroupContext tabGroup) throws Exception {
        if (StringUtils.isEmpty(tabGroup.getRoute())) {
            throw new IllegalArgumentException("Route cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getCondition("ROUTE", "route", tabGroup.getRoute(), StringOperators.IS));

        if (tabGroup.getId() > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(tabGroup.getId()), NumberOperators.NOT_EQUALS));
        }

        if (tabGroup.getLayoutId() > 0) {
            builder.andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(tabGroup.getLayoutId()), NumberOperators.EQUALS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", "", CommonOperators.IS_EMPTY));
        }

        Map<String, Object> map = builder.fetchFirst();
        return MapUtils.isNotEmpty(map) ? (long) map.get("id") : -1;
    }

    public static Map<Long,String> getAppLinkNamesForIds(List<Long> appIds) throws Exception {
        Map<Long,String> appIdVsLinkName = new HashMap<>();
        FacilioModule module = ModuleFactory.getApplicationModule();
        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            FieldFactory.getIdField(module);
            add(FieldFactory.getStringField("linkName", "LINK_NAME", module));
        }};

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(selectableFields)
                .andCondition(CriteriaAPI.getIdCondition(appIds, ModuleFactory.getApplicationModule()));

        List<Map<String, Object>> propsList = builder.get();

        if (CollectionUtils.isNotEmpty(propsList)) {
            for (Map<String, Object> prop : propsList) {
                appIdVsLinkName.put((Long) prop.get("id"), (String) prop.get("linkName"));
            }
        }
        return appIdVsLinkName;
    }

    public static List<Map<String, Object>> getTabGroupsForTabId(WebTabContext webTabContext) throws Exception {
        FacilioModule groupModule = ModuleFactory.getWebTabGroupModule();
        FacilioModule layoutModule = ModuleFactory.getApplicationLayoutModule();
        FacilioModule webTabWebGroupModule = ModuleFactory.getWebTabWebGroupModule();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>(){{
            add(FieldFactory.getStringField("route", "ROUTE", groupModule));
            add(FieldFactory.getNumberField("order", "TAB_ORDER", webTabWebGroupModule));
            add(FieldFactory.getStringField("appType", "APP_TYPE", layoutModule));
            add(FieldFactory.getNumberField("layoutDeviceType", "DEVICE_TYPE", layoutModule));
        }};

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(selectableFields)
                .innerJoin(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .on("WebTab_Group.ID = WebTab_WebGroup.WEBTAB_GROUP_ID")
                .innerJoin(ModuleFactory.getApplicationLayoutModule().getTableName())
                .on("WebTab_Group.LAYOUT_ID = Application_Layout.ID")
                .andCondition(CriteriaAPI.getCondition("WebTab_WebGroup.WEBTAB_ID", "webTabId", String.valueOf(webTabContext.getId()), NumberOperators.EQUALS));

        return builder.get();
    }

    public static List<WebTabContext> getWebTabsForWebGroup(long webTabGroupId) throws Exception {
        List<FacilioField> fields = FieldFactory.getWebTabFields();
        fields.add(
                FieldFactory.getField("order", "TAB_ORDER", ModuleFactory.getWebTabWebGroupModule(), FieldType.NUMBER));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(fields)
                .innerJoin(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .on("WebTab_WebGroup.WEBTAB_ID = WebTab.ID")
                .andCondition(CriteriaAPI.getCondition("WebTab_WebGroup.WEBTAB_GROUP_ID", "groupId",
                        String.valueOf(webTabGroupId), NumberOperators.EQUALS));
        List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);
        return webTabs;
    }

    public static List<WebTabContext> getWebTabsForApplication(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                        NumberOperators.EQUALS));
        List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);
        return webTabs;
    }

    public static List<WebTabContext> getWebTabsForApplication(long appId, boolean withPermissions, boolean withModules) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                        NumberOperators.EQUALS));
        List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);
        if (CollectionUtils.isNotEmpty(webTabs)) {
            for (WebTabContext webtab : webTabs) {
                if (withPermissions) {
                    webtab.setPermissions(ApplicationApi.getPermissionsForWebTab(webtab.getId()));
                }
                if (withModules) {
                    List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = ApplicationApi.getTabIdModules(webtab.getId());
                    List<Long> moduleIds = new ArrayList<>();
                    List<String> specialTypes = new ArrayList<>();
                    if (tabIdAppIdMappingContextList != null && !tabIdAppIdMappingContextList.isEmpty()) {
                        for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabIdAppIdMappingContextList) {
                            if (tabIdAppIdMappingContext.getModuleId() > 0) {
                                moduleIds.add(tabIdAppIdMappingContext.getModuleId());
                            }
                            if (tabIdAppIdMappingContext.getSpecialType() != null && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("null") && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("")) {
                                specialTypes.add(tabIdAppIdMappingContext.getSpecialType());
                            }
                        }
                    }
                    webtab.setModuleIds(moduleIds);
                    webtab.setSpecialTypeModules(specialTypes);
                }
            }
            return webTabs;
        }
        return null;
    }

    public static List<NewPermission> getPermissionsForWebTab(long webTabId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewPermissionModule().getTableName())
                .select(FieldFactory.getNewPermissionFields())
                .andCondition(CriteriaAPI.getCondition(ModuleFactory.getNewPermissionModule().getTableName() + ".TAB_ID", "tabId", String.valueOf(webTabId),
                        NumberOperators.EQUALS));
        List<NewPermission> permissions = FieldUtil.getAsBeanListFromMapList(builder.get(), NewPermission.class);
        return permissions;
    }

    public static NewPermission getPermissionsForWebTabRole(long webTabId, Long roleId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewPermissionModule().getTableName())
                .select(FieldFactory.getNewPermissionFields())
                .andCondition(CriteriaAPI.getCondition(ModuleFactory.getNewPermissionModule().getTableName() + ".TAB_ID", "tabId", String.valueOf(webTabId),
                        NumberOperators.EQUALS));
        if (roleId != null) {
            builder.andCondition(CriteriaAPI.getCondition(ModuleFactory.getNewPermissionModule().getTableName() + ".ROLE_ID", "roleId", String.valueOf(roleId),
                    NumberOperators.EQUALS));
        }
        NewPermission permission = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), NewPermission.class);
        return permission;
    }

    public static List<TabIdAppIdMappingContext> getTabIdModules(long tabId) throws Exception {
        List<TabIdAppIdMappingContext> tabidMappings = null;
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                .select(FieldFactory.getTabIdAppIdMappingFields())
                .andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.TAB_ID", "tabId",
                        String.valueOf(tabId), NumberOperators.EQUALS));
        tabidMappings = FieldUtil.getAsBeanListFromMapList(builder.get(), TabIdAppIdMappingContext.class);
        return tabidMappings;
    }

    public static long getRolesPermissionValForTab(long tabId, long roleId) throws Exception {
        NewPermission permission = getRolesPermissionForTab(tabId, roleId);
        if (permission != null) {
            return permission.getPermission();
        }
        return -1;
    }

    public static List<Long> getAllApplicationIds(boolean skipFacilioMainApp) throws Exception {
        List<Long> appIds = null;
        FacilioModule applicationModule = ModuleFactory.getApplicationModule();
        FacilioField appIdField = FieldFactory.getIdField(applicationModule);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(applicationModule.getTableName())
                .select(Collections.singletonList(appIdField));

        if (skipFacilioMainApp) {
            builder.andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, StringOperators.ISN_T));
        }

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            appIds = props.stream().map(prop -> (Long) prop.get("id")).collect(Collectors.toList());
        }
        return appIds;
    }

    public static List<Long> getApplicationIds(boolean fetchSystem, boolean skipFacilioMainApp) throws Exception {
        List<Long> appIds = null;
        FacilioModule applicationModule = ModuleFactory.getApplicationModule();
        FacilioField appIdField = FieldFactory.getIdField(applicationModule);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(applicationModule.getTableName())
                .select(Collections.singletonList(appIdField))
                .andCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", String.valueOf(fetchSystem), BooleanOperators.IS));

        if (skipFacilioMainApp) {
            builder.andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, StringOperators.ISN_T));
        }

        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            appIds = props.stream().map(prop -> (Long) prop.get("id")).collect(Collectors.toList());
        }
        return appIds;
    }

    public static NewPermission getRolesPermissionForTab(long tabId, long roleId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewPermissionModule().getTableName())
                .select(FieldFactory.getNewPermissionFields())
                .andCondition(CriteriaAPI.getCondition(ModuleFactory.getNewPermissionModule().getTableName() + ".TAB_ID", "tabId", String.valueOf(tabId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(ModuleFactory.getNewPermissionModule().getTableName() + ".ROLE_ID", "roleId", String.valueOf(roleId),
                        NumberOperators.EQUALS));
        List<NewPermission> permissions = FieldUtil.getAsBeanListFromMapList(builder.get(), NewPermission.class);
        if (permissions != null && !permissions.isEmpty()) {
            return permissions.get(0);
        }
        return null;
    }

    public static WebTabContext getWebTab(long tabId) throws Exception {
        return getWebTab(tabId, false);
    }

    public static WebTabContext getWebTab(long tabId, boolean withModules) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getIdCondition(tabId, ModuleFactory.getWebTabModule()));
        List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);
        if (webTabs != null && !webTabs.isEmpty()) {
            for (WebTabContext webtab : webTabs) {
                if (withModules) {
                    List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = ApplicationApi.getTabIdModules(webtab.getId());
                    List<Long> moduleIds = new ArrayList<>();
                    List<String> specialTypes = new ArrayList<>();
                    if (tabIdAppIdMappingContextList != null && !tabIdAppIdMappingContextList.isEmpty()) {
                        for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabIdAppIdMappingContextList) {
                            if (tabIdAppIdMappingContext.getModuleId() > 0) {
                                moduleIds.add(tabIdAppIdMappingContext.getModuleId());
                            }
                            if (tabIdAppIdMappingContext.getSpecialType() != null && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("null") && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("")) {
                                specialTypes.add(tabIdAppIdMappingContext.getSpecialType());
                            }
                        }
                    }
                    webtab.setModuleIds(moduleIds);
                    webtab.setSpecialTypeModules(specialTypes);
                }
            }
            return webTabs.get(0);
        }
        return null;
    }

    public static List<String> getModulesForTab(long tabId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = ApplicationApi.getTabIdModules(tabId);
        List<Long> moduleIds = new ArrayList<>();
        List<String> specialTypes = new ArrayList<>();
        if (tabIdAppIdMappingContextList != null && !tabIdAppIdMappingContextList.isEmpty()) {
            for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabIdAppIdMappingContextList) {
                if (tabIdAppIdMappingContext.getModuleId() > 0) {
                    moduleIds.add(tabIdAppIdMappingContext.getModuleId());
                }
                if (tabIdAppIdMappingContext.getSpecialType() != null
                        && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("null")
                        && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("")) {
                    specialTypes.add(tabIdAppIdMappingContext.getSpecialType());
                }
            }
        }
        List<String> moduleNames = new ArrayList<>();
        if (!moduleIds.isEmpty()) {
            for (long moduleId : moduleIds) {
                moduleNames.add(modBean.getModule(moduleId).getName());
            }
        }
        if (!specialTypes.isEmpty()) {
            moduleNames.addAll(specialTypes);
        }
        return moduleNames;
    }

    public static List<Long> getModuleIdsForTab(long tabId) throws Exception {
        List<Long> ids = new ArrayList<Long>();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                .select(FieldFactory.getTabIdAppIdMappingFields())
                .andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.TAB_ID", "tabId",
                        String.valueOf(tabId), NumberOperators.EQUALS));
        List<TabIdAppIdMappingContext> tabidMappings = FieldUtil.getAsBeanListFromMapList(builder.get(),
                TabIdAppIdMappingContext.class);
        if (tabidMappings != null && !tabidMappings.isEmpty()) {
            for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabidMappings) {
                ids.add(tabIdAppIdMappingContext.getModuleId());
            }
        }
        return ids;
    }

    public static List<Long> getTabForModules(long appId,long moduleId) throws Exception {
        List<Long> ids = new ArrayList<Long>();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                .select(FieldFactory.getTabIdAppIdMappingFields())
                .innerJoin("WebTab")
                .on("TABID_MODULEID_APPID_MAPPING.TAB_ID = WebTab.ID")
                .andCondition(CriteriaAPI.getCondition("WebTab.TYPE", "tabType",
                        WebTabContext.Type.MODULE.getIndex().toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.MODULE_ID", "moduleId",
                        String.valueOf(moduleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.APP_ID", "appId",
                        String.valueOf(appId), NumberOperators.EQUALS));
        List<TabIdAppIdMappingContext> tabidMappings = FieldUtil.getAsBeanListFromMapList(builder.get(),
                TabIdAppIdMappingContext.class);
        if (tabidMappings != null && !tabidMappings.isEmpty()) {
            for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabidMappings) {
                ids.add(tabIdAppIdMappingContext.getTabId());
            }
        }
        return ids;
    }

    public static void addUserInApp(User user, boolean shouldThrowError) throws Exception {
        addUserInApp(user, shouldThrowError, true);
    }

    public static void addUserInApp(User user, boolean shouldThrowError, boolean isEmailVerificationNeeded)
            throws Exception {
        OrgUserApp userExisitsInApp = checkIfUserAlreadyPresentInApp(user.getUid(), user.getApplicationId(),
                user.getOrgId());
        if (userExisitsInApp == null) {
            AccountUtil.getUserBean().addToORGUsersApps(user, isEmailVerificationNeeded);
        } else {
            if (shouldThrowError) {
                throw new IllegalArgumentException("User already exists in app");
            } else {
                if (user.getRoleId() != userExisitsInApp.getRoleId()) {
                    updateRoleForUserInApp(user.getOuid(), user.getApplicationId(), user.getRoleId());
                }
            }
        }
    }

    public static int deleteUserFromApp(User user, long appId) throws Exception {
        int val = AccountUtil.getUserBean().deleteUserFromApps(user, appId);
        PeopleAPI.deletePermissionSetsForPeople(user.getPeopleId());
        return val;
    }

    public static AppDomain getAppDomainForApplication(long applicationId) throws Exception {
        List<FacilioField> fields = FieldFactory.getApplicationFields();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getApplicationModule().getTableName());
        selectBuilder.andCondition(
                CriteriaAPI.getCondition("ID", "id", String.valueOf(applicationId), NumberOperators.EQUALS));

        List<Map<String, Object>> list = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            Integer type = (Integer) list.get(0).get("domainType");
            Long orgId = (Long) list.get(0).get("orgId");
            List<AppDomain> appDomains = IAMAppUtil.getAppDomainForType(type, orgId);
            if (CollectionUtils.isNotEmpty(appDomains)) {
                return appDomains.get(0);
            }
        }
        return null;
    }

    public static com.facilio.identity.client.dto.AppDomain getAppDomainForApp(long appId) throws Exception {
        List<FacilioField> fields = FieldFactory.getApplicationFields();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getApplicationModule().getTableName());
        selectBuilder.andCondition(
                CriteriaAPI.getCondition("ID", "id", String.valueOf(appId), NumberOperators.EQUALS));

        List<Map<String, Object>> application = selectBuilder.get();
        com.facilio.identity.client.dto.AppDomain appDomain = null;
        if (CollectionUtils.isNotEmpty(application)) {
            Integer appDomainType = (Integer) application.get(0).get("domainType");
            Long orgId = (Long) application.get(0).get("orgId");
            appDomain = IdentityClient.getDefaultInstance().getAppDomainBean().getDefaultAppDomain(orgId, com.facilio.identity.client.dto.AppDomain.AppDomainType.valueOf(appDomainType));
            if (appDomain == null && com.facilio.identity.client.dto.AppDomain.AppDomainType.valueOf(appDomainType) == com.facilio.identity.client.dto.AppDomain.AppDomainType.FACILIO) {
                return IdentityClient.getDefaultInstance().getAppDomainBean().getDefaultAppDomain();
            }
        }
        return appDomain;
    }

    public static void addDefaultApps(long orgId) throws Exception {
        List<FacilioField> fields = FieldFactory.getApplicationFields();
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .fields(fields);

        Organization org = AccountUtil.getOrgBean(orgId).getOrg(orgId);
        AppDomain facilioApp = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());
        ApplicationContext facilioApplication = new ApplicationContext(orgId, "Facilio", true,
                facilioApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,
                ApplicationContext.AppLayoutType.DUAL.getIndex(), "Facilio Main App",
                ApplicationContext.AppCategory.FEATURE_GROUPING.getIndex());

        AppDomain servicePortalApp = IAMAppUtil
                .getAppDomain(org.getDomain() + "." + FacilioProperties.getOccupantAppDomain());
        ApplicationContext servicePortalapplication = new ApplicationContext(orgId, "Occupant Portal", true,
                servicePortalApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Occupant Portal App",
                ApplicationContext.AppCategory.PORTALS.getIndex());

        AppDomain tenantPortalApp = IAMAppUtil
                .getAppDomain(org.getDomain() + "." + FacilioProperties.getTenantAppDomain());
        ApplicationContext tenantPortalapplication = new ApplicationContext(orgId, "Tenant Portal", true,
                tenantPortalApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Tenant Portal App",
                ApplicationContext.AppCategory.PORTALS.getIndex());

        AppDomain vendorPortalApp = IAMAppUtil
                .getAppDomain(org.getDomain() + "." + FacilioProperties.getVendorAppDomain());
        ApplicationContext vendorPortalapplication = new ApplicationContext(orgId, "Vendor Portal", true,
                vendorPortalApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Vendor Portal App",
                ApplicationContext.AppCategory.PORTALS.getIndex());

        AppDomain clientPortalApp = IAMAppUtil
                .getAppDomain(org.getDomain() + "." + FacilioProperties.getClientAppDomain());
        ApplicationContext clientPortalapplication = new ApplicationContext(orgId, "Client Portal", true,
                clientPortalApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Client Portal App",
                ApplicationContext.AppCategory.PORTALS.getIndex());

        ApplicationContext facilioAgentApplication = new ApplicationContext(orgId, "Agent", false,
                facilioApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Agent App",
                ApplicationContext.AppCategory.WORK_CENTERS.getIndex());

        ApplicationContext maintenanceApplication = new ApplicationContext(orgId, "Maintenance", false,
                facilioApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Maintenance App",
                ApplicationContext.AppCategory.WORK_CENTERS.getIndex());
        maintenanceApplication.setConfig(FacilioUtil.parseJson("{\"canShowSitesSwitch\":true , \"canShowNotifications\":true , \"canShowProfile\":true}"));
        maintenanceApplication.setIsDefault(true);

        ApplicationContext dataLoaderApplication = new ApplicationContext(orgId, "Data Loader", false,
                facilioApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.DATA_LOADER_APP,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Data Loader App",
                ApplicationContext.AppCategory.TOOLS.getIndex());
        ApplicationContext kioskApplication = new ApplicationContext(orgId, "Visitor Kiosk", false,
                facilioApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.KIOSK_APP,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Kiosk App",
                ApplicationContext.AppCategory.WORK_CENTERS.getIndex());

        AppDomain employeePortalApp = IAMAppUtil.getAppDomain(org.getDomain() + "." + FacilioProperties.getEmployeeAppDomain());
        ApplicationContext employeePortalApplication = new ApplicationContext(orgId, "Employee Portal", true, employeePortalApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP, ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Employee Portal App", ApplicationContext.AppCategory.PORTALS.getIndex());

        ApplicationContext workplaceApplication = new ApplicationContext(orgId, "IWMS App", false,
                facilioApp.getAppDomainType(), FacilioConstants.ApplicationLinkNames.IWMS_APP,
                ApplicationContext.AppLayoutType.SINGLE.getIndex(), "Improve space utilization and streamline workplace administration",
                ApplicationContext.AppCategory.WORK_CENTERS.getIndex());
        workplaceApplication.setConfig(FacilioUtil.parseJson("{\"canShowSitesSwitch\":true , \"canShowNotifications\":true , \"canShowProfile\":true}"));

        List<ApplicationContext> applicationsDefault = new ArrayList<ApplicationContext>();
        if(!SignupUtil.maintenanceAppSignup()) {
            applicationsDefault.add(facilioApplication);
        }
        applicationsDefault.add(servicePortalapplication);
        applicationsDefault.add(tenantPortalapplication);
        applicationsDefault.add(vendorPortalapplication);
        applicationsDefault.add(clientPortalapplication);
        applicationsDefault.add(facilioAgentApplication);
        applicationsDefault.add(maintenanceApplication);
        applicationsDefault.add(dataLoaderApplication);
        applicationsDefault.add(kioskApplication);
        applicationsDefault.add(employeePortalApplication);
        applicationsDefault.add(workplaceApplication);

        List<Map<String, Object>> props = FieldUtil.getAsMapList(applicationsDefault, ApplicationContext.class);

        insertBuilder.addRecords(props);
        insertBuilder.save();

        ApplicationContext agentApplication = getApplicationForLinkName(
                FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);

        if (agentApplication.getId() > 0) {
            ApplicationLayoutContext layout = new ApplicationLayoutContext(agentApplication.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.WEB,
                    FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
            addApplicationLayout(layout);
            addAgentAppWebTabs(layout);
            Role agentAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                    FacilioConstants.DefaultRoleNames.AGENT_ADMIN);
            addAppRoleMapping(agentAdmin.getRoleId(), agentApplication.getId());
        }

        ApplicationContext tenantPortal = getApplicationForLinkName(
                FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);

        if (tenantPortal.getId() > 0) {
            ApplicationLayoutContext tpLayout = new ApplicationLayoutContext(tenantPortal.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.WEB,
                    FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            addApplicationLayout(tpLayout);

            // mobile layout for tenant portal
            ApplicationLayoutContext tpLayoutMobile = new ApplicationLayoutContext(tenantPortal.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.MOBILE,
                    FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            addApplicationLayout(tpLayoutMobile);

            addTenantPortalWebTabs(tpLayout);
            addTenantPortalWebGroupsForMobileLayout(tpLayoutMobile);

            Role tenantAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                    FacilioConstants.DefaultRoleNames.TENANT_USER);
            addAppRoleMapping(tenantAdmin.getRoleId(), tenantPortal.getId());

        }

        ApplicationContext kioskApp = getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.KIOSK_APP);

        if (kioskApp.getId() > 0) {
            ApplicationLayoutContext kioskLayout = new ApplicationLayoutContext(kioskApp.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.MOBILE,
                    FacilioConstants.ApplicationLinkNames.KIOSK_APP);
            addApplicationLayout(kioskLayout);
            addKioskWebTabs(kioskLayout);

            Role kioskAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                    FacilioConstants.DefaultRoleNames.KIOSK_ADMIN);
            addAppRoleMapping(kioskAdmin.getRoleId(), kioskApp.getId());
        }

        ApplicationContext maintenance = getApplicationForLinkName(
                FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        if (maintenance.getId() > 0) {

            // mobile layout for Maintenance App
            ApplicationLayoutContext maintenanceLayoutMobile = new ApplicationLayoutContext(maintenance.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.MOBILE,
                    FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            addApplicationLayout(maintenanceLayoutMobile);


            addMaintenancePortalWebGroupsForMobileLayout(maintenanceLayoutMobile);


            Role maintenanceSuperAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                    FacilioConstants.DefaultRoleNames.MAINTENANCE_SUPER_ADMIN);

            //Setup layout for Maintenance App

            ApplicationLayoutContext maintenanceLayoutSetup = new ApplicationLayoutContext(maintenance.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.SETUP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            addApplicationLayout(maintenanceLayoutSetup);
            addSetupLayoutWebGroups(maintenanceLayoutSetup);


            addAppRoleMapping(maintenanceSuperAdmin.getRoleId(), maintenance.getId());

            Role maintenanceAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                    FacilioConstants.DefaultRoleNames.MAINTENANCE_ADMIN);
            addAppRoleMapping(maintenanceAdmin.getRoleId(), maintenance.getId());
//            Role maintenanceManager = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
//                    FacilioConstants.DefaultRoleNames.MAINTENANCE_MANAGER);
//            addAppRoleMapping(maintenanceManager.getRoleId(), maintenance.getId());
//            Role maintenanceTechnician = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
//                    FacilioConstants.DefaultRoleNames.MAINTENANCE_TECHNICIAN);
//            addAppRoleMapping(maintenanceTechnician.getRoleId(), maintenance.getId());
        }

        ApplicationContext dataLoader = getApplicationForLinkName(
                FacilioConstants.ApplicationLinkNames.DATA_LOADER_APP);

        if (dataLoader.getId() > 0) {

            ApplicationLayoutContext dataLoaderLayout = new ApplicationLayoutContext(dataLoader.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.WEB,
                    FacilioConstants.ApplicationLinkNames.DATA_LOADER_APP);
            addApplicationLayout(dataLoaderLayout);

            Role dataLoaderAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                    FacilioConstants.DefaultRoleNames.DATA_LOADER_ADMIN);
            addAppRoleMapping(dataLoaderAdmin.getRoleId(), dataLoader.getId());
        }

        ApplicationContext servicePortal = getApplicationForLinkName(
                FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);

        if (servicePortal.getId() > 0) {

            ApplicationLayoutContext spLayout = new ApplicationLayoutContext(servicePortal.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.WEB,
                    FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
            addApplicationLayout(spLayout);

            // mobile layout for occupant portal
            ApplicationLayoutContext opLayoutMobile = new ApplicationLayoutContext(servicePortal.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.MOBILE,
                    FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
            addApplicationLayout(opLayoutMobile);

            addOccupantPortalWebTabs(spLayout);
            addOccupantPortalWebGroupsForMobileLayout(opLayoutMobile);

            Role occupantAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                    FacilioConstants.DefaultRoleNames.OCCUPANT_USER);
            addAppRoleMapping(occupantAdmin.getRoleId(), servicePortal.getId());

        }

        if(!SignupUtil.maintenanceAppSignup()) {
            ApplicationContext mainApp = getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);

            if (mainApp.getId() > 0) {

                ApplicationLayoutContext mainLayout = new ApplicationLayoutContext(mainApp.getId(),
                        ApplicationLayoutContext.AppLayoutType.DUAL, ApplicationLayoutContext.LayoutDeviceType.WEB,
                        FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                addApplicationLayout(mainLayout);

                Role admin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                        FacilioConstants.DefaultRoleNames.ADMIN);
                addAppRoleMapping(admin.getRoleId(), mainApp.getId());

                Role superAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                        FacilioConstants.DefaultRoleNames.SUPER_ADMIN);
                ApplicationApi.addAppRoleMapping(superAdmin.getRoleId(), mainApp.getId());

                Role tech = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), "Technician");
                ApplicationApi.addAppRoleMapping(tech.getRoleId(), mainApp.getId());

                Role manager = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), "Manager");
                ApplicationApi.addAppRoleMapping(manager.getRoleId(), mainApp.getId());

            }
        }
        ApplicationContext vendorPortal = getApplicationForLinkName(
                FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);

        if (vendorPortal.getId() > 0) {

            ApplicationLayoutContext vpLayout = new ApplicationLayoutContext(vendorPortal.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.WEB,
                    FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
            addApplicationLayout(vpLayout);

            // mobile layout for occupant portal
            ApplicationLayoutContext opLayoutMobile = new ApplicationLayoutContext(vendorPortal.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.MOBILE,
                    FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
            addApplicationLayout(opLayoutMobile);

            addVendorPortalWebTabs(vpLayout);

            Role vendorAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                    FacilioConstants.DefaultRoleNames.VENDOR_USER);
            addAppRoleMapping(vendorAdmin.getRoleId(), vendorPortal.getId());

        }

        ApplicationContext clientPortal = getApplicationForLinkName(
                FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);

        if (clientPortal.getId() > 0) {

            ApplicationLayoutContext cpLayout = new ApplicationLayoutContext(clientPortal.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.WEB,
                    FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
            addApplicationLayout(cpLayout);

            Role clientAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
                    FacilioConstants.DefaultRoleNames.CLIENT_USER);
            addAppRoleMapping(clientAdmin.getRoleId(), clientPortal.getId());

            ApplicationLayoutContext cpLayoutMobile = new ApplicationLayoutContext(clientPortal.getId(),
                    ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.MOBILE,
                    FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
            addApplicationLayout(cpLayoutMobile);
        }

        ApplicationContext employeePortal = getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);

        if (employeePortal.getId() > 0) {
            ApplicationLayoutContext layout = new ApplicationLayoutContext(employeePortal.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.WEB, FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
            addApplicationLayout(layout);
            addEmployeeAppWebTabs(layout);
            Role employeeAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.EMPLOYEE_ADMIN);
            addAppRoleMapping(employeeAdmin.getRoleId(), employeePortal.getId());
        }

        ApplicationContext workplaceApp = getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        if(workplaceApp.getId() >0)
        {
            //Workplace App Layout
            ApplicationLayoutContext workplaceLayout = new ApplicationLayoutContext(workplaceApp.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE,
                    ApplicationLayoutContext.LayoutDeviceType.WEB, FacilioConstants.ApplicationLinkNames.IWMS_APP);
            addApplicationLayout(workplaceLayout);
            addWorkplaceAppWebTabs(workplaceLayout);
            //ApplicationLayoutContext workplaceLayoutSetup = new ApplicationLayoutContext(maintenance.getId(), ApplicationLayoutContext.AppLayoutType.SINGLE, ApplicationLayoutContext.LayoutDeviceType.SETUP, FacilioConstants.ApplicationLinkNames.IWMS_APP);
            //addApplicationLayout(workplaceLayoutSetup);
            //addIWMSSetupLayoutWebGroups(workplaceLayoutSetup);

            Role workplaceAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.IWMS_ADMIN);
            addAppRoleMapping(workplaceAdmin.getRoleId(), workplaceApp.getId());

        }

    }

    public static void addAgentAppWebTabs(ApplicationLayoutContext layout) {
        ModuleBean modBean = null;
        try {
            modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;
            int groupOrder = 1;
            webTabGroups.add(new WebTabGroupContext("Agent", "agent", layout.getId(), 1, groupOrder));
            webTabs = new ArrayList<>();
            configJSON = new JSONObject();
            configJSON.put("type", "overview");
            webTabs.add(new WebTabContext("Overview", "overview", WebTabContext.Type.CUSTOM, null,
                    layout.getApplicationId(), configJSON));
            configJSON = new JSONObject();
            configJSON.put("type", "agents");
            webTabs.add(new WebTabContext("Agents", "list", WebTabContext.Type.CUSTOM, null, layout.getApplicationId(),
                    configJSON));
            webTabs.add(new WebTabContext("Controllers", "controllers", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("controller").getModuleId()), layout.getApplicationId(), null));
            configJSON = new JSONObject();
            configJSON.put("type", "points");
            webTabs.add(new WebTabContext("Points", "points", WebTabContext.Type.CUSTOM, null,
                    layout.getApplicationId(), configJSON));
            configJSON = new JSONObject();
            configJSON.put("type", "commissioning");
            webTabs.add(new WebTabContext("Commissioning", "commissioning", WebTabContext.Type.CUSTOM, null,
                    layout.getApplicationId(), configJSON));
            configJSON = new JSONObject();
            configJSON.put("type", "trigger");
            webTabs.add(new WebTabContext("Triggers", "trigger", WebTabContext.Type.CUSTOM, null,
                    layout.getApplicationId(), configJSON));
            webTabs.add(new WebTabContext("Alarms", "alarm", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("agentAlarm").getModuleId()), layout.getApplicationId(), null));
            configJSON = new JSONObject();
            configJSON.put("type", "alarmrule");
            webTabs.add(new WebTabContext("Alarm Automation", "automation", WebTabContext.Type.CUSTOM, null,
                    layout.getApplicationId(), configJSON));
            configJSON = new JSONObject();
            configJSON.put("type", "alarm_mapping");
            webTabs.add(new WebTabContext("Alarm Mapping", "alarmmapping", WebTabContext.Type.CUSTOM, null,
                    layout.getApplicationId(), configJSON));
            configJSON = new JSONObject();
            configJSON.put("type", "metrics");
            webTabs.add(new WebTabContext("Metrics", "metrics", WebTabContext.Type.CUSTOM, null,
                    layout.getApplicationId(), configJSON));
            configJSON = new JSONObject();
            configJSON.put("type", "log");
            webTabs.add(new WebTabContext("Command Log", "log", WebTabContext.Type.CUSTOM, null,
                    layout.getApplicationId(), configJSON));
//            configJSON = new JSONObject();
//            configJSON.put("type", "agent_data");
//            webTabs.add(new WebTabContext("Agent Data", "data", WebTabContext.Type.CUSTOM, null,
//                    layout.getApplicationId(), configJSON));

            AgentDataLoggerModule agentModule = new AgentDataLoggerModule();
            agentModule.addData();
            configJSON = new JSONObject();
            configJSON.put("type", "logs");
            webTabs.add(new WebTabContext("Logs", "logs", WebTabContext.Type.CUSTOM,
                    Arrays.asList(modBean.getModule("agentDataLogger").getModuleId()), layout.getApplicationId(), configJSON));


            groupNameVsWebTabsMap.put("agent", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if (CollectionUtils.isNotEmpty(tabs)) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addTenantPortalWebTabs(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;

            webTabGroups.add(new WebTabGroupContext("Home", "home", layout.getId(), 200, groupOrder++));
            webTabs = new ArrayList<>();
            configJSON = new JSONObject();
            configJSON.put("type", "portalOverview");
            webTabs.add(new WebTabContext("Overview", "summary", WebTabContext.Type.CUSTOM, null, appId, configJSON));
            webTabs.add(new WebTabContext("Dashboard", "dashboard", WebTabContext.Type.DASHBOARD, null, appId, null));

            groupNameVsWebTabsMap.put("home", webTabs);

            webTabGroups.add(new WebTabGroupContext("Requests", "requests", layout.getId(), 201, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Work Request", "workorder", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("workorder").getModuleId()), appId, null));

            groupNameVsWebTabsMap.put("requests", webTabs);

            webTabGroups.add(new WebTabGroupContext("Vendors", "vendor", layout.getId(), 202, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Vendor", "vendors", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("vendors").getModuleId()), appId, null));
            webTabs.add(new WebTabContext("Work Permits", "workpermit", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("workpermit").getModuleId()), appId, null));

            groupNameVsWebTabsMap.put("vendor", webTabs);

            webTabGroups.add(new WebTabGroupContext("Visits", "visits", layout.getId(), 203, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Invites", "visitorinvites", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("invitevisitor").getModuleId()), appId, null,
                    AccountUtil.FeatureLicense.VISITOR.getFeatureId()));
            webTabs.add(new WebTabContext("Visits", "visits", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("visitorlog").getModuleId()), appId, null,
                    AccountUtil.FeatureLicense.VISITOR.getFeatureId()));

            groupNameVsWebTabsMap.put("visits", webTabs);

            webTabGroups.add(new WebTabGroupContext("Community", "community", layout.getId(), 204, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Announcements", "announcement", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("peopleannouncement").getModuleId()), appId, null,
                    AccountUtil.FeatureLicense.COMMUNITY.getFeatureId()));
            webTabs.add(new WebTabContext("Neighbourhood", "neighbourhood", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("neighbourhood").getModuleId()), appId, null,
                    AccountUtil.FeatureLicense.COMMUNITY.getFeatureId()));
            webTabs.add(new WebTabContext("Deals & Offers", "deals", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("dealsandoffers").getModuleId()), appId, null,
                    AccountUtil.FeatureLicense.COMMUNITY.getFeatureId()));
            webTabs.add(new WebTabContext("News & Information", "news", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("newsandinformation").getModuleId()), appId, null,
                    AccountUtil.FeatureLicense.COMMUNITY.getFeatureId()));

            groupNameVsWebTabsMap.put("community", webTabs);

            webTabGroups.add(
                    new WebTabGroupContext("Service Catalogue", "servicecatalog", layout.getId(), 205, groupOrder++));
            webTabs = new ArrayList<>();
            configJSON = new JSONObject();
            configJSON.put("type", "serviceCatalog");
            webTabs.add(new WebTabContext("Service Catalog", "catalog", WebTabContext.Type.CUSTOM, null, appId,
                    configJSON));

            groupNameVsWebTabsMap.put("servicecatalog", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if (CollectionUtils.isNotEmpty(tabs)) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addTenantPortalWebGroupsForMobileLayout(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;

            webTabGroups.add(new WebTabGroupContext("Favorite", "favorite", layout.getId(), 200, groupOrder++));
            webTabs = new ArrayList<>();
            configJSON = new JSONObject();
            configJSON.put("path", "/webview/tenant/home");

            WebTabContext homeTab = new WebTabContext("Home", "home", WebTabContext.Type.PORTAL_OVERVIEW, null, appId,
                    configJSON);
            tabBean.addTab(homeTab);
            webTabs.add(homeTab);

            webTabs.add(getWebTabForApplication(appId, "workorder"));
            webTabs.add(getWebTabForApplication(appId, "catalog"));
            WebTabContext notificationWebTab = new WebTabContext("Notifications", "notification",
                    WebTabContext.Type.NOTIFICATION, null, appId, null);
            tabBean.addTab(notificationWebTab);
            webTabs.add(notificationWebTab);
            groupNameVsWebTabsMap.put("favorite", webTabs);

            webTabGroups.add(new WebTabGroupContext("Vendors", "vendor", layout.getId(), 202, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(getWebTabForApplication(appId, "vendors"));
            webTabs.add(getWebTabForApplication(appId, "workpermit"));

            groupNameVsWebTabsMap.put("vendor", webTabs);

            webTabGroups.add(new WebTabGroupContext("Visits", "visits", layout.getId(), 203, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(getWebTabForApplication(appId, "visitorinvites"));
            webTabs.add(getWebTabForApplication(appId, "visits"));

            groupNameVsWebTabsMap.put("visits", webTabs);

            webTabGroups.add(new WebTabGroupContext("Community", "community", layout.getId(), 204, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(getWebTabForApplication(appId, "announcement"));
            webTabs.add(getWebTabForApplication(appId, "neighbourhood"));
            webTabs.add(getWebTabForApplication(appId, "deals"));
            webTabs.add(getWebTabForApplication(appId, "news"));

            groupNameVsWebTabsMap.put("community", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                if (CollectionUtils.isNotEmpty(tabs)) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addMaintenancePortalWebGroupsForMobileLayout(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();

            webTabGroups.add(new WebTabGroupContext("Favorite", "favorite", layout.getId(), 200, groupOrder++));
            webTabs = new ArrayList<>();

            webTabs.add(new WebTabContext("Dashboard", "dashboard", WebTabContext.Type.DASHBOARD, null, appId, null));
            WebTabContext woTab = new WebTabContext("Work Order", "workorder", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("workorder").getModuleId()), appId, null);
            woTab.setFeatureLicense(1);
            webTabs.add(woTab);
            groupNameVsWebTabsMap.put("favorite", webTabs);

            webTabGroups.add(new WebTabGroupContext("Assets", "asset", layout.getId(), 202, groupOrder++));
            webTabs = new ArrayList<>();
            WebTabContext assetTab = new WebTabContext("Assets", "asset", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("asset").getModuleId()), appId, null);
            assetTab.setFeatureLicense(4);
            webTabs.add(assetTab);

            groupNameVsWebTabsMap.put("asset", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if (CollectionUtils.isNotEmpty(tabs)) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addMaintenancePortalWebGroupsForWebLayout(ApplicationLayoutContext layout) {
        try {
            long webGroupId = 0l;
            FacilioChain chain;
            FacilioContext chainContext;
            DefaultTabsAndTabGroups defaultTabsAndTabGroups = new DefaultTabsAndTabGroups();
            for (WebTabGroupContext webTabGroupContext : defaultTabsAndTabGroups.getWebTabGroups(layout.getApplicationId(), layout.getId())) {
                if (!webTabGroupContext.getName().equals("ONLY_TABS")) {
                    chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                    chain.execute();
                    webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                }
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = defaultTabsAndTabGroups.getGroupNameVsTabsMap(layout.getApplicationId(), layout.getId())
                        .get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    WebTabContext webtab = getWebTabForApplication(layout.getApplicationId(), webTabContext.getRoute());
                    long tabId = 0l;
                    if (webtab != null) {
                        tabId = webtab.getId();
                    } else {
                        chain = TransactionChainFactory.getAddOrUpdateTabChain();
                        chainContext = chain.getContext();
                        chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                        chain.execute();
                        tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    }
                    webTabContext.setId(tabId);
                }
                if (CollectionUtils.isNotEmpty(tabs) && !webTabGroupContext.getName().equals("ONLY_TABS")) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            throw new RuntimeException("Error");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error");
        } catch (Exception e) {
            throw new RuntimeException("Error" + e);
        }
    }


    public static long getAddApplicationLayout(ApplicationLayoutContext layout) throws Exception {
        List<FacilioField> fields = FieldFactory.getApplicationLayoutFields();
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getApplicationLayoutModule().getTableName())
                .fields(fields);
        Map<String, Object> props = FieldUtil.getAsProperties(layout);
        long id = insertBuilder.insert(props);
        return id;
    }

    public static void addKioskWebTabs(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;

            webTabGroups.add(new WebTabGroupContext("Visits", "visits", layout.getId(), 203, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Visits", "visits", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("visitorlog").getModuleId()), appId, null,
                    AccountUtil.FeatureLicense.VISITOR.getFeatureId()));

            groupNameVsWebTabsMap.put("visits", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if (CollectionUtils.isNotEmpty(tabs)) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addVendorPortalWebTabs(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;

            webTabGroups.add(new WebTabGroupContext("WorkOrders", "workorder", layout.getId(), 201, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("WorkOrders", "workorders", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("workorder").getModuleId()), appId, null));
            groupNameVsWebTabsMap.put("workorder", webTabs);

            webTabGroups.add(new WebTabGroupContext("Work Permits", "workpermit", layout.getId(), 202, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Work Permits", "workpermits", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("workpermit").getModuleId()), appId, null));
            groupNameVsWebTabsMap.put("workpermit", webTabs);

            webTabGroups.add(new WebTabGroupContext("Invites", "visits", layout.getId(), 203, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Invites", "visitorinvites", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("invitevisitor").getModuleId()), appId, null,
                    AccountUtil.FeatureLicense.VISITOR.getFeatureId()));
            groupNameVsWebTabsMap.put("visits", webTabs);

            webTabGroups.add(new WebTabGroupContext("Insurance", "insurance", layout.getId(), 205, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Insurance", "insurances", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("insurance").getModuleId()), appId, null));
            groupNameVsWebTabsMap.put("insurance", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if (CollectionUtils.isNotEmpty(tabs)) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addDevAppWebTabs(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs;

            webTabGroups.add(new WebTabGroupContext("Workorders", "workorder", layout.getId(), 201, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Workorders", "workorders", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("workorder").getModuleId()), appId, null));
            groupNameVsWebTabsMap.put("workorder", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if (CollectionUtils.isNotEmpty(tabs)) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addOccupantPortalWebTabs(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;

            webTabGroups.add(new WebTabGroupContext("Home", "home", layout.getId(), 200, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Dashboard", "dashboard", WebTabContext.Type.DASHBOARD, null, appId, null));
            groupNameVsWebTabsMap.put("home", webTabs);

            webTabGroups.add(new WebTabGroupContext("My Requests", "workorder", layout.getId(), 201, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Requests", "workorders", WebTabContext.Type.MODULE,
                    Arrays.asList(modBean.getModule("workorder").getModuleId()), appId, null));
            groupNameVsWebTabsMap.put("workorder", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if (CollectionUtils.isNotEmpty(tabs)) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addOccupantPortalWebGroupsForMobileLayout(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();

            webTabGroups.add(new WebTabGroupContext("Home", "home", layout.getId(), 200, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(getWebTabForApplication(appId, "dashboard"));
            groupNameVsWebTabsMap.put("home", webTabs);

            webTabGroups.add(new WebTabGroupContext("My Requests", "workorder", layout.getId(), 201, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(getWebTabForApplication(appId, "workorders"));
            groupNameVsWebTabsMap.put("workorder", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
//                for (WebTabContext webTabContext : tabs) {
//                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
//                    chainContext = chain.getContext();
//                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
//                    chain.execute();
//                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
//                    webTabContext.setId(tabId);
//                }
                if (CollectionUtils.isNotEmpty(tabs)) {
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addEmployeeAppWebTabs(ApplicationLayoutContext layout) {

        ModuleBean modBean = null;

        try {

            modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();

            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();

            List<WebTabContext> webTabs = new ArrayList<>();

            JSONObject configJSON;

            int groupOrder = 1;


            webTabGroups.add(new WebTabGroupContext("Home", "home", layout.getId(), 213, groupOrder++));

            webTabs = new ArrayList<>();

            webTabs.add(new WebTabContext("Home", "homepage", WebTabContext.Type.HOMEPAGE, null, layout.getApplicationId(), null));

            groupNameVsWebTabsMap.put("home", webTabs);


            webTabGroups.add(new WebTabGroupContext("Floor Map", "floorplan", layout.getId(), 214, groupOrder++));

            webTabs = new ArrayList<>();

            webTabs.add(new WebTabContext("Floor Map", "floor-map", WebTabContext.Type.INDOOR_FLOORPLAN, null, layout.getApplicationId(), null));

            groupNameVsWebTabsMap.put("floorplan", webTabs);


            webTabGroups.add(new WebTabGroupContext("My Bookings", "booking", layout.getId(), 215, groupOrder++));

            webTabs = new ArrayList<>();

            webTabs.add(new WebTabContext("My Bookings", "my-bookings", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("spacebooking").getModuleId()), layout.getApplicationId(), null));

            groupNameVsWebTabsMap.put("booking", webTabs);
            webTabGroups.add(new WebTabGroupContext("Service Catalog", "servicecatalog", layout.getId(), 216, groupOrder++));

            webTabs = new ArrayList<>();

            webTabs.add(new WebTabContext("Service Catalog", "service-catalog", WebTabContext.Type.SERVICE_CATALOG, null, layout.getApplicationId(), null));

            groupNameVsWebTabsMap.put("servicecatalog", webTabs);


            webTabGroups.add(new WebTabGroupContext("My Requests", "request", layout.getId(), 217, groupOrder++));

            webTabs = new ArrayList<>();

            webTabs.add(new WebTabContext("Requests", "my-request", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("serviceRequest").getModuleId()), layout.getApplicationId(), null));

            groupNameVsWebTabsMap.put("request", webTabs);


            webTabGroups.add(new WebTabGroupContext("Visitors", "visitor", layout.getId(), 218, groupOrder++));

            webTabs = new ArrayList<>();

            webTabs.add(new WebTabContext("Invites", "visitorinvites", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("invitevisitor").getModuleId()), layout.getApplicationId(), null, AccountUtil.FeatureLicense.VISITOR.getFeatureId()));

            webTabs.add(new WebTabContext("Visits", "visits", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("visitorlog").getModuleId()), layout.getApplicationId(), null, AccountUtil.FeatureLicense.VISITOR.getFeatureId()));

            groupNameVsWebTabsMap.put("visitor", webTabs);


            webTabGroups.add(new WebTabGroupContext("Deliveries", "delivery", layout.getId(), 219, groupOrder++));

            webTabs = new ArrayList<>();

            webTabs.add(new WebTabContext("Deliveries", "deliveries", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("deliveries").getModuleId()), layout.getApplicationId(), null));

            groupNameVsWebTabsMap.put("delivery", webTabs);

            for (WebTabGroupContext webTabGroupContext : webTabGroups) {

                System.out.println("we: " + webTabGroupContext.getRoute());

                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();

                FacilioContext chainContext = chain.getContext();

                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);

                chain.execute();

                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);

                webTabGroupContext.setId(webGroupId);

                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());

                for (WebTabContext webTabContext : tabs) {

                    chain = TransactionChainFactory.getAddOrUpdateTabChain();

                    chainContext = chain.getContext();

                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);

                    chain.execute();

                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);

                    webTabContext.setId(tabId);

                }

                if (CollectionUtils.isNotEmpty(tabs)) {

                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();

                    chainContext = chain.getContext();

                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);

                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);

                    chain.execute();

                }

            }
        } catch (InstantiationException e) {

            e.printStackTrace();

        } catch (IllegalAccessException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }


    }
    public static void addWorkplaceAppWebTabs(ApplicationLayoutContext layout) {
        ModuleBean modBean = null;
        try {
            modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;
            int groupOrder = 1;
            webTabGroups.add(new WebTabGroupContext("Home", "home", layout.getId(), 1, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Dashboard", "dashboard", WebTabContext.Type.DASHBOARD, null, layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Reports","reports",WebTabContext.Type.REPORT,Arrays.asList(modBean.getModule("spacebooking").getModuleId()),layout.getApplicationId(), null));
            groupNameVsWebTabsMap.put("home", webTabs);


            webTabGroups.add(new WebTabGroupContext("Office", "office", layout.getId(), 29, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Floor Map", "floor-map", WebTabContext.Type.INDOOR_FLOORPLAN, null, layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Portfolio", "portfolio", WebTabContext.Type.CUSTOM, null, "{ \"type\": \"portfolio\" }", 1,null,layout.getApplicationId()));
            webTabs.add(new WebTabContext("Rooms", "rooms", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("rooms").getModuleId()), layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Desks", "desks", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("desks").getModuleId()), layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Parkings", "parkings", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("parkingstall").getModuleId()), layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Lockers", "lockers", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("lockers").getModuleId()), layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Amenity", "amenity", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("amenity").getModuleId()), layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Moves", "moves", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("moves").getModuleId()), layout.getApplicationId(), null));
            groupNameVsWebTabsMap.put("office", webTabs);


            webTabGroups.add(new WebTabGroupContext("Booking", "booking", layout.getId(), 31, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Bookings", "bookings", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("spacebooking").getModuleId()), layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Scheduler","booking-scheduler",WebTabContext.Type.TIMELINE,Arrays.asList(modBean.getModule("spacebooking").getModuleId()),layout.getApplicationId(), null));
            groupNameVsWebTabsMap.put("booking", webTabs);


            webTabGroups.add(new WebTabGroupContext("Employee Directory", "employee-directory", layout.getId(), 14, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Employees", "employee", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("employee").getModuleId()), layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Departments", "departments", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("department").getModuleId()), layout.getApplicationId(), null));
            groupNameVsWebTabsMap.put("employee-directory", webTabs);


            webTabGroups.add(new WebTabGroupContext("Visitors", "visitor", layout.getId(), 16, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Visits", "visits", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("visitorlog").getModuleId()), layout.getApplicationId(), null, AccountUtil.FeatureLicense.VISITOR.getFeatureId()));
            webTabs.add(new WebTabContext("Invites", "visitorinvites", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("invitevisitor").getModuleId()), layout.getApplicationId(), null,  AccountUtil.FeatureLicense.VISITOR.getFeatureId()));
            webTabs.add(new WebTabContext("Visitors", "visitors", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("visitor").getModuleId()), layout.getApplicationId(), null, AccountUtil.FeatureLicense.VISITOR.getFeatureId()));
            webTabs.add(new WebTabContext("Watchlist", "watchlist", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("watchlist").getModuleId()), layout.getApplicationId(), null, AccountUtil.FeatureLicense.VISITOR.getFeatureId()));
            groupNameVsWebTabsMap.put("visitor", webTabs);


            webTabGroups.add(new WebTabGroupContext("Deliveries", "deliveries", layout.getId(), 28, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Deliveries", "my-deliveries", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("deliveries").getModuleId()), layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Delivery Areas", "delivery-areas", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("deliveryArea").getModuleId()), layout.getApplicationId(), null));
            groupNameVsWebTabsMap.put("deliveries", webTabs);


            webTabGroups.add(new WebTabGroupContext("Help Center", "helpcenter", layout.getId(), 8, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Service Requests", "service-request", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("serviceRequest").getModuleId()), layout.getApplicationId(), null));
            webTabs.add(new WebTabContext("Documents", "documents", WebTabContext.Type.MODULE, Arrays.asList(modBean.getModule("admindocuments").getModuleId()), layout.getApplicationId(), null));
            groupNameVsWebTabsMap.put("helpcenter", webTabs);



            webTabGroups.add(new WebTabGroupContext("Analytics", "analytics", layout.getId(), 5, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Occupancy Analytics", "occupancy-analytics", WebTabContext.Type.CUSTOM, null, "{ \"type\": \"workplace-analytics\" }", 1,null,layout.getApplicationId()));
            groupNameVsWebTabsMap.put("analytics", webTabs);


            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if(CollectionUtils.isNotEmpty(tabs)){
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
            public static void addWebTab(WebTabContext webtab) throws Exception {

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).fields(FieldFactory.getWebTabFields());
        long tabId = builder.insert(FieldUtil.getAsProperties(webtab));
        webtab.setId(tabId);

    }

    public static void addDefaultAppDomains(long orgId) throws Exception {
        Organization org = AccountUtil.getOrgBean().getOrg(orgId);
        AppDomain servicePortalAppDomain = new AppDomain(
                org.getDomain() + "." + FacilioProperties.getOccupantAppDomain(),
                AppDomainType.SERVICE_PORTAL.getIndex(), GroupType.TENANT_OCCUPANT_PORTAL.getIndex(), orgId,
                AppDomain.DomainType.DEFAULT.getIndex());
        AppDomain vendorPortalAppDomain = new AppDomain(org.getDomain() + "." + FacilioProperties.getVendorAppDomain(),
                AppDomainType.VENDOR_PORTAL.getIndex(), GroupType.VENDOR_PORTAL.getIndex(), orgId,
                AppDomain.DomainType.DEFAULT.getIndex());
        AppDomain tenantPortalAppDomain = new AppDomain(org.getDomain() + "." + FacilioProperties.getTenantAppDomain(),
                AppDomainType.TENANT_PORTAL.getIndex(), GroupType.TENANT_OCCUPANT_PORTAL.getIndex(), orgId,
                AppDomain.DomainType.DEFAULT.getIndex());
        AppDomain clientPortalAppDomain = new AppDomain(org.getDomain() + "." + FacilioProperties.getClientAppDomain(),
                AppDomainType.CLIENT_PORTAL.getIndex(), GroupType.CLIENT_PORTAL.getIndex(), orgId,
                AppDomain.DomainType.DEFAULT.getIndex());
        AppDomain employeePortalAppDomain = new AppDomain(org.getDomain() + "." + FacilioProperties.getEmployeeAppDomain(), AppDomainType.EMPLOYEE_PORTAL.getIndex(), GroupType.EMPLOYEE_PORTAL.getIndex(), orgId, AppDomain.DomainType.DEFAULT.getIndex());


        List<AppDomain> appDomains = new ArrayList<AppDomain>();
        appDomains.add(servicePortalAppDomain);
        appDomains.add(vendorPortalAppDomain);
        appDomains.add(tenantPortalAppDomain);
        appDomains.add(clientPortalAppDomain);
        appDomains.add(employeePortalAppDomain);


        IAMAppUtil.addAppDomains(appDomains);
    }

    public static OrgUserApp checkIfUserAlreadyPresentInApp(long userId, long applicationId, long orgId)
            throws Exception {

        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(AccountConstants.getOrgUserAppsFields());
        fields.addAll(AccountConstants.getAppOrgUserFields());

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("ORG_User_Apps")
                .innerJoin("ORG_Users")
                .on("ORG_User_Apps.ORG_USERID = ORG_Users.ORG_USERID")
                .andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID", "applicationId",
                        String.valueOf(applicationId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ORG_Users.USERID", "userId", String.valueOf(userId),
                        NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
            if (CollectionUtils.isNotEmpty(props)) {
                Map<String, Object> map = props.get(0);
                return FieldUtil.getAsBeanFromMap(map, OrgUserApp.class);
            }
        }
        return null;

    }

    public static String getApplicationName(long appId) throws Exception {

        String appDomainName = "Facilio";
        if (appId > 0) {
            ApplicationContext app = getApplicationForId(appId);
            if (app != null) {
                return app.getName();
            }
        }
        return appDomainName;
    }

    public static String getPushNotificationKeyForApp(int appType) throws Exception {
        if (appType <= 0 || appType == 1) {
            return FacilioProperties.getPushNotificationKey();
        } else if (appType == 4) {
            return FacilioProperties.getPortalPushNotificationKey();
        } else {
            return FacilioProperties.getPortalPushNotificationKey();
        }
    }

    public static Map<Long, ScopingConfigContext> getScopingMapForApp(long scopingId) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        boolean isScopingEnabled = true;
        if(AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING)) {
            isScopingEnabled = false;
            ScopingContext scoping = ApplicationApi.getScoping(scopingId);
            if(scoping != null && scoping.isStatus()) {
                isScopingEnabled = true;
            }
        }
        if(isScopingEnabled) {
            List<ScopingConfigCacheContext> list = userScopeBean.getScopingConfig(scopingId);
            Map<Long, ScopingConfigContext> moduleScoping = new HashMap<>();
            if (CollectionUtils.isNotEmpty(list)) {
                for (ScopingConfigContext scopingConfig : list) {
                    moduleScoping.put(scopingConfig.getModuleId(), scopingConfig);
                }
                return moduleScoping;
            }
        }
        return null;
    }

    public static Map<Long, List<ScopingConfigContext>> migrateScopingMapForApp(long scopingId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getScopingConfigFields())
                .table("Scoping_Config")
                .andCondition(CriteriaAPI.getCondition("SCOPING_ID", "scopingId", String.valueOf(scopingId),
                        NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        Map<Long, List<ScopingConfigContext>> moduleScoping = new HashMap<Long, List<ScopingConfigContext>>();
        if (CollectionUtils.isNotEmpty(props)) {
            List<ScopingConfigContext> list = FieldUtil.getAsBeanListFromMapList(props, ScopingConfigContext.class);
            for (ScopingConfigContext scopingConfig : list) {
                List<ScopingConfigContext> ms = null;
                if (moduleScoping.containsKey(scopingConfig.getModuleId())) {
                    ms = moduleScoping.get(scopingConfig.getModuleId());
                } else {
                    ms = new ArrayList<>();
                }
                ms.add(scopingConfig);
                moduleScoping.put(scopingConfig.getModuleId(), ms);
            }
            return moduleScoping;
        }
        return null;
    }

    public static long updateCriteria(ScopingConfigContext sc, FacilioModule module) {
        Criteria criteria = new Criteria();
        Condition condition = CriteriaAPI.getCondition(sc.getFieldName(),
                StringUtils.isNotEmpty(sc.getValueGenerator()) ? sc.getValueGenerator() : sc.getValue(),
                StringUtils.isNotEmpty(sc.getValueGenerator()) ? ScopeOperator.SCOPING_IS
                        : Operator.getOperator((int) sc.getOperatorId()));
        condition.setModuleName(module.getName());
        criteria.addAndCondition(condition);
        try {
            return CriteriaAPI.addCriteria(criteria);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<FacilioField> computeValueForScopingField(ScopingConfigContext sc, FacilioModule module)
            throws Exception {
        if (sc != null) {
            if (sc.getCriteriaId() <= 0) {
                throw new IllegalArgumentException("Scoping Config --> must have criteria");
            }
            if (sc.getModuleId() <= 0) {
                throw new IllegalArgumentException("Scoping Config must have an associated module");
            }
            List<FacilioField> scopingFields = new ArrayList<>();
            Map<String, Condition> conditionMap = sc.getCriteria().getConditions();
            Map<String, Condition> nullConditionMap = new HashMap<String, Condition>();
            if (MapUtils.isNotEmpty(conditionMap)) {
                Iterator<Map.Entry<String, Condition>> itr = conditionMap.entrySet().iterator();
                Map<String, String> valueGenerators = null;
                if (MapUtils.isNotEmpty(AccountUtil.getValueGenerator())) {
                    valueGenerators = AccountUtil.getValueGenerator();
                } else {
                    valueGenerators = new HashMap<>();
                }
                boolean hasSiteField = false;
                while (itr.hasNext()) {
                    Map.Entry<String, Condition> entry = itr.next();
                    Condition condition = entry.getValue();
                    String fieldName = condition.getFieldName();
                    FacilioField field = RecordAPI.getField(fieldName, module.getName());
                    Condition nullCondition = new Condition();
                    if (field != null) {
                        boolean isCurrentFieldSite = false;
                        if (field.getName().equals("siteId")) {
                            hasSiteField = true;
                            isCurrentFieldSite = true;
                        }
                        if (!field.getDataTypeEnum().isRelRecordField()) {
                            scopingFields.add(field);
                        }
                        if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPE_VARIABLE)){
                            if (condition.getOperatorId() == ScopeOperator.SCOPING_IS.getOperatorId() || condition.getOperatorId() == 93) {
                                Class<? extends ValueGenerator> classObject = (Class<? extends ValueGenerator>) Class.forName(condition.getValue());
                                ValueGenerator valueGenerator = classObject.newInstance();
                                condition.setOperatorId(valueGenerator.getOperatorId());
                                condition.setColumnName(field.getCompleteColumnName());
                                String val = null;
                                if (valueGenerators.containsKey(condition.getValue())) {
                                    val = valueGenerators.get(condition.getValue());
                                } else {
                                    val = ScopeOperator.SCOPING_IS.getEvaluatedValues(valueGenerator);
                                    valueGenerators.put(condition.getValue(), val);
                                }
                                if (StringUtils.isNotEmpty(val)) {
                                    condition.setValue(val);
                                } else {
                                    condition.setValue("");
                                }
                            }
                        }
                        condition.setComputedWhereClause(null);
                        nullCondition = FieldUtil.cloneBean(condition, Condition.class);
                        if (isCurrentFieldSite) {
                            nullCondition.setOperatorId(CommonOperators.IS_EMPTY.getOperatorId());
                        }
                    }
                    nullConditionMap.put(entry.getKey(), nullCondition);
                }
                Long currentSiteId = (Long) AccountUtil.getSwitchScopingFieldValue("siteId");
                User currentUser = AccountUtil.getCurrentAccount().getUser();
                if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPE_VARIABLE)) {
                    if (!(currentSiteId != null && currentSiteId > 0) && hasSiteField && !nullConditionMap.isEmpty()
                            && AccountUtil.getCurrentApp().getAppCategory() != ApplicationContext.AppCategory.PORTALS
                            .getIndex()) {
                        if (CollectionUtils.isEmpty(currentUser.getAccessibleSpace())) {
                            Criteria nullCriteria = FieldUtil.cloneBean(sc.getCriteria(), Criteria.class);
                            nullCriteria.setConditions(nullConditionMap);
                            sc.getCriteria().orCriteria(nullCriteria);
                        }
                    }
                }
                AccountUtil.setValueGenerator(valueGenerators);
                return scopingFields;
            }
        }
        return null;
    }

    public static long getApplicationIdForApp(AppDomain appDomain) throws Exception {
        List<FacilioField> fields = FieldFactory.getApplicationFields();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(ModuleFactory.getApplicationModule().getTableName());
        selectBuilder.andCondition(CriteriaAPI.getCondition("DOMAIN_TYPE", "domainType",
                String.valueOf(appDomain.getAppDomainType()), NumberOperators.EQUALS));
        selectBuilder.orderBy("ID asc");
        List<Map<String, Object>> list = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            return (long) list.get(0).get("id");
        }
        return -1;
    }

    public static ApplicationContext getApplicationIdForAppDomain(String appDomain) throws Exception {

        AppDomain appDomainObj = IAMAppUtil.getAppDomain(appDomain);
        if (appDomainObj == null) {
            throw new IllegalArgumentException("Invalid app domain");
        }
        ApplicationContext app = getApplicationForId(ApplicationApi.getApplicationIdForApp(appDomainObj));
        app.setAppDomain(appDomainObj);
        return app;
    }

    public static List<ApplicationContext> getApplicationsForOrgUser(long ouId, String appDomain) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(FieldFactory.getApplicationFields());
        AppDomain appDomainObj = IAMAppUtil.getAppDomain(appDomain);
        if (appDomainObj == null) {
            throw new IllegalArgumentException("Invalid App Domain");
        }

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("ORG_User_Apps")
                .innerJoin(ModuleFactory.getApplicationModule().getTableName())
                .on("ORG_User_Apps.APPLICATION_ID = Application.ID")
                .andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ORG_USERID", "ouid", String.valueOf(ouId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("Application.DOMAIN_TYPE", "domainType",
                        String.valueOf(appDomainObj.getAppDomainType()), NumberOperators.EQUALS));
        ;

        selectBuilder.orderBy("ID asc");

        if(isRequestFromMobile()) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("LINK_NAME","linkName",FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,StringOperators.ISN_T));
        }

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<ApplicationContext> appsList = FieldUtil.getAsBeanListFromMapList(props, ApplicationContext.class);
            for (ApplicationContext app : appsList) {
                app.setAppDomain(appDomainObj);
            }
            return appsList;
        }
        return null;

    }

    private static boolean isRequestFromMobile() {
        return AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().isFromMobile() != null && AccountUtil.getCurrentAccount().isFromMobile();
    }

    public static List<ApplicationContext> getAllApplicationsWithOutFilter() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .select(FieldFactory.getApplicationFields());
        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);
        return applications;
    }

    public static List<ApplicationContext> getAllApplications() throws Exception {
        return getAllApplications(false);
    }
    public static List<ApplicationContext> getAllApplications(boolean skipFilter) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .select(FieldFactory.getApplicationFields());
        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);
        if(skipFilter){
            return applications;
        }
        return getFilteredApplications(applications);
    }

    public static List<ApplicationContext> getFilteredApplications(List<ApplicationContext> applications) throws Exception {
        List <ApplicationContext> apps = new ArrayList<>();

        applications.forEach(app -> {
            try {
                if(app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
                    if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WORKPLACE_APPS)) {
                        apps.add(app);
                    }
                }
                else if(app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.KIOSK_APP)) {
                    if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.KIOSK_APP_FORM)) {
                        apps.add(app);
                    }
                }
                else {
                    apps.add(app);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return apps;
    }

    public static List<ApplicationContext> getAllApplicationsForDomain(Integer domainType) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .select(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getCondition("DOMAIN_TYPE","domainType",String.valueOf(domainType),NumberOperators.EQUALS));
        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);
        return getFilteredApplications(applications);
    }

    public static void setThisAppForUser(User user, ApplicationContext app, boolean assignDefaultRole)
            throws Exception {
        AppDomain domain = app.getAppDomain();
        user.setAppDomain(domain);
        user.setApplicationId(app.getId());
        user.setAppType(domain.getAppType());

        // need to remove assign default role & scope when we start throwing error
        long roleId = -1;
        Long scopingId = null;
        ScopingContext scoping = null;
        if (!assignDefaultRole) {
            Map<String, Object> map = getRoleAndScopeForApp(app.getId(), user.getOuid());
            if (MapUtils.isNotEmpty(map)) {
                roleId = (long) map.get("roleId");
                if(map.containsKey("scopingId")) {
                    scopingId = (Long) map.get("scopingId");
                    if(scopingId != null && scopingId > -1) {
                        scoping = getScoping(scopingId);
                    }
                }
            }
        }
        if (roleId <= 0) {
            roleId = getPrivelegedRoleForApp(app.getId());
        }
        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING) && (scopingId == null || scopingId <= 0)) {
            scoping = getDefaultScopingForApp(app.getId());
            if(scoping != null) {
                scopingId = scoping.getId();
            }
        }
        user.setRoleId(roleId);
        user.setScopingId(scopingId);
        user.setScoping(scoping);
        Role role = AccountUtil.getRoleBean().getRole(user.getRoleId());
        user.setRole(role);

    }

    private static Map<String, Object> getRoleAndScopeForApp(Long appId, Long ouId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(AccountConstants.getOrgUserAppsModule().getTableName())
                .select(AccountConstants.getOrgUserAppsFields())
                .andCondition(
                        CriteriaAPI.getCondition("ORG_USERID", "ouId", String.valueOf(ouId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "appId", String.valueOf(appId),
                        NumberOperators.EQUALS));
        List<Map<String, Object>> map = builder.get();
        if (CollectionUtils.isNotEmpty(map)) {
            return map.get(0);
        }
        return null;

    }

    private static long getPrivelegedRoleForApp(Long appId) throws Exception {
        // handle after handling roles and app mapping
        return -1;

    }

    public static long addDefaultScoping(String linkName) throws Exception {

        if (StringUtils.isNotEmpty(linkName)) {
            long appId = getApplicationIdForLinkName(linkName);
            return addDefaultScoping(appId);
        }
        throw new IllegalArgumentException("Invalid application");
    }

    public static long addDefaultScoping(long appId) throws Exception {

        if (appId > 0) {
            ScopingContext scoping = getDefaultScopingForApp(appId);
            if (scoping != null) {
                return scoping.getId();
            }
            scoping = new ScopingContext();
            scoping.setScopeName("Default scoping for app - " + appId);
            scoping.setDescription("default scoping for app - " + appId);
            scoping.setApplicationId(appId);
            scoping.setIsDefault(true);
            long scopingId = addScoping(scoping);
            return scopingId;
        }

        throw new IllegalArgumentException("Invalid application");

    }

    public static ScopingContext getDefaultScopingForApp(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .select(FieldFactory.getScopingFields())
                .andCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", "true", BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                        NumberOperators.EQUALS))

        ;
        List<Map<String, Object>> map = builder.get();
        if (CollectionUtils.isNotEmpty(map)) {
            return FieldUtil.getAsBeanFromMap(map.get(0), ScopingContext.class);
        }

        return null;
    }

    public static ScopingContext getScoping(long scopingId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .select(FieldFactory.getScopingFields())
                .andCondition(CriteriaAPI.getIdCondition(scopingId, ModuleFactory.getScopingModule()))

        ;
        List<Map<String, Object>> map = builder.get();
        if (CollectionUtils.isNotEmpty(map)) {
            return FieldUtil.getAsBeanFromMap(map.get(0), ScopingContext.class);
        }

        return null;
    }

    public static List<ScopingContext> getScopingForApp(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .select(FieldFactory.getScopingFields());

        if (appId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                    NumberOperators.EQUALS));
        }
        List<Map<String, Object>> map = builder.get();
        if (CollectionUtils.isNotEmpty(map)) {
            return FieldUtil.getAsBeanListFromMapList(map, ScopingContext.class);
        }

        return null;
    }

    public static List<ScopingContext> getAllScoping() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .select(FieldFactory.getScopingFields());

        List<Map<String, Object>> map = builder.get();
        if (CollectionUtils.isNotEmpty(map)) {
            return FieldUtil.getAsBeanListFromMapList(map, ScopingContext.class);
        }

        return null;
    }

    public static List<ScopingConfigContext> getScopingConfigForModuleApp(long appId,long moduleId) throws Exception {
        List<FacilioField> selectFields = FieldFactory.getScopingConfigFields();
        selectFields.addAll(FieldFactory.getScopingFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .innerJoin(ModuleFactory.getScopingConfigModule().getTableName())
                .on("Scoping_Config.SCOPING_ID = Scoping.ID")
                .select(selectFields);

        if (appId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                    NumberOperators.EQUALS));
        }
        if (moduleId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId),
                    NumberOperators.EQUALS));
        }
        List<Map<String, Object>> map = builder.get();
        if (CollectionUtils.isNotEmpty(map)) {
            return FieldUtil.getAsBeanListFromMapList(map, ScopingConfigContext.class);
        }

        return null;
    }

    public static long addScoping(ScopingContext scoping) throws Exception {
        List<FacilioField> fields = FieldFactory.getScopingFields();

        scoping.setStatus(true);
        scoping.setCreatedTime(System.currentTimeMillis());
        scoping.setCreatedBy(AccountUtil.getCurrentUser().getOuid());
        scoping.setModifiedTime(System.currentTimeMillis());
        scoping.setModifiedBy(AccountUtil.getCurrentUser().getOuid());

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .fields(fields);

        Map<String, Object> map = FieldUtil.getAsProperties(scoping);
        insertBuilder.addRecord(map);
        insertBuilder.save();
        long scopingId = (long) map.get("id");
        scoping.setId(scopingId);
        return scopingId;
    }

    public static void updateCreatedByForDefaultScoping(User superAdminUser) throws Exception {
        List<FacilioField> fields = FieldFactory.getScopingFields();
        ScopingContext scoping = new ScopingContext();
        scoping.setCreatedTime(System.currentTimeMillis());
        scoping.setCreatedBy(superAdminUser.getOuid());
        scoping.setModifiedTime(System.currentTimeMillis());
        scoping.setModifiedBy(superAdminUser.getOuid());

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("CREATED_BY", "createdBy", "NULL", CommonOperators.IS_EMPTY))
                .fields(fields);
        Map<String, Object> map = FieldUtil.getAsProperties(scoping);
        updateRecordBuilder.update(map);
    }

    public static void addScopingConfigForApp(List<ScopingConfigContext> scoping) throws Exception {

        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        userScopeBean.addScopingConfigForApp(scoping);

    }

    public static void updateScopingForUser(Long scopingId, Long applicationId, Long ouid) throws Exception {

        List<FacilioField> fields = AccountConstants.getOrgUserAppsFields();

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table("ORG_User_Apps")
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(applicationId),
                        NumberOperators.EQUALS));

        Map<String, Object> map = new HashMap<>();
        map.put("scopingId", scopingId);
        builder.update(map);
    }

    public static List<OrgUserApp> getScopingsForUser(Long ouid, Long appId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(AccountConstants.getOrgUserAppsFields())
                .table(AccountConstants.getOrgUserAppsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserid", String.valueOf(ouid),
                        NumberOperators.EQUALS));

        if (appId != null) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId",
                    String.valueOf(appId), NumberOperators.EQUALS));
        }
        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            return FieldUtil.getAsBeanListFromMapList(props, OrgUserApp.class);
        }
        return null;
    }



    public static void deleteScoping(long scopingId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(scopingId), NumberOperators.EQUALS));
        int rows = builder.delete();

    }

    public static void addApplicationLayout(ApplicationLayoutContext layout) throws Exception {
        List<FacilioField> fields = FieldFactory.getApplicationLayoutFields();
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getApplicationLayoutModule().getTableName())
                .fields(fields);

        Map<String, Object> props = FieldUtil.getAsProperties(layout);

        long id = insertBuilder.insert(props);
        layout.setId(id);

    }

    public static boolean isAuthorisedApplication(List<ApplicationContext> permissibleAppsForThisDomain, Long appId) {
        List<Long> permissibleApplicationIds = permissibleAppsForThisDomain.stream().map(ApplicationContext::getId).collect(Collectors.toList());
        if(appId > 0 && !permissibleApplicationIds.contains(appId)) {
            return false;
        }
        return true;
    }

    public static List<ApplicationContext> getApplicationsForModule(String moduleName) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(FieldFactory.getApplicationFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .select(FieldFactory.getApplicationFields());
        if (!moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
            builder.andCondition(
                    CriteriaAPI.getCondition("Application.APPLICATION_NAME", "name", "Facilio", StringOperators.IS));
        }

        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);
        return applications;

    }

    public static WebTabContext getWebTabForApplication(long appId, String route) throws Exception {

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ROUTE", "route", route, StringOperators.IS));

        List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);
        if (CollectionUtils.isNotEmpty(webTabs)) {
            return webTabs.get(0);
        }

        return null;
    }

    public static ApplicationContext getDefaultOrFirstApp(List<ApplicationContext> applications,long ouid) throws Exception {
        ApplicationContext defaultAppForUser = getUserDefaultApp(applications,ouid);
        ApplicationContext finalDefaultApplication = null;
        for(ApplicationContext app : applications) {
            if(defaultAppForUser != null && defaultAppForUser.getId() == app.getId()){
                finalDefaultApplication = app;
                break;
            }
            if(app.isDefault()){
                finalDefaultApplication = app;
            }
        }
        if(finalDefaultApplication != null){
            return finalDefaultApplication;
        }
        return checkForMobileMaintenanceApp(applications);
    }

    //backward compatible for default app as maintenance for workQ mobile when there is no default app - can be removed after removing facilio main app from all orgs
    public static ApplicationContext checkForMobileMaintenanceApp(List<ApplicationContext> applications) {
        if(CollectionUtils.isNotEmpty(applications)) {
            if(isRequestFromMobile()) {
                for (ApplicationContext app : applications) {
                    if (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)) {
                        return app;
                    }
                }
            }
            return applications.get(0);
        }
        return null;
    }
    public static ApplicationContext getUserDefaultApp(List<ApplicationContext> permissibleApplications,long ouid) throws Exception {
        if(CollectionUtils.isNotEmpty(permissibleApplications) && ouid > -1){
            List<Long> appIds = permissibleApplications.stream().map(ApplicationContext::getId).collect(Collectors.toList());
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(AccountConstants.getOrgUserAppsFields())
                    .table(AccountConstants.getOrgUserAppsModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", StringUtils.join(appIds,","), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouid), NumberOperators.EQUALS));

            if(isRequestFromMobile()) {
                selectBuilder.andCondition(CriteriaAPI.getCondition("IS_DEFAULT_MOBILE_APP", "isDefaultMobileApp", String.valueOf(Boolean.TRUE), BooleanOperators.IS));
            } else {
                selectBuilder.andCondition(CriteriaAPI.getCondition("IS_DEFAULT_APP", "isDefaultApp", String.valueOf(Boolean.TRUE), BooleanOperators.IS));
            }

            List<Map<String, Object>> props = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(props)) {
                OrgUserApp userApp = FieldUtil.getAsBeanFromMap(props.get(0), OrgUserApp.class);
                if(userApp != null){
                    return ApplicationApi.getApplicationForId(userApp.getApplicationId());
                }
            }
        }
        return null;
    }

    public static List<ApplicationContext> getApplicationsContainsModule(String moduleName) throws Exception {

        List<String> moduleNamesList = Arrays.asList(FacilioConstants.ContextNames.SITE,
                FacilioConstants.ContextNames.BUILDING, FacilioConstants.ContextNames.FLOOR,
                FacilioConstants.ContextNames.SPACE);
        GenericSelectRecordBuilder tabBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName())
                .select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("TYPE", "type",
                        String.valueOf(WebTabContext.Type.CUSTOM.getIndex()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("CONFIG", "config", String.valueOf("portfolio"),
                        StringOperators.CONTAINS));
        List<Map<String, Object>> tabs = new ArrayList<>();
        if (moduleNamesList.contains(moduleName)) {
            tabs = tabBuilder.get();
        }

        List<Long> appIdsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tabs)) {
            List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(tabs, WebTabContext.class);
            appIdsList = webTabs.stream().map(WebTabContext::getApplicationId).collect(Collectors.toList());
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule moduleObj = modBean.getModule(moduleName);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                .select(FieldFactory.getTabIdAppIdMappingFields())
                .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleObj.getModuleId()),
                        NumberOperators.EQUALS));

        List<TabIdAppIdMappingContext> list = FieldUtil.getAsBeanListFromMapList(builder.get(),
                TabIdAppIdMappingContext.class);

        GenericSelectRecordBuilder appbuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .select(FieldFactory.getApplicationFields());

        if (CollectionUtils.isNotEmpty(list) || CollectionUtils.isNotEmpty(appIdsList)) {
            Set<Long> appIdList = new HashSet<>();
            if (CollectionUtils.isNotEmpty(list)) {
                appIdList = list.stream().map(TabIdAppIdMappingContext::getAppId).collect(Collectors.toSet());
            }
            if (CollectionUtils.isNotEmpty(appIdsList)) {
                appIdList.addAll(appIdsList);
            }
            appbuilder.andCondition(CriteriaAPI.getIdCondition(appIdList, ModuleFactory.getApplicationModule()));

        }
        appbuilder.orCondition(
                CriteriaAPI.getCondition("Application.APPLICATION_NAME", "name", "Facilio", StringOperators.IS));
        return getFilteredApplications(FieldUtil.getAsBeanListFromMapList(appbuilder.get(), ApplicationContext.class));
    }

    public static void addAppRoleMapping(long roleId, long appId) throws Exception {
        RoleApp roleApp = new RoleApp(appId, roleId);

        List<FacilioField> fields = AccountConstants.getRolesAppsFields();
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(AccountConstants.getRolesAppsModule().getTableName())
                .fields(fields);

        Map<String, Object> props = FieldUtil.getAsProperties(roleApp);

        insertBuilder.insert(props);

    }

    public static void updateRoleForUserInApp(Long ouId, Long appId, Long roleId) throws Exception {

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(AccountConstants.getOrgUserAppsModule().getTableName())
                .fields(Collections.singletonList(AccountConstants.getRoleIdField()))
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(ouId),
                        NumberOperators.EQUALS))

        ;
        Map<String, Object> map = new HashMap<>();
        map.put("roleId", roleId);
        builder.update(map);
    }

    public static void incrementLayoutVersionByIds(Collection<Long> layoutIds) throws Exception {
        List<ApplicationLayoutContext> layouts = getLayouts(layoutIds);
        incrementLayoutVersion(layouts);
    }

    public static void incrementLayoutVersion(List<ApplicationLayoutContext> layouts) throws Exception {
        if (CollectionUtils.isEmpty(layouts)) {
            return;
        }

        FacilioModule module = ModuleFactory.getApplicationLayoutModule();
        for (ApplicationLayoutContext layout : layouts) {
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(Collections.singletonList(
                            FieldFactory.getField("versionNumber", "VERSION_NUMBER", module, FieldType.NUMBER)))
                    .andCondition(CriteriaAPI.getIdCondition(layout.getId(), module));
            Map<String, Object> map = new HashMap<>();
            map.put("versionNumber", layout.getVersionNumber() + 1);
            builder.update(map);
        }
    }

    public static Map<Long, List<ScopingConfigContext>> getDelegatedUsersScopingMap(List<User> users) {
        Map<Long, List<ScopingConfigContext>> scopingMap = new HashMap<>();
        try {
            for (User user : users) {
                Map<Long, ScopingConfigContext> scope = ApplicationApi.getScopingMapForApp(user.getScopingId());
                if (MapUtils.isNotEmpty(scope)) {
                    for (Long key : scope.keySet()) {
                        if (scopingMap.containsKey(key)) {
                            scopingMap.get(key).add(scope.get(key));
                        } else {
                            scopingMap.put(key, Collections.singletonList(scope.get(key)));
                        }
                    }
                }
            }
            return scopingMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addSetupLayoutWebGroups(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();


            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;

            webTabGroups.add(new WebTabGroupContext("General", "general", layout.getId(), 200, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Organization", "organizationsettings", WebTabContext.Type.ORGANIZATION_SETTINGS, null, appId, null));
            webTabs.add(new WebTabContext("Company Profile", "companyprofile", WebTabContext.Type.COMPANY_PROFILE, null, appId, null));
            webTabs.add(new WebTabContext("Portals", "portal", WebTabContext.Type.PORTALS, null, appId, null));
            webTabs.add(new WebTabContext("Visitor Settings", "visitorsettings", WebTabContext.Type.VISITOR_SETTINGS, null, appId, null,AccountUtil.FeatureLicense.VISITOR.getFeatureId()));
            webTabs.add(new WebTabContext("Service Catalogs", "catalogs", WebTabContext.Type.SERVICE_CATALOGS, null, appId, null));
            webTabs.add(new WebTabContext("Tax", "tax", WebTabContext.Type.TAX, null, appId, null,AccountUtil.FeatureLicense.QUOTATION.getFeatureId()));
            webTabs.add(new WebTabContext("Operating Hours", "operatinghours", WebTabContext.Type.OPERATING_HOURS, null, appId, null));

            groupNameVsWebTabsMap.put("general", webTabs);


            webTabGroups.add(new WebTabGroupContext("Users and Access", "usersandaccess", layout.getId(), 202, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Users", "users", WebTabContext.Type.USERS, null, appId, null));
            webTabs.add(new WebTabContext("Roles", "roles", WebTabContext.Type.ROLES, null, appId, null));
            webTabs.add(new WebTabContext("Single Sign-On", "sso", WebTabContext.Type.SINGLE_SIGN_ON, null, appId, null));
            webTabs.add(new WebTabContext("Security Policy", "securitypolicy", WebTabContext.Type.SECURITY_POLICY, null, appId, null));
            webTabs.add(new WebTabContext("Scope", "scope", WebTabContext.Type.SCOPE, null, appId, null,AccountUtil.FeatureLicense.SCOPE_VARIABLE.getFeatureId()));
            webTabs.add(new WebTabContext("Data Sharing", "datasharing", WebTabContext.Type.DATA_SHARING, null, appId, null,AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING.getFeatureId()));
            groupNameVsWebTabsMap.put("usersandaccess", webTabs);


            webTabGroups.add(new WebTabGroupContext("Resources", "resources", layout.getId(), 201, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("People", "people", WebTabContext.Type.PEOPLE, null, appId, null));
            webTabs.add(new WebTabContext("Teams", "teams", WebTabContext.Type.TEAMS, null, appId, null));
            webTabs.add(new WebTabContext("Labor", "labor", WebTabContext.Type.LABOUR, null, appId, null,AccountUtil.FeatureLicense.RESOURCES.getFeatureId()));
            webTabs.add(new WebTabContext("Crafts", "crafts", WebTabContext.Type.CRAFTS, null, appId, null,AccountUtil.FeatureLicense.RESOURCES.getFeatureId()));
            groupNameVsWebTabsMap.put("resources", webTabs);


            webTabGroups.add(new WebTabGroupContext("Customization", "customization", layout.getId(), 1, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Modules", "modules", WebTabContext.Type.MODULES, null, appId, null));
            webTabs.add(new WebTabContext("Connected Apps", "connectedapps", WebTabContext.Type.CONNECTED_APPS, null, appId, null,AccountUtil.FeatureLicense.CONNECTEDAPPS.getFeatureId()));
            webTabs.add(new WebTabContext("Connectors", "connections", WebTabContext.Type.CONNECTORS, null, appId, null));
            webTabs.add(new WebTabContext("Functions", "functions", WebTabContext.Type.FUNCTIONS, null, appId, null));
            webTabs.add(new WebTabContext("Email Templates", "emailtemplates", WebTabContext.Type.EMAIL_TEMPLATES, null, appId, null));
            webTabs.add(new WebTabContext("Localization", "localization", WebTabContext.Type.LOCALIZATION, null, appId, null,AccountUtil.FeatureLicense.MULTI_LANGUAGE_TRANSLATION.getFeatureId()));
            webTabs.add(new WebTabContext("Tabs and Layouts", "tabsandlayouts", WebTabContext.Type.TABS_AND_LAYOUTS, null, appId, null));
            webTabs.add(new WebTabContext("Classifications","classifications",WebTabContext.Type.CLASSIFICATIONS,null,appId,null,AccountUtil.FeatureLicense.CLASSIFICATION.getFeatureId()));


            groupNameVsWebTabsMap.put("customization", webTabs);

            webTabGroups.add(new WebTabGroupContext("Automation", "automation", layout.getId(), 205, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Workflows", "workflows", WebTabContext.Type.WORKFLOWS, null, appId, null));
            webTabs.add(new WebTabContext("Notifications", "notifications", WebTabContext.Type.NOTIFICATIONS, null, appId, null));
            webTabs.add(new WebTabContext("Condition Manager", "conditionmanager", WebTabContext.Type.CONDITION_MANAGER, null, appId, null));
            webTabs.add(new WebTabContext("Scheduler", "scheduler", WebTabContext.Type.SCHEDULER, null, appId, null));
            webTabs.add(new WebTabContext("Variables", "variables", WebTabContext.Type.VARIABLES, null, appId, null));

            groupNameVsWebTabsMap.put("automation", webTabs);

            webTabGroups.add(new WebTabGroupContext("Automation Plus", "automationplus", layout.getId(), 206, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("SLA Policies", "slapolicies", WebTabContext.Type.SLA_POLICIES, null, appId, null));
            webTabs.add(new WebTabContext("Assignment Rules", "assignmentrules", WebTabContext.Type.ASSIGNMENT_RULES, null, appId, null));
            webTabs.add(new WebTabContext("BMS Event Filtering", "eventfilter", WebTabContext.Type.BMS_EVENT_FILTERING, null, appId, null,AccountUtil.FeatureLicense.NEW_READING_RULE.getFeatureId()));
            webTabs.add(new WebTabContext("Transaction Rules", "transactionrules", WebTabContext.Type.TRANSACTION_RULES, null, appId, null,AccountUtil.FeatureLicense.BUDGET_MONITORING.getFeatureId()));

            groupNameVsWebTabsMap.put("automationplus", webTabs);

            webTabGroups.add(new WebTabGroupContext("Process", "process", layout.getId(), 0, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Stateflows", "stateflows", WebTabContext.Type.STATEFLOWS, null, appId, null));
            webTabs.add(new WebTabContext("Approvals", "approvals", WebTabContext.Type.APPROVALS, null, appId, null,AccountUtil.FeatureLicense.APPROVAL.getFeatureId()));

            groupNameVsWebTabsMap.put("process", webTabs);

            webTabGroups.add(new WebTabGroupContext("Portfolio Settings", "portfoliosettings", layout.getId(), 204, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Readings", "readings", WebTabContext.Type.READINGS, null, appId, null));
            webTabs.add(new WebTabContext("Space Categories", "spacecategory", WebTabContext.Type.SPACE_CATEGORIES, null, appId, null));
            webTabs.add(new WebTabContext("Asset Customization", "assetcustomization", WebTabContext.Type.SPACE_ASSET_CUSTOMIZATION, null, appId, null));
            webTabs.add(new WebTabContext("Asset Depreciation", "depreciation", WebTabContext.Type.ASSET_DEPRECIATION, null, appId, null));
            webTabs.add(new WebTabContext("Weather Station", "weatherstation", WebTabContext.Type.WEATHER_STATION, null, appId, null,AccountUtil.FeatureLicense.WEATHER_INTEGRATION.getFeatureId()));
            webTabs.add(new WebTabContext("Decommission", "decommission", WebTabContext.Type.DECOMMISSION, null, appId, null,AccountUtil.FeatureLicense.COMMISSIONING.getFeatureId()));
            groupNameVsWebTabsMap.put("portfoliosettings", webTabs);

            webTabGroups.add(new WebTabGroupContext("Workorder Settings", "workordersettings", layout.getId(), 203, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Email Settings", "emailsettings", WebTabContext.Type.EMAIL_SETTINGS, null, appId, null));
            webTabs.add(new WebTabContext("Customization", "customization", WebTabContext.Type.WORKORDER_CUSTOMIZATION, null, appId, null));
            webTabs.add(new WebTabContext("Survey", "survey", WebTabContext.Type.SETUP_SURVEY, null, appId, null,AccountUtil.FeatureLicense.SURVEY.getFeatureId()));
            groupNameVsWebTabsMap.put("workordersettings", webTabs);

            webTabGroups.add(new WebTabGroupContext( "Logs","logs", layout.getId(),207, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Email Logs", "emaillogs", WebTabContext.Type.EMAIL_LOGS, null, appId, null,AccountUtil.FeatureLicense.EMAIL_TRACKING.getFeatureId()));
            webTabs.add(new WebTabContext("Audit Logs", "auditlogs", WebTabContext.Type.AUDIT_LOGS, null, appId, null));
            webTabs.add(new WebTabContext("Script Logs","scriptlogs", WebTabContext.Type.SCRIPT_LOGS,null,appId,null,AccountUtil.FeatureLicense.WORKFLOW_LOG.getFeatureId()));
            webTabs.add(new WebTabContext("Background Activity","backgroundactivity", WebTabContext.Type.BACKGROUND_ACTIVITY,null,appId,null,AccountUtil.FeatureLicense.BACKGROUND_ACTIVITY.getFeatureId()));
            webTabs.add(new WebTabContext("KPI Execution Logs", "readingkpilogs", WebTabContext.Type.KPI_EXEC_LOGS, null, appId, null, AccountUtil.FeatureLicense.NEW_KPI.getFeatureId()));
            webTabs.add(new WebTabContext("Rule Logs", "readingrulelogs", WebTabContext.Type.RULE_LOGS, null, appId, null, AccountUtil.FeatureLicense.NEW_READING_RULE.getFeatureId()));
            webTabs.add(new WebTabContext("Inbound Mail Conversion", "inboundmailconversion", WebTabContext.Type.INBOUND_MAIL_CONVERSION, null, appId, null));
            groupNameVsWebTabsMap.put("logs", webTabs);


            webTabGroups.add(new WebTabGroupContext( "Energy Analytics","energyanalytics", layout.getId(),2, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("KPI Templates","kpi", WebTabContext.Type.NEW_KPI_TEMPLATES,null,appId,null,AccountUtil.FeatureLicense.NEW_KPI.getFeatureId()));
            webTabs.add(new WebTabContext("Fault Impact","faultimpact", WebTabContext.Type.FAULT_IMPACT_TEMPLATE,null,appId,null));


            groupNameVsWebTabsMap.put("energyanalytics", webTabs);

            webTabGroups.add(new WebTabGroupContext( "Data Administration","dataadministration", layout.getId(),101, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Import Data","importdata", WebTabContext.Type.IMPORT_DATA,null,appId,null,AccountUtil.FeatureLicense.IMPORT_DATA.getFeatureId()));
            webTabs.add(new WebTabContext("OData Configuration", "odatasettings", WebTabContext.Type.ODATA_CONFIGURATION, null, appId, null,AccountUtil.FeatureLicense.ODATA_API.getFeatureId()));

            groupNameVsWebTabsMap.put("dataadministration", webTabs);


//            webTabGroups.add(new WebTabGroupContext("Developer Space", "developerspace", layout.getId(), 3, groupOrder++));
//            webTabs = new ArrayList<>();
//            webTabs.add(new WebTabContext("API Setup", "apisetup", WebTabContext.Type.API_SETUP, null, appId, null));
//
//            groupNameVsWebTabsMap.put("developerspace", webTabs);


            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if(CollectionUtils.isNotEmpty(tabs)){
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addCustomAppSetupLayoutWebGroups(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();


            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;

            webTabGroups.add(new WebTabGroupContext("General", "general", layout.getId(), 200, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Company Profile", "companyprofile", WebTabContext.Type.COMPANY_PROFILE, null, appId, null));

            groupNameVsWebTabsMap.put("general", webTabs);


            webTabGroups.add(new WebTabGroupContext("Users and Access", "usersandaccess", layout.getId(), 202, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Users", "users", WebTabContext.Type.USERS, null, appId, null));
            webTabs.add(new WebTabContext("Roles", "roles", WebTabContext.Type.ROLES, null, appId, null));
            webTabs.add(new WebTabContext("Single Sign-On", "sso", WebTabContext.Type.SINGLE_SIGN_ON, null, appId, null));
            webTabs.add(new WebTabContext("Security Policy", "securitypolicy", WebTabContext.Type.SECURITY_POLICY, null, appId, null));
            webTabs.add(new WebTabContext("Scope", "scope", WebTabContext.Type.SCOPE, null, appId, null, AccountUtil.FeatureLicense.SCOPE_VARIABLE.getFeatureId()));

            groupNameVsWebTabsMap.put("usersandaccess", webTabs);


            webTabGroups.add(new WebTabGroupContext("Customization", "customization", layout.getId(), 1, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Modules", "modules", WebTabContext.Type.MODULES, null, appId, null));
            webTabs.add(new WebTabContext("Connected Apps", "connectedapps", WebTabContext.Type.CONNECTED_APPS, null, appId, null, AccountUtil.FeatureLicense.CONNECTEDAPPS.getFeatureId()));
            webTabs.add(new WebTabContext("Connectors", "connections", WebTabContext.Type.CONNECTORS, null, appId, null));
            webTabs.add(new WebTabContext("Functions", "functions", WebTabContext.Type.FUNCTIONS, null, appId, null));
            webTabs.add(new WebTabContext("Email Templates", "emailtemplates", WebTabContext.Type.EMAIL_TEMPLATES, null, appId, null));
            webTabs.add(new WebTabContext("Localization", "localization", WebTabContext.Type.LOCALIZATION, null, appId, null, AccountUtil.FeatureLicense.MULTI_LANGUAGE_TRANSLATION.getFeatureId()));
            webTabs.add(new WebTabContext("Tabs and Layouts", "tabsandlayouts", WebTabContext.Type.TABS_AND_LAYOUTS, null, appId, null));
            webTabs.add(new WebTabContext("Classifications","classifications",WebTabContext.Type.CLASSIFICATIONS,null,appId,null,AccountUtil.FeatureLicense.CLASSIFICATION.getFeatureId()));


            groupNameVsWebTabsMap.put("customization", webTabs);

            webTabGroups.add(new WebTabGroupContext("Automation", "automation", layout.getId(), 205, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Workflows", "workflows", WebTabContext.Type.WORKFLOWS, null, appId, null));
            webTabs.add(new WebTabContext("Notifications", "notifications", WebTabContext.Type.NOTIFICATIONS, null, appId, null));
            webTabs.add(new WebTabContext("Condition Manager", "conditionmanager", WebTabContext.Type.CONDITION_MANAGER, null, appId, null));
            webTabs.add(new WebTabContext("Scheduler", "scheduler", WebTabContext.Type.SCHEDULER, null, appId, null));
            webTabs.add(new WebTabContext("Variables", "variables", WebTabContext.Type.VARIABLES, null, appId, null));

            groupNameVsWebTabsMap.put("automation", webTabs);

            webTabGroups.add(new WebTabGroupContext("Process", "process", layout.getId(), 0, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Stateflows", "stateflows", WebTabContext.Type.STATEFLOWS, null, appId, null));
            webTabs.add(new WebTabContext("Approvals", "approvals", WebTabContext.Type.APPROVALS, null, appId, null,AccountUtil.FeatureLicense.NEW_APPROVALS.getFeatureId()));

            groupNameVsWebTabsMap.put("process", webTabs);

            webTabGroups.add(new WebTabGroupContext( "Logs","logs", layout.getId(),207, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Email Logs", "emaillogs", WebTabContext.Type.EMAIL_LOGS, null, appId, null, AccountUtil.FeatureLicense.EMAIL_TRACKING.getFeatureId()));
            webTabs.add(new WebTabContext("Audit Logs", "auditlogs", WebTabContext.Type.AUDIT_LOGS, null, appId, null));
            webTabs.add(new WebTabContext("Script Logs","scriptlogs", WebTabContext.Type.SCRIPT_LOGS,null,appId,null,AccountUtil.FeatureLicense.WORKFLOW_LOG.getFeatureId()));

            groupNameVsWebTabsMap.put("logs", webTabs);

            webTabGroups.add(new WebTabGroupContext( "Data Administration","dataadministration", layout.getId(),101, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Import Data","importdata", WebTabContext.Type.IMPORT_DATA,null,appId,null,AccountUtil.FeatureLicense.IMPORT_DATA.getFeatureId()));

            groupNameVsWebTabsMap.put("dataadministration", webTabs);


            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if(CollectionUtils.isNotEmpty(tabs)){
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Exception occured, while creating setup module for Custom App",e);
        }

    }

    public static void addIWMSSetupLayoutWebGroups(ApplicationLayoutContext layout) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            int groupOrder = 1;

            long appId = layout.getApplicationId();

            List<WebTabGroupContext> webTabGroups = new ArrayList<>();
            Map<String, List<WebTabContext>> groupNameVsWebTabsMap = new HashMap<>();
            List<WebTabContext> webTabs = new ArrayList<>();
            JSONObject configJSON;

            webTabGroups.add(new WebTabGroupContext("General", "general", layout.getId(), 200, groupOrder++));
            webTabs = new ArrayList<>();
            //configJSON = new JSONObject();
            //configJSON.put("type", "portalOverview");
            webTabs.add(new WebTabContext("Organization Settings", "organizationsettings", WebTabContext.Type.ORGANIZATION_SETTINGS, null, appId, null, AccountUtil.FeatureLicense.MULTI_CURRENCY.getFeatureId()));
            webTabs.add(new WebTabContext("Company Profile", "companyprofile", WebTabContext.Type.COMPANY_PROFILE, null, appId, null));
            webTabs.add(new WebTabContext("Portals", "portal", WebTabContext.Type.PORTALS, null, appId, null));
            webTabs.add(new WebTabContext("Visitor Settings", "visitorsettings", WebTabContext.Type.VISITOR_SETTINGS, null, appId, null));
            webTabs.add(new WebTabContext("Feedback & Complaints", "feedbacksettings", WebTabContext.Type.FEEDBACK_COMPLAINTS, null, appId, null));
            webTabs.add(new WebTabContext("Smart Controls", "smartcontrolsettings", WebTabContext.Type.SMART_CONTROLS, null, appId, null));
            webTabs.add(new WebTabContext("Service Catalogs", "catalogs", WebTabContext.Type.SERVICE_CATALOGS, null, appId, null));
            webTabs.add(new WebTabContext("Tax", "tax", WebTabContext.Type.TAX, null, appId, null));

            groupNameVsWebTabsMap.put("general", webTabs);


            webTabGroups.add(new WebTabGroupContext("Users Management", "resource", layout.getId(), 201, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Users", "users", WebTabContext.Type.USERS, null, appId, null));
            webTabs.add(new WebTabContext("Teams", "teams", WebTabContext.Type.TEAMS, null, appId, null));
            webTabs.add(new WebTabContext("Roles", "roles", WebTabContext.Type.ROLES, null, appId, null));
            webTabs.add(new WebTabContext("Labor", "labor", WebTabContext.Type.LABOUR, null, appId, null));
            webTabs.add(new WebTabContext("Crafts", "crafts", WebTabContext.Type.CRAFTS, null, appId, null));
            webTabs.add(new WebTabContext("People", "people", WebTabContext.Type.PEOPLE, null, appId, null));

            groupNameVsWebTabsMap.put("resource", webTabs);


            webTabGroups.add(new WebTabGroupContext("Security Settings", "security", layout.getId(), 202, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Single Sign-On", "sso", WebTabContext.Type.SINGLE_SIGN_ON, null, appId, null));
            webTabs.add(new WebTabContext("Security Policy", "securitypolicy", WebTabContext.Type.SECURITY_POLICY, null, appId, null));

            groupNameVsWebTabsMap.put("security", webTabs);


            webTabGroups.add(new WebTabGroupContext("Space Settings", "spacesettings", layout.getId(), 204, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Space categories", "spacecategory", WebTabContext.Type.SPACE_CATEGORIES, null, appId, null));
            webTabs.add(new WebTabContext("Operating Hours", "operatinghours", WebTabContext.Type.OPERATING_HOURS, null, appId, null));

            groupNameVsWebTabsMap.put("spacesettings", webTabs);

            webTabGroups.add(new WebTabGroupContext("Automation", "automations", layout.getId(), 205, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Workflows", "workflows", WebTabContext.Type.WORKFLOWS, null, appId, null));
            webTabs.add(new WebTabContext("Notifications", "notifications", WebTabContext.Type.NOTIFICATIONS, null, appId, null));
            webTabs.add(new WebTabContext("Condition Manager", "conditionmanager", WebTabContext.Type.CONDITION_MANAGER, null, appId, null));
            webTabs.add(new WebTabContext("Scheduler", "scheduler", WebTabContext.Type.SCHEDULER, null, appId, null));
            webTabs.add(new WebTabContext("Variables", "variables", WebTabContext.Type.VARIABLES, null, appId, null));

            groupNameVsWebTabsMap.put("automations", webTabs);

            webTabGroups.add(new WebTabGroupContext("Process", "process", layout.getId(), 0, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Stateflows", "stateflows", WebTabContext.Type.STATEFLOWS, null, appId, null));
            webTabs.add(new WebTabContext("Approvals", "approvals", WebTabContext.Type.APPROVALS, null, appId, null));

            groupNameVsWebTabsMap.put("process", webTabs);

            webTabGroups.add(new WebTabGroupContext("Customization", "customization", layout.getId(), 1, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Modules", "modules", WebTabContext.Type.MODULES, null, appId, null));
            webTabs.add(new WebTabContext("Connected Apps", "connectedapps", WebTabContext.Type.CONNECTED_APPS, null, appId, null));
            webTabs.add(new WebTabContext("Connectors", "connections", WebTabContext.Type.CONNECTORS, null, appId, null));
            webTabs.add(new WebTabContext("Functions", "functions", WebTabContext.Type.FUNCTIONS, null, appId, null));
            webTabs.add(new WebTabContext("Email Templates", "emailtemplates", WebTabContext.Type.EMAIL_TEMPLATES, null, appId, null));
            webTabs.add(new WebTabContext("Localization", "localization", WebTabContext.Type.LOCALIZATION, null, appId, null));
            webTabs.add(new WebTabContext("Classifications","classifications",WebTabContext.Type.CLASSIFICATIONS,null,appId,null,AccountUtil.FeatureLicense.CLASSIFICATION.getFeatureId()));
            groupNameVsWebTabsMap.put("customization", webTabs);


            webTabGroups.add(new WebTabGroupContext( "Logs","logs", layout.getId(),207, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Email Logs", "emaillogs", WebTabContext.Type.EMAIL_LOGS, null, appId, null));
            webTabs.add(new WebTabContext("Audit Logs", "auditlogs", WebTabContext.Type.AUDIT_LOGS, null, appId, null));

            groupNameVsWebTabsMap.put("logs", webTabs);

            webTabGroups.add(new WebTabGroupContext( "Data Administration","dataadministration", layout.getId(),101, groupOrder++));
            webTabs = new ArrayList<>();
            webTabs.add(new WebTabContext("Import Data","importdata", WebTabContext.Type.IMPORT_DATA,null,appId,null,AccountUtil.FeatureLicense.IMPORT_DATA.getFeatureId()));

            groupNameVsWebTabsMap.put("dataadministration", webTabs);



            for (WebTabGroupContext webTabGroupContext : webTabGroups) {
                System.out.println("we: " + webTabGroupContext.getRoute());
                FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
                FacilioContext chainContext = chain.getContext();
                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
                chain.execute();
                long webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
                webTabGroupContext.setId(webGroupId);
                List<WebTabContext> tabs = groupNameVsWebTabsMap.get(webTabGroupContext.getRoute());
                for (WebTabContext webTabContext : tabs) {
                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
                    chain.execute();
                    long tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
                    webTabContext.setId(tabId);
                }
                if(CollectionUtils.isNotEmpty(tabs)){
                    chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
                    chainContext = chain.getContext();
                    chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
                    chain.execute();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Map<String, String> evaluateValueGenerators() throws Exception {

        Map<String, String> valueGeneratorMap = new HashMap<>();
        Reflections reflections = new Reflections("com.facilio.modules");
        Set<Class<? extends ValueGenerator>> classes = reflections.getSubTypesOf(ValueGenerator.class);
        if (CollectionUtils.isNotEmpty(classes)) {
            for (Class<? extends ValueGenerator> vg : classes) {
                ValueGenerator obj = vg.newInstance();
                String val = ScopeOperator.SCOPING_IS.getEvaluatedValues(obj);
                valueGeneratorMap.put(obj.getLinkName(), val);
            }
            return valueGeneratorMap;
        }
        return null;
    }

    public static List<User> getUsersList(Map<String, Object> paramsMap) throws Exception {
        int page = 0, perPage = 5000, offset = 0;
        List<Long> teamIds = new ArrayList<>();
        List<Long> applicationIds = new ArrayList<>();
        String search = null;
        String orderBy = null;
        String orderType = null;
        Long _default = null;
        List<Long> defaultIdList = new ArrayList<>();
        Criteria serverCriteria = null;
        if (!paramsMap.isEmpty()) {
            page = (int) paramsMap.get("page");
            perPage = (int) paramsMap.get("perPage");
            search = (String) paramsMap.get("search");
            offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            if (paramsMap.containsKey("_default")) {
                String defaultIds = (String) paramsMap.get("_default");
                if (StringUtils.isNotEmpty(defaultIds)) {
                    String[] ids = FacilioUtil.splitByComma(defaultIds);
                    defaultIdList = Arrays.stream(ids).map(Long::parseLong).collect(Collectors.toList());
                }
            }
            if (paramsMap.containsKey("filters")) {
                JSONObject filters = (JSONObject) paramsMap.get("filters");
                if (filters.containsKey("teamId")) {
                    JSONObject values = (JSONObject) filters.get("teamId");
                    if (values.containsKey("value")) {
                        teamIds = (List<Long>) values.get("value");
                    }
                }
                if (filters.containsKey("applicationId")) {
                    JSONObject values = (JSONObject) filters.get("applicationId");
                    if (values.containsKey("value")) {
                        applicationIds = (List<Long>) values.get("value");
                    }
                }
            }
            if (paramsMap.containsKey("serverCriteria")) {
                serverCriteria = (Criteria) paramsMap.get("serverCriteria");
            }
            if (paramsMap.containsKey("orderBy")) {
                FacilioField nameField = Constants.getModBean().getField("name", FacilioConstants.ContextNames.PEOPLE);
                orderBy = nameField.getCompleteColumnName();
                orderType = (String) paramsMap.get("orderType");
            }
        }
        if (CollectionUtils.isEmpty(applicationIds)) {
            ApplicationContext maintenanceApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            if(maintenanceApp == null) {
                maintenanceApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            }
            List<ApplicationContext> allApps = ApplicationApi.getAllApplicationsForDomain(maintenanceApp.getDomainType());
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(allApps)) {
                applicationIds = allApps.stream().map(ApplicationContext::getId).collect(Collectors.toList());
           }
        }
        //TODO - Send criteria object here to filter users in picklist api
        List<User> users = AccountUtil.getOrgBean().getAppUsers(AccountUtil.getCurrentOrg().getOrgId(), -1, -1, false,
                false, offset, perPage, search, true, true, teamIds, applicationIds, defaultIdList, serverCriteria, orderBy, orderType);
        if (CollectionUtils.isNotEmpty(users)) {
            return users;
        }
        return null;
    }

    public static List<User> getRequesterList(long orgId, long appId, Map<String, Object> paramsMap) throws Exception {
        int page = 0, perPage = 5000, offset = 0;
        String search = null;
        String orderBy = null;
        String orderType = null;
        if (!paramsMap.isEmpty()) {
            page = (int) paramsMap.get("page");
            perPage = (int) paramsMap.get("perPage");
            search = (String) paramsMap.get("search");
            offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            if (paramsMap.containsKey("orderBy")) {
                orderBy = (String) paramsMap.get("orderBy");
                orderType = (String) paramsMap.get("orderType");
            }
        }

        List<User> users = AccountUtil.getOrgBean().getAppUsers(orgId,  appId,  -1,  false,  false,  offset, perPage, search,
                null, null, null, null, null, orderBy, orderType);
        if (CollectionUtils.isNotEmpty(users)) {
            return users;
        }
        return null;
    }

    public static User addSuperAdminToNewApp(long orgId, long appId, Role kioskAdmin) throws Exception {
        User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(orgId);
        superAdmin.setApplicationId(appId);
        superAdmin.setRole(kioskAdmin);
        superAdmin.setRoleId(kioskAdmin.getRoleId());
        AccountUtil.getUserBean().addToORGUsersApps(superAdmin, false);
        return superAdmin;
    }

    public static List<ApplicationContext> getLicensedPortalApps() throws Exception {

        List<FacilioField> allFields = FieldFactory.getApplicationFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .select(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("appCategory"), String.valueOf(ApplicationContext.AppCategory.PORTALS.getIndex()), NumberOperators.EQUALS));

        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(), ApplicationContext.class);
        List<ApplicationContext> portalApplications = new ArrayList<>();

        if (applications != null) {
            for (ApplicationContext portal : applications) {
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VENDOR) && portal.getLinkName().equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) {
                    portalApplications.add(portal);
                } else if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS) && portal.getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP)) {
                    portalApplications.add(portal);
                } else if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WORKPLACE_APPS) && portal.getLinkName().equals(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
                    portalApplications.add(portal);
                }
                else if (portal.getLinkName().equals(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP)) {
                    portalApplications.add(portal);
                }
                else if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLIENT_PORTAL) && portal.getLinkName().equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)) {
                    portalApplications.add(portal);
                }
            }
        }
        return portalApplications;
    }

    public static List<OrgUserApp> getApplicationListForUser(long ouId, AppDomain appDomainObj) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(AccountConstants.getOrgUserAppsFields());

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("ORG_User_Apps")
                .innerJoin(ModuleFactory.getApplicationModule().getTableName())
                .on("ORG_User_Apps.APPLICATION_ID = Application.ID")
                .andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ORG_USERID", "ouid", String.valueOf(ouId),
                        NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("Application.DOMAIN_TYPE", "domainType",
                        String.valueOf(appDomainObj.getAppDomainType()), NumberOperators.EQUALS));
        ;

        selectBuilder.orderBy("ID asc");

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<OrgUserApp> appsList = FieldUtil.getAsBeanListFromMapList(props, OrgUserApp.class);
            for (OrgUserApp app : appsList) {
                app.setApplication(ApplicationApi.getApplicationForId(app.getApplicationId()));
                app.setRole(AccountUtil.getRoleBean().getRole(app.getRoleId()));
            }
            return appsList;
        }
        return null;

    }

    public static void setApplicationDomain(List<ApplicationContext> relatedApplication) throws Exception {
        if (CollectionUtils.isNotEmpty(relatedApplication)) {
            Map<Integer, AppDomain> appDomainForTypeMap = new HashMap<>();
            for (ApplicationContext app : relatedApplication) {
                if (!appDomainForTypeMap.containsKey(app.getDomainType())) {
                    List<AppDomain> appDomainList = IAMAppUtil.getAppDomainForType(app.getDomainType(), AccountUtil.getCurrentOrg().getOrgId());
                    if (CollectionUtils.isNotEmpty(appDomainList)) {
                        for (AppDomain domain : appDomainList) {
                            if (domain.getDomainTypeEnum() == AppDomain.DomainType.CUSTOM) {
                                appDomainForTypeMap.put(app.getDomainType(), domain);
                                break;
                            }
                        }
                        if (!appDomainForTypeMap.containsKey(app.getDomainType())) {
                            appDomainForTypeMap.put(app.getDomainType(), appDomainList.get(0));
                        }
                    }
                }
                app.setAppDomain(appDomainForTypeMap.get(app.getDomainType()));
            }
        }
    }
    public static void addRelatedApplications(List<ApplicationRelatedAppsContext> relatedApplications) throws Exception {
        List<Map<String, Object>> props = FieldUtil.getAsMapList(relatedApplications, ApplicationRelatedAppsContext.class);
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getApplicationRelatedAppsModule().getTableName())
                .fields(FieldFactory.getApplicationRelatedAppsModuleFields());
        builder.addRecords(props);
        builder.save();
    }

    public static List<ApplicationContext> getRelatedApplications(Long appId) throws Exception {
        List<ApplicationContext> relatedApplication = null;
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getApplicationFields())
                .table(ModuleFactory.getApplicationRelatedAppsModule().getTableName())
                .innerJoin(ModuleFactory.getApplicationModule().getTableName())
                .on("Application.ID=Application_Related_Apps.RELATED_APPLICATION_ID")
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID","applicationId",String.valueOf(appId),NumberOperators.EQUALS));
        relatedApplication=FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), ApplicationContext.class);
        return relatedApplication;
    }
}
