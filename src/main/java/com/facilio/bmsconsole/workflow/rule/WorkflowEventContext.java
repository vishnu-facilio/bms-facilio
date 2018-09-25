package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.modules.FacilioModule;

public class WorkflowEventContext {
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
	
	private long moduleId = -1;
	public long getModuleId() {
		if(moduleId == -1 && module != null) {
			return module.getModuleId();
		}
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private String moduleName;
	public String getModuleName() {
		if(module != null) {
			return module.getName();
		}
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private FacilioModule module;
	public FacilioModule getModule() {
		return module;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}

	private int activity = -1;
	public int getActivityType() {
		return activity;
	}
	public void setActivityType(int activityType) {
		this.activity = activityType;
		this.activityType = ActivityType.valueOf(activityType);
	}
	
	private ActivityType activityType;
	public void setActivityType(ActivityType activityType) {
		this.activity = activityType.getValue();
		this.activityType = activityType;
	}
	public ActivityType getActivityTypeEnum() {
		return activityType;
	}
}
