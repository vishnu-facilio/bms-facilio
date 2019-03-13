	package com.facilio.bmsconsole.modules;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.time.SecondsChronoUnit;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FacilioModule implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FacilioModule() {
		// TODO Auto-generated constructor stub
	}

	public FacilioModule(FacilioModule module) {
		this.moduleId = module.moduleId;
		this.orgId = module.orgId;
		this.name = module.name;
		this.displayName = module.displayName;
		this.tableName = module.tableName;
		this.extendModule = module.extendModule;
		this.type = module.type;
		this.trashEnabled = module.trashEnabled;
		this.dataInterval = module.dataInterval;
	}

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
	
	@JsonIgnore
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
	
	private int dataInterval = -1;
	public int getDataInterval() {
		return dataInterval;
	}
	public void setDataInterval(int dataInterval) {
		this.dataInterval = dataInterval;
		this.dateIntervalUnit = new SecondsChronoUnit(dataInterval * 60);
	}
	
	private SecondsChronoUnit dateIntervalUnit;
	
	@JsonIgnore
	@JSON(serialize=false)
	public SecondsChronoUnit getDateIntervalUnit() {
		return dateIntervalUnit;
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
            return true;
        }
		if (other!=null && other instanceof FacilioModule ) {
			return this.name.equals(((FacilioModule)other).name);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Objects.hashCode(this.name);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder()
					.append("Module [")
					.append("name : ").append(name).append(", ")
					.append("id : ").append(moduleId)
					.append("]")
					.toString()
					;
					
	}
	
	public boolean isParentOrChildModule(FacilioModule module) {
		if (module == null) {
			return false;
		}
		
		if (checkHierarchy(this, module)) {
			return true;
		}
		
		if (checkHierarchy(module, this)) {
			return true;
		}
		
		return false;
	}
	
	private static boolean checkHierarchy(FacilioModule derivedModule, FacilioModule baseModule) {
		FacilioModule extendedModule = derivedModule;
		while (extendedModule != null) {
			if (extendedModule.equals(baseModule)) {
				return true;
			}
			extendedModule = extendedModule.getExtendModule();
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
		
		SCHEDULED_FORMULA,
		LIVE_FORMULA,
		SYSTEM_SCHEDULED_FORMULA,
		
		CUSTOM,
		PREDICTED_READING,
		ACTIVITY,
		ML_FORECASTING_DATA
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

	@JsonIgnore
	public FacilioModule getParentModule() {
		FacilioModule module = this;
		while (module.getExtendModule() != null) {
			module = module.getExtendModule();
		}
		return module;
	}
}
