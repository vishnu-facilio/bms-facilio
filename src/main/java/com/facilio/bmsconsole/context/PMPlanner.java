package com.facilio.bmsconsole.context;

import com.facilio.v3.context.V3Context;
import lombok.Data;

import java.util.List;

@Data
public class PMPlanner extends V3Context {
    private long pmId;
    private String name;
    private PMTriggerV2 trigger;
    private PMJobPlan adhocJobPlan;
    private PMJobPlan preReqJobPlan;
    private List<PMResourcePlanner> resourcePlanners;
}
