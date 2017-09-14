package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class TaskAction extends ActionSupport {
	
	//New Task Props
	public String newTask() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain newTask = FacilioChainFactory.getNewTaskChain();
		newTask.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		return SUCCESS;
	}
	
	private List<FacilioField> fields;
	
	public List getFormlayout()
	{
		return FormLayout.getNewTicketLayout(fields);
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private ActionForm actionForm;
	public ActionForm getActionForm() {
		return actionForm;
	}
	public void setActionForm(ActionForm actionForm) {
		this.actionForm = actionForm;
	}
	
	private List<Long> attachmentId;
	public List<Long> getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(List<Long> attachmentId) {
		this.attachmentId = attachmentId;
	}
	
	//Add Task Props
	public String addTask() throws Exception {
		// TODO Auto-generated method stub
		
		if(task == null) {
			task = new TaskContext();
		}
		task.setTicket(ticket);
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TASK, task);
		context.put(FacilioConstants.ContextNames.TICKET, ticket);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, getAttachmentId());
		
		Chain addTask = FacilioChainFactory.getAddTaskChain();
		addTask.execute(context);
		
		setTaskId(task.getId());
		
		return SUCCESS;
	}
	
	//View Task Props
	public String viewTask() throws Exception {
		// TODO Auto-generated method stub
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getTaskId());
		
		Chain getTaskChain = FacilioChainFactory.getTaskDetailsChain();
		getTaskChain.execute(context);
		
		setTask((TaskContext) context.get(FacilioConstants.ContextNames.TASK));
		
		return SUCCESS;
	}
	
	private TicketContext ticket;
	public TicketContext getTicket() {
		return ticket;
	}
	public void setTicket(TicketContext ticket) {
		this.ticket = ticket;
	}
	
	private TaskContext task;
	public TaskContext getTask() {
		return task;
	}
	public void setTask(TaskContext task) {
		this.task = task;
	}
	
	private long taskId;
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	
	//Task List
	public String taskList() throws Exception {
		// TODO Auto-generated method stub
		
//		if (UserInfo.getCurrentUser().getRole().hasPermission(FacilioConstants.Permission.TASK_ACCESS_READ_ANY)) {
//			this.viewName = null;
//		}
//		else if (UserInfo.getCurrentUser().getRole().hasPermission(FacilioConstants.Permission.TASK_ACCESS_READ_OWN)) {
//			this.viewName = "mytasks";
//		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		
		Chain taskListChain = FacilioChainFactory.getTaskListChain();
		taskListChain.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setTasks((List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST));
		
		FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if(cv != null) {
			setViewDisplayName(cv.getDisplayName());
		}
		
		return SUCCESS;
	}
	
	private List<TaskContext> tasks;
	public List<TaskContext> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskContext> tasks) {
		this.tasks = tasks;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.TASK;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewTaskLayout();
	}
	
	public List<TaskContext> getRecords() 
	{
		return tasks;
	}
	
	private String viewName = null;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	private String displayName = "All Tasks";
	public String getViewDisplayName() {
		return displayName;
	}
	public void setViewDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public RecordSummaryLayout getRecordSummaryLayout()
	{
		return RecordSummaryLayout.getRecordSummaryTaskLayout();
	}
	
	public TaskContext getRecord() 
	{
		return task;
	}
 }

