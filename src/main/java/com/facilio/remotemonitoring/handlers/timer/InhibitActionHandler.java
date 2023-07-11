package com.facilio.remotemonitoring.handlers.timer;

import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;

public class InhibitActionHandler implements TeamActionHandler<FlaggedEventBureauActionsContext> {
    @Override
    public Long calculateRemainingTime(FlaggedEventBureauActionsContext evaluationContext, FlaggedEventContext flaggedEvent) throws Exception {
        Long currentTime = System.currentTimeMillis();
        if(evaluationContext != null) {
            Long inhibitTimeStamp = evaluationContext.getInhibitTimeStamp();
            if(inhibitTimeStamp != null &&  evaluationContext.getInhibitReason() != null) {
                Long maxInhibitTime = evaluationContext.getInhibitReason().getMaxInhibitTime();
                if(maxInhibitTime != null) {
                    Long timeElapsed = currentTime - inhibitTimeStamp;
                    Long remainingTime = maxInhibitTime - timeElapsed;
                    if(remainingTime > 0) {
                        return remainingTime;
                    }
                }
            }
        }
        return 0L;
    }
}