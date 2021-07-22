package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsoleV3.context.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3ItemContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3ItemTypesContext itemType;
    private V3StoreRoomContext storeRoom;
    private V3ItemStatusContext status;
    private CostType costType;
    private Unit issuingUnit;
    private Double quantity;
    private List<V3PurchasedItemContext> purchasedItems;
    private Long lastPurchasedDate;
    private Double lastPurchasedPrice;
    private Double minimumQuantity;
    public Boolean isUnderstocked;

    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

    public V3ItemStatusContext getStatus() {
        return status;
    }

    public void setStatus(V3ItemStatusContext status) {
        this.status = status;
    }

    public CostType getCostType() {
        return costType;
    }

    public void setCostType(CostType costType) {
        this.costType = costType;
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

    public List<V3PurchasedItemContext> getPurchasedItems() {
        return purchasedItems;
    }

    public void setPurchasedItems(List<V3PurchasedItemContext> purchasedItems) {
        this.purchasedItems = purchasedItems;
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



    public enum CostType implements FacilioIntEnum {
        FIFO, LIFO;

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static CostType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

}
