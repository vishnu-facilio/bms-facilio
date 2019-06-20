package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.tasker.job.JobContext;

public class NotificationConfigContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long parentId;
	private String configModuleName;
	private int notificationType;
	private String scheduleInfo;
	private long actionId;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public String getConfigModuleName() {
		return configModuleName;
	}
	public void setConfigModuleName(String configModuleName) {
		this.configModuleName = configModuleName;
	}
	public int getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}
	public String getScheduleInfo() {
		return scheduleInfo;
	}
	public void setScheduleInfo(String scheduleInfo) {
		this.scheduleInfo = scheduleInfo;
	}
	public long getActionId() {
		return actionId;
	}
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
	
	private List<NotificationUserContext> notificationUsers;
	public List<NotificationUserContext> getNotificationUsers() {
		return notificationUsers;
	}
	public void setNotificationUsers(List<NotificationUserContext> notificationUsers) {
		this.notificationUsers = notificationUsers;
	}
	
	private ActionContext actionContext;
	public ActionContext getActionContext() {
		return actionContext;
	}
	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}
	
	private JobContext jobContext;
	public JobContext getJobContext() {
		return jobContext;
	}
	public void setJobContext(JobContext jobContext) {
		this.jobContext = jobContext;
	}
	
	
	
}
