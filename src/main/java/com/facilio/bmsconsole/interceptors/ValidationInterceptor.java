package com.facilio.bmsconsole.interceptors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.security.requestvalidator.Executor;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.util.FacilioUtil;
import com.opensymphony.xwork2.ActionInvocation;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Log4j
public class ValidationInterceptor extends BaseInterceptor {

    @Override
    public String getInterceptorName() {
        return ValidationInterceptor.class.getSimpleName();
    }

    @Override
    public String run(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        Executor executor = (Executor) request.getAttribute("executor");
        if (executor == null) {
            LOGGER.error(ServletActionContext.getActionMapping().getNamespace() + "/" + ServletActionContext.getActionMapping().getName());
            return invocation.invoke();
        }

        try {
            NodeError nodeError = AccountUtil.getSecurityBean().validate(executor);
            if (nodeError != null) {
                if (!(FacilioProperties.isProduction() || FacilioProperties.isOnpremise())) {
                    HttpServletResponse response = ServletActionContext.getResponse();
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("message", nodeError.getErrorMessage());
                    write(errorMap, 400, response);
                    return null;
                } else {
                    LOGGER.error(nodeError.getErrorMessage());
                }
            }
        }
        catch(Exception ex){
            LOGGER.error(FacilioUtil.constructMessageFromException(ex));
        }

        return invocation.invoke();
    }

    private void write(Map messageMap, int httpCode, ServletResponse servletResponse) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setStatus(httpCode);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.println(new JSONObject(messageMap).toString());
        out.flush();
    }
}