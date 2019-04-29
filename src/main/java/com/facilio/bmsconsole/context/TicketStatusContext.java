package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class TicketStatusContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private String displayName;
	
	public String getDisplayName() {
		// return displayName;
		if(displayName != null && !displayName.isEmpty()) {
			return displayName;
		}
		else {
			return status;
		}
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	
	private Boolean timerEnabled;
	public Boolean getTimerEnabled() {
		if (timerEnabled == null) {
			return false;
		}
		return timerEnabled;
	}
	public void setTimerEnabled(Boolean timerEnabled) {
		this.timerEnabled = timerEnabled;
	}
	
	private Boolean recordLocked;
	public Boolean getRecordLocked() {
		return recordLocked;
	}
	public void setRecordLocked(Boolean recordLocked) {
		this.recordLocked = recordLocked;
	}
	
	private boolean requestedState;
	public boolean isRequestedState() {
		return requestedState;
	}
	public void setRequestedState(boolean requestedState) {
		this.requestedState = requestedState;
	}

	@Override
	public String toString() {
		return status;
	}
	
	public static enum StatusType {
		OPEN(1, "Open"),
		CLOSED(2, "Closed"),
		PRE_OPEN(3, "Pre-Open"),
		REQUESTED(4, "Requested"),
		REJECTED(5, "Rejected")
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

	public boolean shouldChangeTimer(TicketStatusContext oldState) {
		// PRE_OPEN doesn't have timer field
		if (getType() == StatusType.PRE_OPEN) {
			return false;
		}
		
		if (oldState == null) {
			return getTimerEnabled();
		}
		
		return !(oldState.getTimerEnabled() == getTimerEnabled());
	}
}
