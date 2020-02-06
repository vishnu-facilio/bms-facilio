package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.HazardContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class HazardAction extends FacilioAction{

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
	
	private HazardContext hazard;
	private List<HazardContext> hazards;
	private List<Long> hazardIds;
	
	public HazardContext getHazard() {
		return hazard;
	}
	public void setHazard(HazardContext hazard) {
		this.hazard = hazard;
	}
	public List<HazardContext> getHazards() {
		return hazards;
	}
	public void setHazards(List<HazardContext> hazards) {
		this.hazards = hazards;
	}
	public List<Long> getHazardIds() {
		return hazardIds;
	}
	public void setHazardIds(List<Long> hazardIds) {
		this.hazardIds = hazardIds;
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

	private String hazardString;
	
	public String getHazardString() {
		return hazardString;
	}
	public void setHazardString(String hazardString) {
		this.hazardString = hazardString;
	}

	private Map<String, Object> subFormFiles;
	public Map<String, Object> getSubFormFiles() {
		return subFormFiles;
	}
	public void setSubFormFiles(Map<String, Object> subFormFiles) {
		this.subFormFiles = subFormFiles;
	}
	
	public String addHazard() throws Exception {
		
		if (StringUtils.isNotEmpty(hazardString)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(hazardString);
			hazard = FieldUtil.getAsBeanFromJson(json, HazardContext.class);
			
			if (hazard != null && MapUtils.isNotEmpty(subFormFiles)) {
				hazard.addSubFormFiles(subFormFiles);
			}
		}
		
		hazard.parseFormData();
		
		FacilioChain c = TransactionChainFactory.addHazardChain();
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		c.getContext().put(FacilioConstants.ContextNames.RECORD, hazard);
		c.execute();
		setResult(FacilioConstants.ContextNames.HAZARD, c.getContext().get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String updateHazard() throws Exception {
		
		FacilioChain c = TransactionChainFactory.updateHazardChain();
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
		c.getContext().put(FacilioConstants.ContextNames.RECORD, hazard);
		if (StringUtils.isNotEmpty(hazardString)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(hazardString);
			hazard = FieldUtil.getAsBeanFromJson(json, HazardContext.class);
			
			if (hazard != null && MapUtils.isNotEmpty(subFormFiles)) {
				hazard.addSubFormFiles(subFormFiles);
			}
		}
		hazard.parseFormData();
		
		c.execute();
		setResult(FacilioConstants.ContextNames.HAZARD, c.getContext().get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}

	public String deleteHazard() throws Exception {
		
		if(!CollectionUtils.isEmpty(hazardIds)) {
			FacilioChain c = FacilioChainFactory.deleteHazardsChain();
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, hazardIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getHazardList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getHazardListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "hazard");
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Hazard.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "hazard.name");
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
			List<HazardContext> hazards = (List<HazardContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.HAZARDS, hazards);
		}
		
		return SUCCESS;
	}
	
	public String getHazardDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getHazardDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		HazardContext hazardContext = (HazardContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.HAZARD, hazardContext);
		
		return SUCCESS;
	}

}
