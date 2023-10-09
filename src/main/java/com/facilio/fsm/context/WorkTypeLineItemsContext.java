package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class WorkTypeLineItemsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private WorkTypeContext workType;
    private WorkTypeInventoryType inventoryType;
    private V3ItemTypesContext itemType;
    private V3ToolTypesContext toolType;
    private V3ServiceContext service;
    private Double quantity;
    private Long unitOfMeasure;

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

    public V3ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(V3ToolTypesContext toolType) {
        this.toolType = toolType;
    }

    public V3ServiceContext getService() {
        return service;
    }

    public void setService(V3ServiceContext service) {
        this.service = service;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Long getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Long unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Integer getInventoryType() {
        if (inventoryType != null) {
            return inventoryType.getIndex();
        }
        return null;
    }

    public void setInventoryType(Integer inventoryType) {
        if(inventoryType != null) {
            this.inventoryType = WorkTypeInventoryType.valueOf(inventoryType);
        }
    }
    public WorkTypeInventoryType getInventoryTypeEnum(){
        return inventoryType;
    }
    public static enum WorkTypeInventoryType implements FacilioIntEnum {
        ITEM("Item"),
        TOOL("Tool"),
        SERVICE("Service");

        private String name;

        WorkTypeInventoryType(String name) {
            this.name = name;
        }

        public static WorkTypeInventoryType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
}
