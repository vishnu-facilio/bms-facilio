package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EventContext {
	private long eventId;
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long moduleId;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private EventType eventType;
	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(int eventType) {
		this.eventType = EventType.valueOf(eventType);
	}
	
	public static enum EventType {
	    
		/* Bitwise calculation
		 * 
		 * var workflowRule = currentWorkflowRule;
		 * 
		 * If ((workflowRule.eventType & CREATE) == CREATE) {
		 * 		Workflow rule needs to be executed for create action
		 * } 
		 * 
		 */
		CREATE(1),
		EDIT(2),
		DELETE(4),
		CREATE_OR_EDIT(CREATE.getValue() + EDIT.getValue()),
		APPROVE_WORK_ORDER_REQUEST(8),
		ASSIGN_ALARM(16)
		;

	    private int eventType;

	    EventType(int eventType) {
	        this.eventType = eventType;
	    }

	    public int getValue() {
	        return eventType;
	    }
	    
	    public static EventType valueOf(int eventTypeVal) {
	    	return typeMap.get(eventTypeVal);
	    }
	    
	    private static final Map<Integer, EventType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, EventType> initTypeMap() {
			Map<Integer, EventType> typeMap = new HashMap<>();
			
			for(EventType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
		public Map<Integer, EventType> getAllTypes() {
			return typeMap;
		}
	}
}
