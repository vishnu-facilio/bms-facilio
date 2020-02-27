package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class PmJobPlanTriggerContext implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long id;
	long pmjobPlanId;	
	long triggerId;
	
	PMTriggerContext pmTrigger;
	
	public PMTaskSectionTemplateTriggers getpmTaskSectionTemplateTrigger() {
		PMTaskSectionTemplateTriggers pmTaskSectionTemplateTriggers = new PMTaskSectionTemplateTriggers();
		pmTaskSectionTemplateTriggers.setTriggerId(triggerId);
		pmTaskSectionTemplateTriggers.setTriggerName(pmTrigger.getName());
		return pmTaskSectionTemplateTriggers;
	}
	
	public PMTriggerContext getPmTrigger() {
		return pmTrigger;
	}
	public void setPmTrigger(PMTriggerContext pmTrigger) {
		this.pmTrigger = pmTrigger;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPmjobPlanId() {
		return pmjobPlanId;
	}
	public void setPmjobPlanId(long pmjobPlanId) {
		this.pmjobPlanId = pmjobPlanId;
	}
	public long getTriggerId() {
		return triggerId;
	}
	public void setTriggerId(long triggerId) {
		this.triggerId = triggerId;
	}

}
