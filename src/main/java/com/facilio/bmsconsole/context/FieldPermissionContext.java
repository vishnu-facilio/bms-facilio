package com.facilio.bmsconsole.context;

import java.io.Serializable;

import com.facilio.modules.FacilioIntEnum;

public class FieldPermissionContext implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long orgId;
	private long moduleId;
	private long fieldId;
	private long roleId;
	private PermissionType permissionType;
	private long subModuleId;
	private CheckType checkType;
	
	
	public static enum PermissionType implements FacilioIntEnum {
		READ_WRITE, READ_ONLY, DO_NOT_SHOW;

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static PermissionType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	public int getPermissionType() {
		if (permissionType != null) {
			return permissionType.getIndex();
		}
		return -1;
		
	}
	public void setPermissionType(int permissionType) {
		this.permissionType = PermissionType.valueOf(permissionType);
	}
	public PermissionType getPermissionTypeEnum() {
		return permissionType;
	}
	public void setPermissionType(PermissionType permissionType) {
		this.permissionType = permissionType;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public long getSubModuleId() {
		return subModuleId;
	}
	public void setSubModuleId(long subModuleId) {
		this.subModuleId = subModuleId;
	}
	
	public static enum CheckType implements FacilioIntEnum {
		FIELD, MODULE;

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static CheckType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	public int getCheckType() {
		if (checkType != null) {
			return checkType.getIndex();
		}
		return -1;
		
	}
	public void setCheckType(int checkType) {
		this.checkType = CheckType.valueOf(checkType);
	}
	public CheckType getCheckTypeEnum() {
		return checkType;
	}
	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}

	
}
