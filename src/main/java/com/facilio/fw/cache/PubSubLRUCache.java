package com.facilio.fw.cache;

import java.util.LinkedHashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.RedisManager;

import redis.clients.jedis.Jedis;

public class PubSubLRUCache<K, V>{
    
    private static final Logger LOGGER = LogManager.getLogger(PubSubLRUCache.class.getName());

    private String name;
    private RedisManager redis;
    private final LinkedHashMap<String, Object> cache;

    private PubSubLRUCache(String name, int maxSize){
        this.name = name;
        cache = new LRUCacheLinkedHashMap<>(maxSize, 0.9f, true);
        redis = RedisManager.getInstance();
        subscribe();
    }

    public static void main(String[] args) {
        RedisManager.getInstance().connect();
        PubSubLRUCache<String, Object> cache = new PubSubLRUCache<>("test", 20);
        PubSubLRUCache<String, Object> cache1 = new PubSubLRUCache<>("notify", 20);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 31; i++) {
            for (int j = 0; j < 10; j++) {
                cache.get(""+j);
            }
            cache.put(""+i, new Integer(i));
        }

        for (int i = 0; i < 10; i++) {
            cache.get(""+i);
        }

        cache.remove("4");
        System.out.println(cache.toString());
        cache.remove("5");
        System.out.println(cache.toString());
        cache.remove("all");
        System.out.println(cache.toString());

    }

    private void subscribe() {
        if(redis != null) {
            redis.subscribe(new FacilioRedisPubSub(cache), name);
        }
    }
    
    public String toString() {
        return cache.toString();
    }

    private void updateRedisGetCount() {
        if (AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementRedisGetCount(1);
        }
    }
    private void updateRedisPutCount() {
        if (AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementRedisPutCount(1);
        }
    }
    private void updateRedisDeleteCount() {
        if (AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementRedisDeleteCount(1);
        }
    }
    private void updateRedisGetTime(long time) {
        if (AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementRedisGetTime(time);
        }
    }
    private void updateRedisPutTime(long time) {
        if (AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementRedisPutTime(time);
        }
    }
    private void updateRedisDeleteTime(long time) {
        if (AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementRedisDeleteTime(time);
        }
    }

    public void purgeCache() {
        purgeInRedis();
        cache.clear();
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    public Object get(String key){
        try {
            Object value = cache.get(key);
            if (value != null) {
                return value;
            } else {
                cache.remove(key);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return null;
    }


    private long getFromRedis(String redisKey) {
        Long redisTimestamp = -1L;
        if (redis != null) {
            long startTime = System.currentTimeMillis();
            updateRedisGetCount();
            try (Jedis jedis = redis.getJedis()) {
                String value = jedis.get(redisKey);
                if (value == null) {
                    redisTimestamp = Long.MAX_VALUE;
                }
                try {
                    redisTimestamp = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    redisTimestamp = Long.MAX_VALUE;
                }
            } catch (Exception e) {
                LOGGER.debug("Exception while getting key from Redis");
            } finally {
                updateRedisGetTime((System.currentTimeMillis() - startTime));
            }
        }
        return redisTimestamp;
    }

    public void put(String key, Object value) {
        if (cache.containsKey(key)) {
            return;
        }
        cache.put(key, value);
    //    putInRedis(key, value);
    }

    public void remove(String key) {
        if(cache.containsKey(key)) {
            cache.remove(key);
            deleteInRedis(key);
        }
    }

    private void deleteInRedis(String redisKey) {
        if (redis != null) {
            long startTime = System.currentTimeMillis();
            updateRedisDeleteCount();
            try (Jedis jedis = redis.getJedis()) {
                jedis.publish(name, redisKey);
            } catch (Exception e) {
                LOGGER.debug("Exception while removing key in Redis. ");
            } finally {
                updateRedisDeleteTime((System.currentTimeMillis()-startTime));
            }
        }
    }

    private void putInRedis(String redisKey, Object node) {
        if (redis != null) {
            long startTime = System.currentTimeMillis();
            updateRedisPutCount();
            try (Jedis jedis = redis.getJedis()) {
                jedis.setnx(redisKey, String.valueOf(node));
            } catch (Exception e) {
                LOGGER.debug("Exception while putting key in Redis. ");
            } finally {
                updateRedisPutTime((System.currentTimeMillis()-startTime));
            }
        }
    }

    private void purgeInRedis() {
        if (redis != null) {
            try (Jedis jedis = redis.getJedis()) {
                jedis.flushDB();
            } catch (Exception e) {
                LOGGER.info("Exception while purging data in Redis. ", e);
            }
        }
    }
}