package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.context.PermissionFieldEnum;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.context.PermissionSetType;
import com.facilio.permission.handlers.group.PermissionSetGroupHandler;
import com.facilio.permission.util.PermissionSetUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import io.opentelemetry.extension.annotations.WithSpan;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.struts2.dispatcher.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class PermissionSetInterceptor extends AbstractInterceptor {

    @Override
    @WithSpan
    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            Parameter parameter = ActionContext.getContext().getParameters().get("permissionSetInterceptor");
            if(parameter == null || parameter.getValue() == null || parameter.getValue().equals("true")) {
                if (AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                    if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getPeopleId() > -1) {
                        setPermissionSetForAccount();
                        Parameter permissionSet = ActionContext.getContext().getParameters().get("permissionSet");
                        if (permissionSet != null) {
                            String permissionSetType = permissionSet.getValue();
                            if (permissionSetType != null) {
                                PermissionSetType.Type typeEnum = PermissionSetType.Type.valueOf(permissionSetType);
                                if (typeEnum != null) {
                                    Parameter permissionFieldParam = ActionContext.getContext().getParameters().get("permissionValue");
                                    if (permissionFieldParam != null) {
                                        PermissionFieldEnum permissionField = PermissionFieldEnum.valueOf(permissionFieldParam.getValue());
                                        Map<String, String> requiredParamsMap = getRequiredHTTPParamsMap(typeEnum);
                                        PermissionSetGroupHandler handler = typeEnum.getHandler();
                                        Map<String, Long> resolvedParamsValueMap = handler.paramsResolver(requiredParamsMap);
                                        boolean hasPermission = PermissionSetUtil.hasPermission(typeEnum, resolvedParamsValueMap, permissionField);
                                        if (!hasPermission) {
                                            return ErrorUtil.sendError(ErrorUtil.Error.NO_PERMISSION);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error at permission set interceptor" , e);
        }
        return invocation.invoke();
    }

    private Map<String,String> getRequiredHTTPParamsMap(PermissionSetType.Type type) {
        Map<String,String> paramsMap = new HashMap<>();
        List<String> requiredHTTPParams = type.getRequiredHTTPParams();
        if(CollectionUtils.isNotEmpty(requiredHTTPParams)) {
            for(String key : requiredHTTPParams) {
                Parameter httpParam = ActionContext.getContext().getParameters().get(key);
                if(httpParam != null) {
                    paramsMap.put(key,httpParam.getValue());
                }
            }
        }
        return paramsMap;
    }

    private void setPermissionSetForAccount() throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        List<PermissionSetContext> permissionSetContexts = permissionSetBean.getUserPermissionSetIds(AccountUtil.getCurrentUser().getPeopleId());
        AccountUtil.setPermissionSets(permissionSetContexts);
    }
}