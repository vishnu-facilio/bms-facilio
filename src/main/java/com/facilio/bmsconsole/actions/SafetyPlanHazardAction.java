package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SafetyPlanHazardContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class SafetyPlanHazardAction extends FacilioAction{

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
	
	private SafetyPlanHazardContext safetyPlanHazard;
	private List<SafetyPlanHazardContext> safetyPlanHazardList;
	private List<Long> safetyPlanHazardIds;
	
	public SafetyPlanHazardContext getSafetyPlanHazard() {
		return safetyPlanHazard;
	}
	public void setSafetyPlanHazard(SafetyPlanHazardContext safetyPlanHazard) {
		this.safetyPlanHazard = safetyPlanHazard;
	}
	public List<SafetyPlanHazardContext> getSafetyPlanHazardList() {
		return safetyPlanHazardList;
	}
	public void setSafetyPlanHazardList(List<SafetyPlanHazardContext> safetyPlanHazardList) {
		this.safetyPlanHazardList = safetyPlanHazardList;
	}
	public List<Long> getSafetyPlanHazardIds() {
		return safetyPlanHazardIds;
	}
	public void setSafetyPlanHazardIds(List<Long> safetyPlanHazardIds) {
		this.safetyPlanHazardIds = safetyPlanHazardIds;
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
	
	public String addSafetyPlanHazardList() throws Exception {
		FacilioChain c = TransactionChainFactory.addSafetyPlanHazardChain();
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, safetyPlanHazardList);
		c.execute();
		setResult(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	
	}
	
	public String deleteSafetyPlanHazardList() throws Exception {
		
		if(!CollectionUtils.isEmpty(safetyPlanHazardIds)) {
			FacilioChain c = FacilioChainFactory.deleteSafetyPlanHazardChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, safetyPlanHazardIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	
	}
	
	public String safetyPlanHazardList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getSafetyPlanHazardListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "safetyPlanHazard");
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "SafetyPlan_Hazard.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "SafetyPlan_Hazard.hazard");
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
			List<SafetyPlanHazardContext> safetyPlanHazardList = (List<SafetyPlanHazardContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD_LIST, safetyPlanHazardList);
		}
		
		return SUCCESS;
	}
	
	

}
