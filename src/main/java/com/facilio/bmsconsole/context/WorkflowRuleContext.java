package com.facilio.bmsconsole.context;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioContext;

public class WorkflowRuleContext {
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long workflowId;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private String module;
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	private EventType eventType = EventType.CREATE;
	public EventType getEventType() {
		return eventType;
	} 
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	private JSONObject conditions;
	public JSONObject getConditions() {
		return conditions;
	}
	public void setConditions(JSONObject conditions) {
		this.conditions = conditions;
	}
	
	private boolean isActive = true;
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	private int executionOrder = 1;
	public int getExecutionOrder() {
		return executionOrder;
	}
	public void setExecutionOrder(int executionOrder) {
		this.executionOrder = executionOrder;
	}
	
	private JSONArray actions;
	public JSONArray getActions() {
		return actions;
	}
	public void setActions(JSONArray actions) {
		this.actions = actions;
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
