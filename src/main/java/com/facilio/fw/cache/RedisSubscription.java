package com.facilio.fw.cache;

import com.facilio.cache.RedisManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;

public class RedisSubscription implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(RedisSubscription.class.getName());
    private static final HashMap<String, JedisPubSub> SUBSCRIPTION_CHANNELS = new HashMap<>();

    private JedisPubSub pubSub;
    private String channelName;

    public RedisSubscription(JedisPubSub pubSub, String channelName) {
        this.pubSub = pubSub;
        this.channelName = channelName;
        LOGGER.info("started "+ channelName);
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName(channelName + "-redis-subscribe");
            RedisManager.getInstance().getJedis().subscribe(pubSub, channelName);
        } catch (Exception e) {
            LOGGER.info("exception in subscription: "+ channelName);
            pubSub.onMessage(channelName, "all");
            SUBSCRIPTION_CHANNELS.put(channelName, pubSub);
        }
    }
}
