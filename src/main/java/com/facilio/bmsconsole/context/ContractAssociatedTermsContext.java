package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ContractAssociatedTermsContext extends ModuleBaseWithCustomFields{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long contractId;
	private TermsAndConditionContext terms;
	
	
	public long getContractId() {
		return contractId;
	}
	public void setContractId(long contractId) {
		this.contractId = contractId;
	}
	public TermsAndConditionContext getTerms() {
		return terms;
	}
	public void setTerms(TermsAndConditionContext terms) {
		this.terms = terms;
	}
	

}
