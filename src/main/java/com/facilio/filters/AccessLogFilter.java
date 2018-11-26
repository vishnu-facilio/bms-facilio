package com.facilio.filters;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;

public class AccessLogFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(AccessLogFilter.class.getName());
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String DUMMY_REMOTE_IP = "0.0.0.1";
    private static final String REMOTE_IP = "remoteIp";
    private static final String REQUEST_METHOD = "req_method";
    private static final String REQUEST_URL = "req_uri";
    private static final String DEFAULT_QUERY_STRING = "-";
    private static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
    private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
    private static final String X_FRAME_OPTIONS = "X-Frame-options";
    private static final String X_XSS_PROTECTION = "X-XSS-Protecion";
    private static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    private static final String REFERRER_POLICY = "Referrer-Policy";
    private static final String QUERY = "query";
    private static final String RESPONSE_CODE = "responseCode";
    private static final String TIME_TAKEN = "timetaken";
    private static final String TIME_TAKEN_IN_MILLIS = "timeInMillis";
    private static final String DUMMY_MSG = "accesslog";
    private static final String APPENDER_NAME = "graylog2";
    private static final String DEFAULT_ORG_USER_ID = "-1";
    private static final String X_DEVICE_TYPE = "X-Device-Type";

    private static final AtomicInteger THREAD_ID = new AtomicInteger(1);

    private static Appender appender;

    public void init(FilterConfig filterConfig) throws ServletException {
        appender = LOGGER.getAppender(APPENDER_NAME);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Thread thread = Thread.currentThread();
        String threadName = thread.getName();

        thread.setName(String.valueOf(THREAD_ID.getAndIncrement()));

        long startTime = System.currentTimeMillis();
        LoggingEvent event = new LoggingEvent(LOGGER.getName(), LOGGER, Level.INFO, DUMMY_MSG, null);
        String remoteIp = request.getHeader(X_FORWARDED_FOR);
        if(remoteIp == null) {
            remoteIp = request.getRemoteAddr();
        }
        if(remoteIp == null) {
            remoteIp = DUMMY_REMOTE_IP;
        }
        event.setProperty(REMOTE_IP, remoteIp);
        event.setProperty(REQUEST_METHOD, request.getMethod());
        event.setProperty(REQUEST_URL, request.getRequestURI());
        String queryString = request.getQueryString();
        if(queryString == null) {
            queryString = DEFAULT_QUERY_STRING;
        }
        event.setProperty(QUERY, queryString);

        filterChain.doFilter(servletRequest, response);

        Organization org = AccountUtil.getCurrentOrg();
        if(org != null) {
            event.setProperty("orgId", String.valueOf(org.getOrgId()));
        } else {
            event.setProperty("orgId", DEFAULT_ORG_USER_ID);
        }
        User user = AccountUtil.getCurrentUser();
        if (user != null) {
            event.setProperty("userId", String.valueOf(user.getOuid()));
        } else {
            event.setProperty("userId", DEFAULT_ORG_USER_ID);
        }

        String origin = request.getHeader("Origin");
        if(origin != null) {
            event.setProperty("origin", origin);
        } else {
            event.setProperty("origin", DEFAULT_QUERY_STRING);
        }
        
        String deviceType = request.getHeader(X_DEVICE_TYPE);
        if(deviceType != null) {
            event.setProperty("deviceType", deviceType);
        } else {
            event.setProperty("deviceType", DEFAULT_QUERY_STRING);
        }

        long timeTaken = System.currentTimeMillis()-startTime;
        event.setProperty(RESPONSE_CODE, String.valueOf(response.getStatus()));
        event.setProperty(TIME_TAKEN, String.valueOf(timeTaken/1000));
        event.setProperty(TIME_TAKEN_IN_MILLIS, String.valueOf(timeTaken));
        if(appender != null) {
            appender.doAppend(event);
        } else {
            LOGGER.callAppenders(event);
        }
        thread.setName(threadName);
        AccountUtil.cleanCurrentAccount();
        
        response.setHeader(CONTENT_SECURITY_POLICY , "default-src 'self'");
        response.setHeader(STRICT_TRANSPORT_SECURITY , "max-age=31556926; includeSubDomains");
        response.setHeader(X_FRAME_OPTIONS , "SAMEORIGIN");
        response.setHeader(X_XSS_PROTECTION , "1; mode=block");
        response.setHeader(X_CONTENT_TYPE_OPTIONS , "nosniff");
        response.setHeader( REFERRER_POLICY, "strict-origin-when-cross-origin");
    }

    public void destroy() {

    }
}
