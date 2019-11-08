package com.facilio.util;

import com.facilio.aws.util.FacilioProperties;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.UserBuilder;

import java.util.Map;


public class SentryUtil {

    private static final SentryClient SENTRY_CLIENT = SentryClientFactory.sentryClient(FacilioProperties.getsentrydsn());

    public static void sendToSentry(Map<String, String> contextMap, String requestUrl, String userId) {
        if(FacilioProperties.isSentryEnabled()){
            Context context = SENTRY_CLIENT.getContext();
            context.clear();
            context.setUser(new UserBuilder().setId(userId).build());
            for (Map.Entry<String, String> entry : contextMap.entrySet()) {
                context.addTag(entry.getKey(), entry.getValue());
            }
            try {
                SENTRY_CLIENT.sendMessage(requestUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
