package com.facilio.fw.cache;

import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPubSub;

import java.text.MessageFormat;

public class FacilioRedisPubSub<V> extends JedisPubSub {

    private static final Logger LOGGER = Logger.getLogger(FacilioRedisPubSub.class.getName());

    private PubSubLRUCache<V> cache;

    public FacilioRedisPubSub(PubSubLRUCache<V> cache) {
        this.cache = cache;
    }

    public void onMessage(String channel, String message) {
        LOGGER.info(MessageFormat.format("Received message, {0} ,on {1}", message, channel));
        if(message != null) {
            if (message.equals("all")) {
                cache.clear();
            } else {
                cache.onlyRemove(message);
            }
        }
    }

    public void onPMessage(String pattern, String channel, String message) {
        LOGGER.info("Published message : " + message + " ,on " + channel  +" pattern " + pattern);
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        LOGGER.info("Subscribed to channel : " + channel + " : " + subscribedChannels);
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
    }

    public void onPUnsubscribe(String pattern, int subscribedChannels) {
    }

    public void onPSubscribe(String pattern, int subscribedChannels) {
    }

    public void onPong(String pattern) {

    }
}
