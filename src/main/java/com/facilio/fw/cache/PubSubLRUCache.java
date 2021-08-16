package com.facilio.fw.cache;

import java.text.MessageFormat;
import java.util.*;

import com.facilio.aws.util.FacilioProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.RedisManager;

import redis.clients.jedis.Jedis;

public class PubSubLRUCache<V> implements FacilioCache<String, V>  {
    
    private static final Logger LOGGER = LogManager.getLogger(PubSubLRUCache.class.getName());

    private String name;
    private RedisManager redis;
    private final Map<String, V> cache;
    private long hit = 0;
    private long miss = 0;

    public PubSubLRUCache(String name, int maxSize){
        this.name = FacilioProperties.getEnvironment()+"-"+name;
        cache = Collections.synchronizedMap(new LRUCacheLinkedHashMap<>(maxSize, 0.9f, true));
        redis = RedisManager.getInstance();
        subscribe();
    }

    public static void main(String[] args) {
        RedisManager.getInstance().connect();
        PubSubLRUCache<Object> cache = new PubSubLRUCache<>("test", 20);
        PubSubLRUCache<Object> cache1 = new PubSubLRUCache<>("notify", 20);

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
            redis.subscribe(new FacilioRedisPubSub<>(this), name);
        }
    }
    
    public String toString() {
        double hitRatio =  ((hit*100d)/(hit+miss));
        return (" The current size "+cache.size()+"\n hitCount= "+hit+"\n missCount= "+miss+"\n Cache Hit Ratio= "+ hitRatio+"\n\n"+ keys());
    }

    private void updateRedisDeleteCount() {
        AccountUtil.incrementRedisDeleteCount(1);
    }

    private void updateRedisDeleteTime(long time) {
        AccountUtil.incrementRedisDeleteTime(time);
    }

    private void updateRedisGetCount() {
        AccountUtil.incrementRedisGetCount(1);
    }

    private void updateRedisGetTime(long time) {
        AccountUtil.incrementRedisGetTime(time);
    }

    @Override
    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    public void purgeCache() {
        cache.clear();
        deleteInRedis("all");
    }

    @Override
    public Set<String> keys() {
        return new HashSet<>(cache.keySet());
    }

    public V get(String key){
        long startTime = System.currentTimeMillis();
        try {
            updateRedisGetCount();
            V value = cache.get(key);
            if (value != null) {
                hit++;
                return value;
            } else {
                cache.remove(key);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        finally {
            updateRedisGetTime((System.currentTimeMillis()-startTime));
        }
        miss++;
        return null;
    }

    public void put(String key, V value) {
        if (cache.containsKey(key)) {
            return;
        }
        cache.put(key, value);
    }

    public void remove(String key) {
//        if(cache.containsKey(key)) {
            onlyRemove(key);
            deleteInRedis(key);
//        }
    }

    @Override
    public void removeStartsWith(String keyStartsWith) {
        for (String key : keys()) { //Because keyset will be synchronised as well. Anyway we won't directly remove with iterator so cloning is better
            if (key.startsWith(keyStartsWith)) {
                remove(key);
            }
        }
    }

    void onlyRemove(String key) {
        cache.remove(key);
    }

    void clear() {
        cache.clear();
    }

    private void deleteInRedis(String redisKey) {
        if (redis != null) {
            long startTime = System.currentTimeMillis();
            updateRedisDeleteCount();
            try (Jedis jedis = redis.getJedis()) {
                LOGGER.info(MessageFormat.format("Sending message, {0} ,on {1}", redisKey, name));
                jedis.publish(name, redisKey);
            } catch (Exception e) {
                LOGGER.error("Exception while removing key in Redis. ", e);
            } finally {
                updateRedisDeleteTime((System.currentTimeMillis()-startTime));
            }
        }
    }
}