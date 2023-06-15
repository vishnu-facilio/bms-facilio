package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.v3.context.V3Context;

public class WorkTypeServicesContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private WorkTypeContext workType;
    private V3ServiceContext service;
    private Double unitPrice;
    private Double quantity;
    private Double duration;

    public WorkTypeContext getWorkType() {
        return workType;
    }

    public void setWorkType(WorkTypeContext workType) {
        this.workType = workType;
    }

    public V3ServiceContext getService() {
        return service;
    }

    public void setService(V3ServiceContext service) {
        this.service = service;
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
}
