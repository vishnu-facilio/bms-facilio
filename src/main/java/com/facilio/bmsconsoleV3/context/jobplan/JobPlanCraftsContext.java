package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsoleV3.context.CraftContext;
import com.facilio.bmsconsoleV3.context.SkillsContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class JobPlanCraftsContext extends V3Context {

    private JobPlanContext jobPlan;
    private CraftContext craft;
    private SkillsContext skill;

    private Long quantity;
    private Double rate;
    private Double totalPrice;
}
