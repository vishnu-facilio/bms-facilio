package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WarrantyContractContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class WarrantyContractAction extends FacilioAction {

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
	
	private WarrantyContractContext warrantyContract;
	
	public WarrantyContractContext getWarrantyContract() {
		return warrantyContract;
	}
	public void setWarrantyContract(WarrantyContractContext warrantyContract) {
		this.warrantyContract = warrantyContract;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	private int status;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	private int type;
	private long assetId;
	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	public String getWarrantyContractList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "warrantycontracts");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Warranty_Contracts.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "warrantycontracts.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		Chain chain = ReadOnlyChainFactory.getWarrantyContractListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<WarrantyContractContext> warrantyContracts = (List<WarrantyContractContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.WARRANTY_CONTRACTS, warrantyContracts);
		}
		return SUCCESS;
	}
	
	
	public String bulkStatusUpdate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		context.put(FacilioConstants.ContextNames.STATUS, getStatus());
		
		Chain chain = TransactionChainFactory.getUpdateWarrantyContractStatusChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	}
	
	public String addWarrantyContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, warrantyContract);
		context.put(FacilioConstants.ContextNames.IS_CONTRACT_REVISED, false );
		
		Chain chain = TransactionChainFactory.getAddWarrantyContractChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.WARRANTY_CONTRACTS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getWarrantyContractDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		Chain chain = ReadOnlyChainFactory.getWarrantyContractDetailsChain();
		chain.execute(context);
		
		WarrantyContractContext warrantyContractContext = (WarrantyContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.WARRANTY_CONTRACT, warrantyContractContext);
		
		return SUCCESS;
	}
	
	public String deleteWarrantyContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		Chain chain = TransactionChainFactory.getWarrantyContractDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	public String warrantyContractCount() throws Exception {
		return getWarrantyContractList();
	}
	
	public String getWarrantyContractForAsset() throws Exception {
	    
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
		getWarrantyContractList();
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	public String reviseContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId );
		context.put(FacilioConstants.ContextNames.IS_CONTRACT_REVISED, true );
		
		Chain chain = TransactionChainFactory.getAddWarrantyContractChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.WARRANTY_CONTRACTS, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	public String duplicateContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, warrantyContract );
		
		Chain chain = TransactionChainFactory.getDuplicateWarrantyContract();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.WARRANTY_CONTRACTS, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
}
