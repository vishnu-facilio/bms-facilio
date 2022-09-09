package com.facilio.beans;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;
import java.util.Map;

public class GlobalScopeBeanCacheImpl extends GlobalScopeBeanImpl implements GlobalScopeBean {
    @Override
    public Map<String, Pair<GlobalScopeVariableContext,ValueGeneratorContext>> getAllScopeVariableAndValueGen() throws Exception {
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = FWLRUCaches.getGlobalScopeVariableCache();
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(globalScopeVariableCache, key, () -> {
            return super.getAllScopeVariableAndValueGen();
        });
    }

    @Override
    public Long addScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = FWLRUCaches.getGlobalScopeVariableCache();
        Long id = super.addScopeVariable(scopeVariable);
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        globalScopeVariableCache.remove(key);
        return id;
    }

    @Override
    public Long updateScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = FWLRUCaches.getGlobalScopeVariableCache();
        Long id = super.updateScopeVariable(scopeVariable);
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        globalScopeVariableCache.remove(key);
        return id;
    }

    @Override
    public void addScopeVariableModulesFields(List<ScopeVariableModulesFields> scopeVariableModuleFields) throws Exception {
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = FWLRUCaches.getGlobalScopeVariableCache();
        super.addScopeVariableModulesFields(scopeVariableModuleFields);
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        globalScopeVariableCache.remove(key);
    }

    @Override
    public void deleteScopeVariableModulesFieldsByScopeVariableId(Long scopeVariableId) throws Exception {
        FacilioCache<String, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>>> globalScopeVariableCache = FWLRUCaches.getGlobalScopeVariableCache();
        super.deleteScopeVariableModulesFieldsByScopeVariableId(scopeVariableId);
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        globalScopeVariableCache.remove(key);
    }
}