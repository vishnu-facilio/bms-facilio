package com.facilio.mv.context;

public class MVAdjustmentVsBaseline {

	long adjustmentId;
	long baselineId;
	long formulaField;
	
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
	public long getFormulaField() {
		return formulaField;
	}
	public void setFormulaField(long formulaField) {
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
