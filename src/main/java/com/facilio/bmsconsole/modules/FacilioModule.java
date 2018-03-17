	package com.facilio.bmsconsole.modules;

import java.io.Serializable;
import java.util.List;

public class FacilioModule implements Serializable {
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	private String tableName;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	private FacilioModule extendModule;
	public FacilioModule getExtendModule() {
		return extendModule;
	}
	public void setExtendModule(FacilioModule extendModule) {
		this.extendModule = extendModule;
	}
	
	private List<FacilioField> fields;
	public List<FacilioField> getFields() {
		return fields;
	}
	public void setFields(List<FacilioField> fields) {
		this.fields = fields;
	}
	
	private ModuleType type;
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = ModuleType.valueOf(type);
	}
	public ModuleType getTypeEnum() {
		return type;
	}
	public void setType(ModuleType type) {
		this.type = type;
	}
	
	private Boolean trashEnabled;
	public Boolean getTrashEnabled() {
		return trashEnabled;
	}
	public void setTrashEnabled(Boolean trashEnabled) {
		this.trashEnabled = trashEnabled;
	}
	public boolean isTrashEnabled() {
		if (trashEnabled != null) {
			return trashEnabled.booleanValue();
		}
		return false;
	}
	
	
	@Override
	public boolean equals(Object obj) {

		if (obj!=null && obj instanceof FacilioModule ) {
			return this.name.equals(((FacilioModule)obj).name);
		}
		return false;
	}
	
	public static enum ModuleType {
		BASE_ENTITY,
		PICK_LIST,
		READING,
		PHOTOS,
		NOTES,
		ATTACHMENTS,
		ENPI
		;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public static ModuleType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
