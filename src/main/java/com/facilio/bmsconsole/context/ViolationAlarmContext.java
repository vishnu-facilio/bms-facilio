package com.facilio.bmsconsole.context;

public class ViolationAlarmContext extends BaseAlarmContext {
	
	private static final long serialVersionUID = 1L;
	
	private FormulaFieldContext formulaField;
	public FormulaFieldContext getFormulaField() {
		return formulaField;
	}
	public void setFormulaField(FormulaFieldContext formulaField) {
		this.formulaField = formulaField;
	}

}
