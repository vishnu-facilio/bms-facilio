package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class TermsAndConditionsAction extends FacilioAction{

	private static final long serialVersionUID = 1L;

	private TermsAndConditionContext termsAndCondition;
	private List<TermsAndConditionContext> termsAndConditions;
	private long recordId;

	public TermsAndConditionContext getTermsAndCondition() {
		return termsAndCondition;
	}

	public void setTermsAndCondition(TermsAndConditionContext termsAndCondition) {
		this.termsAndCondition = termsAndCondition;
	}

	public List<TermsAndConditionContext> getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(List<TermsAndConditionContext> termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private List<Long> recordIds;
	

	public List<Long> getRecordIds() {
		return recordIds;
	}

	public void setRecordIds(List<Long> recordIds) {
		this.recordIds = recordIds;
	}
	
	private Boolean fetchCount;
	public Boolean getFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(Boolean fetchCount) {
		this.fetchCount = fetchCount;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}


	public String addTermsAndConditions() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, termsAndConditions);
		Chain addTermsAndConditions = TransactionChainFactory.getAddTermsAndConditionsChain();
		addTermsAndConditions.execute(context);
		setResult(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, termsAndConditions);
		return SUCCESS;
	}

	public String deleteTermsAndConditions() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		context.put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
		
		Chain deleteTerms = TransactionChainFactory.getDeleteTermsAndConditionsChain();
		deleteTerms.execute(context);
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		return SUCCESS;
	}

	public String updateTermsAndConditions() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.RECORD, termsAndCondition);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(termsAndCondition.getId()));
		Chain updateTermsAndConditionsChain = TransactionChainFactory.getUpdateTermsAndConditionsChain();
		updateTermsAndConditionsChain.execute(context);
		setResult(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, termsAndCondition);

		return SUCCESS;
	}

	public String termsAndConditionsDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getRecordId());

		Chain termsAndConditionsDetailsChain = ReadOnlyChainFactory.fetchTermsAndConditionsDetails();
		termsAndConditionsDetailsChain.execute(context);

		setTermsAndCondition((TermsAndConditionContext) context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.TERMS_AND_CONDITION, termsAndCondition);
		return SUCCESS;
	}

	public String termsAndConditionsList() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain termsAndConditionsListChain = ReadOnlyChainFactory.getTermsAndConditionsList();
		termsAndConditionsListChain.execute(context);
		setTermsAndConditions((List<TermsAndConditionContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST));
		setResult(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, termsAndConditions);
		
		return SUCCESS;
	}

	public String termsAndConditionsCount() throws Exception {
		return termsAndConditionsList();
	}
}
