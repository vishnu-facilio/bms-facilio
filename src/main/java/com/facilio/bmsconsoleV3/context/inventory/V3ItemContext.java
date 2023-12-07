package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.enums.CostType;
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
    private Double quantity;//available quantity
    private Double currentQuantity;
    private Double reservedQuantity;
    private List<V3PurchasedItemContext> purchasedItems;
    private Long lastPurchasedDate;
    private Double lastPurchasedPrice;
    private Double minimumQuantity;
    public Boolean isUnderstocked;
    public Double issuanceCost;
    private Double weightedAverageCost;

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

    public Integer getCostType() {
        if (costType != null) {
            return costType.getIndex();
        }
        return -1;
    }

    public void setCostType(Integer costType) {
        if (costType != null) {
            this.costType = CostType.valueOf(costType);
        }
    }
    public CostType getCostTypeEnum() {
        return costType;
    }

    public Unit getIssuingUnitEnum() {
        return issuingUnit;
    }

    public void setIssuingUnit(Unit issuingUnit) {
        this.issuingUnit = issuingUnit;
    }

    public int getIssuingUnit() {
        if (issuingUnit != null) {
            return issuingUnit.getUnitId();
        }
        return -1;
    }

    public void setIssuingUnit(int issuingUnit) {
        this.issuingUnit = Unit.valueOf(issuingUnit);
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

    public Double getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Double reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
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

    public Boolean getIsUnderstocked() {
        if(isUnderstocked!=null){
            return isUnderstocked.booleanValue();
        }
        return false;
    }

    public void setIsUnderstocked(Boolean understocked) {
        isUnderstocked = understocked;
    }

    public Double getIssuanceCost() {
        return issuanceCost;
    }

    public void setIssuanceCost(Double issuanceCost) {
        this.issuanceCost = issuanceCost;
    }

    public Double getWeightedAverageCost() {
        return weightedAverageCost;
    }

    public void setWeightedAverageCost(Double weightedAverageCost) {
        this.weightedAverageCost = weightedAverageCost;
    }
}
