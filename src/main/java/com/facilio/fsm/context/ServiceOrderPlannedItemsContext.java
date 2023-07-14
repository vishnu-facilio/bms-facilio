package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.v3.context.V3Context;

public class ServiceOrderPlannedItemsContext  extends V3Context {
    private ServiceOrderContext serviceOrder;
    private ServiceTaskContext serviceTask;
    private V3ItemTypesContext itemType;
    private Double unitPrice;
    private Double quantity;
    private Double totalCost;
    private V3StoreRoomContext storeRoom;
    private Boolean isReserved;
    private ReservationType reservationType;

    public ServiceOrderContext getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrderContext serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public ServiceTaskContext getServiceTask() {
        return serviceTask;
    }

    public void setServiceTask(ServiceTaskContext serviceTask) {
        this.serviceTask = serviceTask;
    }

    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
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

    public Boolean getReserved() {
        return isReserved;
    }

    public void setReserved(Boolean reserved) {
        isReserved = reserved;
    }

    public ReservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }
}
