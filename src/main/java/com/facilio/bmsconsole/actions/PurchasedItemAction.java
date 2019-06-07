package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurchasedItemAction extends FacilioAction{
	private static final long serialVersionUID = 1L;

	private PurchasedItemContext purchasedItem;
	public PurchasedItemContext getPurchasedItem() {
		return purchasedItem;
	}
	public void setPurchasedItem(PurchasedItemContext inventoryCost) {
		this.purchasedItem = inventoryCost;
	}
	
	private List<PurchasedItemContext> purchasedItems;
	public List<PurchasedItemContext> getPurchasedItems() {
		return purchasedItems;
	}
	public void setPurchasedItems(List<PurchasedItemContext> purchasedItems) {
		this.purchasedItems = purchasedItems;
	}
	
	private long purchasedItemId;
	public long getPurchasedItemId() {
		return purchasedItemId;
	}
	public void setPurchasedItemId(long inventoryCostId) {
		this.purchasedItemId = inventoryCostId;
	}
	
	private long itemId;
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long inventoryId) {
		this.itemId = inventoryId;
	}
	
	private List<Long> itemIds;
	public List<Long> getItemIds() {
		return itemIds;
	}
	public void setItemIds(List<Long> itemIds) {
		this.itemIds = itemIds;
	}
	public String addPurchasedItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, purchasedItems);
		context.put(FacilioConstants.ContextNames.ITEM_ID, itemId);
		context.put(FacilioConstants.ContextNames.RECORD_ID, itemId);
		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
//		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, Collections.singletonList(purchasedItems));
		Chain addItem = TransactionChainFactory.getAddPurchasedItemChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItem);
		return SUCCESS;
	}
	
	public String updatePurchasedItem() throws Exception {
		FacilioContext context = new FacilioContext();
		double quantity = purchasedItem.getQuantity();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, purchasedItem);
		context.put(FacilioConstants.ContextNames.ID, purchasedItem.getId());
		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItem);
		context.put(FacilioConstants.ContextNames.ITEM_ID, itemId);
		context.put(FacilioConstants.ContextNames.ITEM_IDS, Collections.singletonList(itemId));
		context.put(FacilioConstants.ContextNames.RECORD_ID, purchasedItem.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(purchasedItem.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		Chain updateInventryChain = TransactionChainFactory.getUpdateInventoryCostChain();
		updateInventryChain.execute(context);
		setPurchasedItemId(purchasedItem.getId());
		purchasedItemDetails();
		setResult(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItem);
		return SUCCESS;
	}
	
	public String purchasedItemDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getPurchasedItemId());

		Chain inventryDetailsChain = ReadOnlyChainFactory.fetchPurchasesItemDetails();
		inventryDetailsChain.execute(context);

		setPurchasedItem((PurchasedItemContext) context.get(FacilioConstants.ContextNames.PURCHASED_ITEM));
		setResult(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItem);
		return SUCCESS;
	}
	
	public String deletePurchasedItem() throws Exception {
		FacilioContext context = new FacilioContext();
		PurchasedItemContext purchaseditem = new PurchasedItemContext();
		purchaseditem.setDeleted(true);

		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, purchaseditem);
		context.put(FacilioConstants.ContextNames.ITEM_ID, itemId);
		context.put(FacilioConstants.ContextNames.ITEM_IDS, Collections.singletonList(itemId));
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, purchasedItemsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteInventoryCostChain();
		deleteInventoryChain.execute(context);
		setPurchasedItemsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		setResult("inventoryCostsId", purchasedItemsId);
		return SUCCESS;
	}
	
	public String purchasedItemsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, itemId);
		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM_IS_USED, true);
		Chain purchasedItemsListChain = ReadOnlyChainFactory.getPurchasdItemsList();
		purchasedItemsListChain.execute(context);
		purchasedItems = ((List<PurchasedItemContext>) context.get(FacilioConstants.ContextNames.PURCHASED_ITEM));
		setResult(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
		return SUCCESS;
	}

	public String unUsedPurchasedItemsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, itemIds);
		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM_IS_USED, false);
		Chain purchasedItemsListChain = ReadOnlyChainFactory.getUnusedPurchasdItemsList();
		purchasedItemsListChain.execute(context);
		purchasedItems = ((List<PurchasedItemContext>) context.get(FacilioConstants.ContextNames.PURCHASED_ITEM));
		setResult(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
		return SUCCESS;
	}
	
	public String purchasedItemsViewsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Purchased_Item.LOCAL_ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
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

		Chain itemsListChain = ReadOnlyChainFactory.getPurchasedItemsViewsList();
		itemsListChain.execute(context);
		if (getCount()) {
			setPurchasedItemCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", purchasedItemCount);
		} else {
			purchasedItemList = (List<PurchasedItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (purchasedItemList == null) {
				purchasedItemList = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItemList);
		}
		return SUCCESS;
	}
	
	public String purchasedItemCount() throws Exception {
		purchasedItemsViewsList();
		setResult(FacilioConstants.ContextNames.PURCHASED_ITEM_COUNT, purchasedItemCount);
		return SUCCESS;
	}
	
	private List<PurchasedItemContext> purchasedItemList;
	public List<PurchasedItemContext> getPurchasedItemList() {
		return purchasedItemList;
	}
	public void setPurchasedItemList(List<PurchasedItemContext> purchasedItemList) {
		this.purchasedItemList = purchasedItemList;
	}

	private Long purchasedItemCount;

	
	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}

	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	
	private List<Long> purchasedItemsId;
	public List<Long> getPurchasedItemsId() {
		return purchasedItemsId;
	}
	public void setPurchasedItemsId(List<Long> inventoryCostsId) {
		this.purchasedItemsId = inventoryCostsId;
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
	private Long purchasedItemsCount;
	public Long getPurchasedItemsCount() {
		return purchasedItemsCount;
	}
	public void setPurchasedItemsCount(Long toolsCount) {
		this.purchasedItemsCount = toolsCount;
	}
	public Long getPurchasedItemCount() {
		return purchasedItemCount;
	}
	public void setPurchasedItemCount(Long purchasedItemCount) {
		this.purchasedItemCount = purchasedItemCount;
	}
}
