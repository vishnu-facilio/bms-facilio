package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitChecklistContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WorkPermitAction extends FacilioAction {

private static final long serialVersionUID = 1L;

	@Getter @Setter
	private long moduleId;
	
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
	
	private Long approvalTransitionId;
	public Long getApprovalTransitionId() {
		return approvalTransitionId;
	}
	public void setApprovalTransitionId(Long approvalTransitionId) {
		this.approvalTransitionId = approvalTransitionId;
	}

	public List<WorkPermitChecklistContext> getChecklistRecords() {
		return checklistRecords;
	}

	public void setChecklistRecords(List<WorkPermitChecklistContext> checklistRecords) {
		this.checklistRecords = checklistRecords;
	}

	private List<WorkPermitChecklistContext> checklistRecords;
	
	private Map<String, List<WorkflowRuleContext>> stateFlows;
	public Map<String, List<WorkflowRuleContext>> getStateFlows() {
		return stateFlows;
	}
	public void setStateFlows(Map<String, List<WorkflowRuleContext>> stateFlows) {
		this.stateFlows = stateFlows;
	}
	public String addWorkPermit() throws Exception {
		
		if(!CollectionUtils.isEmpty(workPermitRecords) || workPermit != null) {
			FacilioChain c = TransactionChainFactory.addWorkPermitRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			if (workPermit != null) {
				workPermit.parseFormData();
				c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(workPermit));
			}else {
				for(WorkPermitContext wp : workPermitRecords) {
					wp.parseFormData();
				}
				c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, workPermitRecords);
			}
			c.execute();
			setResult(FacilioConstants.ContextNames.WORKPERMIT_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateWorkPermit() throws Exception {
		
		if(!CollectionUtils.isEmpty(workPermitRecords)) {
			List<Long> recordIds = new ArrayList<Long>();
			for(WorkPermitContext wp: workPermitRecords) {
				wp.parseFormData();
				recordIds.add(wp.getId());
			}
			FacilioChain c = TransactionChainFactory.updateWorkPermitRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, getStateTransitionId());
			c.getContext().put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, approvalTransitionId);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, workPermitRecords);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
			
			c.execute();
			setResult(FacilioConstants.ContextNames.WORKPERMIT_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteCheckList() throws Exception{
		FacilioChain deleteCheckListChain = TransactionChainFactoryV3.getWorkPermitCheckListDeletionChain();
		FacilioContext deleteCheckListContext = deleteCheckListChain.getContext();
		deleteCheckListContext.put(FacilioConstants.ContextNames.RECORD_ID,id);
		deleteCheckListContext.put(FacilioConstants.ContextNames.MODULE_ID,getModuleId());
		deleteCheckListChain.execute();

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
		setResult(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, workPermitContext);
		
		return SUCCESS;
	}

	
}
