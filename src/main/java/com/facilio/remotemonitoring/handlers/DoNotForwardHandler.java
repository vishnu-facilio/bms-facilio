package com.facilio.remotemonitoring.handlers;

import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;

public class DoNotForwardHandler implements AlarmCriteriaHandler {
    @Override
    public void compute(RawAlarmContext rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if(filterRuleCriteria != null) {
            RawAlarmUtil.updateFilterCriteriaId(rawAlarm, filterRuleCriteria);
        }
        createFilteredAlarm(rawAlarm,filterRuleCriteria);
    }

    @Override
    public void createFilteredAlarm(RawAlarmContext rawAlarm,FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        //Do not forward the alarm
        //RawAlarmUtil.markAsFiltered(rawAlarm.getId());
    }
}
