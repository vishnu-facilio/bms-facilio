package com.facilio.fw.filter;

import com.facilio.security.requestvalidator.Executor;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.util.RequestUtil;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Log4j
public class AfterAuthInputFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Executor executor = (Executor) servletRequest.getAttribute("executor");
        if (executor != null) {
            try {
                NodeError nodeError = executor.validatePostAuth();
                if (nodeError != null) {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("message", nodeError.getErrorMessage());
                    write(errorMap, 400, servletResponse);
                    return;
                }
            } catch (Exception ex) {
                LoggingEvent event = new LoggingEvent(LOGGER.getName(), LOGGER, Level.INFO, ex.getMessage(), null);
                RequestUtil.addRequestLogEvents((HttpServletRequest) servletRequest, event);
                LOGGER.callAppenders(event);
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "Error validating");
                write(errorMap, 500, servletResponse);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
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

    @Override
    public void destroy() {

    }
}
