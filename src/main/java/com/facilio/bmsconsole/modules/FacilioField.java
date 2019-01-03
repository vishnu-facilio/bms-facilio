package com.facilio.bmsconsole.modules;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FacilioField implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FacilioField() {
		// TODO Auto-generated constructor stub
	}
	
	protected FacilioField(FacilioField field) { // Do not forget to Handle here if new property is added
		this.fieldId = field.fieldId;
		this.orgId = field.orgId;
		this.module = field.module;
		this.extendedModule = field.extendedModule;
		this.name = field.name;
		this.columnName = field.columnName;
		this.sequenceNumber = field.sequenceNumber;
		this.dataType = field.dataType;
		this.isDefault = field.isDefault;
		this.isMainField = field.isMainField;
		this.required = field.required;
		this.disabled = field.disabled;
		this.displayName = field.displayName;
		this.styleClass = field.styleClass;
		this.icon = field.icon;
		this.placeHolder = field.placeHolder;
		this.displayType = field.displayType;
		this.readingRules = field.readingRules;
		this.actionsList = field.actionsList;
	}
	
	public FacilioField clone() {
		return new FacilioField(this);
	}
	
	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	public long getId() {
		return fieldId;
	}
	public void setId(long id) {
		this.fieldId = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private FacilioModule module;
	public FacilioModule getModule() {
		return module;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}
	
	public long getModuleId() {
		if(module != null) {
			return module.getModuleId();
		}
		return -1;
	}
	
	private FacilioModule extendedModule;
	public FacilioModule getExtendedModule() {
		if(extendedModule != null) {
			return extendedModule;
		}
		else {
			return module;
		}
	}
	public void setExtendedModule(FacilioModule extendedModule) {
		this.extendedModule = extendedModule;
	}
	
	public long getExtendedModuleId() {
		if(extendedModule != null) {
			return extendedModule.getModuleId();
		}
		return -1;
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
	
	public String getCompleteColumnName() {
		if(getExtendedModule() != null) {
			return getExtendedModule().getTableName()+"."+getColumnName();
		}
		else {
			return getColumnName();
		}
	}
	
	private int sequenceNumber = -1;
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	private FieldType dataType;
	public FieldType getDataTypeEnum() {
		return dataType;
	}
	public void setDataType(FieldType dataType) {
		this.dataType = dataType;
	}
	public int getDataType() {
		if(dataType != null) {
			return dataType.getTypeAsInt();
		}
		return -1;
	}
	public void setDataType(int dataType) {
		this.dataType = FieldType.getCFType(dataType);
	}
	
	private Boolean isDefault;
	public boolean isDefault() {
		if(isDefault != null) {
			return isDefault.booleanValue();
		}
		return false;
	}
	public Boolean getDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	private Boolean isMainField;
	public boolean isMainField() {
		if(isMainField != null) {
			return isMainField.booleanValue();
		}
		return false;
	}
	public Boolean getMainField() {
		return isMainField;
	}
	public void setMainField(boolean isMainField) {
		this.isMainField = isMainField;
	}
	
	private Boolean required;
	public boolean isRequired() {
		if(required != null) {
			return required.booleanValue();
		}
		return false;
	}
	public Boolean getRequired() {
		return required;
	}
	public void setRequired(boolean isRequired) {
		this.required = isRequired;
	}
	
	private Boolean disabled;
	public boolean isDisabled() {
		if(disabled != null) {
			return disabled.booleanValue();
		}
		return false;
	}
	public Boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(boolean isDisabled) {
		this.disabled = isDisabled;
	}
	
	private String displayName;
	public String getDisplayName() {
		if(displayName != null && !displayName.isEmpty()) {
			return displayName;
		}
		else {
			return name;
		}
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	private String styleClass;
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
		if(module != null) {
			String moduleName = module.getName();
			if(dataType == FieldType.LOOKUP) {
				//return moduleName+".customProps['"+name+FacilioConstants.ContextNames.ID+"']";
				return moduleName+"."+name+".id";
			}
			else if (isDefault()){
				return moduleName+"."+name;
			}
			else {
				return moduleName+".data['"+name+"']";
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
            return true;
        }
		if (other != null && other instanceof FacilioField ) {
			FacilioField iObj = (FacilioField) other;
			FacilioModule iModule = iObj.module;
			if(iModule == null && this.module == null) {
				return this.name.equals(iObj.name);
			}
			else if (iModule != null && this.module != null) {
				return this.module.equals(iObj.module) && this.name.equals(iObj.name);
			}
		}
		return false;
	}
	
	private FieldDisplayType displayType;
	
	@JsonIgnore
	public FieldDisplayType getDisplayType() {
		return displayType;
	}
	
	@JsonIgnore
	public void setDisplayType(FieldDisplayType displayType) {
		this.displayType = displayType;
	}
	
	@JsonIgnore
	public void setDisplayType(int displayType) {
		this.displayType = FieldDisplayType.TYPE_MAP.get(displayType);
	}
	
	public void setDisplayTypeInt(int displayType) {
		this.displayType = FieldDisplayType.TYPE_MAP.get(displayType);
	}
	
	public int getDisplayTypeInt() {
		if (displayType != null) {
			return displayType.getIntValForDB();
		}
		return -1;
	}
	
	private List<ReadingRuleContext> readingRules;
	public List<ReadingRuleContext> getReadingRules() {
		return this.readingRules;
	}
	
	public void setReadingRules(List<ReadingRuleContext> readingRules) {
		this.readingRules = readingRules;
	}
	
	private List<List<ActionContext>> actionsList;
	public List<List<ActionContext>> getActionsList() {
		return this.actionsList;
	}
	
	public void setActionsList(List<List<ActionContext>> actionsList) {
		this.actionsList = actionsList;
	}
	
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		if (module != null) {
			builder.append("Module Name : ")
					.append(module.getName())
					.append("::");
		}
		if (name != null) {
			builder.append("Name : ")
					.append(name)
					.append("::");
		}
		if (displayName != null) {
			builder.append("Display Name : ")
					.append(displayName)
					.append("::");
		}
		if (columnName != null) {
			builder.append("Column Name : ")
					.append(columnName)
					.append("::");
		}
		if (dataType != null) {
			builder.append("DataType : ")
					.append(dataType);
		}
		return builder.toString();
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
		LOOKUP_SECTION(12, null),
		DECIMAL(13, "number"),
		ENPI(14, "enpi"), //Temp hack,
		WOASSETSPACECHOOSER(15, "wo-asset-space-chooser"),
		REQUESTER(16, "requester"),
		URGENCY(17, "urgency"),
		ATTACHMENT(18, "attachment"), 
		TEAMSTAFFASSIGNMENT(19, "team-staff-assignment"),
		TEAM(20, "team"),
		TICKETNOTES(21, "ticketnotes"),
		SIGNATURE(22, null),
		DURATION(23, "duration"), 
		TASKS(24, "tasks")
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
		
		
		public static final Map<Integer, FieldDisplayType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, FieldDisplayType> initTypeMap() {
			Map<Integer, FieldDisplayType> typeMap = new HashMap<>();
			for(FieldDisplayType type : values()) {
				typeMap.put(type.getIntValForDB(), type);
			}
			return typeMap;
		}
	}
}
