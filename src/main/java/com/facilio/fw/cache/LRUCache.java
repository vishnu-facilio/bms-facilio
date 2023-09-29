package com.facilio.fw.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.facilio.bmsconsole.context.ScopingConfigCacheContext;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorCacheContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.collections.UniqueMap;
import com.facilio.datastructure.dag.DAGCache;
import com.facilio.ns.context.NameSpaceCacheContext;
import com.facilio.permission.context.BasePermissionContext;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.remotemonitoring.context.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.LicenseMapping;
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
	private static FacilioCache<String, Map<String,Long>> featureLicenseCachePS = new PubSubLRUCache<>("featureLicense", 1000);
	private static FacilioCache<String, Object> orgUnitCachePs = new PubSubLRUCache<>("orgUnit",1000);
	private static FacilioCache<String, Object> roleIdCachePs = new PubSubLRUCache<>("roleId",1000);
	private static FacilioCache<String, Object> roleNameCachePs = new PubSubLRUCache<>("roleName",1000);
	private static FacilioCache<String, Object> userSecurityPolicyPS = new PubSubLRUCache<>("userSecurityPolicyPS", 2000);
	private static FacilioCache<String, Map<String, Map<String, Object>>> globalVariable = new PubSubLRUCache<>("globalVariable", 2000);
	private static FacilioCache<String, Boolean> proxyUsersPS = new PubSubLRUCache<>("proxyUsers", 1000);
	private static FacilioCache<String, Object> agentCache = new PubSubLRUCache<>("agentCache", 1000);
	private static FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = new PubSubLRUCache<>("globalScopeVariableCache", 2000);
	private static FacilioCache<String, List<ScopingConfigCacheContext>> scopeConfigCache = new PubSubLRUCache<>("scopeConfigCache", 2000);
	private static FacilioCache<String, WebTabCacheContext> webTabCache = new PubSubLRUCache<>("webTabCache", 5000);
	private static FacilioCache<String, List<WebTabCacheContext>> webTabsCache = new PubSubLRUCache<>("webTabsCache", 5000);
	private static FacilioCache<String, List<WebTabGroupCacheContext>> webTabGroupCache = new PubSubLRUCache<>("webTabGroupCache", 5000);
	private static FacilioCache<String, List<TabIdAppIdMappingCacheContext>> tabAppModuleCache = new PubSubLRUCache<>("tabAppModuleCache", 5000);
	private static FacilioCache<String, DAGCache> globalScopeGraphCache = new PubSubLRUCache<>("globalScopeGraph", 2000);
	private static FacilioCache<String, ValueGeneratorCacheContext> valueGeneratorCache = new PubSubLRUCache<>("valueGeneratorCache", 2000);
	private static FacilioCache<String, NameSpaceCacheContext> nameSpaceCache = new PubSubLRUCache<>("nameSpaceCache", 2000);
	private static FacilioCache<String, NameSpaceCacheContext> nameSpaceParentCache = new PubSubLRUCache<>("nameSpaceParentCache", 2000);
	private static FacilioCache<String, List<Long>> nameSpaceIdsCache = new PubSubLRUCache("nameSpaceIdsCache", 2000);
	private static FacilioCache<String, Object> superAdminCache = new PubSubLRUCache("superAdminCache", 2000);
	private static FacilioCache<String, Long> peopleScopingCache = new PubSubLRUCache("peopleScopingCache", 2000);
	private static FacilioCache<String, List<PermissionSetContext>> peoplePermissionSets = new PubSubLRUCache("peoplePermissionSets", 2000);
	private static FacilioCache<String, List<Map<String,Object>>> permissionSetsTypePermissionCache = new PubSubLRUCache("permissionSetsTypePermissionCache", 2000);

	private static FacilioCache<String, Object> controllerCache = new PubSubLRUCache<>("controllerCache", 5000);
	private static FacilioCache<String, Object> appDomainBrandingCache = new PubSubLRUCache<>("appDomainBrandingCache", 100);

	private static FacilioCache<String, AlarmTypeContext> alarmTypeCache = new PubSubLRUCache<>("alarmTypeCache", 100);

	private static FacilioCache<String, List<AlarmDefinitionMappingContext>> alarmDefinitionMappingCache = new PubSubLRUCache<>("alarmDefinitionMappingCache", 1000);

	private static FacilioCache<String, List<AlarmDefinitionTaggingContext>> alarmDefinitionTaggingCache = new PubSubLRUCache<>("alarmDefinitionTaggingCache", 1000);
	private static FacilioCache<String, List<AlarmFilterRuleContext>> alarmFilterRuleCache = new PubSubLRUCache<>("alarmFilterRuleCache", 500);

	private static FacilioCache<String, List<FlaggedEventRuleContext>> flaggedEventsRuleCache = new PubSubLRUCache<>("flaggedEventsRuleCache", 500);

	private static FacilioCache<String, FlaggedEventRuleContext> flaggedEventRuleCache = new PubSubLRUCache<>("flaggedEventRuleCache", 500);
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
	public static FacilioCache<String, Boolean> getProxyUsersCache() {
		return proxyUsersPS;
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

	public static FacilioCache<String, Map<String,Long>> getFeatureLicenseCache() {
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

	public static FacilioCache<String, Object> getAgentCache() {return agentCache;}

	public static FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> getGlobalScopeVariableCache() {
		return globalScopeVariableCache;
	}

	public static FacilioCache<String, List<ScopingConfigCacheContext>> getScopeConfigCache() {
		return scopeConfigCache;
	}
	
	public static FacilioCache<String, WebTabCacheContext> getWebTabCache() {
		return webTabCache;
	}

	public static FacilioCache<String, List<TabIdAppIdMappingCacheContext>> getTabAppModuleCache() {
		return tabAppModuleCache;
	}


	public static FacilioCache<String, List<WebTabCacheContext>> getWebTabsCache() {
		return webTabsCache;
	}

	public static FacilioCache<String, List<WebTabGroupCacheContext>> getWebTabGroupCache() {
		return webTabGroupCache;
	}

	public static FacilioCache<String, DAGCache> getGlobalScopeGraphCache() {
		return globalScopeGraphCache;
	}
	public static FacilioCache<String, ValueGeneratorCacheContext> getValueGeneratorCache() {
		return valueGeneratorCache;
	}

	public static FacilioCache<String, NameSpaceCacheContext> getNameSpaceCache(){ return nameSpaceCache;}
	
	public static FacilioCache<String, NameSpaceCacheContext> getNameSpaceParentCache(){ return nameSpaceParentCache;}

	public static FacilioCache<String, List<Long>> getNameSpaceIdCache() {
		return nameSpaceIdsCache;
	}
	public static FacilioCache<String, Object> getSuperAdminCache(){
		return superAdminCache;
	}

	public static FacilioCache<String, Long> getPeopleScopingCache() {
		return peopleScopingCache;
	}
	public static FacilioCache<String, List<PermissionSetContext>> getPeoplePermissionSets(){
		return peoplePermissionSets;
	}

	public static FacilioCache<String, List<Map<String,Object>>> getPermissionSetsTypePermissionCache(){
		return permissionSetsTypePermissionCache;
	}
	public static FacilioCache<String, Object> getControllerCache() {return controllerCache;}
	public static FacilioCache<String, Object> getAppDomainBrandingCache() {return appDomainBrandingCache;}




	public static FacilioCache<String, AlarmTypeContext> getAlarmTypeCache() {
		return alarmTypeCache;
	}
	public static FacilioCache<String, List<AlarmDefinitionMappingContext>> getAlarmDefinitionMappingCache() {
		return alarmDefinitionMappingCache;
	}

	public static FacilioCache<String, List<AlarmDefinitionTaggingContext>> getAlarmDefinitionTaggingCache() {
		return alarmDefinitionTaggingCache;
	}
	public static FacilioCache<String, List<AlarmFilterRuleContext>> getAlarmFilterRuleCache() {
		return alarmFilterRuleCache;
	}

	public static FacilioCache<String, List<FlaggedEventRuleContext>> getFlaggedEventsRuleCache() {
		return flaggedEventsRuleCache;
	}
	public static FacilioCache<String, FlaggedEventRuleContext> getFlaggedEventRuleCache() {
		return flaggedEventRuleCache;
	}

}