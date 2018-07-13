package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.exception.ReadingValidationException;
import com.opensymphony.xwork2.ActionSupport;

public class TaskAction extends ActionSupport {

	private static Logger log = LogManager.getLogger(TaskAction.class.getName());
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
	
	private boolean doValidation;
	public boolean getDoValidation() {
		return doValidation;
	}
	
	public void setDoValidation(boolean doValidation) {
		this.doValidation = doValidation;
	}
	
	private TaskSectionContext section;
	public TaskSectionContext getSection() {
		return section;
	}
	public void setSection(TaskSectionContext section) {
		this.section = section;
	}
	
	public String addTaskSection() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TASK_SECTION, section);
		
		Chain addSectionChain = FacilioChainFactory.addTaskSectionChain();
		addSectionChain.execute(context);
		
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
//		boolean doValidation = getDoValidation();
		if (AccountUtil.getCurrentAccount().getDeviceType() != null) {
			context.put(FacilioConstants.ContextNames.DO_VALIDTION, getDoValidation());
		}
//		context.put(FacilioConstants.ContextNames.DO_VALIDTION, doValidation);
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
		context.put(FacilioConstants.ContextNames.SKIP_LAST_READING_CHECK, true);
		Map<Long, Map<String, String>> errorMap = new HashMap<>();
		Chain updateTask = FacilioChainFactory.getUpdateTaskChain();
		try {
			updateTask.execute(context);
		} catch (ReadingValidationException ex) {
			Map<String, String> msgMap = new HashMap<>();
			msgMap.put("message", ex.getMessage());
			msgMap.put("evaluator", ex.getResultEvaluator());
			errorMap.put(ex.getReadingFieldId(), msgMap);
			setError(errorMap);
		}
		Integer count = (Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		if (count != null) {
			rowsUpdated = count;
		}
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
	
	private Map<Long, Map<String, String>> error;
	
	public String closeAllTask() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		if (taskIdList != null) {
			TaskContext defaultClosedTaskObj = new TaskContext();
			defaultClosedTaskObj.setParentTicketId(parentTicketId);
			defaultClosedTaskObj.setStatus(TicketAPI.getStatus("Closed"));
			context.put(FacilioConstants.ContextNames.TASK, defaultClosedTaskObj);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, taskIdList);
			context.put(FacilioConstants.ContextNames.IS_BULK_ACTION, true);
			context.put(FacilioConstants.ContextNames.PARENT_ID, parentTicketId);
			Chain updateTask = FacilioChainFactory.getUpdateTaskChain();
			updateTask.execute(context);
			rowsUpdated += (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		}
		return SUCCESS;
	}
	public String updateAllTask() throws Exception {
		FacilioContext context = new FacilioContext();
		Map<Long, Map<String, String>> errorMap = new HashMap<>();
		for (TaskContext singleTask :taskContextList)
		{
			context.clear();
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
			context.put(FacilioConstants.ContextNames.TASK, singleTask);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(singleTask.getId()));
			if (AccountUtil.getCurrentAccount().getDeviceType() != null) {
				context.put(FacilioConstants.ContextNames.DO_VALIDTION, getDoValidation());
			}
			context.put(FacilioConstants.ContextNames.SKIP_LAST_READING_CHECK, true);
			Chain updateTask = FacilioChainFactory.getUpdateTaskChain();
			try {
				updateTask.execute(context);
			} catch (ReadingValidationException ex) {
				Map<String, String> msgMap = new HashMap<>();
				msgMap.put("message", ex.getMessage());
				msgMap.put("evaluator", ex.getResultEvaluator());
				errorMap.put(singleTask.getId(), msgMap);
				setError(errorMap);
			}
			Object count = context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
			if (count != null) {
				rowsUpdated += (int) count;
			}
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
				log.info("Exception occurred ", e);
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
	public Map<Long, Map<String, String>> getError() {
		return error;
	}
	public void setError(Map<Long, Map<String, String>> error) {
		this.error = error;
	}
	
	
	
	
	
	
/******************      V2 Api    ******************/
	
	private int responseCode = 0;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	private JSONObject result;
	public JSONObject getResult() {
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public void setResult(String key, Object result) {
		if (this.result == null) {
			this.result = new JSONObject();
		}
		this.result.put(key, result);			
	}
	
	public String v2viewTask() {
		try {
			String response = viewTask();
			setResult(FacilioConstants.ContextNames.TASK, task);
			return response;
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(FacilioConstants.ERROR_MESSAGE);
			return ERROR;
		}
	}
	
	public String v2updateStatus() {
		try {
			String response = updateStatus();
			setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
			return response;
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(FacilioConstants.ERROR_MESSAGE);
			return ERROR;
		}
	}
	
	public String v2closeAllTask() {
		try {
			String response = closeAllTask();
			setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
			return response;
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(FacilioConstants.ERROR_MESSAGE);
			return ERROR;
		}
	}
	
	public String v2updateAllTask() {
		try {
			String response = updateAllTask();
			setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
			setResult("error", getError());
			return response;
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(FacilioConstants.ERROR_MESSAGE);
			return ERROR;
		}
	}
	
	public String v2updateTask() {
		try {
			String response = updateTask();
			setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
			setResult("error", getError());
			return response;
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(FacilioConstants.ERROR_MESSAGE);
			return ERROR;
		}
	}
	
	public String v2taskList() {
		try {
			String response = taskList();
			setResult(FacilioConstants.ContextNames.TASK_LIST, tasks);
			setResult("sections", getSections());
			return response;
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(FacilioConstants.ERROR_MESSAGE);
			return ERROR;
		}
	}
	
 }

