package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ShipmentAction extends FacilioAction{

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
	
	private ShipmentContext shipment;
	public ShipmentContext getShipment() {
		return shipment;
	}
	public void setShipment(ShipmentContext shipment) {
		this.shipment = shipment;
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
    private int inventoryType;
	public int getInventoryType() {
		return inventoryType;
	}
	public void setInventoryType(int inventoryType) {
		this.inventoryType = inventoryType;
	}
	public String getShipmentList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "shipment");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Shipment.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "shipment.id");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		FacilioChain chain = ReadOnlyChainFactory.getShipmentListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<ShipmentContext> shipments = (List<ShipmentContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.SHIPMENTS, shipments);
		}
		return SUCCESS;
	}
	
	
	
	public String addOrUpdateShipment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, shipment);
		
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateShipmentChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.SHIPMENTS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getShipmentDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		FacilioChain chain = ReadOnlyChainFactory.getShipmentDetailsChain();
		chain.execute(context);
		
		ShipmentContext shipmentContext = (ShipmentContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.SHIPMENT, shipmentContext);
		
		return SUCCESS;
	}
	
	public String deleteShipment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		FacilioChain chain = TransactionChainFactory.getDeleteShipmentChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	public String shipmentListCount() throws Exception {
		return getShipmentList();
	}
	
	private ShipmentLineItemContext lineItem;
	public ShipmentLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(ShipmentLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	
	private ShipmentLineItemContext lineItems;
	
	public ShipmentLineItemContext getLineItems() {
		return lineItems;
	}
	public void setLineItems(ShipmentLineItemContext lineItems) {
		this.lineItems = lineItems;
	}
	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getLineItems());
		
		FacilioChain chain = TransactionChainFactory.getAddShipmentLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String deleteLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		FacilioChain chain = TransactionChainFactory.getDeleteShipmentLineItemChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		return SUCCESS;
	}
	
	public String receiveShipment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, shipment);
		context.put(FacilioConstants.ContextNames.SHIPMENT, shipment);
		
		FacilioChain chain = TransactionChainFactory.getReceiveShipmentChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.SHIPMENTS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String stageShipment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, shipment);
		context.put(FacilioConstants.ContextNames.SHIPMENT, shipment);
		
		FacilioChain chain = TransactionChainFactory.getStageShipmentChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.SHIPMENTS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String transfer() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, shipment);
		context.put(FacilioConstants.ContextNames.SHIPMENT, shipment);
		
		FacilioChain chain = TransactionChainFactory.getTransferShipmentChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.SHIPMENTS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
}
