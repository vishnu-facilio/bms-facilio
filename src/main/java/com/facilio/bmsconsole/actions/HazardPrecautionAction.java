package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.HazardPrecautionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class HazardPrecautionAction extends FacilioAction{

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
	
	private HazardPrecautionContext hazardPrecaution;
	private List<HazardPrecautionContext> hazardPrecautionList;
	private List<Long> hazardPrecautionIds;
	
	public HazardPrecautionContext getHazardPrecaution() {
		return hazardPrecaution;
	}
	public void setHazardPrecaution(HazardPrecautionContext hazardPrecaution) {
		this.hazardPrecaution = hazardPrecaution;
	}
	public List<HazardPrecautionContext> getHazardPrecautionList() {
		return hazardPrecautionList;
	}
	public void setHazardPrecautionList(List<HazardPrecautionContext> hazardPrecautionList) {
		this.hazardPrecautionList = hazardPrecautionList;
	}
	public List<Long> getHazardPrecautionIds() {
		return hazardPrecautionIds;
	}
	public void setHazardPrecautionIds(List<Long> hazardPrecautionIds) {
		this.hazardPrecautionIds = hazardPrecautionIds;
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
	
	public String addHazardPrecautionList() throws Exception {
		FacilioChain c = TransactionChainFactory.addHazardPrecautionListChain();
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, hazardPrecautionList);
		c.execute();
		setResult(FacilioConstants.ContextNames.HAZARD_PRECAUTION_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	
	}
	
	public String deleteHazardPrecautionList() throws Exception {
		
		if(!CollectionUtils.isEmpty(hazardPrecautionIds)) {
			FacilioChain c = FacilioChainFactory.deleteHazardPrecautionListChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, hazardPrecautionIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	
	}
	
	public String hazardPrecautionList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getHazardPrecautionListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "hazardPrecaution");
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Hazard_Precautions.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "Hazard_Precautions.hazard");
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
			List<HazardPrecautionContext> hazardPrecautionList = (List<HazardPrecautionContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.HAZARD_PRECAUTION_LIST, hazardPrecautionList);
		}
		
		return SUCCESS;
	}
	
	

}
