package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.v3.context.V3Context;

public class V3TransferRequestPurchasedTool extends V3Context {
    private static final long serialVersionUID = 1L;
    private V3TransferRequestContext transferRequest;
    private V3ToolContext tool;
    private Double rate;


    private Double unitPrice;
    private Double quantity;

    public V3TransferRequestContext getTransferRequest() {
        return transferRequest;
    }

    public void setTransferRequest(V3TransferRequestContext transferRequest) {
        this.transferRequest = transferRequest;
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
}

