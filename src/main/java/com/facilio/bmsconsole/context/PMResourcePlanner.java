package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class PMResourcePlanner extends V3Context {
    private V3ResourceContext resource;
    private long pmId;
    private JobPlanContext jobPlan;
    private PMTriggerV2 trigger;
    private User assignedTo;
    private PMPlanner planner;
}
