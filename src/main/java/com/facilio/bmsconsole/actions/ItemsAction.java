package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ItemsAction extends FacilioAction{

	private static final long serialVersionUID = 1L;
	private ItemsContext item;
	public ItemsContext getItem() {
		return item;
	}
	public void setItem(ItemsContext item) {
		this.item = item;
	}
	private List<ItemsContext> items;
	public List<ItemsContext> getItems() {
		return items;
	}
	public void setItems(List<ItemsContext> items) {
		this.items = items;
	}
	
	private long itemId;
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	
	public String addItem() throws Exception {
		FacilioContext context = new FacilioContext();
		item.setTtime(System.currentTimeMillis());
		item.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, item);
		Chain addItem = TransactionChainFactory.getAddItemChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.ITEM, item);
		return SUCCESS;
	}
	
	public String updateItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, item);
		context.put(FacilioConstants.ContextNames.ID, item.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(item.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		Chain updateItemChain = TransactionChainFactory.getUpdateItemChain();
		updateItemChain.execute(context);
		setItemId(item.getId());
		itemDetails();
		setResult(FacilioConstants.ContextNames.ITEM, item);
		return SUCCESS;
	}
	
	public String itemDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getItemId());

		Chain itemDetailsChain = ReadOnlyChainFactory.fetchItemDetails();
		itemDetailsChain.execute(context);

		setItem((ItemsContext) context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.ITEM, item);
		return SUCCESS;
	}
	
	public String itemsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Items.ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "items.name");
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
			items = (List<ItemsContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (items == null) {
				items = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.ITEMS, items);
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
