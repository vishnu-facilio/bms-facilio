package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.extension.annotations.WithSpan;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

@Log4j
public class APIKeyInterceptor extends AbstractInterceptor {
    @Override
    @WithSpan
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String authMethod = (String) request.getAttribute("authMethod");
        if (authMethod == null || !authMethod.equals("APIKEY")) {
            return invocation.invoke();
        }
        String apiKey = request.getHeader("x-api-key");
        if (StringUtils.isEmpty(apiKey)) {
            LOGGER.error("API Key header missing");
            return Action.LOGIN;
        }
        IAMAccount iamAccount = IAMUserUtil.fetchAccountByAPIKey(apiKey);
        if (iamAccount == null) {
            return Action.LOGIN;
        }
        request.setAttribute("iamAccount", iamAccount);
        Span.current().setAttribute("enduser.id", String.valueOf(iamAccount.getUser().getUid()));
        return invocation.invoke();
    }
}
