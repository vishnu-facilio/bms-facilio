package com.facilio.mv.context;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class MVBaseline extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MVProjectContext project;
	String name;
	FormulaFieldContext formulaField;
	FormulaFieldContext formulaFieldWithAjustment;
	FormulaFieldContext targetConsumption;
	FormulaFieldContext savedConsumption;
	FormulaFieldContext cumulativeSavedConsumption;
	FormulaFieldContext percentageSavedConsumption;
	public FormulaFieldContext getTargetConsumption() {
		return targetConsumption;
	}
	public void setTargetConsumption(FormulaFieldContext targetConsumption) {
		this.targetConsumption = targetConsumption;
	}
	public FormulaFieldContext getSavedConsumption() {
		return savedConsumption;
	}
	public void setSavedConsumption(FormulaFieldContext savedConsumption) {
		this.savedConsumption = savedConsumption;
	}
	public FormulaFieldContext getCumulativeSavedConsumption() {
		return cumulativeSavedConsumption;
	}
	public void setCumulativeSavedConsumption(FormulaFieldContext cumulativeSavedConsumption) {
		this.cumulativeSavedConsumption = cumulativeSavedConsumption;
	}
	public FormulaFieldContext getPercentageSavedConsumption() {
		return percentageSavedConsumption;
	}
	public void setPercentageSavedConsumption(FormulaFieldContext percentageSavedConsumption) {
		this.percentageSavedConsumption = percentageSavedConsumption;
	}
	long startTime;
	long endTime;
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
