package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.RentalLeaseContractContext;
import com.facilio.bmsconsole.context.RentalLeaseContractLineItemsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class RentalLeaseContractAction extends FacilioAction{

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
	
	private RentalLeaseContractContext rentalLeaseContract;
	
	public RentalLeaseContractContext getRentalLeaseContract() {
		return rentalLeaseContract;
	}
	public void setRentalLeaseContract(RentalLeaseContractContext rentalLeaseContract) {
		this.rentalLeaseContract = rentalLeaseContract;
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
	
	private List<ContractAssociatedAssetsContext> associatedAssets;
	
	public List<ContractAssociatedAssetsContext> getAssociatedAssets() {
		return associatedAssets;
	}
	public void setAssociatedAssets(List<ContractAssociatedAssetsContext> associatedAssets) {
		this.associatedAssets = associatedAssets;
	}
	public String getRentalLeaseContractList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "rentalLeasecontracts");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Rental_Lease_Contracts.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "rentalleasecontracts.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		FacilioChain chain = ReadOnlyChainFactory.getRentalLeaseContractListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<RentalLeaseContractContext> rentalLeaseContracts = (List<RentalLeaseContractContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS, rentalLeaseContracts);
		}
		return SUCCESS;
	}
	
	
	private RentalLeaseContractLineItemsContext lineItem;
	public RentalLeaseContractLineItemsContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(RentalLeaseContractLineItemsContext lineItem) {
		this.lineItem = lineItem;
	}
	
	
	public String bulkStatusUpdate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		context.put(FacilioConstants.ContextNames.STATUS, getStatus());
		
		FacilioChain chain = TransactionChainFactory.getUpdateRentalLeaseContractStatusChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	}
	
	public String addRentalLeaseContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, rentalLeaseContract);
		context.put(FacilioConstants.ContextNames.IS_CONTRACT_REVISED, false );
		
		
		FacilioChain chain = TransactionChainFactory.getAddRentalLeaseContractChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACT, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getRentalLeaseContractDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		FacilioChain chain = ReadOnlyChainFactory.getRentalLeaseContractDetailsChain();
		chain.execute(context);
		
		RentalLeaseContractContext rentalLeaseContractContext = (RentalLeaseContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACT, rentalLeaseContractContext);
		
		return SUCCESS;
	}
	
	public String deleteRentalLeaseContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		FacilioChain chain = TransactionChainFactory.getRentalLeaseContractDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	public String rentalLeaseContractCount() throws Exception {
		return getRentalLeaseContractList();
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
		context.put(FacilioConstants.ContextNames.RECORD, rentalLeaseContract );
		context.put(FacilioConstants.ContextNames.IS_CONTRACT_REVISED, true );
		
		FacilioChain chain = TransactionChainFactory.getAddRentalLeaseContractChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.REVISED_RECORD, context.get(FacilioConstants.ContextNames.REVISED_RECORD));
		
		
		return SUCCESS;
	}

	public String duplicateContract() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, rentalLeaseContract );
		
		FacilioChain chain = TransactionChainFactory.getDuplicateRentalLeaseContract();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	public String returnAsset() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS, associatedAssets );
		
		FacilioChain chain = TransactionChainFactory.getReturnAssetChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	}
	
	public String purchaseAsset() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS, associatedAssets );
		
		FacilioChain chain = TransactionChainFactory.getPurchaseAssetChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	public String deleteLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		FacilioChain chain = TransactionChainFactory.getDeleteRentalLeaseContractLineItemChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		return SUCCESS;
	}
	
	private List<RentalLeaseContractLineItemsContext> lineItems;
	
	public List<RentalLeaseContractLineItemsContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<RentalLeaseContractLineItemsContext> lineItems) {
		this.lineItems = lineItems;
	}
	
	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getLineItems());
		
		FacilioChain chain = TransactionChainFactory.getAddRentalLeaseContractLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
}
