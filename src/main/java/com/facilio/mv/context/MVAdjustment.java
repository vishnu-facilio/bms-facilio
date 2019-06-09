package com.facilio.mv.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class MVAdjustment extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	long projectId;
	String name;
	long formulaFieldId;
	int period;				// change to enum;
	long startTime;
	long endTime;
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getFormulaFieldId() {
		return formulaFieldId;
	}
	public void setFormulaFieldId(long formulaFieldId) {
		this.formulaFieldId = formulaFieldId;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	
	

}
