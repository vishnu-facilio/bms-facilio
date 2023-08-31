package com.facilio.wmsv2.endpoint;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.fw.cache.RedisManager;
import com.facilio.wmsv2.handler.WmsProcessor;
import com.facilio.wmsv2.message.WebMessage;
import com.facilio.wmsv2.util.WmsUtil;
import lombok.extern.log4j.Log4j;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j
public class RedisBroadcaster extends DefaultBroadcaster {

    private static RedisBroadcaster broadcaster = new RedisBroadcaster();
    private Set<String> subscribedTopics = new HashSet<>();
    private Set<String> subscribedPatterns = new HashSet<>();

    private int publishedMessage = 0;
    private int totalMessage = 0;

    public static DefaultBroadcaster getBroadcaster() {
        WmsProcessor.getInstance();
        return broadcaster;
    }

    RedisBroadcaster() {
        String[] defaultTopics = new String[] {
                WmsUtil.WMS_SUBSCRIBE_CHANNEL,
                WmsUtil.WMS_UNSUBSCRIBE_CHANNEL
        };
        new Thread(new WmsRedisSubscriber(new WmsDynamicPubSub(), defaultTopics)).start();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        int timeInterval = Integer.parseInt(FacilioProperties.getConfig("wms.unsub.interval", "3"));
        executor.scheduleAtFixedRate(new WmsRedisUnsubscriber(timeInterval), 0, timeInterval, TimeUnit.MINUTES);
    }

    @Override
    public void subscribe(String... topics) {
        try(Jedis jedis = RedisManager.getInstance().getJedis()) {
            for(String topic : topics) {
                subscribe(jedis, topic, topic.contains("*"));
            }
        } catch (Exception ex) {
             LOGGER.error("WMS_ERROR :: Failed to get connection from jedis pool", ex);
        }
    }

    private void subscribe(Jedis jedis, String topic, boolean isPattern) {
        if(isPattern) {
            if(subscribedPatterns.contains(topic)) {
                LOGGER.info("WMS_LOG :: Given patterned-topic already subscribed :: "+topic);
                return;
            } else {
                subscribedPatterns.add(topic);
            }
        } else {
            if(subscribedTopics.contains(topic)) {
                LOGGER.info("WMS_LOG :: Given topic already subscribed :: "+topic);
                return;
            } else {
                subscribedTopics.add(topic);
            }
        }
        jedis.publish(WmsUtil.WMS_SUBSCRIBE_CHANNEL, topic);
        LOGGER.info("WMS_LOG :: Subscribed new topic :: "+topic);
    }

    @Override
    public void unsubscribe(String... topics) {
        try(Jedis jedis = RedisManager.getInstance().getJedis()) {
            for (String topic : topics) {
                unsubscribe(jedis, topic, topic.contains("*"));
            }
        } catch (Exception ex) {
            LOGGER.error("WMS_ERROR :: Failed to get connection from jedis pool", ex);
        }
    }

    private void unsubscribe(Jedis jedis, String topic, boolean isPattern) {
        if(!SessionManager.getInstance().getTopicsLiveSession(topic).isEmpty()) {
            return;
        }
        if(isPattern) {
            if(subscribedPatterns.contains(topic)) {
                subscribedPatterns.remove(topic);
            } else {
                LOGGER.info("WMS_LOG :: Given patterned-topic not subscribed :: "+topic);
                return;
            }
        } else {
            if(subscribedTopics.contains(topic)) {
                subscribedTopics.remove(topic);
            } else {
                LOGGER.info("WMS_LOG :: Given topic not subscribed :: "+topic);
                return;
            }
        }
        jedis.publish(WmsUtil.WMS_UNSUBSCRIBE_CHANNEL, topic);
        LOGGER.info("WMS_LOG :: UnSubscribed topic :: "+topic);
    }

    @Override
    protected void broadcast(WebMessage message) {
        String topic = message.getTopic();
        String content = message.toJson().toJSONString();
        try(Jedis jedis = RedisManager.getInstance().getJedis()) {
            long numSubscribers = jedis.publish(topic, content);
            totalMessage++;
            if (numSubscribers > 0) {
                publishedMessage++;
                LOGGER.info("WMS_LOG :: Message published successfully to " + numSubscribers + " subscribers. " +
                        "Total published message since restart of the server "+publishedMessage);
            }
            if(totalMessage % 100 == 0) {
                LOGGER.info("WMS_LOG :: Total "+totalMessage+" messages. Published "+publishedMessage+" messages");
            }
        } catch (Exception ex) {
            LOGGER.error("WMS_ERROR :: Failed to get connection from jedis pool", ex);
        }
    }

}
