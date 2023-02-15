package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

;

public class UpdateNewPreventiveMaintenanceJobCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		boolean isNew = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_NEW_EVENT, false);
		if (isNew) {
			WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);

			List<FacilioField> fields = modBean.getAllFields(module.getName());
			SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
			selectRecordsBuilder
					.beanClass(WorkOrderContext.class)
					.module(module)
					.select(fields)
					.andCustomWhere("WorkOrders.ID = ?", workorder.getId())
					.skipModuleCriteria();
			List<Map<String, Object>> pmProps = selectRecordsBuilder.getAsProps();
			
			WorkOrderContext newWo = new WorkOrderContext();
			if (workorder.getDueDate() > 0) {
				newWo.setDueDate(workorder.getDueDate());
				newWo.setEstimatedEnd(workorder.getDueDate());
			}
			else if (workorder.getScheduledStart() > 0) {
				long woCreationOffset = -1L;
				if (pmProps.get(0).get("woCreationOffset") != null) {
					woCreationOffset = (Integer) pmProps.get(0).get("woCreationOffset");
				}
				newWo.setScheduledStart(workorder.getScheduledStart());
				newWo.setCreatedTime(workorder.getScheduledStart());
				if (woCreationOffset > 0) {
					newWo.setCreatedTime(workorder.getScheduledStart() - (woCreationOffset * 1000L));
				}
				newWo.setModifiedTime(workorder.getCreatedTime());
				WorkOrderContext currentWo = WorkOrderAPI.getWorkOrder(workorder.getId());
				if (currentWo.getDueDate() > 0) {
					long duration = currentWo.getDueDate() - currentWo.getScheduledStart();
					newWo.setDueDate(currentWo.getScheduledStart()+duration);
					newWo.setEstimatedEnd(newWo.getDueDate());
				}
			}
			if (workorder.getAssignedTo() != null && workorder.getAssignedTo().getId() > 0) {
				newWo.setAssignedTo(workorder.getAssignedTo());
				newWo.setAssignedBy(AccountUtil.getCurrentUser());
			}
			if (workorder.getAssignmentGroup() != null && workorder.getAssignmentGroup().getId() > 0) {
				newWo.setAssignmentGroup(workorder.getAssignmentGroup());
				newWo.setAssignedBy(AccountUtil.getCurrentUser());
			}
			
			UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
					.module(module)
					.fields(modBean.getAllFields(module.getName()))
					.andCondition(CriteriaAPI.getIdCondition(workorder.getId(), module))
					.skipModuleCriteria()
					;
			
			int rowsUpdated = updateBuilder.update(newWo);
			System.out.println(rowsUpdated);
			
		}
		// Temp...will remove
		else {
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			PMJobsContext pmJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_JOB);
			// long pmId = (Long) context.get(FacilioConstants.ContextNames.PM_ID);
			Long resourceId = (Long) context.get(FacilioConstants.ContextNames.PM_RESOURCE_ID);
			
			List<FacilioField> fields = modBean.getAllFields(module.getName());

			SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
			selectRecordsBuilder
					.beanClass(WorkOrderContext.class)
					.module(module)
					.select(fields)
					.andCustomWhere("WorkOrders.ID = ?", recordIds.get(0))
					.skipModuleCriteria();
			List<Map<String, Object>> pmProps = selectRecordsBuilder.getAsProps();
			if(pmProps == null){
				return false;
			}
			Map<String,Object> pmProp = pmProps.get(0);
			if((long)pmProp.get("scheduledStart") > pmJob.getNextExecutionTime()){
				throw new RESTException(ErrorCode.VALIDATION_ERROR,"Cannot Schedule a WorkOrder before it's Scheduled Start Time");
			}
			long scheduledStart = pmJob.getNextExecutionTime();


			pmProps.get(0).put("assignedTo", resourceId);
			pmProps.get(0).put("scheduledStart", scheduledStart);
			FacilioModule ticketModule = ModuleFactory.getTicketsModule();

			if(resourceId != -1) {
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ticketModule.getTableName())
						.fields(Arrays.asList(FieldFactory.getAsMap(fields).get("assignedTo"), FieldFactory.getAsMap(fields).get("scheduledStart")))
						.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), ticketModule))
						.andCondition(CriteriaAPI.getIdCondition(recordIds, ticketModule));
				updateBuilder.update(pmProps.get(0));
			}

			if (pmJob != null && pmJob.getNextExecutionTime() > 0) {

				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ticketModule.getTableName())
						.fields(Arrays.asList(FieldFactory.getAsMap(fields).get("scheduledStart")))
						.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), ticketModule))
						.andCondition(CriteriaAPI.getIdCondition(recordIds, ticketModule));
				updateBuilder.update(pmProps.get(0));
			}
		}
		return false;
	}
}
