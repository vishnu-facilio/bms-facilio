package com.facilio.remotemonitoring.handlers.timer;

import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;

public class TakeActionHandler implements TeamActionHandler<FlaggedEventBureauActionsContext> {
    @Override
    public Long calculateRemainingTime(FlaggedEventBureauActionsContext evaluationContext, FlaggedEventContext flaggedEvent) throws Exception {
        return 0L;
    }
}