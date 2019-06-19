package com.facilio.mv.context;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class MVBaseline extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	long projectId;
	String name;
	FormulaFieldContext formulaField;
	FormulaFieldContext formulaFieldWithAjustment;
	public FormulaFieldContext getFormulaFieldWithAjustment() {
		return formulaFieldWithAjustment;
	}
	public void setFormulaFieldWithAjustment(FormulaFieldContext formulaFieldWithAjustment) {
		this.formulaFieldWithAjustment = formulaFieldWithAjustment;
	}
	long startTime;
	long endTime;
	
	public FormulaFieldContext getFormulaField() {
		return formulaField;
	}
	public void setFormulaField(FormulaFieldContext formulaField) {
		this.formulaField = formulaField;
	}
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
