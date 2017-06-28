package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.util.TaskAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class TaskListAction extends ActionSupport {
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		moduleName = CFUtil.getModuleName("Tasks_Objects", orgId);
		tasks = TaskAPI.getAllTasksOfOrg(orgId);
		
		return SUCCESS;
	}
	
	private List<TaskContext> tasks;
	public List<TaskContext> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskContext> tasks) {
		this.tasks = tasks;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
