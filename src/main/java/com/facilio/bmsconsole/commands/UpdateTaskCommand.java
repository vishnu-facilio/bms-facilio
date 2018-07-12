package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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
					context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ADD_TASK_INPUT);
					task.setReadingDataId(reading.getId());
				}
			}
			
			List<TaskContext> oldTasks = getTasks(recordIds);
			
			ActivityType taskActivity = null;
			ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
			if (recordIds.size() == 1 && reading != null) {
				taskActivity = ActivityType.ADD_TASK_INPUT;
			} else if (task != null && (task.getParentTicketId() != -1 || oldTasks != null)) {
				if (context.get(FacilioConstants.ContextNames.IS_BULK_ACTION) != null) {
					boolean bulkAction = (boolean) context.get(FacilioConstants.ContextNames.IS_BULK_ACTION);
					if (bulkAction) {
						taskActivity = ActivityType.CLOSE_ALL_TASK;
					}
				} else {
					taskActivity = ActivityType.ADD_TASK_INPUT;
				}
			} 
			
			
			updateParentTicketStatus(taskActivity, oldTasks.get(0));
			
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
	
	private void updateParentTicketStatus(ActivityType activityType, TaskContext task) throws Exception {
		
		//TaskContext completeRecord = getTask(taskId);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule taskModule = modBean.getModule(FacilioConstants.ContextNames.TASK);
		FacilioModule module = modBean.getModule(taskModule.getExtendModule().getModuleId());
		List<FacilioField> ticketFields = FieldFactory.getTicketFields(module);
		
		SelectRecordsBuilder<? extends TicketContext> builder = new SelectRecordsBuilder<TicketContext>()
				.select(ticketFields)
				.module(module)
				.beanClass(TicketContext.class)
				.andCondition(CriteriaAPI.getIdCondition(task.getParentTicketId(), module))
				.maxLevel(1);
		
		List<? extends TicketContext> tickets = builder.get();
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
															.module(module)
															.fields(ticketFields)
															.andCondition(CriteriaAPI.getIdCondition(task.getParentTicketId(), module));
				
				updateBuilder.update(newTicket);
			}
			try {
				if (ticket.getAssignedTo() != null) {
					ShiftAPI.handleWorkHoursReading(activityType, ticket.getAssignedTo().getOuid(), ticket.getId(), ticket.getStatus(), TicketAPI.getStatus("Work in Progress"));
				}
			}
			catch(Exception e) {
				log.info("Exception occurred while handling work hours", e);
			}
			
		}
	}
}
