package com.facilio.filters;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.util.SentryUtil;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final long TIME_THRESHOLD = 5000 ;

    private static Appender appender;


    public void init(FilterConfig filterConfig) throws ServletException {
        appender = LOGGER.getAppender(APPENDER_NAME);
    }


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
        String requestUrl = request.getRequestURI();
        if("".equals(requestUrl)) {
            event.setProperty(REQUEST_URL, "/jsp/index.jsp");
        } else {
            event.setProperty(REQUEST_URL, requestUrl);
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
        String orgId = DEFAULT_ORG_USER_ID;
        if(org != null) {
            orgId = String.valueOf(org.getOrgId());
        }
        event.setProperty("orgId", orgId);

        User user = AccountUtil.getCurrentUser();
        String userId = DEFAULT_ORG_USER_ID;
        if (user != null) {
            userId = String.valueOf(user.getOuid());
        }
        event.setProperty("userId", userId);

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
        String responseCode = String.valueOf(response.getStatus());
        event.setProperty(RESPONSE_CODE, String.valueOf(response.getStatus()));
        event.setProperty(TIME_TAKEN, String.valueOf(timeTaken/1000));
        event.setProperty(TIME_TAKEN_IN_MILLIS, String.valueOf(timeTaken));

        if (response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR  || timeTaken > TIME_THRESHOLD  ) {
            String finalRemoteIp = remoteIp;
            String finalOrgId = orgId;
            String finalUserId = userId;
            String finalQueryString = queryString;
            Map<String, String> contextMap = new HashMap<String, String>() {
                {
                    put("orgId", finalOrgId);
                    put("url", requestUrl);
                    put("remoteIp", finalRemoteIp);
                    put("referer", referer);
                    put("query", finalQueryString);
                    put("userId", finalUserId);
                    put("responseCode", responseCode);
                    put("timeTaken", String.valueOf(timeTaken));
                    put("thread", thread.getName());
                }
            };
            LOGGER.log(Level.INFO, "Log this to sentry");
            SentryUtil.sendToSentry(contextMap, requestUrl,finalUserId);
        }
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
