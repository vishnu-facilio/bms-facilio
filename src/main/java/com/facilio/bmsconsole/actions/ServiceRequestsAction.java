package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ServiceRequestContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Map;

public class ServiceRequestsAction extends FacilioAction {
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

	private ServiceRequestContext serviceRequest;

	public ServiceRequestContext getServiceRequest() {
		return serviceRequest;
	}

	public void setServiceRequest(ServiceRequestContext serviceRequest) {
		this.serviceRequest = serviceRequest;
	}

	private List<ServiceRequestContext> serviceRequests;

	public List<ServiceRequestContext> getServiceRequests() {
		return serviceRequests;
	}

	public void setServiceRequests(List<ServiceRequestContext> serviceRequests) {
		this.serviceRequests = serviceRequests;
	}

	private long serviceRequestId;

	public long getServiceRequestId() {
		return serviceRequestId;
	}

	public void setServiceRequestId(long serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}

	private List<Long> serviceRequestIds;

	public List<Long> getServiceRequestIds() {
		return serviceRequestIds;
	}

	public void setServiceRequestIds(List<Long> serviceRequestIds) {
		this.serviceRequestIds = serviceRequestIds;
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

	public String addServiceRequest() throws Exception {

		if (CollectionUtils.isNotEmpty(serviceRequests)) {
			FacilioChain c = TransactionChainFactory.addServiceRequestChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			for(ServiceRequestContext sr : serviceRequests) {
				sr.parseFormData();
			}
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, serviceRequests);
//			c.getContext().put(FacilioConstants.ContextNames.REQUESTER, serviceRequest.getRequester());
			c.execute();
			setResult(FacilioConstants.ContextNames.SERVICE_REQUESTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));

		}
		return SUCCESS;
	}

	public String updateServiceRequests() throws Exception {

		if (!CollectionUtils.isEmpty(serviceRequestIds)) {
			FacilioChain c = TransactionChainFactory.updateServiceRequestChain();
			serviceRequest.parseFormData();
			c.getContext().put(FacilioConstants.ContextNames.RECORD, serviceRequest);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, serviceRequestIds);
			c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
			c.getContext().put(FacilioConstants.ContextNames.REQUESTER, serviceRequest.getRequester());
			c.execute();
			setResult(FacilioConstants.ContextNames.SERVICE_REQUESTS,
					c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteServiceRequests() throws Exception {

		if (!CollectionUtils.isEmpty(serviceRequestIds)) {
			FacilioChain c = FacilioChainFactory.deleteServiceRequestsChain();

			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, serviceRequestIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST,
					c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}

	public String getServicerequestsList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getServiceRequestsListChain();

		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "serviceRequest");

		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Service_Requests.ID asc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
			chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "serviceRequest.subject");
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
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,
					chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		} else {
			serviceRequests = (List<ServiceRequestContext>) chain.getContext()
					.get(FacilioConstants.ContextNames.RECORD_LIST);
			setStateFlows((Map<String, List<WorkflowRuleContext>>) chain.getContext().get("stateFlows"));
			setResult(FacilioConstants.ContextNames.SERVICE_REQUESTS, serviceRequests);
			setResult("stateFlows", getStateFlows());
		}

		return SUCCESS;
	}

	public String getServiceRequestDetails() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getServcieRequestDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);

		chain.execute();

		serviceRequest = (ServiceRequestContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.SERVICE_REQUEST, serviceRequest);

		return SUCCESS;
	}
	
	public String getServcieRequestSubModules() throws Exception{
		FacilioChain chain = ReadOnlyChainFactory.getServiceRequestSubModulesChain();
		chain.execute();
		setResult(FacilioConstants.ContextNames.SUB_MODULES, chain.getContext().get(FacilioConstants.ContextNames.SUB_MODULES));
		return SUCCESS;
		
	}
}
