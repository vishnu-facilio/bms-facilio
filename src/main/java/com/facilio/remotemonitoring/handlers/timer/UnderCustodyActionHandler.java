package com.facilio.remotemonitoring.handlers.timer;

import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;

public class UnderCustodyActionHandler implements TeamActionHandler<FlaggedEventBureauActionsContext> {
    @Override
    public Long calculateRemainingTime(FlaggedEventBureauActionsContext evaluationContext, FlaggedEventContext flaggedEvent) throws Exception {
        Long currentTime = System.currentTimeMillis();
        if(evaluationContext != null) {
            Long takeCustodyTime = evaluationContext.getTakeCustodyTimestamp();
            if(takeCustodyTime != null && !evaluationContext.getIsSLABreached()) {
                Long takeActionTimePeriod = evaluationContext.getTakeActionPeriod();
                if(takeActionTimePeriod != null) {
                    Long timeElapsed = currentTime - takeCustodyTime;
                    Long remainingTime = takeActionTimePeriod - timeElapsed;
                    if(remainingTime > 0) {
                        return remainingTime;
                    }
                }
            }
        }
        return 0L;
    }
}