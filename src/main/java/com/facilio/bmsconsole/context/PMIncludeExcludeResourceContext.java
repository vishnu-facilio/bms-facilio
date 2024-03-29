package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class PMIncludeExcludeResourceContext implements Serializable {

	private static final long serialVersionUID = 1L;
	
	Long id;
	Long pmId;
	Long taskSectionTemplateId;
	Long taskTemplateId;
	Long resourceId;
	Boolean isInclude;

	int parentType;
	
	public int getParentType() {
		return parentType;
	}
	public void setParentType(int parentType) {
		this.parentType = parentType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPmId() {
		return pmId;
	}
	public void setPmId(Long pmId) {
		this.pmId = pmId;
	}
	public Long getTaskSectionTemplateId() {
		return taskSectionTemplateId;
	}
	public void setTaskSectionTemplateId(Long taskSectionTemplateId) {
		this.taskSectionTemplateId = taskSectionTemplateId;
	}
	public Long getTaskTemplateId() {
		return taskTemplateId;
	}
	public void setTaskTemplateId(Long taskTemplateId) {
		this.taskTemplateId = taskTemplateId;
	}
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	public boolean getIsInclude() {
		if(isInclude == null) {
			return false;
		}
		return isInclude;
	}
	public void setIsInclude(Boolean isInclude) {
		this.isInclude = isInclude;
	}
	
	public void setIsInclude(boolean isInclude) {
		this.isInclude = isInclude;
	}
	
	
	public static enum ParentType {
		
		PM, 
		TASK_SECTION_TEMPLATE,
		TASK_TEMPLATE,
		;
		public int getVal() {
			return ordinal() + 1;
		}
		private static final ParentType[] PARENT_TYPES = ParentType.values();
		public static ParentType valueOf(int type) {
			if (type > 0 && type <= PARENT_TYPES.length) {
				return PARENT_TYPES[type - 1];
			}
			return null;
		}
	}
}
