package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.purchaserequest.PrAssociatedTermsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Collections;
import java.util.List;

public class PurchaseRequestAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	private List<PrAssociatedTermsContext> termsAssociated;

	public List<PrAssociatedTermsContext> getTermsAssociated() {
		return termsAssociated;
	}

	public void setTermsAssociated(List<PrAssociatedTermsContext> termsAssociated) {
		this.termsAssociated = termsAssociated;
	}
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
	private int status;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	private List<Long> recordIds;
	
	public List<Long> getRecordIds() {
		return recordIds;
	}
	public void setRecordIds(List<Long> recordIds) {
		this.recordIds = recordIds;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
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
	public String addPurchaseRequest() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, purchaseRequest);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE_OR_EDIT);
		
		FacilioChain chain = TransactionChainFactory.getAddPurchaseRequestChain();
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
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
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
 	 	
		FacilioChain chain = ReadOnlyChainFactory.getPurchaseRequestListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,context.get(FacilioConstants.ContextNames.RECORD_COUNT));
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
		
		FacilioChain chain = ReadOnlyChainFactory.getPurchaseRequestDetailsChain();
		chain.execute(context);
		
		PurchaseRequestContext purchaseRequestContext = (PurchaseRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.PURCHASE_REQUEST, purchaseRequestContext);
		
		return SUCCESS;
	}
	
	public String deletePurchaseRequest() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		FacilioChain chain = TransactionChainFactory.getPurchaseRequestDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
	private PurchaseRequestLineItemContext lineItem;
	public PurchaseRequestLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(PurchaseRequestLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	
	private List<PurchaseRequestLineItemContext> lineItems;
	
	public List<PurchaseRequestLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<PurchaseRequestLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	public String addOrUpdateLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, getLineItems());
		
		FacilioChain chain = TransactionChainFactory.getAddPurchaseRequestLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String bulkStatusUpdate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		context.put(FacilioConstants.ContextNames.STATUS, getStatus());
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		
		
		FacilioChain chain = TransactionChainFactory.getUpdatePurchaseRequestStatusChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	}
	
	public String deleteLineItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		FacilioChain chain = TransactionChainFactory.getDeletePurchaseRequestLineItem();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(recordIds));
		
		return SUCCESS;
	}
	
	public String purchaseRequestCount() throws Exception {
		return getPurchaseRequestList();
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

	public String associateTerms() throws Exception {

		FacilioChain chain = TransactionChainFactory.getAssociateTermsToPRChain();
		chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, recordId );
		chain.getContext().put(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS, termsAssociated );
		chain.execute();

		setResult(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS, chain.getContext().get(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS));

		return SUCCESS;
	}
	public String manageTerms() throws Exception {

		FacilioChain chain = TransactionChainFactory.getManageTermsToPRChain();
		chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, recordId );
		chain.getContext().put(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS, termsAssociated );
		chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST,recordIds);
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS);
		chain.execute();

		setResult(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS, chain.getContext().get(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS));

		return SUCCESS;
	}

	public String disAssociateTerms() throws Exception {

		FacilioChain chain = TransactionChainFactory.getDisAssociateTermsToPRChain();
		chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds );

		chain.execute();

		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, chain.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));

		return SUCCESS;
	}



}
