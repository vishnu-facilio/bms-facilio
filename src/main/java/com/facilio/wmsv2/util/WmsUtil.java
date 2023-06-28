package com.facilio.wmsv2.util;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.handler.WmsHandler;
import com.facilio.wmsv2.handler.WmsProcessor;
import com.facilio.wmsv2.message.TopicHandler;
import com.facilio.wmsv2.message.WebMessage;
import lombok.extern.log4j.Log4j;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.text.MessageFormat;

@Log4j
public class WmsUtil {


    public static final String WMS_SUBSCRIBE_CHANNEL = FacilioProperties.getEnvironment() + "__wms__subscribe__channel";
    public static final String WMS_UNSUBSCRIBE_CHANNEL = FacilioProperties.getEnvironment() + "__wms__unsubscribe__channel";

    public static final String WMS_ORG_TOPIC = FacilioProperties.getEnvironment() + "/wms/org/{0}/{1}";
    public static final String WMS_APP_TOPIC = FacilioProperties.getEnvironment() + "/wms/org/{0}/app/{1}/{2}";
    public static final String WMS_USER_TOPIC = FacilioProperties.getEnvironment() + "/wms/org/{0}/app/{1}/user/{2}/{3}";

    public static String convertToRedisTopic(LiveSession ls, String topic) {
        WmsHandler handler = WmsProcessor.getInstance().getHandler(topic);
        if(handler == null) {
            LOGGER.info("Topic not found in wmsTopicInfo.yml : "+topic );
            return null;
        }

        if(ls.getOrgId() == 0 || ls.getOrgId() == -1) {
            throw new RuntimeException("OrgId is not found in session. So can't subscribe topic :: "+topic);
        }

        TopicHandler.DELIVER_TO deliverTo = handler.getDeliverTo();
        switch (deliverTo) {
            case ORG: {
                return MessageFormat.format(WMS_ORG_TOPIC, ls.getOrgId(), topic);
            }
            case APP: {
                if(ls.getAppId() == -1) {
                    LOGGER.error("APP Scope - AppId is not found in session. So can't subscribe topic :: "+topic);
                    return null;
                }
                return MessageFormat.format(WMS_APP_TOPIC, ls.getOrgId(), ls.getAppId(), topic);
            }
            case USER: {
                if(ls.getAppId() == -1 || ls.getOuid() == -1) {
                    LOGGER.error("USER Scope - OrgId/AppId/UserId is not found in session. So can't subscribe topic :: "+topic);
                    return null;
                }
                return MessageFormat.format(WMS_USER_TOPIC, ls.getOrgId(), ls.getAppId(), ls.getOuid(), topic);
            }
        }
        return null;
    }

    public static String convertToRedisTopic(WebMessage message, String topic) {
        WmsHandler handler = WmsProcessor.getInstance().getHandler(topic);
        if(handler == null) {
            LOGGER.info("Topic not found in wmsTopicInfo.yml : "+topic );
            return null;
        }

        if(message.getOrgId() == null || message.getOrgId() == -1L) {
            LOGGER.info("OrgId is not found in message. So can't publish message to given topic :: "+topic);
            return null;
        }

        TopicHandler.DELIVER_TO deliverTo = handler.getDeliverTo();
        switch (deliverTo) {
            case ORG: {
                return MessageFormat.format(WMS_ORG_TOPIC, message.getOrgId(), topic);
            }
            case APP: {
                if(message.getAppId() == null) {
                    LOGGER.error("APP Scope - AppId is not found in message. So can't publish message to given topic :: "+topic);
                    return null;
                }
                return MessageFormat.format(WMS_APP_TOPIC, message.getOrgId(), message.getAppId(), topic);
            }

            case USER: {
                if(message.getAppId() == null || message.getTo() == null) {
                    LOGGER.error("USER Scope - OrgId/AppId/UserId is not found in message. So can't publish message to given topic :: "+topic);
                    return null;
                }
                return MessageFormat.format(WMS_USER_TOPIC, message.getOrgId(), message.getAppId(), message.getTo(), topic);
            }
        }

        return null;
    }


    public static void sendObject(LiveSession session, WebMessage message) {
        if (session == null) {
            return;
        }
        try {
            session.getSession().getBasicRemote().sendObject(message);
        } catch (IOException  | EncodeException ex) {
            ex.printStackTrace();
        }
    }
}
