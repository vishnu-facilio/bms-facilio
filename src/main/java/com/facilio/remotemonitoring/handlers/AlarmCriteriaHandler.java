package com.facilio.remotemonitoring.handlers;

import com.facilio.qa.context.QuestionContext;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.v3.context.V3Context;

public interface AlarmCriteriaHandler<Q extends RawAlarmContext> {
    void compute(Q rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception;

    default void createFilteredAlarm(Q rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
    }

    default void clearAlarm(Q rawAlarm) throws Exception {
    }
}
