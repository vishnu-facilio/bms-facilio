package com.facilio.fw.filter;

import com.facilio.security.SecurityRequestWrapper;
import com.facilio.security.requestvalidator.Executor;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.security.requestvalidator.config.Config;
import com.facilio.security.requestvalidator.config.ConfigParser;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.context.RequestContext;
import com.facilio.security.requestvalidator.retree.Matcher;
import com.facilio.security.requestvalidator.retree.URLReTree;
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
public class BeforeAuthInputFilter implements Filter {
    private URLReTree urlReTree;
    private Config config;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            this.config = new ConfigParser().read("security");
            this.urlReTree = new URLReTree(this.config.getAllPaths());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        Matcher matcher = this.urlReTree.matcher(httpServletRequest.getRequestURI());

        if (!matcher.isMatch()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        SecurityRequestWrapper securityRequestWrapper = new SecurityRequestWrapper((HttpServletRequest) servletRequest);
        RequestContext requestContext = new RequestContext(securityRequestWrapper, matcher.getMatchMap());
        String matchedPattern = matcher.getMatchedPattern();
        RequestConfig requestConfig = config.getRequestConfig(matchedPattern);
        Executor executor = new Executor(requestConfig, requestContext);
        try {
            NodeError nodeError = executor.validatePreAuth();
            if (nodeError != null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", nodeError.getErrorMessage());
                write(errorMap, 400, servletResponse);
                return;
            }
            securityRequestWrapper.setAttribute("executor", executor);
        } catch (Exception ex) {
            LoggingEvent event = new LoggingEvent(LOGGER.getName(), LOGGER, Level.INFO, ex.getMessage(), null);
            RequestUtil.addRequestLogEvents(securityRequestWrapper, event);
            LOGGER.callAppenders(event);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("message", "Error validating");
            write(errorMap, 500, servletResponse);
            return;
        }
        securityRequestWrapper.reset();
        filterChain.doFilter(securityRequestWrapper, servletResponse);
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
