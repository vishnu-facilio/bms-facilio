package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ServiceRequestContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class ServiceRequestsAction extends FacilioAction{
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
	
	public String addServiceRequest() throws Exception {
	
			FacilioChain c = TransactionChainFactory.addServiceRequestChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD, serviceRequest);
			c.execute();
			setResult(FacilioConstants.ContextNames.SERVICE_REQUEST, serviceRequest);

		return SUCCESS;
	}
}
