package com.facilio.remotemonitoring.handlers.timer;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.BureauInhibitReasonListContext;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.signup.BureauInhibitReasonListModule;
import com.facilio.tasker.FacilioTimer;

public class InhibitActionHandler implements TeamActionHandler<FlaggedEventBureauActionsContext> {
    @Override
    public Long calculateRemainingTime(FlaggedEventBureauActionsContext evaluationContext, FlaggedEventContext flaggedEvent) throws Exception {
        Long currentTime = System.currentTimeMillis();
        if(evaluationContext != null) {
            Long inhibitTimeStamp = evaluationContext.getInhibitTimeStamp();
            if(inhibitTimeStamp != null &&  evaluationContext.getInhibitReason() != null && evaluationContext.getInhibitReason().getId() > 0) {
                BureauInhibitReasonListContext inhibitReason = V3RecordAPI.getRecord(BureauInhibitReasonListModule.MODULE_NAME,evaluationContext.getInhibitReason().getId(), BureauInhibitReasonListContext.class);
                if(inhibitReason != null) {
                    Long takeActionDuration = 0l;
                    if(evaluationContext.getTakeActionPeriod() != null){
                        takeActionDuration = evaluationContext.getTakeActionPeriod();
                    }
                    Long timeElapsed = evaluationContext.getTakeCustodyTimestamp() + takeActionDuration;
                    if (timeElapsed > 0) {
                        timeElapsed = (timeElapsed + inhibitReason.getMaxInhibitTime());
                    }
                    Long maxInhibitTime = inhibitReason.getMaxInhibitTime();
                    if (maxInhibitTime != null && timeElapsed != null) {
                        Long remainingTime = timeElapsed - currentTime;
                        if (remainingTime > 0) {
                            return remainingTime;
                        }
                    }
                }
            }
        }
        return 0L;
    }
}