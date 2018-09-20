package com.facilio.report.context;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.unitconversion.Unit;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReportAxisContext {
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String fieldName;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private FacilioField field;
	
	@JsonIgnore
	public FacilioField getField() throws Exception {
		return field;
	}
	public void setField(FacilioField field) {
		if (field != null) {
			this.fieldId = field.getId();
			this.fieldName = field.getName();
			this.moduleName = field.getModule().getName();
		}
		this.field = field;
	}
	
	private String label;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	private AggregateOperator aggr;
	public AggregateOperator getAggrEnum() {
		return aggr;
	}
	public void setAggr(AggregateOperator aggr) {
		this.aggr = aggr;
	}
	public int getAggr() {
		if (aggr != null) {
			return aggr.getValue();
		}
		return -1;
	}
	public void setAggr(int aggr) {
		this.aggr = AggregateOperator.getAggregateOperator(aggr);
	}
	
	private Unit unit;
	public Unit getUnitEnum() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public int getUnit() {
		if (unit != null) {
			return unit.getUnitId();
		}
		return -1;
	}
	public void setUnit(int unit) {
		this.unit = Unit.valueOf(unit);
	}
	
	// Only for Y Field
	private String joinOn;
	public String getJoinOn() {
		return joinOn;
	}
	public void setJoinOn(String joinOn) {
		this.joinOn = joinOn;
	}
}
