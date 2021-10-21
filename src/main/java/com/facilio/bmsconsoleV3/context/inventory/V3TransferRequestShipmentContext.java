package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3TransferRequestShipmentContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3TransferRequestContext transferRequest;
    private long expectedCompletionDate;
    private Boolean isCompleted;
    private List<V3TransferRequestShipmentReceivablesContext> shipmentReceivables;

    public V3TransferRequestContext getTransferRequest() {
        return transferRequest;
    }

    public void setTransferRequest(V3TransferRequestContext transferRequest) {
        this.transferRequest = transferRequest;
    }

    public long getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(long expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
    }

    public Boolean getCompleted() {
        if (isCompleted != null) {
            return isCompleted.booleanValue();
        }
        return false;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public List<V3TransferRequestShipmentReceivablesContext> getShipmentReceivables() {
        return shipmentReceivables;
    }

    public void setShipmentReceivables(List<V3TransferRequestShipmentReceivablesContext> shipmentReceivables) {
        this.shipmentReceivables = shipmentReceivables;
    }
}
