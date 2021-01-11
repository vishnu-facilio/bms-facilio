package com.facilio.trigger.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.workflow.rule.EventType;

public class BaseTriggerContext {

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private TriggerType type;
	public int getType() {
		if(type != null) {
			return type.getValue();
		}
		return -1;
	}
	public TriggerType getTypeEnum() {
		return type;
	}
	public void setType(int type) {
		this.type = TriggerType.valueOf(type);
	}

	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	private EventType eventType;
	public EventType getEventTypeEnum() {
		return eventType;
	}
	public int getEventType() {
		if(eventType != null) {
			return eventType.getValue();
		}
		return -1;
	}
	public void setEventType(int eventType) {
		this.eventType = EventType.valueOf(eventType);
	}

	private Boolean status;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}

	List<TriggerAction> triggerActions;
	public List<TriggerAction> getTriggerActions() {
		return triggerActions;
	}
	public void setTriggerActions(List<TriggerAction> triggerActions) {
		this.triggerActions = triggerActions;
	}
	
	public void addTriggerAction(TriggerAction action) {
		if(triggerActions == null) { triggerActions = new ArrayList<TriggerAction>();}
		
		triggerActions.add(action);
	}
}
