package com.facilio.report.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.facilio.modules.fields.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter @Setter
public class ReportFieldContext {

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long reportId = -1;
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private FacilioModule module;
	@JsonIgnore
	public FacilioModule getModule() {
		if (module != null) {
			return module;
		}
		if (getField() == null) {
			return null;
		}
		return getField().getModule();
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long lookupFieldId = -1;
	public long getLookupFieldId() {
		return lookupFieldId;
	}
	public void setLookupFieldId(long lookupFieldId) {
		this.lookupFieldId = lookupFieldId;
	}
	
	private long subModuleFieldId = -1;
	public long getSubModuleFieldId() {
		return subModuleFieldId;
	}
	public void setSubModuleFieldId(long subModuleFieldId) {
		this.subModuleFieldId = subModuleFieldId;
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
	public void setField(FacilioModule module, FacilioField field) {
		if (module != null) {
			this.moduleName = module.getName();
			this.moduleId = module.getModuleId();
			this.module = module;
		}
		
		if (field != null) {
			
			this.fieldId = field.getId();
			this.fieldName = field.getName();
			
			if (field.getModule() != null) {
				this.predicted = field.getModule().getTypeEnum() == ModuleType.PREDICTED_READING;
			}
			
			if (this.label == null) {
				this.label = field.getDisplayName();
			}
			if (this.dataType == null) {
				this.dataType = field.getDataTypeEnum();
			}
			
			if (field instanceof NumberField) {
				this.unitStr = ((NumberField) field).getUnitEnum() != null ? ((NumberField) field).getUnitEnum().getSymbol() : ((NumberField) field).getUnit();
				this.metric = ((NumberField) field).getMetricEnum();
				this.unit = ((NumberField) field).getUnitEnum();
			}
			else if (field instanceof BooleanField) {
				BooleanField boolField = (BooleanField) field;
				enumMap = new HashMap<>();
				if (boolField.getTrueVal() != null && !boolField.getTrueVal().isEmpty()) {
					enumMap.put(1, boolField.getTrueVal());
					enumMap.put(0, boolField.getFalseVal());
				}
				else {
					enumMap.put(1, "True");
					enumMap.put(0, "False");
				}
			}
			else if (field instanceof EnumField) {
				enumMap = ((EnumField) field).getEnumMap();
			}
			else if(field instanceof MultiEnumField){
				enumMap = ((MultiEnumField) field).getEnumMap();
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
	
	private String alias;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
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
	
	private Unit unit;
	public int getUnit() {
		if (unit != null) {
			return unit.getUnitId();
		}
		return -1;
	}
	public void setUnit(int unit) {
		this.unit = Unit.valueOf(unit);
	}
	
	public Unit getUnitEnum() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
	@JsonIgnore
	public Map<String, Object> getUnitObj() throws Exception {
		return FieldUtil.getAsProperties(unit);
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
	
	@JsonIgnore
	public Map<String, Object> getMetricObj() throws Exception {
		return FieldUtil.getAsProperties(metric);
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
	
	private Map<Long, Object> lookupMap;
	public Map<Long, Object> getLookupMap() {
		return lookupMap;
	}
	public void setLookupMap(Map<Long, Object> lookupMap) {
		this.lookupMap = lookupMap;
	}

	private boolean predicted;
	public boolean isPredicted() {
		return predicted;
	}
	public void setPredicted(boolean predicted) {
		this.predicted = predicted;
	}

	private List<Long> selectValuesOnly;
	public void setSelectValuesOnly(List<Long> selectValuesOnly) {
		this.selectValuesOnly = selectValuesOnly;
	}
	public List<Long> getSelectValuesOnly() {
		return selectValuesOnly;
	}
	public boolean isOuterJoin() {
		return CollectionUtils.isNotEmpty(selectValuesOnly);
	}

	private String lookUpFieldName;
	private String lookUpFieldModuleName;

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (!(obj instanceof ReportFieldContext)) {
			return false;
		}
		else if (this == obj) {
			return true;
		}
		else if (this.fieldId > 0) {
			return this.fieldId == ((ReportFieldContext) obj).fieldId; 
		}
		else {
			return Objects.equals(this.fieldName, ((ReportFieldContext) obj).fieldName) && Objects.equals(this.moduleName, ((ReportFieldContext) obj).moduleName);
		}
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		if (this.fieldId > 0) {
			return Long.hashCode(fieldId);
		}
		else {
			return Objects.hashCode(fieldName) + Objects.hashCode(moduleName); 
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return field+"";
	}
}
