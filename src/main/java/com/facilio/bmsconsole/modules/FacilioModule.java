	package com.facilio.bmsconsole.modules;

import java.io.Serializable;

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
	
	public static enum SubModuleType {
		MISC(1),
		READING(2),
		PHOTOS(3),
		NOTES(4),
		ATTACHMENTS(5)
		;
		
		private int intVal;
		
		private SubModuleType(int type) {
			// TODO Auto-generated constructor stub
			this.intVal = type;
		}
		
		public int getIntVal() {
			return intVal;
		}
	}
}
