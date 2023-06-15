package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.v3.context.V3Context;

public class WorkTypeItemsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private WorkTypeContext workType;
    private V3ItemTypesContext itemType;
    private Double quantity;

    public WorkTypeContext getWorkType() {
        return workType;
    }

    public void setWorkType(WorkTypeContext workType) {
        this.workType = workType;
    }

    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
