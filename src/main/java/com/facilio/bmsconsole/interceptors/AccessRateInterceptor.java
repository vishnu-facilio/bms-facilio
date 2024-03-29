package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.security.ratelimiter.APIRateLimiter;
import com.facilio.security.ratelimiter.RateLimiterAPI;
import com.facilio.security.requestvalidator.Executor;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j
public class AccessRateInterceptor extends AbstractInterceptor {
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();

        if (FacilioProperties.isApiRateLimiterEnabled()) {
            IAMAccount iamAccount = (IAMAccount) ServletActionContext.getRequest().getAttribute("iamAccount");
            Organization org = iamAccount != null ? iamAccount.getOrg() : null;
            IAMUser user = iamAccount != null ? iamAccount.getUser() : null;
            Long maxAllowedReqCount = -1L;
            Executor executor = (Executor) request.getAttribute("executor");
            if (executor != null) {
                maxAllowedReqCount = executor.getMaxAllowedReqCount();
            }
            String rateLimitKey = null;
            try {
                Parameter rateLimiterKeyParam = ActionContext.getContext().getParameters().get("rateLimitKey");
                if (rateLimiterKeyParam != null && rateLimiterKeyParam.getValue() != null) {
                    rateLimitKey = rateLimiterKeyParam.getValue();
                }
            } catch (Exception e) {
                LOGGER.error("Error while getting rateLimitKey from request", e);
            }
            APIRateLimiter rateLimiter = RateLimiterAPI.getRateLimiter();
            if (org != null && user != null) {
                if (!(rateLimiter.allow(request.getRequestURI(), user.getUid(), org.getOrgId(), rateLimitKey, maxAllowedReqCount))) {
                    HttpServletResponse response = ServletActionContext.getResponse();
                    response.setHeader("X-Retry-After", String.valueOf(FacilioProperties.getRateLimiterInterval()));

                    LOGGER.info("Rate Limiter : API strike limit was reached for OrgId - " + org.getOrgId());
                    return ErrorUtil.sendError(ErrorUtil.Error.RATE_LIMIT_FOR_API_EXCEED);
                }
                if (rateLimiter.getRequestsMade(request.getRequestURI(), user.getUid(), org.getOrgId(), rateLimitKey) > 20L) {
                    LOGGER.info("Rate Limiter : API strike limit was crossed 20 for OrgId - " + org.getOrgId());
                }
            }
        }
        return invocation.invoke();
    }
}
