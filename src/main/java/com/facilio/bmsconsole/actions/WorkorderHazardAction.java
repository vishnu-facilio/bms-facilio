package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkorderHazardContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class WorkorderHazardAction extends FacilioAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private Boolean fetchCount;
	public Boolean getFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(Boolean fetchCount) {
		this.fetchCount = fetchCount;
	}
	
	private WorkorderHazardContext workorderHazard;
	private List<WorkorderHazardContext> workorderHazardList;
	private List<Long> workOrderHazardIds;
	
	public WorkorderHazardContext getWorkorderHazard() {
		return workorderHazard;
	}
	public void setWorkorderHazard(WorkorderHazardContext workorderHazard) {
		this.workorderHazard = workorderHazard;
	}
	public List<WorkorderHazardContext> getWorkorderHazardList() {
		return workorderHazardList;
	}
	public void setWorkorderHazardList(List<WorkorderHazardContext> workorderHazardList) {
		this.workorderHazardList = workorderHazardList;
	}
	public List<Long> getWorkOrderHazardIds() {
		return workOrderHazardIds;
	}
	public void setWorkOrderHazardIds(List<Long> workOrderHazardIds) {
		this.workOrderHazardIds = workOrderHazardIds;
	}

	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String addWorkorderHazardList() throws Exception {
		FacilioChain c = TransactionChainFactory.addWorkorderHazardListChain();
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, workorderHazardList);
		c.execute();
		setResult(FacilioConstants.ContextNames.WORKORDER_HAZARDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	
	}
	
	public String deleteWorkOrderHazardList() throws Exception {
		
		if(!CollectionUtils.isEmpty(workOrderHazardIds)) {
			FacilioChain c = FacilioChainFactory.deleteWorkorderHazardListChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, workOrderHazardIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	
	}
	
	public String workorderHazardList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getWorkorderHazardListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "workorderHazard");
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Workorder_Hazards.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "Workorder_Hazards.hazard");
 			searchObj.put("query", getSearch());
 			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		
 		
 		if(!getFetchCount()) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			chain.getContext().put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}

 	 	chain.execute();
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<WorkorderHazardContext> workorderHazardList = (List<WorkorderHazardContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.WORKORDER_HAZARDS, workorderHazardList);
		}
		
		return SUCCESS;
	}
	
	
}
