package com.facilio.filters;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.fw.util.RequestUtil;
import com.facilio.server.ServerInfo;
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
    public static final String REMOTE_IP = "remoteIp";
    private static final String REQUEST_METHOD = "req_method";
    public static final String REQUEST_URL = "req_uri";
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
    private static final boolean ENABLE_FHR = FacilioProperties.enableFacilioResponse();
    public static final String ORIGIN = "origin";

    private static final AtomicInteger THREAD_ID = new AtomicInteger(1);
    private static final long TIME_THRESHOLD = 5000 ;
    private static final String GRAY_LOG_URL = "https://logs.facilio.in/streams/000000000000000000000001/search?saved=5d19a5329b569c766b3a1f2f&rangetype=relative&fields=logger%2Cmessage&width=1536&relative=86400&q=facility%3Aproduction-user%20AND%20thread%20%3A%20";
    private static Appender appender;


    public void init(FilterConfig filterConfig) throws ServletException {
        appender = LOGGER.getAppender(APPENDER_NAME);
    }


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        System.out.println("Access log filter called : "+request.getRequestURI()+"::"+request.getAttribute(IAMAppUtil.REQUEST_APP_NAME));

        if( ENABLE_FHR) {
            response = new FacilioHttpResponse(response);
        }

        long startTime = System.currentTimeMillis();

        Thread thread = Thread.currentThread();
        String threadName = thread.getName();

        thread.setName(String.valueOf(THREAD_ID.getAndIncrement()));

        filterChain.doFilter(servletRequest, response);

        String message = DUMMY_MSG;
        int responseSize = 0;
        if(AccountUtil.getCurrentAccount() != null) {
            Account account = AccountUtil.getCurrentAccount();
            message = new StringBuilder()
                    .append("select : ").append(account.getSelectQueries()).append(" time : ").append(account.getSelectQueriesTime())
                    .append(", update : ").append(account.getUpdateQueries()).append(" time : ").append(account.getUpdateQueriesTime())
                    .append(", insert : ").append(account.getInsertQueries()).append(" time : ").append(account.getInsertQueriesTime())
                    .append(", delete : ").append(account.getDeleteQueries()).append(" time : ").append(account.getDeleteQueriesTime())
                    .append(", rget : ").append(account.getRedisGetCount()).append(" time : ").append(account.getRedisGetTime())
                    .append(", rput : ").append(account.getRedisPutCount()).append(" time : ").append(account.getRedisPutTime())
                    .append(", rdel : ").append(account.getRedisDeleteCount()).append(" time : ").append(account.getRedisDeleteTime())
                    .append(", pSelect : ").append(account.getPublicSelectQueries()).append(" time : ").append(account.getPublicSelectQueriesTime())
                    .append(", pUpdate : ").append(account.getPublicUpdateQueries()).append(" time : ").append(account.getPublicUpdateQueriesTime())
                    .append(", pInsert : ").append(account.getPublicInsertQueries()).append(" time : ").append(account.getPublicInsertQueriesTime())
                    .append(", pDelete : ").append(account.getPublicDeleteQueries()).append(" time : ").append(account.getPublicDeleteQueriesTime())
                    .append(", pRget : ").append(account.getPublicRedisGetCount()).append(" time : ").append(account.getPublicRedisGetTime())
                    .append(", pRput : ").append(account.getPublicRedisPutCount()).append(" time : ").append(account.getPublicRedisPutTime())
                    .append(", pRdel : ").append(account.getPublicRedisDeleteCount()).append(" time : ").append(account.getPublicRedisDeleteTime())
                    .toString();
        }

        if(ENABLE_FHR) {
            responseSize = ((FacilioHttpResponse)response).getLengthInBytes();
            message = message + "  data: " + responseSize;
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

        String remoteIp = RequestUtil.getRemoteAddr(request);
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

        event.setProperty(ORIGIN, RequestUtil.getOrigin(request));

        String deviceType = request.getHeader(X_DEVICE_TYPE);
        if(deviceType == null) {
            deviceType = DEFAULT_QUERY_STRING;
        }
        event.setProperty("deviceType", deviceType);

        String appVersion = request.getHeader(X_APP_VERSION);
        if(appVersion == null) {
            appVersion = DEFAULT_QUERY_STRING;
        }
        event.setProperty("appVersion", appVersion);

        if ( ENABLE_FHR ) {
            event.setProperty(RESPONSE_SIZE, String.valueOf(responseSize));
        }

        long timeTaken = System.currentTimeMillis()-startTime;
        String responseCode = String.valueOf(response.getStatus());
        event.setProperty(RESPONSE_CODE, String.valueOf(response.getStatus()));
        event.setProperty(TIME_TAKEN, String.valueOf(timeTaken/1000));
        event.setProperty(TIME_TAKEN_IN_MILLIS, String.valueOf(timeTaken));

        String searchQuery = thread.getName()+"%20";
        if (ServerInfo.getHostname() != null) {
            String sourceIp = ServerInfo.getHostname();
            searchQuery = searchQuery + "AND%20source%3A%20"+sourceIp;
        }
        String grayLogSearchUrl = GRAY_LOG_URL + searchQuery;
        event.setProperty("follow", grayLogSearchUrl);

        if ( FacilioProperties.isSentryEnabled() && (response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR || timeTaken > TIME_THRESHOLD) ) {

            Map<String, String> contextMap = new HashMap<>();

            contextMap.put("orgId", orgId);
            contextMap.put("url", requestUrl);
            contextMap.put("remoteIp", remoteIp);
            contextMap.put("referer", referer);
            contextMap.put("query", queryString);
            contextMap.put("userId", userId);
            contextMap.put("responseCode", responseCode);
            contextMap.put("timeTaken", String.valueOf(timeTaken));
            contextMap.put("thread", thread.getName());
            contextMap.put("graylogurl", grayLogSearchUrl);

            if (timeTaken > TIME_THRESHOLD){
                SentryUtil.sendSlowresponseToSentry(contextMap );
            }
            else {
                SentryUtil.sendToSentry(contextMap);
            }
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
