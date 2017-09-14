package com.facilio.bmsconsole.context;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class WorkOrderRequestContext extends ModuleBaseWithCustomFields {
	private TicketContext ticket;
	public TicketContext getTicket() {
		return ticket;
	}
	public void setTicket(TicketContext ticket) {
		this.ticket = ticket;
	}
	
	private RequesterContext requester;
	public RequesterContext getRequester() {
		return requester;
	}
	public void setRequester(RequesterContext requester) {
		this.requester = requester;
	}
	
	private WORUrgency urgency;
	public int getUrgency() {
		if(urgency != null) {
			return urgency.getIntVal();
		}
		return -1;
	}
	public void setUrgency(int urgency) {
		this.urgency = WORUrgency.urgencyMap.get(urgency);
	}
	public void setUrgency(WORUrgency urgency) {
		this.urgency = urgency;
	}
	public String getUrgencyVal() {
		if(urgency != null) {
			return urgency.getStringVal();
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
	
	public static enum WORUrgency {
		HIGH(1, "High"),
		NORMAL(2, "Normal"),
		LOW(3, "Low");
		
		private int intVal;
		private String strVal;
		
		private WORUrgency(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, WORUrgency> urgencyMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, WORUrgency> initTypeMap() {
			Map<Integer, WORUrgency> typeMap = new HashMap<>();
			
			for(WORUrgency type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, WORUrgency> getAllTypes() {
			return urgencyMap;
		}
	}
}
