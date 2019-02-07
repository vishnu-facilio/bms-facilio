package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class InventoryAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	public String newInventory() throws Exception {

		FacilioContext context = new FacilioContext();
		Chain newInventory = FacilioChainFactory.getNewInventoryChain();
		newInventory.execute(context);

		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

		return SUCCESS;
	}

	private List<FacilioField> fields;

	public String addInventory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, inventory);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		Chain addInventoryChain = TransactionChainFactory.getAddInventoryChain();
		addInventoryChain.execute(context);
		setInventoryId(inventory.getId());
		setResult(FacilioConstants.ContextNames.INVENTORY, inventory);
		return SUCCESS;
	}

	public String updateInventory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, inventory);
		context.put(FacilioConstants.ContextNames.ID, inventory.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(inventory.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		Chain updateInventoryChain = TransactionChainFactory.getUpdateInventoryChain();
		updateInventoryChain.execute(context);
		setInventoryId(inventory.getId());
		inventoryDetails();
		setResult(FacilioConstants.ContextNames.INVENTORY, inventory);
		return SUCCESS;
	}

	public String deleteInventory() throws Exception {
		FacilioContext context = new FacilioContext();
		InventoryContext inventory = new InventoryContext();
		inventory.setDeleted(true);

		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, inventory);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, inventoriesId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteInventoryChain();
		deleteInventoryChain.execute(context);
		setInventoriesId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		setResult("inventoriesId", inventoriesId);
		return SUCCESS;
	}

	public String inventoryDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getInventoryId());

		Chain inventoryDetailsChain = ReadOnlyChainFactory.fetchInventoryDetails();
		inventoryDetailsChain.execute(context);

		setInventory((InventoryContext) context.get(FacilioConstants.ContextNames.INVENTORY));
		setResult(FacilioConstants.ContextNames.INVENTORY, inventory);
		return SUCCESS;
	}

	public String inventoryList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Inventory.LOCAL_ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "inventory.name,inventory.serialNumber");
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

		Chain inventoryList = ReadOnlyChainFactory.getInventoryListChain();
		inventoryList.execute(context);
		if (getCount()) {
			setInventoryCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", inventoryCount);
		} else {
			inventories = (List<InventoryContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (inventories == null) {
				inventories = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.INVENTORY_LIST, inventories);
		}
		return SUCCESS;
	}

	private InventoryContext inventory;

	public InventoryContext getInventory() {
		return inventory;
	}

	public void setInventory(InventoryContext inventory) {
		this.inventory = inventory;
	}

	private long inventoryId;

	public long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(long inventoryId) {
		this.inventoryId = inventoryId;
	}

	private int rowsUpdated;

	public int getRowsUpdated() {
		return rowsUpdated;
	}

	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}

	private List<Long> inventoriesId;

	public List<Long> getInventoriesId() {
		return inventoriesId;
	}

	public void setInventoriesId(List<Long> inventoriesId) {
		this.inventoriesId = inventoriesId;
	}

	private String viewName;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	private String filters;

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	private String search;

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSearch() {
		return this.search;
	}

	private int page;

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return this.page;
	}

	private int perPage = -1;

	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public int getPerPage() {
		return this.perPage;
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

	private List<InventoryContext> inventories;

	public List<InventoryContext> getInventories() {
		return inventories;
	}

	public void setInventories(List<InventoryContext> inventories) {
		this.inventories = inventories;
	}
	private Long inventoryCount;

	public Long getInventoryCount() {
		if (inventoryCount == null) {
			inventoryCount = 0L;
		}
		return inventoryCount;
	}

	public void setInventoryCount(Long inventoryCount) {
		this.inventoryCount = inventoryCount;
	}
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	public String inventoryCount () throws Exception {
		return inventoryList();
	}
}
