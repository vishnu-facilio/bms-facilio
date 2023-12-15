package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PoAssociatedTermsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
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

	public List<V3PoAssociatedTermsContext> getTermsAssociated() {
		return termsAssociated;
	}

	public void setTermsAssociated(List<V3PoAssociatedTermsContext> termsAssociated) {
		this.termsAssociated = termsAssociated;
	}

	private List<V3PoAssociatedTermsContext> termsAssociated;
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

	private int inventoryType;
	
	public int getInventoryType() {
		return inventoryType;
	}
	public void setInventoryType(int inventoryType) {
		this.inventoryType = inventoryType;
	}
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long storeRoomId;
	
	
	public long getStoreRoomId() {
		return storeRoomId;
	}
	public void setStoreRoomId(long storeRoomId) {
		this.storeRoomId = storeRoomId;
	}
	private long vendorId;
	
	public long getVendorId() {
		return vendorId;
	}
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}
	
	private long serviceId;
	
	public long getServiceId() {
		return serviceId;
	}
	public void setServiceId(long serviceId) {
		this.serviceId = serviceId;
	}
	
	private long parentId;
	
	
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public String addPurchaseOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE_OR_EDIT);
		if(purchaseOrder.getId() > 0) {
			context.put(FacilioConstants.ContextNames.IS_EDIT, true);
		}
		context.put(FacilioConstants.ContextNames.RECORD, purchaseOrder);
		
		if(!CollectionUtils.isEmpty(prIds)) {
			context.put(FacilioConstants.ContextNames.PR_IDS, prIds);
		}
		
		FacilioChain chain = TransactionChainFactory.getAddPurchaseOrderChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String bulkStatusUpdate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.STATUS, getStatus());
		
		FacilioChain chain = TransactionChainFactory.getUpdatePurchaseOrderStatusChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	}
	
	private int status;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
 	 	
		FacilioChain chain = ReadOnlyChainFactory.getPurchaseOrderListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,context.get(FacilioConstants.ContextNames.RECORD_COUNT));
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
		
		FacilioChain chain = ReadOnlyChainFactory.getPurchaseOrderDetailsChain();
		chain.execute(context);

		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	
	public String deletePurchaseOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		
		FacilioChain chain = TransactionChainFactory.getPurchaseOrderDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	private PurchaseOrderLineItemContext lineItem;
	public PurchaseOrderLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(PurchaseOrderLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	private List<PurchaseOrderLineItemContext> lineItems;
	
	public List<PurchaseOrderLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<PurchaseOrderLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getLineItem());
		
		FacilioChain chain = TransactionChainFactory.getAddPurchaseOrderLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String deleteLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		FacilioChain chain = TransactionChainFactory.getDeletePurchaseOrderLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
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
		
		FacilioChain chain = TransactionChainFactory.getConvertPRToPOChain();
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
	
	private List<Long> prIds;
	
	public List<Long> getPrIds() {
		return prIds;
	}
	public void setPrIds(List<Long> prIds) {
		this.prIds = prIds;
	}
	public String completePurchaseOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDERS, Collections.singletonList(poId));
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, purchaseOrdersLineItems);
		FacilioChain chain = TransactionChainFactory.getPurchaseOrderCompleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIPT, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getPendingLineItems() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PO_ID, poId);
		FacilioChain chain = TransactionChainFactory.getPendingPOLineItemsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS));
		return SUCCESS;

	}
	
	public String getReceivedLineItems() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PO_ID, poId);
		FacilioChain chain = TransactionChainFactory.getReceivedPOLineItemsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS));
		return SUCCESS;

	}
	
	public String filterPoOnInventoryTypeAndStoreRoomId () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, getInventoryType() );
		context.put(FacilioConstants.ContextNames.ID, getId() );
		context.put(FacilioConstants.ContextNames.STORE_ROOM_ID, getStoreRoomId() );
		
		FacilioChain chain = TransactionChainFactory.getPOOnInventoryTypeIdChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDERS, context.get(FacilioConstants.ContextNames.PURCHASE_ORDERS));
		return SUCCESS;

		
	}
	
	public String getServicePriceForVendor() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.VENDOR_ID, getVendorId());
		context.put(FacilioConstants.ContextNames.SERVICE, getServiceId());
		FacilioChain chain = TransactionChainFactory.getServicePriceForVendor();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.UNIT_PRICE, context.get(FacilioConstants.ContextNames.UNIT_PRICE));
		
		return SUCCESS;
	
	}
	
	public String usePoServiceLineItems() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, purchaseOrdersLineItems);
		context.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, inventoryType);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		if(inventoryType == InventoryType.SERVICE.getValue()) {
			FacilioChain useLineItemListChain = TransactionChainFactory.getUseLineItemsForServiceChain();
			useLineItemListChain.execute(context);
		}
		
		setResult(FacilioConstants.ContextNames.WO_TOTAL_COST, context.get(FacilioConstants.ContextNames.WO_TOTAL_COST));
		return SUCCESS;
	}
	
	public String getAvailableServiceLineItems() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, inventoryType);
		
		if(inventoryType == InventoryType.SERVICE.getValue()) {
			FacilioChain getReceivedListListChain = ReadOnlyChainFactory.getReceivedPoLineItemList();
			getReceivedListListChain.execute(context);
		}
		
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS));
		return SUCCESS;
	}
	
	public String associateTerms() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId );
		context.put(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS, termsAssociated );
		
		FacilioChain chain = TransactionChainFactory.getAssociateTermsToPOChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS, context.get(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS));
		
		return SUCCESS;
	}
	public String manageTerms() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId );
		context.put(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS, termsAssociated );
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,recordIds);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);

		FacilioChain chain = TransactionChainFactory.getManageTermsToPOChain();
		chain.execute(context);

		setResult(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS, context.get(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS));

		return SUCCESS;
	}
	
	public String disAssociateTerms() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds );
		
		FacilioChain chain = TransactionChainFactory.getDisAssociateTermsToPOChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		
		return SUCCESS;
	}

}
