package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateWorkOrderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(workOrder != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			Condition idCondition = CriteriaAPI.getIdCondition(recordIds, module);
			List<WorkOrderContext> oldWos = getOldWOs(idCondition, fields);
			List<WorkOrderContext> newWos = new ArrayList<WorkOrderContext>();
			
			if(workOrder.getAssignedTo() != null || workOrder.getAssignmentGroup() != null) {
				workOrder.setStatus(TicketAPI.getStatus("Assigned"));
			}
			else if(workOrder.getStatus() != null) {
				TicketStatusContext statusObj = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), workOrder.getStatus().getId());
				
				for(WorkOrderContext oldWo: oldWos) {
					
					if (!validateWorkorderStatus(statusObj, oldWo)) {
						throw new Exception("Please close all tasks before closing the workorder");
					}
					
					WorkOrderContext newWo = FieldUtil.cloneBean(workOrder, WorkOrderContext.class);
					newWo.setId(oldWo.getId());
					newWos.add(newWo);
					
					TicketAPI.updateTicketStatus(newWo, oldWo, newWo.isWorkDurationChangeAllowed() || (newWo.getIsWorkDurationChangeAllowed() == null && oldWo.isWorkDurationChangeAllowed()));
					
					/*if ("Work in Progress".equalsIgnoreCase(statusObj.getStatus())) {
						if (oldWo.getActualWorkStart() != -1) {
							newWo.setResumedWorkStart(System.currentTimeMillis());
						}
						else {
							newWo.setActualWorkStart(System.currentTimeMillis());
						}
					}
					else if ("On Hold".equalsIgnoreCase(statusObj.getStatus()) || "Resolved".equalsIgnoreCase(statusObj.getStatus()) 
							|| ("Closed".equalsIgnoreCase(statusObj.getStatus()) && oldWo.getStatus().getId() != TicketAPI.getStatus("Resolved").getId()) ) {

						long estimatedDuration = TicketAPI.getEstimatedWorkDuration(oldWo);
						
						if ("Resolved".equalsIgnoreCase(statusObj.getStatus()) || "Closed".equalsIgnoreCase(statusObj.getStatus())) {
							newWo.setActualWorkEnd(System.currentTimeMillis());
							if(newWo.isWorkDurationChangeAllowed() || (newWo.getIsWorkDurationChangeAllowed() == null && oldWo.isWorkDurationChangeAllowed()) ) {
								long actualDuration = newWo.getActualWorkDuration() != -1 ? newWo.getActualWorkDuration() : newWo.getEstimatedWorkDuration();
								newWo.setActualWorkDuration(actualDuration);
								if (estimatedDuration == -1 && actualDuration != -1){
									estimatedDuration = newWo.getActualWorkDuration();
								}
							}
							else {
								newWo.setActualWorkDuration(estimatedDuration);
							}
						}
						
						if (estimatedDuration != -1) {
							newWo.setEstimatedWorkDuration(estimatedDuration);
						}
					}*/
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
						.table(dataTableName)
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
			
			
			if(ActivityType.ASSIGN_TICKET == (ActivityType)context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE) || ActivityType.CLOSE_WORK_ORDER == (ActivityType)context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE)) {
				SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
						.table(dataTableName)
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
	
	private boolean validateWorkorderStatus(TicketStatusContext statusObj , WorkOrderContext oldWo) {
		if ("Resolved".equalsIgnoreCase(statusObj.getStatus()) || "Closed".equalsIgnoreCase(statusObj.getStatus())) {
			if (oldWo.getNoOfClosedTasks() < oldWo.getNoOfTasks()) {
				return false;
			}
		}
		return true;
	}
	
}
