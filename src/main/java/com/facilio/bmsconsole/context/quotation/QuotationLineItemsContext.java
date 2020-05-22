package com.facilio.bmsconsole.context.quotation;

import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class QuotationLineItemsContext extends ModuleBaseWithCustomFields {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private QuotationContext quotation;
    private Type type;
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

    public TaxContext getTax() {
        return tax;
    }

    public void setTax(TaxContext tax) {
        this.tax = tax;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public QuotationContext getQuotation() {
        return quotation;
    }

    public void setQuotation(QuotationContext quotation) {
        this.quotation = quotation;
    }

    public void setType(int type) {
        this.type = Type.valueOf(type);
    }

    public Type getTypeEnum() {
        return type;
    }
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private enum Type implements FacilioEnum {
        ITEM_TYPE("Item Type"),
        TOOL_TYPE("Tool Type"),
        SERVICE("Service"),
        OTHERS("Others"),
        LABOUR("Labour");
        private String name;

        Type(String name) {
            this.name = name;
        }

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

}
