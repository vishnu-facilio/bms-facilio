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
import com.facilio.bmsconsole.context.SafetyPlanContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class SafetyPlanAction extends FacilioAction{

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
	
	private SafetyPlanContext safetyPlan;
	private List<SafetyPlanContext> safetyPlanRecords;
	private List<Long> safetyPlanIds;
	
	public SafetyPlanContext getSafetyPlan() {
		return safetyPlan;
	}
	public void setSafetyPlan(SafetyPlanContext safetyPlan) {
		this.safetyPlan = safetyPlan;
	}
	public List<SafetyPlanContext> getSafetyPlanRecords() {
		return safetyPlanRecords;
	}
	public void setSafetyPlanRecords(List<SafetyPlanContext> safetyPlanRecords) {
		this.safetyPlanRecords = safetyPlanRecords;
	}
	public List<Long> getSafetyPlanIds() {
		return safetyPlanIds;
	}
	public void setSafetyPlanIds(List<Long> safetyPlanIds) {
		this.safetyPlanIds = safetyPlanIds;
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

	private String safetyPlanString;
	public String getSafetyPlanString() {
		return safetyPlanString;
	}
	public void setSafetyPlanString(String safetyPlanString) {
		this.safetyPlanString = safetyPlanString;
	}

	private Map<String, Object> subFormFiles;
	public Map<String, Object> getSubFormFiles() {
		return subFormFiles;
	}
	public void setSubFormFiles(Map<String, Object> subFormFiles) {
		this.subFormFiles = subFormFiles;
	}
	
	public String addSafetyPlan() throws Exception {
		
		if (StringUtils.isNotEmpty(safetyPlanString)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(safetyPlanString);
			safetyPlan = FieldUtil.getAsBeanFromJson(json, SafetyPlanContext.class);
			
			if (safetyPlan != null && MapUtils.isNotEmpty(subFormFiles)) {
				safetyPlan.addSubFormFiles(subFormFiles);
			}
		}
		
		safetyPlan.parseFormData();
		
		FacilioChain c = TransactionChainFactory.addSafetyPlansChain();
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		c.getContext().put(FacilioConstants.ContextNames.RECORD, safetyPlan);
		c.execute();
		setResult(FacilioConstants.ContextNames.SAFETY_PLAN, c.getContext().get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String updateSafetyPlan() throws Exception {
		
		FacilioChain c = TransactionChainFactory.updateSafetyPlansChain();
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
		c.getContext().put(FacilioConstants.ContextNames.RECORD, safetyPlan);
		if (StringUtils.isNotEmpty(safetyPlanString)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(safetyPlanString);
			safetyPlan = FieldUtil.getAsBeanFromJson(json, SafetyPlanContext.class);
			
			if (safetyPlan != null && MapUtils.isNotEmpty(subFormFiles)) {
				safetyPlan.addSubFormFiles(subFormFiles);
			}
		}
		safetyPlan.parseFormData();
		
		c.execute();
		setResult(FacilioConstants.ContextNames.SAFETY_PLAN, c.getContext().get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}

	public String deleteSafetyPlan() throws Exception {
		
		if(!CollectionUtils.isEmpty(safetyPlanIds)) {
			FacilioChain c = FacilioChainFactory.deleteSafetyPlanChain();
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, safetyPlanIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getSafetyPlanList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getSafetyPlanListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "safetyPlan");
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Safety_Plan.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "safetyPlan.name");
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
			List<SafetyPlanContext> safetyPlanRecords = (List<SafetyPlanContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.SAFETY_PLANS, safetyPlanRecords);
		}
		
		return SUCCESS;
	}
	
	public String getSafetyPlanDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getSafetyPlanDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		SafetyPlanContext safetyPlanContext = (SafetyPlanContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.SAFETY_PLAN, safetyPlanContext);
		
		return SUCCESS;
	}

}
