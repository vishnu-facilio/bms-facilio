package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.AlarmFilterCriteriaType;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmNotReceivedForDurationJobScheduler extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmFilterRuleContext> alarmFilterRules = (List<AlarmFilterRuleContext>) recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(alarmFilterRules)) {
            for (AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                List<FilterRuleCriteriaContext> filterRuleCriteriaList = alarmFilterRule.getFilterRuleCriteriaList();
                if (CollectionUtils.isNotEmpty(filterRuleCriteriaList)) {
                    for (FilterRuleCriteriaContext filterRuleCriteria : filterRuleCriteriaList) {
                        if (filterRuleCriteria.getFilterCriteria() == AlarmFilterCriteriaType.NO_ALARM_RECEIVED_FOR_SPECIFIC_PERIOD) {
                            int period = (int) (filterRuleCriteria.getAlarmDuration() / 1000);
                            FacilioTimer.schedulePeriodicJob(filterRuleCriteria.getId(), RemoteMonitorConstants.ALARM_NOT_RECEIVED_JOB, period,period, "priority");
                        }
                    }
                }
            }
        }
        return false;
    }
}
