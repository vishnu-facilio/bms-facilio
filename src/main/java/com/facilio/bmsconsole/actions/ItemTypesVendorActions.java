package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemTypesVendorsContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ItemTypesVendorActions extends FacilioAction{
	private static final long serialVersionUID = 1L;

	private List<ItemTypesVendorsContext> itemTypesVendors;
	public List<ItemTypesVendorsContext> getItemTypesVendors() {
		return itemTypesVendors;
	}
	public void setItemTypesVendors(List<ItemTypesVendorsContext> itemVendors) {
		this.itemTypesVendors = itemVendors;
	}
	
	private List<Long> itemsVendorsId;
	public List<Long> getItemsVendorsId() {
		return itemsVendorsId;
	}
	public void setItemsVendorsId(List<Long> itemsVendorsId) {
		this.itemsVendorsId = itemsVendorsId;
	}
	
	private long itemTypesId;
	public long getItemTypesId() {
		return itemTypesId;
	}
	public void setItemTypesId(long itemId) {
		this.itemTypesId = itemId;
	}
	
	public String addItemVendors() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTypesVendors);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddItemTypesVendorsChain();
		addWorkorderPartChain.execute(context);
		setItemsVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("itemsVendorsId", itemsVendorsId);
		return SUCCESS;
	}
	
	public String udpateItemVendors() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTypesVendors);
		Chain addWorkorderPartChain = TransactionChainFactory.getUpdateItemTypesVendorsChain();
		addWorkorderPartChain.execute(context);
		setItemsVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("itemsVendorsId", itemsVendorsId);
		return SUCCESS;
	}
	
	public String deleteItemVendors() throws Exception {
		FacilioContext context = new FacilioContext();
	
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.PARENT_ID, itemTypesId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, itemsVendorsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteItemTypesVendorsChain();
		deleteInventoryChain.execute(context);
		setItemsVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("itemsVendorsId", itemsVendorsId);
		return SUCCESS;
	}
	
	public String itemVendorsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
		Command getWorkorderPartsList = ReadOnlyChainFactory.getItemVendorsList();
		getWorkorderPartsList.execute(context);
		itemTypesVendors = ((List<ItemTypesVendorsContext>) context.get(FacilioConstants.ContextNames.ITEM_VENDORS));
		setResult(FacilioConstants.ContextNames.ITEM_VENDORS, itemTypesVendors);
		return SUCCESS;
	}
}
