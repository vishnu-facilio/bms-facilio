package com.facilio.bmsconsole.context;

import lombok.Data;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;

@Data
public class PMJobPlan extends JobPlanContext {
    long pmId;
}
