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

public interface AlarmRuleBean {
    List<AlarmDefinitionMappingContext> getAlarmDefinitionMappingsForClient(@NonNull Long clientId) throws Exception;

    AlarmTypeContext getUncategorisedAlarmType() throws Exception;

    List<AlarmDefinitionTaggingContext> getAlarmDefinitionTaggings(@NonNull Long alarmDefinitionId, @NonNull ControllerType controllerType) throws Exception;

    List<AlarmFilterRuleContext> getAlarmFilterRulesForClient(@NonNull Long clientId) throws Exception;

    List<FlaggedEventRuleContext> getFlaggedEventRulesForClient(@NonNull Long clientId) throws Exception;

    FlaggedEventRuleContext getFlaggedEventRule(@NonNull Long id) throws Exception;
    AlarmAssetTaggingContext getAlarmAssetTagging(@NonNull Long clientId,@NonNull Long alarmDefinitionId,@NonNull Long controllerId) throws Exception;
    AlarmTypeContext getAlarmType(@NonNull String linkname) throws Exception;
    AlarmAssetTaggingContext getAlarmAssetMapping(@NonNull Long clientId, @NonNull Long alarmDefinitionId, @NonNull Long controllerId,@NonNull Long assetId) throws Exception;
}