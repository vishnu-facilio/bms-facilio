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
import com.facilio.remotemonitoring.signup.AlarmFilterRuleModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class AlarmFilterRuleInvalidateCacheCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmFilterRuleContext> alarmFilterRules = (List<AlarmFilterRuleContext>) recordMap.get(AlarmFilterRuleModule.MODULE_NAME);
        if (CollectionUtils.isEmpty(alarmFilterRules)) {
            List<Long> recordIds = (List<Long>) context.get("recordIds");
            if (CollectionUtils.isNotEmpty(recordIds)) {
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
                alarmFilterRules = V3RecordAPI.getRecordsListWithSupplements(AlarmFilterRuleModule.MODULE_NAME, null, AlarmFilterRuleContext.class, criteria, null);
            }
        }
        FacilioCache<String, List<AlarmFilterRuleContext>> alarmFilterRulesCache = LRUCache.getAlarmFilterRuleCache();
        if (CollectionUtils.isNotEmpty(alarmFilterRules)) {
            for (AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                if (alarmFilterRule.getClient() != null) {
                    String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
                    alarmFilterRulesCache.removeStartsWith(key);
                }
            }
        }
        return false;
    }
}