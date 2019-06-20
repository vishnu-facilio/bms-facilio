package com.facilio.mv.context;

import java.util.List;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class MVAdjustment extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MVProjectContext project;
	String name;
	FormulaFieldContext formulaField;
//	int period;				// change to enum;
	long startTime;
	long endTime;
	List<MVAdjustmentVsBaseline> adjustmentVsBaseline;
	
	public MVProjectContext getProject() {
		return project;
	}
	public void setProject(MVProjectContext project) {
		this.project = project;
	}
	public FormulaFieldContext getFormulaField() {
		return formulaField;
	}
	public void setFormulaField(FormulaFieldContext formulaField) {
		this.formulaField = formulaField;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public int getPeriod() {
//		return period;
//	}
//	public void setPeriod(int period) {
//		this.period = period;
//	}
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
	
	public List<MVAdjustmentVsBaseline> getAdjustmentVsBaseline() {
		return adjustmentVsBaseline;
	}

	public void setAdjustmentVsBaseline(List<MVAdjustmentVsBaseline> adjustmentVsBaseline) {
		this.adjustmentVsBaseline = adjustmentVsBaseline;
	}

}
