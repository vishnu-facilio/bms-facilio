package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.security.ratelimiter.APIRateLimiter;
import com.facilio.security.ratelimiter.RateLimiterAPI;
import com.facilio.security.requestvalidator.Executor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;

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

            APIRateLimiter rateLimiter = RateLimiterAPI.getRateLimiter();
            if (org != null && user != null && !(rateLimiter.allow(request.getRequestURI(), user.getUid(), org.getOrgId(), null, maxAllowedReqCount))) {
                HttpServletResponse response = ServletActionContext.getResponse();
                response.setHeader("X-Retry-After", String.valueOf(FacilioProperties.getRateLimiterInterval()));

                LOGGER.info("Rate Limiter : API strike limit was reached");
                return ErrorUtil.sendError(ErrorUtil.Error.RATE_LIMIT_FOR_API_EXCEED);
            }
        }
        return invocation.invoke();
    }
}
