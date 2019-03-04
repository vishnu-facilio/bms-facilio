package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemVendorsContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ItemVendorActions extends FacilioAction{
	private static final long serialVersionUID = 1L;

	private List<ItemVendorsContext> itemVendors;
	public List<ItemVendorsContext> getItemVendors() {
		return itemVendors;
	}
	public void setItemVendors(List<ItemVendorsContext> itemVendors) {
		this.itemVendors = itemVendors;
	}
	
	private List<Long> itemsVendorsId;
	public List<Long> getItemsVendorsId() {
		return itemsVendorsId;
	}
	public void setItemsVendorsId(List<Long> itemsVendorsId) {
		this.itemsVendorsId = itemsVendorsId;
	}
	
	private long itemId;
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	
	public String addItemVendors() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemVendors);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddItemVendorsChain();
		addWorkorderPartChain.execute(context);
		setItemsVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("itemsVendorsId", itemsVendorsId);
		return SUCCESS;
	}
	
	public String udpateItemVendors() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemVendors);
		Chain addWorkorderPartChain = TransactionChainFactory.getUpdateItemVendorsChain();
		addWorkorderPartChain.execute(context);
		setItemsVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("itemsVendorsId", itemsVendorsId);
		return SUCCESS;
	}
	
	public String deleteItemVendors() throws Exception {
		FacilioContext context = new FacilioContext();
	
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.PARENT_ID, itemId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, itemsVendorsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteItemVendorsChain();
		deleteInventoryChain.execute(context);
		setItemsVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("itemsVendorsId", itemsVendorsId);
		return SUCCESS;
	}
	
	public String itemVendorsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemId);
		Command getWorkorderPartsList = ReadOnlyChainFactory.getItemVendorsList();
		getWorkorderPartsList.execute(context);
		itemVendors = ((List<ItemVendorsContext>) context.get(FacilioConstants.ContextNames.ITEM_VENDORS));
		setResult(FacilioConstants.ContextNames.ITEM_VENDORS, itemVendors);
		return SUCCESS;
	}
}
