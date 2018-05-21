package com.facilio.bmsconsole.modules;

import com.facilio.workflows.context.WorkflowContext;

public class FormulaField extends FacilioField {
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}

	private int interval = -1; //In minutes
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	private FieldType resultDataType;
	public FieldType getResultDataTypeEnum() {
		return resultDataType;
	}
	public void setResultDataType(FieldType resultDataType) {
		this.resultDataType = resultDataType;
	}
	public int getResultDataType() {
		if (resultDataType != null) {
			return resultDataType.getTypeAsInt();
		}
		return -1;
	}
	public void setResultDataType(int resultDataType) {
		this.resultDataType = FieldType.getCFType(resultDataType);
	}
	
	private FormulaFieldType formulaFieldType;
	public FormulaFieldType getFormulaFieldTypeEnum() {
		return formulaFieldType;
	}
	public void setFormulaFieldType(FormulaFieldType formulaFieldType) {
		this.formulaFieldType = formulaFieldType;
	}
	public int getFormulaFieldType() {
		if (formulaFieldType != null) {
			return formulaFieldType.getValue();
		}
		return -1;
	}
	public void setFormulaFieldType(int formulaFieldType) {
		this.formulaFieldType = FormulaFieldType.valueOf(formulaFieldType);
	}

	public enum FormulaFieldType {
		PRE,
		POST
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static FormulaFieldType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
}
