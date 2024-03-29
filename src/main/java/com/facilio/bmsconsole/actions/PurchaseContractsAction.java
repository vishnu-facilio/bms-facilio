package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PurchaseContractContext;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class PurchaseContractsAction extends FacilioAction {

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
	
	private PurchaseContractContext purchaseContract;
	public PurchaseContractContext getPurchaseContract() {
		return purchaseContract;
	}
	public void setPurchaseContract(PurchaseContractContext purchaseContract) {
		this.purchaseContract = purchaseContract;
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

	private long vendorId;
	
	
	public long getVendorId() {
		return vendorId;
	}
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}
	
	private long itemTypeId;
	
	
	public long getItemTypeId() {
		return itemTypeId;
	}
	public void setItemTypeId(long itemTypeId) {
		this.itemTypeId = itemTypeId;
	}
	
	private long toolTypeId;
	public long getToolTypeId() {
		return toolTypeId;
	}
	public void setToolTypeId(long toolTypeId) {
		this.toolTypeId = toolTypeId;
	}
	private int inventoryType;
	
	
	public int getInventoryType() {
		return inventoryType;
	}
	public void setInventoryType(int inventoryType) {
		this.inventoryType = inventoryType;
	}
	public String getPurchaseContractList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "purchasecontracts");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Purchase_Contracts.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "purchasecontracts.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		FacilioChain chain = ReadOnlyChainFactory.getPurchaseContractListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<PurchaseContractContext> purchaseContracts = (List<PurchaseContractContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.PURCHASE_CONTRACTS, purchaseContracts);
		}
		return SUCCESS;
	}
	
	
	private PurchaseContractLineItemContext lineItem;
	public PurchaseContractLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(PurchaseContractLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	
	private List<PurchaseContractLineItemContext> lineItems;
	
	public List<PurchaseContractLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<PurchaseContractLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getLineItems());
		
		FacilioChain chain = TransactionChainFactory.getAddPurchaseContractLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	
	public String bulkStatusUpdate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		context.put(FacilioConstants.ContextNames.STATUS, getStatus());
		
		FacilioChain chain = TransactionChainFactory.getUpdatePurchaseContractStatusChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	}
	
	public String addPurchaseContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, purchaseContract);
		context.put(FacilioConstants.ContextNames.IS_CONTRACT_REVISED, false );
		
		
		FacilioChain chain = TransactionChainFactory.getAddPurchaseContractChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_CONTRACTS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getPurchaseContractDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		FacilioChain chain = ReadOnlyChainFactory.getPurchaseContractDetailsChain();
		chain.execute(context);
		
		PurchaseContractContext purchaseContractContext = (PurchaseContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.PURCHASE_CONTRACT, purchaseContractContext);
		
		return SUCCESS;
	}
	
	public String deletePurchaseContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		FacilioChain chain = TransactionChainFactory.getPurchaseContractDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	public String purchaseContractCount() throws Exception {
		return getPurchaseContractList();
	}
	
	public String deleteLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		FacilioChain chain = TransactionChainFactory.getDeletePurchaseContractLineItemChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		return SUCCESS;
	}
	
	public String getActiveContractPrice() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.VENDOR_ID, getVendorId());
		context.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, getInventoryType());
		if(inventoryType == 1) {
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, getItemTypeId());
		}
		else if(inventoryType == 2) {
			context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, getToolTypeId());
		}
		FacilioChain chain = TransactionChainFactory.getActivePurchaseContractPrice();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.UNIT_PRICE, context.get(FacilioConstants.ContextNames.UNIT_PRICE));
		
		return SUCCESS;
	
	}
	
	public String reviseContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, purchaseContract );
		context.put(FacilioConstants.ContextNames.IS_CONTRACT_REVISED, true );
		
		FacilioChain chain = TransactionChainFactory.getAddPurchaseContractChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.REVISED_RECORD, context.get(FacilioConstants.ContextNames.REVISED_RECORD));
		
		return SUCCESS;
	}
	
	
	public String duplicateContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, purchaseContract );
		
		FacilioChain chain = TransactionChainFactory.getDuplicatePurchaseContract();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_CONTRACTS, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	
}
