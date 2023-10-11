package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.remotemonitoring.context.AlarmAssetTaggingContext;
import com.facilio.remotemonitoring.context.AlarmDefinitionMappingContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class InvalidateAlarmAssetTaggingCacheCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        FacilioCache<String, AlarmAssetTaggingContext> alarmAssetTaggingCache = LRUCache.getAlarmAssetTaggingCache();
        List<AlarmAssetTaggingContext> alarmAssetTaggings = (List<AlarmAssetTaggingContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(alarmAssetTaggings)) {
            for(AlarmAssetTaggingContext alarmAssetTagging : alarmAssetTaggings) {
                if(alarmAssetTagging.getClient() != null && alarmAssetTagging.getAlarmDefinition() != null && alarmAssetTagging.getController() != null) {
                    String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
                    alarmAssetTaggingCache.removeStartsWith(key);
                }
            }
        }
        return false;
    }
}
