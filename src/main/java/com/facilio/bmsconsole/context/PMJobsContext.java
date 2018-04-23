package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.templates.WorkorderTemplate;

public class PMJobsContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long pmTriggerId = -1;
	public long getPmTriggerId() {
		return pmTriggerId;
	}
	public void setPmTriggerId(long pmTriggerId) {
		this.pmTriggerId = pmTriggerId;
	}

	private long nextExecutionTime = -1;
	public long getNextExecutionTime() {
		return nextExecutionTime;
	}
	public void setNextExecutionTime(long nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}
	
	private long templateId = -1;
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	
	private WorkorderTemplate template;
	public WorkorderTemplate getTemplate() {
		return template;
	}
	public void setTemplate(WorkorderTemplate template) {
		this.template = template;
	}

	private boolean projected;
	public boolean isProjected() {
		return projected;
	}
	public void setProjected(boolean projected) {
		this.projected = projected;
	}
	
	private PMJobsStatus status;
	public PMJobsStatus getStatusEnum() {
		return status;
	}
	public void setStatus(PMJobsStatus status) {
		this.status = status;
	}
	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(int status) {
		this.status = PMJobsStatus.valueOf(status);
	}

	public static enum PMJobsStatus {
		ACTIVE,
		SCHEDULED,
		COMPLETED,
		IN_ACTIVE
		;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public static PMJobsStatus valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
