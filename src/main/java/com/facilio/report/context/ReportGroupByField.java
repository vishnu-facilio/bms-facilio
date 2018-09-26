package com.facilio.report.context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.unitconversion.Metric;
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
			
			if (this.dataType == null) {
				this.dataType = field.getDataTypeEnum();
			}
			if (field instanceof NumberField) {
				if (this.unitStr == null) {
					this.unitStr = ((NumberField) field).getUnit();
				}
				this.metric = ((NumberField) field).getMetricEnum();
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

	private String unitStr;
	public String getUnitStr() {
		return unitStr;
	}
	public void setUnitStr(String unitStr) {
		this.unitStr = unitStr;
	}

	private Metric metric;
	public int getMetric() {
		if (metric != null) {
			return metric.getMetricId();
		}
		return -1;
	}
	public void setMetric(int metric) {
		this.metric = Metric.valueOf(metric);
	}
	public Metric getMetricEnum() {
		return metric;
	}
	public void setMetric(Metric metric) {
		this.metric = metric;
	}
	
	private String joinOn;
	public String getJoinOn() {
		return joinOn;
	}
	public void setJoinOn(String joinOn) {
		this.joinOn = joinOn;
	}
}
