package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.V3Context;

public class V3ServiceVendorContext extends V3Context {
    private static final Long serialVersionUID = 1L;

    private Long serviceId;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    private V3VendorContext vendor;

    public V3VendorContext getVendor() {
        return vendor;
    }

    public void setVendor(V3VendorContext vendor) {
        this.vendor = vendor;
    }

    private Unit orderingUnit;

    public Unit getOrderingUnitEnum() {
        return orderingUnit;
    }

    public void setOrderingUnit(Unit orderingUnit) {
        this.orderingUnit = orderingUnit;
    }

    public int getOrderingUnit() {
        if (orderingUnit != null) {
            return orderingUnit.getUnitId();
        }
        return -1;
    }

    public void setOrderingUnit(int orderingUnit) {
        this.orderingUnit = Unit.valueOf(orderingUnit);
    }

    private Double lastPrice = null;

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    private Long lastOrderedDate = null;


    public Long getLastOrderedDate() {
        return lastOrderedDate;
    }

    public void setLastOrderedDate(Long lastOrderedDate) {
        this.lastOrderedDate = lastOrderedDate;
    }


}
