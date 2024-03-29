package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.webtab.ModuleTypeHandler;
import com.facilio.bmsconsole.context.webtab.SetupTypeHandler;
import com.facilio.bmsconsole.context.webtab.WebTabHandler;
import com.facilio.bmsconsoleV3.util.APIPermissionUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.opensymphony.xwork2.ActionContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.reflections.Reflections;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Log4j
public class WebTabUtil {

    private static final Map<String, Class<? extends WebTabHandler>> WEB_TAB_HANDLER_MAP = new HashMap<>();

    public static void initialize() throws IOException {
        try {
            Reflections reflections = new Reflections("com.facilio.bmsconsole.context.webtab");
            Set<Class<? extends WebTabHandler>> webTabHandlerClasses = reflections.getSubTypesOf(WebTabHandler.class);
            if (CollectionUtils.isNotEmpty(webTabHandlerClasses)) {
                for (Class<? extends WebTabHandler> webTabHandler : webTabHandlerClasses) {
                    WEB_TAB_HANDLER_MAP.put(webTabHandler.getSimpleName(), webTabHandler);
                }
            }
        } catch (Exception e) {
            LOGGER.info(e);
        }
    }

    public static WebTabHandler getWebTabHandler(WebTabContext tab) {
        return tab.getTypeEnum().getHandler();
    }

    public static boolean checkPermission(HttpParameters parameters, String action, long tabId, Map<String, String> extraParameters) throws Exception {
        WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
        WebTabContext tab = tabBean.getWebTab(tabId);
        if (tab == null || StringUtils.isEmpty(action)) {
            return false;
        }
        WebTabHandler handler = getWebTabHandler(tab);
        Map<String, String> params = new HashMap<>();
        List<String> keys = FacilioConstants.HTTPParameter.KEYS;
        if (parameters != null) {
            for (String key : keys) {
                if (parameters.containsKey(key)) {
                    params.put(key, parameters.get(key).getValue());
                }
            }
        }
        if (MapUtils.isNotEmpty(extraParameters)) {
            params.putAll(extraParameters);
        }

        boolean isMatchedTabType = handler instanceof ModuleTypeHandler || handler instanceof SetupTypeHandler;
        if (params.containsKey("skipPermission") && Boolean.parseBoolean(params.get("skipPermission"))) {
            return true;
        } else if (!(isMatchedTabType)) {
            if (params.containsKey("tabType") && StringUtils.isNotEmpty(params.get("tabType"))) {
                List<String> tabType = Arrays.asList(params.get("tabType").split(","));
                isMatchedTabType = tab.getTypeEnum().getTabType().equals(WebTabContext.TabType.NORMAL) && tabType.contains(tab.getTypeEnum().getName());
            }
        } else if (params.containsKey("handler") && StringUtils.isNotEmpty(params.get("handler"))) {
            WebTabHandler formTypeHandler = WEB_TAB_HANDLER_MAP.get(params.get("handler")).newInstance();
            return formTypeHandler.hasPermission(tab, params, action);
        }

        if (handler != null && isMatchedTabType) {
            return handler.hasPermission(tab, params, action);
        }
        LOGGER.info("WebTabHandler is missing for this TabType of TabId - " + tab.getId());
        return false;
    }

    public static String getSpecialModule(String moduleName) {
        if (moduleName.equals("site") || moduleName.equals("building") || moduleName.equals("floor") || moduleName.equals("basespace")) {
            return "space";
        }
        if (moduleName.equals(FacilioConstants.ContextNames.TICKET)) {
            return FacilioConstants.ContextNames.WORK_ORDER;
        }
        return moduleName;
    }

