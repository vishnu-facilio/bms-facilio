package com.facilio.bmsconsole.workflow;

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
		CREATE_OR_EDIT(CREATE.getValue() + EDIT.getValue());

	    private int eventType;

	    EventType(int eventType) {
	        this.eventType = eventType;
	    }

	    public int getValue() {
	        return eventType;
	    }
	    
	    public static EventType valueOf(int eventTypeVal) {
	    	if (eventTypeVal == CREATE.getValue()) {
	    		return CREATE;
	    	}
	    	else if (eventTypeVal == EDIT.getValue()) {
	    		return EDIT;
	    	}
	    	else if (eventTypeVal == DELETE.getValue()) {
	    		return DELETE;
	    	}
	    	else if (eventTypeVal == CREATE_OR_EDIT.getValue()) {
	    		return EventType.CREATE_OR_EDIT;
	    	}
	    	return CREATE;
	    }
	}
}
