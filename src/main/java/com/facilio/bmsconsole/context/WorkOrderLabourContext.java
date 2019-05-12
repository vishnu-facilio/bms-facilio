package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class WorkOrderLabourContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private double cost = -1;

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	private long startTime = -1;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	private long endTime = -1;

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
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

	public void calculate() {
		if (this.duration == -1) {
			if (this.endTime == -1 || this.startTime == -1) {
				this.duration = 0;
			} else {
				this.duration = this.endTime - this.startTime;
			}
		}
		
		if (this.labour != null) {
			this.cost = (this.duration / 1000.0 / 60 / 60) * this.labour.getCost();
		}
	}
	
	
	
}
