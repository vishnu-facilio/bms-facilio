package com.facilio.remotemonitoring.commands;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.AlarmFilterCriteriaType;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleModule;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteScheduledJobsForAlarmRule extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmFilterRuleContext> alarmFilterRuleList = (List<AlarmFilterRuleContext>) recordMap.get(AlarmFilterRuleModule.MODULE_NAME);
        List<FilterRuleCriteriaContext> filterRuleCriteriaList = null;
        List<Long> recordIds = null;
        if (CollectionUtils.isEmpty(alarmFilterRuleList)) {
            recordIds = (List<Long>) context.get("recordIds");
        } else {
            recordIds = alarmFilterRuleList.stream().map(AlarmFilterRuleContext::getId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(recordIds)) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_FILTER_RULE_ID", "alarmFilterRuleId", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
            filterRuleCriteriaList = V3RecordAPI.getRecordsListWithSupplements(AlarmFilterRuleCriteriaModule.MODULE_NAME, null, FilterRuleCriteriaContext.class, criteria, null);
        }
        if(CollectionUtils.isNotEmpty(filterRuleCriteriaList)) {
            for(FilterRuleCriteriaContext filterRuleCriteria : filterRuleCriteriaList) {
                if (filterRuleCriteria.getFilterCriteria() == AlarmFilterCriteriaType.NO_ALARM_RECEIVED_FOR_SPECIFIC_PERIOD) {
                    FacilioTimer.deleteJob(filterRuleCriteria.getId(), RemoteMonitorConstants.ALARM_NOT_RECEIVED_JOB);
                }
            }
        }
        return false;
    }
}