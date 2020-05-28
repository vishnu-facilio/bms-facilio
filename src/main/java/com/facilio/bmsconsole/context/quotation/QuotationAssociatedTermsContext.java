package com.facilio.bmsconsole.context.quotation;

import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class QuotationAssociatedTermsContext extends ModuleBaseWithCustomFields {

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
