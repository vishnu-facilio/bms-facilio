package com.facilio.bmsconsoleV3.context.floorplan;

import com.facilio.bmsconsoleV3.context.V3DepartmentContext;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.v3.context.V3Context;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class V3DeskContext  extends V3SpaceContext {
	
	private V3EmployeeContext employee;
	
	public V3DepartmentContext getDepartment() {
		return department;
	}
	public void setDepartment(V3DepartmentContext department) {
		this.department = department;
	}


	private V3DepartmentContext department;
	

    public V3EmployeeContext getEmployee() {
		return employee;
	}
	public void setEmployee(V3EmployeeContext employee) {
		this.employee = employee;
	}


	private String deskCode;
	
	private Boolean isActive;
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Boolean getIsArchived() {
		return isArchived;
	}
	public void setIsArchived(Boolean isArchived) {
		this.isArchived = isArchived;
	}


	private Boolean isArchived;



	public String getDeskCode() {
		return deskCode;
	}
	public void setDeskCode(String deskCode) {
		this.deskCode = deskCode;
	}


    private DeskType deskType;

    public int getDeskType() {
		if(deskType != null) {
			return deskType.getIntVal();
		}
		else {
			return -1;
		}
	}
	public String getDeskTypeVal() {
		if(deskType != null) {
			return deskType.getStringVal();
		}
		return null;
	}
	public void setDeskType(int type) {
		this.deskType = deskType.typeMap.get(type);
	}
	public void setDeskType(DeskType type) {
		this.deskType = type;
	}
	public DeskType getFileSourceEnum() {
		return deskType;
	}
	public static enum DeskType {
		
		ASSIGNES(1, "Assigned"),
		HOTEL(2, "Hotel"),
        HOT(3, "HOT");

		private int intVal;
		private String strVal;
		
		private DeskType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, DeskType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, DeskType> initTypeMap() {
			Map<Integer, DeskType> typeMap = new HashMap<>();
			
			for(DeskType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, DeskType> getAllTypes() {
			return typeMap;
		}
	}

}