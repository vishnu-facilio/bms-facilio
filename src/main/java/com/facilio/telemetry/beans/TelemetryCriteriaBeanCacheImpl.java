package com.facilio.telemetry.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.remotemonitoring.context.AlarmDefinitionMappingContext;
import com.facilio.telemetry.context.TelemetryCriteriaCacheContext;
import com.facilio.telemetry.context.TelemetryCriteriaContext;

import java.util.List;

public class TelemetryCriteriaBeanCacheImpl extends TelemetryCriteriaBeanImpl implements TelemetryCriteriaBean {
    @Override
    public TelemetryCriteriaCacheContext fetchTelemetryCriteria(long telemetryCriteriaId) throws Exception {
        FacilioCache<String, TelemetryCriteriaCacheContext> telemetryCriteriaCache = LRUCache.getTelemetryCriteriaCache();
        String key = CacheUtil.TELEMETRY_CRITERIA_KEY(AccountUtil.getCurrentOrg().getId(), telemetryCriteriaId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(telemetryCriteriaCache, key, () -> {
            return super.fetchTelemetryCriteria(telemetryCriteriaId);
        });
    }
}