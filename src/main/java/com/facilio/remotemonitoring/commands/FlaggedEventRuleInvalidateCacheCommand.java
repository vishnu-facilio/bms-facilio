package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleModule;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class FlaggedEventRuleInvalidateCacheCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(FlaggedEventRuleModule.MODULE_NAME);
        if (CollectionUtils.isEmpty(flaggedEventRules)) {
            List<Long> recordIds = (List<Long>) context.get("recordIds");
            if (CollectionUtils.isNotEmpty(recordIds)) {
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
                flaggedEventRules = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventRuleModule.MODULE_NAME, null, FlaggedEventRuleContext.class, criteria, null);
            }
        }
        FacilioCache<String, List<FlaggedEventRuleContext>> flaggedEventsRuleCache = LRUCache.getFlaggedEventsRuleCache();
        FacilioCache<String, FlaggedEventRuleContext> flaggedEventRuleCache = LRUCache.getFlaggedEventRuleCache();

        if (CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for (FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                if (flaggedEventRule.getClient() != null) {
                    String key = CacheUtil.CLIENT_ID_KEY(AccountUtil.getCurrentOrg().getId(), flaggedEventRule.getClient().getId());
                    flaggedEventsRuleCache.remove(key);
                }
                String key = CacheUtil.FLAGGED_EVENT_ID_KEY(AccountUtil.getCurrentOrg().getId(), flaggedEventRule.getId());
                flaggedEventRuleCache.remove(key);
            }
        }
        return false;
    }
}