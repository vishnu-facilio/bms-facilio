package com.facilio.remotemonitoring.handlers.timer;

import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleBureauEvaluationContext;

public class OpenActionHandler implements TeamActionHandler<FlaggedEventBureauActionsContext> {
    @Override
    public Long calculateRemainingTime(FlaggedEventBureauActionsContext evaluationContext, FlaggedEventContext flaggedEvent) throws Exception {
        Long currentTime = System.currentTimeMillis();
        if(evaluationContext != null) {
            Long openTimestamp = evaluationContext.getEvaluationOpenTimestamp();
            if(openTimestamp != null) {
                Long takeCustodyTimePeriod = evaluationContext.getTakeCustodyPeriod();
                if(takeCustodyTimePeriod != null) {
                    Long timeElapsed = currentTime - openTimestamp;
                    Long remainingTime = takeCustodyTimePeriod - timeElapsed;
                    if(remainingTime > 0) {
                        return remainingTime;
                    }
                }
            }
        }
        return 0L;
    }
}
