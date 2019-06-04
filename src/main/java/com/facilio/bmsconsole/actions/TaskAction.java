package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.exception.ReadingValidationException;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class TaskAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(TaskAction.class.getName());
	//New Task Props
	public String newTask() throws Exception {
		
		FacilioContext context = new FacilioContext();
		try {
		Chain newTask = FacilioChainFactory.getNewTaskChain();
		newTask.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("moduleName", getModuleName());
			inComingDetails.put("ActionForm", getActionForm());
			inComingDetails.put("fields", fields);
			sendErrorMail(e, inComingDetails);
			throw e;
		}
		
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
		try {
		context.put(FacilioConstants.ContextNames.TASK_SECTION, section);
		
		Chain addSectionChain = FacilioChainFactory.addTaskSectionChain();
		addSectionChain.execute(context);
		}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("section", section);
			sendErrorMail(e, inComingDetails);
			throw e;
		}
		
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
		try {
		task.setSiteId(siteId);
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TASK, task);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, getAttachmentId());
	    context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
		Chain addTask = FacilioChainFactory.getAddTaskChain();
		addTask.execute(context);
		
		setTaskId(task.getId());
		}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("siteId", siteId);
			inComingDetails.put("task", task);
			inComingDetails.put("AttachmentId", getAttachmentId());
			sendErrorMail(e, inComingDetails);
			throw e;
		}
		
		return SUCCESS;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}

	private boolean preRequestStatus;

	public boolean isPreRequestStatus() {
		return preRequestStatus;
	}

	public void setPreRequestStatus(boolean preRequestStatus) {
		this.preRequestStatus = preRequestStatus;
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
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ASSIGN_TICKET);
		return updateTask(context);
	}
	
	public String updateStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		return updateTask(context);
	}
	
	public String updateTask() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
//		boolean doValidation = getDoValidation();
		if (AccountUtil.getCurrentAccount().getDeviceType() != null) {
			context.put(FacilioConstants.ContextNames.DO_VALIDTION, getDoValidation());
		}
