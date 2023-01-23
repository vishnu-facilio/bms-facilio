package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorCacheContext;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;

public class ValueGeneratorCacheBeanImpl extends ValueGeneratorBeanImpl implements ValueGeneratorBean {

    @Override
    public ValueGeneratorCacheContext getValueGenerator(Long id) throws Exception {
        FacilioCache<String, ValueGeneratorCacheContext> valueGeneratorCache = LRUCache.getValueGeneratorCache();
        String key = CacheUtil.ORG_VAL_GEN_KEY(AccountUtil.getCurrentOrg().getId(), id);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(valueGeneratorCache, key, () -> {
            return super.getValueGenerator(id);
        });
    }
}