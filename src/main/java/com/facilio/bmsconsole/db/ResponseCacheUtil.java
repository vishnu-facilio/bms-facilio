package com.facilio.bmsconsole.db;

public class ResponseCacheUtil {

	public static void addCache(long orgId, long userId, String requestURI, String contentHash, Object json) {
//		String cacheKey = CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash);
//		if (!LRUCache.getResponseCache().contains(cacheKey)) {
//			LRUCache.getResponseCache().put(cacheKey, json);
//		}
	}
	
	public static Object getCache(long orgId, long userId, String requestURI, String contentHash) {
//		String cacheKey = CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash);
//		Object object = LRUCache.getResponseCache().get(cacheKey);
//		return object;
		return null;
	}
	
	public static void removeCache(long orgId, long userId, String requestURI, String contentHash) {
//		String cacheKey = CacheUtil.RESPONSE_KEY(orgId, userId, requestURI, contentHash);
//		LRUCache.getResponseCache().remove(cacheKey);
	}
	
	public static void removeOrgCache(long orgId) {
//		LRUCache.getResponseCache().removeStartsWith(CacheUtil.ORG_KEY(orgId));
	}
}
