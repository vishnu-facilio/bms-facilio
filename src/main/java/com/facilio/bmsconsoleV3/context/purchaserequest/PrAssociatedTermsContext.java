package com.facilio.bmsconsoleV3.context.purchaserequest;

import com.facilio.bmsconsoleV3.context.V3TermsAndConditionContext;
import com.facilio.v3.context.V3Context;

public class PrAssociatedTermsContext extends V3Context {
    private static final long serialVersionUID = 1L;
    private V3PurchaseRequestContext purchaseRequest;
    private V3TermsAndConditionContext terms;

    public V3PurchaseRequestContext getPurchaseRequest() {
        return purchaseRequest;
    }

    public void setPurchaseRequest(V3PurchaseRequestContext purchaseRequest) {
        this.purchaseRequest = purchaseRequest;
    }

    public V3TermsAndConditionContext getTerms() {
        return terms;
    }

    public void setTerms(V3TermsAndConditionContext terms) {
        this.terms = terms;
    }
}
