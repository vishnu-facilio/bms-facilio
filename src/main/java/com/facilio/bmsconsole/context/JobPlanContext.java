package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class JobPlanContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
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
	
	private Map<String, List<TaskContext>> tasks;
	public Map<String, List<TaskContext>> getTasks() {
		return tasks;
	}
	public void setTasks(Map<String, List<TaskContext>> tasks) {
		this.tasks = tasks;
	}
	
	@JsonIgnore
	private List<TaskTemplate> taskTemplates;
	public List<TaskTemplate> getTaskTemplates() {
		return taskTemplates;
	}
	public void setTaskTemplates(List<TaskTemplate> taskTemplates) {
		this.taskTemplates = taskTemplates;
	}
	
	@JsonIgnore
	private List<TaskSectionTemplate> sectionTemplates;
	public List<TaskSectionTemplate> getSectionTemplates() {
		return sectionTemplates;
	}
	public void setSectionTemplates(List<TaskSectionTemplate> sectionTemplates) {
		this.sectionTemplates = sectionTemplates;
	}
	
}
