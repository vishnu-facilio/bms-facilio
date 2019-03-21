package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class WorkOrderLabourContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private double cost = -1;

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	private long issueTime = -1;

	public long getIssueTime() {
		return issueTime;
	}

	public void setIssueTime(long issueTime) {
		this.issueTime = issueTime;
	}

	private long returnTime = -1;

	public long getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(long returnTime) {
		this.returnTime = returnTime;
	}

	private long duration = -1;

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDuration() {
		return duration;
	}
	
	private long parentId = -1;
	
	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public LabourContext getLabour() {
		return labour;
	}

	public void setLabour(LabourContext labour) {
		this.labour = labour;
	}

	private LabourContext labour ;
	
	
	
}