//		context.put(FacilioConstants.ContextNames.DO_VALIDTION, doValidation);
		return updateTask(context);
	}
	public String addTaskInput() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ADD_TASK_INPUT);
		return updateTask(context);
	}
	
	private String updateTask(FacilioContext context) throws Exception {
		try {
		if (task.getStatus() != null) {
			FacilioStatus status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), task.getStatus().getId());
			if (status.getStatus().equals("Submitted")) {
				task.setStatusNew(TaskStatus.OPEN);
			} else {
				task.setStatusNew(TaskStatus.CLOSED); 
			}
		}
		context.put(FacilioConstants.ContextNames.TASK, task);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
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
		}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("Task Status", task.getStatus());
			inComingDetails.put("Task", task);
			inComingDetails.put("RECORD_ID_LIST", id);
			sendErrorMail(e, inComingDetails);
			throw e;
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
		try {
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		if (taskIdList != null) {
			TaskContext defaultClosedTaskObj = new TaskContext();
			defaultClosedTaskObj.setParentTicketId(parentTicketId);
			defaultClosedTaskObj.setStatusNew(TaskStatus.CLOSED);
			context.put(FacilioConstants.ContextNames.TASK, defaultClosedTaskObj);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, taskIdList);
			context.put(FacilioConstants.ContextNames.IS_BULK_ACTION, true);
			context.put(FacilioConstants.ContextNames.PARENT_ID, parentTicketId);
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
			Chain updateTask = TransactionChainFactory.getUpdateTaskChain();
			updateTask.execute(context);
			rowsUpdated += (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
			setModifiedTime(defaultClosedTaskObj.getModifiedTime());
		}
		}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("Task Id List (Close All Tasks) ", taskIdList);
			inComingDetails.put("parentTicketId", parentTicketId);
			sendErrorMail(e, inComingDetails);
			throw e;
		}
		return SUCCESS;
	}
	public String updateAllTask() throws Exception {
		FacilioContext context = new FacilioContext();
		Map<Long, Map<String, String>> errorMap = new HashMap<>();
		try {
		for (TaskContext singleTask :taskContextList)
		{
			context.clear();
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
			context.put(FacilioConstants.ContextNames.TASK, singleTask);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(singleTask.getId()));
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
			if (AccountUtil.getCurrentAccount().getDeviceType() != null) {
				context.put(FacilioConstants.ContextNames.DO_VALIDTION, getDoValidation());
			}
			Chain updateTask;
			try {
					if (singleTask.isPreRequest()) {
						updateTask = TransactionChainFactory.getUpdatePreRequestChain();
					} else {
						updateTask = TransactionChainFactory.getUpdateTaskChain();
					}
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
			List<TaskContext> oldTasks = (List<TaskContext>) context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
			if (!oldTasks.isEmpty()) {
				Long workOrderId = oldTasks.get(0).getParentTicketId();
				preRequestStatus =WorkOrderAPI.getPreRequestStatus(workOrderId);
			}
	}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("taskContextList (update All Tasks) ", taskContextList);
			sendErrorMail(e, inComingDetails);
			throw e;
		}
		return SUCCESS;
	}
	
	public String deleteTask() throws Exception {
		
		FacilioContext context = new FacilioContext();
		try {
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		
		Command deleteTask = FacilioChainFactory.getDeleteTaskChain();
		deleteTask.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("RECORD_ID_LIST (deleteTask)", id);
			inComingDetails.put("ActionForm", getActionForm());
			inComingDetails.put("fields", fields);
			sendErrorMail(e, inComingDetails);
			throw e;
		}
		return SUCCESS;
	}
	
	//View Task Props
	public String viewTask() throws Exception {
		// TODO Auto-generated method stub
		
		FacilioContext context = new FacilioContext();
		try {
		context.put(FacilioConstants.ContextNames.ID, getTaskId());
		
		Chain getTaskChain = FacilioChainFactory.getTaskDetailsChain();
		getTaskChain.execute(context);
		
		setTask((TaskContext) context.get(FacilioConstants.ContextNames.TASK));
		}catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("Task Id", getTaskId());
			inComingDetails.put("Task", getTask());
			sendErrorMail(e, inComingDetails);
			throw e;
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
		try {
		if (this.recordId > 0 && getViewName() == null) {
			try {
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID, this.recordId);

				Chain getRelatedTasksChain = FacilioChainFactory.getRelatedTasksChain();
				getRelatedTasksChain.execute(context);

				setTasks((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
				setPreRequests((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP));
				setSections((Map<Long, TaskSectionContext>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS));
				setPreRequestSections((Map<Long, TaskSectionContext>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS));	
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
		}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("recordId", recordId);
			inComingDetails.put("Task", getTask());
			inComingDetails.put("Section", getSections());
			inComingDetails.put("View Name", getViewName());
			sendErrorMail(e, inComingDetails);
			throw e;
		}

		return SUCCESS;
	}

	private Map<Long, List<TaskContext>> preRequests;

	public Map<Long, List<TaskContext>> getPreRequests() {
		return preRequests;
	}

	public void setPreRequests(Map<Long, List<TaskContext>> preRequests) {
		this.preRequests = preRequests;
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

	private Map<Long, TaskSectionContext> preRequestSections;

	public Map<Long, TaskSectionContext> getPreRequestSections() {
		return preRequestSections;
	}

	public void setPreRequestSections(Map<Long, TaskSectionContext> preRequestSections) {
		this.preRequestSections = preRequestSections;
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
		setResult(FacilioConstants.ContextNames.TASK, task);
		setResult(FacilioConstants.ContextNames.MODIFIED_TIME, task.getModifiedTime());
		return SUCCESS;
	}
	
	public String v2closeAllTask() throws Exception {
		closeAllTask();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult(FacilioConstants.ContextNames.MODIFIED_TIME, modifiedTime);
		return SUCCESS;
	}
	
	public String v2updateAllTask() throws Exception {
		updateAllTask();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult(FacilioConstants.ContextNames.TASK_LIST, taskContextList);
		setResult("error", getError());
		return SUCCESS;
	}
	
	public String v2updateTask() throws Exception {
		updateTask();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult(FacilioConstants.ContextNames.TASK, task);
		setResult(FacilioConstants.ContextNames.MODIFIED_TIME, task.getModifiedTime());
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
		try {
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, this.id);

		Chain getRelatedTasksChain = FacilioChainFactory.getRelatedMultipleTasksChain();
		getRelatedTasksChain.execute(context);

		setTaskMap((Map<Long, Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
		setResult(FacilioConstants.ContextNames.TASK_LIST, getTaskMap());
		}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("RECORD_ID_LIST", id);
			inComingDetails.put("TASK_LIST", getTaskMap());
			sendErrorMail(e, inComingDetails);
			throw e;
		}
		
		return SUCCESS;
	}
	
	public String syncOfflineTasks() throws Exception {
		try {
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
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
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
		}
		catch (Exception e) {
			JSONObject inComingDetails = new JSONObject();
			inComingDetails.put("lastSyncTime", lastSyncTime);
			inComingDetails.put("taskContextList", taskContextList);
			sendErrorMail(e, inComingDetails);
			throw e;
		}
		return SUCCESS;
	}
	
	private Long lastSyncTime;
	public Long getLastSyncTime() {
		return lastSyncTime;
	}
	public void setLastSyncTime(Long lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}
	private void sendErrorMail(Exception e, JSONObject inComingDetails) throws Exception {
		// TODO Auto-generated method stub
		String errorTrace = null;
		StringBuilder body = new StringBuilder("\n\nDetails: \n");
		if (e != null) {
			errorTrace = ExceptionUtils.getStackTrace(e);
			body.append(inComingDetails.toString())
				.append("\nOrgId: ")
				.append(AccountUtil.getCurrentOrg().getOrgId())
				.append("\nUser: ")
				.append(AccountUtil.getCurrentUser().getName()).append(" - ").append(AccountUtil.getCurrentUser().getOuid())
				.append("\nDevice Type: ")
				.append(AccountUtil.getCurrentAccount().getDeviceType())
				.append("\nUrl: ")
				.append(ServletActionContext.getRequest().getRequestURI())
				.append("\n\n-----------------\n\n")
				.append("------------------\n\nStackTrace : \n--------\n")
				.append(errorTrace);
			String message = e.getMessage();
			System.out.println("88888888" + body);
			JSONObject mailJson = new JSONObject();
			mailJson.put("sender", "noreply@facilio.com");
			mailJson.put("to", "shaan@facilio.com, tharani@facilio.com, aravind@facilio.com");
			mailJson.put("subject", "Task Exception");
			mailJson.put("message", body.toString());
			AwsUtil.sendEmail(mailJson);
		}
	}
	
	private long modifiedTime = -1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
 }

