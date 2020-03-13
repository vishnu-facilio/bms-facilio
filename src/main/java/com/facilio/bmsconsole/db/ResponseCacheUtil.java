package com.facilio.bmsconsole.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.MapUtils;

import com.facilio.cache.CacheUtil;
import com.facilio.fw.LRUCache;

public class ResponseCacheUtil {

	public static void addCache(long orgId, long userId, String requestURI, String contentHash, Object json) {
		long currentTimeMillis = System.currentTimeMillis();
		String cacheKey = CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash);
		if (!LRUCache.getResponseCache().contains(cacheKey)) {
			LRUCache.getResponseCache().put(cacheKey, currentTimeMillis);
		}
	}
	
	public static Object getCache(long orgId, long userId, String requestURI, String contentHash) {
		String cacheKey = CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash);
		Object object = LRUCache.getResponseCache().get(cacheKey);
		return object;
	}
	
	public static void removeCache(long orgId, long userId, String requestURI, String contentHash) {
		String cacheKey = CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash);
		LRUCache.getResponseCache().remove(cacheKey);
	}
	
	public static void removeOrgCache(long orgId) {
		LRUCache.getResponseCache().removeStartsWith(String.valueOf(orgId));
	}
}
