package com.facilio.bmsconsoleV3.context.jobplan;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.v3.context.V3Context;

public class JobPlanToolsContext extends V3Context {
    private JobPlanContext jobPlan;
    private V3ToolTypesContext toolType;
    private String description;
    private Double rate;
    private Double quantity;
    private Double duration;
    private Double totalCost;
    private V3StoreRoomContext storeRoom;

    public JobPlanContext getJobPlan() {
        return jobPlan;
    }

    public void setJobPlan(JobPlanContext jobPlan) {
        this.jobPlan = jobPlan;
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
}
