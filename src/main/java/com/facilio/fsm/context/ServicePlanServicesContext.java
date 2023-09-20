package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicePlanServicesContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private ServiceTaskTemplateContext serviceTaskTemplate;
    private ServicePlanContext servicePlan;
    private V3ServiceContext service;
    private Double quantity;
    private Double duration;
}
