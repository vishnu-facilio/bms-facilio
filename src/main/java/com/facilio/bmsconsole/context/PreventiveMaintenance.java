package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.PMStatus;
import com.facilio.bmsconsoleV3.context.jobplan.PMJobPlanContextV3;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;

import lombok.Getter;
import lombok.Setter;

public class PreventiveMaintenance extends ResourceContext {
	
	/**
	 * 
	 */
	public List<PMResourcePlannerContext> getResourcePlanners() {
		return resourcePlanners;
	}
	
	public Map<Long,PMResourcePlannerContext> getResourcePlannersMap() {
		Map<Long,PMResourcePlannerContext> pmPlannerMap = new HashMap<>();
		if(resourcePlanners != null) {
			for(PMResourcePlannerContext resourcePlanner :resourcePlanners) {
				pmPlannerMap.put(resourcePlanner.getResourceId(), resourcePlanner);
			}
		}
		return pmPlannerMap;
	}

	public void setResourcePlanners(List<PMResourcePlannerContext> resourcePlanners) {
		this.resourcePlanners = resourcePlanners;
	}

	List<PMResourcePlannerContext> resourcePlanners;
	
	
	List<PMJobPlanContext> pmjobPlans;
	
	public List<PMJobPlanContext> getPmjobPlans() {
		return pmjobPlans;
	}

	public void setPmjobPlans(List<PMJobPlanContext> pmjobPlans) {
		this.pmjobPlans = pmjobPlans;
	}

	private static final long serialVersionUID = 1L;
	public String getTitle() {
		return getName();
	}
	public void setTitle(String title) {
		setName(title);
	}

	private PMStatus status;
	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(int status) {
		this.status = PMStatus.valueOf(status);
	}
	public void setStatus(PMStatus status) {
		this.status = status;
	}
	public PMStatus getStatusEnum() {
		return status;
	}

	public boolean isActive() {
		if (status != null) {
			return status == PMStatus.ACTIVE;
		}
		return false;
	}

	private Boolean isUserTriggerPresent;
	public boolean isUserTriggerPresent() {
		if (this.isUserTriggerPresent != null) {
			return this.isUserTriggerPresent;
		}
		return false;
	}

	public Boolean getIsUserTriggerPresent() {
		return this.isUserTriggerPresent;
	}

	public void setIsUserTriggerPresent(Boolean isUserTriggerPresent) {
		this.isUserTriggerPresent = isUserTriggerPresent;
	}


	private Boolean isAllowedToExecute;
	public boolean isAllowedToExecute() {
		if (this.isAllowedToExecute != null) {
			return this.isAllowedToExecute;
		}
		return false;
	}

	public Boolean getIsAllowedToExecute() {
		return this.isAllowedToExecute;
	}

	public void setIsAllowedToExecute(Boolean isAllowedToExecute) {
		this.isAllowedToExecute = isAllowedToExecute;
	}


	private Boolean woGenerationStatus;
	public Boolean getwoGenerationStatus() {
		return woGenerationStatus;
	}
	public void setwoGenerationStatus(Boolean woGenerationStatus) {
		this.woGenerationStatus = woGenerationStatus;
	}
	public boolean woGenerationStatus() {
		if (woGenerationStatus != null) {
			return woGenerationStatus.booleanValue();
		}
		return false;
	}

