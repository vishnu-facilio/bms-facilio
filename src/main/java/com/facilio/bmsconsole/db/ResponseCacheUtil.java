package com.facilio.bmsconsole.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.MapUtils;

import com.facilio.cache.CacheUtil;
import com.facilio.fw.LRUCache;

public class ResponseCacheUtil {

	private static final Map<Long, Map<String, Object>> RESPONSE_CACHE = new ConcurrentHashMap<>();
	
	public static void addCache(long orgId, long userId, String requestURI, String contentHash, Object json) {
		long currentTimeMillis = System.currentTimeMillis();
		String cacheKey = CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash);
		Map<String, Object> map = RESPONSE_CACHE.get(orgId);
		if (map == null) {
			map = new HashMap<>();
			RESPONSE_CACHE.put(orgId, map);
		}
		if (!LRUCache.getResponseCache().contains(cacheKey)) {
			map.put(cacheKey, json);
			LRUCache.getResponseCache().put(cacheKey, currentTimeMillis);
		}
	}
	
	public static Object getCache(long orgId, long userId, String requestURI, String contentHash) {
		String cacheKey = CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash);
		Map<String, Object> map = RESPONSE_CACHE.get(orgId);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		Object object = LRUCache.getResponseCache().get(cacheKey);
		if (object == null) {
			return null;
		}
		return map.get(cacheKey);
	}
	
	public static Object removeCache(long orgId, long userId, String requestURI, String contentHash) {
		String cacheKey = CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash);
		Map<String, Object> map = RESPONSE_CACHE.get(orgId);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		LRUCache.getResponseCache().remove(cacheKey);
		return map.remove(cacheKey);
	}
	
	public static void removeOrgCache(long orgId) {
		Map<String, Object> map = RESPONSE_CACHE.get(orgId);
		if (MapUtils.isEmpty(map)) {
			return;
		}
		for (String key : map.keySet()) {
			LRUCache.getResponseCache().remove(key);
		}
		RESPONSE_CACHE.remove(orgId);
	}
}
