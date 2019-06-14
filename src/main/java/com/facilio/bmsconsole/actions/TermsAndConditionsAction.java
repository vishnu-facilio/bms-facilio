package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.bmsconsole.context.WarrantyContractContext;
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
		context.put(FacilioConstants.ContextNames.RECORD, termsAndCondition);
		Chain addTermsAndConditions = TransactionChainFactory.getAddTermsAndConditionsChain();
		addTermsAndConditions.execute(context);
		setResult(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, termsAndCondition);
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
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "termsandconditions");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Terms_And_Conditions.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "termsandconditions.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		Chain chain = ReadOnlyChainFactory.getTermsAndConditionsList();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<TermsAndConditionContext> terms = (List<TermsAndConditionContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, terms);
		}
		return SUCCESS;
	}

	public String termsAndConditionsCount() throws Exception {
		return termsAndConditionsList();
	}
}
