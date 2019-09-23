package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class PoAssociatedTermsContext extends ModuleBaseWithCustomFields{

	
	private static final long serialVersionUID = 1L;
	private long poId;
	private TermsAndConditionContext terms;
	
	public long getPoId() {
		return poId;
	}
	public void setPoId(long poId) {
		this.poId = poId;
	}
	public TermsAndConditionContext getTerms() {
		return terms;
	}
	public void setTerms(TermsAndConditionContext terms) {
		this.terms = terms;
	}
	

	}
