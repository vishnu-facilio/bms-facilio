package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseInterceptor extends AbstractInterceptor {

    private static final Tracer TRACER =
            GlobalOpenTelemetry.getTracer(BaseInterceptor.class.getName());

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Span childSpan = TRACER.spanBuilder(getInterceptorName()).startSpan();
        childSpan.setAttribute(FacilioConstants.Telemetry.REGION, FacilioProperties.getRegion());
        childSpan.setAttribute(FacilioConstants.Telemetry.ORG_ID, getOrgID());
        childSpan.setAttribute(FacilioConstants.Telemetry.USER_ID, getUserID());
        try (Scope scope = childSpan.makeCurrent()) {
            run(actionInvocation);
        } finally {
            childSpan.end();
        }
        return null;
    }

    private IAMAccount getAccount() {
        HttpServletRequest request = ServletActionContext.getRequest();
        return (IAMAccount) request.getAttribute("iamAccount");
    }

    private long getOrgID() {
        IAMAccount acc = getAccount();
        if (acc == null) {
            return -1;
        }
        Organization org = acc.getOrg();
        if (org == null) {
            return -1;
        }
        return org.getOrgId();
    }

    private long getUserID() {
        IAMAccount acc = getAccount();
        if (acc == null) {
            return -1;
        }
        IAMUser user = acc.getUser();
        if (user == null) {
            return -1;
        }
        return user.getUid();
    }

    public abstract String run(ActionInvocation actionInvocation) throws Exception;

    public abstract String getInterceptorName();
}
