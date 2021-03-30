package com.facilio.trigger.context;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	@JsonInclude
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
	public void setType(TriggerType type) {
		this.type = type;
	}

	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	private Boolean isDefault;
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public Boolean isDefault() {
		if (isDefault == null) {
			return false;
		}
		return isDefault;
	}

	private Boolean internal;
	@JsonInclude
	public Boolean getInternal() {
		return internal;
	}
	public void setInternal(Boolean internal) {
		this.internal = internal;
	}
	public Boolean isInternal() {
		if (internal == null) {
			return false;
		}
		return internal;
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
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	private Boolean status;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Boolean isActive() {
		if (status == null) {
			return false;
		}
		return status;
	}

	List<TriggerActionContext> triggerActions;
	public List<TriggerActionContext> getTriggerActions() {
		return triggerActions;
	}
	public void setTriggerActions(List<TriggerActionContext> triggerActions) {
		this.triggerActions = triggerActions;
	}
	
	public void addTriggerAction(TriggerActionContext action) {
		if(triggerActions == null) { triggerActions = new ArrayList<TriggerActionContext>();}
		
		triggerActions.add(action);
	}

	public boolean shouldInvoke() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BaseTriggerContext that = (BaseTriggerContext) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
