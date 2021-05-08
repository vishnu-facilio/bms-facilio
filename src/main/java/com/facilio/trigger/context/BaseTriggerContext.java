package com.facilio.trigger.context;

import com.facilio.bmsconsole.instant.jobs.PostTimeseriesWorkflowExecutionJob;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, visible = true, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = BaseTriggerContext.class, name = "1"),
		@JsonSubTypes.Type(value = BaseTriggerContext.class, name = "2"),
		@JsonSubTypes.Type(value = BaseTriggerContext.class, name = "3"),
		@JsonSubTypes.Type(value = BaseTriggerContext.class, name = "4")
})
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

	private boolean validated = false;
	public final boolean isValidated() {
		return validated;
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

	public void validateTrigger() {
		if (StringUtils.isEmpty(getName())) {
			throw new IllegalArgumentException("Trigger name cannot be empty");
		}
		if (getTypeEnum() == null) {
			throw new IllegalArgumentException("Trigger type is not given");
		}
		if (getEventTypeEnum() == null) {
			throw new IllegalArgumentException("Event type cannot be empty");
		}

		switch (getTypeEnum()) {
			case MODULE_TRIGGER:
			case SCORING_RULE_TRIGGER:
			case SLA_DUE_DATE_TRIGGER:
				if (getModuleId() < 0) {
					throw new IllegalArgumentException("Module id is mandatory");
				}
			case TIMESERIES_COMPLETED_TRIGGER:
		}

		validated = true;
	}

	public void handleGet() {}
}
