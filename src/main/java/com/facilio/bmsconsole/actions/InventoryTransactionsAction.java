package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InventoryTransactionsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class InventoryTransactionsAction extends FacilioAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<InventoryTransactionsContext> inventoryActions;
	public List<InventoryTransactionsContext> getInventoryActions() {
		return inventoryActions;
	}
	public void setInventoryActions(List<InventoryTransactionsContext> inventoryActions) {
		this.inventoryActions = inventoryActions;
	}
	
	private List<Long> inventoryActionsId;
	public void setInventoryActionsId(List<Long> inventoryActionsId) {
		this.inventoryActionsId = inventoryActionsId;
	}
	public List<Long> getInventoryActionsId() {
		return inventoryActionsId;
	}
	
	public String addInventoryTransactions() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, inventoryActions);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddInventoryTransactionsChain();
		addWorkorderPartChain.execute(context);
		setInventoryActionsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("inventoryActionsId", inventoryActionsId);
		return SUCCESS;
	} 
	
	public String updateInventoryTransactions() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, inventoryActions);
		Chain addWorkorderPartChain = TransactionChainFactory.getUpdateInventoryTransactionsChain();
		addWorkorderPartChain.execute(context);
		setInventoryActionsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("inventoryActionsId", inventoryActionsId);
		return SUCCESS;
	} 
	
	public String itemsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Inventory_transactions.ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "inventoryTransactions.inventory");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}
		if (getCount()) { // only count
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
		} else {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}

		Chain itemsListChain = ReadOnlyChainFactory.getItemsList();
		itemsListChain.execute(context);
		if (getCount()) {
			setItemsCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", itemsCount);
		} else {
			inventoryActions = (List<InventoryTransactionsContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (inventoryActions == null) {
				inventoryActions = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.INVENTORY_TRANSACTIONS, inventoryActions);
		}
		return SUCCESS;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	private Boolean count;

	public Boolean getCount() {
		if (count == null) {
			return false;
		}
		return count;
	}

	public void setCount(Boolean count) {
		this.count = count;
	}
	
	private Long itemsCount;
	public Long getItemsCount() {
		return itemsCount;
	}
	public void setItemsCount(Long itemsCount) {
		this.itemsCount = itemsCount;
	}
}
