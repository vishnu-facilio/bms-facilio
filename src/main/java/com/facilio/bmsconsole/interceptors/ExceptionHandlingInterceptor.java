package com.facilio.bmsconsole.interceptors;

import com.facilio.exception.ErrorResponseUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import io.opentelemetry.extension.annotations.WithSpan;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Log4j
public class ExceptionHandlingInterceptor extends AbstractInterceptor  {
    @Override
    @WithSpan
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = null;
        try{
            result = invocation.invoke();
        } catch (Exception ex){
            LOGGER.error(FacilioUtil.constructMessageFromException(ex));
            HttpServletResponse response = ServletActionContext.getResponse();
            Boolean errorType;
            Boolean messageNull;
            ErrorResponseUtil values = null;
            if(ex instanceof RESTException){
                errorType = true;
                values = new ErrorResponseUtil((RESTException) ex);
            } else {
                errorType = false;
            }
            Map<String, Object> errorMap = new HashMap<>();
            messageNull = ex.getMessage() == null || StringUtils.isEmpty(ex.getMessage());
            //Handling exception for Multiple transactions in a thread(New Transaction Service)
            try {
                errorMap.put("message", ((InvocationTargetException)ex).getTargetException().getMessage());
            }catch(Exception e){
                errorMap.put("message", errorType ? values.getMessage() : messageNull ? "Error Occurred" :ex.getMessage());
            }
            errorMap.put("responseCode",errorType ? values.getErrorCode().getCode() : messageNull ? ErrorCode.UNHANDLED_EXCEPTION.getCode() : ErrorCode.ERROR.getCode());
            errorMap.put("code",errorType ? values.getErrorCode().getCode() : messageNull ? ErrorCode.UNHANDLED_EXCEPTION.getCode() : ErrorCode.ERROR.getCode());
            errorMap.put("isErrorGeneralized", errorType ? values.getIsErrorGeneralized(): false);
            errorMap.put("isVisible",  errorType ? values.getIsVisible(): true);
            errorMap.put("correctiveAction", errorType ? values.getCorrectiveAction(): "");
            write(errorMap, errorType ? values.getErrorCode().getHttpStatus() : messageNull ? ErrorCode.UNHANDLED_EXCEPTION.getHttpStatus() : ErrorCode.ERROR.getHttpStatus(), response);

        }
        return result;
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
