package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InventoryCostContext;
import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.context.ItemsContext;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class InventryAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	
	private InventryContext inventry;
	public InventryContext getInventry() {
		return inventry;
	}
	public void setInventry(InventryContext inventry) {
		this.inventry = inventry;
	}
	
	private List<InventryContext> inventries;
	public List<InventryContext> getInventries() {
		return inventries;
	}
	public void setInventries(List<InventryContext> inventries) {
		this.inventries = inventries;
	}
	
	private long inventryId;
	public long getInventryId() {
		return inventryId;
	}
	public void setInventryId(long inventryId) {
		this.inventryId = inventryId;
	}
	
	public String addInventry() throws Exception {
		FacilioContext context = new FacilioContext();
		inventry.setTtime(System.currentTimeMillis());
		inventry.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, inventry);
		context.put(FacilioConstants.ContextNames.INVENTORY_COST, inventry.getInventoryCost());
		Chain addInventry = TransactionChainFactory.getAddInventryChain();
		addInventry.execute(context);
		setResult(FacilioConstants.ContextNames.INVENTRY, inventry);
		context.put(FacilioConstants.ContextNames.INVENTORY_ID, inventry.getId());
		context.put(FacilioConstants.ContextNames.INVENTORY_IDS, Collections.singletonList(inventry.getId()));
		return SUCCESS;
	}
	
	public String updateInventry() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, inventry);
		context.put(FacilioConstants.ContextNames.INVENTORY_COST, inventry.getInventoryCost());
		context.put(FacilioConstants.ContextNames.ID, inventry.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID, inventry.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(inventry.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		Chain updateInventryChain = TransactionChainFactory.getUpdateInventryChain();
		updateInventryChain.execute(context);
		setInventryId(inventry.getId());
		inventryDetails();
		setResult(FacilioConstants.ContextNames.INVENTRY, inventry);
		return SUCCESS;
	}
	
	public String inventryDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getInventryId());

		Chain inventryDetailsChain = ReadOnlyChainFactory.fetchInventryDetails();
		inventryDetailsChain.execute(context);

		setInventry((InventryContext) context.get(FacilioConstants.ContextNames.INVENTRY));
		setResult(FacilioConstants.ContextNames.INVENTRY, inventry);
		return SUCCESS;
	}
	
	public String inventryList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Inventry.ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "inventry.name");
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

		Chain itemsListChain = ReadOnlyChainFactory.getInvenrtyList();
		itemsListChain.execute(context);
		if (getCount()) {
			setInventryCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", inventryCount);
		} else {
			inventries = (List<InventryContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (inventries == null) {
				inventries = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.INVENTRIES, inventries);
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
	
	private Long inventryCount;
	public Long getInventryCount() {
		return inventryCount;
	}
	public void setInventryCount(Long inventryCount) {
		this.inventryCount = inventryCount;
	}

	private InventoryCostContext inventoryCost;
	public InventoryCostContext getInventoryCost() {
		return inventoryCost;
	}
	public void setInventoryCost(InventoryCostContext inventoryCost) {
		this.inventoryCost = inventoryCost;
	}
}
