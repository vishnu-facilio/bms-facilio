package com.facilio.report.context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.unitconversion.Unit;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReportGroupByField {
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
	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		if (field != null) {
			this.fieldId = field.getId();
			this.fieldName = field.getName();
			this.moduleName = field.getModule().getName();
			
			if (this.getDataTypeEnum() == null) {
				this.dataType = field.getDataTypeEnum();
			}
			if (this.getUnitEnum() == null && field instanceof NumberField) {
				this.unit = ((NumberField) field).getUnitEnum();
			}
		}
		this.field = field;
	}
	
	private FieldType dataType;
	public FieldType getDataTypeEnum() {
		return dataType;
	}
	public void setDataType(FieldType dataType) {
		this.dataType = dataType;
	}
	public int getDataType() {
		if (dataType != null) {
			return dataType.getTypeAsInt();
		}
		return -1;
	}
	public void setDataType(int dataType) {
		this.dataType = FieldType.getCFType(dataType);
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
	
	private String joinOn;
	public String getJoinOn() {
		return joinOn;
	}
	public void setJoinOn(String joinOn) {
		this.joinOn = joinOn;
	}
}
