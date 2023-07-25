package com.facilio.bmsconsoleV3.context.jobplan;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SFGFacilioJobPlanV3 {
    private JobPlanContext jobplan;
    private List<JobPlanTaskSectionContext> jobplansection;
}
