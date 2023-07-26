package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderContext.PreRequisiteStatus;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

@Log4j
public class UpdateTaskCommand extends FacilioCommand {
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(UpdateTaskCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(task != null && recordIds != null && !recordIds.isEmpty()) {
			LOGGER.error("recordIDs: " + recordIds);
			LOGGER.error("task: " + task);

			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

			//Update task modified time
			if (task.getOfflineModifiedTime() != -1) {
				task.setModifiedTime(task.getOfflineModifiedTime());
			}
			else {
				task.setModifiedTime(System.currentTimeMillis());
			}

			long taskModifiedTime = task.getModifiedTime();

			if(recordIds.size() == 1) {
				ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
				if(reading != null) {
					context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ADD_TASK_INPUT);
					task.setReadingDataId(reading.getId());
				}
			}
			
			Map<Long, TaskContext> taskMap = (Map<Long, TaskContext>) context.get(FacilioConstants.ContextNames.TASK_MAP);
			if (taskMap == null) {
				taskMap = TicketAPI.getTaskMap(recordIds);
				context.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
			}
			List<TaskContext> oldTasks = taskMap.values().stream().collect(Collectors.toList());
			TicketAPI.setTasksInputData(oldTasks);
		   long parentId = oldTasks.get(0).getParentTicketId();
				if(task.getInputValue() != null) {
					long newTaskId = recordIds.get(0);
					TaskContext oldTask = taskMap.get(newTaskId);
					if(StringUtils.isNotEmpty(oldTask.getFailureValue())) {
						if (oldTask.getInputTypeEnum() == InputType.NUMBER && oldTask.getDeviationOperator() != null) {
							FacilioModulePredicate predicate = oldTask.getDeviationOperator().getPredicate("inputValue", oldTask.getFailureValue());
            	   				task.setFailed(predicate.evaluate(task));
	            	   		}
	            	   		else if (oldTask.getFailureValue().equals(task.getInputValue())) {
	            	   			task.setFailed(true);
	            	   		}
	            	   		else {
	    						task.setFailed(false);
	    					}
					}
					
					JSONObject info = new JSONObject();
					info.put("subject", oldTask.getSubject());
					if(task.isPreRequest()){
						if(task.getInputValue().equalsIgnoreCase("1")){
							info.put("newvalue", oldTask.getTruevalue());
						}else{
							info.put("newvalue", oldTask.getFalsevalue());
						}
					}else{					
						info.put("newvalue", task.getInputValue());
					}
					JSONObject newinfo = new JSONObject();
					newinfo.put("taskupdate", info);
					LOGGER.error("task: " + task.getId());
					if(task.isPreRequest()){
						CommonCommandUtil.addActivityToContext(parentId, taskModifiedTime, WorkOrderActivityType.UPDATE_PREREQUISITE, newinfo, (FacilioContext) context);
					}else{
						CommonCommandUtil.addActivityToContext(parentId, taskModifiedTime, WorkOrderActivityType.UPDATE_TASK, newinfo, (FacilioContext) context);
					}
				}
			
			Long lastSyncTime = (Long) context.get(FacilioConstants.ContextNames.LAST_SYNC_TIME);
			TaskContext oldTaskForSync = oldTasks.get(0);
			if (lastSyncTime != null && oldTaskForSync.getModifiedTime() > lastSyncTime ) {
				throw new IllegalArgumentException("The task was modified after the last sync");
			}
			if (task.getSyncTime() != -1 && oldTaskForSync.getModifiedTime() > task.getSyncTime() ) {
				throw new IllegalArgumentException("The task was modified after the last sync");
			}
			
			EventType taskActivity = null;
			ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
			
			
			if (recordIds.size() == 1 && reading != null) {
				taskActivity = EventType.ADD_TASK_INPUT;
			} else if (task != null && (task.getParentTicketId() != -1 || oldTasks != null)) {
				if (context.get(FacilioConstants.ContextNames.IS_BULK_ACTION) != null) {
					boolean bulkAction = (boolean) context.get(FacilioConstants.ContextNames.IS_BULK_ACTION);
					if (bulkAction) {
						taskActivity = EventType.CLOSE_ALL_TASK;
//						for(TaskContext oldTask : oldTasks) {
//							JSONObject info = new JSONObject();
//						info.put("subject", oldTask.getSubject());
//						closedtask.add(info);
//						}
						JSONObject newinfo = new JSONObject();
//						newinfo.put("closetasks", closedtask);
						String filteredName = (String) context.get(FacilioConstants.ContextNames.FILTERED_NAME);
						if (filteredName != null) {
							newinfo.put(FacilioConstants.ContextNames.FILTERED_NAME,filteredName);
							CommonCommandUtil.addActivityToContext(parentId, taskModifiedTime,WorkOrderActivityType.CLOSE_FILTERED_TASK, newinfo, (FacilioContext) context);
						} else {
							CommonCommandUtil.addActivityToContext(parentId, taskModifiedTime,WorkOrderActivityType.CLOSE_ALL_TASK, newinfo, (FacilioContext) context);
						}
					}
				} else {
				   if(task.getStatusNewEnum()!=null) {
					if(task.getStatusNewEnum().toString() == "CLOSED") {
						JSONObject info = new JSONObject();
						long newTaskId = recordIds.get(0);
							info.put("subject", taskMap.get(newTaskId).getSubject());
							CommonCommandUtil.addActivityToContext(taskMap.get(recordIds.get(0)).getParentTicketId(), taskModifiedTime, WorkOrderActivityType.CLOSE_TASK, info, (FacilioContext) context);
						
					}
					else if(task.getStatusNewEnum().toString() == "OPEN") {
						JSONObject info = new JSONObject();
						long newTaskId = recordIds.get(0);
							info.put("subject", taskMap.get(newTaskId).getSubject());
							CommonCommandUtil.addActivityToContext(taskMap.get(recordIds.get(0)).getParentTicketId(), taskModifiedTime, WorkOrderActivityType.REOPEN_TASK, info, (FacilioContext) context);
						
					}
				  }
					taskActivity = EventType.ADD_TASK_INPUT;
				}
			} 
			

			if (!task.isPreRequest()) {
				updateParentTicketStatus(context, taskActivity, oldTasks.get(0), task);
			}
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(module));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			UpdateRecordBuilder<TaskContext> updateBuilder = new UpdateRecordBuilder<TaskContext>()
																		.moduleName(moduleName)
																		.table(dataTableName)
																		.fields(fields)
																		.andCondition(idCondition);
			int updated = updateBuilder.update(task);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updated);
			LOGGER.error("Number of rows updated in Tasks table: " + updated);
			int prerequestStatus;
			if (task.isPreRequest()) {
				PreRequisiteStatus preReqStatus = WorkOrderAPI.updatePreRequisiteStatus(parentId);
				prerequestStatus = preReqStatus.getValue();
			} else {
				prerequestStatus = WorkOrderAPI.getWorkOrder(parentId).getPreRequestStatus();
			}
			
			task.setParentTicketId(parentId);
			context.put(FacilioConstants.ContextNames.PRE_REQUEST_STATUS, prerequestStatus);
			context.put(ContextNames.RECORD_LIST, null);
			context.put(FacilioConstants.ContextNames.RECORD, task);
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		}
		else {
			LOGGER.error("task is null or recordID is null/empty");
		}
		return false;
	}
	
	
	private void updateParentTicketStatus(Context context, EventType activityType, TaskContext task, TaskContext newTask) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		modBean.getModule(FacilioConstants.ContextNames.TASK);
		
		List<WorkOrderContext> tickets = (List<WorkOrderContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> woFields = modBean.getAllFields(woModule.getName());
		if (tickets == null) {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(woFields);
			
			List<LookupField> lookupList = new ArrayList<>();
			lookupList.add((LookupField) fieldMap.get("status"));
			if (fieldMap.containsKey("moduleState")) {
				lookupList.add((LookupField) fieldMap.get("moduleState"));
			}
			SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
					.select(woFields)
					.module(woModule)
					.beanClass(WorkOrderContext.class)
					.andCondition(CriteriaAPI.getIdCondition(task.getParentTicketId(), woModule))
					.fetchSupplements(lookupList);
			;
			
			tickets = builder.get();
		}
		
		WorkOrderContext ticket = tickets.get(0);
		/*
			Adding the Task offlineModifiedTime as Ticket OfflineModifiedTime so that if the status is
		 	changed in this task update API call, it doesn't task current systemTime, which causes ordering issue.
		 */
		if(newTask.getOfflineModifiedTime() != -1){
			ticket.setOfflineModifiedTime(newTask.getOfflineModifiedTime());
		}
		
		if (ticket.getStateFlowId() > 0) {
			FacilioStatus statusObj = ticket.getModuleState();
			StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(woModule);
			if (ticket.getStateFlowId() == defaultStateFlow.getId()) {
				if (ticket.getQrEnabled() != null && ticket.getQrEnabled() && ticket.getResource() != null) {
					if ("Submitted".equalsIgnoreCase(statusObj.getStatus()) || "Assigned".equalsIgnoreCase(statusObj.getStatus())) {
						throw new IllegalArgumentException("Scan the QR before starting the task");
					}
					
				}
				
				if ("Closed".equalsIgnoreCase(statusObj.getStatus()) || "Resolved".equalsIgnoreCase(statusObj.getStatus())) {
					throw new IllegalArgumentException("Task cannot be updated for completed tickets");
				}
				
				if (!("Work in Progress".equalsIgnoreCase(statusObj.getStatus()))) {
					FacilioStatus workInProgressStatus = TicketAPI.getStatus("Work in Progress");
					if (ticket.getAssignedTo() == null) {
						TicketContext newTicket = new TicketContext(); 
						newTicket.setAssignedTo(AccountUtil.getCurrentUser());
						newTicket.setAssignedBy(AccountUtil.getCurrentUser());
						UpdateRecordBuilder<TicketContext> updateBuilder = new UpdateRecordBuilder<TicketContext>()
								.module(woModule)
								.fields(woFields)
								.andCondition(CriteriaAPI.getIdCondition(task.getParentTicketId(), woModule));

						updateBuilder.update(ticket);
					}
					StateFlowRulesAPI.updateState(ticket, woModule, workInProgressStatus, false, context);
					log.info("Status update done through task update");
				}
			}
		}
	}
}