    private static Long getPortfolioTabId(ApplicationContext currentApp) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(currentApp.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("CONFIG", "config", "portfolio", StringOperators.CONTAINS));
        Map<String, Object> prop = builder.fetchFirst();
        if (MapUtils.isNotEmpty(prop)) {
            WebTabContext tab = FieldUtil.getAsBeanFromMap(prop, WebTabContext.class);
            if (tab != null) {
                return tab.getId();
            }
        }
        return null;
    }

    public static boolean isPortfolioTab(WebTabContext webTabContext) {
        if (webTabContext != null && webTabContext.getConfig() != null && webTabContext.getConfig().contains("portfolio")) {
            return true;
        }
        return false;
    }

    public static boolean isNewPortfolioTab(WebTabContext webTabContext) {
        if (webTabContext != null && webTabContext.getTypeEnum().equals(WebTabContext.Type.PORTFOLIO)) {
            return true;
        }
        return false;
    }

    public static boolean currentUserHasPermission(long tabId, String action) {
        return currentUserHasPermission(tabId, action, AccountUtil.getCurrentUser().getRole());
    }

    public static boolean currentUserHasPermission(long tabId, String action, Role role) {

        try {
            if (role.isPrevileged()) {
                return true;
            }
            long rolePermissionVal = ApplicationApi.getRolesPermissionValForTab(tabId, role.getRoleId());
            boolean hasPerm = PermissionUtil.hasPermission(rolePermissionVal, action, tabId);
            return hasPerm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Parameter getActions(Parameter action, String method) throws Exception {
        Parameter v3PermissionActions = action;
        action = null;
        String actions = v3PermissionActions.toString();
        List<String> actionList = Arrays.asList(actions.split(";"));
        for (String act : actionList) {
            String[] actList = act.split(":");
            if (actList[0].equals(method)) {
                action = getActionParam(actList[1]);
                break;
            } else if (isSetupAPI() && actList[0].equals("ALL")) {
                action = getActionParam(actList[0]);
                break;
            }
        }
        return action;
    }

    public static boolean isAuthorizedAccess(String moduleName, String action) throws Exception {
        return isAuthorizedAccess(moduleName, action, null, null);
    }

    public static boolean isAuthorizedAccess(String moduleName, String action, Map<String, String> parameters, Long webTabId) throws Exception {

        if (AccountUtil.getCurrentUser() == null) {
            return false;
        }

        if (AccountUtil.getCurrentUser().getRoleId() <= 0 || AccountUtil.getCurrentUser().getRole() == null) {
            return true;
        }

        Role role = AccountUtil.getCurrentUser().getRole();

        //allowing all access to privileged roles of all apps
        if (role.isPrevileged()) {
            return true;
        }

        if (V3PermissionUtil.isWhitelistedModule(moduleName)) {
            return true;
        }
        HttpServletRequest request = ServletActionContext.getRequest();
        if (request.getRequestURI() != null && !APIPermissionUtil.shouldCheckPermission(request.getRequestURI())) {
            return true;
        }
        try {
            long tabId = V3PermissionUtil.getCurrentTabId();

            if (webTabId != null && webTabId > 0) {
                tabId = webTabId;
            }

            if (tabId > 0) {
                if (!WebTabUtil.checkPermission(ActionContext.getContext().getParameters(), action, tabId, parameters)) {
                    permissionLogsForModule();
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Permission interceptor error occured tab");
        }
        return AccountUtil.getCurrentOrg() == null;
    }


    private static void permissionLogsForTabs(Long tabId, String moduleName, String action) throws Exception {
        try {
            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            WebTabContext tab = tabBean.getWebTab(tabId);
            if (tabId == -1) {
                LOGGER.info("Tab id is -1");
            } else if (tab != null) {
                ApplicationContext app = ApplicationApi.getApplicationForId(tab.getApplicationId());
                List<String> modulesList = ApplicationApi.getModulesForTab(tabId);
                String roleName = AccountUtil.getCurrentUser().getRole().getName();
                String commonString = " Role Name - " + roleName + " - Action " + action + " - referrer - " + getReferrerUri();

                if (app == null) {
                    LOGGER.info("scope interceptor tab permission Case 1 : Application is empty - tab name" + tab.getName() + commonString);
                }
                if (CollectionUtils.isEmpty(modulesList)) {
                    LOGGER.info("scope interceptor tab permission Case 2 : Module for tab is empty - Tab name - " + tab.getName() + commonString);
                }
                if (modulesList.contains(moduleName)) {
                    LOGGER.info("scope interceptor tab permission Case 3 : No permission - Tab config exists - Module exist - Application " + app.getLinkName() + "- Tab Name - " + tab.getName() + " - Module Name - " + moduleName + " - " + commonString);
                } else {
                    LOGGER.info("scope interceptor tab permission Case 4 : No permission - Tab config exists - Module not exist - Application " + app.getLinkName() + "- Tab Name - " + tab.getName() + " - Module Name - " + moduleName + " - " + commonString);
                }
            } else {
                LOGGER.info("scope interceptor tab permission Case 5 : Tab not configured - no permission - Tab Id - " + tabId);
            }
        } catch (Exception e) {
            LOGGER.info("Exception in tab permission logs - " + e);
        }
    }

    public static void permissionLogsForModule() {
        try {
            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            WebTabContext tab = tabBean.getWebTab(V3PermissionUtil.getCurrentTabId());
            String tabName = tab != null ? tab.getName() : null;

            //Handled in client - need to check after logs
            LOGGER.info("Permission interceptor Tab permission missing for : Tab - " + tabName + " reqURI - " + ServletActionContext.getRequest().getRequestURI() + " referrerURI - " + getReferrerUri() + " DeviceType - " + ServletActionContext.getRequest().getHeader("X-Device-Type"));
        } catch (Exception e) {
            LOGGER.info("Exception in module permission logs - " + e);
        }
    }

    private static String getReferrerUri() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String referrer = request.getHeader(HttpHeaders.REFERER);
        if (referrer != null && !"".equals(referrer.trim())) {
            URL url = new URL(referrer);
            if (url != null) {
                return url.getPath();
            }
        }
        return "";
    }

    private static Parameter getActionParam(String action) {
        return new Parameter.Request(FacilioConstants.ContextNames.ACTION, action);
    }

    public static Long getTabForTabType(String tabTypeEnum) throws Exception {
        if (StringUtils.isNotEmpty(tabTypeEnum)) {
            WebTabContext.Type type = WebTabContext.Type.valueOf(tabTypeEnum.toUpperCase());
            if (type != null && AccountUtil.getCurrentApp() != null) {
                return getTabIdForType(type, AccountUtil.getCurrentApp());
            }
        }
        return null;
    }

    private static Long getTabIdForType(WebTabContext.Type type, ApplicationContext currentApp) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(currentApp.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(type.getIndex()), NumberOperators.EQUALS));
        Map<String, Object> prop = builder.fetchFirst();
        if (MapUtils.isNotEmpty(prop)) {
            WebTabContext tab = FieldUtil.getAsBeanFromMap(prop, WebTabContext.class);
            if (tab != null) {
                return tab.getId();
            }
        }
        return null;
    }

    public static boolean isAttachmentAPIAccessible() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        long currentTabId = V3PermissionUtil.getCurrentTabId();
        ApplicationContext currentApp = AccountUtil.getCurrentApp();

        if (currentTabId > 0 && currentApp != null) {
            String token = request.getHeader("q");
            Map<String, String> decodedClaims = FileJWTUtil.validateJWT(token);

            if (MapUtils.isNotEmpty(decodedClaims) && decodedClaims.containsKey("moduleId")) {
                Long moduleId = Long.valueOf(decodedClaims.get("moduleId"));
                ModuleBean modBean = Constants.getModBean();
                FacilioModule module = modBean.getModule(moduleId);
                List<Long> tabIds = ApplicationApi.getTabForModules(currentApp.getId(), module.getModuleId());

                if (CollectionUtils.isNotEmpty(tabIds) && tabIds.contains(currentTabId)) {
                    return V3PermissionUtil.currentUserHasPermission(currentTabId, FacilioConstants.ContextNames.READ_PERMISSIONS);
                }
            }
        }
        return false;
    }

    public static boolean isSetupAPI() throws Exception {
        WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");

        long tabId = V3PermissionUtil.getCurrentTabId();
        if (tabId > 0) {
            WebTabContext tab = tabBean.getWebTab(tabId);
            WebTabHandler handler = WebTabUtil.getWebTabHandler(tab);

            if (handler instanceof SetupTypeHandler) {
                return true;
            }
        }
        return false;
    }
}