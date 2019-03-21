package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.TicketActivity;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.MapDifference.ValueDifference;

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
			if(recordIds.size() == 1) {
				if(task.getInputValue() != null) {
					JSONObject info = new JSONObject();
					long newTaskId = task.getId();
					TaskContext oldTask = oldTasks.get(0);
					info.put("newvalue", task.getInputValue());
					info.put("oldvalue", oldTask.getInputValue());
					info.put("newTaskId", newTaskId);
					info.put("actype", "update");
					JSONObject newinfo = new JSONObject();
                    newinfo.put("taskupdate", info); 
                    CommonCommandUtil.addActivityToContext(task.getParentTicketId(), -1, WorkOrderActivityType.UPDATE, newinfo, (FacilioContext) context);
				}
			}
			else if (recordIds.size()>1) {
				for (Long recordId:recordIds) {
					if(task.getInputValue() != null) {
						JSONObject info = new JSONObject();
						long newTaskId = task.getId();
						TaskContext oldTask = oldTasks.get(0);
						info.put("newvalue", task.getInputValue());
						info.put("oldvalue", oldTask.getInputValue());
						info.put("newTaskId", newTaskId);
						info.put("actype", "update");
						JSONObject newinfo = new JSONObject();
                        newinfo.put("taskupdate", info); 
						CommonCommandUtil.addActivityToContext(task.getParentTicketId(), -1, WorkOrderActivityType.UPDATE, newinfo, (FacilioContext) context);
					}
				}
			}
			Long lastSyncTime = (Long) context.get(FacilioConstants.ContextNames.LAST_SYNC_TIME);
			if (lastSyncTime != null && oldTasks.get(0).getModifiedTime() > lastSyncTime ) {
				throw new RuntimeException("The task was modified after the last sync");
			}
			
			EventType taskActivity = null;
			ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
			if (recordIds.size() == 1 && reading != null) {
				taskActivity = EventType.ADD_TASK_INPUT;
				}
			else if (task != null && (task.getParentTicketId() != -1 || oldTasks != null)) {
				if (context.get(FacilioConstants.ContextNames.IS_BULK_ACTION) != null) {
					boolean bulkAction = (boolean) context.get(FacilioConstants.ContextNames.IS_BULK_ACTION);
					if (bulkAction) {
						taskActivity = EventType.CLOSE_ALL_TASK;
						JSONObject info = new JSONObject();
						info.put("closetasks", recordIds);
						info.put("actype", "closetask");
						JSONObject newinfo = new JSONObject();
                        newinfo.put("closetasks", info);
						CommonCommandUtil.addActivityToContext(task.getParentTicketId(), -1, WorkOrderActivityType.CLOSE_ALL_TASK, newinfo, (FacilioContext) context);
                     }
				} else {
					taskActivity = EventType.ADD_TASK_INPUT;
				}
			} 
			
			updateParentTicketStatus(context,taskActivity, oldTasks.get(0));
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(module));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			task.setModifiedTime(System.currentTimeMillis());
			
			UpdateRecordBuilder<TaskContext> updateBuilder = new UpdateRecordBuilder<TaskContext>()
																		.moduleName(moduleName)
																		.table(dataTableName)
																		.fields(fields)
																		.andCondition(idCondition);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(task));
			context.put(FacilioConstants.TicketActivity.OLD_TICKETS, oldTasks);
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
		
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
				.select(woFields)
				.module(woModule)
				.beanClass(WorkOrderContext.class)
				.andCondition(CriteriaAPI.getIdCondition(task.getParentTicketId(), woModule))
				.fetchLookup(new LookupFieldMeta((LookupField) fieldMap.get("status")));
				;
		
		List<WorkOrderContext> tickets = builder.get();
		if(tickets != null && !tickets.isEmpty()) {
			TicketContext ticket = tickets.get(0);
			
			TicketStatusContext statusObj = ticket.getStatus();
			if ("Closed".equalsIgnoreCase(statusObj.getStatus())|| "Resolved".equalsIgnoreCase(statusObj.getStatus())) {
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
