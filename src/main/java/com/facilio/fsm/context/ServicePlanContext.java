package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class ServicePlanContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private V3ClientContext client;
    private List<ServiceTaskTemplateContext> serviceTaskTemplate;
}
