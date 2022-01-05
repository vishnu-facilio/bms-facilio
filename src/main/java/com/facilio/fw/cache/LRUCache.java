package com.facilio.fw.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.facilio.collections.UniqueMap;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;

import redis.clients.jedis.Jedis;

@Log4j
public class LRUCache {
	private static FacilioCache<String, Object> fieldCachePS = new PubSubLRUCache<>("fieldCache", 2000);
	private static FacilioCache<String, Object> fieldNameCachePS = new PubSubLRUCache<>("fieldNameCache", 2000);
	private static FacilioCache<String, Object> moduleFieldCachePS = new PubSubLRUCache<>("moduleFieldCache", 2000);
	private static FacilioCache<String, Object> userSessionCachePS = new PubSubLRUCache<>("userSessionCache", 300);
	private static FacilioCache<String, Object> moduleCachePS = new PubSubLRUCache<>("moduleCache", 2000);
	private static FacilioCache<String, Long> queryCachePS = new PubSubLRUCache<>("queryCache", 500);
	private static FacilioCache<String, Object> responseCachePS = new PubSubLRUCache<>("responseCache", 5000);
	private static FacilioCache<String, Long> featureLicenseCachePS = new PubSubLRUCache<>("featureLicense", 1000);
	private static FacilioCache<String, Object> orgUnitCachePs = new PubSubLRUCache<>("orgUnit",1000);
	private static FacilioCache<String, Object> roleIdCachePs = new PubSubLRUCache<>("roleId",1000);
	private static FacilioCache<String, Object> roleNameCachePs = new PubSubLRUCache<>("roleName",1000);
	private static FacilioCache<String, Object> userSecurityPolicyPS = new PubSubLRUCache<>("userSecurityPolicyPS", 2000);
	private static FacilioCache<String, Map<String, Map<String, Object>>> globalVariable = new PubSubLRUCache<>("globalVariable", 2000);

	public static void purgeAllCache() {
		RedisManager.purgeAllCache();
	}
	public static FacilioCache<String, Object> getModuleFieldsCache() {
		return moduleFieldCachePS;
	}
	public static FacilioCache<String, Object> getFieldsCache() {
		return fieldCachePS;
	}
	public static FacilioCache<String, Object> getFieldNameCache() {
		return fieldNameCachePS;
	}
	public static FacilioCache<String, Object> getUserSessionCache() {
		return userSessionCachePS;
	}

	public static FacilioCache<String, Object> getUserSecurityPolicyCache() {
		return userSecurityPolicyPS;
	}

	public static FacilioCache<String, Object> getModuleCache() {
		return moduleCachePS;
	}
	public static FacilioCache<String, Long> getQueryCache() {
		return queryCachePS;
	}
	public static FacilioCache<String, Object> getResponseCache() {
		return responseCachePS;
	}
	public static FacilioCache<String, Map<String, Map<String, Object>>> getGlobalVariableCache() {
		return globalVariable;
	}

	public static FacilioCache<String, Long> getFeatureLicenseCache() {
		return featureLicenseCachePS;
	}

	public static FacilioCache<String, Object> getOrgUnitCachePs(){
		return orgUnitCachePs;
	}

	public static FacilioCache<String,Object> getRoleIdCachePs(){
		return roleIdCachePs;
	}

	public static FacilioCache<String,Object> getRoleNameCachePs(){
		return roleNameCachePs;
	}
}