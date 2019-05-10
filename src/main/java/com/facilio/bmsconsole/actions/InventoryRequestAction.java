package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class InventoryRequestAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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
	
	private InventoryRequestContext inventoryRequest;
	private long recordId = -1;
	private List<Long> recordIds;
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	
	public InventoryRequestContext getInventoryRequest() {
		return inventoryRequest;
	}
	public void setInventoryRequest(InventoryRequestContext inventoryRequest) {
		this.inventoryRequest = inventoryRequest;
	}
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	public List<Long> getRecordIds() {
		return recordIds;
	}
	public void setRecordIds(List<Long> recordIds) {
		this.recordIds = recordIds;
	}
	private long requesterId;
	
	public long getRequesterId() {
		return requesterId;
	}
	public void setRequesterId(long requesterId) {
		this.requesterId = requesterId;
	}
	
	private long parentId;
	
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private long storeRoomId;
	
	public long getStoreRoomId() {
		return storeRoomId;
	}
	public void setStoreRoomId(long storeRoomId) {
		this.storeRoomId = storeRoomId;
	}

	private int status;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	private int inventoryType;
	
	public int getInventoryType() {
		return inventoryType;
	}
	public void setInventoryType(int inventoryType) {
		this.inventoryType = inventoryType;
	}

	private List<InventoryRequestLineItemContext> lineItems;
	
	public List<InventoryRequestLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<InventoryRequestLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	public String addOrUpdateInventoryRequest() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, inventoryRequest);
		
		Chain chain = TransactionChainFactory.getAddOrUpdateInventoryRequestChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.INVENTORY_REQUEST, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getInventoryRequestList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.INVENTORY_REQUEST);
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Inventory_Requests.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "inventoryrequest.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		Chain chain = ReadOnlyChainFactory.getInventoryRequestListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<InventoryRequestContext> inventoryRequests = (List<InventoryRequestContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.INVENTORY_REQUESTS, inventoryRequests);
		}
		return SUCCESS;
	}
	
	public String getInventoryRequestCount() throws Exception {
		return getInventoryRequestList();
	}
	
	public String deleteInventoryRequest() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		
		Chain chain = TransactionChainFactory.getInventoryRequestDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	public String getInventoryRequestDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		Chain chain = ReadOnlyChainFactory.getInventoryRequestDetailsChain();
		chain.execute(context);
		
		InventoryRequestContext inventoryRequestContext = (InventoryRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.INVENTORY_REQUEST, inventoryRequestContext);
		
		return SUCCESS;
	
	}
	
	private InventoryRequestLineItemContext lineItem;
	public InventoryRequestLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(InventoryRequestLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	
	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getLineItem());
		
		Chain chain = TransactionChainFactory.getAddInventoryRequestLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	
	public String deleteLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		
		Chain chain = TransactionChainFactory.getDeleteInventoryRequestLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		
		return SUCCESS;
	}
	
	
	public String getInventoryRequestLineItemListByRequesterId() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.REQUESTER, requesterId);
		context.put(FacilioConstants.ContextNames.STATUS, status);
		
		Chain lineItemListChain = ReadOnlyChainFactory.getInventoryRequestLineItemListByRequesterIdChain();
		lineItemListChain.execute(context);
		setLineItems((List<InventoryRequestLineItemContext>)context.get(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS));
		setResult(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, lineItems);
		
		return SUCCESS;
	}
	
	public String getInventoryRequestLineItemListByParentId() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		context.put(FacilioConstants.ContextNames.STATUS, status);
		
		Chain lineItemListChain = ReadOnlyChainFactory.getInventoryRequestLineItemListByParentIdChain();
		lineItemListChain.execute(context);
		setLineItems((List<InventoryRequestLineItemContext>)context.get(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS));
		setResult(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, lineItems);
		
		return SUCCESS;
	}
	
	public String getInventoryRequestLineItemListByStoreRoomId() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.STORE_ROOM_ID, storeRoomId);
		context.put(FacilioConstants.ContextNames.STATUS, status);
		
		Chain lineItemListChain = ReadOnlyChainFactory.getInventoryRequestLineItemListByStoreRoomIdChain();
		lineItemListChain.execute(context);
		setLineItems((List<InventoryRequestLineItemContext>)context.get(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS));
		setResult(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, lineItems);
		
		return SUCCESS;
	}
	
	
	public String useLineItems() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		context.put(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, lineItems);
		context.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, inventoryType);
		if(inventoryType == InventoryType.ITEM.getValue()) {
			Chain useLineItemListChain = TransactionChainFactory.getUseLineItemsForItemsChain();
			useLineItemListChain.execute(context);
		}
		else if(inventoryType == InventoryType.TOOL.getValue()) {
			Chain useLineItemListChain = TransactionChainFactory.getUseLineItemsForToolsChain();
			useLineItemListChain.execute(context);
		}
		
		setResult(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, lineItems);
		
		return SUCCESS;
	}

}
