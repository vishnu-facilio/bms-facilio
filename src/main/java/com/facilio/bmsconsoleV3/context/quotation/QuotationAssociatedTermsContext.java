package com.facilio.bmsconsoleV3.context.quotation;

import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;

public class QuotationAssociatedTermsContext extends V3Context {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public QuotationContext getQuotation() {
        return quotation;
    }

    public void setQuotation(QuotationContext quotation) {
        this.quotation = quotation;
    }

    private QuotationContext quotation;
    private TermsAndConditionContext terms;

    public TermsAndConditionContext getTerms() {
        return terms;
    }

    public void setTerms(TermsAndConditionContext terms) {
        this.terms = terms;
    }


}
