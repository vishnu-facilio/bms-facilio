package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Collections;
import java.util.List;

public class PurchaseOrderAction extends FacilioAction {

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
	
	private PurchaseOrderContext purchaseOrder;
	public PurchaseOrderContext getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(PurchaseOrderContext purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	
	public String addPurchaseOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, purchaseOrder);
		
		Chain chain = TransactionChainFactory.getAddPurchaseOrderChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getPurchaseOrderList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "purchaseorder");
 		
 		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Purchase_Orders.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "purchaseorder.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		Chain chain = ReadOnlyChainFactory.getPurchaseOrderListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
		List<PurchaseOrderContext> purchaseOrders = (List<PurchaseOrderContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDERS, purchaseOrders);
		}
		
		return SUCCESS;
	}
	
	public String getPurchaseOrderDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		Chain chain = ReadOnlyChainFactory.getPurchaseOrderDetailsChain();
		chain.execute(context);
		
		PurchaseOrderContext purchaseOrderContext = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER, purchaseOrderContext);
		
		return SUCCESS;
	}
	
	public String deletePurchaseOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		
		Chain chain = TransactionChainFactory.getPurchaseOrderDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		return SUCCESS;
	}
	
	private PurchaseOrderLineItemContext lineItem;
	public PurchaseOrderLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(PurchaseOrderLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	
	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getLineItem());
		
		Chain chain = TransactionChainFactory.getAddPurchaseOrderLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	
	public String deleteLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		
		Chain chain = TransactionChainFactory.getDeletePurchaseOrderLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		
		return SUCCESS;
	}
	
	private List<Long> recordIds;
	public List<Long> getRecordIds() {
		return recordIds;
	}
	public void setRecordIds(List<Long> recordIds) {
		this.recordIds = recordIds;
	}
	
	public String convertPRToPO() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		Chain chain = TransactionChainFactory.getConvertPRToPOChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	
	public String purchaseOrderCount() throws Exception {
		return getPurchaseOrderList();
	}
	
	private List<PurchaseOrderLineItemContext> purchaseOrdersLineItems;
	public List<PurchaseOrderLineItemContext> getPurchaseOrdersLineItems() {
		return purchaseOrdersLineItems;
	}
	public void setPurchaseOrdersLineItems(List<PurchaseOrderLineItemContext> lineItems) {
		this.purchaseOrdersLineItems = lineItems;
	}
	
	private long poId;
	public long getPoId() {
		return poId;
	}
	public void setPoId(long poId) {
		this.poId = poId;
	}
	
	
	public String completePurchaseOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDERS, Collections.singletonList(poId));
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, purchaseOrdersLineItems);
		Chain chain = TransactionChainFactory.getPurchaseOrderCompleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIPT, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getPendingLineItems() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PO_ID, poId);
		Chain chain = TransactionChainFactory.getPendingPOLineItemsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS));
		return SUCCESS;

	}
	
	public String getReceivedLineItems() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PO_ID, poId);
		Chain chain = TransactionChainFactory.getReceivedPOLineItemsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS));
		return SUCCESS;

	}

}
