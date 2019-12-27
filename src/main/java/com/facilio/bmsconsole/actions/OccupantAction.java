package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.OccupantsContext;
import com.facilio.bmsconsole.util.OccupantsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class OccupantAction extends FacilioAction{

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

	private OccupantsContext occupant;
	private List<OccupantsContext> occupants;
	
	private List<Long> occupantIds;
	
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

	public OccupantsContext getOccupant() {
		return occupant;
	}
	public void setOccupant(OccupantsContext occupant) {
		this.occupant = occupant;
	}
	public List<OccupantsContext> getOccupants() {
		return occupants;
	}
	public void setOccupants(List<OccupantsContext> occupants) {
		this.occupants = occupants;
	}
	
	public List<Long> getOccupantIds() {
		return occupantIds;
	}
	public void setOccupantIds(List<Long> occupantIds) {
		this.occupantIds = occupantIds;
	}
	public String addOccupants() throws Exception {
		
		if(!CollectionUtils.isEmpty(occupants)) {
			FacilioChain c = TransactionChainFactory.addOccupantsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, occupants);
			c.execute();
			setResult(FacilioConstants.ContextNames.OCCUPANTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateOccupants() throws Exception {
		
		if(!CollectionUtils.isEmpty(occupants)) {
			FacilioChain c = TransactionChainFactory.updateOccupantsChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, occupants);
			c.execute();
			setResult(FacilioConstants.ContextNames.OCCUPANTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteOccupants() throws Exception {
		
		if(!CollectionUtils.isEmpty(occupantIds)) {
			FacilioChain c = FacilioChainFactory.deleteOccupantsChain();
			
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, occupantIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getOccupantsList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getOccupantsListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "occupant");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Occupants.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "occupant.name");
 			searchObj.put("query", getSearch());
 			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
 	 	chain.execute();
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<OccupantsContext> occupants = (List<OccupantsContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.OCCUPANTS, occupants);
		}
		
		return SUCCESS;
	}
	
	public String getOccupantDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getOccupantDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		OccupantsContext occupant = (OccupantsContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.OCCUPANT, occupant);
		
		return SUCCESS;
	}
	
	public String updatePortalAccess() throws Exception {
		OccupantsAPI.updatePortalUserAccess(occupant, true);
		return SUCCESS;
	}
	
}
