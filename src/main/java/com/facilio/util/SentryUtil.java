package com.facilio.util;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.server.ServerInfo;
import com.facilio.tasker.job.JobContext;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.interfaces.HttpInterface;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class SentryUtil {

    private static final SentryClient SENTRY_CLIENT = SentryClientFactory.sentryClient(FacilioProperties.getsentrydsn());
    private static final SentryClient SENTRY_SLOWNESS_CLIENT = SentryClientFactory.sentryClient(FacilioProperties.getSentryslownessdsn());
    private static final SentryClient SENTRY_SCHEDULER_CLIENT = SentryClientFactory.sentryClient(FacilioProperties.getSentryschedulerdsn());
    private static final Logger LOGGER = LogManager.getLogger(SentryUtil.class.getName());

    public static void sendToSentry(Map<String, String> contextMap, HttpServletRequest request) {

        if (FacilioProperties.isSentryEnabled()) {
            Context context = SENTRY_CLIENT.getContext();
            context.clear();
            //context.setUser(new UserBuilder().setId(userId).build());
            //context.addTag
            if (request != null) {
                context.setHttp(new HttpInterface(request));
            }
            for (Map.Entry<String, String> entry : contextMap.entrySet()) {
                try {
                    if (contextMap.containsKey("graylogurl")) {
                        context.addExtra("graylogurl", contextMap.get("graylogurl"));
                    } else {
                        context.addTag(entry.getKey(), entry.getValue());
                    }
                    if (contextMap.containsKey("url")) {
                        SENTRY_CLIENT.sendMessage(contextMap.get("url"));
                    } else {
                        LOGGER.log(Level.ERROR, "url is not present");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.ERROR, "Cannot log to sentry");
                }
            }
        }
    }

    public static void sendSlowresponseToSentry(Map<String, String> contextMap, HttpServletRequest request) {
        if (FacilioProperties.isSentryEnabled()) {
            Context context = SENTRY_SLOWNESS_CLIENT.getContext();
            context.clear();
            if (request != null) {
                context.setHttp(new HttpInterface(request));
            }
            for (Map.Entry<String, String> entry : contextMap.entrySet()) {
                try {
                    if (contextMap.containsKey("graylogurl")) {
                        context.addExtra("graylogurl", contextMap.get("graylogurl"));
                    } else {
                        context.addTag(entry.getKey(), entry.getValue());
                    }
                    if (contextMap.containsKey("url")) {
                        SENTRY_SLOWNESS_CLIENT.sendMessage(contextMap.get("url"));
                    } else {
                        LOGGER.log(Level.ERROR, "url is not present");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.ERROR, "Cannot log to sentry");
                }
            }
        }
    }

    public static void sendSchedulerErrorsToSentry(Map<String, String> contextMap) {
        if (FacilioProperties.isSentryEnabled()) {
            Context context = SENTRY_SCHEDULER_CLIENT.getContext();
            context.clear();
            for (Map.Entry<String, String> entry : contextMap.entrySet()) {
                try {
                    if (contextMap.containsKey("graylogurl") ) {
                        context.addExtra("graylogurl", contextMap.get("graylogurl"));
                    } else if (contextMap.containsKey("exceptionStackTrace")) {
                        context.addExtra("exceptionStackTrace", contextMap.get("exceptionStackTrace"));
                    } else {
                        context.addTag(entry.getKey(), entry.getValue());
                    }
                    if (contextMap.containsKey("JobDetail")) {
                        SENTRY_SCHEDULER_CLIENT.sendMessage(contextMap.get("JobDetail"));
                    } else {
                        LOGGER.log(Level.ERROR, "JobDetail is not present");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.ERROR, "Cannot log to sentry");
                }
            }
        }
    }
    public static void handleSchedulerExceptions(JobContext jc, Exception e){
        String GRAY_LOG_URL = "https://logs.facilio.in/streams/000000000000000000000001/search?saved=5dcb93ea9b569c0b63c23fef&rangetype=relative&fields=message%2Csource&width=1536&highlightMessage=&relative=0&q=facility%3A%22production-scheduler%22%20AND%20source%3A%22";
        Long serverInfo = jc.getJobServerId();
        String threadName = Thread.currentThread().getName();
        String sourceIp = null;
        if (ServerInfo.getHostname() != null) {
            sourceIp = ServerInfo.getHostname();
        }

        String searchQuery = sourceIp+"%22%20AND%20thread%3A%20%22"+threadName+"%22";
        String grayLogSearchUrl = GRAY_LOG_URL + searchQuery;
        String jobDetail = jc.getJobId()+jc.getJobName()+jc.getExecutorName()+e.getClass().getName();
        Long orgId = jc.getOrgId();
        String stackTrace = ExceptionUtils.getStackTrace(e);
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("JobDetail",jobDetail);
        contextMap.put("orgId",String.valueOf(orgId));
        contextMap.put("serverInfo",String.valueOf(serverInfo));
        contextMap.put("threadname",threadName);
        contextMap.put("exceptionStackTrace",stackTrace);
        contextMap.put("sourceIp",sourceIp);
        contextMap.put("graylogurl", grayLogSearchUrl);
        sendSchedulerErrorsToSentry(contextMap);
    }
}

