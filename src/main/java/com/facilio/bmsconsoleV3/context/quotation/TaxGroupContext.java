package com.facilio.bmsconsoleV3.context.quotation;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class TaxGroupContext  extends ModuleBaseWithCustomFields {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private TaxContext childTax;

    public TaxContext getChildTax() {
        return childTax;
    }

    public void setChildTax(TaxContext childTax) {
        this.childTax = childTax;
    }

    public TaxContext getParentTax() {
        return parentTax;
    }

    public void setParentTax(TaxContext parentTax) {
        this.parentTax = parentTax;
    }

    private TaxContext parentTax;

}
