package com.facilio.bmsconsoleV3.context.inventory;


import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class V3TransferRequestLineItemContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3TransferRequestContext transferRequest;
    private InventoryType inventoryType;
    private V3ItemTypesContext itemType;
    private V3ToolTypesContext toolType;
    private Double quantity;
    private Double quantityReceived;

    public V3TransferRequestContext getTransferRequest() {
        return transferRequest;
    }

    public void setTransferRequest(V3TransferRequestContext transferRequest) {
        this.transferRequest = transferRequest;
    }

    public Integer getInventoryType() {
        if (inventoryType != null) {
            return inventoryType.getIndex();
        }
        return null;
    }
    public void setInventoryType(Integer inventoryType) {
        if(inventoryType != null) {
            this.inventoryType = InventoryType.valueOf(inventoryType);
        }
    }
    public InventoryType getInventoryTypeEnum() {
        return inventoryType;
    }

    public static enum InventoryType implements FacilioIntEnum {
        ITEM("Item"),
        TOOL("Tool");

        private String name;

        InventoryType(String name) {
            this.name = name;
        }

        public static InventoryType valueOf(int value) {
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Double quantityReceived) {
        this.quantityReceived = quantityReceived;
    }
}
