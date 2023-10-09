package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.v3.context.V3Context;

public class ServiceOrderPlannedServicesContext extends V3Context {
    private ServiceOrderContext serviceOrder;
    private ServiceTaskContext serviceTask;
    private ServiceAppointmentContext serviceAppointment;
    private Long unitOfMeasure;
    private V3ServiceContext service;
    private Double unitPrice;
    private Double quantity;
    private Double duration;
    private Double totalCost;

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

    public ServiceAppointmentContext getServiceAppointment() {
        return serviceAppointment;
    }

    public void setServiceAppointment(ServiceAppointmentContext serviceAppointment) {
        this.serviceAppointment = serviceAppointment;
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

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Long getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Long unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}
