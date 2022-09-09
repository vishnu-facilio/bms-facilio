package com.facilio.beans;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;
import java.util.Map;

public class GlobalScopeBeanCacheImpl extends GlobalScopeBeanImpl implements GlobalScopeBean {
    @Override
    public Map<String, Pair<GlobalScopeVariableContext,ValueGeneratorContext>> getAllScopeVariableAndValueGen(@NonNull Long appId) throws Exception {
        checkAppAndThrowError(appId);
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
        String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(),appId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(globalScopeVariableCache, key, () -> {
            return super.getAllScopeVariableAndValueGen(appId);
        });
    }

    @Override
    public Long addScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        if(scopeVariable != null) {
            checkAppAndThrowError(scopeVariable.getAppId());
            FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
            Long id = super.addScopeVariable(scopeVariable);
            String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), scopeVariable.getAppId());
            globalScopeVariableCache.remove(key);
            return id;
        }
        return null;
    }

    @Override
    public Long updateScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        if(scopeVariable != null) {
            checkAppAndThrowError(scopeVariable.getAppId());
            FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
            Long id = super.updateScopeVariable(scopeVariable);
            String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(), scopeVariable.getAppId());
            globalScopeVariableCache.remove(key);
            return id;
        }
        return null;
    }

    @Override
    public void addScopeVariableModulesFields(List<ScopeVariableModulesFields> scopeVariableModuleFields) throws Exception {
        if (CollectionUtils.isNotEmpty(scopeVariableModuleFields)){
            for(ScopeVariableModulesFields scopeVariableModuleField : scopeVariableModuleFields) {
                GlobalScopeVariableContext scopeVariable = getScopeVariable(scopeVariableModuleField.getScopeVariableId());
                if(scopeVariable != null) {
                    checkAppAndThrowError(scopeVariable.getAppId());
                    FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
                    super.addScopeVariableModulesFields(scopeVariableModuleFields);
                    String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(),scopeVariable.getAppId());
                    globalScopeVariableCache.remove(key);
                }
            }
        }
    }

    @Override
    public void deleteScopeVariableModulesFieldsByScopeVariableId(Long scopeVariableId) throws Exception {
        GlobalScopeVariableContext scopeVariable = getScopeVariable(scopeVariableId);
        if(scopeVariable != null) {
            checkAppAndThrowError(scopeVariable.getAppId());
            FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = LRUCache.getGlobalScopeVariableCache();
            super.deleteScopeVariableModulesFieldsByScopeVariableId(scopeVariableId);
            String key = CacheUtil.GLOBAL_SCOPE_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(),scopeVariable.getAppId());
            globalScopeVariableCache.remove(key);
        }
    }

    private void checkAppAndThrowError(Long appId){
        if(appId == null){
            throw new IllegalArgumentException("Appid cannot be null");
        }
    }
}