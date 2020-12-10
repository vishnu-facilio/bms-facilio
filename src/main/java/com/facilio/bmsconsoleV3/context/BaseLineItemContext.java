package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.v3.context.V3Context;

public class BaseLineItemContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private ItemTypesContext itemType;
    private ToolTypesContext toolType;
    private ServiceContext service;
    private LabourContext labour;
    private TaxContext tax;
    private Double quantity;
    private Double unitPrice;
    private Double cost;
    private Double taxAmount;
    private String description;
    private Long unitOfMeasure;

    public ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(ToolTypesContext toolType) {
        this.toolType = toolType;
    }

    public ServiceContext getService() {
        return service;
    }

    public void setService(ServiceContext service) {
        this.service = service;
    }

    public LabourContext getLabour() {
        return labour;
    }

    public void setLabour(LabourContext labour) {
        this.labour = labour;
    }

    public TaxContext getTax() {
        return tax;
    }

    public void setTax(TaxContext tax) {
        this.tax = tax;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

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
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Long unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}
