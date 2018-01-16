package com.facilio.bmsconsole.context;

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
	
	private boolean projected;
	public boolean isProjected() {
		return projected;
	}
	public void setProjected(boolean projected) {
		this.projected = projected;
	}
}
