package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.exception.ReadingValidationException;

public class TaskAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		
		long siteId = WorkOrderAPI.getSiteIdForWO(task.getParentTicketId());
		task.setSiteId(siteId);
		
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
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, EventType.ASSIGN_TICKET);
		return updateTask(context);
	}
	
	public String updateStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, EventType.EDIT);
		return updateTask(context);
	}
	
	public String updateTask() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, EventType.EDIT);
//		boolean doValidation = getDoValidation();
		if (AccountUtil.getCurrentAccount().getDeviceType() != null) {
			context.put(FacilioConstants.ContextNames.DO_VALIDTION, getDoValidation());
		}
//		context.put(FacilioConstants.ContextNames.DO_VALIDTION, doValidation);
		return updateTask(context);
	}
	public String addTaskInput() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, EventType.ADD_TASK_INPUT);
		return updateTask(context);
	}
	
	private String updateTask(FacilioContext context) throws Exception {
		if (task.getStatus() != null) {
			TicketStatusContext status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), task.getStatus().getId());
			if (status.getStatus().equals("Submitted")) {
				task.setStatusNew(TaskStatus.OPEN);
			} else {
				task.setStatusNew(TaskStatus.CLOSED); 
			}
		}
		context.put(FacilioConstants.ContextNames.TASK, task);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.SKIP_LAST_READING_CHECK, true);
		Map<Long, Map<String, String>> errorMap = new HashMap<>();
		Chain updateTask = TransactionChainFactory.getUpdateTaskChain();
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
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, EventType.EDIT);
		if (taskIdList != null) {
			TaskContext defaultClosedTaskObj = new TaskContext();
			defaultClosedTaskObj.setParentTicketId(parentTicketId);
			defaultClosedTaskObj.setStatusNew(TaskStatus.CLOSED);
			context.put(FacilioConstants.ContextNames.TASK, defaultClosedTaskObj);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, taskIdList);
			context.put(FacilioConstants.ContextNames.IS_BULK_ACTION, true);
			context.put(FacilioConstants.ContextNames.PARENT_ID, parentTicketId);
			Chain updateTask = TransactionChainFactory.getUpdateTaskChain();
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
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, EventType.EDIT);
			context.put(FacilioConstants.ContextNames.TASK, singleTask);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(singleTask.getId()));
			if (AccountUtil.getCurrentAccount().getDeviceType() != null) {
				context.put(FacilioConstants.ContextNames.DO_VALIDTION, getDoValidation());
			}
			context.put(FacilioConstants.ContextNames.SKIP_LAST_READING_CHECK, true);
			Chain updateTask = TransactionChainFactory.getUpdateTaskChain();
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

		if (this.recordId > 0 && getViewName() == null) {
			try {
				FacilioContext context = new FacilioContext();
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
	
	private Map<Long, Map<String, Object>> taskMap;
	public Map<Long, Map<String, Object>> getTaskMap() {
		return taskMap;
	}
	public void setTaskMap(Map<Long, Map<String, Object>> taskMap) {
		this.taskMap = taskMap;
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
	
	
	public String v2viewTask() throws Exception {
		viewTask();
		setResult(FacilioConstants.ContextNames.TASK, task);
		return SUCCESS;
	}
	
	public String v2updateStatus() throws Exception {
		updateStatus();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
	}
	
	public String v2closeAllTask() throws Exception {
		closeAllTask();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
	}
	
	public String v2updateAllTask() throws Exception {
		updateAllTask();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult("error", getError());
		return SUCCESS;
	}
	
	public String v2updateTask() throws Exception {
		updateTask();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult("error", getError());
		return SUCCESS;
	}
	
	public String v2taskList() throws Exception {
		taskList();
		setResult(FacilioConstants.ContextNames.TASK_LIST, tasks);
		setResult("sections", getSections());
		return SUCCESS;
	}
	
	public String v2multipleTaskList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, this.id);

		Chain getRelatedTasksChain = FacilioChainFactory.getRelatedMultipleTasksChain();
		getRelatedTasksChain.execute(context);

		setTaskMap((Map<Long, Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
		setResult(FacilioConstants.ContextNames.TASK_LIST, getTaskMap());
		
		return SUCCESS;
	}
	
	public String syncOfflineTasks() throws Exception {
		if (lastSyncTime == null || lastSyncTime <= 0 ) {
			throw new IllegalArgumentException("Task last synced time is mandatory");
		}
		Map<Long, Map<String, Object>> errors = new HashMap<>();
		int rowsUpdated = 0; 
		for(TaskContext task: taskContextList) {
			try {
				setTask(task);
				setId(Collections.singletonList(task.getId()));
				
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.LAST_SYNC_TIME, lastSyncTime);
				context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, EventType.EDIT);
				if (AccountUtil.getCurrentAccount().getDeviceType() != null) {
					context.put(FacilioConstants.ContextNames.DO_VALIDTION, getDoValidation());
				}
				updateTask(context);
				rowsUpdated += this.rowsUpdated;
			}
			catch(Exception e) {
				Map<String, Object> obj = new HashMap<>();
				obj.put("data", FieldUtil.getAsJSON(task).toJSONString());
				obj.put("error", e.getMessage());
				errors.put(task.getId(), obj);
				log.error("Error occurred on task sync for taskId - " + task.getId() , e);
			}
		}
		if (!errors.isEmpty()) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.LAST_SYNC_TIME, lastSyncTime);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
			context.put(FacilioConstants.ContextNames.CUSTOM_OBJECT, errors);
			
			Chain offlineSync = FacilioChainFactory.addOfflineSyncErrorChain();
			offlineSync.execute(context);
			
			setResult("error", errors.size() + " task(s) sync failed");
		}
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
	}
	
	private Long lastSyncTime;
	public Long getLastSyncTime() {
		return lastSyncTime;
	}
	public void setLastSyncTime(Long lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}
	
 }

