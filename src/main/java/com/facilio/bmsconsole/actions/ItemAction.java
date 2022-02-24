package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ItemAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	
	private ItemContext item;
	public ItemContext getItem() {
		return item;
	}
	public void setItem(ItemContext inventry) {
		this.item = inventry;
	}
	
	private List<ItemContext> items;
	public List<ItemContext> getItems() {
		return items;
	}
	public void setItems(List<ItemContext> inventries) {
		this.items = inventries;
	}
	
	private long itemId;
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long inventryId) {
		this.itemId = inventryId;
	}
	
	private long storeRoom;
	public long getStoreRoom() {
		return storeRoom;
	}
	public void setStoreRoom(long storeRoomId) {
		this.storeRoom = storeRoomId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private Boolean includeServingSite  = false;
	
	public Boolean getIncludeServingSite() {
		return includeServingSite;
	}
	public void setIncludeServingSite(Boolean includeServingSite) {
		this.includeServingSite = includeServingSite;
	}
	
	public boolean isIncludeServingSite() {
		if (includeServingSite != null) {
			return includeServingSite.booleanValue();
		}
		return false;
	}

	public String searchQuery;

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	public String addItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, item);
		context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, item.getPurchasedItems());
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY);
		FacilioChain addInventry = TransactionChainFactory.getAddItemChain();
		addInventry.execute(context);
		setResult(FacilioConstants.ContextNames.ITEM, item);
		context.put(FacilioConstants.ContextNames.ITEM_ID, item.getId());
		context.put(FacilioConstants.ContextNames.ITEM_IDS, Collections.singletonList(item.getId()));
		return SUCCESS;
	}
	
	public String addBulkItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ITEMS, items);
		context.put(FacilioConstants.ContextNames.IS_BULK_ITEM_ADD, true);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
		FacilioChain addInventry = TransactionChainFactory.getAddBulkItemChain();
		addInventry.execute(context);
		items = (List<ItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.ITEMS, items);
		return SUCCESS;
	}
	
	public String updateItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, item);
		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, item.getPurchasedItems());
		context.put(FacilioConstants.ContextNames.ID, item.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID, item.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(item.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY);
		FacilioChain updateInventryChain = TransactionChainFactory.getUpdateItemChain();
		updateInventryChain.execute(context);
		setItemId(item.getId());
		itemDetails();
		setResult(FacilioConstants.ContextNames.ITEM, item);
		return SUCCESS;
	}
	
	public String itemDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getItemId());

		FacilioChain inventryDetailsChain = ReadOnlyChainFactory.fetchItemDetails();
		inventryDetailsChain.execute(context);

		setItem((ItemContext) context.get(FacilioConstants.ContextNames.ITEM));
		setResult(FacilioConstants.ContextNames.ITEM, item);
		return SUCCESS;
	}
	
	public String itemList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.WORK_ORDER_SITE_ID, siteId);
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Item.LOCAL_ID desc");
		context.put(FacilioConstants.ContextNames.INCLUDE_SERVING_SITE, includeServingSite);
		if(StringUtils.isNotEmpty(getSearchQuery())){
			context.put(FacilioConstants.ContextNames.SEARCH_QUERY,searchQuery);
		}
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "item.itemType");
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
		if(getShowForWorkorder()) {
			context.put(FacilioConstants.ContextNames.SHOW_ITEM_FOR_WORKORDER, showForWorkorder);
		}

		FacilioChain itemsListChain = ReadOnlyChainFactory.getItemList();
		itemsListChain.execute(context);
		if (getCount()) {
			setItemCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", itemCount);
		} else {
			items = (List<ItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (items == null) {
				items = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.ITEMS, items);
		}
		return SUCCESS;
	}
	
	public String woItemList() throws Exception {
		setIncludeServingSite(true);
		return itemList();
	}
	
	public String itemCount() throws Exception {
		itemList();
		setResult(FacilioConstants.ContextNames.ITEM_COUNT, itemCount);
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
	
	private Long itemCount;
	public Long getItemCount() {
		return itemCount;
	}
	public void setItemCount(Long inventryCount) {
		this.itemCount = inventryCount;
	}
	
	private Boolean showForWorkorder;
	public Boolean getShowForWorkorder() {
		if(showForWorkorder == null) {
			return false;
		}
		return showForWorkorder;
	}
	public void setShowForWorkorder(Boolean showForWorkorder) {
		this.showForWorkorder = showForWorkorder;
	}
}
