package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.FacilioFrequency;

public class PreventiveMaintenance extends ResourceContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String getTitle() {
		return getName();
	}
	public void setTitle(String title) {
		setName(title);
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
	
	WorkorderTemplate woTemplate; 
	public WorkorderTemplate getWoTemplate() {
		return woTemplate;
	}
	public void setWoTemplate(WorkorderTemplate woTemplate) {
		this.woTemplate = woTemplate;
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
	
	private FacilioFrequency frequency;
	public int getFrequency() {
		if (frequency != null) {
			return frequency.getValue();
		}
		return -1;
	}
	public void setFrequency(int frequency) {
		this.frequency = FacilioFrequency.valueOf(frequency);
	}
	public void setFrequency(FacilioFrequency frequency) {
		this.frequency = frequency;
	}
	public FacilioFrequency getFrequencyEnum() {
		return frequency;
	}
	
	private Boolean custom;
	public Boolean getCustom() {
		return custom;
	}
	public void setCustom(Boolean custom) {
		this.custom = custom;
	}
	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public boolean isCustom() {
		if (custom != null) {
			return custom.booleanValue();
		}
		return false;
	}
	
	private List<PMReminder> reminders;
	public List<PMReminder> getReminders() {
		return reminders;
	}
	public void setReminders(List<PMReminder> reminders) {
		this.reminders = reminders;
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
			if (type > 0 && type <= TRIGGER_TYPES.length) {
				return TRIGGER_TYPES[type - 1];
			}
			return null;
		}
	}
	
	private long siteId = -1;
	
	public long getSiteId() {
		return this.siteId;
	}
	
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	Long baseSpaceId;
	
	public Long getBaseSpaceId() {
		return baseSpaceId;
	}
	public void setBaseSpaceId(Long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}
	
	Long assetCategoryId;
	Long spaceCategoryId;
	
	public Long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(Long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	public Long getSpaceCategoryId() {
		return spaceCategoryId;
	}
	public void setSpaceCategoryId(Long spaceCategoryId) {
		this.spaceCategoryId = spaceCategoryId;
	}

	PMCreationType pmCreationType;
	
	public int getPmCreationType() {
		if(pmCreationType != null) {
			return pmCreationType.getVal();
		}
		return -1;
	}
	public void setPmCreationType(int pmCreationType) {
		this.pmCreationType = PMCreationType.valueOf(pmCreationType);
	}
	
	PMAssignmentType assignmentType;
	
	public int getAssignmentType() {
		if(assignmentType != null) {
			return assignmentType.getVal();
		}
		return -1;
	}
	public void setAssignmentType(int assignmentType) {
		this.assignmentType = PMAssignmentType.valueOf(assignmentType);
	}

	public static enum PMCreationType {
		
		SINGLE, 
		MULTIPLE, 
		;
		public int getVal() {
			return ordinal() + 1;
		}
		private static final PMCreationType[] CREATION_TYPES = PMCreationType.values();
		public static PMCreationType valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}
	}
	
	List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts;
	
	public List<PMIncludeExcludeResourceContext> getPmIncludeExcludeResourceContexts() {
		return pmIncludeExcludeResourceContexts;
	}
	public void setPmIncludeExcludeResourceContexts(List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts) {
		this.pmIncludeExcludeResourceContexts = pmIncludeExcludeResourceContexts;
	}

	public static enum PMAssignmentType {
		
		ALL_FLOORS,
		ALL_SPACES,
		SPACE_CATEGORY,
		ASSET_CATEGORY,
		CURRENT_ASSET,
		SPECIFIC_ASSET,
		;
		public int getVal() {
			return ordinal() + 1;
		}
		private static final PMAssignmentType[] PM_ASSIGNMENT_TYPES = PMAssignmentType.values();
		public static PMAssignmentType valueOf(int type) {
			if (type > 0 && type <= PM_ASSIGNMENT_TYPES.length) {
				return PM_ASSIGNMENT_TYPES[type - 1];
			}
			return null;
		}
	}
}
