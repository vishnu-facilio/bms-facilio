package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.remotemonitoring.context.AlarmDefinitionMappingContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AlarmDefinitionMappingInvalidateCacheCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        FacilioCache<String, List<AlarmDefinitionMappingContext>> alarmDefinitionMappingCache = LRUCache.getAlarmDefinitionMappingCache();
        List<AlarmDefinitionMappingContext> alarmDefinitionMappings = (List<AlarmDefinitionMappingContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(alarmDefinitionMappings)) {
            for(AlarmDefinitionMappingContext alarmDefinitionMapping : alarmDefinitionMappings) {
                if(alarmDefinitionMapping.getClient() != null) {
                    String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
                    alarmDefinitionMappingCache.removeStartsWith(key);
                }
            }
        }
        return false;
    }
}
