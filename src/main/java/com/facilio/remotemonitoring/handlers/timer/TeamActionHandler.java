package com.facilio.remotemonitoring.handlers.timer;

import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleBureauEvaluationContext;

public interface TeamActionHandler <T extends FlaggedEventRuleBureauEvaluationContext> {
    default Long calculateRemainingTime(T evaluationContext, FlaggedEventContext flaggedEvent) throws Exception {
        return 0L;
    }
}