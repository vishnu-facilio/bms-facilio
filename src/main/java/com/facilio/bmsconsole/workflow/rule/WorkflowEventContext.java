package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.io.Serializable;

public class WorkflowEventContext implements Serializable {
	private static final long serialVersionUID = 1L;
	
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
	public FacilioModule getModule() throws Exception {
		if(module == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(moduleId > 0) {
				return modBean.getModule(moduleId);
			}
			else if(moduleName != null) {
				return modBean.getModule(moduleName);
			}
			return null;
			
		}
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
		this.activityType = EventType.valueOf(activityType);
	}
	
	private EventType activityType;
	public void setActivityType(EventType activityType) {
		this.activity = activityType.getValue();
		this.activityType = activityType;
	}
	public EventType getActivityTypeEnum() {
		return activityType;
	}
}
