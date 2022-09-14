package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;

public class V3WorkorderItemContext extends V3ItemTransactionsContext {
    private static final long serialVersionUID = 1L;

    private Double cost;
    private Double unitPrice;
    public Double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public Double getCost() {
        return cost;
    }
    public void setCost(Double cost) {
        this.cost = cost;
        this.setTransactionCost(cost);
    }
}
