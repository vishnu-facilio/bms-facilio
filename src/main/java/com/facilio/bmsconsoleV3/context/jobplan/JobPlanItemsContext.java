package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.v3.context.V3Context;

public class JobPlanItemsContext extends V3Context {
    private JobPlanContext jobPlan;
    private V3ItemTypesContext itemType;
    private String description;
    private Double unitPrice;
    private Double quantity;
    private Double totalCost;
    private V3StoreRoomContext storeRoom;

    public JobPlanContext getJobPlan() {
        return jobPlan;
    }

    public void setJobPlan(JobPlanContext jobPlan) {
        this.jobPlan = jobPlan;
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
}
