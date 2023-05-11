package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
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
        try {
            Parameter parameter = ActionContext.getContext().getParameters().get("permissionInterceptor");
            if (parameter == null || parameter.getValue() == null || parameter.getValue().equals("true")) {
                if (AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.THROW_403_WEBTAB)) {
                    if (!checkSubModulePermission()) {
                        return ErrorUtil.sendError(ErrorUtil.Error.NO_PERMISSION);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error at PermissionInterceptor interceptor computation",e);
        }
        return invocation.invoke();
    }

    private boolean checkSubModulePermission() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        Parameter isSubModule = ActionContext.getContext().getParameters().get("checkSubModulePermission");
        Parameter action = ActionContext.getContext().getParameters().get("permission");
        Parameter parentModuleParam = ActionContext.getContext().getParameters().get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        Parameter recordId = ActionContext.getContext().getParameters().get(FacilioConstants.ContextNames.RECORD_ID);

        String method = request.getMethod();

        if (isSubModule != null && isSubModule.getValue() != null && isSubModule.getValue().equals("true")) {
            Parameter module = ActionContext.getContext().getParameters().get("module");
            if(module != null && module.getValue() != null) {
                String subModuleName = module.getValue();
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule subModule = modBean.getModule(subModuleName);
                if(subModule != null) {
                    FacilioModule parentModule = modBean.getParentModule(subModule.getModuleId());
                    String parentModuleName = null;
                    if(parentModule != null) {
                        parentModuleName = WebTabUtil.getSpecialModule(parentModule.getName());
                    } else if(parentModuleParam != null && parentModuleParam.getValue() != null) {
                        parentModuleName = parentModuleParam.getValue();
                    }
                    if (parentModuleName != null && method != null) {
                        Map<String, String> parameters = new HashMap<>();
                        parameters.put(FacilioConstants.ContextNames.WebTab.PARENT_MODULE_NAME, parentModuleName);
                        boolean havingPermission = WebTabUtil.isAuthorizedAccess(parentModuleName, action.getValue(), false, null, false, true, method, parameters, null);
                        if(recordId != null && recordId.getValue() != null) {
                            boolean parentRecordAccessible = isParentRecordAccessible(parentModuleName, Long.parseLong(recordId.getValue()));
                            return havingPermission && parentRecordAccessible;
                        }
                    }
                }
            }
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
}
