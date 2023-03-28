package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ScopingConfigCacheContext;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import java.util.List;

public class UserScopeBeanImplCache extends UserScopeBeanImpl implements UserScopeBean {


    public List<ScopingConfigCacheContext> getScopingConfig(long scopingId) throws Exception {
        FacilioCache<String, List<ScopingConfigCacheContext>> scopeConfigCache = LRUCache.getScopeConfigCache();
        String key = CacheUtil.USER_SCOPE_CONFIG_VARIABLE_KEY(AccountUtil.getCurrentOrg().getId(),scopingId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(scopeConfigCache, key, () -> {
            return super.getScopingConfig(scopingId);
        });
    }


    public void deleteScopingConfigForId(long scopingConfigId) throws Exception {
        FacilioCache<String, List<ScopingConfigCacheContext>> scopeConfigCache = LRUCache.getScopeConfigCache();
        super.deleteScopingConfigForId(scopingConfigId);
        scopeConfigCache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
    }


    public void addScopingConfigForApp(List<ScopingConfigContext> scoping) throws Exception {
        FacilioCache<String, List<ScopingConfigCacheContext>> scopeConfigCache = LRUCache.getScopeConfigCache();
        super.addScopingConfigForApp(scoping);
        scopeConfigCache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
    }

    public void deleteScopingConfig(long scopingId) throws Exception {
        FacilioCache<String, List<ScopingConfigCacheContext>> scopeConfigCache = LRUCache.getScopeConfigCache();
        super.deleteScopingConfig(scopingId);
        scopeConfigCache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
    }

    public void deleteUserScoping (Long userScopingId) throws Exception {
        FacilioCache<String, List<ScopingConfigCacheContext>> scopeConfigCache = LRUCache.getScopeConfigCache();
        super.deleteUserScoping(userScopingId);
        scopeConfigCache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
    }

    public void updateScopingConfigForUserScoping(List<ScopingConfigContext> userScopingConfigList, Long userScopingId) throws Exception {
        FacilioCache<String, List<ScopingConfigCacheContext>> scopeConfigCache = LRUCache.getScopeConfigCache();
        super.updateScopingConfigForUserScoping(userScopingConfigList, userScopingId);
        scopeConfigCache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
    }

    public void updatePeopleScoping(Long peopleId,Long scopingId) throws Exception {
        FacilioCache<String, Long> scopeConfigCache = LRUCache.getPeopleScopingCache();
        String key = CacheUtil.PEOPLE_ID_KEY(AccountUtil.getCurrentOrg().getId(),peopleId);
        scopeConfigCache.remove(key);
        super.updatePeopleScoping(peopleId,scopingId);
    }
    public Long getPeopleScoping(Long peopleId) throws Exception {
        FacilioCache<String, Long> scopeConfigCache = LRUCache.getPeopleScopingCache();
        String key = CacheUtil.PEOPLE_ID_KEY(AccountUtil.getCurrentOrg().getId(),peopleId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(scopeConfigCache, key, () -> {
            return super.getPeopleScoping(peopleId);
        });
    }
}