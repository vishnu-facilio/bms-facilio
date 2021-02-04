package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsole.context.PMTriggerContext;

import java.io.Serializable;

public class PMJobPlanTriggersContextV3 implements Serializable {

    private static final long serialVersionUID = 1L;
    long id;
    long pmjobPlanId;
    long triggerId;

    PMTriggerContext pmTrigger;
    public PMTriggerContext getPmTrigger() {
        return pmTrigger;
    }
    public void setPmTrigger(PMTriggerContext pmTrigger) {
        this.pmTrigger = pmTrigger;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getPmjobPlanId() {
        return pmjobPlanId;
    }
    public void setPmjobPlanId(long pmjobPlanId) {
        this.pmjobPlanId = pmjobPlanId;
    }

    public long getTriggerId() {
        return triggerId;
    }
    public void setTriggerId(long triggerId) {
        this.triggerId = triggerId;
    }
}
