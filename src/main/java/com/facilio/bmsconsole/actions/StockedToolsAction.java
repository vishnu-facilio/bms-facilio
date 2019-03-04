package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.StockedToolsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class StockedToolsAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	
	private StockedToolsContext stockedTools;
	public StockedToolsContext getStockedTools() {
		return stockedTools;
	}
	public void setStockedTools(StockedToolsContext stockedTools) {
		this.stockedTools = stockedTools;
	}
	
	private List<StockedToolsContext> stockedToolsList;
	public List<StockedToolsContext> getStockedToolsList() {
		return stockedToolsList;
	}
	public void setStockedToolsList(List<StockedToolsContext> stockedToolsList) {
		this.stockedToolsList = stockedToolsList;
	}
	
	private long stockedToolId;
	public long getStockedToolId() {
		return stockedToolId;
	}
	public void setStockedToolId(long stockedToolId) {
		this.stockedToolId = stockedToolId;
	}
	
	private List<Long> stockedToolsIds;
	public List<Long> getStockedToolsIds() {
		return stockedToolsIds;
	}
	public void setStockedToolsIds(List<Long> stockedToolsIds) {
		this.stockedToolsIds = stockedToolsIds;
	}
	
	public String addStockedTools() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, stockedTools);
		Chain addStockedTool = TransactionChainFactory.getAddStockedToolsChain();
		addStockedTool.execute(context);
		setResult(FacilioConstants.ContextNames.STOCKED_TOOLS, stockedTools);
		context.put(FacilioConstants.ContextNames.STOCKED_TOOLS_ID, stockedTools.getId());
		context.put(FacilioConstants.ContextNames.STOCKED_TOOLS_IDS, Collections.singletonList(stockedTools.getId()));
		return SUCCESS;
	}
	
	public String updateStockedTools() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, stockedTools);
		context.put(FacilioConstants.ContextNames.ID, stockedTools.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID, stockedTools.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(stockedTools.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		Chain updateInventryChain = TransactionChainFactory.getUpdateStockedToolsChain();
		updateInventryChain.execute(context);
		setStockedToolId(stockedTools.getId());
		stockedToolsDetails();
		setResult(FacilioConstants.ContextNames.STOCKED_TOOLS, stockedTools);
		return SUCCESS;
	}
	
	public String stockedToolsDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getStockedToolId());

		Chain inventryDetailsChain = ReadOnlyChainFactory.fetchStockedToolsDetails();
		inventryDetailsChain.execute(context);

		setStockedTools((StockedToolsContext) context.get(FacilioConstants.ContextNames.STOCKED_TOOLS));
		setResult(FacilioConstants.ContextNames.STOCKED_TOOLS, stockedTools);
		return SUCCESS;
	}
	
	public String stockedToolsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Stocked_tools.ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "stockedTools.name");
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

		Chain itemsListChain = ReadOnlyChainFactory.getStockedToolsList();
		itemsListChain.execute(context);
		if (getCount()) {
			setStockedToolsCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", stockedToolsCount);
		} else {
			stockedToolsList = (List<StockedToolsContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (stockedToolsList == null) {
				stockedToolsList = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.STOCKED_TOOLS, stockedToolsList);
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
	
	private Long stockedToolsCount;
	public Long getStockedToolsCount() {
		return stockedToolsCount;
	}
	public void setStockedToolsCount(Long stockedToolsCount) {
		this.stockedToolsCount = stockedToolsCount;
	}
}
