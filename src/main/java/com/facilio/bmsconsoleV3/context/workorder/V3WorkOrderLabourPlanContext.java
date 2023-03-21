package com.facilio.bmsconsoleV3.context.workorder;

import com.facilio.bmsconsoleV3.context.CraftContext;
import com.facilio.bmsconsoleV3.context.SkillsContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter@Setter
public class V3WorkOrderLabourPlanContext extends V3Context {

    private V3WorkOrderContext parent;
    private CraftContext craft;
    private SkillsContext skill;

    private Long quantity;
    private Map<String,Object> rate;
    private Map<String,Object> totalPrice;
    private Long duration;
}
