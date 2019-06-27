package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Collections;
import java.util.List;

public class LabourContractAction extends FacilioAction{

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
	
	private LabourContractContext labourContract;
	public LabourContractContext getLabourContract() {
		return labourContract;
	}
	public void setLabourContract(LabourContractContext labourContract) {
		this.labourContract = labourContract;
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

	
	public String getLabourContractList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "labourcontracts");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Labour_Contracts.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "labourcontracts.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		Chain chain = ReadOnlyChainFactory.getLabourContractListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<LabourContractContext> labourContracts = (List<LabourContractContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.LABOUR_CONTRACTS, labourContracts);
		}
		return SUCCESS;
	}
	
	
	private LabourContractLineItemContext lineItem;
	public LabourContractLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(LabourContractLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	
	private List<LabourContractContext> lineItems;
	
	public List<LabourContractContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<LabourContractContext> lineItems) {
		this.lineItems = lineItems;
	}
	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getLineItems());
		
		Chain chain = TransactionChainFactory.getAddLabourContractLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String bulkStatusUpdate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		context.put(FacilioConstants.ContextNames.STATUS, getStatus());
		
		Chain chain = TransactionChainFactory.getUpdateLabourContractStatusChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	}
	
	public String addLabourContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, labourContract);
		context.put(FacilioConstants.ContextNames.IS_CONTRACT_REVISED, false );
		
		
		Chain chain = TransactionChainFactory.getAddLabourContractChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.LABOUR_CONTRACTS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getLabourContractDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		Chain chain = ReadOnlyChainFactory.getLabourContractDetailsChain();
		chain.execute(context);
		
		LabourContractContext labourContractContext = (LabourContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.LABOUR_CONTRACT, labourContractContext);
		
		return SUCCESS;
	}
	
	public String deleteLabourContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		Chain chain = TransactionChainFactory.getLabourContractDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	public String labourContractCount() throws Exception {
		return getLabourContractList();
	}
	
	public String deleteLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		Chain chain = TransactionChainFactory.getDeleteLabourContractLineItemChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		return SUCCESS;
	}
	

	public String reviseContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, labourContract );
		context.put(FacilioConstants.ContextNames.IS_CONTRACT_REVISED, true );
		
		Chain chain = TransactionChainFactory.getAddLabourContractChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.REVISED_RECORD, context.get(FacilioConstants.ContextNames.REVISED_RECORD));
		
		
		return SUCCESS;
	}
	
	public String duplicateContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, labourContract );
		
		Chain chain = TransactionChainFactory.getDuplicateLabourContract();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.LABOUR_CONTRACTS, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}

}
