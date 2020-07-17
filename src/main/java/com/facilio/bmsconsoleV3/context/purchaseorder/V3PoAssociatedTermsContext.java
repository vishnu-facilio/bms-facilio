package com.facilio.bmsconsoleV3.context.purchaseorder;

import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.v3.context.V3Context;

public class V3PoAssociatedTermsContext extends V3Context {
	
	private static final long serialVersionUID = 1L;
	private Long poId;
	private TermsAndConditionContext terms;
		
	public Long getPoId() {
		return poId;
	}
	public void setPoId(Long poId) {
		this.poId = poId;
	}
	public TermsAndConditionContext getTerms() {
		return terms;
	}
	public void setTerms(TermsAndConditionContext terms) {
		this.terms = terms;
	}

}
