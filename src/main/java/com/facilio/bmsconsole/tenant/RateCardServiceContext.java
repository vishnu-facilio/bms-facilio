package com.facilio.bmsconsole.tenant;

import com.facilio.workflows.context.WorkflowContext;

public class RateCardServiceContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long rateCardId = -1;
	public long getRateCardId() {
		return rateCardId;
	}
	public void setRateCardId(long rateCardId) {
		this.rateCardId = rateCardId;
	}
	
	private ServiceType serviceType;
	public ServiceType getServiceTypeEnum() {
		return serviceType;
	}
	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	public int getServiceType() {
		if (serviceType != null) {
			return serviceType.getValue();
		}
		return -1;
	}
	public void setServiceType(int serviceType) {
		this.serviceType = ServiceType.valueOf(serviceType);
	}

	private FacilioUtility utility;
	public FacilioUtility getUtilityEnum() {
		return utility;
	}
	public void setUtility(FacilioUtility utility) {
		this.utility = utility;
	}
	public int getUtility() {
		if (utility != null) {
			return utility.getValue();
		}
		return -1;
	}
	public void setUtility(int utility) {
		this.utility = FacilioUtility.valueOf(utility);
	}
	
	private double price = -1;
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}

	public static enum ServiceType {
		UTILITY,
		FORMULA
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static ServiceType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
