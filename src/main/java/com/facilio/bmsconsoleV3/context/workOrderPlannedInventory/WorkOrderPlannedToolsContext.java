package com.facilio.bmsconsoleV3.context.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.v3.context.V3Context;

public class WorkOrderPlannedToolsContext extends V3Context {
    private V3WorkOrderContext workOrder;
    private V3ToolTypesContext toolType;
    private String description;
    private Double rate;
    private Double quantity;
    private Double duration;
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

    public V3ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(V3ToolTypesContext toolType) {
        this.toolType = toolType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
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

    public Boolean getIsReserved() {
        if(isReserved!=null){
            return isReserved;
        }
        return false;
    }

    public void setIsReserved(Boolean reserved) {
        isReserved = reserved;
    }
}
