package com.facilio.bmsconsoleV3.context.invoice;

import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;

public class InvoiceAssociatedTermsContext extends V3Context {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public InvoiceContextV3 getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceContextV3 invoice) {
        this.invoice = invoice;
    }

    private InvoiceContextV3 invoice;
    private TermsAndConditionContext terms;

    public TermsAndConditionContext getTerms() {
        return terms;
    }

    public void setTerms(TermsAndConditionContext terms) {
        this.terms = terms;
    }


}