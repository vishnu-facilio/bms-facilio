package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import java.util.Collections;
import java.util.List;

public class WorkorderItemsAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	private List<WorkorderItemContext> workorderItems;
	public List<WorkorderItemContext> getWorkorderItems() {
		return workorderItems;
	}
	public void setWorkorderItems(List<WorkorderItemContext> workorderItems) {
		this.workorderItems = workorderItems;
	}
	
	private List<Long> workorderItemsId;
	public List<Long> getWorkorderItemsId() {
		return workorderItemsId;
	}
	public void setWorkorderItemsId(List<Long> workorderItemsId) {
		this.workorderItemsId = workorderItemsId;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public String addOrUpdateWorkorderItems() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderItems);
		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddOrUdpateWorkorderItemsChain();
		addWorkorderPartChain.execute(context);
		setWorkorderItemsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		double cost = (double) context.get(FacilioConstants.ContextNames.TOTAL_COST);
		long qty = (long) context.get(FacilioConstants.ContextNames.TOTAL_QUANTITY);
		setResult(FacilioConstants.ContextNames.TOTAL_QUANTITY, qty);
		setResult(FacilioConstants.ContextNames.TOTAL_COST, cost);
		double totalCost = (double) context.get(FacilioConstants.ContextNames.WO_TOTAL_COST);
		setResult(FacilioConstants.ContextNames.WO_TOTAL_COST, totalCost);
		setResult("workorderItemsId", workorderItemsId);
		setResult("workorderItems", workorderItems);
		return SUCCESS;
	} 
	
	public String deleteWorkorderItems() throws Exception {
		FacilioContext context = new FacilioContext();
	
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, workorderItemsId);
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(parentId));
		context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 1);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteWorkorderItemChain();
		deleteInventoryChain.execute(context);
		setWorkorderItemsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		double cost = (double) context.get(FacilioConstants.ContextNames.TOTAL_COST);
		long qty = (long) context.get(FacilioConstants.ContextNames.TOTAL_QUANTITY);
		setResult(FacilioConstants.ContextNames.TOTAL_QUANTITY, qty);
		setResult(FacilioConstants.ContextNames.TOTAL_COST, cost);
		double totalCost = (double) context.get(FacilioConstants.ContextNames.WO_TOTAL_COST);
		setResult(FacilioConstants.ContextNames.WO_TOTAL_COST, totalCost);
		setResult("workorderItemsId", workorderItemsId);
		return SUCCESS;
	}
	
	public String workorderItemsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		Command getWorkorderPartsList = ReadOnlyChainFactory.getWorkorderItemsList();
		getWorkorderPartsList.execute(context);
		workorderItems = ((List<WorkorderItemContext>) context.get(FacilioConstants.ContextNames.WORKORDER_ITEMS));
		setResult(FacilioConstants.ContextNames.WORKORDER_ITEMS, workorderItems);
		return SUCCESS;
	}

	private List<Long> purchasedItems;
	public List<Long> getPurchasedItems() {
		return purchasedItems;
	}
	public void setPurchasedItems(List<Long> purchasedItems) {
		this.purchasedItems = purchasedItems;
	}
	
}
