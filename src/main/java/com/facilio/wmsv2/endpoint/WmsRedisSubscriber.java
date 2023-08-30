package com.facilio.wmsv2.endpoint;


import com.facilio.db.util.DBConf;
import com.facilio.fw.cache.RedisManager;
import lombok.extern.log4j.Log4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Log4j
public class WmsRedisSubscriber implements Runnable {

    private JedisPubSub pubSub;
    private String[] topics;

    public WmsRedisSubscriber(JedisPubSub pubSub, String[] topics) {
        this.pubSub = pubSub;
        this.topics = topics;
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName(DBConf.getInstance().getEnvironment() + "wms-redis-subscribe-thread");
            LOGGER.info("WMS_LOG :: Starting redis subscription thread - wms");
            try (Jedis jedis = RedisManager.getInstance().getJedis()) {
                jedis.subscribe(pubSub, this.topics);
            }
        } catch (Exception e) {
            LOGGER.error("WMS_ERROR :: Exception during Redis subscription: ", e);
        }
    }
}
