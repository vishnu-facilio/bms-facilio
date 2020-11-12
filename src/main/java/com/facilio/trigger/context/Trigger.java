package com.facilio.trigger.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.controlaction.context.ControlGroupInclExclContext;

public class Trigger {

	long id = -1;
	long orgId = -1;
	String name;
	Trigger_Type type;
	String extrainfo;
	long moduleId = -1;
	long childModuleId = -1;
	long assetCategoryId = -1;
	long fieldId = -1;
	public long getAssetCategoryId() {
		return assetCategoryId;
	}

	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}

	public long getFieldId() {
		return fieldId;
	}

	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}

	EventType eventType;
	Boolean status;
	List<TriggerInclExclContext> inclExclResources;
	
	public List<TriggerInclExclContext> getInclExclResources() {
		return inclExclResources;
	}

	public void setInclExclResources(List<TriggerInclExclContext> inclExclResources) {
		this.inclExclResources = inclExclResources;
	}
	
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
	
	public void addTriggerInclExclResources(TriggerInclExclContext inclExclContext) {
		if(inclExclResources == null) { inclExclResources = new ArrayList<TriggerInclExclContext>();}
		
		inclExclResources.add(inclExclContext);
	}
	
	public List<Long> getActualResourceList() {
		
		List<Long> includedIds = new ArrayList<Long>();
		List<Long> excludedIds = new ArrayList<Long>();
		
		if(this.getInclExclResources() != null && !this.getInclExclResources().isEmpty()) {
			for(TriggerInclExclContext includeExcludeRes :this.getInclExclResources()) {
				if(includeExcludeRes.getIsInclude()) {
					includedIds = includedIds == null ? new ArrayList<>() : includedIds; 
					includedIds.add(includeExcludeRes.getResourceId());
				}
				else {
					excludedIds = excludedIds == null ? new ArrayList<>() : excludedIds;
					excludedIds.add(includeExcludeRes.getResourceId());
				}
			}
		}
		if(excludedIds != null) {
			includedIds.removeAll(excludedIds);
		}
		return includedIds;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		if(type != null) {
			return type.getValue();
		}
		return -1;
	}
	
	public Trigger_Type getTypeEnum() {
		return type;
	}

	public void setType(int type) {
		this.type = Trigger_Type.valueOf(type);
	}

	public String getExtrainfo() {
		return extrainfo;
	}

	public void setExtrainfo(String extrainfo) {
		this.extrainfo = extrainfo;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public long getChildModuleId() {
		return childModuleId;
	}

	public void setChildModuleId(long childModuleId) {
		this.childModuleId = childModuleId;
	}

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
}
