package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InventoryVendorContext;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;

public class InventoryVendorAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	public String addInventoryVendor() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, inventoryVendor);
		Chain addInventoryVendorChain = TransactionChainFactory.getAddVendorChain();
		addInventoryVendorChain.execute(context);
		setInventoryVendorId(inventoryVendor.getId());
		setResult(FacilioConstants.ContextNames.INVENTORY_VENDOR, inventoryVendor);
		return SUCCESS;
	}

	public String updateInventoryVendor() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, inventoryVendor);
		context.put(FacilioConstants.ContextNames.ID, inventoryVendor.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(inventoryVendor.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		Chain updateInventoryChain = TransactionChainFactory.getUpdateVendorChain();
		updateInventoryChain.execute(context);
		setInventoryVendorId(inventoryVendor.getId());
		inventoryVendorDetails();
		// setResult(FacilioConstants.ContextNames.INVENTORY_VENDOR,
		// inventoryVendor);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
	}

	public String inventoryVendorDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getInventoryVendorId());

		Chain inventoryVendorDetailsChain = ReadOnlyChainFactory.fetchInventoryVendorDetails();
		inventoryVendorDetailsChain.execute(context);

		setInventoryVendor((InventoryVendorContext) context.get(FacilioConstants.ContextNames.INVENTORY_VENDOR));
		setResult(FacilioConstants.ContextNames.INVENTORY_VENDOR, inventoryVendor);
		return SUCCESS;
	}

	public String deleteInventoryVendor() throws Exception {
		FacilioContext context = new FacilioContext();
		InventoryVendorContext inventoryVendors = new InventoryVendorContext();
		inventoryVendors.setDeleted(true);

		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, inventoryVendors);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, inventoryVendorsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteVendorChain();
		deleteInventoryChain.execute(context);
		setInventoryVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
//		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult("inventoryVendorsId", inventoryVendorsId);
		return SUCCESS;
	}

	public String inventoryVendorsList() throws Exception {
		FacilioContext context = new FacilioContext();
		Command getVendorsList = ReadOnlyChainFactory.getInventoryVendorsList();
		getVendorsList.execute(context);
		inventoryVendors = ((List<InventoryVendorContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST));
		setResult(FacilioConstants.ContextNames.INVENTORY_VENDOR_LIST, inventoryVendors);
		return SUCCESS;
	}

	private InventoryVendorContext inventoryVendor;

	public InventoryVendorContext getInventoryVendor() {
		return inventoryVendor;
	}

	public void setInventoryVendor(InventoryVendorContext inventoryVendor) {
		this.inventoryVendor = inventoryVendor;
	}

	private long inventoryVendorId;

	public long getInventoryVendorId() {
		return inventoryVendorId;
	}

	public void setInventoryVendorId(long inventoryVendorId) {
		this.inventoryVendorId = inventoryVendorId;
	}

	private int rowsUpdated;

	public int getRowsUpdated() {
		return rowsUpdated;
	}

	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}

	private List<Long> inventoryVendorsId;

	public List<Long> getInventoryVendorsId() {
		return inventoryVendorsId;
	}

	public void setInventoryVendorsId(List<Long> inventoryVendorsId) {
		this.inventoryVendorsId = inventoryVendorsId;
	}

	private List<InventoryVendorContext> inventoryVendors;

	public List<InventoryVendorContext> getInventoryVendors() {
		return inventoryVendors;
	}

	public void setInventoryVendors(List<InventoryVendorContext> inventoryVendors) {
		this.inventoryVendors = inventoryVendors;
	}

}
