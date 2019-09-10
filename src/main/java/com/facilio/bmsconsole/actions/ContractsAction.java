package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractAssociatedTermsContext;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ContractsAction extends FacilioAction{

	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private int contractType;
	
	public int getContractType() {
		return contractType;
	}
	public void setContractType(int contractType) {
		this.contractType = contractType;
	}
	
	private ContractsContext record;
	
	public ContractsContext getRecord() {
		return record;
	}
	public void setRecord(ContractsContext record) {
		this.record = record;
	}
	
	private List<ContractAssociatedTermsContext> associatedTerms;
	
	public List<ContractAssociatedTermsContext> getAssociatedTerms() {
		return associatedTerms;
	}
	public void setAssociatedTerms(List<ContractAssociatedTermsContext> associatedTerms) {
		this.associatedTerms = associatedTerms;
	}
	
	private List<ContractAssociatedAssetsContext> associatedAssets;
	
	public List<ContractAssociatedAssetsContext> getAssociatedAssets() {
		return associatedAssets;
	}
	public void setAssociatedAssets(List<ContractAssociatedAssetsContext> associatedAssets) {
		this.associatedAssets = associatedAssets;
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
	
	public String getContractList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "contracts");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Contracts.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "contracts.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		Chain chain = ReadOnlyChainFactory.getContractListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<ContractsContext> contracts = (List<ContractsContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.CONTRACTS, contracts);
		}
		return SUCCESS;
	}

	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTRACT_TYPE, getContractType());
		Chain chain = TransactionChainFactory.getAddPurchaseContractLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	} 
	
	public String getActiveContractAssociatedAssets() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTRACT_TYPE, getContractType());
		Chain chain = TransactionChainFactory.getActiveContractAssociatedAssetChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ASSET_ID, context.get(FacilioConstants.ContextNames.ASSET_ID));
		
		return SUCCESS;
	}
	
	public String changeNextPaymentDate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
		Chain chain = TransactionChainFactory.getChangeContractPaymentStatusChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.NEXT_PAYMENT_DATE, context.get(FacilioConstants.ContextNames.NEXT_PAYMENT_DATE));
		
		return SUCCESS;
	}
	
	public String associateTerms() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId );
		context.put(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS, associatedTerms );
		
		Chain chain = TransactionChainFactory.getAssociateTermsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS, context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS));
		
		return SUCCESS;
	}
	
   public String associateAsset() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId );
		context.put(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS, associatedAssets );
		
		Chain chain = TransactionChainFactory.getAssociateAssetChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS, context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS));
		
		return SUCCESS;
	}
   
   public String disAssociateAssets() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds );
		
		Chain chain = TransactionChainFactory.getDisAssociateAssetChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS, context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS));
		
		return SUCCESS;
	}
   
   public String disAssociateTerms() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds );
		
		Chain chain = TransactionChainFactory.getDisAssociateTermsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS, context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS));
		
		return SUCCESS;
	}
}
