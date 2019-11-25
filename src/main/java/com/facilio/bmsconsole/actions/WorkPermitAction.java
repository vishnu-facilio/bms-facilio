package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class WorkPermitAction extends FacilioAction {

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
	
	private Boolean vendorPortal;
	public Boolean getVendorPortal() {
		if (vendorPortal == null) {
			return false;
		}
		return vendorPortal;
	}
	public void setVendorPortal(Boolean vendorPortal) {
		this.vendorPortal = vendorPortal;
	}

	private WorkPermitContext workPermit;
	private List<WorkPermitContext> workPermitRecords;
	private List<Long> workPermitIds;
	
	public WorkPermitContext getWorkPermit() {
		return workPermit;
	}
	public void setWorkPermit(WorkPermitContext workPermit) {
		this.workPermit = workPermit;
	}
	public List<WorkPermitContext> getWorkPermitRecords() {
		return workPermitRecords;
	}
	public void setWorkPermitRecords(List<WorkPermitContext> workPermitRecords) {
		this.workPermitRecords = workPermitRecords;
	}
	public List<Long> getWorkPermitIds() {
		return workPermitIds;
	}
	public void setWorkPermitIds(List<Long> workPermitIds) {
		this.workPermitIds = workPermitIds;
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

	private Long stateTransitionId;
	public Long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(Long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}
	
	private Map<String, List<WorkflowRuleContext>> stateFlows;
	public Map<String, List<WorkflowRuleContext>> getStateFlows() {
		return stateFlows;
	}
	public void setStateFlows(Map<String, List<WorkflowRuleContext>> stateFlows) {
		this.stateFlows = stateFlows;
	}
	public String addWorkPermit() throws Exception {
		
		if(!CollectionUtils.isEmpty(workPermitRecords)) {
			FacilioChain c = TransactionChainFactory.addWorkPermitRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, workPermitRecords);
			c.execute();
			setResult(FacilioConstants.ContextNames.WORKPERMIT_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateWorkPermit() throws Exception {
		
		if(!CollectionUtils.isEmpty(workPermitRecords)) {
			FacilioChain c = TransactionChainFactory.updateWorkPermitRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
			c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, getStateTransitionId());
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, workPermitRecords);
			c.execute();
			setResult(FacilioConstants.ContextNames.WORKPERMIT_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteWorkPermit() throws Exception {
		
		if(!CollectionUtils.isEmpty(workPermitIds)) {
			FacilioChain c = FacilioChainFactory.deleteWorkPermitChain();
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, workPermitIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getWorkPermitList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getWorkPermitListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "workpermit");
		chain.getContext().put(FacilioConstants.ContextNames.IS_VENDOR_PORTAL, getVendorPortal());
	 	
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "WorkPermit.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "workpermit.vendor");
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
		List<WorkPermitContext> workPermitRecords = (List<WorkPermitContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.WORKPERMIT_RECORDS, workPermitRecords);
			setStateFlows((Map<String, List<WorkflowRuleContext>>) chain.getContext().get("stateFlows"));
			setResult("stateFlows", getStateFlows());
		}
		
		return SUCCESS;
	}
	
	public String getWorkPermitDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getWorkPermitDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		WorkPermitContext workPermitContext = (WorkPermitContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.WORKPERMIT, workPermitContext);
		
		return SUCCESS;
	}
	
}
