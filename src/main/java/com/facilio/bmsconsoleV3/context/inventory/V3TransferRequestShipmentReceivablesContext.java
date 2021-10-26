package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class V3TransferRequestShipmentReceivablesContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3TransferRequestShipmentContext shipment;
    private InventoryType inventoryType;
    private ItemTypesContext itemType;
    private ToolTypesContext toolType;
    private double quantityReceived;
    private long receiptDate;
    private String remarks;


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

    public V3TransferRequestShipmentContext getShipment() {
        return shipment;
    }

    public void setShipment(V3TransferRequestShipmentContext shipment) {
        this.shipment = shipment;
    }

    public ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(ToolTypesContext toolType) {
        this.toolType = toolType;
    }

    public double getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(double quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public long getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(long receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}