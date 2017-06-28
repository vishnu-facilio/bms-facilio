package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.GetTaskCommand;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class TaskAction extends ActionSupport {
	
	//New Task Props
	public String newTask() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain newTask = FacilioChainFactory.getNewTaskChain();
		newTask.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		List<FacilioCustomField> cfs = (List<FacilioCustomField>) context.get(FacilioConstants.ContextNames.CUSTOM_FIELDS);
		customFieldNames = new ArrayList<>();
		for(FacilioCustomField field : cfs) {
			customFieldNames.add(field.getFieldName());
		}
		
		return SUCCESS;
	}
	
	private long ticketId;
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
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
	
	private List<String> customFieldNames;
	public List<String> getCustomFieldNames() {
		return customFieldNames;
	}
	public void setCustomFieldNames(List<String> customFieldNames) {
		this.customFieldNames = customFieldNames;
	}
	
	//Add Task Props
	public String addTask() throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		task.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		context.put(FacilioConstants.ContextNames.TASK, task);
		
		if(scheduleObj.getScheduledStart() != 0) {
			scheduleObj.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			context.put(FacilioConstants.ContextNames.SCHEDULE_OBJECT, scheduleObj);
			task.setSchedule(scheduleObj);
		}
		
		Chain addTask = FacilioChainFactory.getAddTaskChain();
		addTask.execute(context);
		
		setTaskId(task.getTaskId());
		
		return SUCCESS;
	}
	
	//View Task Props
	public String viewTask() throws Exception {
		// TODO Auto-generated method stub
		
		FacilioContext context = new FacilioContext();
		context.put(GetTaskCommand.TASK_ID, getTaskId());
		Chain getTaskChain = FacilioChainFactory.getTaskDetailsChain();
		getTaskChain.execute(context);
		
		setTask((TaskContext) context.get(FacilioConstants.ContextNames.TASK));
		setScheduleObj((ScheduleContext) context.get(FacilioConstants.ContextNames.SCHEDULE_OBJECT));
		List<NoteContext> taskNotes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
		if(taskNotes != null && taskNotes.size() > 0) {
			setNotes(taskNotes);
		}
		
		return SUCCESS;
	}
	
	private TaskContext task;
	public TaskContext getTask() {
		return task;
	}
	public void setTask(TaskContext task) {
		this.task = task;
	}
	
	private ScheduleContext scheduleObj;
	public ScheduleContext getScheduleObj() {
		return scheduleObj;
	}
	public void setScheduleObj(ScheduleContext scheduleObj) {
		this.scheduleObj = scheduleObj;
	}
	
	private List<NoteContext> notes;
	public List<NoteContext> getNotes() {
		return notes;
	}
	public void setNotes(List<NoteContext> notes) {
		this.notes = notes;
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
		FacilioContext context = new FacilioContext();
		Chain getAllTasks = FacilioChainFactory.getTaskListChain();
		getAllTasks.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_NAME));
		setTasks((List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST));
		
		return SUCCESS;
	}
	
	private List<TaskContext> tasks;
	public List<TaskContext> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskContext> tasks) {
		this.tasks = tasks;
	}
 }

