package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.v3.context.V3Context;

public class WorkTypeToolsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private WorkTypeContext workType;
    private V3ToolTypesContext toolType;
    private Double quantity;

    public WorkTypeContext getWorkType() {
        return workType;
    }

    public void setWorkType(WorkTypeContext workType) {
        this.workType = workType;
    }

    public V3ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(V3ToolTypesContext toolType) {
        this.toolType = toolType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
