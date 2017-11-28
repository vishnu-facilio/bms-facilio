package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ActivityType {
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
	ASSIGN_TICKET(16),
	ADD_TICKET_NOTE(32),
	CLOSE_WORK_ORDER(64)
	;

    private int eventType;

    ActivityType(int eventType) {
        this.eventType = eventType;
    }

    public int getValue() {
        return eventType;
    }
    
    public static ActivityType valueOf(int eventTypeVal) {
    	return typeMap.get(eventTypeVal);
    }
    
    private static final Map<Integer, ActivityType> typeMap = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, ActivityType> initTypeMap() {
		Map<Integer, ActivityType> typeMap = new HashMap<>();
		
		for(ActivityType type : values()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
	public Map<Integer, ActivityType> getAllTypes() {
		return typeMap;
	}
}
