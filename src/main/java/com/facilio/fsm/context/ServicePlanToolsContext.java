package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicePlanToolsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private ServiceTaskTemplateContext serviceTaskTemplate;
    private ServicePlanContext servicePlan;
    private V3ToolTypesContext toolType;
    private V3StoreRoomContext storeRoom;
    private Double quantity;
    private Double duration;
}
