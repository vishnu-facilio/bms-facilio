package com.facilio.mv.context;

import com.facilio.bmsconsole.context.FormulaFieldContext;

public class MVAdjustmentVsBaseline {

	long adjustmentId;
	long baselineId;
	long formulaFieldId;
	FormulaFieldContext formulaField;
	
	String adjustmentName;
	String baselineName;
	public long getAdjustmentId() {
		return adjustmentId;
	}
	public void setAdjustmentId(long adjustmentId) {
		this.adjustmentId = adjustmentId;
	}
	public long getBaselineId() {
		return baselineId;
	}
	public void setBaselineId(long baselineId) {
		this.baselineId = baselineId;
	}
	public long getFormulaFieldId() {
		return formulaFieldId;
	}
	public void setFormulaFieldId(long formulaFieldId) {
		this.formulaFieldId = formulaFieldId;
	}
	public FormulaFieldContext getFormulaField() {
		return formulaField;
	}
	public void setFormulaField(FormulaFieldContext formulaField) {
		this.formulaField = formulaField;
	}
	public String getAdjustmentName() {
		return adjustmentName;
	}
	public void setAdjustmentName(String adjustmentName) {
		this.adjustmentName = adjustmentName;
	}
	public String getBaselineName() {
		return baselineName;
	}
	public void setBaselineName(String baselineName) {
		this.baselineName = baselineName;
	}
	
}
