package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.bmsconsoleV3.context.quotation.TaxSplitUpContext;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class BaseLineItemsParentModuleContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private Double subTotal;
    private Double totalTaxAmount;
    private Double discountAmount;
    private Double discountPercentage;
    private TaxContext tax;
    private Double totalCost;
    private List<TaxSplitUpContext> taxSplitUp;


    public List<TaxSplitUpContext> getTaxSplitUp() {
        return taxSplitUp;
    }

    public void setTaxSplitUp(List<TaxSplitUpContext> taxSplitUp) {
        this.taxSplitUp = taxSplitUp;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(Double totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public TaxContext getTax() {
        return tax;
    }

    public void setTax(TaxContext tax) {
        this.tax = tax;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

}
