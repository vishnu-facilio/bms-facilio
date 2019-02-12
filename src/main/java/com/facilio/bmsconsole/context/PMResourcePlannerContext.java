package com.facilio.bmsconsole.context;

import java.util.List;

public class PMResourcePlannerContext {

	Long id;
	Long pmId;
	Long resourceId;
	ResourceContext resource;

	public List<PMTriggerContext> getTriggerContexts() {
		return triggerContexts;
	}

	public void setTriggerContexts(List<PMTriggerContext> triggers) {
		this.triggerContexts = triggers;
	}

	private List<PMTriggerContext> triggerContexts;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	Long assignedToId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPmId() {
		return pmId;
	}
	public void setPmId(Long pmId) {
		this.pmId = pmId;
	}
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	public Long getAssignedToId() {
		return assignedToId;
	}
	public void setAssignedToId(Long assignedToId) {
		this.assignedToId = assignedToId;
	}
	List<PMResourcePlannerReminderContext> pmResourcePlannerReminderContexts;
	public List<PMResourcePlannerReminderContext> getPmResourcePlannerReminderContexts() {
		return pmResourcePlannerReminderContexts;
	}
	public void setPmResourcePlannerReminderContexts(
			List<PMResourcePlannerReminderContext> pmResourcePlannerReminderContexts) {
		this.pmResourcePlannerReminderContexts = pmResourcePlannerReminderContexts;
	}
	
}
