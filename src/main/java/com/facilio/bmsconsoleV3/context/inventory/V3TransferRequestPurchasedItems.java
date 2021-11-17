package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.v3.context.V3Context;

public class V3TransferRequestPurchasedItems extends V3Context {
    private static final long serialVersionUID = 1L;
    private V3TransferRequestContext transferRequest;
    private ItemContext item;
    private double unitPrice;
    private double quantity;

    public V3TransferRequestContext getTransferRequest() {
        return transferRequest;
    }

    public void setTransferRequest(V3TransferRequestContext transferRequest) {
        this.transferRequest = transferRequest;
    }

    public ItemContext getItem() {
        return item;
    }

    public void setItem(ItemContext item) {
        this.item = item;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
