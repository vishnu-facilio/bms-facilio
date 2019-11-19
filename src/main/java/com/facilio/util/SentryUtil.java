package com.facilio.util;

import com.facilio.aws.util.FacilioProperties;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.interfaces.HttpInterface;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public class SentryUtil {

    private static final SentryClient SENTRY_CLIENT = SentryClientFactory.sentryClient(FacilioProperties.getsentrydsn());
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
}