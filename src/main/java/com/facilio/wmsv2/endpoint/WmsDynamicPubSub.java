package com.facilio.wmsv2.endpoint;


import com.facilio.util.FacilioUtil;
import com.facilio.wmsv2.message.WebMessage;
import com.facilio.wmsv2.util.WmsUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPubSub;

import java.util.Set;

@Log4j
public class WmsDynamicPubSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String content) {
        try {
            if(StringUtils.isNotEmpty(content)) {
                if (WmsUtil.WMS_SUBSCRIBE_CHANNEL.equals(channel)) {
                    if(content.contains("*")) {
                        super.psubscribe(content);
                    } else {
                        super.subscribe(content);
                    }
                } else if(WmsUtil.WMS_UNSUBSCRIBE_CHANNEL.equals(channel)) {
                    if(content.contains("*")) {
                        super.punsubscribe(content);
                    } else {
                        super.unsubscribe(content);
                    }
                } else {
                    sendMessage(channel, content);
                }
            }
        } catch (Exception e) {
            LOGGER.error("WMS_LOG_ERROR :: Exception occurred while processing message "+content+" on channel "+channel, e);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        LOGGER.info("WMS_LOG :: Wms topic <"+channel+"> subscribed successfully");
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOGGER.info("WMS_LOG :: Wms topic <"+channel+"> unsubscribed successfully");
    }

    private void sendMessage(String wmsTopic, String content) throws Exception{
        WebMessage message = FacilioUtil.getAsBeanFromJson(FacilioUtil.parseJson(content), WebMessage.class);
        Set<String> sessionIds = SessionManager.getInstance().getTopicsLiveSession(wmsTopic);
        if (CollectionUtils.isEmpty(sessionIds)) {
            return;
        }
        for (String sessionId : sessionIds) {
            WmsUtil.sendObject(SessionManager.getInstance().getLiveSession(sessionId), message);
        }
    }

    public void onPMessage(String pattern, String channel, String content) {
        try {
           sendMessage(pattern, content);
        } catch (Exception e) {
            LOGGER.error("WMS_LOG_ERROR :: Exception occurred while processing pattern message "+content+" on channel "+channel+ " with pattern "+pattern, e);
        }
    }

    public void onPSubscribe(String pattern, int subscribedChannels) {
        LOGGER.info("WMS_LOG :: Wms topic-pattern <"+pattern+"> subscribed successfully");
    }

    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        LOGGER.info("WMS_LOG :: Wms topic-pattern <"+pattern+"> unsubscribed successfully");
    }


}
