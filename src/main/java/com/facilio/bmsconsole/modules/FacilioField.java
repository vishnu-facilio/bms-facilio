package com.facilio.bmsconsole.modules;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FacilioField implements Serializable {
	
	private long fieldId;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long moduleId;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String columnName;
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	private int sequenceNumber;
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	private int dataTypeCode;
	public int getDataTypeCode() {
		return dataTypeCode;
	}
	public void setDataTypeCode(int dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}
	
	private FieldType dataType;
	public FieldType getDataType() {
		return dataType;
	}
	public void setDataType(FieldType dataType) {
		this.dataType = dataType;
	}
	
	private boolean isDefault = false;
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	private boolean isMainField = false;
	public boolean isMainField() {
		return isMainField;
	}
	public void setMainField(boolean isMainField) {
		this.isMainField = isMainField;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String moduleTableName;
	public String getModuleTableName() {
		return moduleTableName;
	}
	public void setModuleTableName(String moduleTableName) {
		this.moduleTableName = moduleTableName;
	}
	
	private boolean required;
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean isRequired) {
		this.required = isRequired;
	}
	
	private boolean disabled;
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean isDisabled) {
		this.disabled = isDisabled;
	}
	
	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	private String styleClass = "form-control";
	public String getStyleClass() {
		return styleClass;
	}
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	private String icon;
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	private String placeHolder;
	public String getPlaceHolder() {
		return placeHolder;
	}
	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}
	
	public String getInputName() {
		if(moduleName != null &&! moduleName.isEmpty()) {
			if(dataType == FieldType.LOOKUP) {
				//return moduleName+".customProps['"+name+FacilioConstants.ContextNames.ID+"']";
				return moduleName+"."+name+".id";
			}
			else if (isDefault){
				return moduleName+"."+name;
			}
			else {
				return moduleName+".customProps['"+name+"']";
			}
		}
		return null;
	}
	
	private FieldDisplayType displayType;
	public FieldDisplayType getDisplayType() {
		return displayType;
	}
	public void setDisplayType(FieldDisplayType displayType) {
		this.displayType = displayType;
	}
	public void setDisplayType(int displayType) {
		this.displayType = FieldDisplayType.TYPE_MAP.get(displayType);
	}
	
	
	public String toString()
	{
		return "\n"+moduleName + "- "+name + "- " +dataType;
	}
	
	public enum FieldDisplayType {
		TEXTBOX(1, "text"),
		TEXTAREA(2, "text"), 
		SELECTBOX(3, null), 
		RADIO(4, "radio"), 
		DECISION_BOX(5, "checkbox"),
		DATE(6, "text"),
		DATETIME(7, "text"), 
		EMAIL(8, "email"), 
		NUMBER(9, "number"), 
		LOOKUP_SIMPLE(10, null), 
		LOOKUP_POPUP(11, null),
		LOOKUP_SECTION(12, null)
		;
		
		private int intVal;
		private String html5Type;
		private FieldDisplayType(int intVal, String html5Type) {
			this.intVal = intVal;
			this.html5Type = html5Type;
		}
		
		public int getIntValForDB() {
			return intVal;
		}
		
		public String getHtml5Type() {
			return html5Type;
		}
		
		
		private static final Map<Integer, FieldDisplayType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, FieldDisplayType> initTypeMap() {
			Map<Integer, FieldDisplayType> typeMap = new HashMap<>();
			for(FieldDisplayType type : values()) {
				typeMap.put(type.getIntValForDB(), type);
			}
			return typeMap;
		}
	}
}
