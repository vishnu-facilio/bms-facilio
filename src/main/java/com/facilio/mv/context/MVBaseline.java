package com.facilio.mv.context;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.workflows.context.WorkflowContext;

public class MVBaseline extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MVProjectContext project;
	String name;
	FormulaFieldContext formulaField;
	FormulaFieldContext formulaFieldWithAjustment;
	long startTime;
	long endTime;
	WorkflowContext workflow;
	
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	public FormulaFieldContext getFormulaField() {
		return formulaField;
	}
	public void setFormulaField(FormulaFieldContext formulaField) {
		this.formulaField = formulaField;
	}
	public MVProjectContext getProject() {
		return project;
	}
	public void setProject(MVProjectContext project) {
		this.project = project;
	}
	public FormulaFieldContext getFormulaFieldWithAjustment() {
		return formulaFieldWithAjustment;
	}
	public void setFormulaFieldWithAjustment(FormulaFieldContext formulaFieldWithAjustment) {
		this.formulaFieldWithAjustment = formulaFieldWithAjustment;
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