	public boolean isWoGenerating() {
		return woGenerationStatus();
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
	public void addTriggers(PMTriggerContext trigger) {
		this.triggers = this.triggers == null ? new ArrayList<>() : this.triggers;
		this.triggers.add(trigger);
	}
	
	private Map<String,PMTriggerContext> triggerMap;
	private Map<String,PMReminder> reminderMap;

	public Map<String, PMReminder> getReminderMap() {
		return reminderMap;
	}
	public void setReminderMap(Map<String, PMReminder> reminderMap) {
		this.reminderMap = reminderMap;
	}
	public Map<String, PMTriggerContext> getTriggerMap() {
		return triggerMap;
	}
	public void setTriggerMap(Map<String, PMTriggerContext> triggerMap) {
		this.triggerMap = triggerMap;
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

	private long woCreationOffset = -1L;

	private Boolean markIgnoredWo;
	public Boolean getMarkIgnoredWo() { return markIgnoredWo; }
	public void setMarkIgnoredWO(Boolean markIgnoredWO) { this .markIgnoredWo = markIgnoredWO; }
	public boolean markIgnoredWo() {
		if(markIgnoredWo != null){
			return markIgnoredWo.booleanValue();
		}
		return false;
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

	public long getWoGeneratedUpto() {
		return woGeneratedUpto;
	}

	public void setWoGeneratedUpto(long woGeneratedUpto) {
		this.woGeneratedUpto = woGeneratedUpto;
	}

	public long getWoCreationOffset() {
		return woCreationOffset;
	}

	public void setWoCreationOffset(long woCreationOffset) {
		this.woCreationOffset = woCreationOffset;
	}

	private List<SiteContext> siteObjects;
	public List<SiteContext> getSiteObjects() {
		return siteObjects;
	}
	public void setSiteObjects(List<SiteContext> siteObjects) {
		this.siteObjects = siteObjects;
	}

	private TicketCategoryContext ticketCategory;
	public TicketCategoryContext getTicketCategory() {
		return ticketCategory;
	}
	public void setTicketCategory(TicketCategoryContext ticketCategory) {
		this.ticketCategory = ticketCategory;
	}

	private VendorContext vendor;
	public VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}

	private Long lastTriggeredTime;
	public Long getLastTriggeredTime() {
		return lastTriggeredTime;
	}
	public void setLastTriggeredTime(Long lastTriggeredTime) {
		this.lastTriggeredTime = lastTriggeredTime;
	}

	public static enum TriggerType {
		ONLY_SCHEDULE_TRIGGER, 
		FLOATING, 
		FIXED,
		NONE,
		MANUAL;

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

	private List<Long> siteIds;

	public List<Long> getSiteIds() {
		return siteIds;
	}

	public void setSiteIds(List<Long> siteIds) {
		this.siteIds = siteIds;
	}
	
	Long baseSpaceId;
	
	public Long getBaseSpaceId() {
		return baseSpaceId;
	}
	public void setBaseSpaceId(Long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}
	
	private Long assetCategoryId;
	private Long spaceCategoryId;
	
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

	private PMCreationType pmCreationType;
	public int getPmCreationType() {
		if(pmCreationType != null) {
			return pmCreationType.getVal();
		}
		return -1;
	}
	public void setPmCreationType(int pmCreationType) {
		this.pmCreationType = PMCreationType.valueOf(pmCreationType);
	}
	public PMCreationType getPmCreationTypeEnum() {
		return pmCreationType;
	}
	public void PMCreationType(PMCreationType pmCreationType) {
		this.pmCreationType = pmCreationType;
	}
	
	private PMAssignmentType assignmentType;
	public int getAssignmentType() {
		if(assignmentType != null) {
			return assignmentType.getVal();
		}
		return -1;
	}
	public void setAssignmentType(int assignmentType) {
		this.assignmentType = PMAssignmentType.valueOf(assignmentType);
	}
	public PMAssignmentType getAssignmentTypeEnum() {
		return assignmentType;
	}
	public void setAssignmentType(PMAssignmentType assignmentType) {
		this.assignmentType = assignmentType;
	}

	public static enum PMCreationType {
		
		SINGLE, 
		MULTIPLE,
		MULTI_SITE
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
	
	private List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts;
	public List<PMIncludeExcludeResourceContext> getPmIncludeExcludeResourceContexts() {
		return pmIncludeExcludeResourceContexts;
	}
	public void setPmIncludeExcludeResourceContexts(List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts) {
		this.pmIncludeExcludeResourceContexts = pmIncludeExcludeResourceContexts;
	}

	@Getter
	@Setter
	private int pmIncludeExcludeCount;
	
	private Boolean preventOnNoTask;
	
	public boolean isPreventOnNoTask() {
		if (preventOnNoTask == null) {
			return false;
		}
		return preventOnNoTask;
	}
	
	public Boolean getPreventOnNoTask() {
		return preventOnNoTask;
	}

	public void setPreventOnNoTask(Boolean preventOnNoTask) {
		this.preventOnNoTask = preventOnNoTask;
	}

	private Boolean DefaultAllTriggers;

	public boolean isDefaultAllTriggers() {
		if (this.DefaultAllTriggers == null) {
			return false;
		}
		return this.DefaultAllTriggers;
	}

	public void setDefaultAllTriggers(Boolean defaultAllTriggers) {
		this.DefaultAllTriggers = defaultAllTriggers;
	}

	public Boolean getDefaultAllTriggers() {
		return this.DefaultAllTriggers;
	}

	private Boolean enableSkipTriggers;

	public boolean isEnableSkipTriggers() {
		if (this.enableSkipTriggers == null) {
			return false;
		}
		return this.enableSkipTriggers;
	}

	public void setEnableSkipTriggers(Boolean defaultAllTriggers) {
		this.enableSkipTriggers = defaultAllTriggers;
	}

	public Boolean getEnableSkipTriggers() {
		return this.enableSkipTriggers;
	}

	@Getter
	@Setter
	private String pmCategoryDescription;

	@Getter
	@Setter
	private String pmTriggerDescription;

	public static enum PMAssignmentType implements FacilioIntEnum {
		
		ALL_FLOORS("All Floors",FacilioConstants.ContextNames.FLOOR),				//1
		ALL_SPACES("All Spaces",FacilioConstants.ContextNames.SPACE),
		SPACE_CATEGORY("Space Category",FacilioConstants.ContextNames.SPACE),		//3
		ASSET_CATEGORY("Asset Category",FacilioConstants.ContextNames.ASSET),
		CURRENT_ASSET("Current Asset",FacilioConstants.ContextNames.ASSET),			//5
		SPECIFIC_ASSET("Specific Asset",FacilioConstants.ContextNames.ASSET),
		ALL_BUILDINGS("All Buildings",FacilioConstants.ContextNames.BUILDING),		//7
		ALL_SITES("All Sites",FacilioConstants.ContextNames.SITE),
		METER_TYPE("Meter Type",FacilioConstants.Meter.METER)						//9
		;
		String name;
		String moduleName;
		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return this.name;
		}
		
		public String getModuleName() {
			// TODO Auto-generated method stub
			return this.moduleName;
		}
		PMAssignmentType(String name,String moduleName) {
			this.name = name;
			this.moduleName = moduleName;
		}
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

	private long woGeneratedUpto = -1;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder("PM [id : ").append(getId())
											.append(", title : ")
											.append(getName())
											.append("]")
											.toString();
	}

	private List<PMJobPlanContextV3> jobPlanList;

	public List<PMJobPlanContextV3> getJobPlanList() {
		return jobPlanList;
	}

	public void setJobPlanList(List<PMJobPlanContextV3> jobPlanList) {
		this.jobPlanList = jobPlanList;
	}
}
