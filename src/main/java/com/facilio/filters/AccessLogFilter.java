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

import org.apache.http.HttpHeaders;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;

import io.sentry.SentryClient; 
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.EventBuilder; 
import io.sentry.event.UserBuilder;  
import com.facilio.aws.util.FacilioProperties;

public class AccessLogFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(AccessLogFilter.class.getName());
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String DUMMY_REMOTE_IP = "0.0.0.1";
    private static final String REMOTE_IP = "remoteIp";
    private static final String REQUEST_METHOD = "req_method";
    private static final String REQUEST_URL = "req_uri";
    private static final String REQUEST_PARAMS = "req_params";
    private static final String DEFAULT_QUERY_STRING = "-";
    private static final String QUERY = "query";
    private static final String RESPONSE_CODE = "responseCode";
    private static final String TIME_TAKEN = "timetaken";
    private static final String TIME_TAKEN_IN_MILLIS = "timeInMillis";
    private static final String DUMMY_MSG = "accesslog";
    private static final String APPENDER_NAME = "graylog3";
    private static final String DEFAULT_ORG_USER_ID = "-1";
    private static final String X_DEVICE_TYPE = "X-Device-Type";
    private static final String X_APP_VERSION = "X-App-Version";
    private static final String REFERER = "referer";
    private static final String RESPONSE_SIZE = "res_size";

    private static final AtomicInteger THREAD_ID = new AtomicInteger(1);

    private static Appender appender;

    public void init(FilterConfig filterConfig) throws ServletException {
        appender = LOGGER.getAppender(APPENDER_NAME);
    }
    // Sentry instance for logging posts in issue tracking group  
    private static SentryClient sentry = SentryClientFactory.sentryClient("https://8f4e00d379c343d88fcfd4a8d768c8df@hentry.facilio.in/3"); 

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        long startTime = System.currentTimeMillis();

        Thread thread = Thread.currentThread();
        String threadName = thread.getName();

        thread.setName(String.valueOf(THREAD_ID.getAndIncrement()));

        filterChain.doFilter(servletRequest, response);

        String message = DUMMY_MSG;
        if(AccountUtil.getCurrentAccount() != null) {
            Account account = AccountUtil.getCurrentAccount();
            message = "select: " + account.getSelectQueries() + " time: " + account.getSelectQueriesTime() +
                    " update: " + account.getUpdateQueries() + " time: " + account.getUpdateQueriesTime() +
                    " insert: " + account.getInsertQueries() + " time: " + account.getInsertQueriesTime() +
                    " delete: " + account.getDeleteQueries() + " time: " + account.getDeleteQueriesTime() +
                    " rget: " + account.getRedisGetCount() + " time: " + account.getRedisGetTime() +
                    " rput: " + account.getRedisPutCount() + " time: " + account.getRedisPutTime() +
                    " rdel: " + account.getRedisDeleteCount() + " time: " + account.getRedisDeleteTime();
        }
        LoggingEvent event = new LoggingEvent(LOGGER.getName(), LOGGER, Level.INFO, message, null);

        if(AccountUtil.getCurrentAccount() != null) {
            Account account = AccountUtil.getCurrentAccount();
            event.setProperty("fselect", String.valueOf(account.getSelectQueries()));
            event.setProperty("finsert", String.valueOf(account.getInsertQueries()));
            event.setProperty("fdelete", String.valueOf(account.getDeleteQueries()));
            event.setProperty("fupdate", String.valueOf(account.getUpdateQueries()));
            event.setProperty("fstime", String.valueOf(account.getSelectQueriesTime()));
            event.setProperty("fitime", String.valueOf(account.getInsertQueriesTime()));
            event.setProperty("fdtime", String.valueOf(account.getDeleteQueriesTime()));
            event.setProperty("futime", String.valueOf(account.getUpdateQueriesTime()));
            event.setProperty("frget", String.valueOf(account.getRedisGetCount()));
            event.setProperty("frput", String.valueOf(account.getRedisPutCount()));
            event.setProperty("frdel", String.valueOf(account.getRedisDeleteCount()));
            event.setProperty("frgtime", String.valueOf(account.getRedisGetTime()));
            event.setProperty("frptime", String.valueOf(account.getRedisPutTime()));
            event.setProperty("frdtime", String.valueOf(account.getRedisDeleteTime()));
            event.setProperty("ftqueries", String.valueOf(account.getTotalQueries()));
            event.setProperty("ftqtime", String.valueOf(account.getTotalQueryTime()));
        }

        String remoteIp = request.getHeader(X_FORWARDED_FOR);
        if(remoteIp == null) {
            remoteIp = request.getRemoteAddr();
        }
        if(remoteIp == null) {
            remoteIp = DUMMY_REMOTE_IP;
        }
        event.setProperty(REMOTE_IP, remoteIp);
        event.setProperty(REQUEST_METHOD, request.getMethod());
        if("".equals(request.getRequestURI())) {
            event.setProperty(REQUEST_URL, "/jsp/index.jsp");
        } else {
            event.setProperty(REQUEST_URL, request.getRequestURI());
        }
        String referer = request.getHeader(HttpHeaders.REFERER);
        if (referer != null && !"".equals(referer.trim())) {
            event.setProperty(REFERER, referer);
        }
        String queryString = request.getQueryString();
        if(queryString == null) {
            queryString = DEFAULT_QUERY_STRING;
        }
        event.setProperty(QUERY, queryString);

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
        if (AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getRequestParams() != null) {
            event.setProperty(REQUEST_PARAMS, AccountUtil.getCurrentAccount().getRequestParams());
        }

        String origin = request.getHeader("Origin");
        if(origin != null) {
            event.setProperty("origin", origin);
        } else {
            event.setProperty("origin", request.getServerName());
        }
        
        String deviceType = request.getHeader(X_DEVICE_TYPE);
        if(deviceType != null) {
            event.setProperty("deviceType", deviceType);
        } else {
            event.setProperty("deviceType", DEFAULT_QUERY_STRING);
        }
        
        String appVersion = request.getHeader(X_APP_VERSION);
        if(appVersion != null) {
            event.setProperty("appVersion", appVersion);
        } else {
            event.setProperty("appVersion", DEFAULT_QUERY_STRING);
        }

        long timeTaken = System.currentTimeMillis()-startTime;
        event.setProperty(RESPONSE_CODE, String.valueOf(response.getStatus()));
        event.setProperty(TIME_TAKEN, String.valueOf(timeTaken/1000));
        event.setProperty(TIME_TAKEN_IN_MILLIS, String.valueOf(timeTaken));
