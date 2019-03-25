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
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ToolAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private ToolContext tool;

	public ToolContext getTool() {
		return tool;
	}

	public void setTool(ToolContext tool) {
		this.tool = tool;
	}

	private List<ToolContext> tools;

	public List<ToolContext> getTools() {
		return tools;
	}

	public void setTools(List<ToolContext> stockedToolsList) {
		this.tools = stockedToolsList;
	}

	private long toolId;

	public long getToolId() {
		return toolId;
	}

	public void setToolId(long stockedToolId) {
		this.toolId = stockedToolId;
	}

	private long storeRoom;

	public long getStoreRoom() {
		return storeRoom;
	}

	public void setStoreRoom(long storeRoomId) {
		this.storeRoom = storeRoomId;
	}

	public String addTool() throws Exception {
		FacilioContext context = new FacilioContext();
		tool.setLastPurchasedDate(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, tool);
		context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, tool.getPurchasedTools());
		context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, tool.getToolType().getId());
		Chain addStockedTool = TransactionChainFactory.getAddToolChain();
		addStockedTool.execute(context);
		setResult(FacilioConstants.ContextNames.TOOL, tool);
		context.put(FacilioConstants.ContextNames.TOOL_ID, tool.getId());
		context.put(FacilioConstants.ContextNames.TOOL_IDS, Collections.singletonList(tool.getId()));
		return SUCCESS;
	}

	public String addBulkTool() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TOOLS, tools);
		context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		Chain addStockedTool = TransactionChainFactory.getBulkAddToolChain();
		addStockedTool.execute(context);
		setResult(FacilioConstants.ContextNames.TOOL, tools);
		return SUCCESS;
	}

	public String updateTool() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, tool);
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, tool.getPurchasedTools());
		context.put(FacilioConstants.ContextNames.ID, tool.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID, tool.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(tool.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		Chain updateInventryChain = TransactionChainFactory.getUpdateToolChain();
		updateInventryChain.execute(context);
		setToolId(tool.getId());
		toolDetails();
		setResult(FacilioConstants.ContextNames.TOOL, tool);
		return SUCCESS;
	}

	public String toolDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getToolId());

		Chain inventryDetailsChain = ReadOnlyChainFactory.fetchStockedToolsDetails();
		inventryDetailsChain.execute(context);

		setTool((ToolContext) context.get(FacilioConstants.ContextNames.TOOL));
		setResult(FacilioConstants.ContextNames.TOOL, tool);
		return SUCCESS;
	}

	public String toolList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Tool.LOCAL_ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "tool.name");
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
			tools = (List<ToolContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (tools == null) {
				tools = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.TOOL, tools);
		}
		return SUCCESS;
	}

	public String toolCount() throws Exception {
		toolList();
		setResult(FacilioConstants.ContextNames.TOOL_COUNT, stockedToolsCount);
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
