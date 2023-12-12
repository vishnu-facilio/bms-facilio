package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsoleV3.context.V3BinContext;
import com.facilio.v3.context.V3Context;

public class V3PurchasedItemContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3ItemTypesContext itemType;
    private V3ItemContext item;
    private V3BinContext bin;
    private Double unitcost;
    private Double quantity;
    private Double currentQuantity;
    private Long costDate;
    private String serialNumber;
    public Boolean isUsed;


    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public V3ItemContext getItem() {
        return item;
    }

    public void setItem(V3ItemContext item) {
        this.item = item;
    }

    public Double getUnitcost() {
        return unitcost;
    }

    public void setUnitcost(Double unitcost) {
        this.unitcost = unitcost;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Double currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public Long getCostDate() {
        return costDate;
    }

    public void setCostDate(Long costDate) {
        this.costDate = costDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public V3BinContext getBin() {
        return bin;
    }

    public void setBin(V3BinContext bin) {
        this.bin = bin;
    }
}
