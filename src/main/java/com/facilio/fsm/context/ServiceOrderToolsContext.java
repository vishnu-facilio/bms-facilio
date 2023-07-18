package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.v3.context.V3Context;

public class ServiceOrderToolsContext extends V3Context {

    private ServiceOrderContext serviceOrder;
    private ServiceTaskContext serviceTask;
    private Double totalCost;
    private Long issueTime;
    private Long returnTime;
    private Double duration;
    private Double quantity;
    private Double rate;
    private V3ToolTypesContext toolType;
    private V3ToolContext tool;
    private V3StoreRoomContext storeRoom;

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

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Long getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Long issueTime) {
        this.issueTime = issueTime;
    }

    public Long getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Long returnTime) {
        this.returnTime = returnTime;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public V3ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(V3ToolTypesContext toolType) {
        this.toolType = toolType;
    }

    public V3ToolContext getTool() {
        return tool;
    }

    public void setTool(V3ToolContext tool) {
        this.tool = tool;
    }

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }
}
