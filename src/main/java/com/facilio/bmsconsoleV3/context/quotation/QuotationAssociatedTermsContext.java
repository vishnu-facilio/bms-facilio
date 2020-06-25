package com.facilio.bmsconsoleV3.context.quotation;

import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;

public class QuotationAssociatedTermsContext extends V3Context {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public QuotationContext getQuote() {
        return quote;
    }

    public void setQuote(QuotationContext quotation) {
        this.quote = quotation;
    }

    private QuotationContext quote;
    private TermsAndConditionContext terms;

    public TermsAndConditionContext getTerms() {
        return terms;
    }

    public void setTerms(TermsAndConditionContext terms) {
        this.terms = terms;
    }


}
