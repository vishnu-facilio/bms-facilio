package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
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
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class UpdateWorkOrderCommand implements Command {
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(UpdateTaskCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(workOrder != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			Condition idCondition = CriteriaAPI.getIdCondition(recordIds, module);
			List<WorkOrderContext> oldWos = getOldWOs(idCondition, fields);
			List<WorkOrderContext> newWos = new ArrayList<WorkOrderContext>();
			
			TicketAPI.updateTicketAssignedBy(workOrder);
			updateWODetails(workOrder);
			ActivityType activityType = (ActivityType)context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
			if(workOrder.getAssignedTo() != null || workOrder.getAssignmentGroup() != null) {
				TicketStatusContext submittedStatus = TicketAPI.getStatus("Submitted");
				TicketStatusContext assignedStatus = TicketAPI.getStatus("Assigned");  
				TicketStatusContext onHoldStatus = TicketAPI.getStatus("On Hold");
				long submittedId = submittedStatus.getId();
				for (WorkOrderContext oldWo: oldWos) {
					WorkOrderContext newWo = FieldUtil.cloneBean(workOrder, WorkOrderContext.class);
					newWos.add(newWo);
					newWo.setId(oldWo.getId());
					if (oldWo.getStatus().getId() == submittedId) {
						newWo.setStatus(assignedStatus);
					} else {
						if (oldWo.getAssignedTo() != null) {
							if (workOrder.getAssignedTo().getOuid() == -1) {
								ShiftAPI.addUserWorkHoursReading(oldWo.getAssignedTo().getOuid(), oldWo.getId(), activityType, oldWo.getId(), "Close", System.currentTimeMillis());
								newWo.setStatus(submittedStatus);
							} else if (oldWo.getAssignedTo().getOuid() != workOrder.getAssignedTo().getOuid()) {
								try {
									newWo.setStatus(onHoldStatus);
									ShiftAPI.addUserWorkHoursReading(oldWo.getAssignedTo().getOuid(), oldWo.getId(), activityType, oldWo.getId(), "Pause", System.currentTimeMillis()); 
								} catch (Exception e) {
									log.info("Exception occurred while handling work hours", e);
								}
							}
						}
					}
				}
			} else if(workOrder.getStatus() != null) {
				TicketStatusContext statusObj = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), workOrder.getStatus().getId());
				
				for(WorkOrderContext oldWo: oldWos) {
					
					if (!validateWorkorderStatus(statusObj, oldWo)) {
						throw new RuntimeException("Please close all tasks before closing/resolving the workorder");
					}
					
					WorkOrderContext newWo = FieldUtil.cloneBean(workOrder, WorkOrderContext.class);
					newWo.setId(oldWo.getId());
					newWos.add(newWo);
					
					TicketAPI.updateTicketStatus(activityType, newWo, oldWo, newWo.isWorkDurationChangeAllowed() || (newWo.getIsWorkDurationChangeAllowed() == null && oldWo.isWorkDurationChangeAllowed()));
					try {
						if (oldWo.getAssignedTo() != null) {
							ShiftAPI.handleWorkHoursReading(activityType, oldWo.getAssignedTo().getOuid(), oldWo.getId(), oldWo.getStatus(), newWo.getStatus());
						}
					}
					catch(Exception e) {
						log.info("Exception occurred while handling work hours", e);
					}
				}
			}
			
			if (newWos.isEmpty()) {
				newWos.add(workOrder);
			}
			
			context.put(FacilioConstants.TicketActivity.OLD_TICKETS, oldWos);
			
			int rowsUpdated = 0;
			int woCount = newWos.size();
			for (WorkOrderContext wo : newWos) {
				UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
						.moduleName(moduleName)
						.fields(fields);
				
				if (woCount > 1) {
					updateBuilder.andCondition(CriteriaAPI.getIdCondition(wo.getId(), module));
				}
				else {
					updateBuilder.andCondition(idCondition);
				}
				rowsUpdated += updateBuilder.update(wo);
			}
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
			
			
			List<ActivityType> types = Arrays.asList(ActivityType.ASSIGN_TICKET, ActivityType.CLOSE_WORK_ORDER,  ActivityType.SOLVE_WORK_ORDER, ActivityType.HOLD_WORK_ORDER);
			if(types.contains(activityType)) {
				SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
						.moduleName(moduleName)
						.beanClass(WorkOrderContext.class)
						.select(fields)
						.andCustomWhere(module.getTableName()+".ID = ?", recordIds.get(0))
						.orderBy("ID");

				List<WorkOrderContext> workOrders = builder.get();
				context.put(FacilioConstants.ContextNames.RECORD, workOrders.get(0));
			}
		}
		return false;
	}
	
	private List<WorkOrderContext> getOldWOs(Condition ids, List<FacilioField> fields) throws Exception {
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
															.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
															.beanClass(WorkOrderContext.class)
															.select(fields)
															.andCondition(ids)
															.orderBy("ID");

		return builder.get();
	}
	
	private boolean validateWorkorderStatus(TicketStatusContext statusObj , WorkOrderContext oldWo) throws Exception {
		if ("Resolved".equalsIgnoreCase(statusObj.getStatus()) || "Closed".equalsIgnoreCase(statusObj.getStatus())) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = ModuleFactory.getTasksModule();
			List<FacilioField> fields =  modBean.getAllFields("task");
			Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			FacilioModule ticketModule = ModuleFactory.getTicketsModule();
			Map<String,FacilioField> ticketFieldMap = FieldFactory.getAsMap(FieldFactory.getTicketFields(ticketModule));
			
			String statusTable = ModuleFactory.getTicketStatusModule().getTableName();
			Map<String, FacilioField> statusFieldMap = FieldFactory.getAsMap(modBean.getAllFields("ticketstatus"));
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(Collections.singletonList(FieldFactory.getField("closedTasks", "COUNT(Tasks.ID)", FieldType.NUMBER)))
					.table(module.getTableName())
					.innerJoin(ticketModule.getTableName())
					.on(module.getTableName()+".ID="+ticketModule.getTableName()+".ID")
					.innerJoin(statusTable)
					.on(statusTable+".ID="+ticketModule.getTableName()+"."+ticketFieldMap.get("status").getColumnName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentTicketId"), String.valueOf(oldWo.getId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(statusFieldMap.get("typeCode"),"2", NumberOperators.EQUALS));
			
			List<Map<String, Object>> task = builder.get();
			if (task != null && !task.isEmpty()) {
				long count = (long) task.get(0).get("closedTasks");
				if (count < oldWo.getNoOfTasks()) {
					return false;
				}
			}
		}
		return true;
	}
	
	private void updateWODetails (WorkOrderContext wo) {
		wo.setModifiedTime(System.currentTimeMillis());
	}
	
}
