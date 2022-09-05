package com.facilio.bmsconsoleV3.context;


import com.facilio.v3.context.V3Context;

public class V3WorkOrderServiceContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3WorkOrderContext parent;

    private Double cost;

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    private Long parentId;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    private V3ServiceContext service;

    public V3ServiceContext getService() {
        return service;
    }

    public void setService(V3ServiceContext service) {
        this.service = service;
    }

    private Long vendor;

    public Long getVendor() {
        return vendor;
    }

    public void setVendor(Long vendor) {
        this.vendor = vendor;
    }

    private Double unitPrice;
    private Double quantity;

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

    private Long startTime;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    private Long endTime;

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    private Double duration;

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getDuration() {
        return duration;
    }

    public V3WorkOrderContext getParent() {
        return parent;
    }

    public void setParent(V3WorkOrderContext parent) {
        this.parent = parent;
    }
}
