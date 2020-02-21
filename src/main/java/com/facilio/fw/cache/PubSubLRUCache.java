package com.facilio.fw.cache;

import java.util.LinkedHashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.RedisManager;

import redis.clients.jedis.Jedis;

public class PubSubLRUCache<K, V> implements FacilioCache<K, V>  {
    
    private static final Logger LOGGER = LogManager.getLogger(PubSubLRUCache.class.getName());

    private String name;
    private RedisManager redis;
    private final LinkedHashMap<K, V> cache;

    public PubSubLRUCache(String name, int maxSize){
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
        System.out.println(cache.toString());
        cache.remove("4");
        System.out.println(cache.toString());
        cache.remove("5");
        System.out.println(cache.toString());
        cache.purgeCache();
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

    private void updateRedisDeleteCount() {
        if (AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementRedisDeleteCount(1);
        }
    }

    private void updateRedisDeleteTime(long time) {
        if (AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementRedisDeleteTime(time);
        }
    }

    @Override
    public boolean contains(K key) {
        return cache.containsKey(key.toString());
    }

    public void purgeCache() {
        purgeInRedis();
        cache.clear();
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    public V get(K key){
        try {
            V value = cache.get(key);
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

    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            return;
        }
        cache.put(key, value);
    }

    public void remove(K key) {
        if(cache.containsKey(key)) {
            cache.remove(key);
            deleteInRedis(key.toString());
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

    private void purgeInRedis() {
        if (redis != null) {
            try (Jedis jedis = redis.getJedis()) {
                jedis.publish(name, "all");
            } catch (Exception e) {
                LOGGER.info("Exception while purging data in Redis. ", e);
            }
        }
    }
}