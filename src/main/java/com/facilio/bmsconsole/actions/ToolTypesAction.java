package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ToolTypesAction extends FacilioAction{
	
	private static final long serialVersionUID = 1L;
	
	public String addToolTypes() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, toolTypes);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		FacilioChain addToolsChain = TransactionChainFactory.getAddToolTypesChain();
		addToolsChain.execute(context);
		setResult(FacilioConstants.ContextNames.TOOL_TYPES, toolTypes);
		return SUCCESS;
	}
	
	public String updateToolTypes() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, toolTypes);
		context.put(FacilioConstants.ContextNames.ID, toolTypes.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(toolTypes.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		FacilioChain updateToolChain = TransactionChainFactory.getUpdateToolTypesChain();
		updateToolChain.execute(context);
		setToolId(toolTypes.getId());
		toolTypesDetails();
		setResult(FacilioConstants.ContextNames.TOOL_TYPES, toolTypes);
		return SUCCESS;
	}
	
	public String toolTypesDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getToolId());

		FacilioChain toolDetailsChain = ReadOnlyChainFactory.fetchToolDetails();
		toolDetailsChain.execute(context);

		setToolTypes((ToolTypesContext) context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.TOOL_TYPES, toolTypes);
		return SUCCESS;
	}
	
	public String toolTypesList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Tool_types.LOCAL_ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "toolTypes.name");
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

		FacilioChain itemsListChain = ReadOnlyChainFactory.gettoolsList();
		itemsListChain.execute(context);
		if (getCount()) {
			setToolsCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", toolsCount);
		} else {
			toolTypesList = (List<ToolTypesContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (toolTypesList == null) {
				toolTypesList = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.TOOL_TYPES, toolTypesList);
		}
		return SUCCESS;
	}
	
	public String toolTypesCount() throws Exception {
		toolTypesList();
		setResult(FacilioConstants.ContextNames.TOOL_TYPES_COUNT, toolsCount);
		return SUCCESS;
	}
	
	public String BulkAddToolTypes() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTypesList);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		FacilioChain addToolsChain = TransactionChainFactory.getBulkAddToolTypesChain();
		addToolsChain.execute(context);
		setResult(FacilioConstants.ContextNames.TOOL_TYPES, toolTypesList);
		return SUCCESS;
	}
	
	private ToolTypesContext toolTypes;
	public ToolTypesContext getToolTypes() {
		return toolTypes;
	}
	
	public void setToolTypes(ToolTypesContext tool) {
		this.toolTypes = tool;
	}
	
	private List<ToolTypesContext> toolTypesList;
	public List<ToolTypesContext> getToolTypesList() {
		return toolTypesList;
	}
	
	public void setToolTypesList(List<ToolTypesContext> tool) {
		this.toolTypesList = tool;
	}
	
	private long toolId;
	public long getToolId() {
		return toolId;
	}
	public void setToolId(long toolId) {
		this.toolId = toolId;
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
	
	private Long toolsCount;
	public Long getToolsCount() {
		return toolsCount;
	}
	public void setToolsCount(Long toolsCount) {
		this.toolsCount = toolsCount;
	}
}
