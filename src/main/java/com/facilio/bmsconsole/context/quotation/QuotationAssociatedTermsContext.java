package com.facilio.bmsconsole.context.quotation;

import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class QuotationAssociatedTermsContext extends ModuleBaseWithCustomFields {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private QuotationContext quotation;
    private TermsAndConditionContext terms;


    public QuotationContext getContractId() {
        return quotation;
    }

    public void setContractId(QuotationContext quotation) {
        this.quotation = quotation;
    }

    public TermsAndConditionContext getTerms() {
        return terms;
    }

    public void setTerms(TermsAndConditionContext terms) {
        this.terms = terms;
    }


}
