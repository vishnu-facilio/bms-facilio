package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class PurchaseRequestAction extends FacilioAction {
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
	
	private PurchaseRequestContext purchaseRequest;
	public PurchaseRequestContext getPurchaseRequest() {
		return purchaseRequest;
	}
	public void setPurchaseRequest(PurchaseRequestContext purchaseRequest) {
		this.purchaseRequest = purchaseRequest;
	}
	
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
		
	public String addPurchaseRequest() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, purchaseRequest);
		
		Chain chain = TransactionChainFactory.getAddPurchaseRequestChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PURCHASE_REQUEST, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String getPurchaseRequestList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "purchaserequest");
 		
 		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Purchase_Requests.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "purchaserequest.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		Chain chain = ReadOnlyChainFactory.getPurchaseRequestListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<PurchaseRequestContext> purchaseRequests = (List<PurchaseRequestContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.PURCHASE_REQUESTS, purchaseRequests);
		}
		return SUCCESS;
	}
	
	public String getPurchaseRequestDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, recordId);
		
		Chain chain = ReadOnlyChainFactory.getPurchaseRequestDetailsChain();
		chain.execute(context);
		
		PurchaseRequestContext purchaseRequestContext = (PurchaseRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.PURCHASE_REQUEST, purchaseRequestContext);
		
		return SUCCESS;
	}
	
	public String deletePurchaseRequest() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		
		Chain chain = TransactionChainFactory.getPurchaseRequestDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		return SUCCESS;
	}
	
	private PurchaseRequestLineItemContext lineItem;
	public PurchaseRequestLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(PurchaseRequestLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	
	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getLineItem());
		
		Chain chain = TransactionChainFactory.getAddPurchaseRequestLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	
	public String deleteLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		
		Chain chain = TransactionChainFactory.getDeletePurchaseRequestLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordId));
		
		return SUCCESS;
	}
	
	public String purchaseRequestCount() throws Exception {
		return getPurchaseRequestList();
	}

}
