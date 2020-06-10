package com.facilio.bmsconsoleV3.context.quotation;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class TaxSplitUpContext extends ModuleBaseWithCustomFields {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public TaxContext getTax() {
        return tax;
    }

    public void setTax(TaxContext tax) {
        this.tax = tax;
    }

    private Double taxAmount;
    private TaxContext tax;
}
