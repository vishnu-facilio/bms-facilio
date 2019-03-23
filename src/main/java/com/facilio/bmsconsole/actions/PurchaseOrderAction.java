package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

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
		
		Chain chain = ReadOnlyChainFactory.getPurchaseOrderListChain();
		chain.execute(context);
		
		List<PurchaseOrderContext> purchaseOrders = (List<PurchaseOrderContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.PURCHASE_ORDERS, purchaseOrders);
		
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
}
