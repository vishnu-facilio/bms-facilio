package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3ToolTypesContext;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3ToolContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private V3ToolTypesContext toolType;
    private V3StoreRoomContext storeRoom;
    private V3ToolStatusContext status;
    private Unit issuingUnit;
    private Double quantity;
    private Double rate;
    private Double currentQuantity;
    private List<V3PurchasedToolContext> purchasedTools;
    private Long lastPurchasedDate;
    private Double lastPurchasedPrice;
    private Double minimumQuantity;
    public Boolean isUnderstocked;

    public V3ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(V3ToolTypesContext toolType) {
        this.toolType = toolType;
    }

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

    public V3ToolStatusContext getStatus() {
        return status;
    }

    public void setStatus(V3ToolStatusContext status) {
        this.status = status;
    }

    public Unit getIssuingUnit() {
        return issuingUnit;
    }

    public void setIssuingUnit(Unit issuingUnit) {
        this.issuingUnit = issuingUnit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Double currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public List<V3PurchasedToolContext> getPurchasedTools() {
        return purchasedTools;
    }

    public void setPurchasedTools(List<V3PurchasedToolContext> purchasedTools) {
        this.purchasedTools = purchasedTools;
    }

    public Long getLastPurchasedDate() {
        return lastPurchasedDate;
    }

    public void setLastPurchasedDate(Long lastPurchasedDate) {
        this.lastPurchasedDate = lastPurchasedDate;
    }

    public Double getLastPurchasedPrice() {
        return lastPurchasedPrice;
    }

    public void setLastPurchasedPrice(Double lastPurchasedPrice) {
        this.lastPurchasedPrice = lastPurchasedPrice;
    }

    public Double getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Double minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Boolean getUnderstocked() {
        return isUnderstocked;
    }

    public void setUnderstocked(Boolean understocked) {
        isUnderstocked = understocked;
    }


}
