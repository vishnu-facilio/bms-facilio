package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ItemTypesAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	private ItemTypesContext itemTypes;

	public ItemTypesContext getItemTypes() {
		return itemTypes;
	}

	public void setItemTypes(ItemTypesContext itemTypes) {
		this.itemTypes = itemTypes;
	}

	private List<ItemTypesContext> itemTypesList;

	public List<ItemTypesContext> getItemTypesList() {
		return itemTypesList;
	}

	public void setItemTypesList(List<ItemTypesContext> itemTypesList) {
		this.itemTypesList = itemTypesList;
	}

	private long itemTypesId;

	public long getItemTypesId() {
		return itemTypesId;
	}

	public void setItemTypesId(long itemTypesId) {
		this.itemTypesId = itemTypesId;
	}

	public String addItemTypes() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, itemTypes);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		FacilioChain addItem = TransactionChainFactory.getAddItemTypesChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.ITEM_TYPES, itemTypes);
		return SUCCESS;
	}

	public String updateItemTypes() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, itemTypes);
		context.put(FacilioConstants.ContextNames.ID, itemTypes.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(itemTypes.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY);

		FacilioChain updateItemChain = TransactionChainFactory.getUpdateItemTypesChain();
		updateItemChain.execute(context);
		setItemTypesId(itemTypes.getId());
		itemTypesDetails();
		setResult(FacilioConstants.ContextNames.ITEM_TYPES, itemTypes);
		return SUCCESS;
	}

	public String itemTypesDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getItemTypesId());

		FacilioChain itemDetailsChain = ReadOnlyChainFactory.fetchItemTypesDetails();
		itemDetailsChain.execute(context);

		setItemTypes((ItemTypesContext) context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.ITEM_TYPES, itemTypes);
		return SUCCESS;
	}

	public String itemTypesList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Item_Types.LOCAL_ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "itemTypes.name");
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

		FacilioChain itemsListChain = ReadOnlyChainFactory.getItemTypessList();
		itemsListChain.execute(context);
		if (getCount()) {
			setItemTypesCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", itemTypesCount);
		} else {
			itemTypesList = (List<ItemTypesContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (itemTypesList == null) {
				itemTypesList = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.ITEM_TYPES, itemTypesList);
		}
		return SUCCESS;
	}

	public String itemTypesCount() throws Exception {
		itemTypesList();
		setResult(FacilioConstants.ContextNames.ITEM_TYPES_COUNT, itemTypesCount);
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

	private Long itemTypesCount;

	public Long getItemTypesCount() {
		return itemTypesCount;
	}

	public void setItemTypesCount(Long itemTypesCount) {
		this.itemTypesCount = itemTypesCount;
	}

}
