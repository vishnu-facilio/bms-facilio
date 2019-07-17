package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
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
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderContext.PreRequisiteStatus;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class UpdateTaskCommand implements Command {
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(UpdateTaskCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(task != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			if(recordIds.size() == 1) {
				ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
				if(reading != null) {
					context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ADD_TASK_INPUT);
					task.setReadingDataId(reading.getId());
				}
			}
				
			List<TaskContext> oldTasks = getTasks(recordIds);
			TicketAPI.setTasksInputData(oldTasks);
			Map<Long, TaskContext> taskMap = oldTasks.stream().collect(Collectors.toMap(TaskContext::getId, Function.identity()));
		
				if(task.getInputValue() != null) {
					long newTaskId = recordIds.get(0);
					TaskContext oldTask = taskMap.get(newTaskId);
					if(StringUtils.isNotEmpty(oldTask.getFailureValue()) && oldTask.getFailureValue().equals(task.getInputValue())) {
						task.setFailed(true);
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
					if(task.isPreRequest()){
						CommonCommandUtil.addActivityToContext(oldTasks.get(0).getParentTicketId(), -1, WorkOrderActivityType.UPDATE_PREREQUISITE, newinfo, (FacilioContext) context);
					}else{
						CommonCommandUtil.addActivityToContext(oldTasks.get(0).getParentTicketId(), -1, WorkOrderActivityType.UPDATE_TASK, newinfo, (FacilioContext) context);
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
			
			List<Object> closedtask = new ArrayList<>();
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
							CommonCommandUtil.addActivityToContext(oldTasks.get(0).getParentTicketId(), -1,WorkOrderActivityType.CLOSE_FILTERED_TASK, newinfo, (FacilioContext) context);
						} else {
							CommonCommandUtil.addActivityToContext(oldTasks.get(0).getParentTicketId(), -1,WorkOrderActivityType.CLOSE_ALL_TASK, newinfo, (FacilioContext) context);
						}
					}
				} else {
				   if(task.getStatusNewEnum()!=null) {
					if(task.getStatusNewEnum().toString() == "CLOSED") {
						JSONObject info = new JSONObject();
						long newTaskId = recordIds.get(0);
							info.put("subject", taskMap.get(newTaskId).getSubject());
							CommonCommandUtil.addActivityToContext(taskMap.get(recordIds.get(0)).getParentTicketId(), -1, WorkOrderActivityType.CLOSE_TASK, info, (FacilioContext) context);
						
					}
					else if(task.getStatusNewEnum().toString() == "OPEN") {
						JSONObject info = new JSONObject();
						long newTaskId = recordIds.get(0);
							info.put("subject", taskMap.get(newTaskId).getSubject());
							CommonCommandUtil.addActivityToContext(taskMap.get(recordIds.get(0)).getParentTicketId(), -1, WorkOrderActivityType.REOPEN_TASK, info, (FacilioContext) context);
						
					}
				  }
					taskActivity = EventType.ADD_TASK_INPUT;
				}
			} 
			
			
			if (!task.isPreRequest()) {
				updateParentTicketStatus(context, taskActivity, oldTasks.get(0));
			}
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(module));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			if (task.getOfflineModifiedTime() != -1) {
				task.setModifiedTime(task.getOfflineModifiedTime());
			}
			else {
				task.setModifiedTime(System.currentTimeMillis());
			}
			
			UpdateRecordBuilder<TaskContext> updateBuilder = new UpdateRecordBuilder<TaskContext>()
																		.moduleName(moduleName)
																		.table(dataTableName)
																		.fields(fields)
																		.andCondition(idCondition);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(task));
			context.put(FacilioConstants.TicketActivity.OLD_TICKETS, oldTasks);
			int prerequestStatus;
			if (task.isPreRequest()) {
				long woid = oldTasks.stream().filter(x->x.getId() == task.getId()).findFirst().get().getParentTicketId();
				PreRequisiteStatus preReqStatus = WorkOrderAPI.updatePreRequisiteStatus(woid);
				prerequestStatus = preReqStatus.getValue();
			} else {
				prerequestStatus = WorkOrderAPI.getWorkOrder(oldTasks.get(0).getParentTicketId()).getPreRequestStatus();
			}
			context.put(FacilioConstants.ContextNames.PRE_REQUEST_STATUS, prerequestStatus);
		}
		return false;
	}
	
	private List<TaskContext> getTasks(List<Long> ids) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TASK);
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
														.module(module)
														.beanClass(TaskContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
														.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		List<TaskContext> tasks = builder.get();
		if(tasks != null && !tasks.isEmpty()) {
			return tasks;
		}
		return null;
	}
	
	private void updateParentTicketStatus(Context context, EventType activityType, TaskContext task) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		modBean.getModule(FacilioConstants.ContextNames.TASK);
		
		// Assuming that parent ticket will be always Workorder
		FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> woFields = modBean.getAllFields(woModule.getName());
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
				.fetchLookups(lookupList);
				;
		
		List<WorkOrderContext> tickets = builder.get();
		if(tickets != null && !tickets.isEmpty()) {
			TicketContext ticket = tickets.get(0);
			
			if (ticket.getStateFlowId() > 0) {
				StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(woModule);
				if (ticket.getStateFlowId() == defaultStateFlow.getId()) {
					FacilioStatus statusObj = ticket.getModuleState();
					if ("Closed".equalsIgnoreCase(statusObj.getStatus()) || "Resolved".equalsIgnoreCase(statusObj.getStatus())) {
						throw new IllegalArgumentException("Task cannot be updated for completed tickets");
					}
					
					if (!("Work in Progress".equalsIgnoreCase(statusObj.getStatus()))) {
						FacilioStatus workInProgressStatus = TicketAPI.getStatus("Work in Progress");
						if (ticket.getAssignedTo() == null) {
							ticket.setAssignedTo(AccountUtil.getCurrentUser());
							UpdateRecordBuilder<TicketContext> updateBuilder = new UpdateRecordBuilder<TicketContext>()
									.module(woModule)
									.fields(woFields)
									.andCondition(CriteriaAPI.getIdCondition(task.getParentTicketId(), woModule));

							updateBuilder.update(ticket);
						}
						StateFlowRulesAPI.updateState(ticket, woModule, workInProgressStatus, false, context);
					}
				}
			} 
			else {
				FacilioStatus statusObj = ticket.getStatus();
				if ("Closed".equalsIgnoreCase(statusObj.getStatus()) || "Resolved".equalsIgnoreCase(statusObj.getStatus())) {
					throw new IllegalArgumentException("Task cannot be updated for completed tickets");
				}
				
				if (!("Work in Progress".equalsIgnoreCase(statusObj.getStatus()))) {
					TicketContext newTicket = new TicketContext();
					newTicket.setStatus(TicketAPI.getStatus("Work in Progress"));
					TicketAPI.updateTicketStatus(activityType, newTicket, ticket, false);
					
					UpdateRecordBuilder<TicketContext> updateBuilder = new UpdateRecordBuilder<TicketContext>()
																.module(woModule)
																.fields(woFields)
																.andCondition(CriteriaAPI.getIdCondition(task.getParentTicketId(), woModule));
					
					updateBuilder.update(newTicket);
				}
				try {
					if (ticket.getAssignedTo() != null) {
						List<ReadingContext> readings = ShiftAPI.handleWorkHoursReading(activityType, ticket.getAssignedTo().getOuid(), ticket.getId(), ticket.getStatus(), TicketAPI.getStatus("Work in Progress"));
						Map<String, List<ReadingContext>> readingMap = new HashMap<>();
						readingMap.put("userworkhoursreading", readings);
						context.put(FacilioConstants.ContextNames.READINGS_MAP, readingMap);
						context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
					}
				}
				catch(Exception e) {
					log.info("Exception occurred while handling work hours", e);
					CommonCommandUtil.emailException(UpdateTaskCommand.class.getName(), "Exception occurred while handling work hours", e);
				}
			}
			
		}
	}
}
