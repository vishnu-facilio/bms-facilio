package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.TaskStatusContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class TaskStatusAction extends ActionSupport {
	
	public String statusList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		Chain statusListChain = FacilioChainFactory.getTaskStatusListChain();
		statusListChain.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setStatuses((List<TaskStatusContext>) context.get(FacilioConstants.ContextNames.TASK_STATUS_LIST));
		
		return SUCCESS;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private List<TaskStatusContext> statuses = null;
	public List<TaskStatusContext> getStatuses() {
		return statuses;
	}
	public void setStatuses(List<TaskStatusContext> statuses) {
		this.statuses = statuses;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.TASK_STATUS;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewTaskStatusLayout();
	}
	
	public List<TaskStatusContext> getRecords() 
	{
		return statuses;
	}
}
