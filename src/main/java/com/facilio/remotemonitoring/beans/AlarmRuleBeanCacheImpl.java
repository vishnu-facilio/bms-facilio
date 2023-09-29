package com.facilio.remotemonitoring.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.remotemonitoring.context.*;
import lombok.NonNull;

import java.util.List;

public class AlarmRuleBeanCacheImpl extends AlarmRuleBeanImpl implements AlarmRuleBean {


    @Override
    public List<AlarmDefinitionMappingContext> getAlarmDefinitionMappingsForClient(@NonNull Long clientId) throws Exception {
        FacilioCache<String, List<AlarmDefinitionMappingContext>> alarmDefinitionMappingCache = LRUCache.getAlarmDefinitionMappingCache();
        String key = CacheUtil.CLIENT_ID_KEY(AccountUtil.getCurrentOrg().getId(), clientId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(alarmDefinitionMappingCache, key, () -> {
            return super.getAlarmDefinitionMappingsForClient(clientId);
        });
    }

    @Override
    public AlarmTypeContext getUncategorisedAlarmType() throws Exception {
        FacilioCache<String, AlarmTypeContext> alarmTypeCache = LRUCache.getAlarmTypeCache();
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(alarmTypeCache, key, () -> {
            return super.getUncategorisedAlarmType();
        });
    }

    @Override
    public List<AlarmDefinitionTaggingContext> getAlarmDefinitionTaggings(@NonNull Long alarmDefinitionId, @NonNull ControllerType controllerType) throws Exception {
        FacilioCache<String, List<AlarmDefinitionTaggingContext>> alarmDefinitionTaggingCache = LRUCache.getAlarmDefinitionTaggingCache();
        String key = CacheUtil.ALARM_DEFINITION_TAGGING_ID_KEY(AccountUtil.getCurrentOrg().getId(), alarmDefinitionId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(alarmDefinitionTaggingCache, key, () -> {
            return super.getAlarmDefinitionTaggings(alarmDefinitionId,controllerType);
        });
    }

    @Override
    public List<AlarmFilterRuleContext> getAlarmFilterRulesForClient(@NonNull Long clientId) throws Exception {
        FacilioCache<String, List<AlarmFilterRuleContext>> alarmFilterRulesCache = LRUCache.getAlarmFilterRuleCache();
        String key = CacheUtil.CLIENT_ID_KEY(AccountUtil.getCurrentOrg().getId(), clientId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(alarmFilterRulesCache, key, () -> {
            return super.getAlarmFilterRulesForClient(clientId);
        });
    }

    @Override
    public List<FlaggedEventRuleContext> getFlaggedEventRulesForClient(@NonNull Long clientId) throws Exception {
        FacilioCache<String, List<FlaggedEventRuleContext>> flaggedEventsRuleCache = LRUCache.getFlaggedEventsRuleCache();
        String key = CacheUtil.CLIENT_ID_KEY(AccountUtil.getCurrentOrg().getId(), clientId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(flaggedEventsRuleCache, key, () -> {
            return super.getFlaggedEventRulesForClient(clientId);
        });
    }

    @Override
    public FlaggedEventRuleContext getFlaggedEventRule(@NonNull Long id) throws Exception {
        FacilioCache<String, FlaggedEventRuleContext> flaggedEventRuleCache = LRUCache.getFlaggedEventRuleCache();
        String key = CacheUtil.FLAGGED_EVENT_ID_KEY(AccountUtil.getCurrentOrg().getId(), id);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(flaggedEventRuleCache, key, () -> {
            return super.getFlaggedEventRule(id);
        });
    }
}