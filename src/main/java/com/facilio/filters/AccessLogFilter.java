package com.facilio.filters;

import com.amazonaws.regions.Regions;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.server.ServerInfo;
import com.facilio.service.FacilioService;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.RequestUtil;
import com.facilio.util.SentryUtil;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.extension.annotations.WithSpan;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AccessLogFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(AccessLogFilter.class.getName());
    private static final Logger ETISALAT = LogManager.getLogger("Etisalat");
    private static final String RESPONSE_CODE = "responseCode";
    private static final String TIME_TAKEN = "timetaken";
    private static final String TIME_TAKEN_IN_MILLIS = "timeInMillis";
    private static final String DUMMY_MSG = "accesslog";
    private static final String APPENDER_NAME = "graylog3";
    private static final boolean ENABLE_FHR = FacilioProperties.enableFacilioResponse();

    private static final AtomicInteger THREAD_ID = new AtomicInteger(1);
    private static final long TIME_THRESHOLD = 5000;
    private static final String GRAY_LOG_URL = "https://logs.facilio.in/streams/000000000000000000000001/search?saved=5d19a5329b569c766b3a1f2f&rangetype=relative&fields=logger%2Cmessage&width=1536&relative=86400&q=facility%3Aproduction-user%20AND%20thread%20%3A%20";
    private static Appender appender;


    public void init ( FilterConfig filterConfig ) throws ServletException {
        appender = LOGGER.getAppender(APPENDER_NAME);
    }

    @WithSpan("thread")
    private void traceThreadName(String threadName) {
        Span.current().setAttribute("thread", threadName);
    }

    public static boolean isGetAvailableRequest(HttpServletRequest request) {
        return
                Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion())
                        && StringUtils.isNotEmpty(request.getRequestURI())
                        && (
                                request.getRequestURI().contains("getAvailableButtons")
                                        || request.getRequestURI().contains("getAvailableState")
                        )
                ;
    }

    public void doFilter ( ServletRequest servletRequest,ServletResponse servletResponse,FilterChain filterChain ) throws IOException, ServletException {

        Thread thread = Thread.currentThread();
        String threadName = thread.getName();
        try {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            HttpServletResponse response = (HttpServletResponse)servletResponse;
//        System.out.println("Access log filter called : "+request.getRequestURI()+"::"+request.getAttribute(IAMAppUtil.REQUEST_APP_NAME));

            if(ENABLE_FHR) {
                if (isGetAvailableRequest(request)) {
                    response = new FacilioDebugHttpResponse(response);
                }
                else {
                    response = new FacilioHttpResponse(response);
                }
            }
            else if (isGetAvailableRequest(request)) {
                response = new FacilioDebugHttpResponse(response);
            }

            long startTime = System.currentTimeMillis();
            thread.setName(String.valueOf(THREAD_ID.getAndIncrement()));
            traceThreadName(thread.getName());
            filterChain.doFilter(servletRequest,response);

            StringBuilder message = new StringBuilder();
            int responseSize = 0;
            if(AccountUtil.getCurrentAccount() != null) {
                Account account = AccountUtil.getCurrentAccount();
                message.append("select : ").append(account.getSelectQueries()).append(" time : ").append(account.getSelectQueriesTime())
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
                        .append(", jsonConversionTime : ").append(account.getJsonConversionTime())
                ;
            } else {
                message.append(DUMMY_MSG);
            }

            if(ENABLE_FHR) {
                responseSize = ((FacilioHttpResponse)response).getLengthInBytes();
                message.append(", data : ").append(responseSize);
            }
//            System.out.println("Response size in Access => "+responseSize);
//            System.out.println(message.toString());
            LoggingEvent event = new LoggingEvent(LOGGER.getName(),LOGGER,Level.INFO,message.toString(),null);

            Account account = null;
            if(AccountUtil.getCurrentAccount() != null) {
                account = AccountUtil.getCurrentAccount();
                event.setProperty("fselect",String.valueOf(account.getSelectQueries()));
                event.setProperty("finsert",String.valueOf(account.getInsertQueries()));
                event.setProperty("fdelete",String.valueOf(account.getDeleteQueries()));
                event.setProperty("fupdate",String.valueOf(account.getUpdateQueries()));
                event.setProperty("fstime",String.valueOf(account.getSelectQueriesTime()));
                event.setProperty("fitime",String.valueOf(account.getInsertQueriesTime()));
                event.setProperty("fdtime",String.valueOf(account.getDeleteQueriesTime()));
                event.setProperty("futime",String.valueOf(account.getUpdateQueriesTime()));
                event.setProperty("frget",String.valueOf(account.getRedisGetCount()));
                event.setProperty("frput",String.valueOf(account.getRedisPutCount()));
                event.setProperty("frdel",String.valueOf(account.getRedisDeleteCount()));
                event.setProperty("frgtime",String.valueOf(account.getRedisGetTime()));
                event.setProperty("frptime",String.valueOf(account.getRedisPutTime()));
                event.setProperty("frdtime",String.valueOf(account.getRedisDeleteTime()));
                event.setProperty("ijob",String.valueOf(account.getInstantJobCount()));
                event.setProperty("ijobfiletime",String.valueOf(account.getInstantJobFileAddTime()));
                event.setProperty("ftqueries",String.valueOf(account.getTotalQueries()));
                event.setProperty("ftqtime",String.valueOf(account.getTotalQueryTime()));
                event.setProperty("fdatasize",String.valueOf(responseSize));
                event.setProperty("fjsonconvtime",String.valueOf(account.getJsonConversionTime()));
                if (account.getUser() != null && StringUtils.isNotEmpty(account.getUser().getProxy())) {
                    event.setProperty("proxy", String.valueOf(account.getUser().getProxy()));
                }
            }

            RequestUtil.addRequestLogEvents(request,event);
            if(ENABLE_FHR) {
                event.setProperty(RequestUtil.RESPONSE_SIZE,String.valueOf(responseSize));
            }
            if (request.getAttribute("executor") == null) {
                event.setProperty("isInputValidated", "false");
            } else {
                event.setProperty("isInputValidated", "true");
            }
            String otelTraceId = Span.current().getSpanContext().getTraceId();
            event.setProperty("otel-trace", "https://signoz.facilio.in/trace/" + otelTraceId);
            event.setProperty("repeatQueries", "https://appsmith.facilio.in/app/signoz-traces/page1-629721b92b5ae55d651869c0?traceId="+ otelTraceId);
            long timeTaken = System.currentTimeMillis() - startTime;
            String responseCode = String.valueOf(response.getStatus());
            event.setProperty(RESPONSE_CODE,String.valueOf(response.getStatus()));
            event.setProperty(TIME_TAKEN,String.valueOf(timeTaken / 1000));
            event.setProperty(TIME_TAKEN_IN_MILLIS,String.valueOf(timeTaken));

            String searchQuery = thread.getName() + "%20";
            if(ServerInfo.getHostname() != null) {
                String sourceIp = ServerInfo.getHostname();
                searchQuery = searchQuery + "AND%20source%3A%20" + sourceIp;
            }
            String grayLogSearchUrl = GRAY_LOG_URL + searchQuery;
            event.setProperty("follow",grayLogSearchUrl);

            if(FacilioProperties.isSentryEnabled() && (response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR || timeTaken > TIME_THRESHOLD)) {

                Map<String, String> contextMap = new HashMap<>();

                contextMap.put("orgId",event.getProperty(RequestUtil.ORGID_HEADER));
                contextMap.put("url",event.getProperty(RequestUtil.REQUEST_URL));
                contextMap.put("remoteIp",event.getProperty(RequestUtil.REMOTE_IP));
                contextMap.put("referer",event.getProperty(RequestUtil.REFERER));
                contextMap.put("query",event.getProperty(RequestUtil.QUERY));
                contextMap.put("userId",event.getProperty(RequestUtil.USERID_HEADER));
                contextMap.put("uid",event.getProperty(RequestUtil.IAM_USERID_HEADER));
                contextMap.put("userMobileSettingId",event.getProperty(RequestUtil.X_USER_MOBILE_SETTING_ID));
                contextMap.put("responseCode",responseCode);
                contextMap.put("timeTaken",String.valueOf(timeTaken));
                contextMap.put("thread",thread.getName());
                contextMap.put("graylogurl",grayLogSearchUrl);

                if(timeTaken > TIME_THRESHOLD) {
                    SentryUtil.sendSlowresponseToSentry(contextMap);
                } else {
                    SentryUtil.sendToSentry(contextMap);
                }
            }
            if(appender != null) {
                appender.doAppend(event);
            } else {
                LOGGER.callAppenders(event);
            }
            if(FacilioProperties.logUserAccessLog()) {
                String email = "guest";
                if(AccountUtil.getCurrentUser() != null) {
                    email = AccountUtil.getCurrentUser().getEmail();
                }
                event.setProperty("email",email);
                ETISALAT.callAppenders(event);
            }
            if(FacilioProperties.isAccessLogEnable()) {
                long orgId = Long.parseLong(event.getProperty(RequestUtil.ORGID_HEADER));
                long userId = Long.parseLong(event.getProperty(RequestUtil.USERID_HEADER));
                long iamUserId = Long.parseLong(event.getProperty(RequestUtil.IAM_USERID_HEADER));
                long userMobileSettingId = Long.parseLong(event.getProperty(RequestUtil.X_USER_MOBILE_SETTING_ID));
                if((orgId > 0L) && (userId > 0)) {
                    Map<String, Object> props = new HashMap<>();
                    props.put("orgId",orgId);
                    props.put("userId",userId);
                    props.put("uid",iamUserId);
                    props.put("app",event.getProperty("app"));
                    props.put("reqUri",event.getProperty(RequestUtil.REQUEST_URL));
                    props.put("sourceIp",ServerInfo.getHostname());
                    props.put("remoteIp",event.getProperty(RequestUtil.REMOTE_IP));
                    props.put("referer",event.getProperty(RequestUtil.REFERER));
                    props.put("responseCode",responseCode);
                    props.put("timeTaken",event.getProperty("timetaken"));
                    props.put("responseSize",responseSize);
                    props.put("requestSize",event.getProperty("requestSize"));
                    props.put("deviceType",event.getProperty(RequestUtil.DEVICE_TYPE));
                    props.put("userMobileSettingId",userMobileSettingId);
                    props.put("timeInMilliSec",System.currentTimeMillis());
                    props.put("appVersion",event.getProperty("appVersion"));
                    props.put("executor",event.getProperty("executor"));
                    props.put("exception",event.getProperty("exception"));
                    props.put("job",event.getProperty("job"));
                    props.put("fselect",String.valueOf(account.getSelectQueries()));
                    props.put("finsert",String.valueOf(account.getInsertQueries()));
                    props.put("fdelete",String.valueOf(account.getDeleteQueries()));
                    props.put("fupdate",String.valueOf(account.getUpdateQueries()));
                    props.put("fstime",String.valueOf(account.getSelectQueriesTime()));
                    props.put("fitime",String.valueOf(account.getInsertQueriesTime()));
                    props.put("fdtime",String.valueOf(account.getDeleteQueriesTime()));
                    props.put("futime",String.valueOf(account.getUpdateQueriesTime()));
                    props.put("frget",String.valueOf(account.getRedisGetCount()));
                    props.put("frput",String.valueOf(account.getRedisPutCount()));
                    props.put("frdel",String.valueOf(account.getRedisDeleteCount()));
                    props.put("frgtime",String.valueOf(account.getRedisGetTime()));
                    props.put("frptime",String.valueOf(account.getRedisPutTime()));
                    props.put("frdtime",String.valueOf(account.getRedisDeleteTime()));
                    props.put("ijob",String.valueOf(account.getInstantJobCount()));
                    props.put("ijobfiletime",String.valueOf(account.getInstantJobFileAddTime()));
                    props.put("ftqueries",String.valueOf(account.getTotalQueries()));
                    props.put("ftqtime",String.valueOf(account.getTotalQueryTime()));
                    props.put("fjsonconvtime",String.valueOf(account.getJsonConversionTime()));

                    long auditStartTime = System.currentTimeMillis();
                    FacilioService.runAsService(FacilioConstants.Services.AUDIT_SERVICE,() -> inserAuditLog(props));
                    LOGGER.debug("Time taken to insert Audit Log : " + (System.currentTimeMillis() - auditStartTime) + " millsec");
                }
            }

        } catch (Exception e) {
            LOGGER.info("Exception in access log: ",e);
        } finally {
            thread.setName(threadName);
            try {
                AccountUtil.cleanCurrentAccount();
            } catch (Exception e) {
                LOGGER.error("Exception while cleaning current account ",e);
            }
        }
    }

    private void inserAuditLog ( Map<String, Object> props ) throws SQLException {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getFacilioAuditFields())
                .table(ModuleFactory.getFacilioAuditModule().getTableName())
                .addRecord(props);
        builder.save();
    }

    public void destroy () {

    }
}
