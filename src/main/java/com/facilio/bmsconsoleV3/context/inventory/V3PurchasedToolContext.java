package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.v3.context.V3Context;

public class V3PurchasedToolContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private V3ToolTypesContext toolType;
    private V3ToolContext tool;
    private Double rate;
    private Long costDate;
    public Boolean isUsed;
    private String serialNumber;

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

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Long getCostDate() {
        return costDate;
    }

    public void setCostDate(Long costDate) {
        this.costDate = costDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }


}
