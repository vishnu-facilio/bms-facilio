package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.iam.accounts.impl.HydraClient;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

@Log4j
public class Oauth2Interceptor extends AbstractInterceptor {
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            LOGGER.error("Authorization header missing");
            return Action.LOGIN;
        }
        String bearerToken = authorization.replace("Bearer ", "");
        String clientIdForToken = new HydraClient().getClientIdForToken(bearerToken);

        if (StringUtils.isEmpty(clientIdForToken)) {
            return Action.LOGIN;
        }

        IAMAccount iamAccount = IAMUserUtil.fetchAccountByOauth2ClientId(clientIdForToken);
        if (iamAccount == null) {
            return Action.LOGIN;
        }
        request.setAttribute("iamAccount", iamAccount);
        return invocation.invoke();
    }
}
