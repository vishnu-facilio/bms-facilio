package com.facilio.bmsconsoleV3.context.purchaseorder;

import com.facilio.bmsconsoleV3.context.V3TermsAndConditionContext;
import com.facilio.v3.context.V3Context;

public class V3PoAssociatedTermsContext extends V3Context {
	
	private static final long serialVersionUID = 1L;
	private V3PurchaseOrderContext purchaseOrder;
	private V3TermsAndConditionContext terms;

	public V3PurchaseOrderContext getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(V3PurchaseOrderContext purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}


	public V3TermsAndConditionContext getTerms() {
		return terms;
	}
	public void setTerms(V3TermsAndConditionContext terms) {
		this.terms = terms;
	}

}
