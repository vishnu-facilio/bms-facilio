package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class InventoryCostAction extends FacilioAction{
	private static final long serialVersionUID = 1L;

	private InventoryCostContext inventoryCost;
	public InventoryCostContext getInventoryCost() {
		return inventoryCost;
	}
	public void setInventoryCost(InventoryCostContext inventoryCost) {
		this.inventoryCost = inventoryCost;
	}
	
	private long inventoryCostId;
	public long getInventoryCostId() {
		return inventoryCostId;
	}
	public void setInventoryCostId(long inventoryCostId) {
		this.inventoryCostId = inventoryCostId;
	}
	
	private long inventoryId;
	public long getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(long inventoryId) {
		this.inventoryId = inventoryId;
	}
	
	public String updateInventoryCost() throws Exception {
		FacilioContext context = new FacilioContext();
		double quantity = inventoryCost.getQuantity();
		inventoryCost.setCurrentQuantity(quantity);
		inventoryCost.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, inventoryCost);
		context.put(FacilioConstants.ContextNames.ID, inventoryCost.getId());
		context.put(FacilioConstants.ContextNames.INVENTORY_ID, inventoryId);
		context.put(FacilioConstants.ContextNames.INVENTORY_IDS, Collections.singletonList(inventoryId));
		context.put(FacilioConstants.ContextNames.RECORD_ID, inventoryCost.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(inventoryCost.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		Chain updateInventryChain = TransactionChainFactory.getUpdateInventoryCostChain();
		updateInventryChain.execute(context);
		setInventoryCostId(inventoryCost.getId());
		inventoryCostDetails();
		setResult(FacilioConstants.ContextNames.INVENTORY_COST, inventoryCost);
		return SUCCESS;
	}
	
	public String inventoryCostDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getInventoryCostId());

		Chain inventryDetailsChain = ReadOnlyChainFactory.fetchInventoryCostDetails();
		inventryDetailsChain.execute(context);

		setInventoryCost((InventoryCostContext) context.get(FacilioConstants.ContextNames.INVENTORY_COST));
		setResult(FacilioConstants.ContextNames.INVENTORY_COST, inventoryCost);
		return SUCCESS;
	}
	
	public String deleteInventoryCost() throws Exception {
		FacilioContext context = new FacilioContext();
		InventoryCostContext inventoryCost = new InventoryCostContext();
		inventoryCost.setDeleted(true);

		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, inventoryCost);
		context.put(FacilioConstants.ContextNames.INVENTORY_ID, inventoryId);
		context.put(FacilioConstants.ContextNames.INVENTORY_IDS, Collections.singletonList(inventoryId));
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, inventoryCostsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteInventoryCostChain();
		deleteInventoryChain.execute(context);
		setInventoryCostsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		setResult("inventoryCostsId", inventoryCostsId);
		return SUCCESS;
	}
	
	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}

	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	
	private List<Long> inventoryCostsId;
	public List<Long> getInventoryCostsId() {
		return inventoryCostsId;
	}
	public void setInventoryCostsId(List<Long> inventoryCostsId) {
		this.inventoryCostsId = inventoryCostsId;
	}

}
