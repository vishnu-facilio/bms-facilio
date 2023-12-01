package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.webtab.SetupTypeHandler;
import com.facilio.bmsconsole.context.webtab.WebTabHandler;
import com.facilio.bmsconsole.util.ModuleAPI;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.APIPermissionUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Log4j
public class PermissionInterceptor extends AbstractInterceptor {
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        Parameter skipPermissionParam = ActionContext.getContext().getParameters().get("skipPermission");
        boolean skipPermission = skipPermissionParam != null && Boolean.parseBoolean(skipPermissionParam.getValue());

        if (AccountUtil.getCurrentOrg() != null) {
            try {
                boolean isNotValid = APIPermissionUtil.shouldCheckPermission(request.getRequestURI()) && !(checkSubModulePermission(request.getMethod()) && checkTabPermission(request.getMethod()));
                if (V3PermissionUtil.isAllowedEnvironment() && !skipPermission && isNotValid) {
                    return ErrorUtil.sendError(ErrorUtil.Error.PERMISSION_NOT_HANDLED);
                }
            } catch (Exception e) {
                LOGGER.error("Error while checking permission", e);
            }
        }
        return invocation.invoke();
    }

    private boolean checkSubModulePermission(String method) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        Parameter actionParam = ActionContext.getContext().getParameters().get("permission");
        Parameter parentModuleParam = ActionContext.getContext().getParameters().get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        Parameter recordId = ActionContext.getContext().getParameters().get(FacilioConstants.ContextNames.RECORD_ID);
        Parameter moduleParam = ActionContext.getContext().getParameters().get("moduleName");

        if(parentModuleParam!=null && StringUtils.isNotEmpty(parentModuleParam.getValue()) && moduleParam!=null && StringUtils.isNotEmpty(moduleParam.getValue())) {
            String parentModuleName = parentModuleParam.getValue();
            String subModuleName = moduleParam.getValue();

            boolean moduleIsAccessible = V3PermissionUtil.isModuleAccessible(parentModuleName, V3PermissionUtil.getCurrentTabId());
            boolean hasSubModuleRelation = ModuleAPI.hasSubModuleRelation(parentModuleName, subModuleName);

            FacilioModule subModule = modBean.getModule(subModuleName);
            boolean isSubModuleBaseEntity = subModule!=null && subModule.getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY;

            if(moduleIsAccessible && hasSubModuleRelation){
                Map<String, String> parameters = new HashMap<>();
                parameters.put(FacilioConstants.ContextNames.WebTab.PARENT_MODULE_NAME, parentModuleName);
                boolean havingPermission = false;
                String actionValue = null;

                if(actionParam != null && actionParam.getValue() != null) {
                    actionParam = WebTabUtil.getActions(actionParam, method);
                    actionValue = actionParam.getValue();
                }

                if(isSubModuleBaseEntity && !actionValue.contains("READ")) {
                    havingPermission = true;
                }else {
                    havingPermission = WebTabUtil.isAuthorizedAccess(parentModuleName, actionValue, parameters, -1L);
                }

                if(recordId != null && recordId.getValue() != null) {
                    boolean parentRecordAccessible = isParentRecordAccessible(parentModuleName, Long.parseLong(recordId.getValue()));
                    return havingPermission && parentRecordAccessible;
                }
            }
            return false;
        }
        return true;
    }

    private static boolean checkTabPermission(String method) throws Exception {
        Parameter action = ActionContext.getContext().getParameters().get("permission");
        Parameter moduleNameParam = ActionContext.getContext().getParameters().get("moduleName");
        Parameter parentModuleName = ActionContext.getContext().getParameters().get("parentModuleName");
        Parameter setupTab = ActionContext.getContext().getParameters().get("setupTab");
        Boolean deprecated = Boolean.valueOf(String.valueOf(ActionContext.getContext().getParameters().get("deprecated")));

        if(parentModuleName != null && parentModuleName.getValue() != null) {
            moduleNameParam = parentModuleName;
        }
        if(setupTab != null && setupTab.getValue() != null) {
            moduleNameParam = getModuleNameParam("setup");
        }
        if(isSetupAPI()){
            return true;
        }
        if(throwDeprecatedApiError(deprecated)) {
            return false;
        }

        if(action != null && action.getValue() != null) {
            action = WebTabUtil.getActions(action,method);
        }
        String moduleName = null;
        if(moduleNameParam != null) {
            moduleName = moduleNameParam.getValue();
        }
        if (action==null) {
            WebTabUtil.permissionLogsForModule();
            return false;
        } else if (!WebTabUtil.isAuthorizedAccess(moduleName, action.getValue())) {
            return false;
        }
        return true;
    }

    private static boolean isParentRecordAccessible(String moduleName,Long recordId) throws Exception {
        if(StringUtils.isNotEmpty(moduleName) && recordId != null) {
            ModuleBaseWithCustomFields record = V3RecordAPI.getRecord(moduleName,recordId);
            if(record != null) {
                return true;
            }
        }
        return false;
    }
    private static boolean throwDeprecatedApiError(Boolean deprecated) {
        try {
            if(AccountUtil.getCurrentOrg() != null) {
                if(deprecated != null && deprecated) {
                    WebTabUtil.permissionLogsForModule();
                    LOGGER.info("This is Deprecated API");
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.info("Error checking deprecated API");
        }
        return false;
    }

    private static Parameter getModuleNameParam(String moduleName) {
        return new Parameter.Request(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
    }

    private static boolean isSetupAPI() throws Exception {
        WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");

        long tabId = V3PermissionUtil.getCurrentTabId();
        if(tabId > 0){
            WebTabContext tab = tabBean.getWebTab(tabId);
            WebTabHandler handler = WebTabUtil.getWebTabHandler(tab);

            if (handler instanceof SetupTypeHandler) {
                return true;
            }
        }
        return false;
    }
}
