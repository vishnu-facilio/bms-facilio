package com.facilio.remotemonitoring.context;

import com.facilio.remotemonitoring.signup.FlaggedEventBureauEvaluationModule;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BureauInhibitReasonListContext extends V3Context {
    private FlaggedEventRuleBureauEvaluationContext flaggedEventBureauEvaluation;
    private String inhibitReason;
    private Long maxInhibitTime;
    private Boolean hideInBureau;
}