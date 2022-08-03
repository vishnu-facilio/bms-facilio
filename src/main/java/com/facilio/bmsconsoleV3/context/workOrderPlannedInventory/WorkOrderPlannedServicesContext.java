package com.facilio.bmsconsoleV3.context.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.v3.context.V3Context;

public class WorkOrderPlannedServicesContext  extends V3Context {
    private V3WorkOrderContext workOrder;
    private V3ServiceContext service;
    private String description;
    private Double unitPrice;
    private Double quantity;
    private Double duration;
    private Double totalCost;

    public V3WorkOrderContext getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(V3WorkOrderContext workOrder) {
        this.workOrder = workOrder;
    }

    public V3ServiceContext getService() {
        return service;
    }

    public void setService(V3ServiceContext service) {
        this.service = service;
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
}
