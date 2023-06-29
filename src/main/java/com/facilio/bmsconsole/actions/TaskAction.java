package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3WorkOderAPI;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.wmsv2.handler.AuditLogHandler;
import org.apache.commons.chain.Command;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.context.TaskErrorContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
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

	private File beforeAttachment;
	public File getBeforeAttachment() {
		return beforeAttachment;
	}
	public void setBeforeAttachment(File attachment) {
		this.beforeAttachment = attachment;
	}

	private File afterAttachment;
	public File getAfterAttachment() {
		return afterAttachment;
	}
	public void setAfterAttachment(File attachment) {
		this.afterAttachment = attachment;
	}

	private String beforeAttachmentContentType;
	private String afterAttachmentContentType;

	private String afterAttachmentFileName;

	private String beforeAttachmentFileName;

	private Boolean isTaskActionExecuted = false;
	public Boolean isTaskActionExecuted() {
		return isTaskActionExecuted;
	}
	public void setTaskActionExecuted(Boolean taskActionExecuted) {
		isTaskActionExecuted = taskActionExecuted;
	}

	//New Task Props
	public String newTask() throws Exception {
		
		FacilioContext context = new FacilioContext();
		try {
		FacilioChain newTask = FacilioChainFactory.getNewTaskChain();
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
		
		FacilioChain addSectionChain = FacilioChainFactory.addTaskSectionChain();
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

	private long offlineModifiedTime = -1L;
	public long getOfflineModifiedTime() {
		return offlineModifiedTime;
	}
	public void setOfflineModifiedTime(long offlineModifiedTime) {
		this.offlineModifiedTime = offlineModifiedTime;
	}

	public long getCurrentTime() {
		if (this.offlineModifiedTime != -1L) {
			return this.offlineModifiedTime;
		} else {
			return System.currentTimeMillis();
		}
	}

	//Add Task Props
	public String addTask() throws Exception {
		// TODO Auto-generated method stub
		long siteId = WorkOrderAPI.getSiteIdForWO(task.getParentTicketId());
		try {
		task.setSiteId(siteId);
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TASK, task);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, getAttachmentId());
	    context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
		FacilioChain addTask = FacilioChainFactory.getAddTaskChain();
		addTask.execute(context);

		setTaskId(task.getId());
		setResult(FacilioConstants.ContextNames.TASK, task.getId());
		setTaskActionExecuted(context);
			sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Task with id #{%d} of Work Order with id #{%d} has been Added",task.getId(),task.getParentTicketId()),
					null,
					AuditLogHandler.RecordType.MODULE,
					"Task", task.getId())
					.setActionType(AuditLogHandler.ActionType.ADD)
			);
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
	
	public String v2deleteTaskAttachment() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.getDeleteTaskAttachmentChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, this.recordId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, this.module);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, attachmentId);
		context.put(FacilioConstants.ContextNames.CURRENT_TIME, getCurrentTime());
		
		chain.execute();
		
		return SUCCESS;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}

	private int preRequestStatus;

	public int getPreRequestStatus() {
		return preRequestStatus;
	}

	public void setPreRequestStatus(int preRequestStatus) {
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
			checkForPreOpenWorkOrderAndThrowException(task.getParentTicketId());
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
		context.put(FacilioConstants.ContextNames.SKIP_VALIDATION, skipValidation);
		context.put(FacilioConstants.ContextNames.SKIP_LAST_READING_CHECK, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
		Map<Long, Map<String, String>> errorMap = new HashMap<>();
		FacilioChain updateTask;
		if (task.isPreRequest()) {
			updateTask = TransactionChainFactory.getUpdatePreRequestChain();
		} else {
			updateTask = TransactionChainFactory.getUpdateTaskChain();
		}
		try {
			updateTask.execute(context);
			
			if(context.get(FacilioConstants.ContextNames.TASK_ERRORS) != null) {
				List<TaskErrorContext> errors = (List<TaskErrorContext>) context.get(FacilioConstants.ContextNames.TASK_ERRORS);
				setResult(FacilioConstants.ContextNames.TASK_ERRORS, errors);
				
//				if(context.get(FacilioConstants.ContextNames.HAS_TASK_ERRORS) != null) {
//					Boolean hasTaskErrors = (Boolean) context.get(FacilioConstants.ContextNames.HAS_TASK_ERRORS);
//					if(hasTaskErrors != null && hasTaskErrors) {
//						setResponseCode(1);
//					}
//				}
			}

			if(context.get(FacilioConstants.ContextNames.REQUIRES_REMARKS) != null && (boolean) context.get(FacilioConstants.ContextNames.REQUIRES_REMARKS)) {
				setResult(FacilioConstants.ContextNames.REQUIRES_REMARKS, true);
			}
			if (context.get(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT) != null && (boolean) context.get(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT)) {
				setResult(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT, true);
			}
			setResult(FacilioConstants.ContextNames.RELOAD_WORK_ORDER, context.getOrDefault(FacilioConstants.ContextNames.RELOAD_WORK_ORDER, true));

			setTaskActionExecuted(context);
			
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
		if (context.get(FacilioConstants.ContextNames.PRE_REQUEST_STATUS) != null) {
			preRequestStatus = (Integer) context.get(FacilioConstants.ContextNames.PRE_REQUEST_STATUS);
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

	/**
	 * setTaskActionExecuted() checks if IS_TASK_ACTION_EXECUTED is true and sets the value for JSON response.
	 * @param context
	 */
	private void setTaskActionExecuted(FacilioContext context){
		Boolean isTaskActionExecuted = (Boolean) context.get(FacilioConstants.ContextNames.IS_TASK_ACTION_EXECUTED);
		if(isTaskActionExecuted !=null && isTaskActionExecuted){
			setTaskActionExecuted(true);
			setResult(FacilioConstants.ContextNames.IS_TASK_ACTION_EXECUTED, isTaskActionExecuted());
		}
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
	
	private boolean skipValidation;


	public boolean isSkipValidation() {
		return skipValidation;
	}

	public void setSkipValidation(boolean skipValidation) {
		this.skipValidation = skipValidation;
	}

	Long parentTicketId;
	public Long getParentTicketId() {
		return parentTicketId;
	}
	public void setParentTicketId(Long parentTicketId) {
		this.parentTicketId = parentTicketId;
	}

	long resourceId = -1;
	
	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	long sectionId = -1;

	public long getSectionId() {
		return sectionId;
	}

	public void setSectionId(long sectionId) {
		this.sectionId = sectionId;
	}

	private Map<Long, Map<String, String>> error;
	
	public String closeAllTask() throws Exception {
		FacilioContext context = new FacilioContext();
		try {
			checkForPreOpenWorkOrderAndThrowException(parentTicketId);
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		if (CollectionUtils.isNotEmpty(taskIdList)) {
			TaskContext defaultClosedTaskObj = new TaskContext();
			defaultClosedTaskObj.setParentTicketId(parentTicketId);
			defaultClosedTaskObj.setStatusNew(TaskStatus.CLOSED);
			context.put(FacilioConstants.ContextNames.TASK, defaultClosedTaskObj);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, taskIdList);
			context.put(FacilioConstants.ContextNames.IS_BULK_ACTION, true);
		    String filterName;
			if (resourceId > 0) {
				filterName = ResourceAPI.getResource(resourceId).getName();
				context.put(FacilioConstants.ContextNames.FILTERED_NAME, filterName);
			}else if(sectionId > 0) {
				filterName = TicketAPI.getTaskSection(sectionId).getName();
		    	context.put(FacilioConstants.ContextNames.FILTERED_NAME, filterName);
		    }
			context.put(FacilioConstants.ContextNames.PARENT_ID, parentTicketId);
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
			FacilioChain updateTask = TransactionChainFactory.getUpdateTaskChain();
			updateTask.execute(context);
			if(context.get(FacilioConstants.ContextNames.TASK_ERRORS) != null) {
				List<TaskErrorContext> errors = (List<TaskErrorContext>) context.get(FacilioConstants.ContextNames.TASK_ERRORS);
				setResult(FacilioConstants.ContextNames.TASK_ERRORS, errors);
				
//				if(context.get(FacilioConstants.ContextNames.HAS_TASK_ERRORS) != null) {
//					Boolean hasTaskErrors = (Boolean) context.get(FacilioConstants.ContextNames.HAS_TASK_ERRORS);
//					if(hasTaskErrors != null && hasTaskErrors) {
//						setResult(FacilioConstants.ContextNames.HAS_TASK_ERRORS, hasTaskErrors);
//						setResponseCode(1);
//					}
//				}
			}
			if(context.get(FacilioConstants.ContextNames.REQUIRES_REMARKS) != null && (boolean) context.get(FacilioConstants.ContextNames.REQUIRES_REMARKS)) {
				setResult(FacilioConstants.ContextNames.REQUIRES_REMARKS, true);
			}
			rowsUpdated += (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
			setModifiedTime(defaultClosedTaskObj.getModifiedTime());
			setResult(FacilioConstants.ContextNames.RELOAD_WORK_ORDER, context.getOrDefault(FacilioConstants.ContextNames.RELOAD_WORK_ORDER, true));
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

	public String correctPMReadings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TASKS, taskContextList);
		FacilioChain pmReadingCorrectionChain = TransactionChainFactory.getPMReadingCorrectionChain();
		pmReadingCorrectionChain.execute(context);
		return SUCCESS;
	}




	public void handleTaskAttachment(FacilioContext context) throws Exception {

		if (StringUtils.isEmpty(this.module) && this.recordId < 0) {
			return;
		}

		if (beforeAttachment == null && afterAttachment == null) {
			return;
		}

		long currentTime = getCurrentTime();
		if (beforeAttachment != null) {
			FacilioContext context1 = new FacilioContext();
			context1.put(FacilioConstants.ContextNames.MODULE_NAME, this.module);
			context1.put(FacilioConstants.ContextNames.RECORD_ID, this.recordId);
			context1.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, AttachmentContext.AttachmentType.BEFORE);
			context1.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, Arrays.asList(this.beforeAttachment));
			context1.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, Arrays.asList(this.beforeAttachmentFileName));
			context1.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, Arrays.asList(this.beforeAttachmentContentType));
			context1.put(FacilioConstants.ContextNames.CURRENT_TIME, currentTime);

			if (module.equals(FacilioConstants.ContextNames.ITEM_TYPES_ATTACHMENTS)) {
				context1.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY);
			}
			else if (module.equals(FacilioConstants.ContextNames.TICKET_ATTACHMENTS) || module.equals(FacilioConstants.ContextNames.TASK_ATTACHMENTS)) {
				context1.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
			}
			else if (module.equals(FacilioConstants.ContextNames.ASSET_ATTACHMENTS)) {
				context1.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
			}

			FacilioChain addAttachmentChain = FacilioChainFactory.getAddAttachmentChain();
			addAttachmentChain.execute(context1);

			context.put(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT, false);
			setResult(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT, false);
		}

		if (afterAttachment != null) {
			FacilioContext context1 = new FacilioContext();
			context1.put(FacilioConstants.ContextNames.MODULE_NAME, this.module);
			context1.put(FacilioConstants.ContextNames.RECORD_ID, this.recordId);
			context1.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, AttachmentContext.AttachmentType.AFTER);
			context1.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, Arrays.asList(this.afterAttachment));
			context1.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, Arrays.asList(this.afterAttachmentFileName));
			context1.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, Arrays.asList(this.afterAttachmentContentType));
			context1.put(FacilioConstants.ContextNames.CURRENT_TIME, currentTime);

			if (module.equals(FacilioConstants.ContextNames.ITEM_TYPES_ATTACHMENTS)) {
				context1.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY);
			}
			else if (module.equals(FacilioConstants.ContextNames.TICKET_ATTACHMENTS) || module.equals(FacilioConstants.ContextNames.TASK_ATTACHMENTS)) {
				context1.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
			}
			else if (module.equals(FacilioConstants.ContextNames.ASSET_ATTACHMENTS)) {
				context1.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
			}

			FacilioChain addAttachmentChain = FacilioChainFactory.getAddAttachmentChain();
			addAttachmentChain.execute(context1);

			context.put(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT, false);
			setResult(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT, false);
		}
	}


	public String updateAllTask(FacilioContext context) throws Exception {
		Map<Long, Map<String, String>> errorMap = new HashMap<>();
		try {
		for (TaskContext singleTask :taskContextList)
		{
			TaskContext taskContext = WorkOrderAPI.getTask(singleTask.getId());
			if(taskContext == null){
				throw  new RESTException(ErrorCode.VALIDATION_ERROR,true,"Task is Empty - #"+singleTask.getId(),null);
			}
			checkForPreOpenWorkOrderAndThrowException(taskContext.getParentTicketId());
			context.clear();
			context.put(FacilioConstants.ContextNames.SKIP_VALIDATION, skipValidation);
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
			context.put(FacilioConstants.ContextNames.TASK, singleTask);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(singleTask.getId()));
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

			if (AccountUtil.getCurrentAccount().getDeviceType() != null) {
				context.put(FacilioConstants.ContextNames.DO_VALIDTION, getDoValidation());
			}
			context.put(FacilioConstants.ContextNames.SKIP_LAST_READING_CHECK, true);
			FacilioChain updateTask;
			try {
					if (singleTask.isPreRequest()) {
						updateTask = TransactionChainFactory.getUpdatePreRequestChain();
					} else {
						updateTask = TransactionChainFactory.getUpdateTaskChain();
					}
				updateTask.execute(context);
				if(context.get(FacilioConstants.ContextNames.TASK_ERRORS) != null) {
					List<TaskErrorContext> errors = (List<TaskErrorContext>) context.get(FacilioConstants.ContextNames.TASK_ERRORS);
					setResult(FacilioConstants.ContextNames.TASK_ERRORS, errors);

//					if(context.get(FacilioConstants.ContextNames.HAS_TASK_ERRORS) != null) {
//						Boolean hasTaskErrors = (Boolean) context.get(FacilioConstants.ContextNames.HAS_TASK_ERRORS);
//						if(hasTaskErrors != null && hasTaskErrors) {
//							setResult(FacilioConstants.ContextNames.HAS_TASK_ERRORS, hasTaskErrors);
//							setResponseCode(1);
//						}
//					}
				}
				if(context.get(FacilioConstants.ContextNames.REQUIRES_REMARKS) != null && (boolean) context.get(FacilioConstants.ContextNames.REQUIRES_REMARKS)) {
					setResult(FacilioConstants.ContextNames.REQUIRES_REMARKS, true);
				}

				if (context.get(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT) != null && (boolean) context.get(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT)) {
					setResult(FacilioConstants.ContextNames.REQUIRES_ATTACHMENT, true);
				}

				setResult(FacilioConstants.ContextNames.RELOAD_WORK_ORDER, context.getOrDefault(FacilioConstants.ContextNames.RELOAD_WORK_ORDER, true));

				setTaskActionExecuted(context);

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
				if (context.get(FacilioConstants.ContextNames.PRE_REQUEST_STATUS) != null) {
					preRequestStatus = (Integer) context.get(FacilioConstants.ContextNames.PRE_REQUEST_STATUS);
				}
			
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
	public void checkForPreOpenWorkOrderAndThrowException(long ticketId) throws Exception{
		List<V3WorkOrderContext> workOrders = V3WorkOderAPI.getWorkOrders(Collections.singletonList(ticketId),null,true,true);
		if(CollectionUtils.isEmpty(workOrders)) {
			throw new RESTException(ErrorCode.VALIDATION_ERROR, true, "No WorkOrder Found - #" +ticketId, null);
		}
		if(workOrders.get(0).getModuleState() == null){
			throw new RESTException(ErrorCode.VALIDATION_ERROR,true,"Task(s) cannot be Updated for Pre-open WorkOrder #"+ticketId,null);
		}
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
		
		FacilioChain getTaskChain = FacilioChainFactory.getTaskDetailsChain();
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
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID, this.recordId);

				FacilioChain getRelatedTasksChain = FacilioChainFactory.getRelatedTasksChain();
				getRelatedTasksChain.execute(context);
				
				setTasks((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
				setPreRequests((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP));
				setSections((Map<Long, TaskSectionContext>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS));
				setPreRequestSections((Map<Long, TaskSectionContext>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS));	
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

	private Map<Long, Map<String, Object>> prerequisiteMap;

	public Map<Long, Map<String, Object>> getPrerequisiteMap() {
		return prerequisiteMap;
	}

	public void setPrerequisiteMap(Map<Long, Map<String, Object>> prerequisiteMap) {
		this.prerequisiteMap = prerequisiteMap;
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

	/**
	 * -> Single Task Update call
	 * @return
	 * @throws Exception
	 */
	public String v2updateStatus() throws Exception {
		updateStatus();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult(FacilioConstants.ContextNames.TASK, task);
		setResult(FacilioConstants.ContextNames.MODIFIED_TIME, task.getModifiedTime());
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Task with id #{%d} of Work Order with id #{%d} has been Updated",task.getId(),task.getParentTicketId()),
				null,
				AuditLogHandler.RecordType.MODULE,
				"Task", task.getId())
				.setActionType(AuditLogHandler.ActionType.UPDATE)
		);
		return SUCCESS;
	}

	/**
	 * Close All Tasks call
	 * @return
	 * @throws Exception
	 */
	public String v2closeAllTask() throws Exception {
		closeAllTask();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult(FacilioConstants.ContextNames.MODIFIED_TIME, modifiedTime);
		return SUCCESS;
	}

	/**
	 * Update All task Call
	 * @return
	 * @throws Exception
	 */
	public String v2updateAllTask() throws Exception {
		FacilioContext context = new FacilioContext();
		handleTaskAttachment(context);
		updateAllTask(context);
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult(FacilioConstants.ContextNames.TASK_LIST, taskContextList);
		setResult("error", getError());
		setResult("preRequestStatus", preRequestStatus);
		setResult(FacilioConstants.ContextNames.RELOAD_WORK_ORDER, context.getOrDefault(FacilioConstants.ContextNames.RELOAD_WORK_ORDER, true));
		return SUCCESS;
	}
	
	public String v2updateTask() throws Exception {
		if (CollectionUtils.isNotEmpty(id) && id.size() == 1) {
			task.setId(id.get(0));
		}
		updateTask();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		setResult(FacilioConstants.ContextNames.TASK, task);
		setResult(FacilioConstants.ContextNames.MODIFIED_TIME, task.getModifiedTime());
		setResult(FacilioConstants.ContextNames.PRE_REQUEST_STATUS, preRequestStatus);
		setResult("error", getError());
		return SUCCESS;
	}
	
	public String v2taskList() throws Exception {
		taskList();
		setResult(FacilioConstants.ContextNames.TASK_LIST, tasks);
		setResult("sections", getSections());
		setResult("prerequisites", getPreRequests());
		setResult("prerequisiteSections", getPreRequestSections());
		return SUCCESS;
	}
	
	public String v2multipleTaskList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		try {
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, this.id);

		FacilioChain getRelatedTasksChain = FacilioChainFactory.getRelatedMultipleTasksChain();
		getRelatedTasksChain.execute(context);

		setTaskMap((Map<Long, Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
		setResult(FacilioConstants.ContextNames.TASK_LIST, getTaskMap());
		
		setPrerequisiteMap((Map<Long, Map<String, Object>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP));
		setResult(FacilioConstants.ContextNames.PRE_REQUEST_LIST, getPrerequisiteMap());
		
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
			
			FacilioChain offlineSync = FacilioChainFactory.addOfflineSyncErrorChain();
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
		if (e != null && FacilioProperties.isProduction() && !(e instanceof IllegalArgumentException)) {
			CommonCommandUtil.emailException(TaskAction.class.getName(), "Error in Task api", e, inComingDetails.toString());
		}
	}
	
	private long modifiedTime = -1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getBeforeAttachmentFileName() {
		return beforeAttachmentFileName;
	}

	public void setBeforeAttachmentFileName(String beforeAttachmentFileName) {
		this.beforeAttachmentFileName = beforeAttachmentFileName;
	}

	public String getAfterAttachmentFileName() {
		return afterAttachmentFileName;
	}

	public void setAfterAttachmentFileName(String afterAttachmentFileName) {
		this.afterAttachmentFileName = afterAttachmentFileName;
	}

	public String getAfterAttachmentContentType() {
		return afterAttachmentContentType;
	}

	public void setAfterAttachmentContentType(String afterAttachmentContentType) {
		this.afterAttachmentContentType = afterAttachmentContentType;
	}

	public String getBeforeAttachmentContentType() {
		return beforeAttachmentContentType;
	}

	public void setBeforeAttachmentContentType(String beforeAttachmentContentType) {
		this.beforeAttachmentContentType = beforeAttachmentContentType;
	}
}

