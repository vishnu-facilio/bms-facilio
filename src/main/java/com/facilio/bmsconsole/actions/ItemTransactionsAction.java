package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ItemTransactionsAction extends FacilioAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ItemTransactionsContext> itemTransaction;
	public List<ItemTransactionsContext> getItemTransaction() {
		return itemTransaction;
	}
	public void setItemTransaction(List<ItemTransactionsContext> inventoryActions) {
		this.itemTransaction = inventoryActions;
	}
	
	private List<Long> itemTransactionsId;
	public void setItemTransactionsId(List<Long> inventoryActionsId) {
		this.itemTransactionsId = inventoryActionsId;
	}
	public List<Long> getItemTransactionsId() {
		return itemTransactionsId;
	}
	
	public String addOrUpdateItemTransactions() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransaction);
		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddOrUpdateItemTransactionsChain();
		addWorkorderPartChain.execute(context);
		setItemTransactionsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("itemTransactionsId", itemTransactionsId);
		return SUCCESS;
	} 
	
	public String deleteItemTransactions() throws Exception {
		FacilioContext context = new FacilioContext();
	
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, itemTransactionsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteItemTransactionsChain();
		deleteInventoryChain.execute(context);
		setItemTransactionsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("itemTransactionsId", itemTransactionsId);
		return SUCCESS;
	}
	
	
	public String updateItemTransactions() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransaction);
		Chain addWorkorderPartChain = TransactionChainFactory.getUpdateInventoryTransactionsChain();
		addWorkorderPartChain.execute(context);
		setItemTransactionsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("inventoryActionsId", itemTransactionsId);
		return SUCCESS;
	} 
	
	public String itemsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Item_Transactions.ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "itemTransactions.item");
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
		if(getShowItemsForReturn()) {
			context.put(FacilioConstants.ContextNames.SHOW_ITEMS_FOR_RETURN, showItemsForReturn);
		}
		Chain itemsListChain = ReadOnlyChainFactory.getItemTransactionsList();
		itemsListChain.execute(context);
		if (getCount()) {
			setItemsCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", itemsCount);
		} else {
			itemTransaction = (List<ItemTransactionsContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (itemTransaction == null) {
				itemTransaction = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.ITEM_TRANSACTIONS, itemTransaction);
		}
		return SUCCESS;
	}
	public String showItemTransactionListForReturn() throws Exception {		
		itemsList();
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
	
	private List<Long> purchasedItems;
	public List<Long> getPurchasedItems() {
		return purchasedItems;
	}
	public void setPurchasedItems(List<Long> purchasedItems) {
		this.purchasedItems = purchasedItems;
	}
	
	private Boolean showItemsForReturn;
	public Boolean getShowItemsForReturn() {
		if (showItemsForReturn == null) {
			return false;
		}
		return showItemsForReturn;
	}
	public void setShowItemsForReturn(Boolean showToolsForReturn) {
		this.showItemsForReturn = showToolsForReturn;
	}
}
