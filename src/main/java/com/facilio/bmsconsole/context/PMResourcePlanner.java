package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class PMResourcePlanner extends V3Context {
    private V3ResourceContext resource;
    private long pmId;
    private PMJobPlan jobPlan;
    private PMTriggerV2 trigger;
    private User assignedTo;
    private PMPlanner planner;
}
