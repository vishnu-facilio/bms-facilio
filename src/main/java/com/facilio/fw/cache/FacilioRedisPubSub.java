package com.facilio.fw.cache;

import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPubSub;

import java.util.LinkedHashMap;

public class FacilioRedisPubSub extends JedisPubSub {

    private static final Logger LOGGER = Logger.getLogger(FacilioRedisPubSub.class.getName());

    private LinkedHashMap<String, Object> cache;

    public FacilioRedisPubSub(LinkedHashMap<String, Object> map) {
        cache = map;
    }

    public void onMessage(String channel, String message) {
        LOGGER.info("Received message, " + message + " ,on " + channel);
        if(message != null) {
            if (message.equals("all")) {
                cache.clear();
            } else {
                cache.remove(message);
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
