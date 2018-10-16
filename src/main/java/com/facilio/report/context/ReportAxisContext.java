package com.facilio.report.context;

import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.modules.BooleanField;
import com.facilio.bmsconsole.modules.EnumField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.unitconversion.Metric;
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
			
			if (field.getModule() != null) {
				this.moduleName = field.getModule().getName();
			}
			
			if (this.label == null) {
				this.label = field.getDisplayName();
			}
			if (this.dataType == null) {
				this.dataType = field.getDataTypeEnum();
			}
			
			if (field instanceof NumberField) {
				if (this.unitStr == null) {
					this.unitStr = ((NumberField) field).getUnit();
				}
				this.metric = ((NumberField) field).getMetricEnum();
			}
			else if (field instanceof BooleanField) {
				BooleanField boolField = (BooleanField) field;
				if (boolField.getTrueVal() != null && !boolField.getTrueVal().isEmpty()) {
					enumMap = new HashMap<>();
					enumMap.put(1, boolField.getTrueVal());
					enumMap.put(0, boolField.getFalseVal());
				}
			}
			else if (field instanceof EnumField) {
				enumMap = ((EnumField) field).getEnumMap();
			}
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

	// Only for Y Field
	private String joinOn;
	public String getJoinOn() {
		return joinOn;
	}
	public void setJoinOn(String joinOn) {
		this.joinOn = joinOn;
	}
	
	private Map<Integer, Object> enumMap;
	public Map<Integer, Object> getEnumMap() {
		return enumMap;
	}
	public void setEnumMap(Map<Integer, Object> enumMap) {
		this.enumMap = enumMap;
	}
}
