package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class TicketStatusContext extends ModuleBaseWithCustomFields {
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	private StatusType type;
	public StatusType getType() {
		return type;
	}
	public int getTypeCode() {
		if(type != null) {
			return type.getIntVal();
		}
		else {
			return 0;
		}
	}
	public void setTypeCode(int type) {
		this.type = StatusType.typeMap.get(type);
	}
	
	
	public static enum StatusType {
		REQUESTED(5, "Requested"),
		OPEN(1, "Open"),
		PENDING(2, "Pending"),
		RESOLVED(3, "Resolved"),
		CLOSED(4, "Closed"),
		INCOMPLETE(6, "Incomplete")
		;
		
		private int intVal;
		private String strVal;
		
		private StatusType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, StatusType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, StatusType> initTypeMap() {
			Map<Integer, StatusType> typeMap = new HashMap<>();
			
			for(StatusType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, StatusType> getAllTypes() {
			return typeMap;
		}
	}
}
