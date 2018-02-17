package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.leed.context.PMTriggerContext;

public class PreventiveMaintenance {
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	private Boolean status;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}

	public boolean isActive() {
		if (status != null) {
			return status.booleanValue();
		}
		return false;
	}

	private User createdBy;
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	private long createdById = -1;
	public long getCreatedById() {
		return createdById;
	}
	public void setCreatedById(long createdById) {
		this.createdById = createdById;
	}

	private User modifiedBy;
	public User getModifiedBy() {
		if (modifiedBy != null) {
			return modifiedBy;
		} else {
			return createdBy;
		}
	}
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	private long modifiedById = -1;
	public long getModifiedById() {
		return modifiedById;
	}
	public void setModifiedById(long modifiedById) {
		this.modifiedById = modifiedById;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	private long lastModifiedTime = -1;
	public long getLastModifiedTime() {
		if (lastModifiedTime != -1) {
			return lastModifiedTime;
		} else {
			return createdTime;
		}
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	private long templateId = -1;
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private long spaceId = -1;
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}

	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}

	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}

	private long assignedToid = -1;
	public long getAssignedToid() {
		return assignedToid;
	}
	public void setAssignedToid(long assignedToid) {
		this.assignedToid = assignedToid;
	}

	private long assignmentGroupId = -1;
	public long getAssignmentGroupId() {
		return assignmentGroupId;
	}
	public void setAssignmentGroupId(long assignmentGroupId) {
		this.assignmentGroupId = assignmentGroupId;
	}

	private long typeId = -1;
	public long getTypeId() {
		return typeId;
	}
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	private List<WorkOrderContext> workorders;
	public List<WorkOrderContext> getWorkorders() {
		return workorders;
	}
	public void setWorkorders(List<WorkOrderContext> workorders) {
		this.workorders = workorders;
	}

	private Boolean waitForAllTriggers;
	public Boolean getWaitForAllTriggers() {
		return waitForAllTriggers;
	}
	public void setWaitForAllTriggers(Boolean waitForAllTriggers) {
		this.waitForAllTriggers = waitForAllTriggers;
	}
	public boolean waitForAllTriggers() {
		if (waitForAllTriggers != null) {
			return waitForAllTriggers.booleanValue();
		}
		return false;
	}

	private List<PMTriggerContext> triggers;
	public List<PMTriggerContext> getTriggers() {
		return triggers;
	}
	public void setTriggers(List<PMTriggerContext> triggers) {
		this.triggers = triggers;
	}

	private TriggerType triggerType;
	public int getTriggerType() {
		if (triggerType != null) {
			return triggerType.getVal();
		}
		return -1;
	}
	public void setTriggerType(int triggerType) {
		this.triggerType = TriggerType.valueOf(triggerType);
	}
	public TriggerType getTriggerTypeEnum() {
		return triggerType;
	}
	public void setTriggerType(TriggerType triggerType) {
		this.triggerType = triggerType;
	}

	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	private int currentExecutionCount = 0;
	public int getCurrentExecutionCount() {
		return currentExecutionCount;
	}
	public void setCurrentExecutionCount(int currentExecutionCount) {
		this.currentExecutionCount = currentExecutionCount;
	}

	private int maxCount = -1;
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	
	private long nextExecutionTime = -1;
	public long getNextExecutionTime() {
		return nextExecutionTime;
	}
	public void setNextExecutionTime(long nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}
	
	public String getScheduleMsg() {
		StringBuilder msg = new StringBuilder();
		boolean isFirst = true;
		if(triggers != null && !triggers.isEmpty()) {
			for(PMTriggerContext trigger : triggers) {
				if(trigger.getScheduleMsg() != null) {
					if(isFirst) {
						isFirst = false;
					}
					else {
						msg.append(" or ");
					}
					msg.append(trigger.getScheduleMsg());
				}
			}
			return msg.toString();
		}
		return null;
	}
	
	public boolean hasTriggers() {
		return triggerType != TriggerType.NONE;
	}

	private static final TriggerType[] TRIGGER_TYPES = TriggerType.values();
	public static enum TriggerType {
		ONLY_SCHEDULE_TRIGGER, 
		FLOATING, 
		FIXED,
		NONE;

		public int getVal() {
			return ordinal() + 1;
		}

		public static TriggerType valueOf(int type) {
			return TRIGGER_TYPES[type - 1];
		}
	}
}
