package com.facilio.remotemonitoring.handlers;

import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;

public class AlarmCountsInAPeriodOfTimeOrOpenForDurationHandler implements AlarmCriteriaHandler {
    @Override
    public void compute(RawAlarmContext rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        AlarmCountsInAPeriodOfTimeRTNHandler countHandler = new AlarmCountsInAPeriodOfTimeRTNHandler();
        countHandler.compute(rawAlarm,filterRuleCriteria);
        AlarmOpenForDurationOfTimeRTNHandler durationHandler = new AlarmOpenForDurationOfTimeRTNHandler();
        durationHandler.compute(rawAlarm,filterRuleCriteria);
    }
}