//        if(((HttpServletResponse) servletResponse).containsHeader(HttpHeaders.CONTENT_LENGTH)) {
//            event.setProperty(RESPONSE_SIZE, ((HttpServletResponse) servletResponse).getHeader(HttpHeaders.CONTENT_LENGTH));
//        } else {
//            event.setProperty(RESPONSE_SIZE, DEFAULT_ORG_USER_ID);
//        }
//	if (Integer.valueOf(RESPONSE_CODE) > 500 && Integer.valueOf(TIME_TAKEN) > 20 && !FacilioProperties.isProduction() ) {
//		try {
//			Context context = sentry.getContext(); 
//			context.clear();
//			context.setUser(new UserBuilder().setEmail("issues@facilio.com").build()); 
//			context.addTag("orgid", event.getProperty("orgId") );
//			context.addTag("url",event.getProperty("REQUEST_URL"));
//			context.addTag("remote_ip", event.getProperty("REMOTE_IP") );
//			context.addTag("request_method", event.getProperty("REQUEST_METHOD") );
//			context.addTag("referer", event.getProperty("REFERER") );
//			context.addTag("query", event.getProperty("QUERY") );
//			context.addTag("userid", event.getProperty("userId") );
//			context.addTag("request_params", event.getProperty("REQUEST_PARAMS") );
//			context.addTag("response_code", event.getProperty("RESPONSE_CODE") );
//			context.addTag("time_taken", event.getProperty("TIME_TAKEN") );
//			sentry.sendMessage(event.getProperty("REQUEST_URL"));
//
//		}catch (Exception e) {
//			LOGGER.log(Level.INFO, "Error while posting the issue to sentry " , e);
//		}
//	}
        if(appender != null) {
            appender.doAppend(event);
        } else {
            LOGGER.callAppenders(event);
        }
        thread.setName(threadName);
        AccountUtil.cleanCurrentAccount();
    }

    public void destroy() {

    }
}
