package com.facilio.cache;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.aws.util.AwsUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {
	
	private static final Logger LOGGER = Logger.getLogger(RedisManager.class.getName());
	
	private static final RedisManager instance = new RedisManager();
	
	private static JedisPool pool;
	
	private RedisManager() {}
	
	public static RedisManager getInstance() {
		return instance;
	}
	
	public boolean isRedisEnabled() {
		return Boolean.parseBoolean(AwsUtil.getConfig("redis.enabled"));
	}
	
	public void connect() {
		
		if (!isRedisEnabled()) {
			LOGGER.log(Level.WARNING, "Redis disabled, so redis connection pool not initilized.");
			return;
		}

		// Create and set a JedisPoolConfig
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		// Maximum active connections to Redis instance
		poolConfig.setMaxTotal(20);
		// Tests whether connection is dead when connection
		// retrieval method is called
		poolConfig.setTestOnBorrow(true);
		/* Some extra configuration */
		// Tests whether connection is dead when returning a
		// connection to the pool
		poolConfig.setTestOnReturn(true);
		// Number of connections to Redis that just sit there
		// and do nothing
		poolConfig.setMaxIdle(10);
		// Minimum number of idle connections to Redis
		// These can be seen as always open and ready to serve
		poolConfig.setMinIdle(5);
		// Tests whether connections are dead during idle periods
		poolConfig.setTestWhileIdle(true);
		// Maximum number of connections to test in each idle check
		poolConfig.setNumTestsPerEvictionRun(10);
		// Idle connection checking period
		poolConfig.setTimeBetweenEvictionRunsMillis(60000);
		// Create the jedisPool
		pool = new JedisPool(poolConfig, AwsUtil.getConfig("redis.host"), Integer.parseInt(AwsUtil.getConfig("redis.port")));
		
		LOGGER.log(Level.INFO, "Redis connection pool successfully initilized..");
	}
	
	public void release() {
		if (pool != null) {
			pool.destroy();
			LOGGER.log(Level.INFO, "Redis connection pool destroyed..");
		}
	}
	
	public Jedis getJedis() {
		if (pool != null) {
			return pool.getResource();
		}
		return null;
	}
}