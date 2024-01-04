package com.facilio.bmsconsoleV3.context.requestforquotation;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolTypesContext;
import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.bmsconsoleV3.context.quotation.TaxSplitUpContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class V3RequestForQuotationLineItemsContext extends V3Context {
    private static final long serialVersionUID = 1L;
    @Getter @Setter
    private String name;
    private V3RequestForQuotationContext requestForQuotation;
    private InventoryTypeRfq inventoryType;
    private V3ItemTypesContext itemType;
    private V3ToolTypesContext toolType;
    private V3ServiceContext service;
    private String description;
    private Double unitPrice;
    private Double quantity;
    private Long unitOfMeasure;
    private Double awardedPrice;
    private V3VendorContext awardedTo;
    private String remarks;
    private Double totalCost;

    @Getter @Setter
    private TaxContext tax;
    @Getter @Setter
    private Double taxAmount;
    @Getter @Setter
    private String unitOfMeasureEnum;


    public V3RequestForQuotationContext getRequestForQuotation() {
        return requestForQuotation;
    }

    public void setRequestForQuotation(V3RequestForQuotationContext requestForQuotation) {
        this.requestForQuotation = requestForQuotation;
    }

    public Integer getInventoryType() {
        if (inventoryType != null) {
            return inventoryType.getIndex();
        }
        return null;
    }

    public void setInventoryType(Integer inventoryType) {
        if(inventoryType != null) {
            this.inventoryType = InventoryTypeRfq.valueOf(inventoryType);
        }
    }
    public InventoryTypeRfq getInventoryTypeEnum(){
        return inventoryType;
    }
    public static enum InventoryTypeRfq implements FacilioIntEnum {
        ITEM("Item"),
        TOOL("Tool"),
        SERVICE("service"),
        OTHERS("others");

        private String name;

        InventoryTypeRfq(String name) {
            this.name = name;
        }

        public static InventoryTypeRfq valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public V3ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(V3ToolTypesContext toolType) {
        this.toolType = toolType;
    }

    public V3ServiceContext getService() {
        return service;
    }

    public void setService(V3ServiceContext service) {
        this.service = service;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Long unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Double getAwardedPrice() {
        return awardedPrice;
    }

    public void setAwardedPrice(Double awardedPrice) {
        this.awardedPrice = awardedPrice;
    }

    public V3VendorContext getAwardedTo() {
        return awardedTo;
    }

    public void setAwardedTo(V3VendorContext awardedTo) {
        this.awardedTo = awardedTo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
}
