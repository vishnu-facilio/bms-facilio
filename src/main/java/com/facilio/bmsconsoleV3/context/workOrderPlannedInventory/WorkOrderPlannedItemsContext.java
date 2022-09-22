package com.facilio.bmsconsoleV3.context.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.v3.context.V3Context;

public class WorkOrderPlannedItemsContext extends V3Context {
    private V3WorkOrderContext workOrder;
    private V3ItemTypesContext itemType;
    private String description;
    private Double unitPrice;
    private Double quantity;
    private Double totalCost;
    private V3StoreRoomContext storeRoom;
    private Boolean isReserved;
    private ReservationType reservationType;

    public V3WorkOrderContext getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(V3WorkOrderContext workOrder) {
        this.workOrder = workOrder;
    }

    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

    public Boolean getIsReserved() {
        return isReserved;
    }

    public void setIsReserved(Boolean reserved) {
        isReserved = reserved;
    }

    public Integer getReservationType() {
        if (reservationType != null) {
            return reservationType.getIndex();
        }
        return -1;
    }

    public void setReservationType(Integer reservationType) {
        if (reservationType != null) {
            this.reservationType = ReservationType.valueOf(reservationType);
        }
    }
    public ReservationType getReservationTypeEnum() {
        return reservationType;
    }

//    public enum ReservationType implements FacilioIntEnum{
//        HARD, SOFT;
//
//        @Override
//        public Integer getIndex() {
//            return ordinal() + 1;
//        }
//
//        @Override
//        public String getValue() {
//            return name();
//        }
//
//        public static ReservationType valueOf(int value) {
//            if (value > 0 && value <= values().length) {
//                return values()[value - 1];
//            }
//            return null;
//        }
//    }
}
