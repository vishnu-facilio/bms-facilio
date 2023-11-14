package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.webtab.SetupTypeHandler;
import com.facilio.bmsconsole.context.webtab.WebTabHandler;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.APIPermissionUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import javax.servlet.http.HttpServletRequest;

@Log4j
public class PermissionInterceptor extends AbstractInterceptor {
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();

        if(V3PermissionUtil.isAllowedEnvironment() && APIPermissionUtil.shouldCheckPermission(request.getRequestURI())) {
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
            String method = request.getMethod();

            if(isSetupAPI()){
                return invocation.invoke();
            }
            if(throwDeprecatedApiError(deprecated)) {
                return ErrorUtil.sendError(ErrorUtil.Error.PERMISSION_NOT_HANDLED);
            }

            if(action != null && action.getValue() != null) {
                action = WebTabUtil.getActions(action,method);
            }
            String moduleName = null;
            if(moduleNameParam != null) {
                moduleName = moduleNameParam.getValue();
            }
            if (action==null) {
                LOGGER.info("API Permission Check Missing or moduleName is missing");
                return ErrorUtil.sendError(ErrorUtil.Error.PERMISSION_NOT_HANDLED);
            } else if (!WebTabUtil.isAuthorizedAccess(moduleName, action.getValue())) {
                return ErrorUtil.sendError(ErrorUtil.Error.PERMISSION_NOT_HANDLED);
            }
        }
        return invocation.invoke();
    }

    private static boolean throwDeprecatedApiError(Boolean deprecated) {
        try {
            if(AccountUtil.getCurrentOrg() != null) {
                if(deprecated != null && deprecated) {
                    LOGGER.info("This is Deprecated API");
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.info("Error checking deprecated API");
        }
        return false;
    }

    private Parameter getModuleNameParam(String moduleName) {
        return new Parameter.Request(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
    }

    private static boolean isSetupAPI() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String currentTabId = request.getHeader("X-Tab-Id");
        WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");

        if(StringUtils.isNotEmpty(currentTabId)){
            long tabId = Long.parseLong(currentTabId);
            WebTabContext tab = tabBean.getWebTab(tabId);
            WebTabHandler handler = WebTabUtil.getWebTabHandler(tab);

            if (handler instanceof SetupTypeHandler) {
                return true;
            }
        }
        return false;
    }
}
