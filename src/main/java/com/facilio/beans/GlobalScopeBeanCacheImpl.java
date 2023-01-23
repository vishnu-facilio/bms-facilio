package com.facilio.beans;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.bmsconsoleV3.util.GlobalScopeUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.datastructure.dag.DAG;
import com.facilio.datastructure.dag.DAGCache;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GlobalScopeBeanCacheImpl extends GlobalScopeBeanImpl implements GlobalScopeBean {
    @Override
    public Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> getAllScopeVariableAndValueGen(@NonNull Long appId) throws Exception {
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
        String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), appId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(globalScopeVariableCache, key, () -> {
            return super.getAllScopeVariableAndValueGen(appId);
        });
    }

    @Override
    public Long addScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        if (scopeVariable != null) {
            FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
            FacilioCache<String, DAGCache> globalScopeGraphCache = LRUCache.getGlobalScopeGraphCache();
            Long id = super.addScopeVariable(scopeVariable);
            String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), scopeVariable.getAppId());
            globalScopeVariableCache.remove(key);
            globalScopeGraphCache.remove(key);
            return id;
        }
        return null;
    }

    @Override
    public Long updateScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        if (scopeVariable != null) {
            FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
            FacilioCache<String, DAGCache> globalScopeGraphCache = LRUCache.getGlobalScopeGraphCache();
            Long id = super.updateScopeVariable(scopeVariable);
            String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), scopeVariable.getAppId());
            globalScopeVariableCache.remove(key);
            globalScopeGraphCache.remove(key);
            return id;
        }
        return null;
    }

    @Override
    public void addScopeVariableModulesFields(List<ScopeVariableModulesFields> scopeVariableModuleFields) throws Exception {
        if (CollectionUtils.isNotEmpty(scopeVariableModuleFields)) {
            for (ScopeVariableModulesFields scopeVariableModuleField : scopeVariableModuleFields) {
                GlobalScopeVariableContext scopeVariable = getScopeVariable(scopeVariableModuleField.getScopeVariableId());
                if (scopeVariable != null) {
                    FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
                    FacilioCache<String, DAGCache> globalScopeGraphCache = LRUCache.getGlobalScopeGraphCache();
                    super.addScopeVariableModulesFields(Collections.singletonList(scopeVariableModuleField));
                    String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), scopeVariable.getAppId());
                    globalScopeVariableCache.remove(key);
                    globalScopeGraphCache.remove(key);
                }
            }
        }
    }

    @Override
    public void deleteScopeVariableModulesFieldsByScopeVariableId(Long scopeVariableId) throws Exception {
        GlobalScopeVariableContext scopeVariable = getScopeVariable(scopeVariableId);
        if (scopeVariable != null) {
            FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
            FacilioCache<String, DAGCache> globalScopeGraphCache = LRUCache.getGlobalScopeGraphCache();
            super.deleteScopeVariableModulesFieldsByScopeVariableId(scopeVariableId);
            String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), scopeVariable.getAppId());
            globalScopeVariableCache.remove(key);
            globalScopeGraphCache.remove(key);
        }
    }

    @Override
    public void deleteScopeVariable(Long id,Long appId) throws Exception {
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
        FacilioCache<String, DAGCache> globalScopeGraphCache = LRUCache.getGlobalScopeGraphCache();
        super.deleteScopeVariable(id,appId);
        String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), appId);
        globalScopeVariableCache.remove(key);
        globalScopeGraphCache.remove(key);
    }

    @Override
    public void setStatus(Long appId, Long scopeVariableId,boolean status) throws Exception {
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
        FacilioCache<String, DAGCache> globalScopeGraphCache = LRUCache.getGlobalScopeGraphCache();
        super.setStatus(appId, scopeVariableId, status);
        String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), appId);
        globalScopeVariableCache.remove(key);
        globalScopeGraphCache.remove(key);
    }

    @Override
    public void setSwitchStatus(Long appId, Long scopeVariableId,boolean status) throws Exception {
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
        FacilioCache<String, DAGCache> globalScopeGraphCache = LRUCache.getGlobalScopeGraphCache();
        super.setSwitchStatus(appId, scopeVariableId,status);
        String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), appId);
        globalScopeVariableCache.remove(key);
        globalScopeGraphCache.remove(key);
    }

    @Override
    public DAGCache getGlobalScopeGraph(Long appId) throws Exception {
        FacilioCache<String, DAGCache> globalScopeGraphCache = LRUCache.getGlobalScopeGraphCache();
        String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), appId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(globalScopeGraphCache, key, () -> {
            return super.getGlobalScopeGraph(appId);
        });
    }
}