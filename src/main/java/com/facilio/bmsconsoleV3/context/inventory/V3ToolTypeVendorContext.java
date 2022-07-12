package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.agent.integration.DownloadCertFile;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class V3ToolTypeVendorContext extends ModuleBaseWithCustomFields {

    private static final long serialVersionUID = 1L;

    private V3ToolTypesContext toolType;

    public V3ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(V3ToolTypesContext toolType) {
        this.toolType = toolType;
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

    private Double price;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    private Long dateOrdered;

    public Long getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(Long dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public V3ToolTypeVendorContext(V3ToolTypesContext toolType, V3VendorContext vendor, double price, long dateOrdered) {
        super();
        this.toolType = toolType;
        this.vendor = vendor;
        this.price = price;
        this.dateOrdered = dateOrdered;
    }
    public V3ToolTypeVendorContext() {
        // TODO Auto-generated constructor stub
    }

}
