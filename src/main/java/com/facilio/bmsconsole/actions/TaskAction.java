package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;
import com.twilio.sdk.resource.taskrouter.v1.workspace.Task;

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
	
	private TaskSectionContext section;
	public TaskSectionContext getSection() {
		return section;
	}
	public void setSection(TaskSectionContext section) {
		this.section = section;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public String addTaskSection() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TASK_SECTION, section);
		
		Chain addSectionChain = FacilioChainFactory.addTaskSectionChain();
		addSectionChain.execute(context);
		setResult("success");
		
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
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TASK, task);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, getAttachmentId());
		
		Chain addTask = FacilioChainFactory.getAddTaskChain();
		addTask.execute(context);
		
		setTaskId(task.getId());
		
		return SUCCESS;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}
	
	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}
	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	
	public String assignTask() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ASSIGN_TICKET);
		return updateTask(context);
	}
	
	public String updateStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		return updateTask(context);
	}
	
	public String updateTask() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		return updateTask(context);
	}
	public String addTaskInput() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ADD_TASK_INPUT);
		return updateTask(context);
	}
	
	private String updateTask(FacilioContext context) throws Exception {
		context.put(FacilioConstants.ContextNames.TASK, task);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		Chain updateTask = FacilioChainFactory.getUpdateTaskChain();
		updateTask.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		return SUCCESS;
	}
	List<TaskContext> taskContextList;
	public List<TaskContext> getTaskContextList() {
		return taskContextList;
	}
	public void setTaskContextList(List<TaskContext> taskContextList) {
		this.taskContextList = taskContextList;
	}
	List<Long> taskIdList;

	public List<Long> getTaskIdList() {
		return taskIdList;
	}
	public void setTaskIdList(List<Long> taskIdList) {
		this.taskIdList = taskIdList;
	}

	Long parentTicketId;
	public Long getParentTicketId() {
		return parentTicketId;
	}
	public void setParentTicketId(Long parentTicketId) {
		this.parentTicketId = parentTicketId;
	}
	public String closeAllTask() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		if (taskIdList != null) {
			TaskContext defaultClosedTaskObj = new TaskContext();
			defaultClosedTaskObj.setParentTicketId(parentTicketId);
			defaultClosedTaskObj.setStatus(TicketAPI.getStatus("Closed"));
			context.put(FacilioConstants.ContextNames.TASK, defaultClosedTaskObj);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, taskIdList);
			Chain updateTask = FacilioChainFactory.getUpdateTaskChain();
			updateTask.execute(context);
			rowsUpdated += (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		}
		return SUCCESS;
	}
	public String updateAllTask() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		System.out.println(taskContextList.size());
		for (TaskContext singleTask :taskContextList)
		{
			context.put(FacilioConstants.ContextNames.TASK, singleTask);
			System.out.println(taskContextList.size());
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(singleTask.getId()));
			Chain updateTask = FacilioChainFactory.getUpdateTaskChain();
			updateTask.execute(context);
			rowsUpdated += (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		}
		return SUCCESS;
	}
	
	public String deleteTask() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		
		Command deleteTask = FacilioChainFactory.getDeleteTaskChain();
		deleteTask.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
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
	
	private String module;
	public String getModule() {
		return this.module;
	}
	
	public void setModule(String module) {
		this.module = module;
	}
	
	private long recordId;
	public long getRecordId() {
		return this.recordId;
	}
	
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	//Task List
	public String taskList() throws Exception {
		// TODO Auto-generated method stub

		if (this.module != null && this.recordId > 0 && getViewName() == null) {
			try {
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.MODULE_NAME, this.module);
				context.put(FacilioConstants.ContextNames.RECORD_ID, this.recordId);

				Chain getRelatedTasksChain = FacilioChainFactory.getRelatedTasksChain();
				getRelatedTasksChain.execute(context);

				setTasks((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
				setSections((Map<Long, TaskSectionContext>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
//			if (UserInfo.getCurrentUser().getRole().hasPermission(FacilioConstants.Permission.TASK_ACCESS_READ_ANY)) {
//				this.viewName = null;
//			}
//			else if (UserInfo.getCurrentUser().getRole().hasPermission(FacilioConstants.Permission.TASK_ACCESS_READ_OWN)) {
//				this.viewName = "mytasks";
//			}

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());

			Chain taskListChain = FacilioChainFactory.getTaskListChain();
			taskListChain.execute(context);

			setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
			setTasks((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
			setSections((Map<Long, TaskSectionContext>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS));

			FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
			if(cv != null) {
				setViewDisplayName(cv.getDisplayName());
			}
		}

		return SUCCESS;
	}
	
	private Map<Long, List<TaskContext>> tasks;
	public Map<Long, List<TaskContext>> getTasks() {
		return tasks;
	}
	public void setTasks(Map<Long, List<TaskContext>> tasks) {
		this.tasks = tasks;
	}
	
	private Map<Long, TaskSectionContext> sections;
	public Map<Long, TaskSectionContext> getSections() {
		return sections;
	}
	public void setSections(Map<Long, TaskSectionContext> sections) {
		this.sections = sections;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.TASK;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewTaskLayout();
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

