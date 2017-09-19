package com.facilio.bmsconsole.context;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class AlarmContext extends ModuleBaseWithCustomFields {
	private TicketContext ticket;
	public TicketContext getTicket() {
		return ticket;
	}
	public void setTicket(TicketContext ticket) {
		this.ticket = ticket;
	}
	
	private AlarmStatus status;
	public int getStatus() {
		if(status != null) {
			return status.getIntVal();
		}
		return -1;
	}
	public void setStatus(int status) {
		this.status = AlarmStatus.statusMap.get(status);
	}
	public void setStatus(AlarmStatus status) {
		this.status = status;
	}
	public String getStatusVal() {
		if(status != null) {
			return status.getStringVal();
		}
		return null;
	}
	
	private AlarmType type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = AlarmType.typeMap.get(type);
	}
	public void setType(AlarmType type) {
		this.type = type;
	}
	public String getTypeVal() {
		if(type != null) {
			return type.getStringVal();
		}
		return null;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setCreatedTime(String createdTime) {
		if(createdTime != null && !createdTime.isEmpty()) {
			try {
				this.createdTime = FacilioConstants.HTML5_DATE_FORMAT.parse(createdTime).getTime();
			}
			catch (ParseException e) {
				try {
					this.createdTime = FacilioConstants.HTML5_DATE_FORMAT_1.parse(createdTime).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public static enum AlarmStatus {
		ACTIVE(1, "Active"),
		SUPPRESS(2, "Suppressed"),
		CLEAR(3, "Cleared");
		
		private int intVal;
		private String strVal;
		
		private AlarmStatus(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, AlarmStatus> statusMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, AlarmStatus> initTypeMap() {
			Map<Integer, AlarmStatus> typeMap = new HashMap<>();
			
			for(AlarmStatus type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, AlarmStatus> getAllTypes() {
			return statusMap;
		}
	}
	
	public static enum AlarmType {
		MAINTENANCE(1, "Maintenance"),
		CRITICAL(2, "Critical"),
		LIFE_SAFETY(3, "Life Safety"),
		NORMAL(4, "Normal");
		
		private int intVal;
		private String strVal;
		
		private AlarmType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, AlarmType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, AlarmType> initTypeMap() {
			Map<Integer, AlarmType> typeMap = new HashMap<>();
			
			for(AlarmType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, AlarmType> getAllTypes() {
			return typeMap;
		}
	}
}
